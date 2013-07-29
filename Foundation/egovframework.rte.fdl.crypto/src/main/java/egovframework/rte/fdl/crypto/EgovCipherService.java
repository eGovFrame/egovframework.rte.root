/*
 * Copyright 2008-2009 MOPAS(Ministry of Public Administration and Security).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package egovframework.rte.fdl.crypto;

import java.util.*;
import java.io.*;
import java.security.InvalidKeyException;

import egovframework.rte.fdl.crypto.exception.UnsupportedException;



class ARIAEngine {
    private static final char[] HEX_DIGITS = {
      '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'
    };
    
    private static final int[][] KRK = {
    {0x517cc1b7, 0x27220a94, 0xfe13abe8, 0xfa9a6ee0},
    {0x6db14acc, 0x9e21c820, 0xff28b1d5, 0xef5de2b0},
    {0xdb92371d, 0x2126e970, 0x03249775, 0x04e8c90e}
    };
    private static final byte[] S1 = new byte[256];
    private static final byte[] S2 = new byte[256];
    private static final byte[] X1 = new byte[256];
    private static final byte[] X2 = new byte[256];
    private static final int[] TS1 = new int[256];
    private static final int[] TS2 = new int[256];
    private static final int[] TX1 = new int[256];
    private static final int[] TX2 = new int[256];
    
    // Static initializer.  For setting up the tables
    static {
      int[] exp = new int[256];
      int[] log =  new int[256];
      
      exp[0] = 1;
      for (int i=1; i < 256; i++) {
        int j = (exp[i-1] << 1) ^ exp[i-1];
        if ((j & 0x100) != 0) j ^= 0x11b;
        exp[i] = j;
      }
      for (int i=1; i < 255; i++)
          log[exp[i]] = i;
      
      int[][] A = {
      {1, 0, 0, 0, 1, 1, 1, 1},
          {1, 1, 0, 0, 0, 1, 1, 1},
          {1, 1, 1, 0, 0, 0, 1, 1},
          {1, 1, 1, 1, 0, 0, 0, 1},
          {1, 1, 1, 1, 1, 0, 0, 0},
          {0, 1, 1, 1, 1, 1, 0, 0},
          {0, 0, 1, 1, 1, 1, 1, 0},
          {0, 0, 0, 1, 1, 1, 1, 1}
      };
      int[][] B = {
      {0, 1, 0, 1, 1, 1, 1, 0},
          {0, 0, 1, 1, 1, 1, 0, 1},
          {1, 1, 0, 1, 0, 1, 1, 1},
          {1, 0, 0, 1, 1, 1, 0, 1},
          {0, 0, 1, 0, 1, 1, 0, 0},
          {1, 0, 0, 0, 0, 0, 0, 1},
          {0, 1, 0, 1, 1, 1, 0, 1},
          {1, 1, 0, 1, 0, 0, 1, 1}
      };
      
      for (int i=0; i<256; i++) {
          int t=0, p;
          if (i==0)
            p=0;
          else
            p=exp[255-log[i]];
          for (int j=0; j<8; j++) {
            int s=0;
            for (int k=0; k<8; k++) {
              if (((p>>>(7-k))&0x01)!=0)
                s^=A[k][j];
            }
            t=(t<<1)^s;
          }
          t^=0x63;
          S1[i]=(byte)t;
          X1[t]=(byte)i;
      }
      for (int i = 0; i < 256; i++) {
        int t = 0, p;
        if (i==0)
          p=0;
        else
          p=exp[(247*log[i])%255];
        for (int j = 0; j < 8; j++) {
          int s = 0;
          for (int k = 0; k < 8; k++) {
            if (((p >>> k) & 0x01) != 0)
              s ^= B[7-j][k];
          }
          t = (t << 1) ^ s;
        }
        t^=0xe2;
        S2[i] = (byte) t;
        X2[t] = (byte) i;
      }
      
      for (int i = 0; i < 256; i++) {
        TS1[i]=0x00010101*(S1[i]&0xff);
        TS2[i]=0x01000101*(S2[i]&0xff);
        TX1[i]=0x01010001*(X1[i]&0xff);
        TX2[i]=0x01010100*(X2[i]&0xff);
      }
    }
    
    private byte[] p_arr;
    static byte[] mk = new byte[32];
    private int keySize=0;
    private int numberOfRounds=0;
    private byte[] masterKey=null;
    private int[] encRoundKeys=null, decRoundKeys=null;
    
    public ARIAEngine(int keySize) throws InvalidKeyException {
        setKeySize(keySize);
    }
    void reset() {
        this.keySize=0;
        this.numberOfRounds=0;
        this.masterKey=null;
        this.encRoundKeys=null;
        this.decRoundKeys=null;
    }
    
   int getKeySize() {
        return this.keySize;
    }
    
    void setKeySize(int keySize) throws InvalidKeyException {
        this.reset();
        if (keySize!=128 && keySize!=192 && keySize!=256)
          throw new InvalidKeyException("keySize="+keySize);
        this.keySize = keySize;
        switch (keySize) {
          case 128:
            this.numberOfRounds = 12;
            break;
          case 192:
            this.numberOfRounds = 14;
            break;
          case 256:
            this.numberOfRounds = 16;
        }
    }
    
    void setKey(byte[] masterKey) throws InvalidKeyException {
        if (masterKey.length*8<keySize)
          throw new InvalidKeyException("masterKey size="+masterKey.length);
        this.decRoundKeys = null;
        this.encRoundKeys = null;
        this.masterKey = (byte[])masterKey.clone();
    }
    
    void setupEncRoundKeys() throws InvalidKeyException {
      if (this.keySize==0)
        throw new InvalidKeyException("keySize");
      if (this.masterKey==null)
        throw new InvalidKeyException("masterKey");
        if (this.encRoundKeys==null)
          this.encRoundKeys = new int[4*(this.numberOfRounds+1)];
        this.decRoundKeys = null;
        doEncKeySetup(this.masterKey, this.encRoundKeys, this.keySize);
    }
    
    void setupDecRoundKeys() throws InvalidKeyException {
        if (this.keySize==0)
          throw new InvalidKeyException("keySize");
        if (this.encRoundKeys==null)
          if (this.masterKey==null)
            throw new InvalidKeyException("masterKey");
          else
            setupEncRoundKeys();
        this.decRoundKeys = (int[])encRoundKeys.clone();
        doDecKeySetup(this.masterKey, this.decRoundKeys, this.keySize);
    }
    
    void setupRoundKeys() throws InvalidKeyException {
        setupDecRoundKeys();
    }
    
    private static void doCrypt(byte[] i, int ioffset, int[] rk, int nr, byte[] o, int ooffset) {
      int t0, t1, t2, t3, j=0;
      
      t0 = toInt(i[ 0+ioffset], i[ 1+ioffset], i[ 2+ioffset], i[ 3+ioffset]);
      t1 = toInt(i[ 4+ioffset], i[ 5+ioffset], i[ 6+ioffset], i[ 7+ioffset]);
      t2 = toInt(i[ 8+ioffset], i[ 9+ioffset], i[10+ioffset], i[11+ioffset]);
      t3 = toInt(i[12+ioffset], i[13+ioffset], i[14+ioffset], i[15+ioffset]);
      
      for (int r=1; r<nr/2; r++) {
        t0^=rk[j++]; t1^=rk[j++]; t2^=rk[j++]; t3^=rk[j++];
          t0=TS1[(t0>>>24)&0xff]^TS2[(t0>>>16)&0xff]^TX1[(t0>>>8)&0xff]^TX2[t0&0xff];
          t1=TS1[(t1>>>24)&0xff]^TS2[(t1>>>16)&0xff]^TX1[(t1>>>8)&0xff]^TX2[t1&0xff];
          t2=TS1[(t2>>>24)&0xff]^TS2[(t2>>>16)&0xff]^TX1[(t2>>>8)&0xff]^TX2[t2&0xff];
          t3=TS1[(t3>>>24)&0xff]^TS2[(t3>>>16)&0xff]^TX1[(t3>>>8)&0xff]^TX2[t3&0xff];         
          t1^=t2; t2^=t3; t0^=t1; t3^=t1; t2^=t0; t1^=t2;
          t1=badc(t1); t2=cdab(t2); t3=dcba(t3);
          t1^=t2; t2^=t3; t0^=t1; t3^=t1; t2^=t0; t1^=t2;
          
          t0^=rk[j++]; t1^=rk[j++]; t2^=rk[j++]; t3^=rk[j++];
          t0=TX1[(t0>>>24)&0xff]^TX2[(t0>>>16)&0xff]^TS1[(t0>>>8)&0xff]^TS2[t0&0xff];
          t1=TX1[(t1>>>24)&0xff]^TX2[(t1>>>16)&0xff]^TS1[(t1>>>8)&0xff]^TS2[t1&0xff];
          t2=TX1[(t2>>>24)&0xff]^TX2[(t2>>>16)&0xff]^TS1[(t2>>>8)&0xff]^TS2[t2&0xff];
          t3=TX1[(t3>>>24)&0xff]^TX2[(t3>>>16)&0xff]^TS1[(t3>>>8)&0xff]^TS2[t3&0xff];  
          t1^=t2; t2^=t3; t0^=t1; t3^=t1; t2^=t0; t1^=t2;
          t3=badc(t3); t0=cdab(t0); t1=dcba(t1);        
          t1^=t2; t2^=t3; t0^=t1; t3^=t1; t2^=t0; t1^=t2;
      }
      t0^=rk[j++]; t1^=rk[j++]; t2^=rk[j++]; t3^=rk[j++];
      t0=TS1[(t0>>>24)&0xff]^TS2[(t0>>>16)&0xff]^TX1[(t0>>>8)&0xff]^TX2[t0&0xff];
      t1=TS1[(t1>>>24)&0xff]^TS2[(t1>>>16)&0xff]^TX1[(t1>>>8)&0xff]^TX2[t1&0xff];
      t2=TS1[(t2>>>24)&0xff]^TS2[(t2>>>16)&0xff]^TX1[(t2>>>8)&0xff]^TX2[t2&0xff];
      t3=TS1[(t3>>>24)&0xff]^TS2[(t3>>>16)&0xff]^TX1[(t3>>>8)&0xff]^TX2[t3&0xff];
      t1^=t2; t2^=t3; t0^=t1; t3^=t1; t2^=t0; t1^=t2;
      t1=badc(t1); t2=cdab(t2); t3=dcba(t3);
      t1^=t2; t2^=t3; t0^=t1; t3^=t1; t2^=t0; t1^=t2;
      
      t0^=rk[j++]; t1^=rk[j++]; t2^=rk[j++]; t3^=rk[j++]; 
      o[ 0+ooffset] = (byte)(X1[0xff&(t0>>>24)] ^ (rk[j  ]>>>24));
      o[ 1+ooffset] = (byte)(X2[0xff&(t0>>>16)] ^ (rk[j  ]>>>16));
      o[ 2+ooffset] = (byte)(S1[0xff&(t0>>> 8)] ^ (rk[j  ]>>> 8));
      o[ 3+ooffset] = (byte)(S2[0xff&(t0     )] ^ (rk[j  ]     ));
      o[ 4+ooffset] = (byte)(X1[0xff&(t1>>>24)] ^ (rk[j+1]>>>24));
      o[ 5+ooffset] = (byte)(X2[0xff&(t1>>>16)] ^ (rk[j+1]>>>16));
      o[ 6+ooffset] = (byte)(S1[0xff&(t1>>> 8)] ^ (rk[j+1]>>> 8));
      o[ 7+ooffset] = (byte)(S2[0xff&(t1     )] ^ (rk[j+1]     ));
      o[ 8+ooffset] = (byte)(X1[0xff&(t2>>>24)] ^ (rk[j+2]>>>24));
      o[ 9+ooffset] = (byte)(X2[0xff&(t2>>>16)] ^ (rk[j+2]>>>16));
      o[10+ooffset] = (byte)(S1[0xff&(t2>>> 8)] ^ (rk[j+2]>>> 8));
      o[11+ooffset] = (byte)(S2[0xff&(t2     )] ^ (rk[j+2]     ));
      o[12+ooffset] = (byte)(X1[0xff&(t3>>>24)] ^ (rk[j+3]>>>24));
      o[13+ooffset] = (byte)(X2[0xff&(t3>>>16)] ^ (rk[j+3]>>>16));
      o[14+ooffset] = (byte)(S1[0xff&(t3>>> 8)] ^ (rk[j+3]>>> 8));
      o[15+ooffset] = (byte)(S2[0xff&(t3     )] ^ (rk[j+3]     ));
    }
    void encrypt(byte[] i, int ioffset, byte[] o, int ooffset) throws InvalidKeyException {
        if (this.keySize==0)
          throw new InvalidKeyException("keySize");
        if (this.encRoundKeys==null)
          if (this.masterKey==null)
            throw new InvalidKeyException("masterKey");
          else
            setupEncRoundKeys();
        doCrypt(i, ioffset, this.encRoundKeys, this.numberOfRounds, o, ooffset);
    }
    byte[] encrypt(byte[] i, int ioffset) throws InvalidKeyException {
        byte[] o = new byte[16];
        this.encrypt(i, ioffset, o, 0);
        return o;
    }
    void decrypt(byte[] i, int ioffset, byte[] o, int ooffset) throws InvalidKeyException {
        if (this.keySize==0)
          throw new InvalidKeyException("keySize");
        if (this.decRoundKeys==null)
          if (this.masterKey==null)
            throw new InvalidKeyException("masterKey");
          else
            setupDecRoundKeys();
        doCrypt(i, ioffset, this.decRoundKeys, this.numberOfRounds, o, ooffset);
    }
    
    byte[] decrypt(byte[] i, int ioffset) throws InvalidKeyException {
        byte[] o = new byte[16];
        this.decrypt(i, ioffset, o, 0);
        return o;
    }
    private static void doEncKeySetup(byte[] mk, int[] rk, int keyBits) {      
      int t0, t1, t2, t3, q, j=0;
      int[] w0 = new int[4];
      int[] w1 = new int[4];
      int[] w2 = new int[4];
      int[] w3 = new int[4];
      
      w0[0] = toInt(mk[ 0], mk[ 1], mk[ 2], mk[ 3]);
      w0[1] = toInt(mk[ 4], mk[ 5], mk[ 6], mk[ 7]);
      w0[2] = toInt(mk[ 8], mk[ 9], mk[10], mk[11]);
      w0[3] = toInt(mk[12], mk[13], mk[14], mk[15]);
      
      q = (keyBits - 128) / 64;
      t0=w0[0]^KRK[q][0]; t1=w0[1]^KRK[q][1];
      t2=w0[2]^KRK[q][2]; t3=w0[3]^KRK[q][3];  
      t0=TS1[(t0>>>24)&0xff]^TS2[(t0>>>16)&0xff]^TX1[(t0>>>8)&0xff]^TX2[t0&0xff];
      t1=TS1[(t1>>>24)&0xff]^TS2[(t1>>>16)&0xff]^TX1[(t1>>>8)&0xff]^TX2[t1&0xff];
      t2=TS1[(t2>>>24)&0xff]^TS2[(t2>>>16)&0xff]^TX1[(t2>>>8)&0xff]^TX2[t2&0xff];
      t3=TS1[(t3>>>24)&0xff]^TS2[(t3>>>16)&0xff]^TX1[(t3>>>8)&0xff]^TX2[t3&0xff];   
      t1^=t2; t2^=t3; t0^=t1; t3^=t1; t2^=t0; t1^=t2;
      t1=badc(t1); t2=cdab(t2); t3=dcba(t3);
      t1^=t2; t2^=t3; t0^=t1; t3^=t1; t2^=t0; t1^=t2;
      
      if (keyBits > 128) {
        w1[0] = toInt(mk[16], mk[17], mk[18], mk[19]);
        w1[1] = toInt(mk[20], mk[21], mk[22], mk[23]);
        if (keyBits > 192) {
          w1[2] = toInt(mk[24], mk[25], mk[26], mk[27]);
          w1[3] = toInt(mk[28], mk[29], mk[30], mk[31]);
        } else {
          w1[2]=w1[3]=0;
        }
      } else {
        w1[0]=w1[1]=w1[2]=w1[3]=0;
      }
      w1[0]^=t0; w1[1]^=t1; w1[2]^=t2; w1[3]^=t3;
      t0=w1[0];  t1=w1[1];  t2=w1[2];  t3=w1[3];
      
      q = (q==2)? 0 : (q+1);
      t0^=KRK[q][0]; t1^=KRK[q][1]; t2^=KRK[q][2]; t3^=KRK[q][3];
      t0=TX1[(t0>>>24)&0xff]^TX2[(t0>>>16)&0xff]^TS1[(t0>>>8)&0xff]^TS2[t0&0xff];
      t1=TX1[(t1>>>24)&0xff]^TX2[(t1>>>16)&0xff]^TS1[(t1>>>8)&0xff]^TS2[t1&0xff];
      t2=TX1[(t2>>>24)&0xff]^TX2[(t2>>>16)&0xff]^TS1[(t2>>>8)&0xff]^TS2[t2&0xff];
      t3=TX1[(t3>>>24)&0xff]^TX2[(t3>>>16)&0xff]^TS1[(t3>>>8)&0xff]^TS2[t3&0xff]; 
      t1^=t2; t2^=t3; t0^=t1; t3^=t1; t2^=t0; t1^=t2;
      t3=badc(t3); t0=cdab(t0); t1=dcba(t1);        
      t1^=t2; t2^=t3; t0^=t1; t3^=t1; t2^=t0; t1^=t2;
      t0^=w0[0]; t1^=w0[1]; t2^=w0[2]; t3^=w0[3];
      w2[0]=t0; w2[1]=t1; w2[2]=t2; w2[3]=t3;
      
      q = (q==2)? 0 : (q+1);
      t0^=KRK[q][0]; t1^=KRK[q][1]; t2^=KRK[q][2]; t3^=KRK[q][3];
      t0=TS1[(t0>>>24)&0xff]^TS2[(t0>>>16)&0xff]^TX1[(t0>>>8)&0xff]^TX2[t0&0xff];
      t1=TS1[(t1>>>24)&0xff]^TS2[(t1>>>16)&0xff]^TX1[(t1>>>8)&0xff]^TX2[t1&0xff];
      t2=TS1[(t2>>>24)&0xff]^TS2[(t2>>>16)&0xff]^TX1[(t2>>>8)&0xff]^TX2[t2&0xff];
      t3=TS1[(t3>>>24)&0xff]^TS2[(t3>>>16)&0xff]^TX1[(t3>>>8)&0xff]^TX2[t3&0xff];   
      t1^=t2; t2^=t3; t0^=t1; t3^=t1; t2^=t0; t1^=t2;
      t1=badc(t1); t2=cdab(t2); t3=dcba(t3);
      t1^=t2; t2^=t3; t0^=t1; t3^=t1; t2^=t0; t1^=t2;
      w3[0]=t0^w1[0]; w3[1]=t1^w1[1]; w3[2]=t2^w1[2]; w3[3]=t3^w1[3];
      
      gsrk(w0, w1, 19, rk, j); j+=4;
      gsrk(w1, w2, 19, rk, j); j+=4;
      gsrk(w2, w3, 19, rk, j); j+=4;
      gsrk(w3, w0, 19, rk, j); j+=4;
      gsrk(w0, w1, 31, rk, j); j+=4;
      gsrk(w1, w2, 31, rk, j); j+=4;
      gsrk(w2, w3, 31, rk, j); j+=4;
      gsrk(w3, w0, 31, rk, j); j+=4;
      gsrk(w0, w1, 67, rk, j); j+=4;
      gsrk(w1, w2, 67, rk, j); j+=4;
      gsrk(w2, w3, 67, rk, j); j+=4;
      gsrk(w3, w0, 67, rk, j); j+=4;
      gsrk(w0, w1, 97, rk, j); j+=4;
      if (keyBits > 128) {  
        gsrk(w1, w2, 97, rk, j); j+=4;
        gsrk(w2, w3, 97, rk, j); j+=4;
      }
      if (keyBits > 192) {
        gsrk(w3, w0,  97, rk, j); j+=4;
        gsrk(w0, w1, 109, rk, j);
      }
    }
    
     private static void doDecKeySetup(byte[] mk, int[] rk, int keyBits) {
      int a=0, z;
      int[] t = new int[4];
      
      z=32+keyBits/8;
      swapBlocks(rk, 0, z);
      a+=4; z-=4;
      
      for (; a<z; a+=4, z-=4)
        swapAndDiffuse(rk, a, z, t);
      diff(rk, a, t, 0);
      rk[a]=t[0]; rk[a+1]=t[1]; rk[a+2]=t[2]; rk[a+3]=t[3];
    }
    private static int toInt(byte b0, byte b1, byte b2, byte b3) {
      return (b0&0xff)<<24 ^ (b1&0xff)<<16 ^ (b2&0xff)<<8 ^ b3&0xff;
    }
    
    private static void toByteArray(int i, byte[] b, int offset) {
      b[offset  ] = (byte)(i>>>24);
      b[offset+1] = (byte)(i>>>16);
      b[offset+2] = (byte)(i>>> 8);
      b[offset+3] = (byte)(i     );
    }
    private static int m(int t) {
        return 0x00010101*((t>>>24)&0xff) ^ 0x01000101*((t>>>16)&0xff) ^ 
      0x01010001*((t>>>8)&0xff) ^ 0x01010100*(t&0xff);
    }
    
    private static final int badc(int t) {
      return ((t<<8)&0xff00ff00) ^ ((t>>>8)&0x00ff00ff);
    }
    
    private static final int cdab(int t) {
      return ((t<<16)&0xffff0000) ^ ((t>>>16)&0x0000ffff);
    }
    
    private static final int dcba(int t) {
      return (t&0x000000ff)<<24 ^ (t&0x0000ff00)<<8 ^ (t&0x00ff0000)>>>8 ^ (t&0xff000000)>>>24;
    }
    
    private static final void gsrk(int[] x, int[] y, int rot, int[] rk, int offset) {
      int q=4-(rot/32), r=rot%32, s=32-r;
      
      rk[offset]   = x[0] ^ y[(q  )%4]>>>r ^ y[(q+3)%4]<<s;
      rk[offset+1] = x[1] ^ y[(q+1)%4]>>>r ^ y[(q  )%4]<<s;
      rk[offset+2] = x[2] ^ y[(q+2)%4]>>>r ^ y[(q+1)%4]<<s;
      rk[offset+3] = x[3] ^ y[(q+3)%4]>>>r ^ y[(q+2)%4]<<s;
    }
    
    private static final void diff(int[] i, int offset1, int[] o, int offset2) {
      int t0, t1, t2, t3;
        
      t0=m(i[offset1]); t1=m(i[offset1+1]); t2=m(i[offset1+2]); t3=m(i[offset1+3]);         
      t1^=t2; t2^=t3; t0^=t1; t3^=t1; t2^=t0; t1^=t2;
      t1=badc(t1); t2=cdab(t2); t3=dcba(t3);
      t1^=t2; t2^=t3; t0^=t1; t3^=t1; t2^=t0; t1^=t2;
      o[offset2]=t0; o[offset2+1]=t1; o[offset2+2]=t2; o[offset2+3]=t3;
    }
    
    private static final void swapBlocks(int[] arr, int offset1, int offset2) {
      int t;
      
      for (int i=0; i<4; i++) {
        t = arr[offset1+i];
        arr[offset1+i] = arr[offset2+i];
        arr[offset2+i] = t;
      }
    }
    
    private static final void swapAndDiffuse(int[] arr, int offset1, int offset2, int[] tmp) {
      diff(arr, offset1, tmp, 0);
      diff(arr, offset2, arr, offset1);
      arr[offset2]=tmp[0]; arr[offset2+1]=tmp[1]; 
      arr[offset2+2]=tmp[2]; arr[offset2+3]=tmp[3];
    }
    
    private static void printBlock(PrintStream out, byte[] b) {
      for (int i=0; i<4; i++)
        byteToHex(out, b[i]);
      out.print(" ");
      for (int i=4; i<8; i++)
        byteToHex(out, b[i]);
      out.print(" ");
      for (int i=8; i<12; i++)
        byteToHex(out, b[i]);
      out.print(" ");
      for (int i=12; i<16; i++)
        byteToHex(out, b[i]);
    }
    
    private static void printEn_Decrypt(PrintStream out, byte[] b) {
        out.print(new String(b));
      }
    
    private static void printSBox(PrintStream out, byte[] box) {
        for (int i=0; i<16; i++) {
          for (int j=0; j<16; j++) {
            byteToHex(out, box[16*i+j]);
            out.print(" ");
          }
          out.println();
        }
    }
    
    private static void byteToHex(PrintStream out, byte b) {
      char[] buf = {
        HEX_DIGITS[(b >>> 4) & 0x0F],
        HEX_DIGITS[ b        & 0x0F]
      };
      out.print(new String(buf));    
    }
    
    private static void intToHex(PrintStream out, int i) {
      byte[] b = new byte[4];
      toByteArray(i, b, 0);
      byteToHex(out, b[0]);
      byteToHex(out, b[1]);
      byteToHex(out, b[2]);
      byteToHex(out, b[3]);
    }
    
    private static void printRoundKeys(PrintStream out, int[] roundKeys) {
      for (int i=0; i<roundKeys.length; ) { out.print("* ");
        intToHex(out, roundKeys[i++]); out.print(" ");
        intToHex(out, roundKeys[i++]); out.print(" ");
        intToHex(out, roundKeys[i++]); out.print(" ");
        intToHex(out, roundKeys[i++]); out.print(" \n");
      }
    }
    
    public static byte[] ARIA_test(byte[] param) throws InvalidKeyException {
      byte[] p = new byte[16];
      byte[] c = new byte[16];
      byte[] mk = new byte[32];
      
      boolean flag=false;
      PrintStream out=System.out;
      ARIAEngine instance = new ARIAEngine(256);
      
      for (int i=0; i<32; i++)
        mk[i]=0;
      for (int i=0; i<16; i++)
        p[i]=param[i];
      
      instance.setKey(mk);
      instance.setupRoundKeys();
      
      out.print("plaintext : "); printEn_Decrypt(out, p); out.println();
      instance.encrypt(p, 0, c, 0);
      out.print("ciphertext: "); printBlock(out, c); out.println();
      instance.decrypt(c, 0, p, 0);
      out.print("decrypted : "); printEn_Decrypt(out, p); out.println();
      return p;
    } 
    
    public static void setMK(byte[] mk1)
    {
      for (int i=0; i<32; i++)
          mk[i]=0;
      
      for (int rr=0 ; rr < mk1.length ; rr++)
          mk[rr]=mk1[rr];
    }
    
    public static byte[] Encrypt(byte[] param) throws InvalidKeyException {
        byte[] p = new byte[16];
        byte[] c = new byte[16];
        //byte[] mk = new byte[32];
        
        boolean flag=false;
        PrintStream out=System.out;
        ARIAEngine instance = new ARIAEngine(256);
        
      //  for (int i=0; i<32; i++)
      //      mk[i]=0;
        for (int i=0; i<16; i++)
          p[i]=param[i];
        
        instance.setKey(mk);
        instance.setupRoundKeys();
        
        instance.encrypt(p, 0, c, 0);
        out.print("ciphertext : "); printEn_Decrypt(out, c); out.println();
        return c;
    }
    
    public static byte[] Decrypt(byte[] param) throws InvalidKeyException {
        byte[] p = new byte[16];
        byte[] c = new byte[16];
        //byte[] mk = new byte[32];
        
        boolean flag=false;
        PrintStream out=System.out;
        ARIAEngine instance = new ARIAEngine(256);
        
        //for (int i=0; i<32; i++)
        //  mk[i]=0;
        for (int i=0; i<16; i++)
          c[i]=param[i];
        
        instance.setKey(mk);
        instance.setupRoundKeys();
        
        instance.decrypt(c, 0, p, 0);
        out.print("decrypted : "); printEn_Decrypt(out, p); out.println();
        return p;
    }
  }
/**
 * ARIA 암호화 모듈를 이용할 수 있도록 기능 제공
 * @author 개발프레임웍크 실행환경 개발팀 김종호
 * @since 2009. 03.10
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자              수정내용
 *  ---------   ---------   -------------------------------
 *  2009.03.10    김종호        최초생성
 *  2010.11.23    한성곤        ARIA 알고리즘의 마스터키 크기가 32 byte 이상인 경우 오류 수정 
 * 
 * </pre>
 */
public class EgovCipherService {
    /** 마스터 키  */
    String mk_str = null;
    
    /**
     * 암호 설정
     * @param mk_str 암호문자열
     */
    public void setPassword(String mk_str)
    {
	//-------------------------------------
	// 2010-11-23
	//-------------------------------------
	mk_str = (mk_str.length() > 32 ?  mk_str.substring(0, 32) : mk_str);
	////-----------------------------------
	this.mk_str = mk_str;
        ARIAEngine.setMK(mk_str.getBytes());
    }
    /**
     * 바이트 배열 리턴 암호화
     * @param enString 암호화할 문자열
     * @return 암호화된 바이트배열
     */
    public  byte[] encrypt(String enString)
    {
        String pp = enString;
        byte[] rtn_byte = null;
        byte[] cipher = null;
        byte[] pp_arr = pp.getBytes();
        int a_1 = pp_arr.length/16;
        int a_2 = pp_arr.length%16;
        int a_3 = 16 - a_2;
        byte[] a_tmp = null;
        
        if(a_2 != 0)
            a_tmp = new byte[pp_arr.length+a_3];
          else
            a_tmp = new byte[pp_arr.length];   
        
        for(int q=0; q< a_tmp.length; q++)
            a_tmp[q]=0;
            
        for(int io=0; io< pp_arr.length; io++)
            a_tmp[io] = pp_arr[io];
        
        
          if(a_2 > 0)
            a_1 +=1;
        
        byte[][] yu = new byte[a_1][16];
       
        int gh=0;
            int jh=0;
            for(int y=0; y<a_tmp.length ; y++)
            {
                
                yu[gh][jh] = a_tmp[y];
                jh++;
                
                if((y+1)%16 == 0)
                {
                    gh++;
                    jh = 0;
                }
            }
        
         try 
         {
          byte[] p_tmp = new byte[a_tmp.length];
          byte[] p_tmp_ = new byte[16];
          int po = 0;
          for(int tt=0; tt<a_1; tt++)   
          {
            byte[] tmp = yu[tt]; 
            cipher =  ARIAEngine.Encrypt(tmp);
            for(int y=0; y<cipher.length ; y++)
            {
                p_tmp[po] =  cipher[y];
                po++;      
            }
          }
          rtn_byte = p_tmp;
      } catch(Exception e) {e.printStackTrace();}
      return rtn_byte;
    }
    /**
     * 바이트 배열 리턴 암호화
     * @param bytearr 암호화할 바이트배열
     * @return 암호화된 바이트배열
     */
    public  byte[] encrypt(byte[] bytearr)
    {
        byte[] rtn_byte = null;
        byte[] cipher = null;
        byte[] pp_arr = bytearr;
        int a_1 = pp_arr.length/16;
        int a_2 = pp_arr.length%16;
        int a_3 = 16 - a_2;
        byte[] a_tmp = null;
        
        if(a_2 != 0)
            a_tmp = new byte[pp_arr.length+a_3];
          else
            a_tmp = new byte[pp_arr.length];   
        
        for(int q=0; q< a_tmp.length; q++)
            a_tmp[q]=0;
            
        for(int io=0; io< pp_arr.length; io++)
            a_tmp[io] = pp_arr[io];
        
        
          if(a_2 > 0)
            a_1 +=1;
        
        byte[][] yu = new byte[a_1][16];
       
        int gh=0;
            int jh=0;
            for(int y=0; y<a_tmp.length ; y++)
            {
                
                yu[gh][jh] = a_tmp[y];
                jh++;
                
                if((y+1)%16 == 0)
                {
                    gh++;
                    jh = 0;
                }
            }
        
         try 
         {
          byte[] p_tmp = new byte[a_tmp.length];
          byte[] p_tmp_ = new byte[16];
          int po = 0;
          for(int tt=0; tt<a_1; tt++)   
          {
            byte[] tmp = yu[tt]; 
            cipher =  ARIAEngine.Encrypt(tmp);
            for(int y=0; y<cipher.length ; y++)
            {
                p_tmp[po] =  cipher[y];
                po++;      
            }
          }
          rtn_byte = p_tmp;
      } catch(Exception e) {e.printStackTrace();}
      return rtn_byte;
    }
    /**
     * 복호화 배열 리턴 암호화
     * @param deString 복호화할 데이타 바이트배열
     * @return 복호화된 바이트배열
     */
    public byte[] decrypt(byte[] deString)
    {
        String rtn_str = "";
        byte[] cipher = null;
        byte[] rtn_arr = null;
        byte[] pp_arr = deString;
        int a_1 = pp_arr.length/16;
        int a_2 = pp_arr.length%16;
        int a_3 = 16 - a_2;
        byte[] a_tmp = null;
        int gcount = 0; 
        
        if(a_2 != 0)
            a_tmp = new byte[pp_arr.length+a_3];
          else
            a_tmp = new byte[pp_arr.length];   
        
        for(int q=0; q< a_tmp.length; q++)
            a_tmp[q]=0;
            
        for(int io=0; io< pp_arr.length; io++)
            a_tmp[io] = pp_arr[io];
        
        
          if(a_2 > 0)
            a_1 +=1;
        
        byte[][] yu = new byte[a_1][16];
       
        int gh=0;
            int jh=0;
            for(int y=0; y<a_tmp.length ; y++)
            {
                
                yu[gh][jh] = a_tmp[y];
                jh++;
                
                if((y+1)%16 == 0)
                {
                    gh++;
                    jh = 0;
                }
            }
        
         try 
         {
          byte[] p_tmp = new byte[a_tmp.length];
          byte[] p_tmp_ = new byte[16];
          int po = 0;
          for(int tt=0; tt<a_1; tt++)   
          {
            byte[] tmp = yu[tt]; 
            p_tmp_ = ARIAEngine.Decrypt(tmp);
            for(int y=0; y<p_tmp_.length ; y++)
            {
                p_tmp[po] =  p_tmp_[y];
                if(tt == (a_1-1) && p_tmp[po]==0 )
                {
                    gcount++;
                }
                po++;      
            }
          }
          rtn_arr = new byte[p_tmp.length-gcount];
          for(int qq=0; qq < rtn_arr.length; qq++) 
            rtn_arr[qq] = p_tmp[qq];
      } catch(Exception e) {e.printStackTrace();}
      return rtn_arr;
    }
    
   
}

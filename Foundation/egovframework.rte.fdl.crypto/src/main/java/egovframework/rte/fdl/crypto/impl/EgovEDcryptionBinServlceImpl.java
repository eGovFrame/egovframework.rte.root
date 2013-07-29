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
package egovframework.rte.fdl.crypto.impl;

import java.io.*;
import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.*; 

import egovframework.rte.fdl.crypto.EgovPasswordLoad;
import egovframework.rte.fdl.crypto.ConfigInfo;
import egovframework.rte.fdl.crypto.CryptoLog;
import egovframework.rte.fdl.crypto.EgovEDcryptionService;
import egovframework.rte.fdl.crypto.exception.UnsupportedException;

/**
 *  바이너리 암호화 구현 Class
 * @author 개발프레임웍크 실행환경 개발팀 김종호
 * @since   2009. 03.12
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자              수정내용
 *  ---------   ---------   -------------------------------
 *  2009. 03.12    김종호        최초생성
 * 
 * </pre>
 */
public class EgovEDcryptionBinServlceImpl implements EgovEDcryptionService {
	private static Logger logger  = CryptoLog.getLogger(EgovEDcryptionBinServlceImpl.class);
	/** 복호화 시 사용 바이트 배열*/
	byte buf[] = null; 
	/** 암호화 결과 리턴 바이트 배열*/
	byte[] rt_buf = null;
	/** 복호화 결과 리턴 바이트 배열*/
	byte[] rtn_buf = null;
	/** 복호화 문자열 */
	static String decypt_str = null; 
	/** 암호화 문자열 */
    static String encrypt_str = null;
    /** 임시 문자열 */
    String tmp_con = "";
    /** 패스워드 확인 문자열 */    
    String orign_pwd = null;
    /** 패스워드 확인 문자열 */
    String new_pwd = null;
    /** 임시 Char 배열 */
    char[] tmp_arr = null;
    /** 파일명 문자열 */
    String str_or_file = "";
    /** 알고리즘 문자열 */
    String algorithm = "";
    /** 파일, 문자열 암호화 구분 */
    int iswhich=-1;
    /** 암복화 Class */
    StandardPBEByteEncryptor EDcryptor = null;
    /** 패스워드 암호화, 암호화된 패스워드와 입력된 암호 비교 Class */
    EgovPasswordLoad epl = null;
    FileInputStream fis =null;
	FileOutputStream fos = null;
	/** Password 와 Algorithm 추출하는 Class */
	ConfigInfo cinfo = null; 
	/** 패스 문자열 */
	String path_str = null;
	
	/**
	 * EgovEDcryptionBinServlceImpl 생성자
	 */
	public EgovEDcryptionBinServlceImpl()
	{
		EDcryptor = new StandardPBEByteEncryptor();
		cinfo = new ConfigInfo(path_str);
		EDcryptor.setAlgorithm(cinfo.getAlgorithm().trim());
		EDcryptor.setPassword(cinfo.getPassword().trim());
		epl = new EgovPasswordLoad();
	}
	
	/**
	 * 암호화 문자열 또는 파일명 저장
	 * @param iswhich - 문자열 또는 바이너리 암호화 구분 
	 * @param str_or_file - 암호화 할 문자열 또는 파일명
	 */
	public void setConfig(int iswhich,String str_or_file)
	{
		this.iswhich = iswhich;
		this.str_or_file = str_or_file;
	}
	/**
	 * 복호화
	 * @return 복호화된 데이터 바이트 배열
	 */
	public byte[] decrypt() {
		// TODO Auto-generated method stub
		    String tmp_enc = epl.encrypt(new_pwd);
		  if(tmp_enc.equals(cinfo.getPassword().trim()))
		 {
			 try
		       {
	    		fos = new FileOutputStream( str_or_file.replaceAll("_egov_encrypt_", "_egov_decrypt_"));
				fis = new FileInputStream(str_or_file);
				buf = new byte[fis.available()];
				 fis.read(buf);
				}catch(Exception e)
		       {
					logger.debug(e.getMessage());
		       }
	    	   rtn_buf = EDcryptor.decrypt(buf);
	    	 
	    	 try
			  {
			   fos.write(rtn_buf);
			  }catch(IOException e)
			  {
				  e.printStackTrace();
			  }
			  finally
			  {
				 try
				  {
					  fos.close();
					  fis.close();
				  }catch(IOException ee)
				  {
					ee.printStackTrace();  
				  }
			  }
		 }else
		 {
			 logger.debug("[Decrypt] 암호가 설정되지 안았습니다.");
			 System.exit(0);
		 }
		 
		return rtn_buf;
	}

	

	/**
	 * 암호화
	 * @return 암호화된 데이터 바이트 배열
	 */
	public byte[] encrypt() {
	// TODO Auto-generated method stub
	
	String tmp_enc = epl.encrypt(orign_pwd);
	logger.debug("tmp_enc :"+tmp_enc);
	logger.debug("password :"+cinfo.getPassword().trim());
	if(tmp_enc.equals(cinfo.getPassword().trim()))
	 {
		  if(iswhich == 0)
		  {
	       try
	       {
	    	int index_ = str_or_file.indexOf('.');
	    	fos = new FileOutputStream(str_or_file.substring(0,index_)+"_egov_encrypt_"+str_or_file.substring(index_));
			fis = new FileInputStream(str_or_file);
			buf = new byte[fis.available()];
			 fis.read(buf);
			}catch(Exception e)
	       {
				logger.debug(e.getMessage());
	       }
		  }else if(iswhich == 1)
		  {
			  buf = str_or_file.getBytes();
		  }
		  rt_buf = EDcryptor.encrypt(buf);
		  try
		  {
		   fos.write(rt_buf);
		  }catch(IOException e)
		  {
			  e.printStackTrace();
		  }
		  finally
		  {
			 try
			  {
				  fos.close();
				  fis.close();
			  }catch(IOException ee)
			  {
				ee.printStackTrace();  
			  }
		  }
	 }else
	 {
		 logger.debug("[Encrypt]암호가 설정되지 안았습니다.");
		 System.exit(0);
	 }
	
		return rt_buf;
	}
    /**
     * Number 암호화
     * @param bigdecimal - 암호화 할 Number
     * @return  암호화된 bigdecimal
      */
	public BigDecimal encrypt(BigDecimal bigdecimal)
    {
	// TODO Auto-generated method stub
		try
		 {
		   throw new UnsupportedException("지원하지 않는 메소드 입니다.");
		 }catch(UnsupportedException e)
		 {
			 e.printStackTrace();
		 }
		 return null;
    }
	/**
     * 복호화
     * @param bigdecimal - 복호화 할 Number
     * @return  암호화된 bigdecimal
     */ 
    public BigDecimal decrypt(BigDecimal bigdecimal)
    {
    	// TODO Auto-generated method stub
		try
		 {
		   throw new UnsupportedException("지원하지 않는 메소드 입니다.");
		 }catch(UnsupportedException e)
		 {
			 e.printStackTrace();
		 }
		 return null;
    }
	/**
	 * 암호화전 패스워드 확인
	 * @param pwd - 확인할 패스워드
	 */
	public void setComformStr(String pwd)
    {
   	 if(orign_pwd == null)
   		 orign_pwd = pwd;
   	 else
   		logger.debug("암호가 이미 설정되어 있습니다.");	 
    } 
	/**
	 * 복호화전 패스워드 확인
	 * @param pwd - 확인할 패스워드
	 */
    public void getComformStr(String pwd)
    {
    	 new_pwd = pwd;
   	}
    /**
     * 알고리즘 설정
     * @param algorithm - 알고리즘 명
    */
    public void setAlgorithm(String algorithm)
    {
    	this.algorithm = algorithm;
    }
    /**
     * 패스워드 암호화 여부선택
     * @param isplan - 선택여부
     */
    public void setPlainDigest(boolean isplan)throws UnsupportedException
    {
    	try
		 {
		   throw new UnsupportedException("지원하지 않는 메소드 입니다.");
		 }catch(UnsupportedException e)
		 {
			 e.printStackTrace();
		 }
    }
    /**
     * 암호화된 패스워드와 평문 패스워드 비교
     * @param planPD - 평문 패스워드
     * @param cryptoPD - 암호화된 패스워드
     * @return 비교결과
     */
	 public boolean checkPassword(String planPD,byte[] cryptoPD)
	 {
		 try
		 {
		   throw new UnsupportedException("지원하지 않는 메소드 입니다.");
		 }catch(UnsupportedException e)
		 {
			 e.printStackTrace();
		 }
		 return false;
	 }
	 
	 /**
	     * 암호화 문자열 또는 파일명 저장
	     * @param iswhich - 문자열 또는 바이너리 암호화 구분 
	     * @param str_or_file - 암호화 할 byte 배열
	     */
	    public void setARIAConfig(int is_which, byte[] p_arr) {
	    // TODO Auto-generated method stub
        try
         {
           throw new UnsupportedException("지원하지 않는 메소드 입니다.");
         }catch(UnsupportedException e)
         {
             e.printStackTrace();
         }
	    }
	    
	    /**
	     * Aria Number 암호화
	     * @param bigdecimal - 암호화 할 Number
	     * @return 복호화된 데이터 바이트 배열
	     */
	    public byte[] Aria_encrypt(BigDecimal bigdecimal)
	    {
	        try
	         {
	           throw new UnsupportedException("지원하지 않는 메소드 입니다.");
	         }catch(UnsupportedException e)
	         {
	             e.printStackTrace();
	         }
	         return null;
	    }
	    /**
	     * Aria Number 복호화
	     * @param endecimal - 복호화 할 Number byte 배열
	     * @exception  UnsupportedException
	     * @return 복호화된 endecimal
	     */ 
	    public BigDecimal Aria_decrypt(byte[] endecimal)
	    {
	        try
	         {
	           throw new UnsupportedException("지원하지 않는 메소드 입니다.");
	         }catch(UnsupportedException e)
	         {
	             e.printStackTrace();
	         }
	         return null;
	    }
}

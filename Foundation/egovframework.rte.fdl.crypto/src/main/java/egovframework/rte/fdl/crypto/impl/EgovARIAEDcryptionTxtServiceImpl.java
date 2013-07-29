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

import java.math.BigDecimal;
import egovframework.rte.fdl.crypto.EgovEDcryptionService;
import egovframework.rte.fdl.crypto.EgovCipherService;
import egovframework.rte.fdl.crypto.exception.UnsupportedException;
import org.apache.log4j.Logger;
import egovframework.rte.fdl.crypto.ConfigInfo;
import egovframework.rte.fdl.crypto.CryptoLog;
import egovframework.rte.fdl.crypto.EgovPasswordLoad;

/**
 * ARIA 모듈 Text 암호화 구현 Class
 * @author 개발프레임웍크 실행환경 개발팀 김종호
 * @since   2009. 05.15
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자              수정내용
 *  ---------   ---------   -------------------------------
 *   2009. 05.15    김종호        최초생성
 * 
 * </pre>
 */

public class EgovARIAEDcryptionTxtServiceImpl implements EgovEDcryptionService {
    private static Logger logger  = CryptoLog.getLogger(EgovARIAEDcryptionTxtServiceImpl.class);
    /** 패스워드 확인 문자열 */
    String orign_pwd = null;
    /** Password 와 Algorithm 추출하는 Class */
    ConfigInfo cinfo = null;
    /** 패스워드 확인 문자열 */
    String new_pwd = null;
    /** 파일명 문자열 */
    String str_or_file = "";
    /** 암호화 결과 리턴 바이트 배열*/
    public byte[] rt_buf = null;
    /** 파일명 byte 배열 */
    byte[] p_arr = null;
    /** 패스워드 암호화, 암호화된 패스워드와 입력된 암호 비교 Class */
    EgovPasswordLoad epl = null;
    /** 원본 문자열 byte[] length **/
    int arrlength = 0;
    
    EgovCipherService egovCipherService = null;
    
    
    /**
     * EgovARIAEDcryptionTxtServiceImpl 생성자
     * @param filePath - 암복화 설정 파일 경로
     */
    public EgovARIAEDcryptionTxtServiceImpl(String filePath)
    {
        cinfo = new ConfigInfo(filePath);
        egovCipherService = new EgovCipherService();
        egovCipherService.setPassword(cinfo.getPassword().trim());
        epl = new EgovPasswordLoad();  
    }
    
    /**
     * EgovARIAEDcryptionTxtServiceImpl 생성자
     */
    public EgovARIAEDcryptionTxtServiceImpl()
    {
        egovCipherService = new EgovCipherService();
        cinfo = new ConfigInfo(null);
        egovCipherService.setPassword(cinfo.getPassword().trim());
        epl = new EgovPasswordLoad();
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
      * 복호화
      * @return 복호화된 데이터 바이트 배열
      */
    public byte[] decrypt() {
        // TODO Auto-generated method stub
        byte rtn_buf[] = null;
        byte nrtn_buf[] = new byte[arrlength];
        String pram_str1 = "";
        String tmp_enc = epl.encrypt(new_pwd);
           
        if(tmp_enc.equals(cinfo.getPassword().trim()))
        {
            rtn_buf = egovCipherService.decrypt(p_arr);
        }else
        {
            logger.debug("암호가 설정되지 안았습니다.");
            System.exit(0);
        }
        
       return rtn_buf;
    }

    /**
     * 암호화
     * @return 암호된 데이터 바이트 배열
     */
    public byte[] encrypt() {
        // TODO Auto-generated method stub
        String pram_str = "";
        String tmp_enc = epl.encrypt(orign_pwd);
        
        if(tmp_enc.equals(cinfo.getPassword().trim()))
         {
            arrlength = (str_or_file.getBytes()).length;
            rt_buf = egovCipherService.encrypt(str_or_file);
         }else
         {
             logger.debug("암호가 설정되지 안았습니다.");
             System.exit(0);
         }
        
        return rt_buf;
    }

    /**
     * Number 암호화
     * @param bigdecimal - 암호화 할 Number
     * @return 복호화된 bigdecimal
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
     * @return 복호화된  bigdecimal
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
    public void setAlgorithm(String alg) {
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
     * 암호화 문자열 또는 파일명 저장
     * @param iswhich - 문자열 또는 바이너리 암호화 구분 
     * @param str_or_file - 암호화 할 문자열 또는 파일명
     */
    public void setConfig(int is_which, String str_or_file) {
        // TODO Auto-generated method stub
        this.str_or_file = str_or_file;
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
     * 패스워드 암호화 여부선택
     * @param isplan - 선택여부
     * @exception UnsupportedException
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
     * 암호화 문자열 또는 파일명 저장
     * @param iswhich - 문자열 또는 바이너리 암호화 구분 
     * @param str_or_file - 암호화 할 byte 배열
     */
    public void setARIAConfig(int is_which, byte[] p_arr) {
        // TODO Auto-generated method stub
        this.p_arr = p_arr;
    }
    
    /**
     * Aria Number 암호화
     * @param bigdecimal - 암호화 할 Number
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
     * @return  복호화된 endecimal
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

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

import egovframework.rte.fdl.crypto.exception.UnsupportedException;
import java.math.BigDecimal;
/**
 * 암복화 Interface Class
 * @author 실행환경 개발팀 김태호
 * @since 2009.02.01
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.03.10    김종호        최초생성
 * 
 * </pre>
 */
public interface EgovEDcryptionService {
    
    /**
     * 바이트 배열 리턴 암호화
     * 
     * @return 암호화된 바이트 배열
     */
    public byte[] encrypt();
    /**
     * 바이트 배열 리턴 복호화
     * 
     * @return 복호화된 바이트 배열
     */	 
    public byte[] decrypt();
    /**
     *BigDecimal 리턴 암호화
     * 
     * @return 암호화된 BigDecimal
     */  
    public BigDecimal encrypt(BigDecimal bigdecimal);
    /**
     *BigDecimal 리턴 복호화
     * 
     * @return 복호화된 BigDecimal
     */  
    public BigDecimal decrypt(BigDecimal bigdecimal);
    /**
     * ARIA 암호화 모듈을 이용한 BigDecimal 리턴 암호화
     * @param bigdecimal 암호화할 bigdecimal
     * @return 암호화된 BigDecimal
     */  
    public byte[] Aria_encrypt(BigDecimal bigdecimal);
    /**
     * ARIA 복호화 모듈을 이용한 BigDecimal 리턴 복호화
     * @param bigdecimal 복호화할 bigdecimal
     * @return 복호화된 BigDecimal
     */  
    public BigDecimal Aria_decrypt(byte[] endecimal);
    /**
     * 암호 설정
     * @param pwd 암호설정
     * @exception UnsupportedException
     */  
    public void setComformStr(String pwd) throws UnsupportedException;
    /**
     * 암호 설정
     * @param pwd 암호설정
     * @exception UnsupportedException
     */  
    public void getComformStr(String pwd)throws UnsupportedException;
    /**
     * 암호화 알고리즘 설정
     * @param alg 알고리즘명
     * @exception UnsupportedException
     */  
    public void setAlgorithm(String alg)throws UnsupportedException;
    /**
     * 암호화 알고리즘 설정
     * @param alg 알고리즘명
     * @exception UnsupportedException
     */  
    public void setConfig(int is_which,String str_or_file)throws UnsupportedException;
    /**
     * 암호화할 파일 또는 문자열 설정
     * @param is_which 선택
     * @param str_or_file 암호화할 문자열 또는 파일
     * @exception UnsupportedException
     */  
    public void setPlainDigest(boolean isplan)throws UnsupportedException;
    /**
     * 암호화된 암호와 문자열 암호를 비교
     * @param planPD 암호 문자열
     * @param cryptoPD 암호화된 암호 바이트 배열
     * @exception UnsupportedException
     */  
    public boolean checkPassword(String planPD,byte[] cryptoPD);
    /**
     * ARIA 암호모듈로 암호화할 파일 또는 문자열 설정
     * @param planPD 암호 문자열
     * @param cryptoPD 암호화된 암호 바이트 배열
     * @exception UnsupportedException
     */  
    public void setARIAConfig(int is_which,byte[] p_arr)throws UnsupportedException;
	
}

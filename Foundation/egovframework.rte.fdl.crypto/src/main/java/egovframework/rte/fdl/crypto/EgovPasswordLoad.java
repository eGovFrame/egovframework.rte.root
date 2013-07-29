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

import org.apache.log4j.Logger;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
    
/*
* 패스워드 암호화, 암호화된 패스워드와 입력된 암호 비교
* @author 개발프레임웍크 실행환경 개발팀 김종호
* @since 2009. 03.10
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

public class EgovPasswordLoad {
 /** Password 암호화 Class */	
 ConfigurablePasswordEncryptor  passwordEncryptor  = null;
 private static Logger logger  = CryptoLog.getLogger(EgovPasswordLoad.class);
 /** ApplicationContext */
 ApplicationContext context;
 /** crypto_config_xml문서 경로 */
 String default_path="classpath*:spring/context-config.xml";
 /** crytoConfig 관리 Class */
 CryptoConfig cryptoConfig;
 /**
  * EgovPasswordLoad Class 생성자
  */
 public EgovPasswordLoad()
 {
 	passwordEncryptor = new ConfigurablePasswordEncryptor();
 	context = new FileSystemXmlApplicationContext(default_path);
 	cryptoConfig = (CryptoConfig)context.getBean("config");
 	String pwdAlgorithm = cryptoConfig.getPasswordAlgorithm();
 	if(pwdAlgorithm.trim().length() <=0)
 	  passwordEncryptor.setAlgorithm("SHA-1");  
 	else
 	  passwordEncryptor.setAlgorithm(pwdAlgorithm);   	
 	passwordEncryptor.setPlainDigest(true);
 }
 /**
  * 입력된 패스워드 암호화
  * @param str - 암호화 할 패스워드
  * @return - 암호화된 문자열
  */
 public String encrypt(String str) {
		// TODO Auto-generated method stub
		 String rtn_str = passwordEncryptor.encryptPassword(str);
	  return rtn_str;
	}
 /**
  * 암복화된 패스워드와 비교
  * @param planPD - 평문 패스워드
  * @param cryptoPD - 암호화된 패스워드
  * @return true/false
  */
 public boolean checkPassword(String planPD,String cryptoPD)
 {
   return passwordEncryptor.checkPassword(planPD, cryptoPD);
 } 
 
 /**
  * 로그
  * @param str - 로그 문자열
  */
 public void debug(String str)
 {
	 logger.debug(str);
 }
 
 public static void main(String[] args)
 {
	 EgovPasswordLoad epl = new EgovPasswordLoad();
	 String str = epl.encrypt("Egov");
	 epl.debug(str);
	 
	 if(epl.checkPassword("Egov",str))
		 epl.debug("일치합니다.");
	 else
		 epl.debug("일치하지 안습니다.");
 }
 
}

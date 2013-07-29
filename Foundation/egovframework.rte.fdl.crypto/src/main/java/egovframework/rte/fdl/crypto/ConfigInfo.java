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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;



/**
 * Password 와 Algorithm 추출하는 Class
 * @author 개발프레임웍크 실행환경 개발팀 김종호
 * @since 2009. 03.10
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자              수정내용
 *  ---------   ---------   -------------------------------
 *  2009.03.10    김종호        최초생성
 * 
 * </pre>
 */

public class ConfigInfo 
{
 private static Logger logger  = CryptoLog.getLogger(ConfigInfo.class);	
 /** 파일경로 */
 String fileName = null;
 /** ApplicationContext */
 ApplicationContext context;
 /** crytoConfig 관리 Class */
 CryptoConfig cryptoConfig;
 /** crypto config xml문서 경로 */
 String default_path="classpath*:spring/context-config.xml";
 
 /**
  * ConfigInfo 생성자
  * @param fileName  암복화 설정정보 파일 경로
  */
 public ConfigInfo(String fileName) 
 {
	 setUp(fileName);
 }
 
 /**
  * Context Component 추출 
  * @param fileName - 암복화 설정정보 파일 경로
  */
 public void setUp(String fileName){
	 if(fileName == null) 
		 context = new FileSystemXmlApplicationContext(default_path);
		else
		 context = new FileSystemXmlApplicationContext(fileName); 	
		
	     cryptoConfig = (CryptoConfig)context.getBean("config");
 }
 /**
  * Password 추출
  * @return Password
  */
 public String getPassword()
 {
	 return  cryptoConfig.getPassword();
 }
 /**
  * Algorithm 추출
  * @return Algorithm
  */
 public String getAlgorithm()
 {
	 return cryptoConfig.getAlgorithm();
 }
 
 public static void main(String[] args)
 {
	 ConfigInfo cinfo = new ConfigInfo(null);
 }
}


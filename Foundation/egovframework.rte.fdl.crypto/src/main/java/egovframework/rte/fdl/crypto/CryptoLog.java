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

import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * 암호화 모듈을 위한 Log Class
 * @author 개발프레임웍크 실행환경 개발팀 김종호
 * @since  2009. 03.10
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일     수정자           수정내용
 *  -------    --------    ---------------------------
 * 2009.03.10   김종호             최초생성
 * 
 * </pre>
 */

public class CryptoLog {
    /**
     * Log4j XML 파일을 초기화 시 로딩
     * 
     * @param xmlFile - Log4j 설정 XML 문서
     */
    public static void init(String xmlFile) {
        String resource = xmlFile;
        DOMConfigurator.configure(xmlFile);
    }

    /**
     * 파라미터로 받은 Class에 로그 설정
     * 
     * @param c - 로그설정할 Class
     * @return log4j 객체
     */
    public static Logger getLogger(Class c) {
        return Logger.getLogger(c);
    }
}

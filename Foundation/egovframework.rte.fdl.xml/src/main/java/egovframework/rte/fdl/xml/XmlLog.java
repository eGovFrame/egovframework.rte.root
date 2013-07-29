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
package egovframework.rte.fdl.xml;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
/**
 * XML Log 관리 Class
 * @author 개발프레임웍크 실행환경 개발팀 김종호
 * @since 2009.03.18 
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자              수정내용
 *  ---------   ---------   -------------------------------
 * 2009.03.18    김종호        최초생성
 * 
 * </pre>
 */
public class XmlLog {
    /**
     * 초기화
     * 
     * @param xmlFile
     *            - log4j 설정 xml문서
     */
    public static void init(String xmlFile) {
//        String resource = xmlFile; // log4j 설정 xml문서
//        URL configFileResource = XmlLog.class.getResource(resource);
        DOMConfigurator.configure(xmlFile);
    }

    /**
     * 로그 객체 리턴
     * 
     * @param c
     *            - 로그객체 사용 Class
     * @return log4j 객체
     */
    public static Logger getLogger(Class c) {
        return Logger.getLogger(c);
    }
}

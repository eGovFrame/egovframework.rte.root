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
package egovframework.rte.fdl.security.securedobject;

import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.ConfigAttributeDefinition;

/**
 * 보호객체 관리를 지원하는 인터페이스
 * <p>
 * <b>NOTE:</b> Spring Security의 초기 데이터를 DB로 부터 조회하여
 * 보호된 자원 접근 권한을 지원, 제어 할 수 있도록 인터페이스를 제공한다.
 * @author ByungHun Woo
 * @since 2009.06.01
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.06.01  윤성종           최초 생성
 * 
 * </pre>
 */
public interface EgovSecuredObjectService {
    Log LOGGER = LogFactory.getLog(EgovSecuredObjectService.class);

    /**
     * 롤에 대한 URL의 매핑 정보를 얻어온다.
     * @return
     * @throws Exception
     */
    public LinkedHashMap getRolesAndUrl() throws Exception;

    /**
     * 롤에 대한 메소드의 매핑 정보를 얻어온다.
     * @return
     * @throws Exception
     */
    public LinkedHashMap getRolesAndMethod() throws Exception;

    /**
     * 롤에 대한 AOP pointcut 메핑 정보를 얻어온다.
     * @return
     * @throws Exception
     */
    public LinkedHashMap getRolesAndPointcut() throws Exception;

    /**
     * Best 매칭 정보를 얻어온다.
     * @param url
     * @return
     * @throws Exception
     */
    public ConfigAttributeDefinition getMatchedRequestMapping(String url)
            throws Exception;

    /**
     * 롤의 계층적 구조를 얻어온다.
     * @return
     * @throws Exception
     */
    public String getHierarchicalRoles() throws Exception;

}

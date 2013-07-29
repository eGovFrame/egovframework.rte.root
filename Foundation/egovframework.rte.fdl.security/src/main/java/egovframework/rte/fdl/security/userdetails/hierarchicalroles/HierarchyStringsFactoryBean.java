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
package egovframework.rte.fdl.security.userdetails.hierarchicalroles;

import org.springframework.beans.factory.FactoryBean;

import egovframework.rte.fdl.security.securedobject.EgovSecuredObjectService;

/**
 * DB기반의 롤 계층정보를 지원하는 비즈니스 구현 클래스
 * <p>
 * <b>NOTE:</b> DB 기반의 Role 계층 관계 정보를 얻어 이를 참조하는 Bean 의
 * 초기화 데이터로 제공한다.
 * @author marcos.sousa - reference
 *         http://forum.springframework
 *         .org/showthread.php
 *         ?t=56615&highlight=database&page=2
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
public class HierarchyStringsFactoryBean implements FactoryBean {

    private EgovSecuredObjectService securedObjectService;

    public void setSecuredObjectService(
            EgovSecuredObjectService securedObjectService) {
        this.securedObjectService = securedObjectService;
    }

    private String hierarchyStrings;

    public void init() throws Exception {
        hierarchyStrings = (String) securedObjectService.getHierarchicalRoles();
    }

    public Object getObject() throws Exception {
        if (hierarchyStrings == null) {
            init();
        }
        return hierarchyStrings;
    }

    public Class getObjectType() {
        return String.class;
    }

    public boolean isSingleton() {
        return true;
    }

}

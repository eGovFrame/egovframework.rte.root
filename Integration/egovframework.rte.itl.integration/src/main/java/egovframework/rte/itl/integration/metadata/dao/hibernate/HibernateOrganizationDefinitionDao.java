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
package egovframework.rte.itl.integration.metadata.dao.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import egovframework.rte.itl.integration.metadata.OrganizationDefinition;
import egovframework.rte.itl.integration.metadata.dao.OrganizationDefinitionDao;

/**
 * 연계 서비스 OrganizationDefinitionDao 구현 클래스
 * <p>
 * <b>NOTE:</b> 전자정부 연계 서비스 OrganizationDefinitionDao
 * interface 를 Hibernate를 이용하여 구현한 DAO class이다.
 * @author 실행환경 개발팀 심상호
 * @since 2009.06.01
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.06.01  심상호           최초 생성
 * 
 * </pre>
 */
public class HibernateOrganizationDefinitionDao extends HibernateDaoSupport
        implements OrganizationDefinitionDao {
    private Log LOG = LogFactory.getLog(this.getClass());

    public OrganizationDefinition getOrganizationDefinition(String id) {
        LOG.debug("get OrganizationDefinition(id = \"" + id + "\")");

        OrganizationDefinition organizationDefinition =
            (OrganizationDefinition) getHibernateTemplate().get(
                OrganizationDefinition.class, id);

        LOG.debug("get OrganizationDefinition(id = \"" + id + "\") = "
            + organizationDefinition);

        return organizationDefinition;
    }

}

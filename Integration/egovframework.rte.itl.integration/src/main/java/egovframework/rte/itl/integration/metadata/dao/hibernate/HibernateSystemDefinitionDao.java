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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import egovframework.rte.itl.integration.metadata.SystemDefinition;
import egovframework.rte.itl.integration.metadata.dao.SystemDefinitionDao;

/**
 * 연계 서비스 SystemDefinitionDao 구현 클래스
 * <p>
 * <b>NOTE:</b> 전자정부 연계 서비스 SystemDefinitionDao
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
public class HibernateSystemDefinitionDao extends HibernateDaoSupport implements
        SystemDefinitionDao {
    private Log LOG = LogFactory.getLog(this.getClass());

    public SystemDefinition getSystemDefinition(String key) {
        LOG.debug("get SystemDefinition(key = \"" + key + "\")");

        SystemDefinition systemDefinition =
            (SystemDefinition) getHibernateTemplate().get(
                SystemDefinition.class, key);

        LOG.debug("get SystemDefinition(key = \"" + key + "\") = "
            + systemDefinition);

        return systemDefinition;
    }

    @SuppressWarnings("unchecked")
    public SystemDefinition getSystemDefinition(String organizationId,
            String systemId) {
        LOG.debug("get SystemDefintion(organizationId = \"" + organizationId
            + "\", systemId = \"" + systemId + "\")");

        SystemDefinition systemDefinition = null;

        List<SystemDefinition> result =
            getHibernateTemplate()
                .find(
                    "from SystemDefinition as system "
                        + "where system.organization.id = ? "
                        + "and system.id = ?",
                    new Object[] {organizationId, systemId });
        if (result != null && result.size() > 0) {
            systemDefinition = result.get(0);
            if (result.size() > 1) {
                LOG.debug("get SystemDefintion(organizationId = \""
                    + organizationId + "\", systemId = \"" + systemId
                    + "\")'s size is not 1 (" + result.size() + ")");
            }
        }

        LOG.debug("get SystemDefintion(organizationId = \"" + organizationId
            + "\", systemId = \"" + systemId + "\") = " + systemDefinition);

        return systemDefinition;
    }
}

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
package egovframework.rte.fdl.security.securedobject.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.SecurityConfig;
import org.springframework.security.intercept.web.RequestKey;

import egovframework.rte.fdl.security.securedobject.EgovSecuredObjectService;

/**
 * DB기반의 보호된 자원 관리를 구현한 DAO 클래스
 * <p>
 * <b>NOTE:</b> DB 기반의 Secured Object 정보를 제공하기 위한 DAO 로
 * default 쿼리를 제공하며 사용자 DB 에 맞는 각 유형의 DB 쿼리는 재설정 가능하다.
 * namedParameterJdbcTemplate 를 사용하여 DB 조회를 처리한다.
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
public class SecuredObjectDAO {

    /**
     * url 형식인 보호자원 - Role 맵핑정보를 조회하는 default 쿼리이다.
     */
    public static final String DEF_ROLES_AND_URL_QUERY =
        "SELECT a.resource_pattern url, b.authority authority "
            + "FROM SECURED_RESOURCES a, SECURED_RESOURCES_ROLE b "
            + "WHERE a.resource_id = b.resource_id "
            + "AND a.resource_type = 'url' ORDER BY a.sort_order ";

    /**
     * method 형식인 보호자원 - Role 맵핑정보를 조회하는 default 쿼리이다.
     */
    public static final String DEF_ROLES_AND_METHOD_QUERY =
        "SELECT a.resource_pattern method, b.authority authority "
            + "FROM SECURED_RESOURCES a, SECURED_RESOURCES_ROLE b "
            + "WHERE a.resource_id = b.resource_id "
            + "AND a.resource_type = 'method' ORDER BY a.sort_order ";

    /**
     * pointcut 형식인 보호자원 - Role 맵핑정보를 조회하는 default
     * 쿼리이다.
     */
    public static final String DEF_ROLES_AND_POINTCUT_QUERY =
        "SELECT a.resource_pattern pointcut, b.authority authority "
            + "FROM SECURED_RESOURCES a, SECURED_RESOURCES_ROLE b "
            + "WHERE a.resource_id = b.resource_id "
            + "AND a.resource_type = 'pointcut' ORDER BY a.sort_order ";

    /**
     * 매 request 마다 best matching url 보호자원 - Role 맵핑정보를
     * 얻기위한 default 쿼리이다. (Oracle 10g specific)
     */
    public static final String DEF_REGEX_MATCHED_REQUEST_MAPPING_QUERY_ORACLE10G =
        "SELECT a.resource_pattern uri, b.authority authority "
            + "FROM secured_resources a, secured_resources_role b "
            + "WHERE a.resource_id = b.resource_id "
            + "AND a.resource_id =  "
            + " ( SELECT resource_id FROM "
            + "    ( SELECT resource_id, ROW_NUMBER() OVER (ORDER BY sort_order) resource_order FROM secured_resources c "
            + "      WHERE REGEXP_LIKE ( :url, c.resource_pattern ) "
            + "      AND c.resource_type = 'url' "
            + "      ORDER BY c.sort_order ) "
            + "   WHERE resource_order = 1 ) ";

    /**
     * Role 의 계층(Hierarchy) 관계를 조회하는 default 쿼리이다.
     */
    public static final String DEF_HIERARCHICAL_ROLES_QUERY =
        "SELECT a.child_role child, a.parent_role parent "
            + "FROM ROLES_HIERARCHY a LEFT JOIN ROLES_HIERARCHY b on (a.child_role = b.parent_role) ";

    private String sqlRolesAndUrl;

    private String sqlRolesAndMethod;

    private String sqlRolesAndPointcut;

    private String sqlRegexMatchedRequestMapping;

    private String sqlHierarchicalRoles;

    public SecuredObjectDAO() {
        this.sqlRolesAndUrl = DEF_ROLES_AND_URL_QUERY;
        this.sqlRolesAndMethod = DEF_ROLES_AND_METHOD_QUERY;
        this.sqlRolesAndPointcut = DEF_ROLES_AND_POINTCUT_QUERY;
        this.sqlRegexMatchedRequestMapping =
            DEF_REGEX_MATCHED_REQUEST_MAPPING_QUERY_ORACLE10G;
        this.sqlHierarchicalRoles = DEF_HIERARCHICAL_ROLES_QUERY;
    }

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate =
            new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * 롤에 대한 URL 정보를 가져오는 SQL을 얻어온다.
     * @return
     */
    public String getSqlRolesAndUrl() {
        return sqlRolesAndUrl;
    }

    /**
     * 롤에대한 URL 정보를 가져오는 SQL을 설정한다.
     * @param sqlRolesAndUrl
     */
    public void setSqlRolesAndUrl(String sqlRolesAndUrl) {
        this.sqlRolesAndUrl = sqlRolesAndUrl;
    }

    public String getSqlRolesAndMethod() {
        return sqlRolesAndMethod;
    }

    public void setSqlRolesAndMethod(String sqlRolesAndMethod) {
        this.sqlRolesAndMethod = sqlRolesAndMethod;
    }

    public String getSqlRolesAndPointcut() {
        return sqlRolesAndPointcut;
    }

    public void setSqlRolesAndPointcut(String sqlRolesAndPointcut) {
        this.sqlRolesAndPointcut = sqlRolesAndPointcut;
    }

    public String getSqlRegexMatchedRequestMapping() {
        return sqlRegexMatchedRequestMapping;
    }

    public void setSqlRegexMatchedRequestMapping(
            String sqlRegexMatchedRequestMapping) {
        this.sqlRegexMatchedRequestMapping = sqlRegexMatchedRequestMapping;
    }

    public String getSqlHierarchicalRoles() {
        return sqlHierarchicalRoles;
    }

    public void setSqlHierarchicalRoles(String sqlHierarchicalRoles) {
        this.sqlHierarchicalRoles = sqlHierarchicalRoles;
    }

    public LinkedHashMap getRolesAndResources(String resourceType)
            throws Exception {

        LinkedHashMap resourcesMap = new LinkedHashMap();

        String sqlRolesAndResources;
        boolean isResourcesUrl = true;
        if ("method".equals(resourceType)) {
            sqlRolesAndResources = getSqlRolesAndMethod();
            isResourcesUrl = false;
        } else if ("pointcut".equals(resourceType)) {
            sqlRolesAndResources = getSqlRolesAndPointcut();
            isResourcesUrl = false;
        } else {
            sqlRolesAndResources = getSqlRolesAndUrl();
        }

        List resultList =
            this.namedParameterJdbcTemplate.queryForList(sqlRolesAndResources,
                new HashMap());

        Iterator itr = resultList.iterator();
        Map tempMap;
        String preResource = null;
        String presentResourceStr;
        Object presentResource;
        while (itr.hasNext()) {
            tempMap = (Map) itr.next();

            presentResourceStr = (String) tempMap.get(resourceType);
            // url 인 경우 RequestKey 형식의 key를 Map에 담아야 함
            presentResource =
                isResourcesUrl
                    ? new RequestKey(presentResourceStr)
                    : (Object) presentResourceStr;
            List configList = new LinkedList();

            // 이미 requestMap 에 해당 Resource 에 대한 Role 이
            // 하나 이상 맵핑되어 있었던 경우, sort_order 는
            // resource(Resource) 에 대해 매겨지므로 같은
            // Resource 에 대한 Role 맵핑은 연속으로 조회됨.
            // 해당 맵핑 Role List (SecurityConfig) 의 데이터를
            // 재활용하여 새롭게 데이터 구축
            if (preResource != null && presentResourceStr.equals(preResource)) {
                List preAuthList =
                    (List) ((ConfigAttributeDefinition) resourcesMap
                        .get(presentResource)).getConfigAttributes();
                Iterator preAuthItr = preAuthList.iterator();
                while (preAuthItr.hasNext()) {
                    SecurityConfig tempConfig =
                        (SecurityConfig) preAuthItr.next();
                    configList.add(tempConfig);
                }
            }

            configList
                .add(new SecurityConfig((String) tempMap.get("authority")));
            ConfigAttributeDefinition cad =
                new ConfigAttributeDefinition(configList);

            // 만약 동일한 Resource 에 대해 한개 이상의 Role 맵핑 추가인
            // 경우 이전 resourceKey 에 현재 새로 계산된 Role 맵핑
            // 리스트로 덮어쓰게 됨.
            resourcesMap.put(presentResource, cad);

            // 이전 resource 비교위해 저장
            preResource = presentResourceStr;
        }

        return resourcesMap;
    }

    public LinkedHashMap getRolesAndUrl() throws Exception {
        return getRolesAndResources("url");
    }

    public LinkedHashMap getRolesAndMethod() throws Exception {
        return getRolesAndResources("method");
    }

    public LinkedHashMap getRolesAndPointcut() throws Exception {
        return getRolesAndResources("pointcut");
    }

    public ConfigAttributeDefinition getRegexMatchedRequestMapping(String url)
            throws Exception {

        ConfigAttributeDefinition attributes = null;

        // best regex matching - best 매칭된 Uri 에 따른 Role
        // List 조회, DB 차원의 정규식 지원이 있는 경우 사용 (ex. hsqldb
        // custom function, Oracle 10g regexp_like 등)
        Map paramMap = new HashMap();
        paramMap.put("url", url);
        List resultList =
            this.namedParameterJdbcTemplate.queryForList(
                getSqlRegexMatchedRequestMapping(), paramMap);

        Iterator itr = resultList.iterator();
        Map tempMap;
        List configList = new LinkedList();
        // 같은 Uri 에 대한 Role 맵핑이므로 무조건 configList 에 add
        // 함
        while (itr.hasNext()) {
            tempMap = (Map) itr.next();
            configList
                .add(new SecurityConfig((String) tempMap.get("authority")));
        }

        if (configList.size() > 0) {
            attributes = new ConfigAttributeDefinition(configList);
            EgovSecuredObjectService.LOGGER.debug("Request Uri : " + url
                + ", matched Uri : " + ((Map) resultList.get(0)).get("uri")
                + ", mapping Roles : " + attributes);
        }

        return attributes;
    }

    public String getHierarchicalRoles() throws Exception {

        List resultList =
            this.namedParameterJdbcTemplate.queryForList(
                getSqlHierarchicalRoles(), new HashMap());

        Iterator itr = resultList.iterator();
        StringBuffer concatedRoles = new StringBuffer();
        Map tempMap;
        while (itr.hasNext()) {
            tempMap = (Map) itr.next();
            concatedRoles.append(tempMap.get("child"));
            concatedRoles.append(" > ");
            concatedRoles.append(tempMap.get("parent"));
            concatedRoles.append("\n");
        }

        return concatedRoles.toString();
    }

}

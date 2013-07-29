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
package egovframework.rte.fdl.security.userdetails.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

/**
 * 사용자 계정 정보를 DB에서 관리할 수 있도록 구현한 클래스
 * <p>
 * <b>NOTE:</b>
 * org.springframework.jdbc.object.MappingSqlQuery 를
 * 확장하여 사용자 계정 정보를 DB에서 관리할 수 있도록 구현한 클래스이다.
 * @author 실행환경 개발팀 윤성종
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
public abstract class EgovUsersByUsernameMapping extends MappingSqlQuery {

    /**
     * 사용자정보를 테이블에서 조회하여 사용자객체에 매핑한다.
     * @param ds
     * @param usersByUsernameQuery
     */
    public EgovUsersByUsernameMapping(DataSource ds, String usersByUsernameQuery) {
        super(ds, usersByUsernameQuery);
        declareParameter(new SqlParameter(Types.VARCHAR));
        compile();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.jdbc.object.MappingSqlQuery
     * #mapRow(java.sql.ResultSet, int)
     */
    /**
     * mapRow 추상클래스 jdbc-user-service 에서 지정된
     * users-by-username-query 의 쿼리문을 조회하여 ResultSet에
     * 매핑된다. 사용 예) protected Object mapRow(ResultSet
     * rs, int rownum) throws SQLException; { String
     * userid = rs.getString(0); String password =
     * rs.getString(1); boolean enabled =
     * rs.getBoolean(2); EgovUserVO userVO = new
     * EgovUserVO(); userVO.setUserId(userid);
     * userVO.setPassWord(password); return new
     * EgovUser(userid, password, enabled, userVO); }
     */
    @Override
    protected abstract Object mapRow(ResultSet rs, int rownum)
            throws SQLException;
}

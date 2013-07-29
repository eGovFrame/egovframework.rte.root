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
package egovframework.rte.fdl.idgnr.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import egovframework.rte.fdl.cmmn.exception.FdlException;

/**
 * ID Generation 서비스를 위한 Sequence 구현 클래스
 * <p>
 * <b>NOTE</b>: DBMS 에서 Sequence 를 제공하는 경우, Sequence
 * 기반의 유일키를 제공 받을 수 있다.
 * @author 실행환경 개발팀 김태호
 * @since 2009.02.01
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.02.01  김태호          최초 생성
 * 
 * </pre>
 */
public class EgovSequenceIdGnrService extends AbstractDataIdGnrService {

    /**
     * BigDecimal 유형의 ID 제공
     * @return the next id as a BigDecimal.
     * @throws FdlException
     *         여타이유에 의해 아이디 생성이 불가능 할때
     */
    protected BigDecimal getNextBigDecimalIdInner() throws FdlException {
        if (getLogger().isDebugEnabled())
            getLogger().debug(
                messageSource.getMessage("debug.idgnr.sequenceid.query",
                    new String[] {query }, Locale.getDefault()));

        try {
            Connection conn = getConnection();
            try {
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                } else {
                    if (getLogger().isErrorEnabled())
                        getLogger().error(
                            messageSource.getMessage(
                                "error.idgnr.sequenceid.notallocate.id",
                                new String[] {}, Locale.getDefault()));
                    throw new FdlException(messageSource,
                        "error.idgnr.sequenceid.notallocate.id");
                }
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            if (getLogger().isErrorEnabled())
                getLogger().error(
                    messageSource.getMessage("error.idgnr.get.connection",
                        new String[] {}, Locale.getDefault()));
            throw new FdlException(messageSource, "error.idgnr.get.connection",
                e);
        }
    }

    /**
     * long 유형의 ID 제공
     * @return the next id as a long.
     * @throws FdlException
     *         여타이유에 의해 아이디 생성이 불가능 할때
     */
    protected long getNextLongIdInner() throws FdlException {
        if (getLogger().isDebugEnabled())
            getLogger().debug(
                messageSource.getMessage("debug.idgnr.sequenceid.query",
                    new String[] {query }, Locale.getDefault()));

        try {
            Connection conn = getConnection();
            try {
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    if (getLogger().isErrorEnabled())
                        getLogger().error(
                            messageSource.getMessage(
                                "error.idgnr.sequenceid.notallocate.id",
                                new String[] {}, Locale.getDefault()));
                    throw new FdlException(messageSource,
                        "error.idgnr.sequenceid.notallocate.id");
                }
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            if (getLogger().isErrorEnabled())
                getLogger().error(
                    messageSource.getMessage("error.idgnr.get.connection",
                        new String[] {}, Locale.getDefault()));
            throw new FdlException(messageSource, "error.idgnr.get.connection",
                e);
        }
    }

}

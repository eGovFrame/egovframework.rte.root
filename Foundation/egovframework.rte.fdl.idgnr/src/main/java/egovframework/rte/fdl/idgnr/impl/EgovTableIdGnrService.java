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
import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import egovframework.rte.fdl.cmmn.exception.FdlException;

/**
 * ID Generation 서비스를 위한 Table 구현 클래스
 * <p>
 * <b>NOTE</b>: 채번 테이블을 정의하고, 각 관리대상에 대한 현재 최종 Max 번호를
 * 관리하여 Table 기반의 유일키를 제공 받을 수 있다.
 * 
 * <pre>
 *       필요한 테이블 생성 스크립트
 *   CREATE TABLE ids (
 *       table_name varchar(16) NOT NULL,
 *       next_id INTEGER NOT NULL,
 *       PRIMARY KEY (table_name)
 *   );
 * </pre>
 * @author 실행환경 개발팀 김태호
 * @since 2009.02.01
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.02.01	김태호	최초 생성
 *   2013.03.25	한성곤	필드명 속성 처리, JdbcTemplate 방식으로 변경, 초기 id 값 등록(자동 insert 처리), 반복처리 제외
 * 
 * </pre>
 */
public class EgovTableIdGnrService extends AbstractDataBlockIdGnrService {

    /**
     * ID생성을 위한 테이블 정보 디폴트는 ids임.
     */
	private String table = "ids";

    /**
     * 테이블 정보에 기록되는 대상 키정보 대개의 경우는 아이디로 생성되는 테이블명을 기재함
     */
    private String tableName = "id";
    
    /**
     * 테이블명(구분값)에 대한 테이블 필드명 지정
     */
    private String tableNameFieldName = "table_name";
    
    /**
     * Next Id 정보를 보관하는 필드명 지정
     */
    private String nextIdFieldName = "next_id";
    
    /**
     * Jdbc template
     */
    private JdbcTemplate jdbcTemplate;

    /**
     * 생성자
     */
    public EgovTableIdGnrService() {
    }
    
    @Override
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    /**
     * tableName에 대한 초기 값이 없는 경우 초기 id 값 등록 (blockSize 처리)
     * 
     * @param useBigDecimals
     * @param initId
     */
    private Object insertInitId(boolean useBigDecimals, int blockSize) {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                messageSource.getMessage("debug.idgnr.init.idblock",
                    new Object[] { tableName }, Locale.getDefault()));
        }
        
        Object initId = null;
        
    	String insertQuery = "INSERT INTO " + table + "(" + tableNameFieldName + ", " + nextIdFieldName + ") " + "values('" + tableName + "', ?)";
    	
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Insert Query : " + insertQuery);
        }
        
    	if (useBigDecimals) {
    		initId = new BigDecimal(blockSize);
    		
    	} else {
    		initId = new Long(blockSize);
    	}
    	
    	jdbcTemplate.update(insertQuery, initId);
    	
    	return initId;
    }

    /**
     * blockSize 대로 ID 지정
     * 
     * @param blockSize 지정되는 blockSize
     * @param useBigDecimals BigDecimal 사용 여부
     * @return BigDecimal을 사용하면 BigDecimal 아니면 long 리턴
     * @throws FdlException ID생성을 위한 블럭 할당이 불가능할때
     */
	private Object allocateIdBlock(int blockSize, boolean useBigDecimals) throws FdlException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                messageSource.getMessage("debug.idgnr.allocate.idblock",
                    new Object[] { new Integer(blockSize), tableName }, Locale.getDefault()));
        }

        Object nextId;
        Object newNextId;
        
        try {
        
	        String selectQuery = "SELECT " + nextIdFieldName + " FROM " + table + " WHERE " + tableNameFieldName + " = ?";
	        
	        if (getLogger().isDebugEnabled()) {
                getLogger().debug("Select Query : " + selectQuery);
            }
	        
			if (useBigDecimals) {
				try {
					nextId = jdbcTemplate.queryForObject(selectQuery, new Object[] { tableName }, BigDecimal.class);
				} catch (EmptyResultDataAccessException erdae) {
					nextId = null;
				}
				
				if (nextId == null) {	// no row
					insertInitId(useBigDecimals, blockSize);
					
					return new BigDecimal(0);
				}
			} else {
				try {
					nextId = jdbcTemplate.queryForLong(selectQuery, tableName);
				} catch (EmptyResultDataAccessException erdae) {
					nextId = -1L;
				}
				
				if ((Long)nextId == -1L) {	// no row
					insertInitId(useBigDecimals, blockSize);
					
					return new Long(0);
				}
			}
        } catch (DataAccessException dae) {
        	dae.printStackTrace();
        	 if (getLogger().isDebugEnabled()) {
                 getLogger().debug("", dae);
             }
        	throw new FdlException(messageSource, "error.idgnr.select.idblock", new String[] { tableName }, null);
        }
		
        try {
			String updateQuery = "UPDATE " + table + " SET " + nextIdFieldName + " = ?" + " WHERE " + tableNameFieldName + " = ?";
	        
	        if (getLogger().isDebugEnabled()) {
	            getLogger().debug("Update Query : " + updateQuery);
	        }
	        
			if (useBigDecimals) {
				newNextId = ((BigDecimal) nextId).add(new BigDecimal(blockSize));
	    		
	    	} else {
	    		newNextId = new Long(((Long) nextId).longValue() + blockSize);
	    	}
	    	
	    	jdbcTemplate.update(updateQuery, newNextId, tableName);
	        
	    	return nextId;
        } catch (DataAccessException dae) {
        	throw new FdlException(messageSource, "error.idgnr.update.idblockk", new String[] { tableName }, null);
        }
       
    }

    /**
     * blockSize 대로 ID 지정(BigDecimal)
     * 
     * @param blockSize 지정되는 blockSize
     * @return 할당된 블럭의 첫번째 아이디
     * @throws FdlException ID생성을 위한 블럭 할당이 불가능할때
     */
    protected BigDecimal allocateBigDecimalIdBlock(int blockSize)
            throws FdlException {
        return (BigDecimal) allocateIdBlock(blockSize, true);
    }

    /**
     * blockSize 대로 ID 지정(long)
     * 
     * @param blockSize 지정되는 blockSize
     * @return 할당된 블럭의 첫번째 아이디
     * @throws FdlException ID생성을 위한 블럭 할당이 불가능할때
     */
    protected long allocateLongIdBlock(int blockSize) throws FdlException {
        Long id = (Long) allocateIdBlock(blockSize, false);

        return id.longValue();
    }

    /**
     * ID생성을 위한 테이블 정보 Injection
     * 
     * @param table config로 지정되는 정보
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * ID 생성을 위한 테이블의 키정보 ( 대개의경우는 대상 테이블명을 기재함 )
     * 
     * @param tableName config로 지정되는 정보
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    /**
     *  테이블명(구분값)에 대한 테이블 필드명 정보 지정
     * 
     * @param tableNameFieldName
     */
    public void setTableNameFieldName(String tableNameFieldName) {
    	this.tableNameFieldName = tableNameFieldName;
    }
    
    /**
     * Next Id 정보를 보관하는 필드명 정보 지정
     * 
     * @param nextIdFieldName
     */
    public void setNextIdFieldName(String nextIdFieldName) {
    	this.nextIdFieldName = nextIdFieldName;
    }
}

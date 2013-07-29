/*
 * Copyright 2006-2007 the original author or authors. 
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

package egovframework.rte.bat.core.item.database;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.util.Assert;

import egovframework.rte.bat.core.item.database.support.EgovItemPreparedStatementSetter;
import egovframework.rte.bat.core.reflection.EgovReflectionSupport;

/**
 * EgovJdbcBatchItemWriter EgovItemPreparedStatementSetter인터페이스를 상속받은
 * ItemPreparedStatementSetter이 설정되어 있어야 함.
 * 
 * @author 배치실행개발팀
 * @since 2012.07.20
 * @version 1.0
 * @see <pre>
 * 
 *      개정이력(Modification Information)
 *   
 *   수정일              수정자                수정내용
 *  ---------   -----------   ---------------------------
 *  2012.07.20  배치실행개발팀     최초 생성
 * </pre>
 */
public class EgovJdbcBatchItemWriter<T> implements ItemWriter<T>,
		InitializingBean {

	protected static final Log logger = LogFactory
			.getLog(EgovJdbcBatchItemWriter.class);

	// SimpleJdbcOperations
	private SimpleJdbcOperations simpleJdbcTemplate;

	// EgovItemPreparedStatementSetter
	private EgovItemPreparedStatementSetter<T> itemPreparedStatementSetter;

	// sql 쿼리
	private String sql;

	// params 초기화
	private String[] params = new String[0];

	// assertUpdates 초기화
	private boolean assertUpdates = true;

	// usingParameters
	private boolean usingParameters;
	private EgovReflectionSupport<T> reflector;

	/**
	 * AssertUpdates 설정 셋팅
	 * 
	 * @param assertUpdates
	 */
	public void setAssertUpdates(boolean assertUpdates) {
		this.assertUpdates = assertUpdates;
	}

	/**
	 * sql 셋팅
	 * 
	 * @param sql
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * ItemPreparedStatementSetter 셋팅
	 * 
	 * @param preparedStatementSetter
	 */
	public void setItemPreparedStatementSetter(
			EgovItemPreparedStatementSetter<T> preparedStatementSetter) {
		this.itemPreparedStatementSetter = preparedStatementSetter;
	}

	/**
	 * params 셋팅
	 * 
	 * @param params
	 */
	public void setParams(String[] params) {
		this.params = params == null ? null : Arrays.asList(params).toArray(
				new String[params.length]);
	}

	/**
	 * dataSource 셋팅
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		if (simpleJdbcTemplate == null) {
			this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
		}
	}

	/**
	 * SimpleJdbcTemplate 셋팅
	 * 
	 * @param simpleJdbcTemplate
	 */
	public void setSimpleJdbcTemplate(SimpleJdbcOperations simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
	}

	/**
	 * 설정의 properties 셋팅확인
	 */
	public void afterPropertiesSet() {
		Assert.notNull(simpleJdbcTemplate,
				"A DataSource or a SimpleJdbcTemplate is required.");
		Assert.notNull(sql, "An SQL statement is required.");

		if (params.length != 0) {
			usingParameters = true;
		}

		Assert.notNull(
				itemPreparedStatementSetter,
				"Using SQL statement with '?' placeholders requires an EgovMethodMapItemPreparedStatementSetter");
		reflector = new EgovReflectionSupport<T>();

	}

	/**
	 * DB Write 를 위해 적절한 setValues 호출
	 * setValues(item, ps, params, sqlTypes, methodMap) : 
	 * setValues(item, ps) : 따로 VO
	 */
	@SuppressWarnings("unchecked")
	public void write(final List<? extends T> items) throws Exception {

		if (!items.isEmpty()) {

			if (logger.isDebugEnabled()) {
				logger.debug("Executing batch with " + items.size() + " items.");
			}

			int[] updateCounts = null;

			updateCounts = (int[]) simpleJdbcTemplate.getJdbcOperations()
					.execute(sql, new PreparedStatementCallback() {
						public Object doInPreparedStatement(PreparedStatement ps)
								throws SQLException, DataAccessException {
							// Parameters 가 있으면 item, ps, params, sqlTypes,methodMap 를 파라메터로 받는 setValues call
							// 없으면 item, ps 를 파라메터로 받는 setValues call
							if (usingParameters) {

								String[] sqlTypes = reflector.getSqlTypeArray(
										params, items.get(0));
								try {
									reflector.generateGetterMethodMap(params,
											items.get(0));
								} catch (Exception e) {
									// generateGetterMethodMap 실패에 대한 에러내용 출력
									logger.error(e);
								}
								Map<String, Method> methodMap = reflector
										.getMethodMap();

								for (T item : items) {

									itemPreparedStatementSetter.setValues(item,ps, params, sqlTypes, methodMap);
									ps.addBatch();
								}
							} else {
								for (T item : items) {
									itemPreparedStatementSetter.setValues(item,
											ps);
									ps.addBatch();
								}
							}
							return ps.executeBatch();

						}
					});

			if (assertUpdates) {

				for (int i = 0; i < updateCounts.length; i++) {
					int value = updateCounts[i];
					if (value == 0) {
						throw new EmptyResultDataAccessException("Item " + i
								+ " of " + updateCounts.length
								+ " did not update any rows: [" + items.get(i)
								+ "]", 1);
					}
				}

			}
		}

	}
}

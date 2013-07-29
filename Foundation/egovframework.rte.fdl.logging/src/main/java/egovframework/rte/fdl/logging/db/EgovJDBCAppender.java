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
package egovframework.rte.fdl.logging.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.log4j.MDC;
import org.apache.log4j.db.DBHelper;
import org.apache.log4j.jdbc.JDBCAppender;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.springframework.stereotype.Component;

import egovframework.rte.fdl.logging.db.SingletonDataSourceProvider;

/**
 * log4j 의 JDBCAppender 에 대해 Spring DataSource 사용 가능토록 확장한 클래스
 * <p>
 * <b>NOTE</b>: log4j의 JDBCAppender 을 extends 하고 있으며 JDBCAppender 는 log4j 의 접속
 * 설정을 따라 매번 Connection 을 직접 생성하게 되나, EgovJDBCAppender 는 Spring 의 dataSource 를
 * Annotation 형식으로 injection 하여 사용할 수 있게 확장한 Appender 이다.
 * 
 * @author 실행환경 개발팀 우병훈
 * @since 2009.03.09
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.03.09  우병훈          최초 생성
 * 
 * </pre>
 */
@SuppressWarnings("deprecation")
@Component("egovJDBCAppender")
public class EgovJDBCAppender extends JDBCAppender {

	/** 로그 실행 위치 정보 flag */
	boolean locationInfo = false;

	/** 싱글톤 dataSource provider - Spring 연동 dataSource 제공 */
	private final SingletonDataSourceProvider provider;

	/**
	 * '@Autowired' Annotation 형식으로 dataSource 를 받아와 이를
	 * SingletonDataSourceProvider 의 setDataSource 메서드를 호출하여 설정해 준다.
	 * 
	 * @param dataSource
	 *            - Spring 에서 설정한 dataSource
	 */
	// @Resource(name = "dataSource")
	//@Autowired(required = false)
	@Resource(name = "dataSource" )
	public void setDataSource(DataSource dataSource) {
		provider.setDataSource(dataSource);
	}

	/**
	 * EgovJDBCAppender 의 기본 생성자에서는 SingletonDataSourceProvider 싱글톤 객체를 설정한다.
	 * EgovJDBCAppender 는 log4j 에서 인스턴스화 되고, 또한 Spring Container 에서 bean 으로 등록되어
	 * Container 에 의해 위 dataSource injection 처리가 되는데 이 때 최초 한번만(log4j 기동 시)
	 * SingletonDataSourceProvider 의 인스턴스가 만들어지도록 처리함.
	 */
	public EgovJDBCAppender() {
		this.provider = SingletonDataSourceProvider.getInstance();
	}

	/**
	 * connection pool 에 connection 을 되돌려주기 위해 override 하였다.
	 * 
	 * @param con
	 *            - 현재 사용중인 connection
	 */
	@Override
	protected void closeConnection(Connection con) {
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 여기서는 Spring 연동을 가정하고 있으며 Spring 에서 설정한 dataSource를 통해 connection 을 얻기 위해
	 * override 하였다.
	 * 
	 * @return Spring 의 dataSource 기반의 connection
	 * @exception SQLException
	 */
	@Override
	protected Connection getConnection() throws SQLException {
		return provider.getDataSource().getConnection();
	}

	/**
	 * connection 을 얻고 로그 쿼리를 실행 후 관련 리소스를 해제하도록 override 하였다.
	 * 
	 * @param sql
	 *            - log4j.xml 에 설정된 <param name="sql" .. 의 쿼리
	 * @exception SQLException
	 */
	@Override
	protected void execute(String sql) throws SQLException {

		Connection con = null;
		Statement stmt = null;

		try {
			con = getConnection();
			// dataSource bean 에 기본으로 autoCommit false 가 되어 있어 여기서는 true 로 변경
			con.setAutoCommit(true);

			stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			stmt.close();
			closeConnection(con);
		}

		// LogFactory.getLog("sysoutLogger").debug("Execute: " + sql);
	}

	/**
	 * JDBCAppender 에서 로그 기록 시 해당 로깅 이벤트를 buffer에 추가하는 부분으로 여기서는
	 * log4j-1.3alpha-8 의 DB Appender 에서 기본 제공되는 logging_event 테이블에 대한
	 * 칼럼들(sequence_number, timestamp, rendered_message, logger_name,
	 * level_string, thread_name, reference_flag) 과 locationInfo 설정에 따른 추가
	 * 정보(caller_filename, caller_class, caller_method, caller_line)를 MDC 를 이용해
	 * 설정하였다.
	 * 
	 * @param event
	 *            - JDBCAppender 형식으로 기록하는 현재 LoggingEvent
	 */
	@Override
	public void append(LoggingEvent event) {
		MDC.put("sequence_number", event.getSequenceNumber());
		MDC.put("timestamp", event.getTimeStamp());
		MDC.put("rendered_message", event.getRenderedMessage());
		MDC.put("logger_name", event.getLoggerName());
		MDC.put("level_string", event.getLevel().toString());
		String ndc = event.getNDC();
		if (ndc != null) {
			MDC.put("ndc", ndc);
		}
		MDC.put("thread_name", event.getThreadName());
		MDC.put("reference_flag", DBHelper.computeReferenceMask(event));

		LocationInfo li;

		if (event.locationInformationExists() || locationInfo) {
			li = event.getLocationInformation();
		} else {
			li = LocationInfo.NA_LOCATION_INFO;
		}

		MDC.put("caller_filename", li.getFileName());
		MDC.put("caller_class", li.getClassName());
		MDC.put("caller_method", li.getMethodName());
		MDC.put("caller_line", li.getLineNumber());

		// TODO Auto-generated method stub
		super.append(event);
	}

	/**
	 * log4j.xml 에 <param name="locationInfo" value="true" /> locationInfo 설정 여부
	 * 
	 * @return boolean(true/false)
	 */
	public boolean isLocationInfo() {
		return locationInfo;
	}

	/**
	 * log4j.xml 에 locationInfo 설정에 대한 setter. locationInfo 설정이 없는 경우 default 는
	 * false 임.
	 * 
	 * @param locationInfo
	 *            - <param name="locationInfo" value="true" /> 와 같이 설정
	 */
	public void setLocationInfo(boolean locationInfo) {
		this.locationInfo = locationInfo;
	}

	/**
	 * SingletonDataSourceProvider 멤버 객체에 대한 getter - Singleton 검증을 위해 테스트 용도로
	 * 사용.
	 * 
	 * @return 현재 EgovJDBCAppender 에 멤버로 설정된 SingletonDataSourceProvider
	 */
	public SingletonDataSourceProvider getProvider() {
		return provider;
	}

}

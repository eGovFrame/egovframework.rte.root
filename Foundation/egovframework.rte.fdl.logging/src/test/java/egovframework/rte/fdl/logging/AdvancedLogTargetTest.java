package egovframework.rte.fdl.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import egovframework.rte.fdl.logging.util.LogFileUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath*:META-INF/spring/context-common.xml",
		"classpath*:META-INF/spring/context-datasource.xml",
		"classpath*:META-INF/spring/context-transaction.xml",
		"classpath*:META-INF/spring/context-jndi-jeus.xml" })
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = false)
@Transactional
public class AdvancedLogTargetTest {
	@Resource(name = "dataSource")
	DataSource dataSource;

	@Resource(name = "jdbcProperties")
	Properties jdbcProperties;

	boolean isHsql = true;
	boolean isOracle = false;
	boolean isMysql = false;

	// @SuppressWarnings("unchecked")
	// @AfterClass
	// public static void deleteAllLogFiles() throws InterruptedException {
	//
	// // 테스트 케이스가 종료할 때 생성한 모든 로그 파일을 삭제함.
	// // log4j 에서 로그 파일에 대한 lock 을 잡고 있기 때문에 모든 appender 의 close() 를 실행하여
	// resource 해제 필요
	// Enumeration<Logger> loggers = LogManager.getCurrentLoggers();
	// while (loggers.hasMoreElements()) {
	// Logger logger = loggers.nextElement();
	// Enumeration<Appender> appenders = logger.getAllAppenders();
	// while (appenders.hasMoreElements()) {
	// Appender appender = appenders.nextElement();
	// appender.close();
	// }
	// }
	//
	// // 모든 로그 파일 삭제
	// File logDir = new File("./logs");
	// for (File tempDir : logDir.listFiles()) {
	// for (File tempFile : tempDir.listFiles()) {
	// //assertTrue(tempFile.delete());
	// tempFile.delete();
	// }
	// }
	// }

	@Before
	public void onSetUp() throws Exception {

		isHsql = "hsql".equals(jdbcProperties.getProperty("usingDBMS"));
		isMysql = "mysql".equals(jdbcProperties.getProperty("usingDBMS"));
		isOracle = "oracle".equals(jdbcProperties.getProperty("usingDBMS"));

		if (isHsql) {
			SimpleJdbcTestUtils.executeSqlScript(new SimpleJdbcTemplate(
					dataSource), new ClassPathResource(
					"META-INF/testdata/dialect/hsqldb.sql"), true);
		} else if (isMysql) {
			SimpleJdbcTestUtils.executeSqlScript(new SimpleJdbcTemplate(
					dataSource), new ClassPathResource(
					"META-INF/testdata/dialect/mysql.sql"), true);
		} else if (isOracle) {
			SimpleJdbcTestUtils.executeSqlScript(new SimpleJdbcTemplate(
					dataSource), new ClassPathResource(
					"META-INF/testdata/dialect/oracle.sql"), true);

			SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(dataSource);
			StringBuffer callableStmt = new StringBuffer();

			callableStmt
					.append(" CREATE OR REPLACE TRIGGER logging_event_id_seq_trig \n");
			callableStmt.append(" BEFORE INSERT ON logging_event  \n");
			callableStmt.append(" FOR EACH ROW    \n");
			callableStmt.append("  BEGIN    \n");
			callableStmt.append("   SELECT logging_event_id_seq.NEXTVAL   \n");
			callableStmt.append("   INTO   :NEW.event_id   \n");
			callableStmt.append("    FROM   DUAL;  \n");
			callableStmt.append("  END logging_event_id_seq_trig;  \n");
			jdbcTemplate.getJdbcOperations().execute(callableStmt.toString());
		} else {
			// TODO
			LogFactory.getLog("sysoutLogger").debug(
					jdbcProperties.getProperty("usingDBMS")
							+ " currently not supported!");
		}
	}

	@Test
	public void testDBAppender() throws Exception {
		// db 로 설정되어 있는 로거
		Log log = null;

		if (isOracle) {
			// Oracle 인 경우 getGeneratedKeys 를 사용하지 않는 형태로 확장한
			// DBAppender(EgovDBAppender) 사용
			log = LogFactory.getLog("egovDBLogger");
		} else {
			log = LogFactory.getLog("dbLogger");
		}

		// 로그 기록
		log.debug("DBAppender test");

		// DB 확인
		String sql = "select * from logging_event order by event_id desc ";
		Map<String, Object> logMap = new SimpleJdbcTemplate(dataSource)
				.queryForList(sql).get(0);
		assertEquals(isOracle ? "egovDBLogger" : "dbLogger", logMap
				.get("LOGGER_NAME"));
		assertEquals("DEBUG", logMap.get("LEVEL_STRING"));
		assertEquals("DBAppender test", logMap.get("RENDERED_MESSAGE"));
		assertTrue(((String) logMap.get("THREAD_NAME")).contains("main"));
		assertEquals("AdvancedLogTargetTest.java", logMap
				.get("CALLER_FILENAME"));
		assertEquals("egovframework.rte.fdl.logging.AdvancedLogTargetTest",
				logMap.get("CALLER_CLASS"));
		assertEquals("testDBAppender", logMap.get("CALLER_METHOD"));
		// CALLER_LINE 은 CHAR(4) 로 정의되 있음 - blank 에 주의 (Oracle/Hsql 의 trim 처리가
		// 다름)
		String patternStr = "^[0-9]{1,4}$";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(((String) logMap.get("CALLER_LINE"))
				.trim());
		assertTrue(matcher.matches());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				java.util.Locale.getDefault());

		// oracle 인 경우 Number 필드에 대한 맵핑 타입인 BigDecimal 로 돌아옴
		if (isOracle) {
			assertTrue(1 <= ((BigDecimal) logMap.get("EVENT_ID")).intValue());
			// timestamp 필드가 NUMBER(20) 로 선언되어 있음.
			// 현재 분까지만 Date 변환하여 비교
			// cf.) 로그 테이블에 들어있는 timestamp 는 DB 서버의 값이 아닌 log 를 실행한 (여기서는 local
			// testcase) jvm 의 시간임에 유의!
			assertEquals(sdf.format(new Date()), sdf.format(new Timestamp(
					((BigDecimal) logMap.get("TIMESTAMP")).longValue())));
		} else {
			// hsql 인 경우는 0 부터 시작됨을 확인
			assertTrue((isHsql ? 0 : 1) <= (Integer) logMap.get("EVENT_ID"));
			// timestamp 필드가 BIGINT 로 선언되어 있음 -> Long
			assertEquals(sdf.format(new Date()), sdf.format(new Timestamp(
					(Long) logMap.get("TIMESTAMP"))));
		}

	}

	// EgovJDBCAppender 는 내부에 SingletonDataSourceProvider 의 singleton 인스턴스를 가지고
	// 있으며
	// SingletonDataSourceProvider 의 dataSource 를 Spring 의 Annotation 형태의
	// EgovJDBCAppender bean 생성 시
	// injection 해 주도록 하였음. (log4j 에서 생성하는 EgovJDBCAppender 와 Spring 에서 생성하는
	// EgovJDBCAppender bean 은
	// 서로 다르나 내부의 SingletonDataSourceProvider 는 같음!
	// EgovJDBCAppender 에서 DB 접속/해제 와 관련한 로직은 SingletonDataSourceProvider 의
	// dataSource 를 사용하게 함.
	@Test
	public void testEgovJDBCAppender() throws Exception {
		// pooledDB 로 설정되어 있는 로거
		Log log = LogFactory.getLog("pooledDBLogger");

		// 로그 기록
		log.debug("EgovJDBCAppender test");

		// DB 확인
		String sql = "select * from logging_event order by event_id desc ";
		Map<String, Object> logMap = new SimpleJdbcTemplate(dataSource)
				.queryForList(sql).get(0);
		assertEquals("pooledDBLogger", logMap.get("LOGGER_NAME"));
		assertEquals("DEBUG", logMap.get("LEVEL_STRING"));
		assertEquals("EgovJDBCAppender test", logMap.get("RENDERED_MESSAGE"));
		assertTrue(((String) logMap.get("THREAD_NAME")).contains("main"));
		assertEquals("AdvancedLogTargetTest.java", logMap
				.get("CALLER_FILENAME"));
		assertEquals("egovframework.rte.fdl.logging.AdvancedLogTargetTest",
				logMap.get("CALLER_CLASS"));
		assertEquals("testEgovJDBCAppender", logMap.get("CALLER_METHOD"));
		// CALLER_LINE 은 CHAR(4) 로 정의되 있음 - blank 에 주의 (Oracle/Hsql 의 trim 처리가
		// 다름)
		String patternStr = "^[0-9]{1,4}$";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(((String) logMap.get("CALLER_LINE"))
				.trim());
		assertTrue(matcher.matches());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				java.util.Locale.getDefault());

		// oracle 인 경우 Number 필드에 대한 맵핑 타입인 BigDecimal 로 돌아옴
		if (isOracle) {
			assertTrue(1 <= ((BigDecimal) logMap.get("EVENT_ID")).intValue());
			// timestamp 필드가 NUMBER(20) 로 선언되어 있음.
			// 현재 분까지만 Date 변환하여 비교
			// cf.) 로그 테이블에 들어있는 timestamp 는 DB 서버의 값이 아닌 log 를 실행한 (여기서는 local
			// testcase) jvm 의 시간임에 유의!
			assertEquals(sdf.format(new Date()), sdf.format(new Timestamp(
					((BigDecimal) logMap.get("TIMESTAMP")).longValue())));
		} else {
			// hsql 인 경우는 0 부터 시작됨을 확인
			assertTrue((isHsql ? 0 : 1) <= (Integer) logMap.get("EVENT_ID"));
			// timestamp 필드가 BIGINT 로 선언되어 있음 -> Long
			assertEquals(sdf.format(new Date()), sdf.format(new Timestamp(
					(Long) logMap.get("TIMESTAMP"))));
		}

	}

	@Test
	public void testJMSAppender() throws Exception {
		// console 로 설정되어 있는 로거
		Log log = LogFactory.getLog("jmsLogger");

		// jms 메시지에 담을 추가 정보 - 보통 로그인 사용자 관련 정보, 시스템 정보 등을 포함함
		MDC.put("class", this.getClass().getSimpleName());
		MDC.put("method", "testJMSAppender");
		MDC.put("testKey", "test value");

		// 로그 기록
		log.debug("JMSAppender test");

		// JMS 메시지 consumer (보통 remote 에 있는 system 이 될 수 있음. 여기서는 테스트로 같은
		// container 에 LoggerMDP)가
		// 해당 메시지를 처리하는 동안 (여기서는 ./logs/jmsconsume/jmsSample.log 에 기록) 잠시 sleep
		Thread.sleep(1000);

		// JMS 확인
		File jmsFile = new File("./logs/jmsconsume/jmsSample.log");

		String lastLine = LogFileUtil.getLastLine(jmsFile);

		LogFactory.getLog("sysoutLogger").debug(lastLine);

		// %d %5p [%c] [%X{class} %X{method} %X{testKey}] %m%n
		String patternStr = "^(.*\\]) \\[([a-zA-Z]+) ([a-zA-Z]+) ([a-zA-Z| ]+)\\] (.*)$";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(lastLine);
		boolean isMatch = matcher.matches();
		assertTrue(isMatch);

		LogFactory.getLog("sysoutLogger").debug(lastLine);
		LogFactory.getLog("sysoutLogger").debug(
				"%d %5p [%c] : " + matcher.group(1));
		LogFactory.getLog("sysoutLogger").debug(
				"%X{class} : " + matcher.group(2));
		LogFactory.getLog("sysoutLogger").debug(
				"%X{method} : " + matcher.group(3));
		LogFactory.getLog("sysoutLogger").debug(
				"%X{testKey} : " + matcher.group(4));
		LogFactory.getLog("sysoutLogger").debug("%m%n : " + matcher.group(5));
	}

}

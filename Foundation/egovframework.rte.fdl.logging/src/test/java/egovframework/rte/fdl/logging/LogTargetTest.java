package egovframework.rte.fdl.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import egovframework.rte.fdl.logging.sample.LogLayoutSample;
import egovframework.rte.fdl.logging.util.LogFileUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/context-common.xml" })
public class LogTargetTest {

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

	@Test
	public void testConsoleAppender() throws Exception {
		// console 로 설정되어 있는 로거
		Log log = LogFactory.getLog("egovframework");

		// 로그 기록
		log.debug("ConsoleAppender test");

		// 하단 콘솔창 확인
	}

	@Test
	public void testFileAppender() throws Exception {
		// file 로 설정되어 있는 로거 - ex.)
		// egovframework.rte.fdl.logging.sample.LogLayoutSample
		Log log = LogFactory.getLog(LogLayoutSample.class);

		File logFile = new File("./logs/file/sample.log");

		// 로그 기록
		log.debug("FileAppender test");

		// 로그파일의 마지막 라인 확인
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				java.util.Locale.getDefault());
		assertTrue(LogFileUtil.getLastLine(logFile).contains(
				sdf.format(new Date())));
		assertTrue(LogFileUtil.getLastLine(logFile).endsWith(
				"FileAppender test"));
	}

	@Test
	public void testRollingFileAppender() throws Exception {

		// rollingFile 로 설정되어 있는 로거
		Log log = LogFactory.getLog("rollingLogger");

		File logFile = new File("./logs/rolling/rollingSample.1.log");

		// 로그 기록
		log.debug("RollingFileAppender test");

		// 로그파일의 마지막 라인 확인
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				java.util.Locale.getDefault());
		assertTrue(LogFileUtil.getLastLine(logFile).contains(
				sdf.format(new Date())));
		assertTrue(LogFileUtil.getLastLine(logFile).endsWith(
				"RollingFileAppender test"));

		// 
		for (int i = 0; i < 1000; i++) {
			log.debug("RollingFileAppender loop : " + i);
		}

		File logFileDir = new File("./logs/rolling");

		// mvn command line 에서 테스트 시는 문제 발생 - MaxFileSize 로 파일 분리가 안되는 경우가 발생
		// 프로세스의 파일 접근 관련하여 미묘한 문제가 있어 보임. 1.3alpha-8 의 RollingFileAppender 사용은
		// 추천하지 않음.
		assertEquals(3, logFileDir.listFiles().length);
		for (File tempFile : logFileDir.listFiles()) {
			assertTrue(10000 >= tempFile.length());
		}
	}

	@Test
	public void testDailyRollingFileAppender() throws Exception {
		// dailyRollingFile 로 설정되어 있는 로거
		Log log = LogFactory.getLog("dailyLogger");

		// 최근 로그는 date pattern 이 들어가 있지 않은 기본 로그파일로 생김
		File logFile = new File("./logs/daily/dailyRollingSample.log");

		// 로그 기록
		log.debug("DailyRollingFileAppender test");

		// 로그파일의 마지막 라인 확인
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				java.util.Locale.getDefault());
		assertTrue(LogFileUtil.getLastLine(logFile).contains(
				sdf.format(new Date())));
		assertTrue(LogFileUtil.getLastLine(logFile).endsWith(
				"DailyRollingFileAppender test"));

		// 현재 log4j.xml 에서는 테스트 편의성을 위해 1초 단위로 로그파일을 변경하도록 설정하였으므로
		Thread.sleep(1000);

		log.debug("DailyRollingFileAppender - file change test");

		// 로그파일의 마지막 라인 확인 - 파일이 변경되었더라도 최신 로그는 original 로그 파일명으로 남음
		// 예전 로그는 date pattern 이 포함된 이전 로그 파일의 백업에서 확인할 것
		assertTrue(LogFileUtil.getLastLine(logFile).contains(
				sdf.format(new Date())));
		assertTrue(LogFileUtil.getLastLine(logFile).endsWith(
				"DailyRollingFileAppender - file change test"));

		File logFileDir = new File("./logs/daily");

		// 백업 파일이 하나 생겼을 것임.
		assertTrue(2 <= logFileDir.listFiles().length);

		SimpleDateFormat logDatePattern = new SimpleDateFormat("yyyy-MM-dd-HH",
				java.util.Locale.getDefault());
		// 설정 상 초단위로 나누었지만 백업 파일이 만들어지는 시간인 현재 시각과 초단위의 차이가 있으므로 빼고 비교함
		// 마침 백업이 59초에 발생하면 현재 시각과 분 단위가 달라질 수도 있으므로 시간까지만 비교함.
		assertTrue(logFileDir.listFiles()[1].getName().contains(
				logDatePattern.format(new Date())));
	}

}

package egovframework.rte.fdl.logging;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import egovframework.rte.fdl.logging.sample.LogLayoutSample;
import egovframework.rte.fdl.logging.util.LogFileUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/context-common.xml" })
public class Slf4JLoggerTest {

	@Test
	public void testSlf4jLogger() throws Exception {
		// console 로 설정되어 있는 slf4j 로거
		Logger slf4jLogger = LoggerFactory.getLogger("egovframework");

		// 로그 기록
		String arg = "some argument";
		slf4jLogger.debug("Slf4j Logger  test - {}", arg);

		// 하단 콘솔창 확인

		// file 로 설정되어 있는 slf4j 로거
		Logger slf4jFileLogger = LoggerFactory.getLogger(LogLayoutSample.class);

		// 로그 기록
		Object[] arguments = new Object[3];
		arguments[0] = "1st";
		arguments[1] = Integer.valueOf("2");
		arguments[2] = new Date();

		slf4jFileLogger.debug("Slf4j Logger test - {} {} {}", arguments);

		File logFile = new File("./logs/file/sample.log");

		// 로그파일의 마지막 라인 확인
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				java.util.Locale.getDefault());
		// ex. 2009-03-06 16:27 <-- %d cf.) 초는 비교하지 않았으나 로그파일에는 기록되 있음.
		assertTrue(LogFileUtil.getLastLine(logFile).contains(
				sdf.format(new Date())));
		// ex. Fri Mar 06 16:27 <-- arguments[2] = new Date() cf.) 초, KST 2009 는
		// 비교하지 않았으나 로그파일에는 기록되 있음.
		assertTrue(LogFileUtil.getLastLine(logFile).contains(
				"Slf4j Logger test - 1st 2 "
						+ (new Date()).toString().substring(0, 16)));
	}

}

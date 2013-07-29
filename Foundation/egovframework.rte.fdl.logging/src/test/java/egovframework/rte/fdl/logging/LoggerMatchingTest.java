package egovframework.rte.fdl.logging;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import egovframework.rte.fdl.logging.sample.LogLayoutSample;
import egovframework.rte.fdl.logging.sample.LogLevelDebug;
import egovframework.rte.fdl.logging.sample.LogLevelError;
import egovframework.rte.fdl.logging.sample.LogLevelFatal;
import egovframework.rte.fdl.logging.sample.LogLevelInfo;
import egovframework.rte.fdl.logging.sample.LogLevelWarn;
import egovframework.rte.fdl.logging.sample.LogTestSample;
import egovframework.rte.fdl.logging.util.LogFileUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/context-common.xml" })
public class LoggerMatchingTest {

	File logFile;

	@Before
	public void onSetUp() throws Exception {
		logFile = new File("./logs/file/sample.log");
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
	}

	@Test
	public void testLoggerMatching() throws Exception {

		// 로거 매칭 assert 확인 시 %d 에 해당하는 시간은 체크하지 않고 endsWith 으로 비교함

		// this - LoggerMatchingTest 의 매칭되는 Logger (Category) 는
		// egovframework.rte.fdl.logging.LoggerMatchingTest 의 풀패키지 명과 가장 매칭이 많이
		// 되는 logger 를 찾은 결과
		// log4j.xml 의 <logger name="egovframework" ..> 임
		Log testClassLog = LogFactory.getLog(this.getClass());
		testClassLog.debug("logger - egovframework");
		// 하단 console 출력 확인

		// class 패턴에 완전히 일치하는 log4j.xml 의
		// egovframework.rte.fdl.logging.sample.LogLayoutSample 로거가 있음
		Log layoutSampleLog = LogFactory.getLog(LogLayoutSample.class);

		layoutSampleLog
				.debug("logger - egovframework.rte.fdl.logging.sample.LogLayoutSample");
		assertTrue(LogFileUtil
				.getLastLine(logFile)
				.endsWith(
						"logger - egovframework.rte.fdl.logging.sample.LogLayoutSample"));

		// class 패턴에 완전히 일치하는 log4j.xml 의
		// egovframework.rte.fdl.logging.sample.LogLevelDebug 로거가 있음
		Log logLevelDebugLog = LogFactory.getLog(LogLevelDebug.class);
		logLevelDebugLog
				.debug("logger - egovframework.rte.fdl.logging.sample.LogLevelDebug");
		assertTrue(LogFileUtil.getLastLine(logFile).endsWith(
				"logger - egovframework.rte.fdl.logging.sample.LogLevelDebug"));

		// 이하 유사
		Log logLevelInfoLog = LogFactory.getLog(LogLevelInfo.class);
		Log logLevelWarnLog = LogFactory.getLog(LogLevelWarn.class);
		Log logLevelErrorLog = LogFactory.getLog(LogLevelError.class);
		Log logLevelFatalLog = LogFactory.getLog(LogLevelFatal.class);

		logLevelInfoLog
				.info("logger - egovframework.rte.fdl.logging.sample.LogLevelInfo");
		logLevelWarnLog
				.warn("logger - egovframework.rte.fdl.logging.sample.LogLevelWarn");
		logLevelErrorLog
				.error("logger - egovframework.rte.fdl.logging.sample.LogLevelError");
		logLevelFatalLog
				.fatal("logger - egovframework.rte.fdl.logging.sample.LogLevelFatal");

		String[] tailLines = LogFileUtil.getTailLines(logFile, 4);
		assertTrue(tailLines[0]
				.endsWith("logger - egovframework.rte.fdl.logging.sample.LogLevelInfo"));
		assertTrue(tailLines[1]
				.endsWith("logger - egovframework.rte.fdl.logging.sample.LogLevelWarn"));
		assertTrue(tailLines[2]
				.endsWith("logger - egovframework.rte.fdl.logging.sample.LogLevelError"));
		assertTrue(tailLines[3]
				.endsWith("logger - egovframework.rte.fdl.logging.sample.LogLevelFatal"));

		// LogTestSample 의 class 패턴과 가장 매칭이 많이 되는 logger 는 egovframework 임
		Log logTestSampleLog = LogFactory.getLog(LogTestSample.class);
		logTestSampleLog.debug("logger - egovframework");
		// 하단 console 출력 확인

		// 직접 로거명 mdcLogger 을 주어 log4j.xml 의 mdcLogger 로거를 지정
		Log mdcLogger = LogFactory.getLog("mdcLogger");
		MDC.put("class", this.getClass().getSimpleName());
		MDC.put("method", "testLogMDC");
		MDC.put("testKey", "test value");
		mdcLogger.debug("MDC test!");

		File mdcFile = new File("./logs/file/mdcSample.log");
		assertTrue(LogFileUtil
				.getLastLine(mdcFile)
				.endsWith(
						"DEBUG [mdcLogger] [LoggerMatchingTest testLogMDC test value] MDC test!"));

		// 존재하지 않는 로거명을 지정하는 경우 root 로거에 걸림
		Log notExistLog = LogFactory.getLog("notExistLogger");
		notExistLog.debug("logger - root, root 는 ERROR 레벨이므로 이 부분은 출력되지 않음.");
		notExistLog.error("logger - root, ERROR 는 출력됨.");
		// 하단에 root 로거에 매칭되어 console 창에 ERROR 레벨 이상인 메시지에 해당하는
		// "logger - root, ERROR 는 출력됨." 해당 메시지를 확인할 것

	}
}

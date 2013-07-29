package egovframework.rte.fdl.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import egovframework.rte.fdl.logging.sample.LogLevelDebug;
import egovframework.rte.fdl.logging.sample.LogLevelError;
import egovframework.rte.fdl.logging.sample.LogLevelFatal;
import egovframework.rte.fdl.logging.sample.LogLevelInfo;
import egovframework.rte.fdl.logging.sample.LogLevelWarn;
import egovframework.rte.fdl.logging.sample.LogTestSample;
import egovframework.rte.fdl.logging.sample.service.LogTestService;
import egovframework.rte.fdl.logging.sample.service.SomeVO;
import egovframework.rte.fdl.logging.sample.service.impl.LogTestServiceImpl;
import egovframework.rte.fdl.logging.util.LogFileUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/context-common.xml" })
public class LogLevelTest {

	@Resource(name = "logTestSample")
	LogTestSample logTestSample;

	@Resource(name = "logTestService")
	LogTestService logTestService;

	@Resource(name = "logLevelDebug")
	LogLevelDebug logLevelDebug;

	@Resource(name = "logLevelInfo")
	LogLevelInfo logLevelInfo;

	@Resource(name = "logLevelWarn")
	LogLevelWarn logLevelWarn;

	@Resource(name = "logLevelError")
	LogLevelError logLevelError;

	@Resource(name = "logLevelFatal")
	LogLevelFatal logLevelFatal;

	File logFile;

	@Before
	public void onSetUp() throws Exception {
		// ResourceLoader resourceLoader = new DefaultResourceLoader();
		// org.springframework.core.io.Resource resource =
		// resourceLoader.getResource("file:./logs/file/sample.log");
		// logFile = resource.getFile();
		logFile = new File("./logs/file/sample.log");
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
	}

	@Test
	public void testLogLevel() throws Exception {
		Logger targetLogger = logTestSample.getTargetLogger();

		assertEquals("egovframework.rte.fdl.logging.sample.LogTestSample",
				targetLogger.getName());
		Category selectedCategory = targetLogger;
		// 대상 클래스 풀패키지명에 매칭되는 정의된 로거(Category)가 없으면 parent 를 따라 설정되므로
		// 등록된 Category 는 level 이 정의되 있다는 가정하에 해당 Category 를 찾음
		while (selectedCategory.getLevel() == null) {
			selectedCategory = selectedCategory.getParent();
		}
		assertEquals("egovframework", selectedCategory.getName());
		assertTrue(selectedCategory.isDebugEnabled());
		assertEquals(Level.DEBUG, selectedCategory.getLevel());
		assertTrue(!selectedCategory.getAdditivity());

		// 현재 egovframework 로그 레벨이 log4j.xml 에 DEBUG 로 정의되 있으므로
		// 해당 로거 정의(Category) 를 따르는 logTestSample 의 메서드를 실행하면
		// DEBUG, INFO, WARN, ERROR, FATAL 에 대한 모든 로그가 출력될 것임. console 창에서 확인
		logTestSample.executeSomeLogic();

	}

	@Test
	public void testLogLevelWithIsLevelEnabled() throws Exception {
		SomeVO vo = new SomeVO();
		vo.setSomeAttr("some");

		// log4j.xml 에 정의되 있는 데로 "egoveframework" 로거 Category 가 선택되어 DEBUG 레벨의
		// console appender 로 출력됨 - console 창에서 확인
		logTestService.executeSomeLogic(vo);

		// logTestService 에 정의되 있는 로거와 같은 로거를 log4j API 를 써서 런타임에 변경해 가며 테스트
		Logger targetLogger = Logger.getLogger(LogTestServiceImpl.class);

		Category selectedCategory = targetLogger;
		// 대상 클래스 풀패키지명에 매칭되는 정의된 로거(Category)가 없으면 parent 를 따라 설정되므로
		// 등록된 Category 는 level 이 정의되 있다는 가정하에 해당 Category 를 찾음
		while (selectedCategory.getLevel() == null) {
			selectedCategory = selectedCategory.getParent();
		}

		// file appender 추가 - file Appender 를 가지고 있는 logger 를 통해 이를 대상 Logger 에
		// 적용토록 함.
		Appender fileAppender = Logger.getLogger(
				"egovframework.rte.fdl.logging.sample.LogLevelDebug")
				.getAppender("file");
		selectedCategory.addAppender(fileAppender);

		// log Level 변경 - INFO
		selectedCategory.setLevel(Level.INFO);
		logTestService.executeSomeLogic(vo);
		String[] tailLines = LogFileUtil.getTailLines(logFile, 4);
		assertTrue(tailLines[0]
				.endsWith("INFO - LogTestServiceImpl.executeSomeLogic executed"));
		assertTrue(tailLines[1]
				.endsWith("WARN - LogTestServiceImpl.executeSomeLogic executed"));
		assertTrue(tailLines[2]
				.endsWith("ERROR - LogTestServiceImpl.executeSomeLogic executed"));
		assertTrue(tailLines[3]
				.endsWith("FATAL - LogTestServiceImpl.executeSomeLogic executed"));

		// log Level 변경 - WARN
		selectedCategory.setLevel(Level.WARN);
		logTestService.executeSomeLogic(vo);
		// tail 3 라인 까지만 이번에 변경한 WARN 에 관련된 로깅이지만 이전 데이터 일부를 함께 가져와 새롭게 적용된 내용을
		// 명확히 함.
		tailLines = LogFileUtil.getTailLines(logFile, 4);
		// 이전 INFO 레벨일 때 마지막 라인
		assertTrue(tailLines[0]
				.endsWith("FATAL - LogTestServiceImpl.executeSomeLogic executed"));
		// 새로운 WARN 레벨 이후 기록된 라인 - WARN 이상 로그만 나옴
		assertTrue(tailLines[1]
				.endsWith("WARN - LogTestServiceImpl.executeSomeLogic executed"));
		assertTrue(tailLines[2]
				.endsWith("ERROR - LogTestServiceImpl.executeSomeLogic executed"));
		assertTrue(tailLines[3]
				.endsWith("FATAL - LogTestServiceImpl.executeSomeLogic executed"));

		// log Level 변경 - ERROR
		selectedCategory.setLevel(Level.ERROR);
		logTestService.executeSomeLogic(vo);
		// tail 2 라인 까지만 이번에 변경한 로그 레벨 에 관련된 로깅이지만 이전 데이터 일부를 함께 가져와 새롭게 적용된 내용을
		// 명확히 함.
		tailLines = LogFileUtil.getTailLines(logFile, 4);
		// 이전 WARN 레벨일 때 마지막 2 라인
		assertTrue(tailLines[0]
				.endsWith("ERROR - LogTestServiceImpl.executeSomeLogic executed"));
		assertTrue(tailLines[1]
				.endsWith("FATAL - LogTestServiceImpl.executeSomeLogic executed"));
		// 새로운 ERROR 레벨 이후 기록된 라인 - ERROR 이상 로그만 나옴
		assertTrue(tailLines[2]
				.endsWith("ERROR - LogTestServiceImpl.executeSomeLogic executed"));
		assertTrue(tailLines[3]
				.endsWith("FATAL - LogTestServiceImpl.executeSomeLogic executed"));

		// log Level 변경 - FATAL
		selectedCategory.setLevel(Level.FATAL);
		logTestService.executeSomeLogic(vo);
		// tail 1 라인 까지만 이번에 변경한 로그 레벨 에 관련된 로깅이지만 이전 데이터 일부를 함께 가져와 새롭게 적용된 내용을
		// 명확히 함.
		tailLines = LogFileUtil.getTailLines(logFile, 4);
		// 이전 ERROR 레벨일 때 마지막 3 라인
		assertTrue(tailLines[0]
				.endsWith("FATAL - LogTestServiceImpl.executeSomeLogic executed"));
		assertTrue(tailLines[1]
				.endsWith("ERROR - LogTestServiceImpl.executeSomeLogic executed"));
		assertTrue(tailLines[2]
				.endsWith("FATAL - LogTestServiceImpl.executeSomeLogic executed"));
		// 새로운 FATAL 레벨 이후 기록된 라인 - ERROR 이상 로그만 나옴
		assertTrue(tailLines[3]
				.endsWith("FATAL - LogTestServiceImpl.executeSomeLogic executed"));

	}

	@Test
	public void testLogLevelDebug() throws Exception {
		logLevelDebug.executeSomeLogic();
		LogFactory.getLog("sysoutLogger").debug(
				"logFile 현재 최종 라인 : " + LogFileUtil.getLastLine(logFile));
		assertTrue(LogFileUtil.getLastLine(logFile).endsWith(
				"DEBUG - LogLevelDebug.executeSomeLogic executed"));
	}

	@Test
	public void testLogLevelInfo() throws Exception {
		logLevelInfo.executeSomeLogic();
		LogFactory.getLog("sysoutLogger").debug(
				"logFile 현재 최종 라인 : " + LogFileUtil.getLastLine(logFile));
		assertTrue(LogFileUtil.getLastLine(logFile).endsWith(
				"INFO - LogLevelInfo.executeSomeLogic executed"));
	}

	@Test
	public void testLogLevelWarn() throws Exception {
		logLevelWarn.executeSomeLogic();
		LogFactory.getLog("sysoutLogger").debug(
				"logFile 현재 최종 라인 : " + LogFileUtil.getLastLine(logFile));
		assertTrue(LogFileUtil.getLastLine(logFile).endsWith(
				"WARN - LogLevelWarn.executeSomeLogic executed"));
	}

	@Test
	public void testLogLevelError() throws Exception {
		logLevelError.executeSomeLogic();
		LogFactory.getLog("sysoutLogger").debug(
				"logFile 현재 최종 라인 : " + LogFileUtil.getLastLine(logFile));
		assertTrue(LogFileUtil.getLastLine(logFile).endsWith(
				"ERROR - LogLevelError.executeSomeLogic executed"));
	}

	@Test
	public void testLogLevelFatal() throws Exception {
		logLevelFatal.executeSomeLogic();
		LogFactory.getLog("sysoutLogger").debug(
				"logFile 현재 최종 라인 : " + LogFileUtil.getLastLine(logFile));
		assertTrue(LogFileUtil.getLastLine(logFile).endsWith(
				"FATAL - LogLevelFatal.executeSomeLogic executed"));
	}
}

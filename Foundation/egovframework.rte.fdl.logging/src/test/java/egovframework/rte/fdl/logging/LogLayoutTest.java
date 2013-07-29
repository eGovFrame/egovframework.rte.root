package egovframework.rte.fdl.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import egovframework.rte.fdl.logging.sample.LogLayoutSample;
import egovframework.rte.fdl.logging.util.LogFileUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/context-common.xml" })
public class LogLayoutTest {

	@Resource(name = "logLayoutSample")
	LogLayoutSample logLayoutSample;

	File logFile;

	@Before
	public void onSetUp() throws Exception {
		logFile = new File("./logs/file/sample.log");
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
	}

	@Test
	public void testLogLayout() throws Exception {

		Logger targetLogger = logLayoutSample.getTargetLogger();

		assertEquals("egovframework.rte.fdl.logging.sample.LogLayoutSample",
				targetLogger.getName());
		// 해당 클래스에 대한 로거(Category) 를 직접 log4j.xml 에 설정하였으므로 로거 hierarchy 를 찾을 필요
		// 없음
		assertNotNull(targetLogger.getLevel());
		Category selectedCategory = targetLogger;

		assertEquals("egovframework.rte.fdl.logging.sample.LogLayoutSample",
				selectedCategory.getName());
		assertTrue(selectedCategory.isDebugEnabled());
		assertEquals(Level.DEBUG, selectedCategory.getLevel());
		assertTrue(!selectedCategory.getAdditivity());
		// 대상 로거는 file appender 가 달려 있음
		Appender fileAppender = selectedCategory.getAppender("file");
		assertNotNull(fileAppender);

		// 대상 로거의 file appender 의 Layout 을 변경해 가며 테스트
		assertEquals("org.apache.log4j.PatternLayout", fileAppender.getLayout()
				.getClass().getName());
		PatternLayout layout = (PatternLayout) fileAppender.getLayout();
		assertEquals("%d %5p [%c] %m%n", layout.getConversionPattern());

		logLayoutSample.executeSomeLogic();

		// ref.)
		// http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html
		// 주요 Conversion Character
		// d (date)
		// p (priority = log level)
		// c (Category)
		// m (사용자 메시지)
		// n (platform dependent line separator)
		// C (Class - 느림)
		// M (Method - 느림)
		// L (line number - 느림)
		// t (thread)
		// r (기동 후 경과시간)
		// X (MDC 객체의 특정 property 출력을 위해 - ex. %X{key} )
		String[] tailLines = LogFileUtil.getTailLines(logFile, 5);
		// check - %d
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS",
				java.util.Locale.getDefault());
		assertTrue(sdf.parse(tailLines[0].substring(0, 23)) instanceof Date);
		assertTrue(sdf.parse(tailLines[1].substring(0, 23)) instanceof Date);
		assertTrue(sdf.parse(tailLines[2].substring(0, 23)) instanceof Date);
		assertTrue(sdf.parse(tailLines[3].substring(0, 23)) instanceof Date);
		assertTrue(sdf.parse(tailLines[4].substring(0, 23)) instanceof Date);
		// check - %5p - cf.)%-5p 와 같이 -를 기술하면 right pad 됨!
		assertEquals("DEBUG", tailLines[0].substring(24, 29));
		assertEquals(" INFO", tailLines[1].substring(24, 29));
		assertEquals(" WARN", tailLines[2].substring(24, 29));
		assertEquals("ERROR", tailLines[3].substring(24, 29));
		assertEquals("FATAL", tailLines[4].substring(24, 29));
		// check - [%c]
		assertTrue(tailLines[0]
				.contains("[egovframework.rte.fdl.logging.sample.LogLayoutSample]"));
		assertTrue(tailLines[1]
				.contains("[egovframework.rte.fdl.logging.sample.LogLayoutSample]"));
		assertTrue(tailLines[2]
				.contains("[egovframework.rte.fdl.logging.sample.LogLayoutSample]"));
		assertTrue(tailLines[3]
				.contains("[egovframework.rte.fdl.logging.sample.LogLayoutSample]"));
		assertTrue(tailLines[4]
				.contains("[egovframework.rte.fdl.logging.sample.LogLayoutSample]"));
		// check - %m
		assertTrue(tailLines[0]
				.endsWith("DEBUG - LogLayoutSample.executeSomeLogic executed"));
		assertTrue(tailLines[1]
				.endsWith("INFO - LogLayoutSample.executeSomeLogic executed"));
		assertTrue(tailLines[2]
				.endsWith("WARN - LogLayoutSample.executeSomeLogic executed"));
		assertTrue(tailLines[3]
				.endsWith("ERROR - LogLayoutSample.executeSomeLogic executed"));
		assertTrue(tailLines[4]
				.endsWith("FATAL - LogLayoutSample.executeSomeLogic executed"));

		// layout 변경 테스트 - 아래와 같이 setConversionPattern 만 하면 layout 변경이 반영되지 않음.
		// 새로 변경한 conversionPattern 을 런타임에 반영하려면 반드시 activateOptions() 를 콜해줘야 함.
		layout.setConversionPattern("%d %5p [%c] %C %M %L %t %r %m%n");
		layout.activateOptions();

		// 로깅 재실행
		logLayoutSample.executeSomeLogic();

		// 로그파일에 기록된 마지막 5행을 다시 가져옴
		tailLines = LogFileUtil.getTailLines(logFile, 5);

		// 위의 %d %5p [%c] 및 마지막 %m%n 의 출력은 동일함.
		// thread 명이 main 으로만 나올 때도 있고 main [client-10] 과 같이 나오는 경우도 있음.
		String patternStr = "^(.*\\]) (egovframework\\.rte\\.fdl\\.logging\\.sample\\.LogLayoutSample) (executeSomeLogic) ([0-9]{2,}) ([a-z]+ ?\\[client-[0-9]{0,2}\\]|[a-z]+) ([0-9]+) (.*)$";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(tailLines[0]);
		boolean isMatch = matcher.matches();
		assertTrue(isMatch);
		// if (isMatch) {
		// for (int i=0; i<=matcher.groupCount(); i++) {
		// String groupStr = matcher.group(i);
		// LogFactory.getLog("sysoutLogger").debug(groupStr);
		// }
		// }
		LogFactory.getLog("sysoutLogger").debug("== tailLines[0] ==");
		LogFactory.getLog("sysoutLogger").debug(
				"%d %5p [%c] : " + matcher.group(1));
		LogFactory.getLog("sysoutLogger").debug("%C : " + matcher.group(2));
		LogFactory.getLog("sysoutLogger").debug("%M : " + matcher.group(3));
		LogFactory.getLog("sysoutLogger").debug("%L : " + matcher.group(4));
		LogFactory.getLog("sysoutLogger").debug("%t : " + matcher.group(5));
		LogFactory.getLog("sysoutLogger").debug("%r : " + matcher.group(6));
		LogFactory.getLog("sysoutLogger").debug("%m%n : " + matcher.group(7));

		matcher = pattern.matcher(tailLines[1]);
		isMatch = matcher.matches();
		assertTrue(isMatch);

		LogFactory.getLog("sysoutLogger").debug("== tailLines[1] ==");
		LogFactory.getLog("sysoutLogger").debug(
				"%d %5p [%c] : " + matcher.group(1));
		LogFactory.getLog("sysoutLogger").debug("%C : " + matcher.group(2));
		LogFactory.getLog("sysoutLogger").debug("%M : " + matcher.group(3));
		LogFactory.getLog("sysoutLogger").debug("%L : " + matcher.group(4));
		LogFactory.getLog("sysoutLogger").debug("%t : " + matcher.group(5));
		LogFactory.getLog("sysoutLogger").debug("%r : " + matcher.group(6));
		LogFactory.getLog("sysoutLogger").debug("%m%n : " + matcher.group(7));

		matcher = pattern.matcher(tailLines[2]);
		isMatch = matcher.matches();
		assertTrue(isMatch);

		LogFactory.getLog("sysoutLogger").debug("== tailLines[2] ==");
		LogFactory.getLog("sysoutLogger").debug(
				"%d %5p [%c] : " + matcher.group(1));
		LogFactory.getLog("sysoutLogger").debug("%C : " + matcher.group(2));
		LogFactory.getLog("sysoutLogger").debug("%M : " + matcher.group(3));
		LogFactory.getLog("sysoutLogger").debug("%L : " + matcher.group(4));
		LogFactory.getLog("sysoutLogger").debug("%t : " + matcher.group(5));
		LogFactory.getLog("sysoutLogger").debug("%r : " + matcher.group(6));
		LogFactory.getLog("sysoutLogger").debug("%m%n : " + matcher.group(7));

		matcher = pattern.matcher(tailLines[3]);
		isMatch = matcher.matches();
		assertTrue(isMatch);

		LogFactory.getLog("sysoutLogger").debug("== tailLines[3] ==");
		LogFactory.getLog("sysoutLogger").debug(
				"%d %5p [%c] : " + matcher.group(1));
		LogFactory.getLog("sysoutLogger").debug("%C : " + matcher.group(2));
		LogFactory.getLog("sysoutLogger").debug("%M : " + matcher.group(3));
		LogFactory.getLog("sysoutLogger").debug("%L : " + matcher.group(4));
		LogFactory.getLog("sysoutLogger").debug("%t : " + matcher.group(5));
		LogFactory.getLog("sysoutLogger").debug("%r : " + matcher.group(6));
		LogFactory.getLog("sysoutLogger").debug("%m%n : " + matcher.group(7));

		matcher = pattern.matcher(tailLines[4]);
		isMatch = matcher.matches();
		assertTrue(isMatch);

		LogFactory.getLog("sysoutLogger").debug("== tailLines[4] ==");
		LogFactory.getLog("sysoutLogger").debug(
				"%d %5p [%c] : " + matcher.group(1));
		LogFactory.getLog("sysoutLogger").debug("%C : " + matcher.group(2));
		LogFactory.getLog("sysoutLogger").debug("%M : " + matcher.group(3));
		LogFactory.getLog("sysoutLogger").debug("%L : " + matcher.group(4));
		LogFactory.getLog("sysoutLogger").debug("%t : " + matcher.group(5));
		LogFactory.getLog("sysoutLogger").debug("%r : " + matcher.group(6));
		LogFactory.getLog("sysoutLogger").debug("%m%n : " + matcher.group(7));

	}

	@Test
	public void testLogMDC() throws Exception {

		// mdc log file
		File mdcFile = new File("./logs/file/mdcSample.log");
		if (!mdcFile.exists()) {
			mdcFile.createNewFile();
		}

		// MDC (mapped diagnostic context) test
		MDC.put("class", this.getClass().getSimpleName());
		MDC.put("method", "testLogMDC");
		MDC.put("testKey", "test value");

		// class 패턴이 아닌 명시적인 로그명을 주었음. - 일반 로그 처리 시에는 class 패턴을 사용하는 것을 추천
		Logger testLogger = Logger.getLogger("mdcLogger");
		// MDC 객체를 put 한 다음 일반 로그 출력과 마찬가지로 debug 로 로그 수행
		testLogger.debug("MDC test!");

		String lastLine = LogFileUtil.getLastLine(mdcFile);

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

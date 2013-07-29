package egovframework.rte.fdl.logging;

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

import egovframework.rte.fdl.logging.util.LogFileUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/context-common.xml" })
public class AdvancedNetLogTargetTest {

	// @BeforeClass
	// public static void runRemoteSocketServer() {
	// try {
	// // Runtime.getRuntime().exec("runSocketServer.bat", null, new File("."));
	// Runtime
	// .getRuntime()
	// .exec(
	// "java -classpath ./lib/log4j-1.3alpha-8-374949.jar org.apache.log4j.net.SimpleSocketServer 4448 ./remote/log4j.xml");
	// } catch (BindException be) {
	// LogFactory.getLog("sysoutLogger").debug("이미 SimpleSocketServer 가 떠 있습니다.");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// 테스트 케이스를 실행하기 전에 반드시 외부 cmd 창에서 프로젝트 루트에 있는 runSocketServer.bat 를 실행할 것!

	@Test
	public void testSocketAppender() throws Exception {

		// socket 로 설정되어 있는 로거
		Log log = LogFactory.getLog("socketLogger");

		// 로그 기록
		log.debug("SocketAppender test");

		// remote socket 으로 전송하는 시간만큼 기다림
		Thread.sleep(500);

		// remote socket 확인
		// 테스트 편의를 위해 localhost 에 SimpleSocketServer 를 띄우고 remote log4j.xml 에서는
		// socketLogger 로 들어오는 로그를
		// ./remote/logs/remoteSample.log 에 기록되게 하였음
		File remoteFile = new File("./remote/logs/remoteSample.log");
		String lastLine = LogFileUtil.getLastLine(remoteFile);

		// 로그파일의 마지막 라인 확인
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				java.util.Locale.getDefault());
		assertTrue(lastLine.contains(sdf.format(new Date())));
		assertTrue(lastLine.endsWith("SocketAppender test"));
	}

	@Test
	public void testSMTPAppender() throws Exception {
		System.setProperty("mail.smtp.starttls.enable", "true");
		System.setProperty("mail.smtps.auth", "true");

		// mail 로 설정되어 있는 로거
		Log log = LogFactory.getLog("mailLogger");

		// 로그 기록 - SMTPAppender 로 기록하는 로그는 ERROR 레벨로 처리
		log.error("SMTPAppender 테스트 test");

		// 로컬 pc 에 James 메일 서버(servername = woo)를 설치하고 kimth 유저를 생성하여 테스트 하였음.
		// James 는 pop3 도 지원하므로 outlook 으로 연결하여 kimth 계정의 받은 편지함 확인

	}
}

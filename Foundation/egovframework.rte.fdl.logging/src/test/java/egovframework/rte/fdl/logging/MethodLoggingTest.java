package egovframework.rte.fdl.logging;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import egovframework.rte.fdl.logging.sample.service.LogTestService;
import egovframework.rte.fdl.logging.sample.service.SomeVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath*:META-INF/spring/context-common.xml",
		"classpath*:META-INF/spring/context-aspect.xml" })
public class MethodLoggingTest {

	@Resource(name = "logTestService")
	LogTestService logTestService;

	@Test
	public void testLogLevelWithIsLevelEnabled() throws Exception {
		SomeVO vo = new SomeVO();
		vo.setSomeAttr("some");

		// LogTestServiceImpl 의 대상 메서드는 aop 로 MethodParameterLoggingAspect 를 통해
		// 메서드의 arguments 를 먼저 로깅하게 됨. - console 에서 확인

		// log4j.xml 에 정의되 있는 데로 "egoveframework" 로거 Category 가 선택되어 DEBUG 레벨의
		// console appender 로 출력됨 - console 창에서 확인
		logTestService.executeSomeLogic(vo);

	}
}

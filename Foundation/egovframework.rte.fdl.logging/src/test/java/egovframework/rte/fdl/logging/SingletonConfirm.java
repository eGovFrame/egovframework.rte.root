package egovframework.rte.fdl.logging;

import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import egovframework.rte.fdl.logging.db.EgovJDBCAppender;

public class SingletonConfirm {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		EgovJDBCAppender egovJDBCAppender = new EgovJDBCAppender();

		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath*:META-INF/spring/context-*.xml" });

		EgovJDBCAppender egovJDBCAppenderBean = (EgovJDBCAppender) context
				.getBean("egovJDBCAppender");

		Assert.notNull(egovJDBCAppender);
		Assert.notNull(egovJDBCAppenderBean);

		Assert.notNull(egovJDBCAppender.getProvider());
		Assert.notNull(egovJDBCAppenderBean.getProvider());

		Assert.isTrue(egovJDBCAppender != egovJDBCAppenderBean);
		Assert.isTrue(egovJDBCAppender.getProvider() == egovJDBCAppenderBean
				.getProvider());

		LogFactory.getLog("sysoutLogger").debug("confirmed!");

	}

}

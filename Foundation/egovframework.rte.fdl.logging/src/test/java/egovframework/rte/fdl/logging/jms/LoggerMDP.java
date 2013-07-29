package egovframework.rte.fdl.logging.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.spi.LoggingEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component("loggerMDP")
public class LoggerMDP implements MessageListener {

	private final org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger("jmsMessageConsumerLogger");

	public void onMessage(Message message) {

		try {
			if (message instanceof ObjectMessage) {
				LogFactory.getLog("sysoutLogger").debug("Message recived: ");
				ObjectMessage obj = (ObjectMessage) message;
				LoggingEvent event = (LoggingEvent) obj.getObject();
				LogFactory.getLog("sysoutLogger").debug(
						"The logger name: " + event.getLoggerName());
				LogFactory.getLog("sysoutLogger").debug(
						"The message: " + event.getMessage().toString());
				logger.callAppenders(event);

			} else {
				LogFactory.getLog("sysoutLogger").debug(
						"Message of wrong type: "
								+ message.getClass().getName());
			}
		} catch (JMSException e) {
			LogFactory.getLog("sysoutLogger").debug(
					"JMSException in onMessage(): " + e.toString());
			e.printStackTrace();
		} catch (Throwable t) {
			LogFactory.getLog("sysoutLogger").debug(
					"Exception in onMessage():" + t.getMessage());
			t.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		@SuppressWarnings("unused")
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath*:META-INF/spring/context-*.xml" });
		System.in.read();
	}
}

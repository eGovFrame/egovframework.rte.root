package egovframework.rte.fdl.exception.handler;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import egovframework.rte.fdl.cmmn.exception.handler.ExceptionHandler;
import egovframework.rte.fdl.cmmn.mail.SimpleSSLMail;

public class EgovServiceExceptionHandler implements ExceptionHandler {

	protected Log log = LogFactory.getLog(this.getClass());

	@Resource(name = "otherSSLMailSender")
	private SimpleSSLMail mailSender;

	public void occur(Exception ex, String packageName) {

		log.debug(" EgovServiceExceptionHandler run..............."+ex.getMessage());
		try {
			
			mailSender.send("[Exception Handler Subject]" , ex.getMessage() +" <strong>occur</strong> !!! find the exception problem ");
			log.debug(" sending a alert mail  is completed ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

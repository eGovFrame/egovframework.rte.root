package egovframework.rte.fdl.exception.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import egovframework.rte.fdl.cmmn.exception.EgovBizException;
import egovframework.rte.fdl.cmmn.exception.handler.ExceptionHandler;

public class OthersServiceExceptionHandler implements ExceptionHandler {

	protected Log log = LogFactory.getLog(this.getClass());

	public void occur(Exception exception, String packageName) {
		log.debug(" OthersServiceExceptionHandler run..............."+((EgovBizException) exception).getWrappedException());
		
		try {
			if( exception instanceof EgovBizException){
				Exception exx = (Exception) ((EgovBizException) exception).getWrappedException();
				if(exx != null){
					exx.printStackTrace();
				}
			}
			
			log.debug(" sending a alert mail  is completed ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

package egovframework.rte.fdl.logging.sample.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import egovframework.rte.fdl.logging.sample.service.LogTestService;
import egovframework.rte.fdl.logging.sample.service.SomeVO;

@Service("logTestService")
public class LogTestServiceImpl implements LogTestService {

	protected Log log = LogFactory.getLog(this.getClass());

	public void executeSomeLogic(SomeVO vo) {
		if (log.isDebugEnabled()) {
			log.debug("DEBUG - LogTestServiceImpl.executeSomeLogic executed");
		}

		if (log.isInfoEnabled()) {
			log.info("INFO - LogTestServiceImpl.executeSomeLogic executed");
		}

		if (log.isWarnEnabled()) {
			log.warn("WARN - LogTestServiceImpl.executeSomeLogic executed");
		}

		if (log.isErrorEnabled()) {
			log.error("ERROR - LogTestServiceImpl.executeSomeLogic executed");
		}

		if (log.isFatalEnabled()) {
			log.fatal("FATAL - LogTestServiceImpl.executeSomeLogic executed");
		}
	}

}

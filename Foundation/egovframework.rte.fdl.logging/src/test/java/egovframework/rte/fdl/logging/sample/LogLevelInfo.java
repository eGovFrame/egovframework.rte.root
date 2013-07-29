package egovframework.rte.fdl.logging.sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component("logLevelInfo")
public class LogLevelInfo {

	protected Log log = LogFactory.getLog(this.getClass());

	public void executeSomeLogic() {
		log.info("INFO - LogLevelInfo.executeSomeLogic executed");
	}
}

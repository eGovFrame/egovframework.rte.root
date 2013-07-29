package egovframework.rte.fdl.logging.sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component("logLevelError")
public class LogLevelError {

	protected Log log = LogFactory.getLog(this.getClass());

	public void executeSomeLogic() {
		log.error("ERROR - LogLevelError.executeSomeLogic executed");
	}
}

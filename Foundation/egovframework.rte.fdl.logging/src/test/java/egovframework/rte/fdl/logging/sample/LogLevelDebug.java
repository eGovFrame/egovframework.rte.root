package egovframework.rte.fdl.logging.sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component("logLevelDebug")
public class LogLevelDebug {

	protected Log log = LogFactory.getLog(this.getClass());

	public void executeSomeLogic() {
		log.debug("DEBUG - LogLevelDebug.executeSomeLogic executed");
	}
}

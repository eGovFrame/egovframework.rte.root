package egovframework.rte.fdl.logging.sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component("logLevelWarn")
public class LogLevelWarn {

	protected Log log = LogFactory.getLog(this.getClass());

	public void executeSomeLogic() {
		log.warn("WARN - LogLevelWarn.executeSomeLogic executed");
	}
}

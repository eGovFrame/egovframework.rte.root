package egovframework.rte.fdl.logging.sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component("logLevelFatal")
public class LogLevelFatal {

	protected Log log = LogFactory.getLog(this.getClass());

	public void executeSomeLogic() {
		log.fatal("FATAL - LogLevelFatal.executeSomeLogic executed");
	}
}

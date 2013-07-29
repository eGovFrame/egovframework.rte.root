package egovframework.rte.bat.core.job.flow;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

/**
 * stepExecution의 exitCode를 변경 없이 return 하는 클래스
 * @author 실행환경 개발팀
 * @since 2012.07.20
 * @version 1.0
 * @see 
 * <pre>
 *      개정이력(Modification Information)
 *   
 *   수정일      수정자           수정내용
 *  ------- -------- ---------------------------
 *  2012.07.20  실행환경개발팀     최초 생성
 * </pre>
*/
public class EgovDecider implements JobExecutionDecider{

	/**
	 * stepExecution의 exitCode를 변경 없이 return
	 */
	public FlowExecutionStatus decide(JobExecution jobExecution,
			StepExecution stepExecution) {
		
		return new FlowExecutionStatus(stepExecution.getExitStatus().getExitCode());
	}

}

package egovframework.rte.fdl.logging.sample.aop;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;

public class MethodParameterLoggingAspect {

	@SuppressWarnings("unchecked")
	public void beforeLog(JoinPoint thisJoinPoint) {
		Class clazz = thisJoinPoint.getTarget().getClass();
		String className = thisJoinPoint.getTarget().getClass().getName();
		String methodName = thisJoinPoint.getSignature().getName();

		StringBuffer buf = new StringBuffer();
		buf.append("\n== MethodParameterLoggingAspect : [" + className + "."
				+ methodName + "()] ==");
		Object[] arguments = thisJoinPoint.getArgs();
		int argCount = 0;
		for (Object obj : arguments) {
			buf.append("\n - arg ");
			buf.append(argCount++);
			buf.append(" : ");
			buf.append(ToStringBuilder.reflectionToString(obj));
		}

		Log logger = LogFactory.getLog(clazz);
		if (logger.isDebugEnabled())
			logger.debug(buf.toString());
	}
}

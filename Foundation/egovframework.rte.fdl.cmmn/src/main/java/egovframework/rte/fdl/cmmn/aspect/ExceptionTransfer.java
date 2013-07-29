/*
 * Copyright 2008-2009 MOPAS(Ministry of Public Administration and Security).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package egovframework.rte.fdl.cmmn.aspect;

import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import egovframework.rte.fdl.cmmn.exception.BaseException;
import egovframework.rte.fdl.cmmn.exception.EgovBizException;
import egovframework.rte.fdl.cmmn.exception.FdlException;
import egovframework.rte.fdl.cmmn.exception.manager.ExceptionHandlerService;


/**
 * Exception 발생시 AOP(after-throwing) 에 의해 후처리로직 연결고리 역할 수행하는 클래스이다.
 * 
 * <p><b>NOTE:</b> Exception 종류를 EgovBizException, RuntimeException(DataAccessException 포함), FdlException ,
 * 나머지 Exception 으로 나누고 있으며, 후처리로직은 EgovBizException, RuntimeException 에서만 동작한다.
 * 그리고 나머지 Exception 의 경우 Exception 을 BaseException (메세지: fail.common.msg)으로 재생성하여 변경 던진다. 
 * 따라서 fail.common.msg 메세지키가 Message Resource 에 정의 되어 있어야 한다.</b>
 * 
 * @author Judd Cho (horanghi@gmail.com)
 * @since 2009.06.01
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.05.30  Judd Cho        최초 생성
 *
 * </pre>
 */

public class ExceptionTransfer {

	protected Log log = LogFactory.getLog(this.getClass());

	@Resource(name = "messageSource")
	private MessageSource messageSource;

	private ExceptionHandlerService[] exceptionHandlerServices;

	/**
	 * 디볼트로 패턴 매칭은 ANT 형태로 비교한다.
	 */
	private PathMatcher pm = new AntPathMatcher();


	/**
	 * ExceptionHandlerService을 여러개 지정한다.
	 * @param exceptionHandlerServices array of HandlerService
	 */
	public void setExceptionHandlerService(ExceptionHandlerService[] exceptionHandlerServices) {
		this.exceptionHandlerServices = exceptionHandlerServices;
		if (this.exceptionHandlerServices != null)
			log.debug(" count of ExceptionHandlerServices = " + exceptionHandlerServices.length);
	}
	
	/**
	 * ExceptionHandlerService을 여러개 지정한다.
	 * @return int ExceptionHandlerService 갯수 
	 */
	public int countOfTheExceptionHandlerService() {
		return (exceptionHandlerServices != null) ? exceptionHandlerServices.length : 0;
	}
	
	/**
	 * 발생한 Exception 에 따라 후처리 로직이 실행할 수 있도록 연결하는 역할을 수행한다.
	 * @param thisJoinPoint joinPoint 객체
	 * @param exception 발생한 Exception 
	 */
	public void transfer(JoinPoint thisJoinPoint, Exception exception) throws Exception {
		log.debug("execute ExceptionTransfer.transfer ");

		Class clazz = thisJoinPoint.getTarget().getClass();
		Signature signature = thisJoinPoint.getSignature();

		Locale locale = LocaleContextHolder.getLocale();
		/**
		 * BizException 을 구분하여 후처리로직을 수행하려 했으나 고려해야 할 부분이 발생.
		 * Exception 구분하여 후처리 로직을 발생하려면 설정상에 Exception의 상세 설정이 필요하게된다.
		 * 하지만 실제 현장에서 그렇게 나누는 경우는 없다. 
		 * 클래스 정보로만 패턴 분석을 통해 Handler 로 연결해주는 고리 역할 수행을 하게 된다.
		 */
		//EgovBizException 이 발생시 
		if (exception instanceof EgovBizException) {
			log.debug("Exception case :: EgovBizException ");

			EgovBizException be = (EgovBizException) exception;
			//wrapp 된 Exception 있는 경우 error 원인으로 출력해준다.
			if(be.getWrappedException() != null){
				Throwable _throwable = be.getWrappedException();
				getLog(clazz).error(be.getMessage(), _throwable);
			}else{
				getLog(clazz).error(be.getMessage(), be.getCause());
			}

			// Exception Handler 에 발생된 Package 와 Exception 설정. (runtime 이 아닌 ExceptionHandlerService를 실행함)
			processHandling(clazz, signature.getName(), be, pm, exceptionHandlerServices);

			throw be;

			//RuntimeException 이 발생시 내부에서 DataAccessException 인 경우 는 별도록 throw 하고 있다.
		} else if (exception instanceof RuntimeException) {
			log.debug("RuntimeException case :: RuntimeException ");

			RuntimeException be = (RuntimeException) exception;
			getLog(clazz).error(be.getMessage(), be.getCause());

			// Exception Handler 에 발생된 Package 와 Exception 설정.
			processHandling(clazz, signature.getName(), exception, pm, exceptionHandlerServices);

			if (be instanceof DataAccessException) {
				log.debug("RuntimeException case :: DataAccessException ");
				DataAccessException sqlEx = (DataAccessException) be;
				throw sqlEx;
			}

			throw be;
			
			//실행환경 확장모듈에서 발생한 Exception (요청: 공통모듈) :: 후처리로직 실행하지 않음.
		} else if (exception instanceof FdlException) {
			log.debug("FdlException case :: FdlException ");

			FdlException fe = (FdlException) exception;
			getLog(clazz).error(fe.getMessage(), fe.getCause());

			throw fe;

		} else {
			//그외에 발생한 Exception 을  BaseException (메세지: fail.common.msg) 로  만들어 변경 던진다. 
			//:: 후처리로직 실행하지 않음.
			log.debug("case :: Exception ");

			getLog(clazz).error(exception.getMessage(), exception.getCause());

			throw processException(clazz, "fail.common.msg", new String[] {}, exception, locale);

		}
	}

	protected Exception processException(final Class clazz, final String msgKey, final String[] msgArgs,
	                                     final Exception e, Locale locale) {
		return processException(clazz, msgKey, msgArgs, e, locale, null);
	}

	protected Exception processException(final Class clazz, final String msgKey, final String[] msgArgs,
	                                     final Exception e, final Locale locale, ExceptionCreator exceptionCreator) {
		getLog(clazz).error(messageSource.getMessage(msgKey, msgArgs, locale), e);
		ExceptionCreator eC = null;
		if (exceptionCreator == null) {
			eC = new ExceptionCreator() {
				public Exception processException(MessageSource messageSource) {
					return new BaseException(messageSource, msgKey, msgArgs, locale, e);
				}
			};
		}
		return eC.processException(messageSource);
	}

	protected interface ExceptionCreator {
		Exception processException(MessageSource messageSource);
	}

	protected Log getLog(Class clazz) {
		return LogFactory.getLog(clazz);
	}

	/**
	 * 발생한 Exception 에 따라 후처리 로직이 실행할 수 있도록 연결하는 역할을 수행한다.
	 * 
	 * @param clazz Exception 발생 클래스 
	 * @param methodName Exception 발생 메소드명 
	 * @param exception 발생한 Exception 
	 * @param pm 발생한 PathMatcher(default : AntPathMatcher) 
	 * @param exceptionHandlerServices[] 등록되어 있는 ExceptionHandlerService 리스트
	 */
	protected void processHandling(Class clazz, String methodName, Exception exception, PathMatcher pm,
	                               ExceptionHandlerService[] exceptionHandlerServices) {
		try {
			for (ExceptionHandlerService ehm : exceptionHandlerServices) {

				if (!ehm.hasReqExpMatcher())
					ehm.setReqExpMatcher(pm);
				ehm.setPackageName(clazz.getCanonicalName()+"."+methodName);
				ehm.run(exception);

			}
		} catch (Exception e) {
		}
	}
}

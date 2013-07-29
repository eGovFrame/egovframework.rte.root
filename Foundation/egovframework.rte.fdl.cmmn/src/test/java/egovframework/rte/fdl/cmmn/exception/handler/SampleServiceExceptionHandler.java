/*
 * Copyright 2002-2009 MOPAS(Ministry of Public Administration and Security).
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
package egovframework.rte.fdl.cmmn.exception.handler;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import egovframework.rte.fdl.cmmn.mail.SimpleSSLMail;

/**
 * Handler Sample : Exception 발생시 조건매칭후 메일을 발송한다. 
 * 
 * @author Judd Cho
 */
public class SampleServiceExceptionHandler implements ExceptionHandler {

	protected Log log = LogFactory.getLog(this.getClass());

	@Resource(name = "egovSampleSSLMailSender")
	private SimpleSSLMail mailSender;

	public void occur(Exception ex, String packageName) {

		log.debug(" SampleServiceExceptionHandler run...............");
		try {
			mailSender.send("[Exception Handler Subject]" , ex.getMessage() +" occur !!! find the exception problem ");
			log.debug(" sending a alert mail  is completed ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

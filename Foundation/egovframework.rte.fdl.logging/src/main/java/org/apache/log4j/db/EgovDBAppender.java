/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the ";License&quot;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS"; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.log4j.db;

/**
 * log4j 의 DBAppender 의 Oracle 적용을 위한 확장 클래스
 * <p>
 * <b>NOTE</b>: log4j-1.3alpha-8 의 DB Appender 를 extends 하고 있으며
 * useSupportsGetGeneratedKeys flag 에 따라 JDBC3.0 의 getGeneratedKeys 를 사용하지 않는 옵션
 * 추가한 Appeder 이다. Oracle 인 경우 (ojdbc-14.jar jdbc type 4 thin) getGeneratedKeys
 * 를 실행할 때 java.sql.SQLException: 허용되지 않은 작업 (operation not allowed) 에러가 나는데
 * EgovDBAppender 를 통해 useSupportsGetGeneratedKeys 를 사용하지 않도록 설정하여 Oracle 에 대한
 * DBAppender 의 처리에 문제가 없도록 지원한다.
 * 
 * @author 실행환경 개발팀 우병훈
 * @since 2009.03.09
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.03.09  우병훈          최초 생성
 * 
 * </pre>
 */
public class EgovDBAppender extends DBAppender {
	/** useSupportsGetGeneratedKeys flag - default 는 false */
	private boolean useSupportsGetGeneratedKeys = false;

	/**
	 * 현재 useSupportsGetGeneratedKeys 설정 여부 - log4j.xml 에 <param
	 * name="useSupportsGetGeneratedKeys" value="false" /> 의 설정에 따름
	 * 
	 * @return boolean(true/false)
	 */
	public boolean isUseSupportsGetGeneratedKeys() {
		return useSupportsGetGeneratedKeys;
	}

	/**
	 * log4j.xml 의 useSupportsGetGeneratedKeys 설정에 대한 setter.
	 * useSupportsGetGeneratedKeys 설정이 없는 경우 default 는 false 임.
	 * 
	 * @param useSupportsGetGeneratedKeys
	 *            - <param name="useSupportsGetGeneratedKeys" value="false" /> 와
	 *            같이 설정
	 */
	public void setUseSupportsGetGeneratedKeys(
			boolean useSupportsGetGeneratedKeys) {
		this.useSupportsGetGeneratedKeys = useSupportsGetGeneratedKeys;
	}

	/**
	 * 사용자가 정의한 useSupportsGetGeneratedKeys 설정에 따라 JDBC3.0 의 getGeneratedKeys 를
	 * 사용하지 않는 옵션으로 override 할 수 있음. Oracle 인 경우 JDBC API 로 getGeneratedKeys 가
	 * 존재하지만 실행 시 java.sql.SQLException: 허용되지 않은 작업 의 에러가 발생하는 문제를 회피하기 위해
	 * 확장하였음.
	 */
	@Override
	public void activateOptions() {
		// TODO Auto-generated method stub
		super.activateOptions();

		// useSupportsGetGeneratedKeys flag 에 따라 JDBC3.0 의 getGeneratedKeys 를
		// 사용하지 않는 옵션으로
		// 덮어쓸 수 있음.
		cnxSupportsGetGeneratedKeys = this.useSupportsGetGeneratedKeys;
	}

}

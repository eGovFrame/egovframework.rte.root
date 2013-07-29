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
package egovframework.rte.fdl.logging.db;

import javax.sql.DataSource;

/**
 * SingletonDataSourceProvider
 * <p>
 * <b>NOTE</b>: 싱글톤 패턴을 구현하고 있으며 dataSource를 설정하고 제공한다. log4j 의 Appender 에 대한 객체
 * 생성 시 SingletonDataSourceProvider 싱글톤 객체가 생성 되며, Spring Container 에 의한
 * Annotation 형식 dataSource injection 에 의해 dataSource 가 설정된다. 이후 log4j 의
 * EgovJDBCAppender 에서는 SingletonDataSourceProvider 를 통해 Spring 의 dataSource를
 * 사용하여 로깅 처리를 할 수 있다.
 * 
 * @author 실행환경 개발팀 우병훈
 * @since 2009.03.10
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.03.10  우병훈          최초 생성
 * 
 * </pre>
 */
public class SingletonDataSourceProvider {

	/** 싱글톤 해당 class 의 인스턴스 */
	private static SingletonDataSourceProvider instance;

	/** dataSource - Spring 연동으로 설정될 것임 */
	private DataSource dataSource;

	/**
	 * SingletonDataSourceProvider 싱글톤을 구현하기 위해 생성자를 private 로 선언하여 외부 framework
	 * 에서 인스턴스 생성할 수 없도록 처리함.
	 */
	private SingletonDataSourceProvider() {
		// LogFactory.getLog("sysoutLogger").debug("instance created!");
	}

	/**
	 * SingletonDataSourceProvider 싱글톤 객체를 얻는다. (이미 만들어져 있는 경우 해당 객체를 리턴하고, 처음인
	 * 경우는 싱글톤 인스턴스를 만든다.)
	 * 
	 * @return SingletonDataSourceProvider 의 싱글톤 instance
	 */
	public static SingletonDataSourceProvider getInstance() {
		if (instance == null) {
			synchronized (SingletonDataSourceProvider.class) {
				SingletonDataSourceProvider inst = instance;
				if (inst == null) {
					synchronized (SingletonDataSourceProvider.class) {
						instance = new SingletonDataSourceProvider();
					}
				}
			}
		}
		return instance;
	}

	/**
	 * Spring 연동 dataSource 에 대한 getter
	 * 
	 * @return 현재 Spring 에서 설정된 dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Spring 연동 dataSource 에 대한 setter
	 * 
	 * @param dataSource
	 *            - Spring Bean 으로 설정한 EgovJDBCAppender 에서 dataSource 만
	 *            injection 하여 현재 SingletonDataSourceProvider 의 setDataSource 로
	 *            재설정 해줌.
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}

/*
 * Copyright 2006-2007 the original author or authors.
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

package egovframework.rte.bat.core.item.file.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import egovframework.rte.bat.core.reflection.EgovReflectionSupport;


/**
 * item에 담긴 정보들을 getter 호출을 통해 추출하여 Object 배열로 반환하는 클래스
 * 
 * @author 배치실행개발팀
 * @since 2012. 07.20
 * @version 1.0
 * @see <pre>
 *      개정이력(Modification Information)
 *   
 *    수정일         수정자           수정내용
 *   -------    --------   ----------------
 * 2012.07.20  배치실행개발팀      최초 생성
 *  </pre>
 */

public class EgovFieldExtractor<T> implements FieldExtractor<T>, InitializingBean {
	
	// logger
	private static final Log logger = LogFactory.getLog(EgovFieldExtractor.class);
	
	// xml에 설정 된 extract할 VO의 field names
	private String[] names;

	// EgovReflectionSupport 사용을 위한 변수
	private EgovReflectionSupport<T> reflection;
	
	/**
	 * @param names xml에 설정 된 extract할 VO의 field names
	 */
	public void setNames(String[] names) {
		Assert.notNull(names, "Names must be non-null");
		this.names = Arrays.asList(names).toArray(new String[names.length]);
	}
	
	/**
	 * item에 담긴 field 정보를 추출하여 Object 배열로 return
	 * @see org.springframework.batch.item.file.transform.FieldExtractor#extract(java.lang.Object)
	 */
	public Object[] extract(T item) { 

		List<Object> values = new ArrayList<Object>(); 
		try {	
			reflection.generateGetterMethodMap(names, item);
		} catch (Exception e) {
			logger.debug(e);
		}	

		for(int i = 0; i<names.length; i++){
			values.add(reflection.invokeGettterMethod(item, names[i]));
		}
		return values.toArray();
	}

	/**
	 * bean이 등록 될 때 실행
	 */
	public void afterPropertiesSet() {
		Assert.notNull(names, "The 'names' property must be set.");
		reflection = new EgovReflectionSupport<T>();
	}
}

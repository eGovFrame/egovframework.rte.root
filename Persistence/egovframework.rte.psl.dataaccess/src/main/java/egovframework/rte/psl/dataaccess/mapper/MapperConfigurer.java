package egovframework.rte.psl.dataaccess.mapper;

import org.mybatis.spring.mapper.MapperScannerConfigurer;

/**
 * Mapper를 등록하기 위한 configurer로 Mapper annotation을 대상으로 scan한다.
 * <p>
 * 추가적으로 sqlSessionFactoryBeanName에 대하여 "sqlSession"을 사용한다. 
 * <p>
 * 설정 예:
 * <p>
 *
 * <pre class="code">
 * {@code
 *   <bean class="egovframework.rte.psl.dataaccess.mapper.MapperConfigurer">
 *       <property name="basePackage" value="egovframework.rte.**.mapper" />
 *   </bean>
 * }
 * </pre>
 *
 * @author Vincent Han
 * @since 2.6
 * 
 */
public class MapperConfigurer extends MapperScannerConfigurer {
	
	/**
	 * 기본 정보(anntationClass, sqlSessionFactoryBeanName)으로 설정한다.
	 */
	public MapperConfigurer() {
		super.setAnnotationClass(Mapper.class);
		super.setSqlSessionFactoryBeanName("sqlSession");
	}
}

package egovframework.rte.fdl.string;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author sjyoon
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/context-*.xml" })
public class EgovObjectUtilTest {

	Log log = LogFactory.getLog(getClass());

	@Before
    public void onSetUp() throws Exception {

    	log.debug("###### EgovObjectUtilTest.onSetUp START ######");
        
        log.debug("###### EgovObjectUtilTest.onSetUp END ######");
        
    }
    
    @After
    public void onTearDown() throws Exception {
    	
    	log.debug("###### EgovObjectUtilTest.onTearDown START ######");
    	
    	log.debug("###### EgovObjectUtilTest.onTearDown END ######");
    }
    
	/**
	 * [Flow #-1] Positive Case : check which string is empty or not
	 */
	@Test
	public void testIsNull() throws Exception {

		assertFalse(EgovObjectUtil.isNull(new Object()));
		
		assertTrue(EgovObjectUtil.isNull(null));
	}

	
	/**
	 * @throws Exception
	 */
	@Test
	public void testLoadClass() throws Exception {
		
		String className = "egovframework.rte.fdl.string.EgovStringUtil";
		String wrongClassName = "egovframework.rte.fdl.string.EgovStringUtil1";
		
		Class<?> clazz = EgovObjectUtil.loadClass(className);
		assertNotNull(clazz);

		clazz = null;
		assertNull(clazz);
		
		Class<Exception> exceptionClass = null;

		try {
			clazz = EgovObjectUtil.loadClass(wrongClassName);
		} catch (Exception e) {
			log.error("### Exception : " + e.toString());
			exceptionClass = (Class<Exception>) e.getClass();
		} finally {
			assertEquals(ClassNotFoundException.class, exceptionClass);
			assertNotSame(InstantiationException.class, exceptionClass);
			assertNotSame(IllegalAccessException.class, exceptionClass);
		}
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testInstantiate() throws Exception {
		
		String className = "java.lang.String";
		String wrongClassName = "java.lang.String1";
		String staticClassName = "egovframework.rte.fdl.string.EgovStringUtil";
		
		Object object = EgovObjectUtil.instantiate(className);
		assertNotNull(object);
		
		String string = (String) object;
		string = "eGovFramework";
		assertEquals("Framework", string.substring(4));

		object = null;
		assertNull(object);
		
		// wrongClass : ClassNotFoundException
		Class<Exception> exceptionClass = null;

		try {
			object = EgovObjectUtil.instantiate(wrongClassName);
		} catch (Exception e) {
			log.error("### Exception : " + e.toString());
			exceptionClass = (Class<Exception>) e.getClass();
		} finally {
			assertEquals(ClassNotFoundException.class, exceptionClass);
			assertNotSame(InstantiationException.class, exceptionClass);
			assertNotSame(IllegalAccessException.class, exceptionClass);
		}
		/*
		// static Class : IllegalAccessException
		exceptionClass = null;
		
		try {
			object = EgovObjectUtil.instantiate(staticClassName);
		} catch (Exception e) {
			log.error("### Exception : " + e.toString());
			exceptionClass = (Class<Exception>) e.getClass();
		} finally {
			assertNotSame(ClassNotFoundException.class, exceptionClass);
			assertNotSame(InstantiationException.class, exceptionClass);
			assertEquals(IllegalAccessException.class, exceptionClass);
		}
		*/
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testInstantiateParamConstructor() throws Exception {
		String className = "java.lang.StringBuffer";
		String[] types = new String[]{"java.lang.String"};
		Object[] values = new Object[]{"전자정부 공통서비스"};
		
		StringBuffer sb = (StringBuffer)EgovObjectUtil.instantiate(className, types, values);
		
		sb.append(" 및 개발프레임워크 구축 사업");
		
		assertEquals("전자정부 공통서비스 및 개발프레임워크 구축 사업", sb.toString());
		log.debug(sb.toString());
	}
}

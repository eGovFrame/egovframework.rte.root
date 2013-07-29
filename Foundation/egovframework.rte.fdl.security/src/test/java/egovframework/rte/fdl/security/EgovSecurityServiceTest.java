package egovframework.rte.fdl.security;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.SecurityConfig;
import org.springframework.security.config.BeanIds;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.intercept.method.DelegatingMethodDefinitionSource;
import org.springframework.security.intercept.web.FilterInvocation;
import org.springframework.security.intercept.web.FilterInvocationDefinitionSource;
import org.springframework.security.intercept.web.FilterSecurityInterceptor;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.util.FilterChainProxy;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

import egovframework.rte.fdl.security.userdetails.EgovUserDetailsVO;
import egovframework.rte.fdl.security.userdetails.util.EgovUserDetailsHelper;
import egovframework.rte.fdl.security.web.CategoryController;


/**
 * @author sjyoon
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath*:META-INF/spring/context-common.xml",
    "classpath*:META-INF/spring/context-datasource-jdbc.xml"
    })
public class EgovSecurityServiceTest extends
	AbstractDependencyInjectionSpringContextTests {
    
	private Log log = LogFactory.getLog(getClass());
    
	@Autowired
	private ApplicationContext context;
      
    @Resource(name = "dataSource")
    private DataSource dataSource;

    private boolean isHsql = true;

    @Resource(name = "jdbcProperties")
    private Properties jdbcProperties;


    /* (non-Javadoc)
     * @see org.springframework.test.AbstractSingleSpringContextTests#onSetUp()
     */
    @Before
    public void onSetUp() throws Exception {
    	log.debug("###### EgovSecurityServiceTest.onSetUp START ######");

    	isHsql = "hsql".equals(jdbcProperties.getProperty("usingDBMS"));

    	if (isHsql) {
            SimpleJdbcTestUtils.executeSqlScript(new SimpleJdbcTemplate(
                dataSource), new ClassPathResource(
                "META-INF/testdata/sample_schema_hsql.sql"), true);
        }

        context =
            new ClassPathXmlApplicationContext("classpath*:META-INF/spring/context-*.xml");
        
        log.debug("###### EgovSecurityServiceTest.onSetUp END ######");
        
    }
    
    @After
    public void onTearDown() throws Exception {
    	
    	log.debug("###### EgovSecurityServiceTest.onTearDown START ######");

        isHsql = "hsql".equals(jdbcProperties.getProperty("usingDBMS"));

        if (isHsql) {
            SimpleJdbcTestUtils.executeSqlScript(new SimpleJdbcTemplate(
                dataSource), new ClassPathResource(
                "META-INF/testdata/sample_schema_hsql_drop.sql"), true);
        }

        
        log.debug("###### EgovSecurityServiceTest.onTearDown END ######");
        
    	SecurityContextHolder.clearContext();
    }
    
    /**
     * DB에 사용자 정보(id/password)를 유지하여 인증처리 함
     * DB에 등록된 사용자의 인증 확인 테스트
     * @throws Exception
     */
    @Test
    public void testAllowAccessForAuthorizedUser() throws Exception {
       
       UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken("jimi", "jimi");
       AuthenticationManager authManager =
       	(AuthenticationManager) context.getBean(BeanIds.AUTHENTICATION_MANAGER);
       
       SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(login));
       log.debug("### jimi's password is right!!");
       
       ///////////
       login = new UsernamePasswordAuthenticationToken("test", "test");
       authManager =
       	(AuthenticationManager) context.getBean(BeanIds.AUTHENTICATION_MANAGER);
       
       SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(login));
       log.debug("### test's password is right!!");
       
       ///////////
       login = new UsernamePasswordAuthenticationToken("user", "user");
       authManager =
       	(AuthenticationManager) context.getBean(BeanIds.AUTHENTICATION_MANAGER);
       
       SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(login));
       log.debug("### user's password is right!!");
       
       ///////////
       login = new UsernamePasswordAuthenticationToken("buyer", "buyer");
       authManager =
       	(AuthenticationManager) context.getBean(BeanIds.AUTHENTICATION_MANAGER);
       
       SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(login));
       log.debug("### buyer's password is right!!");
    }
    
    /**
     * DB에 등록된 사용자의 인증 실패 테스트
     * @throws Exception
     */
    @Test
    @ExpectedException(BadCredentialsException.class)
    public void testRejectAccessForUnauthorizedUser() throws Exception {
       
       UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken("jimi", "wrongpw");
       AuthenticationManager authManager =
       	(AuthenticationManager) context.getBean(BeanIds.AUTHENTICATION_MANAGER);
       
       log.debug("### jimi's password is wrong!!");
       SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(login));
       
    }

    /**
     * 메소드 접근 제어 권한에 따른 Role 맵핑을 처리함
     * 메소드 수행이 허용된 메소드 실행 시 성공 테스트
     * @throws Exception
     */
    @Test
    public void testMethodAndRoleMapping() throws Exception {
    	
    	DelegatingMethodDefinitionSource definitionsource = (DelegatingMethodDefinitionSource) context.getBean(BeanIds.DELEGATING_METHOD_DEFINITION_SOURCE);
    	Method method = null;
    	ConfigAttributeDefinition role = null;
    	
    	// test1 : matched role
    	try {
    		method = CategoryController.class.getMethod("selectCategoryList", null);
    	} catch (NoSuchMethodException nsme) {
    		log.error("## testMethodAndRoleMapping : ", nsme);
    	}

   		role = definitionsource.getAttributes(method, CategoryController.class);

   		assertEquals("ROLE_USER", role.getConfigAttributes().toArray()[0].toString());
   		log.debug("## testMethodAndRoleMapping : " + method.getName() + " is " + role.getConfigAttributes().toArray()[0].toString());

    }
    
    /**
     * 메소드 수행이 허용되지 않은 메소드 실행 시 실패 테스트
     * @throws Exception
     */
    @Test
    public void testFailedMethodAndRoleMapping() throws Exception {
    	
    	DelegatingMethodDefinitionSource definitionsource = (DelegatingMethodDefinitionSource) context.getBean(BeanIds.DELEGATING_METHOD_DEFINITION_SOURCE);
    	Method method = null;
    	ConfigAttributeDefinition role = null;
    	
    	// test1 : no matched role
    	try {
    		method = CategoryController.class.getMethod("addCategoryView", null);
    	} catch (NoSuchMethodException nsme) {
    		log.error("## testMethodAndRoleMapping : ", nsme);
    	}

   		role = definitionsource.getAttributes(method, CategoryController.class);

   		assertEquals(null, role);
   		log.debug("## testMethodAndRoleMapping : " + method.getName() + " is no roles");
    }
    
    /**
     * 웹 URL 접근 제어 권한에 따른 Role 맵핑을 처리함
     * 웹 접근이 허용된 URL로 접근 시 성공 테스트
     * @throws Exception
     */
    @Test
    public void testURLAndRoleMapping() throws Exception {

        FilterSecurityInterceptor interceptor = (FilterSecurityInterceptor) context.getBean("filterSecurityInterceptor");
        FilterInvocationDefinitionSource definitionsource = interceptor.getObjectDefinitionSource();

        // "/test.do" ROLE_USER
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI(null);

        request.setServletPath("/test.do");

        FilterInvocation filterInvocation =
        	new FilterInvocation(request, new MockHttpServletResponse(), new MockFilterChain());
        
        ConfigAttributeDefinition attrs = definitionsource.getAttributes(filterInvocation);
        
        log.debug("### Pattern Matched url size is " +  attrs.getConfigAttributes().size() + " and Roles are " + attrs);
        assertTrue(attrs.contains(new SecurityConfig("ROLE_USER")));

        // "/sale/index.do" ROLE_RESTRICTED
        request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI(null);

        request.setServletPath("/sale/index.do");

        filterInvocation =
        	new FilterInvocation(request, new MockHttpServletResponse(), new MockFilterChain());

        
        attrs = definitionsource.getAttributes(filterInvocation);
        
        log.debug("### Pattern Matched url size is " +  attrs.getConfigAttributes().size() + " and Roles are " + attrs);
        assertTrue(attrs.contains(new SecurityConfig("ROLE_RESTRICTED")));

    }
    
    /**
     * 웹 접근이 허용되지 않은 URL로 접근 시 실패 테스트
     * @throws Exception
     */
    @Test
    public void testFailedURLAndRoleMapping() throws Exception {

        FilterSecurityInterceptor interceptor = (FilterSecurityInterceptor) context.getBean("filterSecurityInterceptor");
        FilterInvocationDefinitionSource definitionsource = interceptor.getObjectDefinitionSource();

        // "/test.do" ROLE_USER
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI(null);

        request.setServletPath("/index.do");

        FilterInvocation filterInvocation =
        	new FilterInvocation(request, new MockHttpServletResponse(), new MockFilterChain());
        
        ConfigAttributeDefinition attrs = definitionsource.getAttributes(filterInvocation);
        
        log.debug("### Pattern Matched url is none");
        assertNull(attrs);

    }
    

    /**
     * 웹 접근이 허용된 URL로 접근 시 Context 에서 지정한 로그인 화면으로 이동됨 검사
     * @throws Exception
     */
    @Test
    public void testSuccessfulUrlInvocation() throws Exception {

    	final String loginPage = "/cvpl/EgovCvplLogin.do";
    	
    	FilterChainProxy filterChainProxy = (FilterChainProxy) context.getBean(BeanIds.FILTER_CHAIN_PROXY);
   	
    	
    	////////////////
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setServletPath("/test.do");

    	MockHttpServletResponse response = new MockHttpServletResponse();
    	MockFilterChain chain = new MockFilterChain();

    	filterChainProxy.doFilter(request, response, chain);
    	
    	assertTrue(response.getRedirectedUrl().indexOf(loginPage) >= 0);
    	log.debug("### getRedirectedUrl " + response.getRedirectedUrl());
    	log.debug("### getForwardedUrl " + response.getForwardedUrl());
    	log.debug("### getIncludedUrl " + response.getIncludedUrl());
    	log.debug("### getErrorMessage " + response.getErrorMessage());
    	log.debug("### getContentAsString " + response.getContentAsString());


    	/////////////
    	request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setServletPath("/sale/index.do");

    	response = new MockHttpServletResponse();

    	filterChainProxy.doFilter(request, response, chain);
    	
    	assertTrue(response.getRedirectedUrl().indexOf(loginPage) >= 0);
    	log.debug("### getRedirectedUrl " + response.getRedirectedUrl());
    	log.debug("### getForwardedUrl " + response.getForwardedUrl());
    	log.debug("### getIncludedUrl " + response.getIncludedUrl());
    	log.debug("### getErrorMessage " + response.getErrorMessage());
    	log.debug("### getContentAsString " + response.getContentAsString());

    }
    
    /**
     * 웹 접근이 허용되지 않은 URL로 접근 시 Context 에서 지정한 로그인 화면으로 이동되지 않음 검사
     * @throws Exception
     */
    @Test
    public void testFailureUrlInvocation() throws Exception {

    	final String loginPage = "/cvpl/EgovCvplLogin.do";
    	
    	FilterChainProxy filterChainProxy = (FilterChainProxy) context.getBean(BeanIds.FILTER_CHAIN_PROXY);
   	
    	
    	////////////////
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setServletPath("/index.do");

    	MockHttpServletResponse response = new MockHttpServletResponse();
    	MockFilterChain chain = new MockFilterChain();

    	filterChainProxy.doFilter(request, response, chain);
    	
    	assertNull(response.getRedirectedUrl());
    	log.debug("### getRedirectedUrl is null");
    	
    	
    	////////////////
        request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setServletPath("/sale/index.doit");

    	response = new MockHttpServletResponse();
    	chain = new MockFilterChain();

    	filterChainProxy.doFilter(request, response, chain);
    	
    	assertNull(response.getRedirectedUrl());
    	log.debug("### getRedirectedUrl is null");

    }

    /**
     * 세션처리를 위한 UserDetails 확장 테스트
     * @throws Exception
     */
    @Test
    public void testUserDetailsExt() throws Exception {

        // 인증되지 않은 사용자 체크
    	Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
    	assertFalse(isAuthenticated.booleanValue());
    	log.debug("### testUserDetailsExt 인증 : " + isAuthenticated.booleanValue());

    	EgovUserDetailsVO user = (EgovUserDetailsVO)EgovUserDetailsHelper.getAuthenticatedUser();
    	assertNull(user);
    	log.debug("### testUserDetailsExt 사용자정보 : " + user);

    	// 로그인 jimi
    	UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken("jimi", "jimi");
        AuthenticationManager authManager =
        	(AuthenticationManager) context.getBean(BeanIds.AUTHENTICATION_MANAGER);
        
        SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(login));        

        // 인증된 사용자 검증
    	isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
    	assertTrue(isAuthenticated.booleanValue());
    	log.debug("### testUserDetailsExt 인증 : " + isAuthenticated.booleanValue());

        // 검증
        // ID : jimi
        user = (EgovUserDetailsVO)EgovUserDetailsHelper.getAuthenticatedUser();

        assertNotNull(user);
        assertEquals("jimi", user.getUserId());
        assertEquals("jimi test", user.getUserName());
        assertEquals("19800604", user.getBirthDay());
        assertEquals("1234567890123", user.getSsn());
        log.debug("### testUserDetailsExt 사용자 : " + user.getUserId());
        
    	// 로그인
        login = new UsernamePasswordAuthenticationToken("test", "test");
        authManager =
        	(AuthenticationManager) context.getBean(BeanIds.AUTHENTICATION_MANAGER);
        
        SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(login));        

        // 인증된 사용자 검증
    	isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
    	assertTrue(isAuthenticated.booleanValue());


        // ID : test
        user = (EgovUserDetailsVO)EgovUserDetailsHelper.getAuthenticatedUser();

        assertNotNull(user);
        assertEquals("test", user.getUserId());
        assertEquals("Kim, Young-Su", user.getUserName());
        assertEquals("19800604", user.getBirthDay());
        assertEquals("1234567890123", user.getSsn());
        log.debug("### testUserDetailsExt 사용자 : " + user.getUserId());

    	// 로그인
    	login = new UsernamePasswordAuthenticationToken("user", "user");
        authManager =
        	(AuthenticationManager) context.getBean(BeanIds.AUTHENTICATION_MANAGER);
        
        SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(login));        

        // 인증된 사용자 검증
    	isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
    	assertTrue(isAuthenticated.booleanValue());


        // ID : test
        user = (EgovUserDetailsVO)EgovUserDetailsHelper.getAuthenticatedUser();

        assertNotNull(user);
        assertEquals("user", user.getUserId());
        assertEquals("Hong Gil-dong", user.getUserName());
        assertEquals("19800603", user.getBirthDay());
        assertEquals("8006041227717", user.getSsn());
        log.debug("### testUserDetailsExt 사용자 : " + user.getUserId());
    
    	// 로그인
    	login = new UsernamePasswordAuthenticationToken("buyer", "buyer");
        authManager =
        	(AuthenticationManager) context.getBean(BeanIds.AUTHENTICATION_MANAGER);
        
        SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(login));        

        // 인증된 사용자 검증
    	isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
    	assertTrue(isAuthenticated.booleanValue());

        // ID : buyer
        user = (EgovUserDetailsVO)EgovUserDetailsHelper.getAuthenticatedUser();

        assertEquals("buyer", user.getUserId());
        assertEquals("Lee, Man-hong", user.getUserName());
        assertEquals("19701231", user.getBirthDay());
        assertEquals("1234567890123", user.getSsn());
        log.debug("### testUserDetailsExt 사용자 : " + user.getUserId());

    }
    
    /**
     * 지정된 Role 조회 테스트
     * @throws Exception
     */
    @Test
    public void testAuthoritiesAndRoleHierarchy() throws Exception {
    	// user User : ROLE_USER
    	UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken("user", "user");
        AuthenticationManager authManager =
        	(AuthenticationManager) context.getBean(BeanIds.AUTHENTICATION_MANAGER);
        
        SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(login));

    	log.debug(EgovUserDetailsHelper.getAuthorities());
    	
    	List<String> authorities = EgovUserDetailsHelper.getAuthorities();
    	
    	// 1. authorites 에  ROLE_USER 권한이 있는지 체크 TRUE/FALSE
    	log.debug("########### user ROLES are " + authorities);
    	assertTrue(authorities.contains("ROLE_USER"));
    	assertTrue(authorities.contains("ROLE_RESTRICTED"));
    	assertTrue(authorities.contains("IS_AUTHENTICATED_ANONYMOUSLY"));
    	assertTrue(authorities.contains("IS_AUTHENTICATED_FULLY"));
    	assertTrue(authorities.contains("IS_AUTHENTICATED_REMEMBERED"));


    	// 2. authorites 에  ROLE 이 여러개 설정된 경우
    	for (Iterator<String> it = authorities.iterator(); it.hasNext();) {
    		String auth = it.next();
    		log.debug("########### user ROLE is " + auth);
    	}    	

    	// 3. authorites 에  ROLE 이 하나만 설정된 경우
    	String auth = (String) authorities.toArray()[0];
    	log.debug("########### user ROLE is " + auth);

    	
    	// buyer USER : ROLE_RESTRICTED
    	login = new UsernamePasswordAuthenticationToken("buyer", "buyer");
        authManager =
        	(AuthenticationManager) context.getBean(BeanIds.AUTHENTICATION_MANAGER);
        
        SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(login));

    	log.debug(EgovUserDetailsHelper.getAuthorities());
    	
    	authorities = EgovUserDetailsHelper.getAuthorities();
    	
    	log.debug("########### buyer ROLES are " + authorities);
    	
    	assertFalse(authorities.contains("ROLE_USER"));
    	assertFalse(authorities.contains("ROLE_ADMIN"));
    	assertTrue(authorities.contains("ROLE_RESTRICTED"));
    	assertTrue(authorities.contains("IS_AUTHENTICATED_ANONYMOUSLY"));
    	assertTrue(authorities.contains("IS_AUTHENTICATED_FULLY"));
    	assertTrue(authorities.contains("IS_AUTHENTICATED_REMEMBERED"));
    	
    	// test USER : ROLE_ADMIN
    	login = new UsernamePasswordAuthenticationToken("test", "test");
        authManager =
        	(AuthenticationManager) context.getBean(BeanIds.AUTHENTICATION_MANAGER);
        
        SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(login));

    	log.debug(EgovUserDetailsHelper.getAuthorities());
    	
    	authorities = EgovUserDetailsHelper.getAuthorities();
    	
    	log.debug("########### test ROLES are " + authorities);
    	
    	assertTrue(authorities.contains("ROLE_USER"));
    	assertTrue(authorities.contains("ROLE_ADMIN"));
    	assertTrue(authorities.contains("ROLE_RESTRICTED"));
    	assertTrue(authorities.contains("IS_AUTHENTICATED_ANONYMOUSLY"));
    	assertTrue(authorities.contains("IS_AUTHENTICATED_FULLY"));
    	assertTrue(authorities.contains("IS_AUTHENTICATED_REMEMBERED"));
    }

}


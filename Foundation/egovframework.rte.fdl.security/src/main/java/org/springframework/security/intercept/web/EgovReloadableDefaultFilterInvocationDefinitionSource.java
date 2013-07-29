package org.springframework.security.intercept.web;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.util.UrlMatcher;

import egovframework.rte.fdl.security.securedobject.EgovSecuredObjectService;

/**
 * 실시간으로 url보호자원과 권한매핑 정보를 반영하기 위한 구현 클래스
 * <p>
 * <b>NOTE:</b> DB 기반으로 현재 시점의 url 보호자원 - 권한 맵핑 정보를
 * Runtime 에 동적으로 변경 반영하기 위한 Spring Security 의
 * DefaultFilterInvocationDefinitionSource 확장 클래스이다.
 * @author ByungHun Woo
 * @since 2009.06.01
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.06.01  윤성종           최초 생성
 * 
 * </pre>
 */
public class EgovReloadableDefaultFilterInvocationDefinitionSource extends
        DefaultFilterInvocationDefinitionSource implements
        ApplicationContextAware {

    private MessageSource messageSource;

    /**
     * set ApplicationContext.
     * @param applicationContext
     *        to be set by container
     */
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.messageSource =
            (MessageSource) applicationContext.getBean("messageSource");
    }

    /**
     * @return the messageSource
     */
    protected MessageSource getMessageSource() {
        return messageSource;
    }

    private EgovSecuredObjectService securedObjectService;

    public void setSecuredObjectService(
            EgovSecuredObjectService securedObjectService) {
        this.securedObjectService = securedObjectService;
    }

    public EgovReloadableDefaultFilterInvocationDefinitionSource(
            UrlMatcher urlMatcher, LinkedHashMap requestMap) {
        super(urlMatcher, requestMap);
    }

    public void reloadRequestMap() throws Exception {

        try {
            Map reloadedMap = securedObjectService.getRolesAndUrl();

            Iterator iterator = reloadedMap.entrySet().iterator();

            // 이전 데이터 삭제
            Map mapToUse = getRequestMap();
            mapToUse.clear();

            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                RequestKey reqKey = (RequestKey) entry.getKey();
                addSecureUrl(reqKey.getUrl(), reqKey.getMethod(),
                    (ConfigAttributeDefinition) entry.getValue());
            }

            // System.out.println("■
            // ReloadableDefaultFilterInvocationDefinitionSource.reloadRequestMap
            // processed! ■");
            if (logger.isInfoEnabled()) {
                logger
                    .info("Secured Url Resources - Role Mappings reloaded at Runtime!");
            }

        } catch (Exception e) {
            logger.error(getMessageSource().getMessage(
                "error.security.runtime.error",
                new Object[] {"Reload RequestMap" }, Locale.getDefault()), e);
            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                throw new Exception(/*
                                     * getMessageSource(
                                     * ),
                                     * "error.security.runtime.error"
                                     * , new Object[]
                                     * {e .getMessage()
                                     * },
                                     */e);
            }
        }
    }

    void addSecureUrl(String pattern, String method,
            ConfigAttributeDefinition attr) {
        Map mapToUse = getRequestMap();

        mapToUse.put(getUrlMatcher().compile(pattern), attr);

        if (logger.isDebugEnabled()) {
            logger.debug("Added URL pattern: " + pattern + "; attributes: "
                + attr
                + (method == null ? "" : " for HTTP method '" + method + "'"));
        }
    }

}

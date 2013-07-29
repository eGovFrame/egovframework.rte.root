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
package egovframework.rte.fdl.security.securedobject.impl;

import java.util.LinkedHashMap;
import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.security.ConfigAttributeDefinition;

import egovframework.rte.fdl.security.securedobject.EgovSecuredObjectService;

/**
 * 보호객체 관리를 지원하는 구현 클래스
 * <p>
 * <b>NOTE:</b> Spring Security의 초기 데이터를 DB로 부터 조회하여
 * 보호된 자원 접근 권한을 지원, 제어 할 수 있도록 구현한 클래스이다.
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
public class SecuredObjectServiceImpl implements EgovSecuredObjectService,
        ApplicationContextAware, InitializingBean {

    private MessageSource messageSource;

    private SecuredObjectDAO securedObjectDAO;

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

    public void setSecuredObjectDAO(SecuredObjectDAO securedObjectDAO) {
        this.securedObjectDAO = securedObjectDAO;
    }

    /**
     * initialize SecuredObjectService.
     * @throws Exception
     *         fail to initialize
     */
    public void afterPropertiesSet() throws Exception {
        // try {
        // // TODO
        // } catch (Exception e) {
        // if (e instanceof Exception) {
        // throw (Exception) e;
        // } else {
        // if
        // (EgovSecuredObjectService.LOGGER.isErrorEnabled())
        // {
        // EgovSecuredObjectService.LOGGER.error(messageSource
        // .getMessage("error.security.initialize.reason",
        // new String[] {}, Locale.getDefault()));
        // throw new Exception(e);
        // }
        // }
        // }
        //        
    }

    public LinkedHashMap getRolesAndUrl() throws Exception {
        try {
            return securedObjectDAO.getRolesAndUrl();
        } catch (Exception e) {
            EgovSecuredObjectService.LOGGER.error(getMessageSource()
                .getMessage("error.security.runtime.error",
                    new Object[] {"Roles and Url" }, Locale.getDefault()), e);
            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                throw new Exception(e);
            }
        }
    }

    public LinkedHashMap getRolesAndMethod() throws Exception {
        try {
            return securedObjectDAO.getRolesAndMethod();
        } catch (Exception e) {
            EgovSecuredObjectService.LOGGER
                .error(getMessageSource().getMessage(
                    "error.security.runtime.error",
                    new Object[] {"Roles and Method" }, Locale.getDefault()), e);
            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                throw new Exception(e);
            }
        }
    }

    public LinkedHashMap getRolesAndPointcut() throws Exception {
        try {
            return securedObjectDAO.getRolesAndPointcut();
        } catch (Exception e) {
            EgovSecuredObjectService.LOGGER.error(getMessageSource()
                .getMessage("error.security.runtime.error",
                    new Object[] {"Roles and Pointcut" }, Locale.getDefault()),
                e);
            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                throw new Exception(e);
            }
        }
    }

    public ConfigAttributeDefinition getMatchedRequestMapping(String url)
            throws Exception {
        try {
            return securedObjectDAO.getRegexMatchedRequestMapping(url);
        } catch (Exception e) {
            EgovSecuredObjectService.LOGGER.error(getMessageSource()
                .getMessage("error.security.runtime.error",
                    new Object[] {"MatchedRequestMapping : " + url },
                    Locale.getDefault()), e);
            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                throw new Exception(e);
            }
        }
    }

    public String getHierarchicalRoles() throws Exception {
        try {
            return securedObjectDAO.getHierarchicalRoles();
        } catch (Exception e) {
            EgovSecuredObjectService.LOGGER.error(getMessageSource()
                .getMessage("error.security.runtime.error",
                    new Object[] {"Hierarchical Roles" }, Locale.getDefault()),
                e);
            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                throw new Exception(e);
            }
        }
    }

}

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
package egovframework.rte.itl.webservice;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Endpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import egovframework.rte.itl.integration.EgovIntegrationContext;
import egovframework.rte.itl.integration.EgovIntegrationNoSuchServiceException;
import egovframework.rte.itl.integration.EgovIntegrationService;
import egovframework.rte.itl.integration.EgovIntegrationServiceProvider;
import egovframework.rte.itl.integration.metadata.IntegrationDefinition;
import egovframework.rte.itl.integration.metadata.dao.IntegrationDefinitionDao;
import egovframework.rte.itl.integration.monitor.EgovIntegrationServiceMonitor;
import egovframework.rte.itl.integration.type.CircularInheritanceException;
import egovframework.rte.itl.integration.type.NoSuchTypeException;
import egovframework.rte.itl.integration.type.RecordType;
import egovframework.rte.itl.integration.type.TypeLoader;
import egovframework.rte.itl.webservice.data.WebServiceClientDefinition;
import egovframework.rte.itl.webservice.data.WebServiceServerDefinition;
import egovframework.rte.itl.webservice.data.dao.WebServiceClientDefinitionDao;
import egovframework.rte.itl.webservice.data.dao.WebServiceServerDefinitionDao;
import egovframework.rte.itl.webservice.service.EgovWebServiceClassLoader;
import egovframework.rte.itl.webservice.service.EgovWebServiceClient;
import egovframework.rte.itl.webservice.service.MessageConverter;
import egovframework.rte.itl.webservice.service.ServiceBridge;
import egovframework.rte.itl.webservice.service.ServiceEndpointInfo;
import egovframework.rte.itl.webservice.service.ServiceEndpointInterfaceInfo;
import egovframework.rte.itl.webservice.service.impl.EgovWebServiceClientImpl;
import egovframework.rte.itl.webservice.service.impl.MessageConverterImpl;
import egovframework.rte.itl.webservice.service.impl.ServiceBridgeImpl;
import egovframework.rte.itl.webservice.service.impl.ServiceEndpointInfoImpl;
import egovframework.rte.itl.webservice.service.impl.ServiceEndpointInterfaceInfoImpl;

/**
 * 전자정부 웹서비스 Context 구현 클래스
 * <p>
 * <b>NOTE:</b> 전자정부 웹서비스 Context Class이다.
 * @author 실행환경 개발팀 심상호
 * @since 2009.06.01
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.06.01  심상호           최초 생성
 * 
 * </pre>
 */
public class EgovWebServiceContext implements EgovIntegrationContext,
        ApplicationContextAware {
    private Log LOG = LogFactory.getLog(this.getClass());

    // //////////////////////////////////////////////////////////////////////////
    // set method를 이용하여 설정되는 attributes
    //

    /** 기관 Id */
    protected String organizationId;

    /** 시스템 Id */
    protected String systemId;

    /** Default Timeout (millisecond) */
    protected long defaultTimeout;

    /** 연계 정의 DAO */
    protected IntegrationDefinitionDao integrationDefinitionDao;

    /** 웹서비스 클라이언트 정의 DAO */
    protected WebServiceClientDefinitionDao webServiceClientDefinitionDao;

    /** 웹서비스 서버 정의 DAO */
    protected WebServiceServerDefinitionDao webServiceServerDefinitionDao;

    /** Type Loader */
    protected TypeLoader typeLoader;

    /** ClassLoader */
    protected EgovWebServiceClassLoader classLoader;

    //
    // //////////////////////////////////////////////////////////////////////////

    // //////////////////////////////////////////////////////////////////////////
    // ApplicationContextAware interface에 의해 자동으로 설정되는
    // attribute
    //

    /** ApplicationContext */
    protected ApplicationContext applicationContext;

    //
    // //////////////////////////////////////////////////////////////////////////

    // //////////////////////////////////////////////////////////////////////////
    // init method에서 생성되는 attributes
    //

    /** MessageConverter */
    protected MessageConverter messageConverter;

    /** ServiceMap */
    protected Map<String, EgovWebService> serviceMap;

    /** ServerList */
    protected List<ServerInfo> serverList;

    /** Bus */
    protected Bus bus;

    //
    // //////////////////////////////////////////////////////////////////////////

    /**
     * Default Constructor
     */
    public EgovWebServiceContext() {
        super();
    }

    /**
     * @param organizationId
     *        the organizationId to set
     */
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * @param systemId
     *        the systemId to set
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /**
     * @return the defaultTimeout
     */
    public long getDefaultTimeout() {
        return defaultTimeout;
    }

    /**
     * @param defaultTimeout
     *        the defaultTimeout to set
     */
    public void setDefaultTimeout(long defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    /**
     * @param integrationDefinitionDao
     *        the integrationDefinitionDao to set
     */
    public void setIntegrationDefinitionDao(
            IntegrationDefinitionDao integrationDefinitionDao) {
        this.integrationDefinitionDao = integrationDefinitionDao;
    }

    /**
     * @param webServiceClientDefinitionDao
     *        the webServiceClientDefinitionDao to set
     */
    public void setWebServiceClientDefinitionDao(
            WebServiceClientDefinitionDao webServiceClientDefinitionDao) {
        this.webServiceClientDefinitionDao = webServiceClientDefinitionDao;
    }

    /**
     * @param webServiceServerDefinitionDao
     *        the webServiceServerDefinitionDao to set
     */
    public void setWebServiceServerDefinitionDao(
            WebServiceServerDefinitionDao webServiceServerDefinitionDao) {
        this.webServiceServerDefinitionDao = webServiceServerDefinitionDao;
    }

    /**
     * @param typeLoader
     *        the typeLoader to set
     */
    public void setTypeLoader(TypeLoader typeLoader) {
        this.typeLoader = typeLoader;
    }

    /**
     * @param classLoader
     *        the classLoader to set
     */
    public void setClassLoader(EgovWebServiceClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * @param applicationContext
     *        the applicationContext to set
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * EgovWebServiceContext를 초기화한다.
     * @throws IllegalArgumentException
     *         필요한 attribute가 설정되어 있지 않을 경우
     */
    public void init() throws IllegalArgumentException {
        LOG.debug("Initilaize EgovWebServiceContext");

        if (StringUtils.hasText(organizationId) == false) {
            LOG.error("Argument 'organizationId' is null or has no text "
                + "(organizationId = " + (organizationId == null ? "\"" : "")
                + organizationId + (organizationId == null ? "\"" : "") + ")");
            throw new IllegalArgumentException();
        } else if (StringUtils.hasText(systemId) == false) {
            LOG.error("Argument 'systemId' is null or has no text "
                + "(systemId = " + (systemId == null ? "\"" : "") + systemId
                + (systemId == null ? "\"" : "") + ")");
            throw new IllegalArgumentException();
        } else if (integrationDefinitionDao == null) {
            LOG.error("Argument 'integrationDefinitionDao' is null");
            throw new IllegalArgumentException();
        } else if (webServiceClientDefinitionDao == null) {
            LOG.error("Argument 'webServiceClientDefinitionDao' is null");
            throw new IllegalArgumentException();
        } else if (webServiceServerDefinitionDao == null) {
            LOG.error("Argument 'webServiceServerDefinitionDao' is null");
            throw new IllegalArgumentException();
        } else if (typeLoader == null) {
            LOG.error("Argument 'typeLoader' is null");
            throw new IllegalArgumentException();
        } else if (classLoader == null) {
            LOG.error("Argument 'classLoader' is null");
            throw new IllegalArgumentException();
        }

        LOG.debug("Create MessageConverter");
        messageConverter = new MessageConverterImpl(classLoader);

        initServerInfo();

        initClient();
    }

    /**
     * 웹서비스 서버 정보를 생성한다.
     */
    protected void initServerInfo() {
        LOG.debug("Initialzise Server Info");

        List<WebServiceServerDefinition> webServiceServerDefinitions =
            new ArrayList<WebServiceServerDefinition>();

        List<IntegrationDefinition> integrationDefinitions =
            integrationDefinitionDao.getIntegrationDefinitionOfProvider(
                organizationId, systemId);

        LOG.debug("Scan IntegrationDefinitions");
        for (IntegrationDefinition integrationDefinition : integrationDefinitions) {
            LOG.debug("Create Service Info of IntegrationDefinition("
                + integrationDefinition + ")");

            if (integrationDefinition == null) {
                LOG.error("IntegrationDefinition is null");
                continue;
            } else if (integrationDefinition.isValid() == false) {
                LOG.error("IntegrationDefinition is invalid");
                continue;
            } else if (integrationDefinition.isUsing() == false) {
                LOG.info("IntegrationDefinition(" + integrationDefinition
                    + ") is not usable");
                continue;
            } else if (integrationDefinition.getProvider().isUsing() == false) {
                LOG.info("IntegrationDefinition(" + integrationDefinition
                    + ")'s provider service is not usable");
                continue;
            }

            WebServiceServerDefinition webServiceServerDefinition =
                webServiceServerDefinitionDao
                    .getWebServiceServerDefinition(integrationDefinition
                        .getProvider());

            if (webServiceServerDefinition == null) {
                LOG
                    .error("WebServiceServerDefinition of IntegrationDefinition("
                        + integrationDefinition + ") does not exist.");
                continue;
            } else if (webServiceServerDefinition.isValid() == false) {
                LOG
                    .error("WebServiceServerDefinition of IntegrationDefinition("
                        + integrationDefinition + ") is invalid");
                continue;
            }

            if (webServiceServerDefinitions
                .contains(webServiceServerDefinition) == false) {
                webServiceServerDefinitions.add(webServiceServerDefinition);
            }
        }

        LOG.debug("Create WebService Server Module");

        serverList = new ArrayList<ServerInfo>();

        for (WebServiceServerDefinition webServiceServerDefinition : webServiceServerDefinitions) {
            LOG.debug("webServiceServerDefinition = "
                + webServiceServerDefinition);

            RecordType requestType = null;
            try {
                requestType =
                    (RecordType) typeLoader.getType(webServiceServerDefinition
                        .getServiceDefinition().getRequestMessageTypeId());
            } catch (NoSuchTypeException e) {
                LOG.error("RequestMessageType RecordType(id = \""
                    + webServiceServerDefinition.getServiceDefinition()
                        .getRequestMessageTypeId()
                    + "\")'s definition does not exist.", e);
                continue;
            } catch (CircularInheritanceException e) {
                LOG.error("RequestMessageType RecordType(id = \""
                    + webServiceServerDefinition.getServiceDefinition()
                        .getRequestMessageTypeId()
                    + "\") has circular inheritance.", e);
                continue;
            }
            LOG.debug("RequestMesageType = " + requestType);

            RecordType responseType = null;
            try {
                responseType =
                    (RecordType) typeLoader.getType(webServiceServerDefinition
                        .getServiceDefinition().getResponseMessageTypeId());
            } catch (NoSuchTypeException e) {
                LOG.error("ResponseMessageType RecordType(id = \""
                    + webServiceServerDefinition.getServiceDefinition()
                        .getResponseMessageTypeId()
                    + "\")'s definition does not exist.", e);
                continue;
            } catch (CircularInheritanceException e) {
                LOG.error("ResponseMessageType RecordType(id = \""
                    + webServiceServerDefinition.getServiceDefinition()
                        .getResponseMessageTypeId()
                    + "\") has circular inheritance.", e);
                continue;
            }
            LOG.debug("ResponseMessageType = " + responseType);

            ServiceEndpointInfo serviceEndpointInfo = null;
            try {
                serviceEndpointInfo =
                    new ServiceEndpointInfoImpl(webServiceServerDefinition,
                        requestType, responseType);
            } catch (IllegalArgumentException e) {
                LOG.error("Cannot create ServiceEndpointInfoImpl", e);
                continue;
            }
            LOG.debug("ServiceEndpointInfo = " + serviceEndpointInfo);

            Class<?> serviceImplClass = null;
            try {
                serviceImplClass = classLoader.loadClass(serviceEndpointInfo);
            } catch (ClassNotFoundException e) {
                LOG.error("Cannot load ServerEndpoint Class", e);
                continue;
            }
            LOG.debug("ServiceEndpoint Class = " + serviceImplClass);

            Object serviceImpl = null;
            try {
                serviceImpl = serviceImplClass.newInstance();
            } catch (IllegalAccessException e) {
                LOG.error("Cannot instantiate ServiceEndpoint", e);
                continue;
            } catch (InstantiationException e) {
                LOG.error("Cannot instantiate ServiceEndpoint", e);
                continue;
            }
            LOG.debug("ServiceEndpoint instance = " + serviceImpl);

            EgovIntegrationServiceProvider provider = null;
            try {
                provider =
                    (EgovIntegrationServiceProvider) applicationContext
                        .getBean(webServiceServerDefinition
                            .getServiceDefinition().getServiceProviderBeanId());
            } catch (BeansException e) {
                LOG.error("Cannot get providerBean(id = \""
                    + webServiceServerDefinition.getServiceDefinition()
                        .getServiceProviderBeanId() + "\")", e);
                continue;
            }
            LOG.debug("EgovIntegrationServiceProvider = " + provider);

            ServiceBridge serviceBridge = null;
            try {
                serviceBridge =
                    new ServiceBridgeImpl(provider, serviceEndpointInfo,
                        messageConverter);
            } catch (IllegalArgumentException e) {
                LOG.error("Cannot create ServiceBridge", e);
                continue;
            }
            LOG.debug("ServiceBridge = " + serviceBridge);

            Field fieldServiceBridge = null;
            String fieldName = classLoader.getFieldNameOfServiceBridge();
            try {
                fieldServiceBridge = serviceImplClass.getField(fieldName);
            } catch (NoSuchFieldException e) {
                LOG.error("ServiceEndpoint does not have the field(name = \""
                    + fieldName + "\")", e);
                continue;
            }

            try {
                fieldServiceBridge.set(serviceImpl, serviceBridge);
            } catch (IllegalAccessException e) {
                LOG.error("Cannot set ServiceBridge to provider", e);
                continue;
            } catch (IllegalArgumentException e) {
                LOG.error("Cannot set ServiceBridge to provider", e);
                continue;
            } catch (SecurityException e) {
                LOG.error("Cannot set ServiceBridge to provider", e);
                continue;
            }

            LOG.debug("Add new ServiceInfo");
            serverList.add(new ServerInfo(webServiceServerDefinition
                .getAddress(), serviceImpl));
        }

        LOG.debug("Finished Initializing Server Info");
    }

    protected void initClient() {
        LOG.debug("Initialize Client Info");

        serviceMap = new HashMap<String, EgovWebService>();

        List<IntegrationDefinition> integrationDefinitions =
            integrationDefinitionDao.getIntegrationDefinitionOfConsumer(
                organizationId, systemId);

        for (IntegrationDefinition integrationDefinition : integrationDefinitions) {
            LOG.debug("Create Client Info of IntegrationDefinition("
                + integrationDefinition + ")");

            if (integrationDefinition == null) {
                LOG.error("IntegrationDefinition is null");
                continue;
            } else if (integrationDefinition.isValid() == false) {
                LOG.error("IntegrationDefinition is invalid");
                continue;
            } else if (integrationDefinition.isUsing() == false) {
                LOG.info("IntegrationDefinition(" + integrationDefinition
                    + ") is not usagble");
                continue;
            } else if (integrationDefinition.getProvider().isUsing() == false) {
                LOG.info("IntegrationDefinition(" + integrationDefinition
                    + ")'s provider service is not usable");
                continue;
            }

            WebServiceClientDefinition webServiceClientDefinition =
                webServiceClientDefinitionDao
                    .getWebServiceClientDefinition(integrationDefinition
                        .getProvider());
            if (webServiceClientDefinition == null) {
                LOG
                    .error("WebServiceClientDefinition of IntegrationDefinition("
                        + integrationDefinition + ") does not exist.");
                continue;
            } else if (webServiceClientDefinition.isValid() == false) {
                LOG
                    .error("WebServiceClientDefinition of IntegrationDefinition("
                        + integrationDefinition + ") is invalid");
                continue;
            }

            LOG.debug("WebServiceClientDefinition = "
                + webServiceClientDefinition);

            RecordType requestType = null;
            try {
                requestType =
                    (RecordType) typeLoader.getType(webServiceClientDefinition
                        .getServiceDefinition().getRequestMessageTypeId());
            } catch (NoSuchTypeException e) {
                LOG.error("RequestMessageType RecordType(id = \""
                    + webServiceClientDefinition.getServiceDefinition()
                        .getRequestMessageTypeId()
                    + "\")'s definition does not exist.", e);
                continue;
            } catch (CircularInheritanceException e) {
                LOG.error("RequestMessageType RecordType(id = \""
                    + webServiceClientDefinition.getServiceDefinition()
                        .getRequestMessageTypeId()
                    + "\") has circular inheritance.", e);
                continue;
            }
            LOG.debug("RequestMesageType = " + requestType);

            RecordType responseType = null;
            try {
                responseType =
                    (RecordType) typeLoader.getType(webServiceClientDefinition
                        .getServiceDefinition().getResponseMessageTypeId());
            } catch (NoSuchTypeException e) {
                LOG.error("ResponseMessageType RecordType(id = \""
                    + webServiceClientDefinition.getServiceDefinition()
                        .getResponseMessageTypeId()
                    + "\")'s definition does not exist.", e);
                continue;
            } catch (CircularInheritanceException e) {
                LOG.error("ResponseMessageType RecordType(id = \""
                    + webServiceClientDefinition.getServiceDefinition()
                        .getResponseMessageTypeId()
                    + "\") has circular inheritance.", e);
                continue;
            }
            LOG.debug("ResponseMessageType = " + responseType);

            // ServiceEndpointInterface 정보를 생성한다.
            ServiceEndpointInterfaceInfo serviceEndpointInterfaceInfo = null;
            try {
                serviceEndpointInterfaceInfo =
                    new ServiceEndpointInterfaceInfoImpl(
                        webServiceClientDefinition, requestType, responseType);
            } catch (IllegalArgumentException e) {
                LOG.error("Cannot create ServiceEndpointInterfaceInfo", e);
                continue;
            }
            LOG.debug("ServiceEndpointInterfaceInfo = "
                + serviceEndpointInterfaceInfo);

            EgovWebServiceClient client = null;
            try {
                client =
                    new EgovWebServiceClientImpl(classLoader,
                        serviceEndpointInterfaceInfo, messageConverter);
            } catch (ClassNotFoundException e) {
                LOG.error("Cannot create EgovWebServiceClient", e);
                continue;
            } catch (MalformedURLException e) {
                LOG.error("Cannot create EgovWebServiceClient", e);
                continue;
            } catch (NoSuchMethodException e) {
                LOG.error("Cannot create EgovWebServiceClient", e);
                continue;
            } catch (SecurityException e) {
                LOG.error("Cannot create EgovWebServiceClient", e);
                continue;
            }
            LOG.debug("EgovWebServiceClient = " + client);

            EgovWebService service = null;
            try {
                service =
                    new EgovWebService(integrationDefinition.getId(),
                        defaultTimeout, integrationDefinition, client);
            } catch (IllegalArgumentException e) {
                LOG.error("Cannot create EgovWebService", e);
                continue;
            }
            LOG.debug("EgovWebService = " + service);

            LOG.debug("Add Client Info");
            serviceMap.put(integrationDefinition.getId(), service);
        }

        LOG.debug("Finish initializing Client Info");
    }

    public void publishServer(Bus bus) {
        LOG.debug("Publish Server");

        this.bus = bus;
        BusFactory.setDefaultBus(bus);

        for (ServerInfo serverInfo : serverList) {
            LOG.debug("Publish Server (address = \"" + serverInfo.address
                + "\")");
            try {
                Endpoint.publish(serverInfo.address, serverInfo.serviceImpl);
            } catch (Throwable e) {
                LOG.error("Fail in publishing Server (address = \""
                    + serverInfo.address + "\")", e);
            }
        }

        LOG.debug("Finish publish Server");
    }

    public void attachMonitor(EgovIntegrationServiceMonitor monitor) {
        // TODO :
    }

    public void detachMonitor(EgovIntegrationServiceMonitor monitor) {
        // TODO :
    }

    public EgovIntegrationService getService(String id) {
        if (serviceMap.containsKey(id) == false) {
            throw new EgovIntegrationNoSuchServiceException();
        }
        return serviceMap.get(id);
    }

    protected static class ServerInfo {
        public String address;

        public Object serviceImpl;

        public ServerInfo(String address, Object serviceImpl) {
            super();
            this.address = address;
            this.serviceImpl = serviceImpl;
        }
    }
}

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
package egovframework.rte.itl.webservice.service.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebParam.Mode;
import javax.xml.ws.Holder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import egovframework.rte.itl.integration.EgovIntegrationMessageHeader;
import egovframework.rte.itl.integration.EgovIntegrationServiceProvider;
import egovframework.rte.itl.integration.EgovIntegrationMessageHeader.ResultCode;
import egovframework.rte.itl.webservice.EgovWebServiceMessage;
import egovframework.rte.itl.webservice.EgovWebServiceMessageHeader;
import egovframework.rte.itl.webservice.service.MessageConverter;
import egovframework.rte.itl.webservice.service.ServiceBridge;
import egovframework.rte.itl.webservice.service.ServiceEndpointInfo;
import egovframework.rte.itl.webservice.service.ServiceParamInfo;

/**
 * 자동 생성된 웹 서비스 ServiceEndpoint와
 * EgovIntegrationServiceProvider의 인터페이스 구현 클래스
 * <p>
 * <b>NOTE:</b> 자동 생성된 웹 서비스 ServiceEndpoint와
 * EgovIntegrationServiceProvider를 연결해주는 ServiceBridge
 * class이다.
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
public class ServiceBridgeImpl implements ServiceBridge {
    private Log LOG = LogFactory.getLog(this.getClass());

    /** 서비스 제공 모듈 */
    private EgovIntegrationServiceProvider provider;

    /** ServiceEndpointInfo */
    private ServiceEndpointInfo serviceEndpointInfo;

    /** MessageConverter */
    private MessageConverter messageConverter;

    /**
     * Constructor
     * @param provider
     *        서비스 제공 모듈
     * @param serviceEndpointInfo
     *        ServiceEndpointInfo
     * @param messageConverter
     *        MessageConverter
     * @throws IllegalArgumentException
     *         <code>provider</code>,
     *         <code>serviceEndpointInfo</code>,
     *         <code>messageConverter</code> 값이
     *         <code>null</code>인 경우
     */
    public ServiceBridgeImpl(EgovIntegrationServiceProvider provider,
            ServiceEndpointInfo serviceEndpointInfo,
            MessageConverter messageConverter) {
        super();
        if (provider == null) {
            LOG.error("Argument 'provider' is null");
            throw new IllegalArgumentException();
        } else if (serviceEndpointInfo == null) {
            LOG.error("Argument 'serviceEndpointInfo' is null");
            throw new IllegalArgumentException();
        } else if (messageConverter == null) {
            LOG.error("Argument 'messageConverter' is null");
            throw new IllegalArgumentException();
        }
        this.provider = provider;
        this.serviceEndpointInfo = serviceEndpointInfo;
        this.messageConverter = messageConverter;
    }

    @SuppressWarnings("unchecked")
    public Object doService(Map<String, Object> params) {
        LOG.debug("ServiceBridge's doService");

        // requestMessage를 parsing한다.
        // 최대한 가능한 부분을 parsing한다.
        LOG.debug("Parse Request Message");
        EgovIntegrationMessageHeader requestHeader = null;
        Map<String, Object> requestBody = new HashMap<String, Object>();
        boolean succeed = true;
        for (ServiceParamInfo paramInfo : serviceEndpointInfo.getParamInfos()) {
            if (paramInfo.getMode().equals(Mode.IN)
                || paramInfo.getMode().equals(Mode.INOUT)) {
                Object valueObject = params.get(paramInfo.getName());
                if (paramInfo.getMode().equals(Mode.INOUT)) {
                    valueObject = ((Holder) valueObject).value;
                }

                if (paramInfo.getType() == EgovWebServiceMessageHeader.TYPE) {
                    requestHeader = (EgovWebServiceMessageHeader) valueObject;
                } else {
                    try {
                        Object typedObject =
                            messageConverter.convertToTypedObject(valueObject,
                                paramInfo.getType());
                        requestBody.put(paramInfo.getName(), typedObject);
                    } catch (Throwable e) {
                        LOG.error("Cannot parse request message", e);
                        succeed = false;
                    }
                }
            }
        }

        // 현재 시각
        final Calendar now = Calendar.getInstance();

        // 응답 메시지
        EgovWebServiceMessage responseMessage = null;

        if (succeed == false) {
            // 요청 메시지 parsing 실패
            LOG.error("Fail to parse request message");
            EgovWebServiceMessageHeader responseHeader = null;
            if (requestHeader == null) {
                responseHeader = new EgovWebServiceMessageHeader();
            } else {
                responseHeader = new EgovWebServiceMessageHeader(requestHeader);
            }
            responseHeader.setRequestReceiveTime(now);
            responseHeader
                .setResultCode(ResultCode.FAIL_IN_PARSING_REQUEST_MESSAGE);

            responseMessage = new EgovWebServiceMessage(responseHeader);
        } else if (requestHeader == null) {
            // 요청 메시지 헤더가 없음
            LOG.error("No MessageHeader in request message");
            responseMessage =
                new EgovWebServiceMessage(new EgovWebServiceMessageHeader() {
                    {
                        setRequestReceiveTime(now);
                        setResultCode(ResultCode.NO_MESSAGE_HEADER_IN_REQUEST);
                    }
                });
        } else {
            // 요청 수신 시각 설정
            requestHeader.setRequestReceiveTime(Calendar.getInstance());

            // 요청 메시지 생성
            EgovWebServiceMessage requestMessage =
                new EgovWebServiceMessage(requestHeader, requestBody);

            // empty 응답 메시지 생성
            responseMessage =
                new EgovWebServiceMessage(new EgovWebServiceMessageHeader(
                    requestHeader));

            LOG.debug("Call Provider's service");
            succeed = true;
            try {
                provider.service(requestMessage, responseMessage);
            } catch (Throwable e) {
                // provider service 메소드 실패
                LOG.error("Fail to call provider's service", e);
                responseMessage =
                    new EgovWebServiceMessage(new EgovWebServiceMessageHeader(
                        requestHeader) {
                        {
                            setResultCode(ResultCode.FAIL_TO_CALL_SERVICE_PROVIDER);
                        }
                    });
                succeed = false;
            }
        }

        LOG.debug("Create Response Message");
        if (succeed) {
            try {
                Map<String, Object> responseBody = responseMessage.getBody();
                for (ServiceParamInfo paramInfo : serviceEndpointInfo
                    .getParamInfos()) {
                    if (paramInfo.getMode().equals(Mode.OUT)
                        || paramInfo.getMode().equals(Mode.INOUT)) {
                        Object valueObject = null;
                        if (paramInfo.getType() == EgovWebServiceMessageHeader.TYPE) {
                            valueObject = responseMessage.getHeader();
                        } else {
                            Object typedObject =
                                responseBody.get(paramInfo.getName());
                            valueObject =
                                messageConverter.convertToValueObject(
                                    typedObject, paramInfo.getType());
                        }

                        ((Holder) params.get(paramInfo.getName())).value =
                            valueObject;
                    }
                }
                responseMessage.getHeader().setResponseSendTime(
                    Calendar.getInstance());

                return null;
            } catch (Throwable e) {
                LOG.error("Cannot create response message", e);
                responseMessage =
                    new EgovWebServiceMessage(new EgovWebServiceMessageHeader(
                        requestHeader) {
                        {
                            setResultCode(ResultCode.FAIL_IN_CREATING_RESPONSE_MESSAGE);
                        }
                    });
                succeed = false;
            }
        }

        // 전체 실패
        // 헤더부만 찾아서 헤더만 넘긴다.
        ServiceParamInfo headerParamInfo = null;
        for (ServiceParamInfo paramInfo : serviceEndpointInfo.getParamInfos()) {
            if (paramInfo.getType() == EgovWebServiceMessageHeader.TYPE) {
                headerParamInfo = paramInfo;
            }
        }
        if (headerParamInfo == null) {
            // 발생하면 안됨
            LOG.error("No Header ParamInfo");
        } else if (headerParamInfo.getMode().equals(Mode.IN)) {
            // 발생하면 안됨
            LOG.error("Header ParamInfo's Mode must be IN or INOUT");
        } else {
            ((Holder) params.get(headerParamInfo.getName())).value =
                responseMessage.getHeader();
            responseMessage.getHeader().setResponseSendTime(
                Calendar.getInstance());
        }

        return null;
    }
}

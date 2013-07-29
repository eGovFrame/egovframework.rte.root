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

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import egovframework.rte.itl.integration.EgovIntegrationMessage;
import egovframework.rte.itl.integration.EgovIntegrationMessageHeader.ResultCode;
import egovframework.rte.itl.integration.metadata.IntegrationDefinition;
import egovframework.rte.itl.integration.metadata.OrganizationDefinition;
import egovframework.rte.itl.integration.metadata.ServiceDefinition;
import egovframework.rte.itl.integration.metadata.SystemDefinition;
import egovframework.rte.itl.integration.support.AbstractService;
import egovframework.rte.itl.webservice.service.EgovWebServiceClient;

/**
 * 전자정부 웹 서비스의 단위 서비스 구현 클래스
 * <p>
 * <b>NOTE:</b> 전자정부 웹 서비스의 단위 서비스 class이다.
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
public class EgovWebService extends AbstractService {
    private Log LOG = LogFactory.getLog(this.getClass());

    /** 연계 정의 */
    private final IntegrationDefinition integrationDefinition;

    /** 기본 메시지 헤더 */
    private final EgovWebServiceMessageHeader defaultHeader;

    /** EgovWebServiceClient */
    private EgovWebServiceClient client;

    /**
     * Constructor
     * @param id
     *        연계 ID
     * @param defaultTimeout
     *        default timeout
     * @param integrationDefinition
     *        연계 정의
     * @param client
     *        client
     * @throws IllegalArgumentException
     *         1. Argument <code>id</code>,
     *         <code>integrationDefinition</code>,
     *         <code>client</code> 값이 <code>null</code>
     *         인 경우 <br>
     *         2. MessageHeader를 구성하기 위해 필요한 값들을 얻을 수
     *         없는 경우 <br>
     *         3. Argument <code>id</code>와
     *         <code>integrationDefinition.id</code> 값이
     *         다른 경우
     */
    public EgovWebService(String id, long defaultTimeout,
            IntegrationDefinition integrationDefinition,
            EgovWebServiceClient client) {
        super(id, defaultTimeout);
        if (integrationDefinition == null) {
            LOG.error("integrationDefinition is null");
            throw new IllegalArgumentException();
        } else if (integrationDefinition.isValid() == false) {
            LOG.error("integrationDefinition is invalid");
            throw new IllegalArgumentException();
        } else if (id.equals(integrationDefinition.getId()) == false) {
            LOG.error("id not equals to integrationDefinition's id");
            throw new IllegalArgumentException();
        } else if (client == null) {
            LOG.error("client is null");
            throw new IllegalArgumentException();
        }
        this.integrationDefinition = integrationDefinition;
        this.client = client;

        // need not check, is validated by
        // integrationDefinition.isValid
        ServiceDefinition providerService = integrationDefinition.getProvider();
        // if (providerService == null)
        // {
        // LOG.error("integrationDefinition.provider is null");
        // throw new IllegalArgumentException();
        // }
        SystemDefinition providerSystem = providerService.getSystem();
        // if (providerSystem == null)
        // {
        // LOG.error("integrationDefinition.provider.system is null");
        // throw new IllegalArgumentException();
        // }
        OrganizationDefinition providerOrganization =
            providerSystem.getOrganization();
        // if (providerOrganization == null)
        // {
        // LOG.error("integrationDefinition.provider.system.organization "
        // +
        // "is null");
        // throw new IllegalArgumentException();
        // }
        SystemDefinition consumerSystem = integrationDefinition.getConsumer();
        // if (consumerSystem == null)
        // {
        // LOG.error("integrationDefinition.consumer is null");
        // throw new IllegalArgumentException();
        // }
        OrganizationDefinition consumerOrganization =
            consumerSystem.getOrganization();
        // if (consumerOrganization == null)
        // {
        // LOG.error("integrationDefinition.consumer.organization is null");
        // throw new IllegalArgumentException();
        // }
        this.defaultHeader =
            new EgovWebServiceMessageHeader(id, providerOrganization.getId(),
                providerSystem.getId(), providerService.getId(),
                consumerOrganization.getId(), consumerSystem.getId(), null,
                null, null, null, ResultCode.OK, null);
    }

    public EgovIntegrationMessage createRequestMessage() {
        return new EgovWebServiceMessage(new EgovWebServiceMessageHeader(
            defaultHeader));
    }

    @Override
    protected EgovIntegrationMessage doSend(
            EgovIntegrationMessage requestMessage) {
        LOG.debug("EgovWebSerivce doSend (requestMessage = " + requestMessage
            + ")");

        // 연계가 가능한지 체크한다.
        if (integrationDefinition.isUsing() == false) {
            LOG.info("Integration (id = \"" + id + "\") is not usable");
            return new EgovWebServiceMessage(new EgovWebServiceMessageHeader(
                defaultHeader) {
                {
                    setResultCode(ResultCode.NOT_USABLE_INTEGRATION);
                }
            });
        }

        // 연계 가능한 시간인지 체크한다.
        boolean validFrom = true;
        boolean validTo = true;
        Calendar now = Calendar.getInstance();
        if (integrationDefinition.getValidateFrom() != null) {
            validFrom =
                (integrationDefinition.getValidateFrom().compareTo(now) <= 0);
        }
        if (integrationDefinition.getValidateTo() != null) {
            validTo =
                (now.compareTo(integrationDefinition.getValidateTo()) <= 0);
        }
        if (validFrom == false || validTo == false) {
            LOG.info("Integration (id = \"" + id + "\") is invalid at " + now
                + " (validFrom = " + integrationDefinition.getValidateFrom()
                + ", validTo = " + integrationDefinition.getValidateTo() + ")");
            return new EgovWebServiceMessage(new EgovWebServiceMessageHeader(
                defaultHeader) {
                {
                    setResultCode(ResultCode.INVALID_TIME);
                }
            });
        }

        // 대상 서비스가 사용 가능한지 체크한다.
        if (integrationDefinition.getProvider().isUsing() == false) {
            LOG.info("Integration (id = \"" + id + "\")'s provider service "
                + "is not usable");
            return new EgovWebServiceMessage(new EgovWebServiceMessageHeader(
                defaultHeader) {
                {
                    setResultCode(ResultCode.NOT_USABLE_SERVICE);
                }
            });
        }

        // 요청 송신 시각 설정
        requestMessage.getHeader().setRequestSendTime(now);

        // 요청 송신 & 응답 수신
        EgovIntegrationMessage responseMessage = client.service(requestMessage);

        // 응답 수신 시각 설정
        responseMessage.getHeader().setResponseReceiveTime(
            Calendar.getInstance());

        return responseMessage;
    }
}

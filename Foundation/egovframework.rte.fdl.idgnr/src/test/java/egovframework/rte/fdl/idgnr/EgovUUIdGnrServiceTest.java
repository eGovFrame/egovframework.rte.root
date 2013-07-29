package egovframework.rte.fdl.idgnr;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import egovframework.rte.fdl.cmmn.exception.FdlException;
import egovframework.rte.fdl.idgnr.impl.strategy.EgovIdGnrStrategyImpl;

/**
 * UUId Generation Service Test 클래스
 * @author 실행환경 개발팀 김태호
 * @since 2009.02.01
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.02.01  김태호          최초 생성
 * 
 * </pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/context-uuid.xml" })
public class EgovUUIdGnrServiceTest {

    @Resource(name = "UUIdGenerationService")
    private EgovIdGnrService uUidGenerationService;

    @Resource(name = "UUIdGenerationServiceWithoutAddress")
    private EgovIdGnrService uUIdGenerationServiceWithoutAddress;

    @Resource(name = "UUIdGenerationServiceWithIP")
    private EgovIdGnrService uUIdGenerationServiceWithIP;

    /**
     * Mac Address 세팅 테스트
     * @throws Exception
     *         fail to test
     */
    @Test
    public void testUUIdGeneration() throws Exception {

        // 1. get next String id
        for (int i = 0; i < 10; i++) {
        	System.out.println(uUidGenerationService.getNextStringId());
            assertNotNull(uUidGenerationService.getNextStringId());
        }
        // 2. get next BigDecimal id
        for (int i = 0; i < 10; i++) {
            assertNotNull(uUidGenerationService.getNextBigDecimalId());
            System.out.println(uUidGenerationService.getNextBigDecimalId());
        }
    }

    /**
     * Mac Address 세팅없이 테스트
     * @throws Exception
     *         fail to test
     */
    @Test
    public void testUUIdGenerationNoAddress() throws Exception {

        // 1. get next String id
        for (int i = 0; i < 10; i++) {
            assertNotNull(uUIdGenerationServiceWithoutAddress.getNextStringId());
        }
        // 2. get next BigDecimal id
        for (int i = 0; i < 10; i++) {
            assertNotNull(uUIdGenerationServiceWithoutAddress
                .getNextBigDecimalId());
        }
    }

    /**
     * IP 세팅 테스트
     * @throws Exception
     *         fail to test
     */
    @Test
    public void testUUIdGenerationIP() throws Exception {

        // 1. get next String id
        for (int i = 0; i < 10; i++) {
            assertNotNull(uUIdGenerationServiceWithIP.getNextStringId());
        }
        // 2. get next BigDecimal id
        for (int i = 0; i < 10; i++) {
            assertNotNull(uUIdGenerationServiceWithIP.getNextBigDecimalId());
        }
    }

    /**
     * UUID Generation Service는
     * getNextStringId,getNextBigDecimalId 만 제공.
     * @throws Exception
     *         fail to test
     */
    @Test
    public void testNotSupported() throws Exception {

        // 1. get next byte id
        try {
            uUidGenerationService.getNextByteId();
        } catch (Exception e) {
            assertTrue(e instanceof FdlException);
        }

        // 2. get next integer id
        try {
            uUidGenerationService.getNextIntegerId();
        } catch (Exception e) {
            assertTrue(e instanceof FdlException);
        }

        // 3. get next long id
        try {
            uUidGenerationService.getNextLongId();
        } catch (Exception e) {
            assertTrue(e instanceof FdlException);
        }

        // 4. get next short id
        try {
            uUidGenerationService.getNextShortId();
        } catch (Exception e) {
            assertTrue(e instanceof FdlException);
        }

        // 5. get next string id with a specific
        // strategy
        try {
            uUidGenerationService.getNextStringId("mixPrefix");
        } catch (Exception e) {
            assertTrue(e instanceof FdlException);
        }

        // 6. get next string id with a specific
        // strategy
        try {
            uUidGenerationService.getNextStringId(new EgovIdGnrStrategyImpl());
        } catch (Exception e) {
            assertTrue(e instanceof FdlException);
        }
    }
}

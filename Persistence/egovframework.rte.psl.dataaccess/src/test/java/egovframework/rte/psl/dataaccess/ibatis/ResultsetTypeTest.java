package egovframework.rte.psl.dataaccess.ibatis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.ibatis.common.util.PaginatedList;

import egovframework.rte.psl.dataaccess.TestBase;
import egovframework.rte.psl.dataaccess.dao.EmpDAO;
import egovframework.rte.psl.dataaccess.vo.EmpVO;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/context-*.xml" })
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = false)
@Transactional
public class ResultsetTypeTest extends TestBase {

    @Resource(name = "empDAO")
    EmpDAO empDAO;

    @Before
    public void onSetUp() throws Exception {
        SimpleJdbcTestUtils.executeSqlScript(
            new SimpleJdbcTemplate(dataSource), new ClassPathResource(
                "META-INF/testdata/sample_schema_ddl_" + usingDBMS + ".sql"),
            true);

        // init data
        SimpleJdbcTestUtils.executeSqlScript(
            new SimpleJdbcTemplate(dataSource), new ClassPathResource(
                "META-INF/testdata/sample_schema_initdata_" + usingDBMS
                    + ".sql"), true);
    }

    @SuppressWarnings( {"unchecked" })
    @Test
    public void testResultSetType() throws Exception {
        // 검색조건 없이 전체 EMP 리스트 조회

        // select
        List<EmpVO> resultList =
            empDAO.selectEmpList("selectEmpUsingResultSetType", null);

        // check
        assertNotNull(resultList);
        assertEquals(14, resultList.size());
        assertEquals(new BigDecimal(7369), resultList.get(0).getEmpNo());
        assertEquals(new BigDecimal(7499), resultList.get(1).getEmpNo());
        assertEquals(new BigDecimal(7521), resultList.get(2).getEmpNo());
        assertEquals(new BigDecimal(7566), resultList.get(3).getEmpNo());
        assertEquals(new BigDecimal(7654), resultList.get(4).getEmpNo());
        assertEquals(new BigDecimal(7698), resultList.get(5).getEmpNo());
        assertEquals(new BigDecimal(7782), resultList.get(6).getEmpNo());
        assertEquals(new BigDecimal(7788), resultList.get(7).getEmpNo());
        assertEquals(new BigDecimal(7839), resultList.get(8).getEmpNo());
        assertEquals(new BigDecimal(7844), resultList.get(9).getEmpNo());
        assertEquals(new BigDecimal(7876), resultList.get(10).getEmpNo());
        assertEquals(new BigDecimal(7900), resultList.get(11).getEmpNo());
        assertEquals(new BigDecimal(7902), resultList.get(12).getEmpNo());
        assertEquals(new BigDecimal(7934), resultList.get(13).getEmpNo());

        // skip 5, max 3 부분 범위 조회
        resultList =
            empDAO.getSqlMapClientTemplate().queryForList(
                "selectEmpUsingResultSetType", 5, 3);

        assertNotNull(resultList);
        assertEquals(3, resultList.size());
        assertEquals(new BigDecimal(7698), resultList.get(0).getEmpNo());
        assertEquals(new BigDecimal(7782), resultList.get(1).getEmpNo());
        assertEquals(new BigDecimal(7788), resultList.get(2).getEmpNo());

    }

    @SuppressWarnings( {"unchecked" })
    @Test
    public void testResultSetTypeScrollInsensitive() throws Exception {
        // 검색조건 없이 전체 EMP 리스트 조회

        // select
        List<EmpVO> resultList =
            empDAO.selectEmpList(
                "selectEmpUsingResultSetTypeScrollInsensitive", null);

        // check
        assertNotNull(resultList);
        assertEquals(14, resultList.size());
        assertEquals(new BigDecimal(7369), resultList.get(0).getEmpNo());
        assertEquals(new BigDecimal(7499), resultList.get(1).getEmpNo());
        assertEquals(new BigDecimal(7521), resultList.get(2).getEmpNo());
        assertEquals(new BigDecimal(7566), resultList.get(3).getEmpNo());
        assertEquals(new BigDecimal(7654), resultList.get(4).getEmpNo());
        assertEquals(new BigDecimal(7698), resultList.get(5).getEmpNo());
        assertEquals(new BigDecimal(7782), resultList.get(6).getEmpNo());
        assertEquals(new BigDecimal(7788), resultList.get(7).getEmpNo());
        assertEquals(new BigDecimal(7839), resultList.get(8).getEmpNo());
        assertEquals(new BigDecimal(7844), resultList.get(9).getEmpNo());
        assertEquals(new BigDecimal(7876), resultList.get(10).getEmpNo());
        assertEquals(new BigDecimal(7900), resultList.get(11).getEmpNo());
        assertEquals(new BigDecimal(7902), resultList.get(12).getEmpNo());
        assertEquals(new BigDecimal(7934), resultList.get(13).getEmpNo());

        // skip 5, max 3 부분 범위 조회
        resultList =
            empDAO.getSqlMapClientTemplate().queryForList(
                "selectEmpUsingResultSetTypeScrollInsensitive", 5, 3);

        assertNotNull(resultList);
        assertEquals(3, resultList.size());
        assertEquals(new BigDecimal(7698), resultList.get(0).getEmpNo());
        assertEquals(new BigDecimal(7782), resultList.get(1).getEmpNo());
        assertEquals(new BigDecimal(7788), resultList.get(2).getEmpNo());


    }

    @SuppressWarnings( {"unchecked" })
    @Test
    public void testResultSetTypeScrollSensitive() throws Exception {
        // 검색조건 없이 전체 EMP 리스트 조회

        List<EmpVO> resultList;

        // select
        // try {
        resultList =
            empDAO.selectEmpList("selectEmpUsingResultSetTypeScrollSensitive",
                null);

        // if (isOracle) {
        // fail("Oracle 인 경우 SCROLL_SENSITIVE 인 resultSetType 설정을 하면 에러가 납니다.");
        // }

        // check
        assertNotNull(resultList);
        assertEquals(14, resultList.size());
        assertEquals(new BigDecimal(7369), resultList.get(0).getEmpNo());
        assertEquals(new BigDecimal(7499), resultList.get(1).getEmpNo());
        assertEquals(new BigDecimal(7521), resultList.get(2).getEmpNo());
        assertEquals(new BigDecimal(7566), resultList.get(3).getEmpNo());
        assertEquals(new BigDecimal(7654), resultList.get(4).getEmpNo());
        assertEquals(new BigDecimal(7698), resultList.get(5).getEmpNo());
        assertEquals(new BigDecimal(7782), resultList.get(6).getEmpNo());
        assertEquals(new BigDecimal(7788), resultList.get(7).getEmpNo());
        assertEquals(new BigDecimal(7839), resultList.get(8).getEmpNo());
        assertEquals(new BigDecimal(7844), resultList.get(9).getEmpNo());
        assertEquals(new BigDecimal(7876), resultList.get(10).getEmpNo());
        assertEquals(new BigDecimal(7900), resultList.get(11).getEmpNo());
        assertEquals(new BigDecimal(7902), resultList.get(12).getEmpNo());
        assertEquals(new BigDecimal(7934), resultList.get(13).getEmpNo());

        // skip 5, max 3 부분 범위 조회
        resultList =
            empDAO.getSqlMapClientTemplate().queryForList(
                "selectEmpUsingResultSetTypeScrollSensitive", 5, 3);

        assertNotNull(resultList);
        assertEquals(3, resultList.size());
        assertEquals(new BigDecimal(7698), resultList.get(0).getEmpNo());
        assertEquals(new BigDecimal(7782), resultList.get(1).getEmpNo());
        assertEquals(new BigDecimal(7788), resultList.get(2).getEmpNo());

    }

}

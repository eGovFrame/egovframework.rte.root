package egovframework.rte.psl.dataaccess.ibatis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Properties;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import egovframework.rte.psl.dataaccess.TestBase;
import egovframework.rte.psl.dataaccess.dao.EmpDAO;
import egovframework.rte.psl.dataaccess.rowhandler.FileWritingRowHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/context-*.xml" })
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = false)
@Transactional
public class RowHandlerTest extends TestBase {

    @Resource(name = "schemaProperties")
    Properties schemaProperties;

    @Resource(name = "empDAO")
    EmpDAO empDAO;

    // fileWritingRowHandler 는 prototype 으로 선언했음
    @Resource(name = "fileWritingRowHandler")
    FileWritingRowHandler rowHandler;

    boolean isHsql = true;

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

    @Test
    public void testRowHandlerForOutFileWriting() throws Exception {

        // select to outFile using rowHandler
        empDAO.getSqlMapClientTemplate().queryWithRowHandler(
            "selectEmpListToOutFileUsingRowHandler", null, rowHandler);

        // check
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        org.springframework.core.io.Resource resource =
            resourceLoader
                .getResource("file:./src/test/resources/META-INF/testdata/"
                    + schemaProperties.getProperty("outResultFile"));
        // BufferedOutputStream flush 및 close
        rowHandler.releaseResource();

        assertEquals(38416, rowHandler.getTotalCount());

        File file = resource.getFile();
        assertNotNull(file);
        // 대용량 out file size 체크
        assertTrue(1000000 < file.length());
    }

}

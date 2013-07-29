package egovframework.rte.fdl.excel;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * FileServiceTest is TestCase of File Handling Service
 * @author Seongjong Yoon
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/context-*.xml" })
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = false)
@Transactional
public class EgovExcelUploadTest extends
		AbstractDependencyInjectionSpringContextTests {

	protected static String usingDBMS = "hsql";

    @Resource(name = "jdbcProperties")
    protected Properties jdbcProperties;

    @Resource(name = "dataSource")
    protected DataSource dataSource;

    
	@Resource(name = "excelService")
    private EgovExcelService excelService;
	
	@Resource(name = "excelBigService")
    private EgovExcelService excelBigService;
	
	private static final Log log = LogFactory.getLog(EgovExcelUploadTest.class);

    @Before
    public void onSetUp() throws Exception {
        SimpleJdbcTestUtils.executeSqlScript(
            new SimpleJdbcTemplate(dataSource), new ClassPathResource(
                "META-INF/testdata/sample_schema_ddl_" + usingDBMS + ".sql"),
            true);
    }

    @Test
    public void testUploadExcelFile() throws Exception {
        
        try {
        	log.debug("testUploadExcelFile start....");

        	FileInputStream fileIn = new FileInputStream(new File("testdata/testBatch.xls"));
        	excelService.uploadExcel("insertEmpUsingBatch", fileIn);
        	
        } catch (Exception e) {
        	log.error(e.toString());
        	throw new Exception(e);
        } finally {
        	log.debug("testUploadExcelFile end....");
        }
    }
    
    /**
     * 대용량 엑셀파일 업로드
     * @throws Exception
     */
    @Test
    public void testBigUploadExcelFile() throws Exception {
        
        try {
        	log.debug("testBigUploadExcelFile start....");

        	FileInputStream fileIn = new FileInputStream(new File("testdata/zipExcel.xls"));
        	excelBigService.uploadExcel("insertZipUsingBatch", fileIn, 2, (long)5000);
        	
        } catch (Exception e) {
        	log.error("Exception - Runtime.getRuntime().freeMemory() : " + Runtime.getRuntime().freeMemory());
        	log.error(e.toString());
        	throw new Exception(e);
        } finally {
        	log.debug("testBigUploadExcelFile end....");
        }
    }
	
}
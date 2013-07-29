/*
 * Copyright 2002-2008 MOPAS(Ministry of Public Administration and Security).
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
package egovframework.rte.fdl.excel;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFOddHeader;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import egovframework.rte.fdl.excel.util.EgovExcelUtil;
import egovframework.rte.fdl.excel.vo.PersonHourVO;
import egovframework.rte.fdl.filehandling.EgovFileUtil;

/**
 * FileServiceTest is TestCase of File Handling Service
 * @author Seongjong Yoon
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/spring/context-*.xml" })
public class EgovExcelXSSFServiceTest extends
		AbstractDependencyInjectionSpringContextTests {

	@Resource(name = "excelService")
    private EgovExcelService excelService;
	private String fileLocation;
	
	private static final Log log = LogFactory.getLog(EgovExcelXSSFServiceTest.class);

    @Before
    public void onSetUp() throws Exception {
    	this.fileLocation = "testdata";
    }
	
    /**
     * [Flow #-1] 엑셀 파일 생성 : 새 엑셀 파일을 생성함
     */
    @Test
    public void testWriteExcelFile() throws Exception {
        
        try {
        	log.debug("testWriteExcelFile start....");

        	String sheetName1 = "first sheet";
        	String sheetName2 = "second sheet";
        	StringBuffer sb = new StringBuffer();
        	sb.append(fileLocation).append("/").append("testWriteExcelFile.xlsx");

        	// delete file
        	if (EgovFileUtil.isExistsFile(sb.toString())) {
        		EgovFileUtil.delete(new File(sb.toString()));
        		log.debug("Delete file...." + sb.toString());
        	}
        	
        	XSSFWorkbook wb = new XSSFWorkbook();
    	
        	wb.createSheet(sheetName1);
            wb.createSheet(sheetName2);
        	wb.createSheet();

        	// 엑셀 파일 생성
        	Workbook tmp = excelService.createXSSFWorkbook(wb, sb.toString());
        	
        	// 파일 존재 확인
        	assertTrue(EgovFileUtil.isExistsFile(sb.toString()));
        	
        	// 저장된 Sheet명 일치 점검
        	assertEquals(sheetName1, tmp.getSheetName(0));
        	assertEquals(sheetName2, tmp.getSheetName(1));

        } catch (Exception e) {
        	log.error(e.toString());
        	throw new Exception(e);
        } finally {
        	log.debug("testWriteExcelFile end....");
        }
    }
	

    /**
     * [Flow #-2] 액셀 파일 수정 : 엑샐 파일 내 셀의 내용을 변경하고 저장할 수 있음
     */
    @Test
    public void testModifyCellContents() throws Exception {
        
        try {
        	String content = "Use \n with word wrap on to create a new line";
        	short rownum = 2;
        	int cellnum = 2;
        	
        	log.debug("testModifyCellContents start....");

        	StringBuffer sb = new StringBuffer();
        	sb.append(fileLocation).append("/").append("testModifyCellContents.xlsx");

        	if (!EgovFileUtil.isExistsFile(sb.toString())) {
        		XSSFWorkbook wbT = new XSSFWorkbook();
	        	wbT.createSheet();
	
	            // 엑셀 파일 생성
	            excelService.createXSSFWorkbook(wbT, sb.toString());
        	}
        	
            // 엑셀 파일 로드
            XSSFWorkbook wb = excelService.loadXSSFWorkbook(sb.toString());
            log.debug("testModifyCellContents after loadWorkbook....");
            
            XSSFSheet sheet = wb.getSheetAt(0);
            Font f2 = wb.createFont();
            CellStyle cs = wb.createCellStyle();
            cs = wb.createCellStyle();
            
            cs.setFont( f2 );
            //Word Wrap MUST be turned on
            cs.setWrapText( true );
            
            XSSFRow row = sheet.createRow( rownum );
            row.setHeight( (short) 0x349 );
            XSSFCell cellx = row.createCell( cellnum );
            cellx.setCellType( SXSSFCell.CELL_TYPE_STRING );
            cellx.setCellValue( new XSSFRichTextString(content) );
            cellx.setCellStyle( cs );
            
            sheet.setColumnWidth( 20, (int) ( ( 50 * 8 ) / ( (double) 1 / 20 ) ) );
            
            //excelService.writeWorkbook(wb);
            
            FileOutputStream outx = new FileOutputStream(sb.toString());
            wb.write(outx);
            outx.close();
            
            // 엑셀 파일 로드
            Workbook wb1 = excelService.loadXSSFWorkbook(sb.toString());
            
            Sheet sheet1 = wb1.getSheetAt(0);
            Row row1 = sheet1.getRow(rownum);
            Cell cell1 = row1.getCell( cellnum );
            
            // 수정된 셀의 내용 점검
            log.debug("cell ###" + cell1.getRichStringCellValue() + "###");
            log.debug("cont ###" + content + "###");
            
            assertNotSame("TEST", cell1.getRichStringCellValue().toString());
            assertEquals(content, cell1.getRichStringCellValue().toString());
            
        } catch (Exception e) {
        	log.error(e.toString());
        	throw new Exception(e);
        } finally {
        	log.debug("testModifyCellContents end....");
        }
    }
    
    
    /**
     * [Flow #-3] 엑셀 파일 속성 수정 : 엑셀 파일의 속성(셀의 크기, Border의 속성, 셀의 색상, 정렬 등)을 수정함
     */
    @Test
    public void testWriteExcelFileAttribute() throws Exception {
        
        try {
        	log.debug("testWriteExcelFileAttribute start....");

        	short rowheight = 40;
        	int columnwidth = 30;
        	
        	StringBuffer sb = new StringBuffer();
        	sb.append(fileLocation).append("/").append("testWriteExcelFileAttribute.xlsx");        	

        	// delete file
        	if (EgovFileUtil.isExistsFile(sb.toString())) {
        		EgovFileUtil.delete(new File(sb.toString()));
        	
        		log.debug("Delete file...." + sb.toString());
        	}
        	
        	XSSFWorkbook wb = new XSSFWorkbook();
        	
        	XSSFSheet sheet1 = wb.createSheet("new sheet");
            wb.createSheet("second sheet");
            
            // 셀의 크기
        	sheet1.setDefaultRowHeight(rowheight);
        	sheet1.setDefaultColumnWidth(columnwidth);
        	
        	XSSFFont f2 = wb.createFont();
        	XSSFCellStyle cs = wb.createCellStyle();
        	cs = wb.createCellStyle();
        	
            cs.setFont( f2 );
            cs.setWrapText( true );
         
            // 정렬
            cs.setAlignment(XSSFCellStyle.ALIGN_RIGHT);

            cs.setFillPattern(XSSFCellStyle.DIAMONDS); // 무늬 스타일
            
            // 셀의 색상
            cs.setFillForegroundColor(new XSSFColor(Color.BLUE).getIndexed()); // 무늬 색
            cs.setFillBackgroundColor(new XSSFColor(Color.RED).getIndexed());  // 배경색
            
        	sheet1.setDefaultColumnStyle((short) 0, cs);
        	
            XSSFWorkbook tmp = excelService.createXSSFWorkbook(wb, sb.toString());

            XSSFSheet sheetTmp1 = tmp.getSheetAt(0);

            assertEquals(rowheight, sheetTmp1.getDefaultRowHeight());
            assertEquals(columnwidth, sheetTmp1.getDefaultColumnWidth());
            
            XSSFCellStyle cs1 = tmp.getCellStyleAt((short)(tmp.getNumCellStyles() - 1));

            log.debug("getAlignment : " + cs1.getAlignment());
        	assertEquals(XSSFCellStyle.ALIGN_RIGHT, cs1.getAlignment());
        	
        	log.debug("getFillPattern : " + cs1.getFillPattern());
        	assertEquals(XSSFCellStyle.DIAMONDS, cs1.getFillPattern());
        	
        	log.debug("getFillForegroundColor : " + cs1.getFillForegroundColor());
        	log.debug("getFillBackgroundColor : " + cs1.getFillBackgroundColor());
        	assertEquals(new XSSFColor(Color.BLUE).getIndexed(), cs1.getFillForegroundColor());
        	assertEquals(new XSSFColor(Color.RED).getIndexed(), cs1.getFillBackgroundColor());        	


        } catch (Exception e) {
        	log.error(e.toString());
        	throw new Exception(e);
        } finally {
        	log.debug("testWriteExcelFileAttribute end....");
        }
    }
    
    /**
     * [Flow #-4] 엑셀 문서 속성 수정 : 엑셀 파일 문서의 속성(Header, Footer)을 수정함
     */
    @Test
    public void testModifyDocAttribute() throws Exception {
        
        try {
        	log.debug("testModifyDocAttribute start....");

        	StringBuffer sb = new StringBuffer();
        	sb.append(fileLocation).append("/").append("testModifyDocAttribute.xlsx");        	


        	if (EgovFileUtil.isExistsFile(sb.toString())) {
        		EgovFileUtil.delete(new File(sb.toString()));
        	
        		log.debug("Delete file...." + sb.toString());
        	}

        	XSSFWorkbook wbTmp = new XSSFWorkbook();
        	wbTmp.createSheet();

            // 엑셀 파일 생성
            excelService.createXSSFWorkbook(wbTmp, sb.toString());


        	// 엑셀 파일 로드
        	XSSFWorkbook wb = excelService.loadXSSFWorkbook(sb.toString());
        	log.debug("testModifyCellContents after loadWorkbook....");

        	XSSFSheet sheet = wb.createSheet("doc test sheet");

        	XSSFRow row = sheet.createRow(1);
        	XSSFCell cell = row.createCell(1);
        	cell.setCellValue(new XSSFRichTextString("Header/Footer Test"));
        	
        	// Header
        	XSSFOddHeader header = (XSSFOddHeader) sheet.getHeader();
            header.setCenter("Center Header");
            header.setLeft("Left Header");
            header.setRight(XSSFOddHeader.stripFields("&IRight Stencil-Normal Italic font and size 16"));
            
            // Footer
            XSSFOddHeader footer = (XSSFOddHeader) sheet.getFooter();
            footer.setCenter(XSSFOddHeader.stripFields("Fixedsys"));
            log.debug("Style is ... " + XSSFOddHeader.stripFields("Fixedsys"));
            footer.setLeft("Left Footer");
            footer.setRight("Right Footer");
            
            // 엑셀 파일 저장
            FileOutputStream out = new FileOutputStream(sb.toString());
            wb.write(out);
            out.close();

            assertTrue(EgovFileUtil.isExistsFile(sb.toString()));
            
            //////////////////////////////////////////////////////////////////////////
            // 검증
            XSSFWorkbook wbT = excelService.loadXSSFWorkbook(sb.toString());
            XSSFSheet sheetT = wbT.getSheet("doc test sheet");

            Header headerT = sheetT.getHeader();
            assertEquals("Center Header", headerT.getCenter());
            assertEquals("Left Header", headerT.getLeft());
            assertEquals(XSSFOddHeader.stripFields("Right Stencil-Normal Italic font and size 16"), headerT.getRight());
            
            Footer footerT = sheetT.getFooter();
            assertEquals("Right Footer", footerT.getRight());
            assertEquals("Left Footer", footerT.getLeft());
            assertEquals(XSSFOddHeader.stripFields("Fixedsys"), footerT.getCenter());

        } catch (Exception e) {
        	log.error(e.toString());
        	throw new Exception(e);
        } finally {
        	log.debug("testModifyDocAttribute end....");
        }
    }
    
    /**
     * [Flow #-5] 셀 내용 추출 : 엑셀 파일을 읽어 특정 셀의 값을 얻어 옴
     */
    @Test
    public void testGetCellContents() throws Exception {
        
        try {
        	log.debug("testGetCellContents start....");

        	StringBuffer sb = new StringBuffer();
        	sb.append(fileLocation).append("/").append("testGetCellContents.xlsx");        	

        	if (EgovFileUtil.isExistsFile(sb.toString())) {
        		EgovFileUtil.delete(new File(sb.toString()));
        	
        		log.debug("Delete file...." + sb.toString());
        	}
        	
        	XSSFWorkbook wbTmp = new XSSFWorkbook();
        	wbTmp.createSheet();

            // 엑셀 파일 생성
            excelService.createXSSFWorkbook(wbTmp, sb.toString());

        	// 엑셀 파일 로드
        	XSSFWorkbook wb = excelService.loadXSSFWorkbook(sb.toString());
        	log.debug("testGetCellContents after loadWorkbook....");

        	XSSFSheet sheet = wb.createSheet("cell test sheet");

        	XSSFCellStyle cs = wb.createCellStyle();
        	cs = wb.createCellStyle();
            cs.setWrapText( true );
            
        	for (int i = 0; i < 100; i++) {
	        	XSSFRow row = sheet.createRow(i);
	        	for (int j = 0; j < 5; j++) {
		        	XSSFCell cell = row.createCell(j);
		        	cell.setCellValue(new XSSFRichTextString("row " + i + ", cell " + j));
		        	cell.setCellStyle( cs );
	        	}
        	}

        	// 엑셀 파일 저장
        	FileOutputStream out = new FileOutputStream(sb.toString());
            wb.write(out);
            out.close();
            
        	//////////////////////////////////////////////////////////////////////////
            // 검증
            XSSFWorkbook wbT = excelService.loadXSSFWorkbook(sb.toString());
            XSSFSheet sheetT = wbT.getSheet("cell test sheet");

            for (int i = 0; i < 100; i++) {
	        	XSSFRow row1 = sheetT.getRow(i);
	            for (int j = 0; j < 5; j++) {
	            	XSSFCell cell1 = row1.getCell(j);
	            	log.debug("row " + i + ", cell " + j + " : " + cell1.getRichStringCellValue());
		            assertEquals("row " + i + ", cell " + j, cell1.getRichStringCellValue().toString());
	            }
            }

        } catch (Exception e) {
        	log.error(e.toString());
        	throw new Exception(e);
        } finally {
        	log.debug("testGetCellContents end....");
        }
    }

    /**
     * [Flow #-6] 셀 속성 수정 : 특정 셀의 속성(폰트, 사이즈 등)을 수정함
     */
    @Test
    public void testModifyCellAttribute() throws Exception {
        
        try {
        	log.debug("testModifyCellAttribute start....");

        	StringBuffer sb = new StringBuffer();
        	sb.append(fileLocation).append("/").append("testModifyCellAttribute.xlsx");        	

        	if (EgovFileUtil.isExistsFile(sb.toString())) {
        		EgovFileUtil.delete(new File(sb.toString()));
        	
        		log.debug("Delete file...." + sb.toString());
        	}
        	
        	XSSFWorkbook wbTmp = new XSSFWorkbook();
        	wbTmp.createSheet();

            // 엑셀 파일 생성
            excelService.createXSSFWorkbook(wbTmp, sb.toString());

        	// 엑셀 파일 로드
        	XSSFWorkbook wb = excelService.loadXSSFWorkbook(sb.toString());
        	log.debug("testModifyCellAttribute after loadWorkbook....");

        	XSSFSheet sheet = wb.createSheet("cell test sheet2");
//        	sheet.setColumnWidth((short) 3, (short) 200);	// column Width
        	
        	XSSFCellStyle cs = wb.createCellStyle();
        	XSSFFont font = wb.createFont();
        	font.setFontHeight((short) 16);
        	font.setBoldweight((short) 3);
        	font.setFontName("fixedsys");
        	
            cs.setFont(font);
        	cs.setAlignment(XSSFCellStyle.ALIGN_RIGHT);	// cell 정렬
            cs.setWrapText( true );

        	for (int i = 0; i < 100; i++) {
	        	XSSFRow row = sheet.createRow(i);
//	        	row.setHeight((short)300); // row의 height 설정
	        	
	        	for (int j = 0; j < 5; j++) {
		        	XSSFCell cell = row.createCell(j);
		        	cell.setCellValue(new XSSFRichTextString("row " + i + ", cell " + j));
		        	cell.setCellStyle( cs );
	        	}
        	}

        	// 엑셀 파일 저장
        	FileOutputStream out = new FileOutputStream(sb.toString());
            wb.write(out);
            out.close();
            
        	//////////////////////////////////////////////////////////////////////////
            // 검증
            XSSFWorkbook wbT = excelService.loadXSSFWorkbook(sb.toString());
            XSSFSheet sheetT = wbT.getSheet("cell test sheet2");
            log.debug("getNumCellStyles : " + wbT.getNumCellStyles());

        	XSSFCellStyle cs1 = wbT.getCellStyleAt((short)(wbT.getNumCellStyles() - 1));

            XSSFFont fontT = cs1.getFont();
            log.debug("font getFontHeight : " + fontT.getFontHeight());
            log.debug("font getBoldweight : " + fontT.getBoldweight());
            log.debug("font getFontName : " + fontT.getFontName());
            log.debug("getAlignment : " + cs1.getAlignment());
            log.debug("getWrapText : " + cs1.getWrapText());
        	 
            for (int i = 0; i < 100; i++) {
	        	XSSFRow row1 = sheetT.getRow(i);
	            for (int j = 0; j < 5; j++) {
	            	XSSFCell cell1 = row1.getCell(j);
	            	log.debug("row " + i + ", cell " + j + " : " + cell1.getRichStringCellValue());
		            assertEquals(16, fontT.getFontHeight());
		            assertEquals(3, fontT.getBoldweight());
		            assertEquals(XSSFCellStyle.ALIGN_RIGHT, cs1.getAlignment());
		            assertTrue(cs1.getWrapText());
	            }
            }

        } catch (Exception e) {
        	log.error(e.toString());
        	throw new Exception(e);
        } finally {
        	log.debug("testModifyCellAttribute end....");
        }
    }
    
    /**
     * [Flow #-7] 공통 템플릿 사용 : 공통 템플릿을 사용하여 일관성을 유지함
     */
    @Test
    public void testUseTemplate1() throws Exception {
    	
    	StringBuffer sb = new StringBuffer();
    	StringBuffer sbResult = new StringBuffer();

    	sb.append(fileLocation).append("/template/").append("template.xlsx");   
    	sbResult.append(fileLocation).append("/").append("testUseTemplate1.xlsx");

    	Object[][] sample_data = {
            {"Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0},
            {"Gisella Bronzetti", "GB", 4.0, 3.0, 1.0, 3.5, null, null, 4.0},
    	};

        try {
        	
        	XSSFWorkbook wb = excelService.loadXSSFExcelTemplate(sb.toString());
        	XSSFSheet sheet = wb.getSheetAt(0);
        	
            // set data
        	for (int i = 0; i < sample_data.length; i++) {
                XSSFRow row = sheet.getRow(2 + i);
                for (int j = 0; j < sample_data[i].length; j++) {
                    if(sample_data[i][j] == null) continue;

                    XSSFCell cell = row.getCell(j);
                    
                    if(sample_data[i][j] instanceof String) {
                    	cell.setCellValue(new XSSFRichTextString((String)sample_data[i][j]));
                    } else {
                    	cell.setCellValue((Double)sample_data[i][j]);
                    }
                }
            }

        	// 수식 재계산
        	sheet.setForceFormulaRecalculation(true);
        	
        	excelService.createXSSFWorkbook(wb, sbResult.toString());
        	
        	//////////////////////////////////////////////////////////////////////////
            // 검증
            XSSFWorkbook wbT = excelService.loadXSSFWorkbook(sbResult.toString());
            XSSFSheet sheetT = wbT.getSheetAt(0);

            for (int i = 0; i < sample_data.length; i++) {
                XSSFRow row = sheetT.getRow(2 + i);
                for (int j = 0; j < sample_data[i].length; j++) {
                	XSSFCell cell = row.getCell(j);

                	
                	log.debug("sample_data[i][j] : " + sample_data[i][j]);

                	if (sample_data[i][j] == null) {
                		assertEquals(cell.getCellType(), XSSFCell.CELL_TYPE_BLANK);
                	} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                		assertEquals((Double)sample_data[i][j], Double.valueOf(cell.getNumericCellValue()));
                	} else {
                		assertEquals((String)sample_data[i][j], cell.getRichStringCellValue().getString());
                	}
                }
            }
            
        } catch (Exception e) {
        	log.error(e.toString());
        	throw new Exception(e);
        } finally {
        	log.debug("testUseTemplate end....");
        }
    }
    
    /**
     * [Flow #-7-1] 공통 템플릿 사용 : 공통 템플릿을 사용하여 일관성을 유지함
     * jXLS 사용
     */
    @Test
    public void testUseTemplate2() throws Exception {
    	StringBuffer sb = new StringBuffer();
    	StringBuffer sbResult = new StringBuffer();

    	sb.append(fileLocation).append("/template/").append("template2.xlsx");
    	sbResult.append(fileLocation).append("/").append("testUseTemplate2.xlsx");
    	
        try {
        	// 출력할 객체를 만든다.
            List<PersonHourVO> persons = new ArrayList<PersonHourVO>();
            PersonHourVO person = new PersonHourVO();
            person.setName("Yegor Kozlov");
            person.setId("YK");
            person.setMon(5.0);
            person.setTue(8.0);
            person.setWed(10.0);
            person.setThu(5.0);
            person.setFri(5.0);
            person.setSat(7.0);
            person.setSun(6.0);
            
            persons.add(person); 
            
            PersonHourVO person1 = new PersonHourVO();
            person1.setName("Gisella Bronzetti");
            person1.setId("GB");
            person1.setMon(4.0);
            person1.setTue(3.0);
            person1.setWed(1.0);
            person1.setThu(3.5);
            person1.setSun(4.0);
            
            persons.add(person1); 
           
            Map<String, Object> beans = new HashMap<String, Object>();
            beans.put("persons", persons);
            XLSTransformer transformer = new XLSTransformer();

            transformer.transformXLS(sb.toString(), beans, sbResult.toString());
            
            //////////////////////////////////////////////////////////////////////////
            // 검증
            XSSFWorkbook wb = excelService.loadXSSFWorkbook(sbResult.toString());
            XSSFSheet sheet = wb.getSheetAt(0);

            Double[][] value = {
            		{5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0},
            		{4.0, 3.0, 1.0, 3.5, null, null, 4.0}
            };

            for (int i = 0; i < 2; i++) {
            	XSSFRow row2 = sheet.getRow(i + 2);
            	
            	for (int j = 0; j < 7; j++) {
            		 XSSFCell cellValue = row2.getCell((j + 2));
            		 if (cellValue.getCellType() == HSSFCell.CELL_TYPE_BLANK) continue;
		            log.debug("cellTot.getNumericCellValue() : " + cellValue.getNumericCellValue());
		            assertEquals(value[i][j], Double.valueOf(cellValue.getNumericCellValue()));
            	}
            }

        } catch (Exception e) {
        	log.error(e.toString());
        	throw new Exception(e);
        } finally {
        	log.debug("testUseTemplate end....");
        }
    }

    /**
     * 셀 Data type 테스트
     *   셀의 값이 Null 인경우 오류발생
     */
    @Test
    public void testCellDataFormat() throws Exception {
    	StringBuffer sb = new StringBuffer();
    	sb.append(fileLocation).append("/").append("testDataFormat.xlsx");
    
        XSSFWorkbook wb = excelService.loadXSSFWorkbook(sb.toString());
        XSSFSheet sheet = wb.getSheetAt(0);
        
        XSSFRow row = sheet.getRow(7);
    	XSSFCell cell = row.getCell(0);

    	assertEquals("2009/04/01", EgovExcelUtil.getValue(cell));

    	row = sheet.getRow(8);
    	cell = row.getCell(0);

    	assertEquals("2009/04/02", EgovExcelUtil.getValue(cell));
    }
}
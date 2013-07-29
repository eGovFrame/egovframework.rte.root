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
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
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
public class EgovExcelServiceTest extends
		AbstractDependencyInjectionSpringContextTests {

	@Resource(name = "excelService")
    private EgovExcelService excelService;
	private String fileLocation;
	
	private static final Log log = LogFactory.getLog(EgovExcelServiceTest.class);

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
        	sb.append(fileLocation).append("/").append("testWriteExcelFile.xls");

        	// delete file
        	if (EgovFileUtil.isExistsFile(sb.toString())) {
        		EgovFileUtil.delete(new File(sb.toString()));
        		log.debug("Delete file...." + sb.toString());
        	}
        	
        	HSSFWorkbook wb = new HSSFWorkbook();
        	
        	wb.createSheet(sheetName1);
            wb.createSheet(sheetName2);
        	wb.createSheet();

            // 엑셀 파일 생성
            HSSFWorkbook tmp = excelService.createWorkbook(wb, sb.toString());

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
        	sb.append(fileLocation).append("/").append("testModifyCellContents.xls");

        	if (!EgovFileUtil.isExistsFile(sb.toString())) {
	        	HSSFWorkbook wbT = new HSSFWorkbook();
	        	wbT.createSheet();
	
	            // 엑셀 파일 생성
	            excelService.createWorkbook(wbT, sb.toString());
        	}

        	// 엑셀 파일 로드
        	HSSFWorkbook wb = excelService.loadWorkbook(sb.toString());
        	log.debug("testModifyCellContents after loadWorkbook....");
        	
        	HSSFSheet sheet = wb.getSheetAt(0);
            HSSFFont f2 = wb.createFont();
        	HSSFCellStyle cs = wb.createCellStyle();
        	cs = wb.createCellStyle();

            cs.setFont( f2 );
            //Word Wrap MUST be turned on
            cs.setWrapText( true );
            
        	HSSFRow row = sheet.createRow( rownum );
        	row.setHeight( (short) 0x349 );
            HSSFCell cell = row.createCell( cellnum );
            cell.setCellType( HSSFCell.CELL_TYPE_STRING );
            cell.setCellValue( new HSSFRichTextString(content) );
            cell.setCellStyle( cs );

            sheet.setColumnWidth( 20, (int) ( ( 50 * 8 ) / ( (double) 1 / 20 ) ) );

            //excelService.writeWorkbook(wb);
            
            FileOutputStream out = new FileOutputStream(sb.toString());
            wb.write(out);
            out.close();
            
            // 엑셀 파일 로드
            HSSFWorkbook wb1 = excelService.loadWorkbook(sb.toString());
            
            HSSFSheet sheet1 = wb1.getSheetAt(0);
            HSSFRow row1 = sheet1.getRow(rownum);
            HSSFCell cell1 = row1.getCell( cellnum );

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
        	sb.append(fileLocation).append("/").append("testWriteExcelFileAttribute.xls");        	

        	// delete file
        	if (EgovFileUtil.isExistsFile(sb.toString())) {
        		EgovFileUtil.delete(new File(sb.toString()));
        	
        		log.debug("Delete file...." + sb.toString());
        	}
        	
        	HSSFWorkbook wb = new HSSFWorkbook();
        	
        	HSSFSheet sheet1 = wb.createSheet("new sheet");
            wb.createSheet("second sheet");
            
            // 셀의 크기
        	sheet1.setDefaultRowHeight(rowheight);
        	sheet1.setDefaultColumnWidth(columnwidth);
        	
        	HSSFFont f2 = wb.createFont();
        	HSSFCellStyle cs = wb.createCellStyle();
        	cs = wb.createCellStyle();

            cs.setFont( f2 );
            cs.setWrapText( true );
         
            // 정렬
            cs.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

            cs.setFillPattern(HSSFCellStyle.DIAMONDS); // 무늬 스타일
            
            // 셀의 색상
            cs.setFillForegroundColor(new HSSFColor.BLUE().getIndex()); // 무늬 색
            cs.setFillBackgroundColor(new HSSFColor.RED().getIndex());  // 배경색
            
        	sheet1.setDefaultColumnStyle((short) 0, cs);
        	
            HSSFWorkbook tmp = excelService.createWorkbook(wb, sb.toString());

            HSSFSheet sheetTmp1 = tmp.getSheetAt(0);

            assertEquals(rowheight, sheetTmp1.getDefaultRowHeight());
            assertEquals(columnwidth, sheetTmp1.getDefaultColumnWidth());
            
            HSSFCellStyle cs1 = tmp.getCellStyleAt((short)(tmp.getNumCellStyles() - 1));

            log.debug("getAlignment : " + cs1.getAlignment());
        	assertEquals(HSSFCellStyle.ALIGN_RIGHT, cs1.getAlignment());
        	
        	log.debug("getFillPattern : " + cs1.getFillPattern());
        	assertEquals(HSSFCellStyle.DIAMONDS, cs1.getFillPattern());
        	
        	log.debug("getFillForegroundColor : " + cs1.getFillForegroundColor());
        	log.debug("getFillBackgroundColor : " + cs1.getFillBackgroundColor());
        	assertEquals(new HSSFColor.BLUE().getIndex(), cs1.getFillForegroundColor());
        	assertEquals(new HSSFColor.RED().getIndex(), cs1.getFillBackgroundColor());        	


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
        	sb.append(fileLocation).append("/").append("testModifyDocAttribute.xls");        	


        	if (EgovFileUtil.isExistsFile(sb.toString())) {
        		EgovFileUtil.delete(new File(sb.toString()));
        	
        		log.debug("Delete file...." + sb.toString());
        	}

        	HSSFWorkbook wbTmp = new HSSFWorkbook();
        	wbTmp.createSheet();

            // 엑셀 파일 생성
            excelService.createWorkbook(wbTmp, sb.toString());


        	// 엑셀 파일 로드
        	HSSFWorkbook wb = excelService.loadWorkbook(sb.toString());
        	log.debug("testModifyCellContents after loadWorkbook....");

        	HSSFSheet sheet = wb.createSheet("doc test sheet");

        	HSSFRow row = sheet.createRow(1);
        	HSSFCell cell = row.createCell(1);
        	cell.setCellValue(new HSSFRichTextString("Header/Footer Test"));

        	// Header
            HSSFHeader header = sheet.getHeader();
            header.setCenter("Center Header");
            header.setLeft("Left Header");
            header.setRight(HSSFHeader.font("Stencil-Normal", "Italic") +
                            HSSFHeader.fontSize((short) 16) + "Right Stencil-Normal Italic font and size 16");
            
            // Footer
            HSSFFooter footer = sheet.getFooter();
            footer.setCenter(HSSFHeader.font("Fixedsys", "Normal") + HSSFHeader.fontSize((short) 12) + "- 1 -");
            log.debug("Style is ... " + HSSFHeader.font("Fixedsys", "Normal") + HSSFHeader.fontSize((short) 12) + "- 1 -");
            footer.setLeft("Left Footer");
            footer.setRight("Right Footer");
            
            // 엑셀 파일 저장
            FileOutputStream out = new FileOutputStream(sb.toString());
            wb.write(out);
            out.close();

            assertTrue(EgovFileUtil.isExistsFile(sb.toString()));
            
            //////////////////////////////////////////////////////////////////////////
            // 검증
            HSSFWorkbook wbT = excelService.loadWorkbook(sb.toString());
            HSSFSheet sheetT = wbT.getSheet("doc test sheet");

            HSSFHeader headerT = sheetT.getHeader();
            assertEquals("Center Header", headerT.getCenter());
            assertEquals("Left Header", headerT.getLeft());
            assertEquals(HSSFHeader.font("Stencil-Normal", "Italic") + HSSFHeader.fontSize((short) 16) + 
            		"Right Stencil-Normal Italic font and size 16", headerT.getRight());
            
            HSSFFooter footerT = sheetT.getFooter();
            assertEquals("Right Footer", footerT.getRight());
            assertEquals("Left Footer", footerT.getLeft());
            assertEquals(HSSFHeader.font("Fixedsys", "Normal") + HSSFHeader.fontSize((short) 12) + 
            		"- 1 -", footerT.getCenter());

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
        	sb.append(fileLocation).append("/").append("testGetCellContents.xls");        	

        	if (EgovFileUtil.isExistsFile(sb.toString())) {
        		EgovFileUtil.delete(new File(sb.toString()));
        	
        		log.debug("Delete file...." + sb.toString());
        	}
        	
        	HSSFWorkbook wbTmp = new HSSFWorkbook();
        	wbTmp.createSheet();

            // 엑셀 파일 생성
            excelService.createWorkbook(wbTmp, sb.toString());

        	// 엑셀 파일 로드
        	HSSFWorkbook wb = excelService.loadWorkbook(sb.toString());
        	log.debug("testGetCellContents after loadWorkbook....");

        	HSSFSheet sheet = wb.createSheet("cell test sheet");

        	HSSFCellStyle cs = wb.createCellStyle();
        	cs = wb.createCellStyle();
            cs.setWrapText( true );
            
        	for (int i = 0; i < 100; i++) {
	        	HSSFRow row = sheet.createRow(i);
	        	for (int j = 0; j < 5; j++) {
		        	HSSFCell cell = row.createCell(j);
		        	cell.setCellValue(new HSSFRichTextString("row " + i + ", cell " + j));
		        	cell.setCellStyle( cs );
	        	}
        	}

        	// 엑셀 파일 저장
        	FileOutputStream out = new FileOutputStream(sb.toString());
            wb.write(out);
            out.close();
            
        	//////////////////////////////////////////////////////////////////////////
            // 검증
            HSSFWorkbook wbT = excelService.loadWorkbook(sb.toString());
            HSSFSheet sheetT = wbT.getSheet("cell test sheet");

            for (int i = 0; i < 100; i++) {
	        	HSSFRow row1 = sheetT.getRow(i);
	            for (int j = 0; j < 5; j++) {
	            	HSSFCell cell1 = row1.getCell(j);
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
        	sb.append(fileLocation).append("/").append("testModifyCellAttribute.xls");        	

        	if (EgovFileUtil.isExistsFile(sb.toString())) {
        		EgovFileUtil.delete(new File(sb.toString()));
        	
        		log.debug("Delete file...." + sb.toString());
        	}
        	
        	HSSFWorkbook wbTmp = new HSSFWorkbook();
        	wbTmp.createSheet();

            // 엑셀 파일 생성
            excelService.createWorkbook(wbTmp, sb.toString());

        	// 엑셀 파일 로드
        	HSSFWorkbook wb = excelService.loadWorkbook(sb.toString());
        	log.debug("testModifyCellAttribute after loadWorkbook....");

        	HSSFSheet sheet = wb.createSheet("cell test sheet2");
//        	sheet.setColumnWidth((short) 3, (short) 200);	// column Width
        	
        	HSSFCellStyle cs = wb.createCellStyle();
        	HSSFFont font = wb.createFont();
        	font.setFontHeight((short) 16);
        	font.setBoldweight((short) 3);
        	font.setFontName("fixedsys");
        	
            cs.setFont(font);
        	cs.setAlignment(HSSFCellStyle.ALIGN_RIGHT);	// cell 정렬
            cs.setWrapText( true );

        	for (int i = 0; i < 100; i++) {
	        	HSSFRow row = sheet.createRow(i);
//	        	row.setHeight((short)300); // row의 height 설정
	        	
	        	for (int j = 0; j < 5; j++) {
		        	HSSFCell cell = row.createCell(j);
		        	cell.setCellValue(new HSSFRichTextString("row " + i + ", cell " + j));
		        	cell.setCellStyle( cs );
	        	}
        	}

        	// 엑셀 파일 저장
        	FileOutputStream out = new FileOutputStream(sb.toString());
            wb.write(out);
            out.close();
            
        	//////////////////////////////////////////////////////////////////////////
            // 검증
            HSSFWorkbook wbT = excelService.loadWorkbook(sb.toString());
            HSSFSheet sheetT = wbT.getSheet("cell test sheet2");
            log.debug("getNumCellStyles : " + wbT.getNumCellStyles());

        	HSSFCellStyle cs1 = wbT.getCellStyleAt((short)(wbT.getNumCellStyles() - 1));

            HSSFFont fontT = cs1.getFont(wbT);
            log.debug("font getFontHeight : " + fontT.getFontHeight());
            log.debug("font getBoldweight : " + fontT.getBoldweight());
            log.debug("font getFontName : " + fontT.getFontName());
            log.debug("getAlignment : " + cs1.getAlignment());
            log.debug("getWrapText : " + cs1.getWrapText());
        	 
            for (int i = 0; i < 100; i++) {
	        	HSSFRow row1 = sheetT.getRow(i);
	            for (int j = 0; j < 5; j++) {
	            	HSSFCell cell1 = row1.getCell(j);
	            	log.debug("row " + i + ", cell " + j + " : " + cell1.getRichStringCellValue());
		            assertEquals(16, fontT.getFontHeight());
		            assertEquals(3, fontT.getBoldweight());
		            assertEquals(HSSFCellStyle.ALIGN_RIGHT, cs1.getAlignment());
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

    	sb.append(fileLocation).append("/template/").append("template.xls");   
    	sbResult.append(fileLocation).append("/").append("testUseTemplate1.xls");

    	Object[][] sample_data = {
            {"Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0},
            {"Gisella Bronzetti", "GB", 4.0, 3.0, 1.0, 3.5, null, null, 4.0},
    	};

        try {
        	
        	HSSFWorkbook wb = excelService.loadExcelTemplate(sb.toString());
        	HSSFSheet sheet = wb.getSheetAt(0);
        	
            // set data
        	for (int i = 0; i < sample_data.length; i++) {
                HSSFRow row = sheet.getRow(2 + i);
                for (int j = 0; j < sample_data[i].length; j++) {
                    if(sample_data[i][j] == null) continue;

                    HSSFCell cell = row.getCell(j);
                    
                    if(sample_data[i][j] instanceof String) {
                    	cell.setCellValue(new HSSFRichTextString((String)sample_data[i][j]));
                    } else {
                    	cell.setCellValue((Double)sample_data[i][j]);
                    }
                }
            }

        	// 수식 재계산
        	sheet.setForceFormulaRecalculation(true);
        	
        	excelService.createWorkbook(wb, sbResult.toString());
        	
        	//////////////////////////////////////////////////////////////////////////
            // 검증
            HSSFWorkbook wbT = excelService.loadWorkbook(sbResult.toString());
            HSSFSheet sheetT = wbT.getSheetAt(0);

            for (int i = 0; i < sample_data.length; i++) {
                HSSFRow row = sheetT.getRow(2 + i);
                for (int j = 0; j < sample_data[i].length; j++) {
                	HSSFCell cell = row.getCell(j);

                	
                	log.debug("sample_data[i][j] : " + sample_data[i][j]);

                	if (sample_data[i][j] == null) {
                		assertEquals(cell.getCellType(), HSSFCell.CELL_TYPE_BLANK);
                	} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
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

    	sb.append(fileLocation).append("/template/").append("template2.xls");
    	sbResult.append(fileLocation).append("/").append("testUseTemplate2.xls");
    	
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
            HSSFWorkbook wb = excelService.loadWorkbook(sbResult.toString());
            HSSFSheet sheet = wb.getSheetAt(0);

            Double[][] value = {
            		{5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0},
            		{4.0, 3.0, 1.0, 3.5, null, null, 4.0}
            };

            for (int i = 0; i < 2; i++) {
            	HSSFRow row2 = sheet.getRow(i + 2);
            	
            	for (int j = 0; j < 7; j++) {
            		 HSSFCell cellValue = row2.getCell((j + 2));
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
    	sb.append(fileLocation).append("/").append("testDataFormat.xls");
    
        HSSFWorkbook wb = excelService.loadWorkbook(sb.toString());
        HSSFSheet sheet = wb.getSheetAt(0);
        
        HSSFRow row = sheet.getRow(7);
    	HSSFCell cell = row.getCell(0);

    	assertEquals("2009/04/01", EgovExcelUtil.getValue(cell));

    	row = sheet.getRow(8);
    	cell = row.getCell(0);

    	assertEquals("2009/04/02", EgovExcelUtil.getValue(cell));
    }
}
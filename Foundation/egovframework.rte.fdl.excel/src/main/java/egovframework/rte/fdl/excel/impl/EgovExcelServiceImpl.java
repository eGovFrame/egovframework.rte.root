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
package egovframework.rte.fdl.excel.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;

import com.ibatis.sqlmap.client.SqlMapClient;

import egovframework.rte.fdl.cmmn.exception.BaseException;
import egovframework.rte.fdl.excel.EgovExcelMapping;
import egovframework.rte.fdl.excel.EgovExcelService;
import egovframework.rte.fdl.excel.EgovExcelXSSFMapping;
import egovframework.rte.fdl.filehandling.EgovFileUtil;
import egovframework.rte.fdl.string.EgovObjectUtil;

/**
 * 엑셀 서비스를 처리하는 비즈니스 구현 클래스
 * <p>
 * <b>NOTE:</b> 엑셀 서비스를 제공하기 위해 구현한 클래스이다.
 * @author 실행환경 개발팀 윤성종
 * @since 2009.06.01
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  ----------- --------    ---------------------------
 *   2009.06.01  윤성종			최초 생성
 *   2013.05.22  이기하 		XSSF, SXSSF형식 추가(xlsx 지원)
 *   2013.05.29  한성곤			mapBeanName property 추가 및 코드 정리
 * 
 * </pre>
 */
public class EgovExcelServiceImpl implements EgovExcelService, ApplicationContextAware {
    private EgovExcelServiceDAO dao;

    protected Log log = LogFactory.getLog(this.getClass());

    private MessageSource messageSource;
    private ApplicationContext applicationContext;
    private String mapClass;
    private String mapBeanName;
    private SqlMapClient sqlMapClient;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    	this.applicationContext = applicationContext;
        this.messageSource = (MessageSource) applicationContext.getBean("messageSource");
    }

    /**
     * @return the messageSource
     */
    protected MessageSource getMessageSource() {
        return messageSource;
    }

    public void setSqlMapClient(SqlMapClient sqlMapClient) throws Exception {
        this.sqlMapClient = sqlMapClient;
        dao = new EgovExcelServiceDAO(this.sqlMapClient);
    }

    /**
     * Excel Cell과 VO를 mapping 구현 클래스
     * 
     * @param mapClass
     * @throws Exception
     */
    public void setMapClass(String mapClass) throws BaseException {
        this.mapClass = mapClass;
        log.debug("mapClass : " + mapClass);

    }
    
    /**
     * Excel Cell과 VO를 mapping 구현 Bean name (mapClass보다 우선함)
     * 
     * @param mapBeanName
     * @throws BaseException
     */
    public void setMapBeanName(String mapBeanName) throws BaseException {
        this.mapBeanName = mapBeanName;
        log.debug("mapBeanName : " + mapBeanName);

    }
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#loadExcelTemplate(java.lang.String)
     */
    public HSSFWorkbook loadExcelTemplate(String templateName) throws BaseException, FileNotFoundException, IOException {

        FileInputStream fileIn = new FileInputStream(templateName);
        HSSFWorkbook wb = null;

        log.debug("EgovExcelServiceImpl.loadExcelTemplate : templatePath is " + templateName);

        try {
            log.debug("ExcelServiceImpl loadExcelTemplate ...");

            POIFSFileSystem fs = new POIFSFileSystem(fileIn);
            wb = new HSSFWorkbook(fs);

        } catch (Exception e) {
            log.error(getMessageSource().getMessage("error.excel.runtime.error", new Object[] {"EgovExcelServiceImpl loadExcelTemplate" }, Locale.getDefault()), e);
        } finally {
            log.debug("ExcelServiceImpl loadExcelTemplate end...");
            fileIn.close();
        }
        return wb;

    }
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#loadXSSFExcelTemplate(java.lang.String)
     */
    public XSSFWorkbook loadXSSFExcelTemplate(String templateName)
    		throws BaseException, FileNotFoundException, IOException {
    	
    	FileInputStream fileIn = new FileInputStream(templateName);
    	XSSFWorkbook wb = null;
    	
    	log.debug("EgovExcelServiceImpl.loadXSSFExcelTemplate : templatePath is " + templateName);
    	
    	try {
    		log.debug("ExcelServiceImpl loadXSSFExcelTemplate ...");
    		
    		wb = new XSSFWorkbook(fileIn);
    		
    	} catch (Exception e) {
    		log.error(getMessageSource().getMessage("error.excel.runtime.error", new Object[] {"EgovExcelServiceImpl loadXSSFExcelTemplate" }, Locale.getDefault()), e);

    	} finally {
    		log.debug("ExcelServiceImpl loadXSSFExcelTemplate end...");
    		fileIn.close();
    	}
    	return wb;
    	
    }
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#loadWorkbook(java.lang.String)
     */
    public HSSFWorkbook loadWorkbook(String filepath) throws BaseException, FileNotFoundException, IOException {

        FileInputStream fileIn = new FileInputStream(filepath);
        HSSFWorkbook wb = loadWorkbook(fileIn);
        fileIn.close();

        return wb;
    }
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#loadXSSFWorkbook(java.lang.String)
     */
    public XSSFWorkbook loadXSSFWorkbook(String filepath) throws BaseException, FileNotFoundException, IOException {
    	
    	FileInputStream fileIn = new FileInputStream(filepath);
    	XSSFWorkbook wb = loadXSSFWorkbook(fileIn);
    	fileIn.close();
    	
    	return wb;
    }

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#loadWorkbook(java.io.InputStream)
     */
    public HSSFWorkbook loadWorkbook(InputStream fileIn) throws BaseException {
        HSSFWorkbook wb = new HSSFWorkbook();

        try {
            log.debug("ExcelServiceImpl loadWorkbook ...");

            POIFSFileSystem fs = new POIFSFileSystem(fileIn);
            wb = new HSSFWorkbook(fs);

        } catch (Exception e) {
            log.error(getMessageSource().getMessage("error.excel.runtime.error", new Object[] {"loadWorkbook" }, Locale.getDefault()), e);
        }

        return wb;
    }
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#loadXSSFWorkbook(java.io.InputStream)
     */
    public XSSFWorkbook loadXSSFWorkbook(InputStream fileIn) throws BaseException {
    	XSSFWorkbook wb = new XSSFWorkbook();
    	
    	try {
    		log.debug("ExcelServiceImpl loadXSSFWorkbook ...");
    		
    		wb = new XSSFWorkbook(fileIn);
    		
    	} catch (Exception e) {
    		log.error(getMessageSource().getMessage("error.excel.runtime.error", new Object[] {"loadXSSFWorkbook" }, Locale.getDefault()), e);
    	}
    	
    	return wb;
    }

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#writeWorkbook(org.apache.poi.hssf.usermodel.HSSFWorkbook)
     */
    public void writeWorkbook(HSSFWorkbook workbook) throws BaseException, IOException {

        workbook.write(null);
    }
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#writeWorkbook(org.apache.poi.hssf.usermodel.HSSFWorkbook)
     */
    public void writeXSSFWorkbook(XSSFWorkbook workbook) throws BaseException, IOException {
    	
    	workbook.write(null);
    }
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#writeWorkbook(org.apache.poi.hssf.usermodel.HSSFWorkbook)
     */
    public void writeSXSSFWorkbook(SXSSFWorkbook workbook) throws BaseException, IOException {
    	
    	workbook.write(null);
    	
    }

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#createWorkbook(org.apache.poi.hssf.usermodel.HSSFWorkbook, java.lang.String)
     */
    public HSSFWorkbook createWorkbook(HSSFWorkbook wb, String filepath) throws BaseException, FileNotFoundException, IOException {

        String fullFileName = filepath;

        log.debug("EgovExcelServiceImpl.createWorkbook : templatePath is " + FilenameUtils.getFullPath(fullFileName));

        // 작업 디렉토리 생성
        if (!EgovFileUtil.isExistsFile(FilenameUtils.getFullPath(fullFileName))) {
            log.debug("make dir " + FilenameUtils.getFullPath(fullFileName));

            try {
                FileUtils.forceMkdir(new File(FilenameUtils.getFullPath(fullFileName)));
            } catch (IOException e) {
                throw new IOException("Cannot create directory for path: " + FilenameUtils.getFullPath(fullFileName));
            }
        }

        FileOutputStream fileOut = new FileOutputStream(fullFileName);

        log.debug("EgovExcelServiceImpl.createWorkbook : templatePath is " + fullFileName);

        try {
            log.debug("ExcelServiceImpl loadExcelObject ...");

            wb.write(fileOut);

        } catch (Exception e) {
            log.error(getMessageSource().getMessage("error.excel.runtime.error", new Object[] {"createWorkbook" }, Locale.getDefault()), e);
        } finally {
            log.debug("ExcelServiceImpl loadExcelObject end...");
            fileOut.close();
        }

        return wb;
    }

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#createWorkbook(org.apache.poi.xssf.usermodel.XSSFWorkbook, java.lang.String)
     */
    public XSSFWorkbook createXSSFWorkbook(XSSFWorkbook wb, String filepath) throws BaseException, FileNotFoundException, IOException {

        String fullFileName = filepath;
        
		log.debug("EgovExcelServiceImpl.createWorkbook : templatePath is " + FilenameUtils.getFullPath(fullFileName));

        // 작업 디렉토리 생성
        if (!EgovFileUtil.isExistsFile(FilenameUtils.getFullPath(fullFileName))) {
            log.debug("make dir " + FilenameUtils.getFullPath(fullFileName));

			try {
				FileUtils.forceMkdir(new File(FilenameUtils.getFullPath(fullFileName)));
			} catch (IOException e) {
				throw new IOException("Cannot create directory for path: " + FilenameUtils.getFullPath(fullFileName));
			}
        }

        FileOutputStream fileOut = new FileOutputStream(fullFileName);

		log.debug("EgovExcelServiceImpl.createWorkbook : templatePath is " + fullFileName);

        try {
            log.debug("ExcelServiceImpl loadExcelObject ...");
            
            wb.write(fileOut);

        } catch (Exception e) {
			log.error(getMessageSource().getMessage("error.excel.runtime.error", new Object[] { "createXSSFWorkbook" }, Locale.getDefault()), e);
        } finally {
            log.debug("ExcelServiceImpl loadExcelObject end...");
            fileOut.close();
        }

        return wb;
    }
    
    /*
	 * (non-Javadoc)
	 * @see egovframework.rte.fdl.excel.EgovExcelService#createSXSSFWorkbook(org.apache.poi.xssf.streaming.SXSSFWorkbook, java.lang.String)
	 */
	public SXSSFWorkbook createSXSSFWorkbook(SXSSFWorkbook wb, String filepath) throws BaseException, FileNotFoundException, IOException {

        String fullFileName = filepath;
        
		log.debug("EgovExcelServiceImpl.createWorkbook : templatePath is " + FilenameUtils.getFullPath(fullFileName));

        // 작업 디렉토리 생성
        if (!EgovFileUtil.isExistsFile(FilenameUtils.getFullPath(fullFileName))) {
            log.debug("make dir " + FilenameUtils.getFullPath(fullFileName));

			try {
				FileUtils.forceMkdir(new File(FilenameUtils.getFullPath(fullFileName)));
			} catch (IOException e) {
				throw new IOException("Cannot create directory for path: " + FilenameUtils.getFullPath(fullFileName));
			}
        }

        FileOutputStream fileOut = new FileOutputStream(fullFileName);

		log.debug("EgovExcelServiceImpl.createWorkbook : templatePath is " + fullFileName);

		try {
            log.debug("ExcelServiceImpl loadExcelObject ...");

            wb.write(fileOut);

        } catch (Exception e) {
			log.error(getMessageSource().getMessage("error.excel.runtime.error", new Object[] { "createSXSSFWorkbook" }, Locale.getDefault()), e);

        } finally {
            log.debug("ExcelServiceImpl loadExcelObject end...");
            fileOut.close();
        }

        return wb;
    }
    
    /**
     * 엑셀Sheet을 읽어서 DB upload 한다.
     * 
     * @param String queryId
     * @param HSSFSheet sheet
     * @param int start
     * @param long commitCnt
     * @return
     * @throws Exception
     */
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, org.apache.poi.hssf.usermodel.HSSFSheet, int, long)
     */
	public Integer uploadExcel(String queryId, HSSFSheet sheet, int start, long commitCnt) throws BaseException, Exception {

		log.debug("sheet.getPhysicalNumberOfRows() : " + sheet.getPhysicalNumberOfRows());

        Integer rowsAffected = 0;

        try {

            long rowCnt = sheet.getPhysicalNumberOfRows();
            long cnt = (commitCnt == 0) ? rowCnt : commitCnt;

			log.debug("Runtime.getRuntime().totalMemory() : " + Runtime.getRuntime().totalMemory());
			log.debug("Runtime.getRuntime().freeMemory() : " + Runtime.getRuntime().freeMemory());

            long startTime = System.currentTimeMillis();

            for (int idx = start, i = start; idx < rowCnt; idx = i) {
                List<Object> list = new ArrayList<Object>();

				log.debug("before Runtime.getRuntime().freeMemory() : " + Runtime.getRuntime().freeMemory());
				
				EgovExcelMapping mapping = null;
				
				if (mapBeanName != null) {
					mapping = (EgovExcelMapping) applicationContext.getBean(mapBeanName);
				} else if (mapClass != null) {
					mapping = (EgovExcelMapping) EgovObjectUtil.instantiate(mapClass);
				} else {
					throw new RuntimeException(getMessageSource().getMessage("error.excel.property.error", null, Locale.getDefault()));
				}

                for (i = idx; i < rowCnt && i < (cnt + idx); i++) {
                    HSSFRow row = sheet.getRow(i);
                    list.add(mapping.mappingColumn(row));
                }

                // insert
                // 현재 spring 연계 ibatis의 batch 형식으로 작성 후 중간에 exception 발생시켜도 rollback 이 불가한 문제가 있음.
                // ibatis 의 batch 관련하여서는 sqlMapClient.startTransaction() 이하의 코드 등 추가 작업이 필요한지 확인 필요!

                rowsAffected += dao.batchInsert(queryId, list);

				log.debug("after Runtime.getRuntime().freeMemory() : " + Runtime.getRuntime().freeMemory());

                log.debug("\n\n\n" + rowsAffected);
            }

			log.debug("batchInsert time is " + (System.currentTimeMillis() - startTime));

        } catch (Exception e) {
            throw new Exception(e);
        }

        log.debug("uploadExcel result count is " + rowsAffected);

        return rowsAffected;
    }
    
    /**
     * 엑셀Sheet을 읽어서 DB upload 한다.
     * 
     * @param String queryId
     * @param HSSFSheet sheet
     * @param int start
     * @param long commitCnt
     * @return
     * @throws Exception
     */
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadSXSSFExcel(java.lang.String, org.apache.poi.xssf.usermodel.XSSFSheet, int, long)
     */
	public Integer uploadXSSFExcel(String queryId, XSSFSheet sheet, int start, long commitCnt) throws BaseException, Exception {
    	
		log.debug("sheet.getPhysicalNumberOfRows() : " + sheet.getPhysicalNumberOfRows());
    	
    	Integer rowsAffected = 0;
    	
		try {

			long rowCnt = sheet.getPhysicalNumberOfRows();
			long cnt = (commitCnt == 0) ? rowCnt : commitCnt;

			log.debug("Runtime.getRuntime().totalMemory() : " + Runtime.getRuntime().totalMemory());
			log.debug("Runtime.getRuntime().freeMemory() : " + Runtime.getRuntime().freeMemory());

			long startTime = System.currentTimeMillis();

			for (int idx = start, i = start; idx < rowCnt; idx = i) {
				List<Object> list = new ArrayList<Object>();

				log.debug("before Runtime.getRuntime().freeMemory() : " + Runtime.getRuntime().freeMemory());
				EgovExcelXSSFMapping mapping = null;
				
				if (mapBeanName != null) {
					mapping = (EgovExcelXSSFMapping) applicationContext.getBean(mapBeanName);
				} else if (mapClass != null) {
					mapping = (EgovExcelXSSFMapping) EgovObjectUtil.instantiate(mapClass);
				} else {
					throw new RuntimeException(getMessageSource().getMessage("error.excel.property.error", null, Locale.getDefault()));
				}

				for (i = idx; i < rowCnt && i < (cnt + idx); i++) {
					XSSFRow row = sheet.getRow(i);
					list.add(mapping.mappingColumn(row));
				}

    			// insert
    			// 현재 spring 연계 ibatis의 batch 형식으로 작성 중간에 exception 발생시켜도 rollback 이 불가한 문제가 있음.
    			// ibatis 의 batch 관련하여서는 sqlMapClient.startTransaction() 이하의 코드 등 추가 작업이 필요한지 확인 필요!
    			
				rowsAffected += dao.batchInsert(queryId, list);

				log.debug("after Runtime.getRuntime().freeMemory() : " + Runtime.getRuntime().freeMemory());

				log.debug("\n\n\n" + rowsAffected);
			}

			log.debug("batchInsert time is " + (System.currentTimeMillis() - startTime));

    	} catch (Exception e) {
    		throw new Exception(e);
    	}
    	
    	log.debug("uploadExcel result count is " + rowsAffected);
    	
    	return rowsAffected;
    }

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, org.apache.poi.hssf.usermodel.HSSFSheet, int)
     */
	public Integer uploadExcel(String queryId, HSSFSheet sheet, int start) throws BaseException, Exception {
		return uploadExcel(queryId, sheet, start, 0);
	}
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, org.apache.poi.xssf.usermodel.XSSFSheet, int)
     */
	public Integer uploadXSSFExcel(String queryId, XSSFSheet sheet, int start) throws BaseException, Exception {
		return uploadXSSFExcel(queryId, sheet, start, 0);
	}

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, org.apache.poi.hssf.usermodel.HSSFSheet, long)
     */
	public Integer uploadExcel(String queryId, HSSFSheet sheet, long commitCnt) throws BaseException, Exception {
		return uploadExcel(queryId, sheet, 0, commitCnt);
	}
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, org.apache.poi.xssf.usermodel.XSSFSheet, long)
     */
	public Integer uploadXSSFExcel(String queryId, XSSFSheet sheet, long commitCnt) throws BaseException, Exception {
		return uploadXSSFExcel(queryId, sheet, 0, commitCnt);
	}

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, org.apache.poi.hssf.usermodel.HSSFSheet)
     */
	public Integer uploadExcel(String queryId, HSSFSheet sheet) throws BaseException, Exception {
		return uploadExcel(queryId, sheet, 0, 0);
	}
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, org.apache.poi.hssf.usermodel.HSSFSheet)
     */
	public Integer uploadXSSFExcel(String queryId, XSSFSheet sheet) throws BaseException, Exception {
		return uploadXSSFExcel(queryId, sheet, 0, 0);
	}

    /**
     * 엑셀파일을 읽어서 DB upload 한다.
     * 
     * @param String queryId
     * @param InputStream fileIn
     * @param int start
     * @param long commitCnt
     * @return
     * @throws Exception
     */
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, ava.io.InputStream, int)
     */
	public Integer uploadExcel(String queryId, InputStream fileIn, int start, long commitCnt) throws BaseException, Exception {
		HSSFWorkbook wb = loadWorkbook(fileIn);
		HSSFSheet sheet = wb.getSheetAt(0);

		return uploadExcel(queryId, sheet, start, commitCnt);
	}
    
    /**
     * 엑셀파일을 읽어서 DB upload 한다.
     * 
     * @param String queryId
     * @param InputStream fileIn
     * @param int start
     * @param long commitCnt
     * @return
     * @throws Exception
     */
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, int)
     */
	public Integer uploadXSSFExcel(String queryId, InputStream fileIn, int start, long commitCnt) throws BaseException, Exception {
		XSSFWorkbook wb = loadXSSFWorkbook(fileIn);
		XSSFSheet sheet = wb.getSheetAt(0);

		return uploadXSSFExcel(queryId, sheet, start, commitCnt);
	}

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, int)
     */
	public Integer uploadExcel(String queryId, InputStream fileIn, int start) throws BaseException, Exception {
		return uploadExcel(queryId, fileIn, start, 0);
	}
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, int)
     */
	public Integer uploadXSSFExcel(String queryId, InputStream fileIn, int start) throws BaseException, Exception {
		return uploadXSSFExcel(queryId, fileIn, start, 0);
	}

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream)
     */
	public Integer uploadExcel(String queryId, InputStream fileIn, long commitCnt) throws BaseException, Exception {
		return uploadExcel(queryId, fileIn, 0, commitCnt);
	}
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream)
     */
	public Integer uploadXSSFExcel(String queryId, InputStream fileIn, long commitCnt) throws BaseException, Exception {
		return uploadXSSFExcel(queryId, fileIn, 0, commitCnt);
	}

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream)
     */
	public Integer uploadExcel(String queryId, InputStream fileIn) throws BaseException, Exception {
		return uploadExcel(queryId, fileIn, 0, 0);
	}
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream)
     */
	public Integer uploadXSSFExcel(String queryId, InputStream fileIn) throws BaseException, Exception {
		return uploadXSSFExcel(queryId, fileIn, 0, 0);
	}

    /**
     * 엑셀파일을 읽어서 DB upload 한다. sheet의 인덱스값으로 upload할 sheet를 지정한다.
     * 
     * @param String queryId
     * @param InputStream fileIn
     * @param short sheetIndex
     * @param int start
     * @param long commitCnt
     * @return
     * @throws Exception
     */
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, short, int)
     */
	public Integer uploadExcel(String queryId, InputStream fileIn, short sheetIndex, int start, long commitCnt) throws BaseException, Exception {
		HSSFWorkbook wb = loadWorkbook(fileIn);
		HSSFSheet sheet = wb.getSheetAt(sheetIndex);

		return uploadExcel(queryId, sheet, start, commitCnt);
	}
    
    /**
     * 엑셀파일을 읽어서 DB upload 한다. sheet의 인덱스값으로 upload할 sheet를 지정한다.
     * 
     * @param String queryId
     * @param InputStream fileIn
     * @param short sheetIndex
     * @param int start
     * @param long commitCnt
     * @return
     * @throws Exception
     */
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, short, int)
     */
	public Integer uploadXSSFExcel(String queryId, InputStream fileIn, short sheetIndex, int start, long commitCnt) throws BaseException, Exception {
		XSSFWorkbook wb = loadXSSFWorkbook(fileIn);
		XSSFSheet sheet = wb.getSheetAt(sheetIndex);

		return uploadXSSFExcel(queryId, sheet, start, commitCnt);
	}

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, short, int)
     */
	public Integer uploadExcel(String queryId, InputStream fileIn, short sheetIndex, int start) throws BaseException, Exception {
		return uploadExcel(queryId, fileIn, sheetIndex, start, 0);
	}
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, short, int)
     */
	public Integer uploadXSSFExcel(String queryId, InputStream fileIn, short sheetIndex, int start) throws BaseException, Exception {
		return uploadXSSFExcel(queryId, fileIn, sheetIndex, start, 0);
	}

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, short, long)
     */
	public Integer uploadExcel(String queryId, InputStream fileIn, short sheetIndex, long commitCnt) throws BaseException, Exception {
		return uploadExcel(queryId, fileIn, sheetIndex, 0, commitCnt);
	}
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, short, long)
     */
	public Integer uploadXSSFExcel(String queryId, InputStream fileIn, short sheetIndex, long commitCnt) throws BaseException, Exception {
		return uploadXSSFExcel(queryId, fileIn, sheetIndex, 0, commitCnt);
	}

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, short)
     */
	public Integer uploadExcel(String queryId, InputStream fileIn, short sheetIndex) throws BaseException, Exception {
		return uploadExcel(queryId, fileIn, sheetIndex, 0, 0);
	}
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, short)
     */
	public Integer uploadXSSFExcel(String queryId, InputStream fileIn, short sheetIndex) throws BaseException, Exception {
		return uploadXSSFExcel(queryId, fileIn, sheetIndex, 0, 0);
	}

    /**
     * 엑셀파일을 읽어서 DB upload 한다. sheet의 명으로 upload할 sheet를 지정한다.
     * 
     * @param String queryId
     * @param InputStream fileIn
     * @param String sheetName
     * @param int start
     * @param long commitCnt
     * @return
     * @throws Exception
     */
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, java.lang.String, int, long)
     */
	public Integer uploadExcel(String queryId, InputStream fileIn, String sheetName, int start, long commitCnt) throws BaseException, Exception {
		HSSFWorkbook wb = loadWorkbook(fileIn);
		HSSFSheet sheet = wb.getSheet(sheetName);

		return uploadExcel(queryId, sheet, start, commitCnt);
	}
    
    /**
     * 엑셀파일을 읽어서 DB upload 한다. sheet의 명으로 upload할 sheet를 지정한다.
     * 
     * @param String queryId
     * @param InputStream fileIn
     * @param String sheetName
     * @param int start
     * @param long commitCnt
     * @return
     * @throws Exception
     */
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, java.lang.String, int, long)
     */
	public Integer uploadXSSFExcel(String queryId, InputStream fileIn, String sheetName, int start, long commitCnt) throws BaseException, Exception {
		XSSFWorkbook wb = loadXSSFWorkbook(fileIn);
		XSSFSheet sheet = wb.getSheet(sheetName);

		return uploadXSSFExcel(queryId, sheet, start, commitCnt);
	}

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, java.lang.String, int)
     */
	public Integer uploadExcel(String queryId, InputStream fileIn, String sheetName, int start) throws BaseException, Exception {
		return uploadExcel(queryId, fileIn, sheetName, start, 0);
	}
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, java.lang.String, int)
     */
	public Integer uploadXSSFExcel(String queryId, InputStream fileIn, String sheetName, int start) throws BaseException, Exception {
		return uploadXSSFExcel(queryId, fileIn, sheetName, start, 0);
	}

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, java.lang.String, long)
     */
	public Integer uploadExcel(String queryId, InputStream fileIn, String sheetName, long commitCnt) throws BaseException, Exception {
		return uploadExcel(queryId, fileIn, sheetName, 0, commitCnt);
	}
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, java.lang.String, long)
     */
	public Integer uploadXSSFExcel(String queryId, InputStream fileIn, String sheetName, long commitCnt) throws BaseException, Exception {
		return uploadXSSFExcel(queryId, fileIn, sheetName, 0, commitCnt);
	}

    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, java.lang.String)
     */
	public Integer uploadExcel(String queryId, InputStream fileIn, String sheetName) throws BaseException, Exception {
		return uploadExcel(queryId, fileIn, sheetName, 0, 0);
	}
    
    /*
     * (non-Javadoc)
     * @see egovframework.rte.fdl.excel.EgovExcelService#uploadExcel(java.lang.String, java.io.InputStream, java.lang.String)
     */
	public Integer uploadXSSFExcel(String queryId, InputStream fileIn, String sheetName) throws BaseException, Exception {
		return uploadXSSFExcel(queryId, fileIn, sheetName, 0, 0);
	}

}

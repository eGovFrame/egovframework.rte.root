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
package egovframework.rte.fdl.excel;

import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * 엑셀 서비스를 제공하는 인터페이스
 * <p>
 * <b>NOTE:</b> 엑셀 서비스를 제공하기 위해 여러 기능들을 선언하는 인터페이스이다.
 * @author 실행환경 개발팀 윤성종
 * @since 2009.06.01
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.06.01  윤성종           최초 생성
 *   2013.05.22  이기하           XSSF, SXSSF 형식 추가
 * 
 * </pre>
 */
public interface EgovExcelService {

    /**
     * HSSFWorkbook 객체를 생성하여 엑셀파일을 생성한다.
     * @param wb
     * @param filepath
     * @return
     * @throws Exception
     */
    public HSSFWorkbook createWorkbook(HSSFWorkbook wb, String filepath)
            throws Exception;
    
    /**
     * XSSFWorkbook 객체를 생성하여 엑셀파일을 생성한다.
     * @param wb
     * @param filepath
     * @return
     * @throws Exception
     */
    public XSSFWorkbook createXSSFWorkbook(XSSFWorkbook wb, String filepath)
            throws Exception;
    
    /**
     * SXSSFWorkbook 객체를 생성하여 엑셀파일을 생성한다.
     * @param wb
     * @param filepath
     * @return
     * @throws Exception
     */
    public SXSSFWorkbook createSXSSFWorkbook(SXSSFWorkbook wb, String filepath)
    		throws Exception;

    /**
     * 엑셀 Template를 로딩하여 엑셀파일을 생성한다.
     * @param templateName
     * @return
     * @throws Exception
     */
    public HSSFWorkbook loadExcelTemplate(String templateName) throws Exception;
    
    /**
     * xlsx 엑셀 Template를 로딩하여 엑셀파일을 생성한다.
     * @param templateName
     * @return
     * @throws Exception
     */
    public XSSFWorkbook loadXSSFExcelTemplate(String templateName) throws Exception;
    
    /**
     * 엑셀 파일을 로딩한다.
     * @param filepath
     * @return
     * @throws Exception
     */
    public HSSFWorkbook loadWorkbook(String filepath) throws Exception;
    
    /**
     * xlsx 엑셀 파일을 로딩한다.
     * @param filepath
     * @return
     * @throws Exception
     */
    public XSSFWorkbook loadXSSFWorkbook(String filepath) throws Exception;
    
    /**
     * 엑셀 파일을 로딩한다.
     * @param fileIn
     * @return
     * @throws Exception
     */
    public HSSFWorkbook loadWorkbook(InputStream fileIn) throws Exception;
    
    /**
     * xlsx 엑셀 파일을 로딩한다.
     * @param fileIn
     * @return
     * @throws Exception
     */
    public XSSFWorkbook loadXSSFWorkbook(InputStream fileIn) throws Exception;

    /**
     * 엑셀 파일을 저장한다.
     * @param workbook
     * @throws Exception
     */
    public void writeWorkbook(HSSFWorkbook workbook) throws Exception;
    
    /**
     * xlsx 엑셀 파일을 저장한다.
     * @param workbook
     * @throws Exception
     */
    public void writeXSSFWorkbook(XSSFWorkbook workbook) throws Exception;
    
    /**
     * xlsx 엑셀 파일을 저장한다.
     * @param workbook
     * @throws Exception
     */
    public void writeSXSSFWorkbook(SXSSFWorkbook workbook) throws Exception;

    /**
     * 엑셀파일을 저장한다.
     * @param queryId
     * @param fileIn
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, InputStream fileIn)
            throws Exception;
    
    /**
     * 엑셀파일을 저장한다.
     * @param queryId
     * @param fileIn
     * @return
     * @throws Exception
     */
    public Integer uploadXSSFExcel(String queryId, InputStream fileIn)
    		throws Exception;

    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
     * 셀부터 업로드한다.
     * @param queryId
     * @param fileIn
     * @param start
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, InputStream fileIn, int start)
            throws Exception;
    
    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
     * 셀부터 업로드한다.
     * @param queryId
     * @param fileIn
     * @param start
     * @return
     * @throws Exception
     */
    public Integer uploadXSSFExcel(String queryId, InputStream fileIn, int start)
    		throws Exception;

    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다.
     * @param queryId
     * @param fileIn
     * @param sheetIndex
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, InputStream fileIn,
            short sheetIndex) throws Exception;
    
    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다.
     * @param queryId
     * @param fileIn
     * @param sheetIndex
     * @return
     * @throws Exception
     */
    public Integer uploadXSSFExcel(String queryId, InputStream fileIn,
    		short sheetIndex) throws Exception;

    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
     * 셀부터 업로드한다.
     * @param queryId
     * @param fileIn
     * @param sheetIndex
     * @param start
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, InputStream fileIn,
            short sheetIndex, int start) throws Exception;
    
    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
     * 셀부터 업로드한다.
     * @param queryId
     * @param fileIn
     * @param sheetIndex
     * @param start
     * @return
     * @throws Exception
     */
    public Integer uploadXSSFExcel(String queryId, InputStream fileIn,
    		short sheetIndex, int start) throws Exception;

    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다.
     * @param queryId
     * @param fileIn
     * @param sheetName
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, InputStream fileIn,
            String sheetName) throws Exception;
    
    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다.
     * @param queryId
     * @param fileIn
     * @param sheetName
     * @return
     * @throws Exception
     */
    public Integer uploadXSSFExcel(String queryId, InputStream fileIn,
    		String sheetName) throws Exception;

    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
     * 셀부터 업로드한다.
     * @param queryId
     * @param fileIn
     * @param sheetName
     * @param start
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, InputStream fileIn,
            String sheetName, int start) throws Exception;
    
    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
     * 셀부터 업로드한다.
     * @param queryId
     * @param fileIn
     * @param sheetName
     * @param start
     * @return
     * @throws Exception
     */
    public Integer uploadXSSFExcel(String queryId, InputStream fileIn,
    		String sheetName, int start) throws Exception;

    /**
     * 엑셀파일을 저장한다.
     * @param queryId
     * @param fileIn
     * @param commitCnt
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, InputStream fileIn,
            long commitCnt) throws Exception;
    
    /**
     * 엑셀파일을 저장한다.
     * @param queryId
     * @param fileIn
     * @param commitCnt
     * @return
     * @throws Exception
     */
    public Integer uploadXSSFExcel(String queryId, InputStream fileIn,
    		long commitCnt) throws Exception;

    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
     * 셀부터 업로드한다.
     * @param queryId
     * @param fileIn
     * @param start
     * @param commitCnt
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, InputStream fileIn, int start,
            long commitCnt) throws Exception;
    
    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
     * 셀부터 업로드한다.
     * @param queryId
     * @param fileIn
     * @param start
     * @param commitCnt
     * @return
     * @throws Exception
     */
    public Integer uploadXSSFExcel(String queryId, InputStream fileIn, int start,
    		long commitCnt) throws Exception;

    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다.
     * @param queryId
     * @param fileIn
     * @param sheetIndex
     * @param commitCnt
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, InputStream fileIn,
            short sheetIndex, long commitCnt) throws Exception;
    
    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다.
     * @param queryId
     * @param fileIn
     * @param sheetIndex
     * @param commitCnt
     * @return
     * @throws Exception
     */
    public Integer uploadXSSFExcel(String queryId, InputStream fileIn,
    		short sheetIndex, long commitCnt) throws Exception;

    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
     * 셀부터 업로드한다.
     * @param queryId
     * @param fileIn
     * @param sheetIndex
     * @param start
     * @param commitCnt
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, InputStream fileIn,
            short sheetIndex, int start, long commitCnt) throws Exception;
    
    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
     * 셀부터 업로드한다.
     * @param queryId
     * @param fileIn
     * @param sheetIndex
     * @param start
     * @param commitCnt
     * @return
     * @throws Exception
     */
    public Integer uploadXSSFExcel(String queryId, InputStream fileIn,
    		short sheetIndex, int start, long commitCnt) throws Exception;

    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다.
     * @param queryId
     * @param fileIn
     * @param sheetName
     * @param commitCnt
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, InputStream fileIn,
            String sheetName, long commitCnt) throws Exception;
    
    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다.
     * @param queryId
     * @param fileIn
     * @param sheetName
     * @param commitCnt
     * @return
     * @throws Exception
     */
    public Integer uploadXSSFExcel(String queryId, InputStream fileIn,
    		String sheetName, long commitCnt) throws Exception;

    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
     * 셀부터 업로드한다.
     * @param queryId
     * @param fileIn
     * @param sheetName
     * @param start
     * @param commitCnt
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, InputStream fileIn,
            String sheetName, int start, long commitCnt) throws Exception;
    
    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
     * 셀부터 업로드한다.
     * @param queryId
     * @param fileIn
     * @param sheetName
     * @param start
     * @param commitCnt
     * @return
     * @throws Exception
     */
    public Integer uploadXSSFExcel(String queryId, InputStream fileIn,
    		String sheetName, int start, long commitCnt) throws Exception;

    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다.
     * @param queryId
     * @param sheet
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, HSSFSheet sheet)
            throws Exception;
    
    /**
     * xlsx 엑셀파일을 업로드하여 DB에 일괄저장한다.
     * @param queryId
     * @param sheet
     * @return
     * @throws Exception
     */
    public Integer uploadXSSFExcel(String queryId, XSSFSheet sheet)
    		throws Exception;

    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
     * 셀부터 업로드한다.
     * @param queryId
     * @param sheet
     * @param start
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, HSSFSheet sheet, int start)
            throws Exception;
    
    /**
     * xlsx 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
     * 셀부터 업로드한다.
     * @param queryId
     * @param sheet
     * @param start
     * @return
     * @throws Exception
     */
    public Integer uploadXSSFExcel(String queryId, XSSFSheet sheet, int start)
    		throws Exception;

    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다. commit할 건수를 입력한다.
     * @param queryId
     * @param sheet
     * @param commitCnt
     *        커밋 건수 단위
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, HSSFSheet sheet, long commitCnt)
            throws Exception;
    
    /**
     * xlsx 엑셀파일을 업로드하여 DB에 일괄저장한다. commit할 건수를 입력한다.
     * @param queryId
     * @param sheet
     * @param commitCnt
     *        커밋 건수 단위
     * @return
     * @throws Exception
     */
    public Integer uploadXSSFExcel(String queryId, XSSFSheet sheet, long commitCnt)
    		throws Exception;

    /**
     * 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
     * 셀부터 업로드한다. commit할 건수를 입력한다.
     * @param queryId
     * @param sheet
     * @param start
     * @param commitCnt
     *        커밋 건수 단위
     * @return
     * @throws Exception
     */
    public Integer uploadExcel(String queryId, HSSFSheet sheet, int start,
            long commitCnt) throws Exception;

	/**
	 * xlsx 엑셀파일을 업로드하여 DB에 일괄저장한다. 업로드할 엑셀의 시작 위치를 정하여 지정한
	 * 셀부터 업로드한다. commit할 건수를 입력한다.
	 * @param queryId
	 * @param sheet
	 * @param start
	 * @param commitCnt
	 *        커밋 건수 단위
	 * @return
	 * @throws Exception
	 */
	public Integer uploadXSSFExcel(String queryId, XSSFSheet sheet, int start,
			long commitCnt) throws Exception;
	}

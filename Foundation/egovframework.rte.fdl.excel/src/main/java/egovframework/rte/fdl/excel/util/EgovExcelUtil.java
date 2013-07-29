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
package egovframework.rte.fdl.excel.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;

import egovframework.rte.fdl.string.EgovDateUtil;
import egovframework.rte.fdl.string.EgovStringUtil;

/**
 * 엑셀 서비스 제공을 위한 유틸 클래스
 * <p>
 * <b>NOTE:</b> 엑셀 서비스를 제공하기 위해 유용한 유틸을 포함하는 클래스이다.
 * @author 실행환경 개발팀 윤성종
 * @since 2009.06.01
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      	수정자           수정내용
 *  ------------   --------    ---------------------------
 *   2009.06.01		윤성종           최초 생성
 *   2013.05.22		이기하           XSSFCell 추가
 * 
 * </pre>
 */
public class EgovExcelUtil {

    private static Log log = LogFactory.getLog(EgovExcelUtil.class);

    /**
     * <p>
     * 엑셀의 셀값을 String 타입으로 변환하여 리턴한다.
     * </p>
     * @param cell
     *        <code>HSSFCell</code>
     * @return 결과 값
     */
    public static String getValue(HSSFCell cell) {

        String result = "";

        if (null == cell || cell.equals(null))
            return "";

        if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
            log.debug("### HSSFCell.CELL_TYPE_BOOLEAN : "
                + HSSFCell.CELL_TYPE_BOOLEAN);
            result = String.valueOf(cell.getBooleanCellValue());

        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_ERROR) {
            log.debug("### HSSFCell.CELL_TYPE_ERROR : "
                + HSSFCell.CELL_TYPE_ERROR);
            // byte errorValue =
            // cell.getErrorCellValue();

        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
            log.debug("### HSSFCell.CELL_TYPE_FORMULA : "
                + HSSFCell.CELL_TYPE_FORMULA);

            String stringValue = cell.getRichStringCellValue().getString();
            String longValue = doubleToString(cell.getNumericCellValue());

            result =
                EgovStringUtil.isNumeric(longValue) ? longValue : stringValue;

        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
            log.debug("### HSSFCell.CELL_TYPE_NUMERIC : "
                + HSSFCell.CELL_TYPE_NUMERIC);

            result =
                HSSFDateUtil.isCellDateFormatted(cell)
                    ? EgovDateUtil.toString(cell.getDateCellValue(),
                        "yyyy/MM/dd", null) : doubleToString(cell
                        .getNumericCellValue());

        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
            log.debug("### HSSFCell.CELL_TYPE_STRING : "
                + HSSFCell.CELL_TYPE_STRING);
            result = cell.getRichStringCellValue().getString();

        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
            log.debug("### HSSFCell.CELL_TYPE_BLANK : "
                + HSSFCell.CELL_TYPE_BLANK);
        }

        return result;
    }
    
    /**
     * <p>
     * xlsx 엑셀의 셀값을 String 타입으로 변환하여 리턴한다.
     * </p>
     * @param cell
     *        <code>XSSFCell</code>
     * @return 결과 값
     */
    public static String getValue(XSSFCell cell) {
    	
    	String result = "";
    	
    	if (null == cell || cell.equals(null))
    		return "";
    	
    	if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
    		log.debug("### XSSFCell.CELL_TYPE_BOOLEAN : "
    				+ XSSFCell.CELL_TYPE_BOOLEAN);
    		result = String.valueOf(cell.getBooleanCellValue());
    		
    	} else if (cell.getCellType() == XSSFCell.CELL_TYPE_ERROR) {
    		log.debug("### XSSFCell.CELL_TYPE_ERROR : "
    				+ XSSFCell.CELL_TYPE_ERROR);
    		// byte errorValue =
    		// cell.getErrorCellValue();
    		
    	} else if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
    		log.debug("### XSSFCell.CELL_TYPE_FORMULA : "
    				+ XSSFCell.CELL_TYPE_FORMULA);
    		
    		String stringValue = cell.getRichStringCellValue().getString();
    		String longValue = doubleToString(cell.getNumericCellValue());
    		
    		result =
    				EgovStringUtil.isNumeric(longValue) ? longValue : stringValue;
    		
    	} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
    		log.debug("### XSSFCell.CELL_TYPE_NUMERIC : "
    				+ XSSFCell.CELL_TYPE_NUMERIC);
    		
    		result =
    				HSSFDateUtil.isCellDateFormatted(cell)
    				? EgovDateUtil.toString(cell.getDateCellValue(),
    						"yyyy/MM/dd", null) : doubleToString(cell
    								.getNumericCellValue());
    				
    	} else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
    		log.debug("### XSSFCell.CELL_TYPE_STRING : "
    				+ XSSFCell.CELL_TYPE_STRING);
    		result = cell.getRichStringCellValue().getString();
    		
    	} else if (cell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
    		log.debug("### XSSFCell.CELL_TYPE_BLANK : "
    				+ XSSFCell.CELL_TYPE_BLANK);
    	}
    	
    	return result;
    }

    /*
     * 0, "General" 1, "0" 2, "0.00" 3, "#,##0" 4,
     * "#,##0.00" 5, "($#,##0_);($#,##0)" 6,
     * "($#,##0_);[Red]($#,##0)" 7,
     * "($#,##0.00);($#,##0.00)" 8,
     * "($#,##0.00_);[Red]($#,##0.00)" 9, "0%" 0xa,
     * "0.00%" 0xb, "0.00E+00" 0xc, "# ?/?" 0xd,
     * "# ??/??" 0xe, "m/d/yy" 0xf, "d-mmm-yy" 0x10,
     * "d-mmm" 0x11, "mmm-yy" 0x12, "h:mm AM/PM" 0x13,
     * "h:mm:ss AM/PM" 0x14, "h:mm" 0x15, "h:mm:ss"
     * 0x16, "m/d/yy h:mm" // 0x17 - 0x24 reserved for
     * international and undocumented 0x25,
     * "(#,##0_);(#,##0)" 0x26, "(#,##0_);[Red](#,##0)"
     * 0x27, "(#,##0.00_);(#,##0.00)" 0x28,
     * "(#,##0.00_);[Red](#,##0.00)" 0x29,
     * "_(*#,##0_);_(*(#,##0);_(* \"-\"_);_(@_)" 0x2a,
     * "_($*#,##0_);_($*(#,##0);_($* \"-\"_);_(@_)"
     * 0x2b,
     * "_(*#,##0.00_);_(*(#,##0.00);_(*\"-\"??_);_(@_)"
     * 0x2c,
     * "_($*#,##0.00_);_($*(#,##0.00);_($*\"-\"??_);_(@_)"
     * 0x2d, "mm:ss" 0x2e, "[h]:mm:ss" 0x2f, "mm:ss.0"
     * 0x30, "##0.0E+0" 0x31, "@" - This is text
     * format. 0x31 "text" - Alias for "@"
     */

    /**
     * <p>
     * double 형의 셀 데이터를 String 형으로 변환하여 리턴한다.
     * </p>
     * @param d
     *        <code>double</code>
     * @return 결과 값
     */
    public static String doubleToString(double d) {
        long lValue = (long) d;
        return (lValue == d) ? Long.toString(lValue) : Double.toString(d);
    }

}

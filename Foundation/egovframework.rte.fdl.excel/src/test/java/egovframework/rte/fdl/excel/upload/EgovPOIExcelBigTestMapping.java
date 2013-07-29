package egovframework.rte.fdl.excel.upload;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import egovframework.rte.fdl.excel.EgovExcelXSSFMapping;
import egovframework.rte.fdl.excel.util.EgovExcelUtil;
import egovframework.rte.fdl.excel.vo.ZipVO;

/**
 * @author sjyoon
 *
 */
public class EgovPOIExcelBigTestMapping extends EgovExcelXSSFMapping {

	protected Log log = LogFactory.getLog(this.getClass());
	
	/* (non-Javadoc)
	 * @see egovframework.rte.fdl.excel.EgovExcelMapping#mappingColumn(org.apache.poi.hssf.usermodel.HSSFRow)
	 */
	@Override
	public ZipVO mappingColumn(XSSFRow row) {
		XSSFCell cell0 = row.getCell(0);
    	XSSFCell cell1 = row.getCell(1);
    	XSSFCell cell2 = row.getCell(2);
    	XSSFCell cell3 = row.getCell(3);
    	XSSFCell cell4 = row.getCell(4);
    	XSSFCell cell5 = row.getCell(5);
    	XSSFCell cell6 = row.getCell(6);
    	XSSFCell cell7 = row.getCell(7);

		ZipVO vo = new ZipVO();

		vo.setZipNo(new BigDecimal(cell0.getNumericCellValue()));
		vo.setSerNo(new BigDecimal(cell1.getNumericCellValue()));
		vo.setSidoNm(EgovExcelUtil.getValue(cell2));
		vo.setCggNm(EgovExcelUtil.getValue(cell3));
		vo.setUmdNm(EgovExcelUtil.getValue(cell4));
		vo.setBdNm(EgovExcelUtil.getValue(cell5));
		vo.setJibun(EgovExcelUtil.getValue(cell6));
		vo.setRegId(EgovExcelUtil.getValue(cell7));
		
		return vo;
	}
}

package egovframework.rte.fdl.excel.upload;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import egovframework.rte.fdl.excel.EgovExcelXSSFMapping;
import egovframework.rte.fdl.excel.util.EgovExcelUtil;
import egovframework.rte.fdl.excel.vo.EmpVO;

/**
 * @author sjyoon
 *
 */
public class EgovPOIExcelTestMapping extends EgovExcelXSSFMapping {

	protected Log log = LogFactory.getLog(this.getClass());
	
	/* (non-Javadoc)
	 * @see egovframework.rte.fdl.excel.EgovPOIExcelMapping#mappingColumn(org.apache.poi.xssf.usermodel.XSSFRow)
	 */
	@Override
	public EmpVO mappingColumn(XSSFRow row) {
		XSSFCell cell0 = row.getCell(0);
    	XSSFCell cell1 = row.getCell(1);
    	XSSFCell cell2 = row.getCell(2);

		EmpVO vo = new EmpVO();

		vo.setEmpNo(new BigDecimal(cell0.getNumericCellValue()));
		vo.setEmpName(EgovExcelUtil.getValue(cell1));
		vo.setJob(EgovExcelUtil.getValue(cell2));
		
		log.debug("########### vo is " + vo.getEmpNo());
		log.debug("########### vo is " + vo.getEmpName());
		log.debug("########### vo is " + vo.getJob());
		
		return vo;
	}
}

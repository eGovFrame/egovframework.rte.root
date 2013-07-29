package egovframework.rte.fdl.excel.upload;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

import egovframework.rte.fdl.excel.EgovExcelMapping;
import egovframework.rte.fdl.excel.util.EgovExcelUtil;
import egovframework.rte.fdl.excel.vo.EmpVO;

/**
 * @author sjyoon
 *
 */
public class EgovExcelTestMapping extends EgovExcelMapping {

	protected Log log = LogFactory.getLog(this.getClass());
	
	/* (non-Javadoc)
	 * @see egovframework.rte.fdl.excel.EgovExcelMapping#mappingColumn(org.apache.poi.hssf.usermodel.HSSFRow)
	 */
	@Override
	public EmpVO mappingColumn(HSSFRow row) {
		HSSFCell cell0 = row.getCell(0);
    	HSSFCell cell1 = row.getCell(1);
    	HSSFCell cell2 = row.getCell(2);

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

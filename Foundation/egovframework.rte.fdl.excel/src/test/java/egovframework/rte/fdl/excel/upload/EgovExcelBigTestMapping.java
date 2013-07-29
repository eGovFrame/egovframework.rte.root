package egovframework.rte.fdl.excel.upload;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

import egovframework.rte.fdl.excel.EgovExcelMapping;
import egovframework.rte.fdl.excel.util.EgovExcelUtil;
import egovframework.rte.fdl.excel.vo.ZipVO;

/**
 * @author sjyoon
 *
 */
public class EgovExcelBigTestMapping extends EgovExcelMapping {

	protected Log log = LogFactory.getLog(this.getClass());
	
	/* (non-Javadoc)
	 * @see egovframework.rte.fdl.excel.EgovExcelMapping#mappingColumn(org.apache.poi.hssf.usermodel.HSSFRow)
	 */
	@Override
	public ZipVO mappingColumn(HSSFRow row) {
		HSSFCell cell0 = row.getCell(0);
    	HSSFCell cell1 = row.getCell(1);
    	HSSFCell cell2 = row.getCell(2);
    	HSSFCell cell3 = row.getCell(3);
    	HSSFCell cell4 = row.getCell(4);
    	HSSFCell cell5 = row.getCell(5);
    	HSSFCell cell6 = row.getCell(6);
    	HSSFCell cell7 = row.getCell(7);

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

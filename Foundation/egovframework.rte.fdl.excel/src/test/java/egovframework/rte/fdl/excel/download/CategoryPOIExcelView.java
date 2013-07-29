package egovframework.rte.fdl.excel.download;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import egovframework.rte.fdl.excel.util.AbstractPOIExcelView;
import egovframework.rte.fdl.excel.vo.UsersVO;

public class CategoryPOIExcelView extends AbstractPOIExcelView {

	Logger log  = Logger.getLogger(this.getClass());
	
	@Override
	protected void buildExcelDocument(Map model, XSSFWorkbook wb,
            HttpServletRequest req, HttpServletResponse resp) throws Exception {
        XSSFCell cell = null;

        log.debug("### buildExcelDocument start !!!");
        
        XSSFSheet sheet = wb.createSheet("User List");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        sheet.setDefaultColumnWidth((short) 12);

        // put text in first cell
        cell = getCell(sheet, 0, 0);
        setText(cell, "User List");

        // set header information
        setText(getCell(sheet, 2, 0), "id");
        setText(getCell(sheet, 2, 1), "name");
        setText(getCell(sheet, 2, 2), "description");
        setText(getCell(sheet, 2, 3), "use_yn");
        setText(getCell(sheet, 2, 4), "reg_user");

        log.debug("### buildExcelDocument cast");
        
        
        Map<String, Object> map= (Map<String, Object>) model.get("categoryMap");
        List<Object> categories = (List<Object>) map.get("category");
        
        boolean isVO = false;
        
        if (categories.size() > 0) {
        	Object obj = categories.get(0);
        	isVO = obj instanceof UsersVO;
        }

        for (int i = 0; i < categories.size(); i++) {
        	
        	if (isVO) {	// VO
        		
        		log.debug("### buildExcelDocument VO : " + i + " started!!");
        		
        		UsersVO category = (UsersVO) categories.get(i);
        		
	            cell = getCell(sheet, 3 + i, 0);
	            setText(cell, category.getId());
	
	            cell = getCell(sheet, 3 + i, 1);
	            setText(cell, category.getName());
	
	            cell = getCell(sheet, 3 + i, 2);
	            setText(cell, category.getDescription());
	
	            cell = getCell(sheet, 3 + i, 3);
	            setText(cell, category.getUseYn());
	
	            cell = getCell(sheet, 3 + i, 4);
	            setText(cell, category.getRegUser());
	            
	            log.debug("### buildExcelDocument VO : " + i + " end!!");
	            
        	 } else {	// Map
        		 
        		log.debug("### buildExcelDocument Map : " + i + " started!!");
        		 
        		Map<String, String> category = (Map<String, String>) categories.get(i);
        			
 	            cell = getCell(sheet, 3 + i, 0);
 	            setText(cell, category.get("id"));
 	
 	            cell = getCell(sheet, 3 + i, 1);
 	            setText(cell, category.get("name"));
 	
 	            cell = getCell(sheet, 3 + i, 2);
 	            setText(cell, category.get("description"));
 	
 	            cell = getCell(sheet, 3 + i, 3);
 	            setText(cell, category.get("useyn"));
 	
 	            cell = getCell(sheet, 3 + i, 4);
 	            setText(cell, category.get("reguser"));
 	            
 	            log.debug("### buildExcelDocument Map : " + i + " end!!");
        	 }
        }
    }

}

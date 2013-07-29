package egovframework.rte.fdl.security.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CategoryController {
	Log log = LogFactory.getLog(getClass());

    @RequestMapping("/sale/listCategory.do")
    public void selectCategoryList() throws Exception {
    	log.debug("##### selectCategoryList #####");
        //return new ArrayList();
    }

    @RequestMapping("/sale/addCategoryView.do")
    public String addCategoryView()
            throws Exception {

        return "/sale/registerCategory";
    }
    
    @RequestMapping("/sale/addCategory.do")
    public String addCategory()
            throws Exception {

    	return "forward:/sale/listCategory.do";
    }

    @RequestMapping("/sale/updateCategoryView.do")
    public String updateCategoryView()
            throws Exception {

        return "/sale/registerCategory";
    }

    @RequestMapping("/sale/updateCategory.do")
    public String updateCategory()
            throws Exception {

        return "forward:/sale/listCategory.do";
    }
    
    @RequestMapping("/sale/deleteCategory.do")
    public String deleteCategory()
            throws Exception {

        return "forward:/sale/listCategory.do";
    }

    @RequestMapping("/system/accessDenied.do")
    public String accessDenyView()
            throws Exception {

    	log.debug("##### ACCESS DENY #####");
        return "/system/accessDenied";
    }

}

package egovframework.rte.ptl.mvc.bind;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CommondMapTestController {
	
	@RequestMapping("/test.do")
	public void test(HttpServletRequest request,Map<String, String> commandMap){
		
		for(Iterator it=commandMap.keySet().iterator();it.hasNext();){
			String key = (String) it.next();
			String value = (String) commandMap.get(key);
			request.setAttribute(key, value);
		}
	}

}

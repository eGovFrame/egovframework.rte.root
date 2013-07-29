package egovframework.rte.ptl.mvc.handler;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class InterceptorTestController {
	
	Log logger = LogFactory.getLog(InterceptorTestController.class);
	
	@RequestMapping("/test.do")
	public void test(HttpServletRequest request,
			HttpServletResponse response){
		
		ArrayList array = (ArrayList) request.getAttribute("interceptor");
		array.add("InterceptorTestController");
		request.setAttribute("interceptor", array);		
	}
}
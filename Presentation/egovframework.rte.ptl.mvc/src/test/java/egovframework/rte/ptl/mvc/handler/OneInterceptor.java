package egovframework.rte.ptl.mvc.handler;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class OneInterceptor extends HandlerInterceptorAdapter{
	
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		ArrayList array = new ArrayList();
		array.add("OneInterceptor.preHandle");
		request.setAttribute("interceptor", array);
		return super.preHandle(request, response, handler);
	}
	
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		ArrayList array = (ArrayList) request.getAttribute("interceptor");
		array.add("OneInterceptor.postHandle");
		request.setAttribute("interceptor", array);
		super.postHandle(request, response, handler, modelAndView);			
	}
}
package edu.poly.thtechnology.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthenticationInterceptor implements HandlerInterceptor{
	
	@Autowired
	private HttpSession session;
	
////	source + implement methods
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("pre handle of request " + request.getRequestURI());
		if(session.getAttribute("username") != null) {
			return true;
		}
		
		session.setAttribute("redirect-uri", request.getRequestURI());
		response.sendRedirect("/adminLogin");
		// không tiếp tục thực hiện các yêu cầu của client và chuyển hướng ts trang đăng nhập
		return false;
	}
	
	
}

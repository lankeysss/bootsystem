package com.casit.suwen;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger; 

import com.casit.suwen.datatool.DB3;
import com.casit.suwen.security.CookieSession;
import com.casit.suwen.security.SessionManager;

public class CoreFilter implements Filter {

	static Logger log = Logger.getLogger(CoreFilter.class);
	public void init(FilterConfig config) throws ServletException { 
		SuwenCore.init();
		Event.init();
	} 
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException 
	{
		String uri = ((HttpServletRequest)request).getServletPath();
		Object obj;
		request.setCharacterEncoding(DB3.request_encoding);
		response.setCharacterEncoding(DB3.response_encoding);
		response.setContentType("text/html;charset="+DB3.response_encoding);
		
		CookieSession cs = SessionManager.manager(request, response);
		
		try{  
			obj=SuwenCore.invoke(uri, null, request,response);
			if(obj!=null)
			{response.getWriter().write(obj.toString());}
			else
			{} 
			
			Cookie oItem = new Cookie("CasitSessionId",cs.toString());
			oItem.setMaxAge(-1);
			oItem.setPath("/");
			((HttpServletResponse)response).addCookie(oItem);
			
		}catch(ForwardException e)
		{
			request.getRequestDispatcher(e.forward ).forward(request, response);	
		}catch(PathException e)
		{
			chain.doFilter(request, response);		
		}
	}
	 
	
	  
	
	public void destroy() {
		
	}
	
	public static void main(String[] s)
	{
		SuwenCore.init();
		SuwenCore.invoke("/myclass/hello/{123}/{23124124}", null);
		SuwenCore.invoke("/myclass/hello", null);
		SuwenCore.invoke("/myclass/hello", null);
		SuwenCore.invoke("/myclass/hello", null);
	}
}

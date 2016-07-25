package com.casit.suwen.security;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionManager {
	public static CookieSession manager(ServletRequest request, ServletResponse response){
		CookieSession cs = null;
		HttpSession hs = ((HttpServletRequest)request).getSession();
		final Cookie[] oCookies = ((HttpServletRequest)request).getCookies();
		if (oCookies != null)  
	    {  
	        for (final Cookie oItem : oCookies)  
	        {  
	            final String sName = oItem.getName(); 
	            if(sName.equals("CasitSessionId")){
	            	cs = new CookieSession(oItem.getValue(),hs);
	            }
	        }	        
	    }
		if(cs==null){
			String ssid = SessionIdGenerator.newSessionId();
			cs = new CookieSession(ssid,System.currentTimeMillis(),hs);
		}else{
			cs.checkB4UpdateTS();
		}	
		request.setAttribute("CasitCookieSession_", cs);
		
		return cs;
	}
}

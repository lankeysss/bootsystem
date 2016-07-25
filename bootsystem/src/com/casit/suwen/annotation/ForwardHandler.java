package com.casit.suwen.annotation;

import java.util.ArrayList; 
import java.util.regex.Matcher;
import java.util.regex.Pattern;
  

public class ForwardHandler {
	 
	  
	 
	public static String handleRequest(String forwardstr,Object res) 
	{ 
		if(  (!forwardstr.contains(",")) && (!forwardstr.contains("<forward>")))
			{return forwardstr;	} 
		String[] forwardstrs= getstrs(forwardstr);
		if(forwardstrs.length==1)
			{return forwardstrs[0];}
		else
		{
			Integer i=new Integer(0);
			try{ 
				i=Integer.valueOf((String)res); 
			}catch (Exception e) {
				return forwardstrs[forwardstrs.length-1]; 
			}
			if( 0<i && i<(forwardstrs.length))
			{
				return forwardstrs[i-1];
			}else
			{
				return forwardstrs[forwardstrs.length-1];
			}
		}
	
	
	}
	
	public static String [] getstrs(String forwardstr)
	{
		if (forwardstr.contains("<forward>")&& forwardstr.contains("</forward>")) {
			Pattern p = Pattern.compile("<forward>(.*?)</forward>");
			Matcher m = p.matcher(forwardstr);
			ArrayList<String> alist=new ArrayList<String>();
			while(m.find()) {
				String s = m.group(1);   
				alist.add(new String(s));
			}
			String[] ress=new String[alist.size()];
			for (int i = 0; i < alist.size(); i++) {
				ress[i]=alist.get(i);
			}
			return ress;
		}else
		{
			return forwardstr.split(",");
		}
		
	}
	

}

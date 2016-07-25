package com.casit.suwen.annotation;
  
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 


import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 



import com.casit.json.JsonO; 
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.DataInterface;
import com.casit.suwen.datatool.MultiDataSourceFactory;
import com.casit.suwen.upload.SmartUpload;

public class StringHandler  {
 
	  
	 
	public static Object handleRequest(String result ,String distr,Map<String, String>  map,ServletRequest request,ServletResponse response) { 
		 
		if(result!=null)
		{
			Pattern psql = Pattern.compile("^@(sql|tpl)\\((.*?)\\):");
			//Pattern psql2 = Pattern.compile("^@(forward)\\((.*?)\\):");
			//Pattern psql3 = Pattern.compile("^@(download)\\((.*?)\\):");
			String unquoted = "";
			String order = "";
			String realsql = "";
			String type = "";
			DataInterface di=null; 
			if(distr!=null)
				{ di = MultiDataSourceFactory.getDataInterface(distr);}
			else
			    { di = MultiDataSourceFactory.getDataInterface(DB3.dbcpname);}
			Matcher m = psql.matcher(result);	 
			if(m.find()){
				Object[] o= SelectHandler.sqltpl(unquoted, order, realsql, type, m, map, request);
				unquoted=(String)o[0];order=(String)o[1];realsql=(String)o[2];type=(String)o[3];m=(Matcher)o[4];
				
				JsonO store= di.getAutoStore(realsql, order, unquoted, map, request);
				return store;
			} 			
			else if(result.startsWith("@forward():"))
			{
				try {
					String path = result.replace("@forward():", "");
					if(!path.startsWith("/")) path = "/"+path;
					request.getRequestDispatcher(path).forward(request, response);
				} catch (Exception e) { 
					e.printStackTrace();
				}
				return null;
			}
			
			else if(result.startsWith("@download():"))
			{
				SmartUpload su=new SmartUpload();
				HttpServletRequest hreq=(HttpServletRequest) request;
				HttpServletResponse hres=(HttpServletResponse) response;
				try {
					su.initialize(hreq.getSession().getServletContext(), hreq, hres); 
					su.downloadFile(result.replace("@download():", ""));
				} catch ( Exception e) {e.printStackTrace();	}   
				return null;
			}else{
				return result;
			} 
		}else
		{
			return null;
		}
	}

}

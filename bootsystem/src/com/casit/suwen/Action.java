package com.casit.suwen;
 
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.datatool.D;
import com.casit.suwen.datatool.Template;
import com.casit.suwen.security.CookieSession;
import com.casit.suwen.security.PrivilegeFilter;
import com.casit.suwen.upload.DownloadFile;
import com.casit.suwen.upload.SmartUpload;
import com.casit.suwen.upload.UploadException;
import com.casit.suwen.upload.UploadFile;
import com.casit.suwen.upload.UploadFiles;

public class Action { 
	static Logger log = Logger.getLogger(Action.class);
	private Class<?> dclass = null;
	private Method method = null;
	private List<String> params = new ArrayList<String>();
	private List<String> paramsja = new ArrayList<String>();
	public Class<?> getDclass() {
		return dclass;
	}
	public void setDclass(Class<?> dclass) {
		this.dclass = dclass;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public void addParam(String param){
		this.params.add(param);
	}
	public void addParamja(String param){
		this.paramsja.add(param);
	}
	public List<String> getParams(){
		return this.params;
	}
	public SmartUpload initsuup(ServletRequest request,ServletResponse response)
	{
		SmartUpload su=new SmartUpload();
		HttpServletRequest hreq=(HttpServletRequest) request;
		HttpServletResponse hres=(HttpServletResponse) response;
		try {
			su.initialize(hreq.getSession().getServletContext(), hreq, hres);
			su.upload();
		} catch (ServletException e) {e.printStackTrace();	} 
		catch (IOException e) { e.printStackTrace();}
		catch (UploadException e) {	e.printStackTrace();} 
		return su;
	}
	
	public SmartUpload initsudown(ServletRequest request,ServletResponse response)
	{
		SmartUpload su=new SmartUpload();
		HttpServletRequest hreq=(HttpServletRequest) request;
		HttpServletResponse hres=(HttpServletResponse) response;
		try {
			su.initialize(hreq.getSession().getServletContext(), hreq, hres); 
		} catch ( Exception e) {e.printStackTrace();	}  
		return su;
	}
	
	public Object invoke(Map<String,String> map,ServletRequest request,ServletResponse response,String distr){
		SmartUpload su=null;
		int size = params.size();
		Object[] args = new Object[size];
		String info="";
		Class<?> paramtypes[]=method.getParameterTypes(); 
		for(int i=0;i<size;i++)
		{  
			String nm = params.get(i);
			if(nm==null){nm=paramsja.get(i);}
			if(nm==null){nm="CASIT_ACTION_NO_PARAM_NAME_";} 
			if(nm.equals("@request") || paramtypes[i].equals(HttpServletRequest.class) 
					|| paramtypes[i].equals(ServletRequest.class) ){
				requestsetParameter(request, map ); 
				args[i] = request;
				info += "," + "@request";
			}else if(nm.equals("@response")|| paramtypes[i].equals(HttpServletResponse.class) 
					|| paramtypes[i].equals(ServletResponse.class) ){
				args[i] = response;
				info += ","+"@response";
			}
			else if(paramtypes[i].equals(D.class)  ){
				requestsetParameter(request, map );
				if(distr!=null)
					{args[i] = new D(map,(HttpServletRequest)request,(HttpServletResponse)response,null,distr);}
				else
					{args[i] = new D(map,(HttpServletRequest)request,(HttpServletResponse)response,null);}
				info += ","+ "@D";
			}
			else if(paramtypes[i].equals(Template.class)  ){
				requestsetParameter(request, map );
				args[i] = new Template(request);
				info += ","+ "@Template";
			}else if(paramtypes[i].equals(SmartUpload.class)  ){
				if(su==null)
					{su=initsudown(request, response); } 
				args[i] = su;
				info += "," + "@SmartUpload";
			}else if(paramtypes[i].equals(UploadFiles.class)  ){
				if(su==null)
					{su=initsuup(request, response); } 
				args[i] = su.getFiles();
				info += ","+ "@UploadFiles";
			}else if(paramtypes[i].equals(UploadFile.class)  ){
				if(su==null)
					{su=initsuup(request, response); } 
				args[i] =su.getFiles().getFile(0); 
				info += ","+ "@UploadFile";
			}else if(paramtypes[i].equals(DownloadFile.class)  ){
				if(su==null)
					{su=initsudown(request, response); } 
				args[i] =new DownloadFile(su); 
				info += "," + "@DownloadFile";
			}else if(paramtypes[i].equals(PrivilegeFilter.class)  ){ 
				HttpServletRequest httprequest=(HttpServletRequest)request;
				PrivilegeFilter pf=(PrivilegeFilter)(httprequest.getSession().getAttribute("PrivilegeFilter"));
				args[i] =pf;
				info += "," + "@PrivilegeFilter";
			}else if(paramtypes[i].equals(CookieSession.class)){
				args[i] = request.getAttribute("CasitCookieSession_");
				info += "," + "@CookieSession";
			}else if(map.containsKey(nm)){ 
				args[i] = map.get(nm);
				if (paramtypes[i].equals(JsonA.class))
				{	args[i] =new JsonA((String)args[i] );
				}else if(paramtypes[i].equals(JsonO.class))
				{	args[i] =new JsonO((String)args[i] );}	
				info += "," + "\""+args[i]+"\"";
			}else{ 
				args[i] = (request!=null) ?request.getParameter(nm):null;
				if (paramtypes[i].equals(JsonA.class))
				{	args[i] =new JsonA((String)args[i] );
				}else if(paramtypes[i].equals(JsonO.class))    
				{	args[i] =new JsonO((String)args[i] );}			 
				info += ","+ "\""+args[i]+"\"";    
			}
		}
		if(info.length()>0){info =info.substring(1, info.length());}
		String logstr = "------------------------------------------------------------------\n("+
			dclass.getSimpleName()+".java:1) method:"+method.getName()+"("+info+")";
		log.debug(logstr);
		try {
			return method.invoke(dclass.newInstance(), args);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(info);
		}
		return null;
	}
	
	
	@SuppressWarnings("rawtypes")
	public static void requestsetParameter(ServletRequest request,String paramname,String paramvalue)
	{
		try {  
            Class clazz = request.getClass();  
            Field requestField = clazz.getDeclaredField("request");  
            requestField.setAccessible(true);  
            Object innerRequest = requestField.get(request);//获取到request对象   
  
  
            //设置尚未初始化 (否则在获取一些参数的时候，可能会导致不一致)   
            Field field = innerRequest.getClass().getDeclaredField("parametersParsed");  
            field.setAccessible(true);  
            field.setBoolean(innerRequest , false);  
  
  
            Field coyoteRequestField = innerRequest.getClass().getDeclaredField("coyoteRequest");  
            coyoteRequestField.setAccessible(true);  
            Object coyoteRequestObject = coyoteRequestField.get(innerRequest);//获取到coyoteRequest对象   
  
  
            Field parametersField = coyoteRequestObject.getClass().getDeclaredField("parameters");  
            parametersField.setAccessible(true);  
            Object parameterObject = parametersField.get(coyoteRequestObject);//获取到parameter的对象   
//获取hashtable来完成对参数变量的修改   
            Field hashTabArrField = parameterObject.getClass().getDeclaredField("paramHashStringArray");  
            hashTabArrField.setAccessible(true);  
            @SuppressWarnings("unchecked")  
            Map<String,String[]> map = (Map<String,String[]>)hashTabArrField.get(parameterObject);  
            map.put(paramname, new String[] {paramvalue});  
//也可以通过下面的方法，不过下面的方法只能添加参数，如果有相同的key，会追加参数，即，同一个key的结果集会有多个   
//            Method method = parameterObject.getClass().getDeclaredMethod("addParameterValues" , String.class , String[].class);   
//            method.invoke(parameterObject , paramname , new String[] {paramvalue , "sssss"});   
  
  
        } catch (Exception e) {  
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.   
        }    
	}
	
	
	@SuppressWarnings("rawtypes")
	public static void requestsetParameter(ServletRequest request,Map<String,String> mapinto)
	{
		try {  
            Class clazz = request.getClass();  
            Field requestField = clazz.getDeclaredField("request");  
            requestField.setAccessible(true);  
            Object innerRequest = requestField.get(request);//获取到request对象   
  
  
            //设置尚未初始化 (否则在获取一些参数的时候，可能会导致不一致)   
            Field field = innerRequest.getClass().getDeclaredField("parametersParsed");  
            field.setAccessible(true);  
            field.setBoolean(innerRequest , false);  
  
  
            Field coyoteRequestField = innerRequest.getClass().getDeclaredField("coyoteRequest");  
            coyoteRequestField.setAccessible(true);  
            Object coyoteRequestObject = coyoteRequestField.get(innerRequest);//获取到coyoteRequest对象   
  
  
            Field parametersField = coyoteRequestObject.getClass().getDeclaredField("parameters");  
            parametersField.setAccessible(true);  
            Object parameterObject = parametersField.get(coyoteRequestObject);//获取到parameter的对象   
//获取hashtable来完成对参数变量的修改   
            Field hashTabArrField = parameterObject.getClass().getDeclaredField("paramHashStringArray");  
            hashTabArrField.setAccessible(true);  
            @SuppressWarnings("unchecked")  
            Map<String,String[]> map = (Map<String,String[]>)hashTabArrField.get(parameterObject);  
            
            for (Iterator<String> iterator = mapinto.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				map.put(key, new String[] {mapinto.get(key).toString()}); 
			}
             
//也可以通过下面的方法，不过下面的方法只能添加参数，如果有相同的key，会追加参数，即，同一个key的结果集会有多个   
//            Method method = parameterObject.getClass().getDeclaredMethod("addParameterValues" , String.class , String[].class);   
//            method.invoke(parameterObject , paramname , new String[] {paramvalue , "sssss"});   
  
  
        } catch (Exception e) {  
        	log.error("action-requestsetParameter-error");
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.   
        }    
	}
}

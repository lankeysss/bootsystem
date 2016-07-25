package com.casit.suwen;

import java.util.HashMap;
import java.util.Map;
  

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

import com.casit.suwen.annotation.Forward;
import com.casit.suwen.annotation.ForwardHandler;
import com.casit.suwen.annotation.Save;
import com.casit.suwen.annotation.SaveHandler;
import com.casit.suwen.annotation.Select;
import com.casit.suwen.annotation.SelectHandler;
import com.casit.suwen.annotation.Source;
import com.casit.suwen.annotation.StringHandler;
import com.casit.suwen.annotation.Update;
import com.casit.suwen.annotation.UpdateHandler;
import com.casit.suwen.datatool.DB3;

public class SuwenCore {
	private static PathNode root = new PathNode("root"); 

	static Logger log = Logger.getLogger(SuwenCore.class);

	public static void init()
	{ 
		log.debug("begin scan class file in "+DB3.scannerpackage+".* ");
		new Scanner().scan2PathTree(root);
		log.debug("end scan"); 
	}

	public static Object invoke(String uri,Map<String,String> map)
	{
 
		return invoke(uri, map, null,null);
	}
	
	public static Object invoke(String uri,Map<String,String> map,ServletRequest request,ServletResponse response)
	{
		if(map==null){map= new HashMap<String,String>();}
		if(!uri.startsWith("/")){uri="/"+uri;}
		Action ac = root.getAction(uri, map);  
		Object returnobj=null;
		if(ac!=null){
			//如果另有数据源，则建立该数据源
			Source s = (Source)ac.getMethod().getAnnotation(Source.class); 
			String distr=null;			if(s!=null) {distr=s.value();}
			
			//调用
			Object result = ac.invoke(map,request,response,distr);  
			
			//优先检查是否有select、update、save的annotation。
			Select sel ;
			Update upd ;		
			Save sav;
			Forward fwd;
			if((sel= (Select)ac.getMethod().getAnnotation(Select.class))!=null)
			{
				returnobj=SelectHandler.handleRequest(sel.value(),distr,map,request);
			}else if((upd=(Update)ac.getMethod().getAnnotation(Update.class))!=null)
			{
				returnobj=UpdateHandler.handleRequest(upd.value(),distr,map,request);
			}else if((sav=(Save)ac.getMethod().getAnnotation(Save.class))!=null)
			{
				returnobj=SaveHandler.handleRequest(sav.value(),distr,map,request);
			}else 
			{
				returnobj=StringHandler.handleRequest((result!=null)?result.toString():null,distr,map,request,response);
			} 
			
			if ((fwd=(Forward)ac.getMethod().getAnnotation(Forward.class)) !=null) {
				String disstr=ForwardHandler.handleRequest(fwd.value(), returnobj); 
				throw new ForwardException(disstr);
			}
			
		}
		else
		{
			throw new PathException();
		}
		
		return returnobj;
	}
	
	public static PathNode getroot()
	{
		return root;
	}

}

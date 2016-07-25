package com.casit.suwen.annotation;
 


import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 

import javax.servlet.ServletRequest;

import org.apache.log4j.Logger;

import com.casit.json.JsonO; 
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.DataInterface;
import com.casit.suwen.datatool.MultiDataSourceFactory;
import com.casit.suwen.datatool.Template;

public class SelectHandler   {

	private static Logger log = Logger.getLogger(SelectHandler.class);
	  
	 
	public static Object handleRequest(String sel ,String distr,Map<String,String> map,ServletRequest request) {
				 	
		//非构造参数
		 Pattern psql = Pattern.compile("^@(sql|tpl)\\((.*?)\\):");
		 String unquoted = "";
		 String order = "";
		 String realsql = "";
		 String type = "";
		 DataInterface di=null;
		 
		if(distr!=null)
			{ di = MultiDataSourceFactory.getDataInterface(distr);}
		else
		    { di = MultiDataSourceFactory.getDataInterface(DB3.dbcpname);}
		
		if(sel!=null){
			try{
				Matcher m = psql.matcher(sel);
				if(m.find()){
					Object[] o= sqltpl(unquoted, order, realsql, type, m, map, request);
					unquoted=(String)o[0];order=(String)o[1];realsql=(String)o[2];type=(String)o[3];m=(Matcher)o[4];
					JsonO store= di.getAutoStore(realsql, order, unquoted, map, request);				 
					return store;
				}else
				{
					realsql = sel;
					realsql = Template.apply(realsql,map,request);
					JsonO store= di.getAutoStore(realsql, order, unquoted, map, request);
					return store;
				}
			}catch (Exception e) {
				log.error(e.getMessage());
				return "failure"; 
			} 
		} else
		{
			return null;
		}


	}
	
	//处理sql和tpl字符串的方法。-------ps：之前是涵在doFilter中，因增加了select标记需要重用，故单独拿出成方法。
	public static Object[] sqltpl(String unquoted ,String order ,String realsql ,String type,Matcher m,Map<String,String> map,ServletRequest request)
	{

		StringBuffer sbuf = new StringBuffer();
		type = m.group(1);
		String tem = m.group(2);
		int i = tem.indexOf("#");
		if(i==-1)unquoted = tem;
		else {
			unquoted = tem.substring(0,i);
			order = tem.substring(i+1);
		}
		m.appendReplacement(sbuf, "");
		m.appendTail(sbuf);
		realsql = sbuf.toString(); 
		realsql = Template.apply(realsql,map,request);					
		 
		
		Object[] ret={unquoted,order,realsql,type,m};
		return ret;
	}

}

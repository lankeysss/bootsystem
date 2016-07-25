package com.casit.suwen.annotation;
 
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.log4j.Logger;

import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.DataInterface;
import com.casit.suwen.datatool.MultiDataSourceFactory;
import com.casit.suwen.datatool.Template;

public class UpdateHandler  { 
	private static Logger log = Logger.getLogger(UpdateHandler.class); 
	
	public static Object handleRequest(String upd,String distr,Map<String, String> map,ServletRequest request) {
		if(upd!=null){ 
			DataInterface di=null;
			String realsql = ""; 
			if(distr!=null)
				{ di = MultiDataSourceFactory.getDataInterface(distr);}
			else
			    { di = MultiDataSourceFactory.getDataInterface(DB3.dbcpname);}
			try
			{
				realsql =upd; 
				realsql = Template.apply(realsql,map,request);					 
				di.update(realsql) ;
			}catch(Exception e)
			{
				log.error(e.getMessage());
				return "failure";
			}
			return "success";
		}
		else
		{ 
			return null;			
		}
	}

}

package com.casit.suwen.annotation;
 
import java.util.Map; 

import javax.servlet.ServletRequest;

import org.apache.log4j.Logger;

import com.casit.json.JsonA;
import com.casit.json.JsonO; 
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.DataInterface;
import com.casit.suwen.datatool.MultiDataSourceFactory;

public class SaveHandler  {

	private static Logger log = Logger.getLogger(SaveHandler.class);
	 
	 
	 
	public static Object handleRequest(String sav,String distr,Map<String, String> map,ServletRequest request) {

		String jostr=null;
		DataInterface di=null;
		if(distr!=null)
			{ di = MultiDataSourceFactory.getDataInterface(distr);}
		else
		    { di = MultiDataSourceFactory.getDataInterface(DB3.dbcpname);}
		
		String savstr=sav;
		String savejsonname=savstr.split("#")[0].split(",")[0];
		String savetablename=savstr.split("#")[0].split(",")[1];
		String saveidcol=savstr.split("#")[0].split(",")[2];
		String saveunquoted="";
		if(savstr.split("#").length==1)
		{saveunquoted="";}
		else
		{saveunquoted=savstr.split("#")[1];	}		//log.debug(savejsonname+" "+savetablename+" "+saveidcol+" "+saveunquoted);
		if  (jostr==null)
		{
			if(request!=null)
			{jostr=request.getParameter(savejsonname);}
			else
			{jostr=map.get(savejsonname).toString();}
		} 
		if(sav!=null)
		{
			if (jostr!=null)
			{
				try
				{
					JsonO jo=new JsonO(jostr);
					try{
						di.saveJsonOToDB(jo, savetablename, saveidcol, saveunquoted);
					}catch (Exception e) {
						log.error(e.getMessage());
						return "failure";
					}
					return "success"; 
				}catch (Exception e1) {
					try
					{
						JsonA ja=new JsonA(jostr);
						try{
							di.saveJsonAToDB(ja, savetablename, saveidcol, saveunquoted);
						}catch (Exception e2) {
							log.error(e2.getMessage());
							return "failure";
						}	
						return "success"; 
					}catch (Exception e3) {						
						log.error("not json object");  	
						return "failure";
					} 
				}
			}else
			{
				log.error("request has no json object");  
				return "failure";
			} 
		}
		else
		{ 
			return null;	
		}

	}

}

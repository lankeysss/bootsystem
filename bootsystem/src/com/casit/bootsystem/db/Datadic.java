package com.casit.bootsystem.db;

import java.util.HashMap;
import java.util.Map;

import com.casit.bootsystem.sys.Log;
import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;
import com.casit.suwen.security.CookieSession;

@Path
public class Datadic {  
	/**
	 * *****************************************************************
	 * 功能描述：数据查询
	 * 参数 ：  keys   查询关键字
	 * *****************************************************************
	 */
	@Post
	public JsonO info(String keys, String limit, String offset){
		JsonO json = new JsonO();
		String tpl = "";
		if(keys != "" && keys != null){
			tpl = "select * from sys_cs where syscode like '%${keys}%' or sysnm like '%${keys}%' order by sysid";
			tpl = Template.apply(tpl,keys,keys);
		}else{
			tpl = "select * from sys_cs order by sysid";
			tpl = Template.apply(tpl);
		}
		Map<String, String> hm=new HashMap<String, String>();
		hm.put("start", offset);
		hm.put("limit", limit);
		json = DB3.getAutoStore(tpl, "", "", hm, null);
		JsonA ja = json.getJsonA("root");
		json.remove("root");
		json.put("rows", ja);
		return json;
		
	} 
	/**
	 * *****************************************************************
	 * 功能描述：删除系统用户
	 * 参数 ：  node   sysid
	 * *****************************************************************
	 */
	@Post
	public String delsysuser(String node,CookieSession cs){
		try{
			Log.syslog("删除数据","删除系统用户", cs);
			node = node.replace("insert_", "");
			DB3.update(Template.apply("delete from sys_cs where sysid=${node}",node));
			return "success";
		}catch(Exception e){
			return "fail";			
		}
	}
	/**
	 * *****************************************************************
	 * 功能描述：系统用户保存
	 * 参数 ：  
	 * *****************************************************************
	 */
	@Post
	public String save(String jstr, CookieSession cs){
		JsonO json = new JsonO(jstr);
		try{
			Log.syslog("保存数据","保存系统用户信息", cs);
			DB3.saveJsonOToDB(json, "sys_cs", "sysid", "");
			return "success";
		}catch(Exception e){
			return "fail";			
		}
	}
	/**
	 * *****************************************************************
	 * 功能描述：getNewID
	 * 参数 ：  
	 * *****************************************************************
	 */
	@Post
	public String sysNID(){
		return DB3.getNewID("sys_cs", 1);
	}
	
}

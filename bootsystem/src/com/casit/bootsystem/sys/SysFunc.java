package com.casit.bootsystem.sys;

import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;
import com.casit.suwen.security.CookieSession;

@Path
public class SysFunc {  
	/**
	 * *****************************************************************
	 * 功能描述：系统功能菜单数据查询
	 * 参数 ： 
	 * *****************************************************************
	 */
	@Post
	public JsonA info(){
		String tpl = "select * from sys_funcs order by funcid";
		return DB3.getResultAsJsonA(Template.apply(tpl), "");
		
	} 
	/**
	 * *****************************************************************
	 * 功能描述：删除系统功能菜单
	 * 参数 ：  node   funcid
	 * *****************************************************************
	 */
	@Post
	public String delsysfunc(String node, String parents, CookieSession cs){
		try{
			Log.syslog("删除数据","删除系统功能菜单", cs);
			node = node.replace("insert_", "");
			DB3.update(Template.apply("delete from sys_funcs where funcid=${node}",node));
			String tpl = "select * from sys_funcs where parents=${parents}";
			JsonA jsons = DB3.getResultAsJsonA(Template.apply(tpl,parents), "");
			if(jsons.size() == 0){
				DB3.update(Template.apply("update sys_funcs set leaf='true' where funcid=${parents}",parents));
			}
			return "success";
		}catch(Exception e){
			return "fail";			
		}
	} 
	/**
	 * *****************************************************************
	 * 功能描述：系统功能菜单保存
	 * 参数 ：  
	 * *****************************************************************
	 */
	@Post
	public String savefunc(String jstr, CookieSession cs){
		JsonO json = new JsonO(jstr);
		json.remove("parentname");
		String parents = json.getString("parents");
		try{
			Log.syslog("保存数据","保存系统功能菜单信息", cs);
			DB3.saveJsonOToDB(json, "sys_funcs", "funcid", "");
			DB3.update(Template.apply("update sys_funcs set leaf='false' where funcid=${parents}",parents));
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
	public String sysfuncNID(){
		return DB3.getNewID("sys_funcs", 1);
	}
	
}

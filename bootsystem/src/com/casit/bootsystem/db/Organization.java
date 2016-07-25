package com.casit.bootsystem.db;

import com.casit.bootsystem.sys.Log;
import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;
import com.casit.suwen.security.CookieSession;

@Path
public class Organization {  
	/**
	 * *****************************************************************
	 * 功能描述：组织机构数据查询
	 * 参数 ： 
	 * *****************************************************************
	 */
	@Post
	public JsonA info(){
		String tpl = "select * from sys_dept order by deptid";
		return DB3.getResultAsJsonA(Template.apply(tpl), "");
		
	} 
	/**
	 * *****************************************************************
	 * 功能描述：删除组织机构
	 * 参数 ：  node   deptid
	 * *****************************************************************
	 */
	@Post
	public String delsysfunc(String node, String parents, CookieSession cs){
		try{
			Log.syslog("删除数据","删除系统功能菜单", cs);
			node = node.replace("insert_", "");
			DB3.update(Template.apply("delete from sys_dept where deptid=${node}",node));
			String tpl = "select * from sys_dept where parents=${parents}";
			JsonA jsons = DB3.getResultAsJsonA(Template.apply(tpl,parents), "");
			if(jsons.size() == 0){
				DB3.update(Template.apply("update sys_dept set leaf='true' where deptid=${parents}",parents));
			}
			return "success";
		}catch(Exception e){
			return "fail";			
		}
	} 
	/**
	 * *****************************************************************
	 * 功能描述：组织机构保存
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
			DB3.saveJsonOToDB(json, "sys_dept", "deptid", "");
			DB3.update(Template.apply("update sys_dept set leaf='false' where deptid=${parents}",parents));
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
		return DB3.getNewID("sys_dept", 1);
	}
	
}

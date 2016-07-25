package com.casit.bootsystem.sys;

import java.util.HashMap;
import java.util.Map;

import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;
import com.casit.suwen.security.CookieSession;
@Path
public class SysRole {  
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
			tpl = "select * from sys_role where rolename like '%${keys}%' or rolecode like '%${keys}%' "+
				  "order by sortnouser";
			tpl = Template.apply(tpl,keys,keys);
		}else{
			tpl = "select * from sys_role order by sortnouser";
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
	 * 功能描述：数据查询
	 * 参数 ：  keys   查询关键字
	 * *****************************************************************
	 */
	@Post
	public JsonO userinfo(String node, String keys, String limit, String offset){
		JsonO json = new JsonO();
		String tpl = "";
		if(node != "" && node != null){
			if(keys != "" && keys != null){
				tpl = "select userid,loginnm,usernm,roleidf,isvalid,memo from sys_user where roleidf = ${node} "+
					  "and ( usernm like '%${keys}%' or loginnm like '%${keys}%') order by userid";
				tpl = Template.apply(tpl,node,keys,keys);
			}else{
				tpl = "select userid,loginnm,usernm,roleidf,isvalid,memo from sys_user where roleidf = ${node} "+
					  "order by userid";
				tpl = Template.apply(tpl,node);
			}
			Map<String, String> hm=new HashMap<String, String>();
			hm.put("start", offset);
			hm.put("limit", limit);
			json = DB3.getAutoStore(tpl, "", "", hm, null);
			JsonA ja = json.getJsonA("root");
			json.remove("root");
			json.put("rows", ja);
		}
		return json;
		
	} 
	/**
	 * *****************************************************************
	 * 功能描述：getNewID
	 * 参数 ：  
	 * *****************************************************************
	 */
	@Post
	public String sysroleNID(){
		return DB3.getNewID("sys_role", 1);
	}
	/**
	 * *****************************************************************
	 * 功能描述：系统角色保存
	 * 参数 ：  
	 * *****************************************************************
	 */
	@Post
	public String saverole(String jstr, CookieSession cs){
		JsonO json = new JsonO(jstr);
		try{
			Log.syslog("保存数据","保存系统角色信息", cs);
			DB3.saveJsonOToDB(json, "sys_role", "roleid", "sortnouser");
			return "success";
		}catch(Exception e){
			return "fail";			
		}
	}
	/**
	 * *****************************************************************
	 * 功能描述：删除系统角色
	 * 参数 ：  node   roleid
	 * *****************************************************************
	 */
	@Post
	public String delsysrole(String node,CookieSession cs){
		try{
			Log.syslog("删除数据","删除系统角色", cs);
			node = node.replace("insert_", "");
			DB3.update(Template.apply("delete from sys_role where roleid=${node}",node));
			return "success";
		}catch(Exception e){
			return "fail";			
		}
	}
	/**
	 * *****************************************************************
	 * 功能描述：角色查询
	 * 参数 ：  
	 * *****************************************************************
	 */
	@Post
	public JsonA roleinfo(){
		String tpl = "select * from sys_role order by roleid";
		return DB3.getResultAsJsonA(Template.apply(tpl), "");
	}
	/**
	 * *****************************************************************
	 * 功能描述：系统用户保存
	 * 参数 ：  
	 * *****************************************************************
	 */
	@Post
	public String saveuser(String jstr, CookieSession cs){
		JsonO json = new JsonO(jstr);
		try{
			Log.syslog("保存数据","保存系统用户信息", cs);
			DB3.saveJsonOToDB(json, "sys_user", "userid", "sortnouser");
			return "success";
		}catch(Exception e){
			return "fail";			
		}
	}
	/**
	 * *****************************************************************
	 * 功能描述：删除系统用户
	 * 参数 ：  node   userid
	 * *****************************************************************
	 */
	@Post
	public String delsysuser(String node,CookieSession cs){
		try{
			Log.syslog("删除数据","删除系统用户", cs);
			node = node.replace("insert_", "");
			DB3.update(Template.apply("delete from sys_user where userid=${node}",node));
			return "success";
		}catch(Exception e){
			return "fail";			
		}
	}
}

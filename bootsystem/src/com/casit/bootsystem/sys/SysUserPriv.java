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
public class SysUserPriv {  
	/**
	 * *****************************************************************
	 * 功能描述：用户数据查询
	 * 参数 ：  keys   查询关键字
	 * *****************************************************************
	 */
	@Post
	public JsonO info(String keys, String limit, String offset){
		JsonO json = new JsonO();
		String tpl = "";
		if(keys != "" && keys != null){
			tpl = "select r.rolename,u.userid,u.loginnm,u.usernm,u.roleidf,u.isvalid,u.sortnouser,u.memo "+
				  "from sys_user u, sys_role r where u.roleidf = r.roleid "+
				  "and (u.usernm like '%${keys}%' or u.loginnm like '%${keys}%') order by userid";
			tpl = Template.apply(tpl,keys,keys);
		}else{
			tpl = "select r.rolename,u.userid,u.loginnm,u.usernm,u.roleidf,u.isvalid,u.sortnouser,u.memo "+
				  "from sys_user u, sys_role r where u.roleidf = r.roleid order by userid";
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
	 * 功能描述：系统功能菜单权限数据查询
	 * 参数 ： 
	 * *****************************************************************
	 */
	@Post
	public JsonA funcinfo(String userid){
		String tpl = "select f.funcid,f.funcnm,f.parents,f.leaf,s.userid,s.funcid as id from sys_funcs f "+
					 "left join sys_user_func s on f.funcid = s.funcid and s.userid=${userid} where "+
					 "f.isvalid=1 order by f.funcid";
		return DB3.getResultAsJsonA(Template.apply(tpl,userid), "");
	} 
	/**
	 * *****************************************************************
	 * 功能描述：系统用户权限保存
	 * 参数 ：  
	 * *****************************************************************
	 */
	@Post
	public String savepriv(String jstr, CookieSession cs){
		JsonA json = new JsonA(jstr);
		System.out.println(json);
		String userid = json.getJsonO(0).getString("userid");
		try{
			DB3.update(Template.apply("delete from sys_user_func where userid=${userid}",userid));
			for(int i=0; i<json.size(); i++){
				String userfuncid = DB3.getNewID("sys_user_func", 1);
				JsonO j = json.getJsonO(i);
				j.putQuoted("userfuncid", "insert_"+userfuncid);
			}
			System.out.println(json);
			DB3.saveJsonAToDB(json, "sys_user_func", "userfuncid", "sortnouser");
			Log.syslog("保存数据","保存系统用户权限信息", cs);
			return "success";
		}catch(Exception e){
			return "fail";			
		}
	}
	
	
}

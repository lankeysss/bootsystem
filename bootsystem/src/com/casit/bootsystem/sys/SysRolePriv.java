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
public class SysRolePriv {  
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
	 * 功能描述：系统功能菜单权限数据查询
	 * 参数 ： 
	 * *****************************************************************
	 */
	@Post
	public JsonA funcinfo(String roleid){
		String tpl = "select f.funcid,f.funcnm,f.parents,f.leaf,s.roleid,s.funcid as id from sys_funcs f "+
					 "left join sys_role_func s on f.funcid = s.funcid and s.roleid=${roleid} where "+
					 "f.isvalid=1 order by f.funcid";
		return DB3.getResultAsJsonA(Template.apply(tpl,roleid), "");
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
		String roleid = json.getJsonO(0).getString("roleid");
		try{
			DB3.update(Template.apply("delete from sys_role_func where roleid=${roleid}",roleid));
			for(int i=0; i<json.size(); i++){
				String rolefunid = DB3.getNewID("sys_role_func", 1);
				JsonO j = json.getJsonO(i);
				j.putQuoted("rolefunid", "insert_"+rolefunid);
			}
			System.out.println(json);
			DB3.saveJsonAToDB(json, "sys_role_func", "rolefunid", "sortnouser");
			Log.syslog("保存数据","保存系统用户权限信息", cs);
			return "success";
		}catch(Exception e){
			return "fail";			
		}
	}
	
	
}

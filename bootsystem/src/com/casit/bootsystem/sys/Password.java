package com.casit.bootsystem.sys;

import com.casit.json.JsonA;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;
import com.casit.suwen.security.CookieSession;
import com.casit.suwen.security.MD5;

/**
 * *****************************************************************
 * 功能描述：用户密码管理 
 * *****************************************************************
 */
@Path("password")
public class Password{
	/**
	 * *****************************************************************
	 * 功能描述：保存功能
	 * *****************************************************************
	 */
	@Post("save")
	public String save(String userid, String oldpassword, String newpassword, CookieSession cs){ 
		JsonA arr = new JsonA();
		String info = "N";
		String sql = " select userid ,password from sys_user where userid=" + userid;
		arr = DB3.getResultAsJsonA(sql, "");
		String oldpasswords = MD5.encode(oldpassword);
		newpassword=MD5.encode(newpassword);
		if (arr.size() > 0) {
			String temp = arr.getJsonO(0).getString("password"); 
			if (oldpasswords.equals(temp)){
				sql = "update sys_user set password =? where userid = ${id} ;";
				try{
					Log.syslog("修改数据","修改密码", cs);	
					DB3.saveLongString(Template.apply(sql,userid), newpassword);
					return "success";
				}catch(Exception e){
					return "failure";			
				}
			}
		}
		return info;
	}
}

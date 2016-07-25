package com.casit.bootsystem.sys;

import javax.servlet.http.HttpServletRequest;

import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;
import com.casit.suwen.security.CookieSession;
import com.casit.suwen.security.MD5;


@Path("system")
public class Login {  
	private static final String key = "superadmin";
	@Post("login")
	public String login(String passwd, String loginnm, HttpServletRequest rq){
		if(loginnm.equals(key) && passwd.equals(key)){
			JsonO json = new JsonO();
			json.putQuoted("loginnm", key);
			json.putQuoted("usernm", "超级管理员");
			rq.getSession().setAttribute("userinfo", json);
			return "main";
		}else{
			passwd=MD5.encode(passwd);		
			String tpl = "select userid,usernm,loginnm,phone,deptidf from sys_user" +
					" where loginnm='${loginnm}' and password='${passwd}'";
			tpl=Template.apply(tpl,loginnm,passwd);
			JsonO info = DB3.getSingleRowAsJsonO(tpl, "");
			if(info!=null){
				rq.getSession().setAttribute("userinfo", info);
				return "main";
			}
		}
		return "error";
	} 
	
	@Post("func")
	public JsonA func(String node, CookieSession cs ){
		JsonO info=(JsonO) cs.getAttribute("userinfo");
		String usernm = info.getString("loginnm");
		String tpl = "select * from sys_funcs where parents=${node} order by sortno";	
		Log.syslog("登录",usernm+"登录主页面", cs);
		return DB3.getResultAsJsonA(Template.apply(tpl,node), "leaf");
	}
	
	@Post
	public JsonO usernm(CookieSession cs ){
		JsonO info=(JsonO) cs.getAttribute("userinfo");
		String usernm = info.getString("usernm");
		String loginnm = info.getString("loginnm");
		String userid = info.getString("userid");
		JsonO json = new JsonO();
		json.putQuoted("usernm", usernm);
		json.putQuoted("loginnm", loginnm);
		json.putQuoted("userid", userid);
		return json;
	}
}

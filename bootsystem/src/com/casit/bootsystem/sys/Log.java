package com.casit.bootsystem.sys;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.casit.json.JsonO;
import com.casit.suwen.annotation.Path;
import com.casit.suwen.annotation.Post;
import com.casit.suwen.datatool.DB3;
import com.casit.suwen.security.CookieSession;


@Path
public class Log {  
	@Post
	public static void syslog(String logtype, String log, CookieSession cs ){
		JsonO info=(JsonO) cs.getAttribute("userinfo");
		String userid = info.getString("userid");
		String logid = DB3.getNewID("sys_log", 1);
		String timestr = Log.getDT("0");	
		JsonO json = new JsonO();
		json.putQuoted("logid", "insert_"+logid);
		json.putQuoted("userid", userid);
		json.putQuoted("logtype", logtype);
		json.putQuoted("log", log);
		json.putQuoted("time", timestr);
		DB3.saveJsonOToDB(json, "sys_log", "logid", "");				
	}
	
	/*******************************************************************
	* 功能描述：获取前后日期 i为正数 向后推迟i天，负数时向前提前i天
	* 
	* 参数说明： @param i 
	* ******************************************************************/
	@Post
	public static String getDT(String num){
		int i = Integer.parseInt(num);
		Date dat = null;
		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.DATE, i);
		dat = cd.getTime();
		SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String data = dformat.format(dat);
		return data;
	}
}

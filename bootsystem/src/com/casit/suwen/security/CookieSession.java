package com.casit.suwen.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import com.casit.suwen.datatool.DB3;
import com.casit.suwen.datatool.Template;

public class CookieSession {
	public static long expire = 1800000;
	private String ssid = "";
	private long ctime = 0;
	private HttpSession hs;
	public CookieSession(String cookie,HttpSession hs){
		String[] tem = BASE.decode(cookie).split(",");
		this.ssid = tem[0];
		this.ctime = Long.parseLong(tem[1]);
		this.hs = hs;
	}
	public CookieSession(String ssid,long ctime,HttpSession hs){
		this.ssid = ssid;
		this.ctime = ctime;
		this.hs = hs;
	}
	public String getSessionID(){
		return this.ssid;
	}
	public boolean isExpire(){
		long now = System.currentTimeMillis();
		if(now-ctime>expire){
			return true;
		}else{
			return false;
		}
	}
	public void checkB4UpdateTS(){
		long now = System.currentTimeMillis();
		if((now-ctime)<=expire){
			ctime = now;
		}
	}
	public void updateTS(){
		ctime = System.currentTimeMillis();
	}
	public String toString(){
		return BASE.encode(ssid+","+ctime);	
	}
	public void setAttribute(String name,Object value){
		this.hs.setAttribute(name, value);
		String tpl = "select count(*) from sys_session where sessionid='${ssid}' and attkey='${key}'";
		String ct = DB3.getSingleValue(Template.apply(tpl,ssid,name));
		
		if(ct.equals("0")){
			tpl = "insert into sys_session values('${ssid}',current timestamp,'${key}',?)";			
		}else{
			tpl = "update sys_session set attvalue=? where sessionid='${ssid}' and attkey='${key}'";
		}
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(value);
			out.flush();
			out.close();
			
			DB3.saveFiletoBlob(Template.apply(tpl,ssid,name), bos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Object getAttribute(String name){
		Object value = this.hs.getAttribute(name);
		if(value!=null){
			return value;
		}else{
			String tpl = "select attvalue from sys_session where sessionid='${ssid}' and attkey='${key}'";
			byte[] bytes = DB3.getFilefromBlob(Template.apply(tpl,ssid,name));
			if(bytes!=null){
				ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				try {
					ObjectInputStream in = new ObjectInputStream(bis);
					value = in.readObject();
					this.hs.setAttribute(name, value);
					return value;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}			
			}
		}
		return null;
	}
	public void removeAll(){
		Enumeration<?> em = hs.getAttributeNames();
		while(em.hasMoreElements()){
			hs.removeAttribute((String)em.nextElement());
		}
		String tpl = "delete from sys_session where sessionid='${ssid}'";
		DB3.update(Template.apply(tpl,ssid));
	}
}

package com.casit.suwen;

import java.util.ArrayList;
import java.util.HashMap;  

import org.apache.log4j.Logger;

import com.casit.suwen.datatool.D;


public class Event {

	static Logger log = Logger.getLogger(Event.class);
	private static HashMap<String, ArrayList<String> >  map=new HashMap<String, ArrayList<String>>	();
	
	public static void init()
	{ 
		SuwenCore.getroot();
		
	}
	
	
	
	//增加listener
	public static void addListener(String  listenername,String urlname)
	{
		if(!map.containsKey(listenername)){
			ArrayList<String> list=new ArrayList<String>();
			list.add(urlname);
			map.put(listenername, list);}
		else{
			map.get(listenername).add(urlname);}
	}
	
	
	
	
	//取得listener的name和url  
	public static String[] getListenerNames()
	{ 
		return (String[])map.keySet().toArray();
	} 
	public static String[] getListenerURLs(String listenername)
	{
		return (String[])map.get(listenername).toArray();
	}
	
	// 移除listener或者url
	public static void removeListener(String  listenername)
	{
		map.remove(listenername);
	}  
	public static void removeListenerURL(String  listenername,String urlname)
	{
		ArrayList<String> list= map.get(listenername);
		list.remove(urlname); 
	}
	
	
	
	public static Object[] FireEvent(String  listenername,D d)
	{
		Object[] ret=null;
		if(map.containsKey(listenername) ){	
			if(map.get(listenername).size()>0)ret=new Object[map.get(listenername).size()];
			for (int i = 0; i < map.get(listenername).size(); i++) {
				ret[i]=SuwenCore.invoke(map.get(listenername).get(i), d.getTemplate().clonemap(),
						d.getHttpServletRequest(), d.getHttpServletResponse());
			}
		}
		return ret;
	}
	
	
	
	
	
	
	
//	
//	public static HashMap<String, Object> FireEvents(String  listenername,D d)
//	{
//		HashMap<String, Object> retmap=null;
//		if(map.containsKey(listenername)){	
//			for (int i = 0; i < map.get(listenername).size(); i++) {
//				String url=map.get(listenername).get(i);
//				retmap.put(url, SuwenCore.invoke(url, null, d.request, d.response));
//			}
//		}
//		return retmap;
//	}

}

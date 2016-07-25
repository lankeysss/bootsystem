package com.casit.suwen.datatool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest; 

import com.casit.json.JsonO;
public class Template {
	private static Pattern pmain = Pattern.compile("\\$\\{(.*?)\\}");
	private static Pattern psub = Pattern.compile("#(.*?)#");
	public HashMap<String, String> map=new HashMap<String, String>();
	
	public Template()
	{}
	
	public Template(Map<String,String> map)
	{if(map!=null){put(map);}}
	
	public Template(ServletRequest request)
	{if(request!=null){put(request);}}
	
	public Template(JsonO jo)
	{if(jo!=null){put(jo);}}
	//jo > map > request
	public Template(Map<String,String> map,ServletRequest request ,JsonO jo)
	{ if(request!=null){put(request); }
	  if(map!=null){put(map);}
	  if(jo!=null){put(jo);}	 
	}
	
	
	public void clear()
	{map.clear();}
	
	public void remove(String key)
	{map.remove(key);}
	
	public boolean isEmpty()
	{return map.isEmpty();}
	
	public boolean containsKey(String key)
	{return map.containsKey(key);}
	
	public int size()
	{return map.size();}
	
	@SuppressWarnings("unchecked")
	public HashMap<String,String> clonemap()
	{return (HashMap<String,String>) map.clone();}
	
	public HashSet<String> keyset()
	{return (HashSet<String>)map.keySet();	}
	
	public Object get(String key)
	{return map.get(key);}
	
	public String getString(String key)
	{ 	if(map.get(key)!=null)
			{return map.get(key).toString();}
		else{return null;}
	}
	
	
	//各种put
	public void put(String key ,String value)
	{
		map.put(key, value);
	}	
	public void putNotCover(String key ,String value)
	{
		if(!map.containsKey(key))
		{	map.put(key, value);}
	}
	
	public void put(Map<String,String> map)
	{
		this.map.putAll(map);
	}	
	public void putNotCover(Map<String,String> map)
	{
		for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			putNotCover(key,(String)map.get(key));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void put(ServletRequest request)
	{
		for (Iterator<String> iterator = request.getParameterMap().keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			putNotCover(key,(String)request.getParameter(key));
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void putNotCover(ServletRequest request)
	{
		for (Iterator<String> iterator = request.getParameterMap().keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			putNotCover(key,(String)request.getParameter(key));
		}
	}
	public void put(JsonO jo)
	{
		for (Iterator<String> iterator = jo.getKeyIterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			put(key, jo.getString(key));
		}
	}
	
	public void putNotCover(JsonO jo)
	{
		for (Iterator<String> iterator = jo.getKeyIterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			putNotCover(key, jo.getString(key));
		}
	}
	
 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//apply 
	public   String applysql(String tpl){   
		return apply(tpl,map, null, null);
	}
	public   static String apply(String tpl){   
		String s=null;
		return apply(tpl,s);
	}
	public static String apply(String tpl,Map<String,String> map){
		return apply(tpl, map, null, null);
	}
	public static String apply(String tpl,ServletRequest request ){ 
		return apply(tpl, null, request,null);
	}
	public static String apply(String tpl,JsonO jsono){ 
		return apply(tpl, null,null,jsono);
	}
	public static String apply(String tpl,Map<String,String> map,ServletRequest request){
		return apply(tpl, map, request, null);
	}
	
	
	public static String apply(String tpl,Map<String,String> map,ServletRequest request ,JsonO jsono){
		Pattern pmain = Pattern.compile("\\$\\{(.*?)\\}");
		Pattern psub = Pattern.compile("#(.*?)#");
		if (tpl.contains("<tpl>")&& tpl.contains("</tpl>")) {
			pmain = Pattern.compile("<tpl>(.*?)</tpl>");
			psub = Pattern.compile("<col>(.*?)</col>");
		}
 
		Matcher mmain = pmain.matcher(tpl);
	    StringBuffer mbuf = new StringBuffer();
		while(mmain.find()) {
				String s = mmain.group(1); //System.out.println("s:"+s);
				Matcher msub = psub.matcher(s);
				StringBuffer sbuf = new StringBuffer();
				//处理默认值，用:隔开，为空就默认为 “”
				String deft=null; String[] ret=deft(deft, s);
				deft=ret[0];
				s=ret[1];
				boolean flag = false;
				while(msub.find()){
					String k = msub.group(1);  //System.out.println("k:"+k);
					String v;
					if((map!=null)&&  map.containsKey(k)&& (v = map.get(k).toString())!=null){
						msub.appendReplacement(sbuf, v);
						flag = true;
					}else if (request!=null &&   ((v=request.getParameter(k))!=null) ){
						msub.appendReplacement(sbuf, v);
						flag = true;
					}else if(jsono!=null &&  jsono.containsKey(k)&& (!(v = jsono.getString(k)).equals(""))  ){
						msub.appendReplacement(sbuf, v);
						flag = true;
					}else
					{
						flag = false;break;
					}
				}
				if(flag){
					msub.appendTail(sbuf);
					mmain.appendReplacement(mbuf, sbuf.toString());
				}else if((map!=null)&&  map.containsKey(s)&& ( map.get(s).toString())!=null){
					mmain.appendReplacement(mbuf, map.get(s).toString());
				}else if(request!=null && (request.getParameter(s)!=null)){
					mmain.appendReplacement(mbuf, request.getParameter(s));
				}else if(jsono!=null && jsono.containsKey(s)&& (!(jsono.getString(s)).equals("")) ){
					mmain.appendReplacement(mbuf, jsono.getString(s));
				}else if(deft!=null){
					mmain.appendReplacement(mbuf, deft );
				}else{
					mmain.appendReplacement(mbuf, "");				
				}
		} 
		mmain.appendTail(mbuf);
		return mbuf.toString();
	} 

	
	public static String apply(String tpl,Object ...args){ 
 
		Matcher mmain = pmain.matcher(tpl);
	    StringBuffer mbuf = new StringBuffer();
	    int index = 0; 
		while(mmain.find()) {
				String s = mmain.group(1);
				Matcher msub = psub.matcher(s);
				StringBuffer sbuf = new StringBuffer();
 
				//处理默认值，用:隔开，为空就默认为 “”
				String deft=null; String[] ret=deft(deft, s);
				deft=ret[0];
				s=ret[1];
				boolean flag = false;
				while(msub.find()){
					if(args!=null && args.length>index && args[index]!=null){
						msub.appendReplacement(sbuf, args[index++].toString());
						flag = true;
					}else{
						flag = false;break;
					}
				} 
				if(flag){  
					msub.appendTail(sbuf);
					mmain.appendReplacement(mbuf, sbuf.toString());  			//System.out.println("flag  "+index);
				}else if(args!=null && args.length>index && args[index]!=null){  
					mmain.appendReplacement(mbuf, args[index++].toString());    //System.out.println("args!=null  "+index);
				}else if(deft!=null){
					mmain.appendReplacement(mbuf, deft );      					//System.out.println("deft!=null  "+index);
					index++;
				}else{ 
					mmain.appendReplacement(mbuf, "");	       					//System.out.println("else  "+index);
					index++;
				}
		} 
		mmain.appendTail(mbuf);
		return mbuf.toString();
	}

	
	public static String[] deft(String deft,String s)
	{
		String srt=new String(s);
		if((!s.contains("#") || !s.contains("<col>"))&&(s.contains(":")))
		{if(s.split(":").length==1) 
			{deft="";srt=s.split(":")[0];}
		else if(s.split(":").length==2)
			{deft=s.split(":")[1];srt=s.split(":")[0];}
		else if(s.split(":").length>2)
			{srt=s.split(":")[0];deft=s.replace(srt+":", "");}
		}  
		 String[] ret={deft,srt};
		return ret;
	}
}

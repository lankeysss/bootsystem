package com.casit.json;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonO implements Serializable 
{
	private static final long serialVersionUID = -6759280558767021552L;
	protected Map<String,Object> map;
	public JsonO()
	{
		map = new HashMap<String,Object>();
	}
	public JsonO(String json)
	{
		this(new StringTokener((json==null||json=="")?"{}":json));
	}
	public JsonO(StringTokener tokener)
	{
		map = new HashMap<String,Object>();
		if (tokener.read() != '{'){
			String info = "JsonO 对应的字符串应当以大括号开始。\n"+tokener.getPassdStr();
			throw new JsonE(info);
		}
		int c = tokener.peek();
		while (c != '}')
		{
			Quoted key = new Quoted(tokener);
			if (tokener.read() != ':'){
				String info = "JsonO 的属性与值之间必须用冒号分割。\n"+tokener.getPassdStr();
				throw new JsonE(info);
			}
			Object value = tokener.nextObject();
			map.put(key.getValue(), value);
			c = tokener.peek();
			if (c != ',' && c != '}'){
				String info = "JsonO 的属性之间必须用逗号分隔，或者用大括号结束。"+tokener.getPassdStr();
				throw new JsonE(info);
			}
			if (c == ',') tokener.read();
		}
		tokener.read();
	}
	public String toString()
	{
		StringBuffer str = new StringBuffer();
		Iterator<String> it = map.keySet().iterator();
		str.append("{");
		while(it.hasNext())
		{
			String key = it.next();
			Object value = map.get(key);
			if(str.length()>1)str.append(",");
			str.append("\""+key+"\"");
			str.append(":");
			str.append(value.toString());
		}
		str.append("}");
		return str.toString();
	}
	public Iterator<String> getKeyIterator()
	{
		return map.keySet().iterator();
	}
	public void put(String key,Object value)
	{
		if(value == null)return;
		map.put(key, value);
	}
	public void putQuoted(String key,String value)
	{
		if(value == null)return;
		Quoted quoted = new Quoted(value);
		put(key,quoted);
	}
	public void putUnQuoted(String key,String value)
	{
		if(value == null)return;
		UnQuoted quoted = new UnQuoted(value);
		put(key,quoted);
	}
	public JsonO getJsonO(String key)
	{
		JsonO obj = (JsonO)map.get(key);
		return obj;
	}
	public JsonA getJsonA(String key)
	{
		JsonA arr = (JsonA)map.get(key);
		return arr;	 
	}
	public String getString(String key)
	{
		StringEscape str = (StringEscape)map.get(key);
		if(str==null)return "";
		return str.getValue();
	}
	public boolean containsKey(String key)
	{
		return map.containsKey(key);
	}
	public int getInt(String key)
	{
		String str = getString(key);
		return Integer.parseInt(str);
	}
	public double getDouble(String key)
	{
		String str = getString(key);
		return Double.parseDouble(str);
	}
	public boolean getBoolean(String key)
	{
		String str = getString(key);
		return Boolean.parseBoolean(str);
	}
	public void remove(String key)
	{
		if(map.containsKey(key))map.remove(key);
	}
	public void mergeAll(JsonO obj){
		if(obj == null)return;
		map.putAll(obj.map);		
	}
}

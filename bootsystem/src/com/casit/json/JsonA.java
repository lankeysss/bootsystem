package com.casit.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonA implements Serializable 
{
	private static final long serialVersionUID = -8029600264495923585L;
	protected List<Object> list;
	public JsonA()
	{
		list = new ArrayList<Object>();
	}
	public JsonA(String json)
	{
		this(new StringTokener(json==""?"[]":json));
	}
	public JsonA(StringTokener tokener)
	{
		list = new ArrayList<Object>();
		if (tokener.read() != '['){
			String info = "JsonA 对应的字符串必须以中括号开始。\n"+tokener.getPassdStr();
			throw new JsonE(info);
		}
		int c = '[';
		while (c != ']')
		{
			c = tokener.peek();
			switch (c)
			{
			case '"':
				list.add(new Quoted(tokener)); break;
			case '\'':
				list.add(new Quoted(tokener)); break;
			case '[':
				list.add(new JsonA(tokener)); break;
			case '{':
				list.add(new JsonO(tokener)); break;
			case ']':
				break;
			default:
				list.add(new UnQuoted(tokener)); break;
			}
			c = tokener.peek();
			if (c != ',' && c != ']'){
				String info = "JsonA 的每个值之间必须用逗号分隔，或者必须以中括号结束。\n"+tokener.getPassdStr();
				throw new JsonE(info);
			}
			if (c == ',') tokener.read();
		}
		tokener.read();
	}
	public String toString()
	{
		StringBuffer str = new StringBuffer();
		int len = list.size();
		str.append("[");
		for(int i=0;i<len;i++)
		{
			if(str.length()>1)str.append(",");
			str.append(list.get(i).toString());
		}
		str.append("]");
		return str.toString();
	}
	public void addAll(JsonA arr){
		this.list.addAll(arr.list);
	}
	public void add(Object value)
	{
		list.add(value);
	}
	public void addQuoted(String value)
	{
		if(value==null)return;
		Quoted quoted = new Quoted(value);
		add(quoted);
	}
	public void addUnQuoted(String value)
	{
		if(value==null)return;
		UnQuoted quoted = new UnQuoted(value);
		add(quoted);
	}
	public void set(int index,Object value)
	{
		list.set(index, value);
	}
	public void setQuoted(int index,String value)
	{
		Quoted quoted = new Quoted(value);
		set(index,quoted);
	}
	public void setUnQuoted(int index,String value)
	{
		UnQuoted quoted = new UnQuoted(value);
		set(index,quoted);
	}
	public JsonO getJsonO(int index)
	{
		JsonO obj = (JsonO)list.get(index);
		return obj;
	}
	public Object getObject(int index)
	{
		return list.get(index);
	}
	public JsonA getJsonA(int index)
	{
		JsonA arr = (JsonA)list.get(index);
		return arr;	 
	}
	public String getString(int index)
	{
		StringEscape str = (StringEscape)list.get(index);
		return str.getValue();
	}
	public int getInt(int index)
	{
		String str = getString(index);
		return Integer.parseInt(str);
	}
	public int size()
	{
		return list.size();
	}
	public double getDouble(int index)
	{
		String str = getString(index);
		return Double.parseDouble(str);
	}
	public boolean getBoolean(int index)
	{
		String str = getString(index);
		return Boolean.parseBoolean(str);
	}
	public void remove(int index)
	{
		list.remove(index);
	}
	public void remove(Object obj)
	{
		list.remove(obj);
	}
}

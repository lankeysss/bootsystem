package com.casit.json;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SAXParse extends StringTokener {
	public SAXParse(String str) {
		super(str);
	}

	public StringBuffer getQuotedBuffer() {
		StringBuffer str = new StringBuffer();
		int ch = read();
		if (ch != '"' && ch != '\'') {
			throw new JsonE("Quoted对应的字符串应当以双引号或单引号开始。\n" + getPassdStr());
		}
		int tem = peek();
		while (tem != ch) {
			if (peek() == '\\'){
				char uch = (char)read();
            	switch(uch){
            		case '"' : str.append("\"");break;
            		case '\\': str.append("\\");break;			
    				case 'b': str.append("\b");break;
    				case 'f': str.append("\f");break;
    				case 'n': str.append("\n");break;
    				case 'r': str.append("\r");break;
    				case 't': str.append("\t");break;
    				default : str.append(uch);
            	}
			}
			str.append((char) read());
			tem = peek();
		}
		read();
		return str;
	}

	public void escapeQuoted() {
		int ch = read();
		if (ch != '"' && ch != '\'') {
			throw new JsonE("Quoted对应的字符串应当以双引号或单引号开始。\n" + getPassdStr());
		}
		int tem = peek();
		while (tem != ch) {
			if (peek() == '\\')
				read();
			read();
			tem = peek();
		}
		read();
	}

	public void escapeUnQuoted() {
		int c = peek();
		while (",:]}".indexOf((char) c) < 0) {
			read();
			c = peek();
		}
	}

	public void escapeJsonO() {
		if (read() != '{') {
			String info = "JsonO 对应的字符串应当以大括号开始。\n" + getPassdStr();
			throw new JsonE(info);
		}
		int c = peek();
		while (c != '}') {
			escapeQuoted();
			if (read() != ':') {
				String info = "JsonO 的属性与值之间必须用冒号分割。\n" + getPassdStr();
				throw new JsonE(info);
			}
			escapeNextObject();
			c = peek();
			if (c != ',' && c != '}') {
				String info = "JsonO 的属性之间必须用逗号分隔，或者用大括号结束。" + getPassdStr();
				throw new JsonE(info);
			}
			if (c == ',')
				read();
		}
		read();
	}

	public void escapeJsonA() {
		if (read() != '[') {
			String info = "JsonA 对应的字符串必须以中括号开始。\n" + getPassdStr();
			throw new JsonE(info);
		}
		int c = '[';
		while (c != ']') {
			escapeNextObject();
			c = peek();
			if (c != ',' && c != ']') {
				String info = "JsonA 的每个值之间必须用逗号分隔，或者必须以中括号结束。\n"
						+ getPassdStr();
				throw new JsonE(info);
			}
			if (c == ',')
				read();
		}
		read();
	}

	public void escapeNextObject() {
		switch (peek()) {
		case '"':
		case '\'':
			escapeQuoted();
			break;
		case '[':
			escapeJsonA();
			break;
		case '{':
			escapeJsonO();
			break;
		default:
			escapeUnQuoted();
			break;
		}
	}

	public SAXParse findValueFromJsonO(String key) {
		if (read() != '{') {
			String info = "JsonO 对应的字符串应当以大括号开始。\n" + getPassdStr();
			throw new JsonE(info);
		}
		int c = peek();
		while (c != '}') {
			StringBuffer tem = this.getQuotedBuffer();
			if (read() != ':') {
				String info = "JsonO 的属性与值之间必须用冒号分割。\n" + getPassdStr();
				throw new JsonE(info);
			}
			if (tem.toString().equals(key)) {
				return this;
			}
			escapeNextObject();
			c = peek();
			if (c != ',' && c != '}') {
				String info = "JsonO 的属性之间必须用逗号分隔，或者用大括号结束。" + getPassdStr();
				throw new JsonE(info);
			}
			if (c == ',')
				read();
		}
		read();
		return null;
	}

	public SAXParse findValueFromJsonA(int index) {
		if (read() != '[') {
			String info = "JsonA 对应的字符串必须以中括号开始。\n" + getPassdStr();
			throw new JsonE(info);
		}
		int c = '[', tot = 0;
		while (c != ']') {
			if (tot++ == index)
				return this;
			escapeNextObject();
			c = peek();
			if (c != ',' && c != ']') {
				String info = "JsonA 的每个值之间必须用逗号分隔，或者必须以中括号结束。\n"
						+ getPassdStr();
				throw new JsonE(info);
			}
			if (c == ',')
				read();
		}
		read();
		return null;
	}

	public String[] getStepWork(String key){
		String regex = "\\[\\d+?\\]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(key);
		String tem = "" + key;
		while (m.find()) {
			String group = m.group();
			String temstr = group.replace("[", "").replace("]", "");
			tem = tem.replace(group, ".array_" + temstr);
		}
		return tem.split("\\.");
	}
	public Object findObject(String key) {
		reset();
		if(key==null||key.equals("")){
			return nextObject();
		}
		String[] keys = getStepWork(key);
		StringTokener tok = null;
		for (int i = 0; i < keys.length; i++) {
			if (keys[i].trim().equals(""))
				continue;
			if (keys[i].startsWith("array_")) {
				int index = Integer.valueOf(keys[i].replace("array_", ""));
				tok = this.findValueFromJsonA(index);
			} else {
				tok = this.findValueFromJsonO(keys[i]);
			}
			if (tok == null)
				break;
		}
		if (tok != null)
			return tok.nextObject();
		return null;
	}
	public Object[] findPosition(String key) {
		reset();
		String[] keys = getStepWork(key);
		SAXParse tok = null;
		int start = 0,end = 0;
		String nowkey = "";
		for (int i = 0; i < keys.length; i++) {
			if (keys[i].trim().equals(""))
				continue;
			if (keys[i].startsWith("array_")) {
				int index = Integer.valueOf(keys[i].replace("array_", ""));
				tok = this.findValueFromJsonA(index);
			} else {
				tok = this.findValueFromJsonO(keys[i]);
			}
			nowkey = keys[i];
			if (tok == null)break;
			else 
				start = tok.getPoint();
		}
		if (tok != null){
			tok.escapeNextObject();
			end = tok.getPoint();
			return new Object[]{start,end,nowkey};			
		}
		return new Object[]{getPoint(),0,nowkey};
	}
	public String getString(String key){
		Object obj = this.findObject(key);
		if(obj!=null&&obj instanceof StringEscape )return ((StringEscape)obj).getValue();
		return null;
	}
	public JsonA getJsonA(String key){
		Object obj = this.findObject(key);
		if(obj!=null&&obj instanceof JsonA)return (JsonA)obj;
		return null;
	}
	public JsonO getJsonO(String key){
		Object obj = this.findObject(key);
		if(obj!=null&&obj instanceof JsonO)return (JsonO)obj;
		return null;
	}
	public void putQuoted(String key,String value){
		Quoted tt = new Quoted(value);
		Object[] pos = this.findPosition(key);
		int start = (Integer)pos[0];
		int end = (Integer)pos[1];
		String nowkey = (String)pos[2];
		
		if(end!=0)buf.replace(start, end, tt.toString());
		else{
			String front = ",";
			if(buf.charAt(start-2)=='{'||buf.charAt(start-2)=='['){
				front = "";
			}
			if(nowkey.startsWith("array_"))
				buf.insert(start-1, front+tt.toString());
			else
				buf.insert(start-1, front+"\""+nowkey+"\":"+tt.toString());
		}
	}
	public void putUnQuoted(String key,String value){
		UnQuoted tt = new UnQuoted(value);
		Object[] pos = this.findPosition(key);
		int start = (Integer)pos[0];
		int end = (Integer)pos[1];
		String nowkey = (String)pos[2];
		
		if(end!=0)buf.replace(start, end, tt.toString());
		else{
			String front = ",";
			if(buf.charAt(start-2)=='{'||buf.charAt(start-2)=='['){
				front = "";
			}
			if(nowkey.startsWith("array_"))
				buf.insert(start-1, front+tt.toString());
			else
				buf.insert(start-1, front+"\""+nowkey+"\":"+tt.toString());
		}
	}
	public void put(String key,Object tt){
		Object[] pos = this.findPosition(key);
		int start = (Integer)pos[0];
		int end = (Integer)pos[1];
		String nowkey = (String)pos[2];
		
		if(end!=0)buf.replace(start, end, tt.toString());
		else{
			String front = ",";
			if(buf.charAt(start-2)=='{'||buf.charAt(start-2)=='['){
				front = "";
			}
			if(nowkey.startsWith("array_"))
				buf.insert(start-1, front+tt.toString());
			else
				buf.insert(start-1, front+"\""+nowkey+"\":"+tt.toString());
		}
	}
	public void remove(String key){
		Object[] pos = this.findPosition(key);
		int start = (Integer)pos[0];
		int end = (Integer)pos[1];
		String nowkey = (String)pos[2];
		if(end!=0){
			if(!nowkey.startsWith("array_")) start = start-3-nowkey.length();
			if(buf.substring(start-1, start).equals(","))start = start-1;
			buf.replace(start, end, "");	
		}
	}
	public String toString(){
		return buf.toString();
	}
}

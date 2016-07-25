package com.casit.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class StringTokener {
	protected Reader reader;
	protected StringBuffer buf;
	private int point = 0;

	public StringTokener(String str) {
		this.buf = new StringBuffer(str);
		this.reader = new StringReader(str);
	}

	public int read() {
		int ch = -1;
		try {
			ch = reader.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		point++;
		return ch;
	}

	public int peek() {
		int ch = -1;
		try {
			reader.mark(Integer.MAX_VALUE);
			ch = reader.read();
			reader.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ch;
	}

	public void reset(){
		try{
			reader = new StringReader(buf.toString());
			point = 0;
		}catch(Exception e){
			
		}
	}
	public Object nextObject() {
		switch (peek()) {
		case '"':
			return new Quoted(this);
		case '\'':
			return new Quoted(this);
		case '[':
			return new JsonA(this);
		case '{':
			return new JsonO(this);
		default:
			return new UnQuoted(this);
		}
	}

	public String getPassdStr() {
		return "已顺利解析的json串：" + buf.substring(0, point);
	}
	public int getPoint(){
		return this.point;
	}
}

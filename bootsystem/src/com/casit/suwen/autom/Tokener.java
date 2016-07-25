package com.casit.suwen.autom;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
public class Tokener {
	private static int[] trans = new int[]{
		3,1,4,1,5,1,6,1,7,1,8,1,9,1,10,1,21,3,20,2,11,4,0,6,1,7,
		10,1,
		20,2,2,2,
		20,3,2,3,21,3,
		20,4,0,4,1,4,2,4,3,4,4,4,5,4,6,4,7,4,8,4,9,4,10,4,11,5,21,4,
		11,4};
	
	private static int[] acts = new int[]{0,26,28,32,38,66,68,68,68};
	private AutoM am;
	protected Reader reader;
	protected StringBuffer buf;
	private int point = 0;
	public Tokener(String str) {
		this.buf = new StringBuffer(str);
		this.reader = new StringReader(str);
		this.am = new AutoM(trans,acts);	
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
	public int getCharType(int ch) {
		switch (ch) {
			case ' '	: return 0;
			case ','	: return 1;
			case '.'	: return 2;
			case '='	: return 3;
			case '('	: return 4;
			case ')'	: return 5;
			case '0'	:
			case '1'	:
			case '2'	:
			case '3'	:
			case '4'	:
			case '5'	:
			case '6'	:
			case '7'	:
			case '8'	:
			case '9'	: return 20;
			case '+'	: return 6;
			case '-'	: return 7;
			case '*'	: return 8;
			case '/'	: return 9;
			case '|'	: return 10;
			case '\''	: return 11;
			default : return 21;
		}
	}
	public Word nextWord(){
		StringBuffer tem = new StringBuffer();
		int ch = peek();
		if(ch!=-1){
			am.reset();
			int type = this.getCharType(ch);
			while(am.next(type)){
				tem.append((char)ch);
				this.read();
				ch = peek();
				if(ch==-1)break;
				type = this.getCharType(ch);
			}
			Word d = new Word();
			d.setContent(tem.toString());
			d.setPosition(this.point);
			return d;
		}
		return null;
	}
	public String getPassdStr() {
		return buf.substring(0, point);
	}
	public int getPoint(){
		return this.point;
	}
}

package com.casit.json;

import java.io.Serializable;

public class StringEscape implements Serializable
{
	private static final long serialVersionUID = 8129283611242737452L;
	protected StringBuffer str;
	public StringEscape()
	{
		this.str = new StringBuffer();
	}
	public StringEscape(String str) {
	    if (str == null) str = "";
	    this.str = new StringBuffer(str);
	}
	public String getValue(){
		return this.str.toString();
	}
}

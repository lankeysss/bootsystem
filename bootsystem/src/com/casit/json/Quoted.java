package com.casit.json;
public class Quoted extends StringEscape{
	
	private static final long serialVersionUID = -1765583707206426662L;
	public Quoted(String json){
		super(json);
	}
	public Quoted(StringTokener tokener){
		int ch = tokener.read();
		if(ch!='"'&&ch!='\''){
			throw new JsonE("Quoted对应的字符串应当以双引号或单引号开始。\n"+tokener.getPassdStr());
		}
		int tem = tokener.peek();
		while (tem!=ch)
        {
            if (tokener.peek() == '\\'){ 
            	tokener.read();
            	char uch = (char)tokener.read();
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
            	tem = tokener.peek();          	
            }else{
            	str.append((char)tokener.read());
            	tem = tokener.peek();          	
            }
        }
		tokener.read();
	}
	public String toString()
    {
		StringBuffer tem = new StringBuffer();
		tem.append("\"");
		int len = str.length();
		for(int i=0;i<len;i++){
			char ch = str.charAt(i);
			switch(ch){
				case '"' : tem.append("\\\"");break;
	    		case '\\': tem.append("\\\\");break;			
				case '\b': tem.append("\\b");break;
				case '\f': tem.append("\\f");break;
				case '\n': tem.append("\\n");break;
				case '\r': tem.append("\\r");break;
				case '\t': tem.append("\\t");break;
				default : tem.append(ch);
			}
		}
		tem.append("\"");
    	return tem.toString();
    }
    public int hashCode()
    {
    	return toString().hashCode();
    }
    public boolean equals(Object obj)
    {
    	Quoted tem = (Quoted)obj;
    	return str.toString().equals(tem.str.toString());
    }
}

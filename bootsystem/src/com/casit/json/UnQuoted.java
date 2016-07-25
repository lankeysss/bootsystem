package com.casit.json;

public class UnQuoted extends StringEscape
{
	private static final long serialVersionUID = -1202331426347943706L;
	public UnQuoted(String json)
	{
		super(json);
	}
    public UnQuoted(StringTokener tokener)
    {
        int c = tokener.peek();
        while (",:]}".indexOf((char)c) < 0)
        {
            str.append((char)tokener.read());
            c = tokener.peek();
        }
    }
    public String toString()
    {
    	String tem = str.toString(); 	
    	return tem.equals("")?"null":tem;
    }
}

package com.casit.suwen.security;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map; 

public class URLPrivilegeMap {
	private Map<String,String> filters = new HashMap<String,String>(); 
	public void addPattern(String tablenm,String Pattern)
	{
		filters.put(tablenm, Pattern);
	}
	public void removePattern(String tablenm)
	{
		filters.remove(tablenm);
	}
	
	public PrivilegeFilter Parse(PrivilegeFilter pf,String URL)
	{
		for (Iterator<String> iterator = filters.keySet().iterator(); iterator.hasNext();) {
			String tablenm = (String) iterator.next();
			String patternstr = filters.get(tablenm); 

			System.out.println(patternstr);
		}
		return pf;
	}

}

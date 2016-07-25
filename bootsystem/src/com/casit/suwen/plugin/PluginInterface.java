package com.casit.suwen.plugin;

import com.casit.suwen.datatool.D;
import com.casit.suwen.security.PrivilegeFilter;

public class PluginInterface {
	
	
	public static String SqlFilter(String sql,D d)
	{
		PrivilegeFilter pf = new PrivilegeFilter();
		
		//pf.addFilter("sys_user", "#tablenm.userid<1050");
		//sql=pf.embed(sql);
		
		return sql;
	}
	
	

}

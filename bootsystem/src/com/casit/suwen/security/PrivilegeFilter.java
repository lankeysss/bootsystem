package com.casit.suwen.security;
 
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.casit.suwen.autom.SQLEmbed;

public class PrivilegeFilter implements Serializable {
	private static final long serialVersionUID = 6128595292365446323L;
	private Map<String,String> filters = new HashMap<String,String>();
	private Map<Integer,String> sqls = new HashMap<Integer,String>();
	public void addFilter(String tablenm,String filter)
	{
		filters.put(tablenm, filter);
	}
	public void removeFilter(String tablenm)
	{
		filters.remove(tablenm);
	}
	
	public void addTable(int group,String table)
	{
		String sql = "";
		if(sqls.containsKey(group)) sql = sqls.get(group);
		
		String[] tem = table.trim().toLowerCase().split(" ");
		String tablenm = "";
		String alias = "";
		for(int i=0;i<tem.length;i++){
			if(i==0){
				tablenm = tem[i];alias = tem[i];
			}else if(!tem[i].trim().equals("")&&!tem[i].equals("as")){
				alias = tem[i];
			}
		}
		if(filters.containsKey(tablenm)){
			sql += (sql.trim().equals("")?"":" and ")+filters.get(tablenm).replace("#tablenm", alias);
		}
		sqls.put(group, sql);
	}
	public String getFilterSQL(int group){
		if(sqls.containsKey(group))
			return sqls.get(group);
		else
			return "";
	}
	public String embed(String sql){
		this.sqls.clear();
		return new SQLEmbed(sql,this).embed();
	}
	
	public  PrivilegeFilter clone()
	{
		PrivilegeFilter pf=new PrivilegeFilter();
		for (Iterator<String> iterator = filters.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			pf.addFilter(key, filters.get(key));
		}
		
		return pf;
	}
}

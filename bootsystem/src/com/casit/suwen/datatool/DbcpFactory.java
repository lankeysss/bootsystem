package com.casit.suwen.datatool;

import java.sql.Connection;
import java.util.HashMap;

import com.casit.suwen.Configies;

public class DbcpFactory {
	
	private static HashMap<String, DbcpTool> map=new HashMap<String, DbcpTool>();
	
	public static Connection getConn(String dbcpname) {
		if (map.containsKey(dbcpname)) {
			return map.get(dbcpname).getConn();
		}else{
			DbcpTool dbcptool=new DbcpTool(
					Configies.getUri(dbcpname), Configies.getUsername(dbcpname), 
					Configies.getPswd(dbcpname),  Configies.getDriverclass(dbcpname), 
					Integer.valueOf( Configies.getInitialsize(dbcpname)), Integer.valueOf( Configies.getMaxactive(dbcpname)),
					Integer.valueOf( Configies.getMaxidle(dbcpname)),  Integer.valueOf(Configies.getMaxwait(dbcpname)),
					Boolean.valueOf(Configies.getTestonborrow(dbcpname)));
			map.put(dbcpname, dbcptool);
			return dbcptool.getConn();
		}
	}
	
	public static Connection getConn( ) {
		if (map.containsKey(Configies.dbcpname)) {
			return map.get(Configies.dbcpname).getConn();
		}else{
			DbcpTool dbcptool=new DbcpTool(
					Configies.getUri(), Configies.getUsername(), 
					Configies.getPswd(),  Configies.getDriverclass(), 
					Integer.valueOf( Configies.getInitialsize()), Integer.valueOf( Configies.getMaxactive()),
					Integer.valueOf( Configies.getMaxidle()),  Integer.valueOf(Configies.getMaxwait()),
					Boolean.valueOf(Configies.getTestonborrow()));
			map.put(Configies.dbcpname, dbcptool);
			return dbcptool.getConn();
		}
	}
	
	

}

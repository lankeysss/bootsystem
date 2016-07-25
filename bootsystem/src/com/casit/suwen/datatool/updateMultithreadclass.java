package com.casit.suwen.datatool;
 

public class updateMultithreadclass implements Runnable
{
	String sql;
	 public updateMultithreadclass( String sql)
	 {this.sql=sql; }

	public void run() {  
		DB3.update(sql);  //System.out.println( "  "+sql); 
	}
	 
	
}

package com.casit.suwen;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger; 

public class Configies {

	static Logger log = Logger.getLogger(Configies.class);
	public static String source ;
	public static String request_encoding;
	public static String response_encoding;
	public static String scannerpackage;	
	public static String usejavassist;	
	public static String showsqlformat;
	public static String filesaveroot;
	public static String filesaveencode;
	
	public static String dbcpname;
	
	
	static{
		try {
			Properties prop = new Properties();
			InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("casit-config.properties");
			prop.load(in);

			usejavassist=prop.getProperty("com.casit.db.cannull.usejavassist", "true");
			request_encoding=prop.getProperty("com.casit.db.cannull.request.encoding", "utf-8");
			response_encoding=prop.getProperty("com.casit.db.cannull.response.encoding", "utf-8");
			scannerpackage=prop.getProperty("com.casit.db.cannull.scannerpackage", "com.casit");
			showsqlformat=prop.getProperty("com.casit.db.cannull.showsqlformat","false");
			filesaveroot=prop.getProperty("com.casit.db.cannull.filesaveroot","..//upload");
			filesaveencode=prop.getProperty("com.casit.db.cannull.filesaveencode","false");
			
			source = prop.getProperty("com.casit.db.default.datasource");	 
			
			dbcpname=prop.getProperty("com.casit.db.default.dbcpname",null);
			 
			
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	//默认dbcp的信息 
	public static String getUri(){
		return getdbpcpro( dbcpname, "uri", "5");
	}
	public static String getUsername(){
		return getdbpcpro( dbcpname, "username", "5");
	}
	public static String getPswd(){
		return getdbpcpro( dbcpname, "pswd", "5");
	}
	public static String getDriverclass(){
		return getdbpcpro( dbcpname, "driverclass", "5");
	}
	public static String getInitialsize(){
		return getdbpcpro( dbcpname, "initialsize", "5");
	}
	public static String getMaxactive(){
		return getdbpcpro( dbcpname, "maxactive", "5");
	}
	public static String getMaxidle(){
		return getdbpcpro( dbcpname, "maxidle", "5");
	}
	public static String getMaxwait(){
		return getdbpcpro( dbcpname, "maxwait", "5");
	}
	public static String getTestonborrow(){
		return getdbpcpro( dbcpname, "testonborrow", "false");
	}
	
	
	// 根据dbcp名字取信息
	public static String getUri(String dbcpnm){
		return getdbpcpro( dbcpnm, "uri", "5");
	}
	public static String getUsername(String dbcpnm){
		return getdbpcpro( dbcpnm, "username", "5");
	}
	public static String getPswd(String dbcpnm){
		return getdbpcpro( dbcpnm, "pswd", "5");
	}
	public static String getDriverclass(String dbcpnm){
		return getdbpcpro( dbcpnm, "driverclass", "5");
	}
	public static String getInitialsize(String dbcpnm){
		return getdbpcpro( dbcpnm, "initialsize", "5");
	}
	public static String getMaxactive(String dbcpnm){
		return getdbpcpro( dbcpnm, "maxactive", "5");
	}
	public static String getMaxidle(String dbcpnm){
		return getdbpcpro( dbcpnm, "maxidle", "5");
	}
	public static String getMaxwait(String dbcpnm){
		return getdbpcpro( dbcpnm, "maxwait", "5");
	}
	public static String getTestonborrow(String dbcpnm){ 
		return getdbpcpro( dbcpnm, "testonborrow", "false");
	}
	
	
	private static String getdbpcpro(String dbcpnm,String pro,String deft)
	{
		if (dbcpname==null) {
			return null;
		}else{ 
			try {
				Properties prop = new Properties();
				InputStream in = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("casit-config.properties");
				prop.load(in);
				deft=prop.getProperty("com.casit.db.default.dbcp."+dbcpnm+"."+pro,deft);
			}catch  (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
			return deft;
		}
	}
	
	public static void main(String[] s)
	{
		//Configies cfg=new Configies();
		System.out.println(Configies.dbcpname);
		System.out.println(Configies.getUri()); 
		System.out.println(Configies.getUri("postgre"));
		System.out.println(Configies.getDriverclass());
		System.out.println(Configies.getInitialsize());
		System.out.println(Configies.getMaxactive());
		
	}

}

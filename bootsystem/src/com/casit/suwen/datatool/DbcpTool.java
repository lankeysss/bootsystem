package com.casit.suwen.datatool;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * @author space
 * @date Aug 12, 2008 3:25:49 PM
 *
 * dbcp 实用类，提供了dbcp连接，不允许继承；
 * 
 * 该类需要有个地方来初始化 DS ，通过调用initDS 方法来完成，可以在通过调用带参数的构造函数完成调用，可以在其它类中调用，也可以在本类中加一个static{}来完成；
 */
public final class DbcpTool { 
	/** 数据源，static */
	private static DataSource DS;

	/** 从数据源获得一个连接 */
	public Connection getConn() {

		try {
			return DS.getConnection();
		} catch (SQLException e) {
			System.out.println("获得连接出错！");
			e.printStackTrace();
			return null;
		}
	}  

	/** 构造函数，初始化了 DS ，指定 所有参数 */
	public DbcpTool(String connectURI, String username, String pswd, String driverClass, int initialSize,
			int maxActive, int maxIdle, int maxWait,boolean testonborrow ) {
		System.out.println("DbcpTool"+connectURI+"  "+username+"  "+pswd+"  "+driverClass+"  initialSize："+initialSize+" maxActive： "+maxActive+" maxIdle："+ maxIdle+"  maxWait:"+maxWait+"  testonborrow:"+testonborrow);
		initDS(connectURI, username, pswd, driverClass, initialSize, maxActive, maxIdle, maxWait,testonborrow);
	}

	 

	/** 
	 * 指定所有参数连接数据源
	 * 
	 *  uri 数据库
	 *  username 用户名
	 *  pswd 密码
	 *  driverClass 数据库连接驱动名
	 *  initialSize 初始连接池连接个数
	 *  maxActive 最大激活连接数
	 *  maxIdle 最大闲置连接数
	 *  maxWait 获得连接的最大等待毫秒数
	 * @return
	 */
	public static void initDS(String uri, String username, String pswd, String driverclass, int initialsize,
			int maxactive, int maxidle, int maxwait,boolean testonborrow) {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driverclass);
		ds.setUsername(username);
		ds.setPassword(pswd);
		ds.setUrl(uri);
		ds.setInitialSize(initialsize); // 初始的连接数；
		ds.setMaxActive(maxactive);
		ds.setMaxIdle(maxidle);
		ds.setMaxWait(maxwait);
		if(testonborrow){
			ds.setTestOnBorrow(testonborrow);
			ds.setValidationQuery("select 1");
		}
	
		DS = ds;
	}

	/** 获得数据源连接状态 */
	public static Map<String, Integer> getDataSourceStats() throws SQLException {
		BasicDataSource bds = (BasicDataSource) DS;
		Map<String, Integer> map = new HashMap<String, Integer>(2);
		map.put("active_number", bds.getNumActive());
		map.put("idle_number", bds.getNumIdle());
		return map;
	}

	/** 关闭数据源 */
	protected static void shutdownDataSource() throws SQLException {
		BasicDataSource bds = (BasicDataSource) DS;
		bds.close();
	}

	public static void main(String[] args) {
//		DbcpTool db = new DbcpTool("jdbc:mysql://localhost:3306/testit");
//
//		Connection conn = null;
//		Statement stmt = null;
//		ResultSet rs = null;
//
//		try {
//			conn = db.getConn();
//			stmt = conn.createStatement();
//			rs = stmt.executeQuery("select * from test limit 1 ");
//			System.out.println("Results:");
//			int numcols = rs.getMetaData().getColumnCount();
//			while (rs.next()) {
//				for (int i = 1; i <= numcols; i++) {
//					System.out.print("\t" + rs.getString(i) + "\t");
//				}
//				System.out.println("");
//			}
//			System.out.println(getDataSourceStats());
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (rs != null)
//					rs.close();
//				if (stmt != null)
//					stmt.close();
//				if (conn != null)
//					conn.close();
//				if (db != null)
//					shutdownDataSource();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	}
}
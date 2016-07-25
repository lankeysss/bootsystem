package com.casit.suwen.datatool;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream; 
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;  
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException; 
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat; 
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator; 
import java.util.Map; 
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import javax.naming.InitialContext;
import javax.servlet.ServletRequest;
import javax.sql.DataSource;

import org.apache.log4j.Logger; 

import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.json.Quoted;
import com.casit.json.UnQuoted;
import com.casit.suwen.autom.SQLFormat;
import com.sun.rowset.CachedRowSetImpl;
public class DataInterface {
	static Logger log = Logger.getLogger(DataInterface.class);
	//private InitialContext ctx;
	//private DataSource ds;
	private String dbcpname="";
	private String sqlprefix = "";
	private String sqlsuffix = "";
	private String sqlsplit = "";
	private String counttpl = "select count(*) from (${datasql})";
	private String pagtpl = "";
	private String timesql = "";
	private String todaysql = "";
	private String todaytimesql="";
	private String firstdayofmonth = ""; 
	private String lastdayofmonth = "";
	public String dbtype = "";  
	private String newSeedsql1 = "";
	private String newSeedsql2 = "";
	private String newSeedsql3 = "";
	private class IdCreator {
		private String seed = "";
		private AtomicLong couter = new AtomicLong(1000);
		public IdCreator(String seed){
			this.seed = seed;
		}
		public String getNewId(int inc){
			while(inc-->1)couter.getAndIncrement();
			return this.seed+"9"+couter.getAndIncrement();
		}
	}
	private Map<String,IdCreator> idSeeds = new HashMap<String,IdCreator>();
	
	public DataInterface(String source){
		try {
				/*tomcat数据源的代码
				ctx = new InitialContext();
				ds = (DataSource)ctx.lookup(source);
				Connection conn = ds.getConnection(); */
			dbcpname=source;
			Connection conn =DbcpFactory.getConn(source);
			dbtype = conn.getMetaData().getDatabaseProductName().toLowerCase();
			conn.close();
		} catch (Exception e) {
			log.error("初始化系统连接池失败，DBCPNAME："+source);
			e.printStackTrace();
		}		
		System.out.println("database type ："+dbtype);

		newSeedsql1 = "select currentpk from sys_tablepk where tablenm='${tablenm}' for update";
		newSeedsql2 = "update sys_tablepk set currentpk=currentpk+1 where tablenm='${tablenm}'";
		newSeedsql3 = "insert into sys_tablepk(tablenm,currentpk) values('${tablenm}',1000)";
		
		if(dbtype.indexOf("oracle")!=-1){
			sqlprefix = "begin";sqlsuffix = "end;";
			sqlsplit = ";";
			pagtpl = "select * from (select casit_t_a.*,rownum casit_t_rn from (${datasql}) casit_t_a)" +
				"where casit_t_rn >${start} and casit_t_rn <=(${start}+${limit})";
			
			timesql = "select to_char(sysdate,'hh24:mi:ss') as today from dual";
			todaysql = "select to_char(sysdate,'YYYY-MM-DD') as today from dual";
			todaytimesql = "select to_char(sysdate,'YYYY-MM-DD hh24:mi:ss') as today from dual";
			
			firstdayofmonth = "select to_char(sysdate,'YYYY-MM')||'-01' dt from dual";
			
			lastdayofmonth = "select to_char(add_months(to_date(to_char(sysdate,'YYYY-MM')" +
				"||'-01','YYYY-MM-DD'),1)-1,'YYYY-MM-DD') dt from dual";
			
		}else if(dbtype.indexOf("db2")!=-1){
			sqlprefix = "begin atomic";sqlsuffix = "end";
			sqlsplit = ";";
			pagtpl = "select * from (select casit_t_a.*,row_number() over () as casit_t_rn from (${datasql}) as casit_t_a )" +
					"as casit_t_b where casit_t_rn>${start} and casit_t_rn <=(${start}+${limit})";
			
			timesql = "select to_char(current timestamp,'hh24:mi:ss') as today from sysibm.dual";
			
			todaysql = "select to_char(current timestamp,'yyyy-mm-dd') as today from sysibm.dual";

			todaytimesql = "select to_char(current timestamp,'yyyy-mm-dd hh24:mi:ss') as today from sysibm.dual";
			
			firstdayofmonth = "select  to_char(current timestamp,'yyyy-mm')||'-01' from sysibm.dual";
			
			lastdayofmonth = "select  to_char(timestamp(to_char(current timestamp,'yyyy-mm')||'-01 00:00:00') - 1 day" +
				" + 1 month,'yyyy-mm-dd') from sysibm.dual";

			newSeedsql1 = "select zhujian from xt_zhujian where biaoming='${biaoming}' for update WITH RS/RR USE AND KEEP UPDATE LOCKS";
			newSeedsql2 = "update xt_zhujian set zhujian=zhujian+1 where biaoming='${biaoming}'";
			newSeedsql3 = "insert into xt_zhujian(biaoming,zhujian) values('${biaoming}',1000)";
		}else if(dbtype.indexOf("microsoft")!=-1){
			sqlprefix = "";sqlsuffix = "";
			sqlsplit = "\n";
			pagtpl = "select * from (select *,row_number() over (order by ${order}) as casit_t_rn) casit_t_a " +
					"where casit_t_rn>${start} and casit_t_rn <=(${start}+${limit})";
			
			timesql = "select convert(char(19),getdate(),8)";
			todaytimesql = "select convert(char(19),getdate(),120)";
			todaysql = "select convert(char(10),getdate(),120)";
			
			firstdayofmonth = "select convert(char(10),dateadd(month,datediff(month,0,getdate()),0),120)";
			
			lastdayofmonth = "select convert(char(10),dateadd(day,-1,dateadd(month,datediff(month,0,getdate())+1,0)),120)";
		}else if(dbtype.indexOf("sqlite")!=-1){
			pagtpl = "${datasql} limit ${start},${limit}";
		}else if(dbtype.indexOf("postgresql")!=-1){
			sqlprefix = "";sqlsuffix = "";
			sqlsplit = ";";
			pagtpl = "${datasql} limit ${limit} offset ${start}";
			timesql = "select current_time";
			todaytimesql = "select current_timestamp";
			todaysql = "select current_date";
			
			firstdayofmonth = "select extract(year from current_timestamp)||'-'||extract(month from current_timestamp)||'-01'";
			lastdayofmonth = "select to_date(extract(year from current_timestamp)||'-'||extract(month from current_timestamp)||'-01','YYYY-MM-DD') + interval '1 months' - interval '1 days'";
		}else if(dbtype.indexOf("mysql")!=-1){
			sqlprefix = "";sqlsuffix = "";
			sqlsplit = ";";
			pagtpl = "${datasql} limit ${limit} offset ${start}";
			timesql = "select CURTIME()";
			todaytimesql = "select NOW()";
			todaysql = "select CURDATE()";
			 counttpl = "select count(*) from (${datasql}) as totol";
			
			firstdayofmonth = "select DATE_FORMAT(CURRENT_DATE,'%Y-%m-01')";
			lastdayofmonth = "select DATE_FORMAT(CURRENT_DATE,'%Y-%m-01') + INTERVAL 1 MONTH - INTERVAL 1 DAY";
			
		}else{
			log.error("暂不支持的数据库类型："+dbtype);
		}	
	}
	
	
	public Connection getConnection() throws SQLException{
		return DbcpFactory.getConn(dbcpname);			
	}
	public String getStackAndSqlInfo(String sql){
		StringBuffer buf = new StringBuffer("");
		if (DB3.showsqlformat.equals("true")) {
			buf.append( SQLFormat.format(sql) +"\n"); 
		}else
		{	buf.append( sql +"\n");} 
		return buf.toString();
	}
	public void update(String sql ){
		//针对sqlserver数据库，sql为空会导致其后连接异常。
		if(sql==null||sql.trim().equals(""))return;
		 log.debug(getStackAndSqlInfo(sql));
		Statement sm = null;
		Connection conn = null;
		try {
			conn = getConnection();
			sm = conn.createStatement();
			sm.executeUpdate(sql);
		}
		catch (Exception e) {
			log.error(e.getMessage());
			log.error(getStackAndSqlInfo(sql));
			throw new DBException(e.getMessage());
		}
		finally{
			try{sm.close();conn.close();}catch(SQLException e){
				log.error("数据库连接关闭失败!");
			}
		}
	}
	public Object query(String sql,ResultHandler handler){
		//针对sqlserver数据库，sql为空会导致其后连接异常。
		if(sql==null||sql.trim().equals(""))return null;
		 log.debug(getStackAndSqlInfo(sql));
		Statement sm = null;
		Connection conn = null;
		try {
			conn = getConnection();
			sm = conn.createStatement();
			ResultSet rs = sm.executeQuery(sql);
			return handler.handler(rs);
		}
		catch (SQLException e) {
			log.error(e.getMessage());
			log.error(getStackAndSqlInfo(sql));
			throw new DBException(e.getMessage());
		}
		finally{
			try{sm.close();conn.close();}catch(SQLException e){
				log.error("数据库连接关闭失败!");
			}
		}
	}
	
	
	public void updateMultithread(String[] sqls ,int n)
	{ 
		ExecutorService pool = Executors.newFixedThreadPool(n); 
		for (int i = 0; i < sqls.length; i++) { 
			pool.execute(new updateMultithreadclass(sqls[i])) ;
		}  
		pool.shutdown();

	}
	 
	 
	
	
	
	
	public Object exeParamsSql(String sql,StateHandler handler){
		//针对sqlserver数据库，sql为空会导致其后连接异常。
		if(sql==null||sql.trim().equals(""))return null;
		if(log.isInfoEnabled()) log.debug(getStackAndSqlInfo(sql));
		CallableStatement sm = null;
		Connection conn = null;		
		try {
			conn = getConnection();
			sm = conn.prepareCall(sql);
			return handler.handler(sm);
		}
		catch (SQLException e) {
			log.error(e.getMessage());
			log.error(getStackAndSqlInfo(sql));
			throw new DBException(e.getMessage());
		}
		finally{
			try{sm.close();conn.close();}catch(SQLException e){
				log.error("数据库连接关闭失败!");
			}
		}
	}
	private String getValueFromRS(ResultSet rs,String key,int type){
		try { 
			//System.out.println(rs.getBigDecimal(key)+" "+rs.getDouble(key)+" "+rs.getFloat(key)+" ");
			//System.out.println(precision+" "+rs.getBigDecimal(key).toString());
			if(type==2)
			{
				BigDecimal ret=rs.getBigDecimal(key);
				
				if(ret==null) return "0";
				return ret.toString();
			}
			return rs.getString(key);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public JsonO resultToJsonO(ResultSet rs,String unquoted) throws SQLException
	{
		String unstr = ","+unquoted+",";
		if(rs.next()){
			JsonO obj = new JsonO();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cloums = rsmd.getColumnCount();
			for(int i=1;i<=cloums;i++)
			{
				String key = rsmd.getColumnName(i).toLowerCase();
				int type = rsmd.getColumnType(i); 
				Object value = null;
				if(unstr.indexOf(","+key+",")<0)
					value = new Quoted(getValueFromRS(rs,key,type));
				else
					value = new UnQuoted(getValueFromRS(rs,key,type));
				obj.put(key, value);
			}
			return obj;
		}
		return null;
	}
	public JsonO resultToMergedJsonO(ResultSet rs,String unquoted,String variables) throws SQLException
	{
		String unstr = ","+unquoted+",";
		if(rs.next()){
			JsonO obj = new JsonO();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cloums = rsmd.getColumnCount();
			for(int i=1;i<=cloums;i++)
			{
				//String key = rsmd.getColumnName(i).toLowerCase();
				String key = rsmd.getColumnLabel(i).toLowerCase();
				int type = rsmd.getColumnType(i);
				if(key.equals(variables)){
					JsonO tem = new JsonO(rs.getString(key));
					obj.mergeAll(tem);
					continue;
				}
				Object value = null;
				if(unstr.indexOf(","+key+",")<0)
					value = new Quoted(getValueFromRS(rs,key,type));
				else
					value = new UnQuoted(getValueFromRS(rs,key,type));
				obj.put(key, value);
			}
			return obj;
		}
		return null;
	}
	public JsonA resultToJsonA(ResultSet rs,String unquoted) throws SQLException
	{
		final String unstr = ","+unquoted+",";
		JsonA arr = new JsonA();
		ResultSetMetaData rsmd = rs.getMetaData();
		int cloums = rsmd.getColumnCount();
		while(rs.next())
		{
			JsonO obj = new JsonO();
			for(int i=1;i<=cloums;i++)
			{
				String key = rsmd.getColumnLabel(i).toLowerCase();
				//System.out.println(key +" "+rsmd.getColumnLabel(i));
				int type = rsmd.getColumnType(i); 
				Object value = null;

				if(unstr.indexOf(","+key+",")<0)
					value = new Quoted(getValueFromRS(rs,key,type));										
				else{
					value = new UnQuoted(getValueFromRS(rs,key,type));									
				}
				obj.put(key, value);
			}
			arr.add(obj);
		}
		return arr;
	}
	public String getNewID2(final String table,final int count)
	{ 
		log.debug(getStackAndSqlInfo("{call sys_db_newid('"+table+"',"+count+",?)}"));
		Object result = exeParamsSql("{call sys_db_newid(?,?,?)}",
			new StateHandler(){
				public Object handler(CallableStatement sm) throws SQLException{
					sm.setString(1, table);
					sm.setInt(2, count);
					sm.registerOutParameter(3, java.sql.Types.VARCHAR);
					sm.executeUpdate();
					return sm.getString(3);
				}
			}
		);
		if(result == null)return null;
		else return (String)result;
	}
	
	public String getNewID(final String tablenm,final int count)
	{ 
		IdCreator idc = null;
		if(idSeeds.containsKey(tablenm)){
			idc = idSeeds.get(tablenm);
		}else{
			idc = new IdCreator(getIDSeed(tablenm));
			idSeeds.put(tablenm, idc);
		}
		 return idc.getNewId(count);
	}

	public String getIDSeed(final String tablenm){
		Connection conn = null;
		Statement sm = null;
		Long seed = (long) 1000;
		try{
			conn = getConnection();
			conn.setAutoCommit(false);
			sm = conn.createStatement();
			
			ResultSet rs = sm.executeQuery(Template.apply(newSeedsql1,tablenm));
			if(rs.next()){
				seed = rs.getLong(1);
				sm.executeUpdate(Template.apply(newSeedsql2,tablenm));
			}else{
				seed = (long)1000;
				sm.executeUpdate(Template.apply(newSeedsql3,tablenm));
			}
			conn.commit();
			rs.close();
		}catch (SQLException e) {
			log.error(e.getMessage());
			throw new DBException(e.getMessage());
		}
		finally{
			try{sm.close();conn.setAutoCommit(true);
			conn.close();}catch(SQLException e){
				log.error("数据库连接关闭失败!");
			}
		}
		return Long.toOctalString(seed);		
	}
	
	public void saveLongString(String sql,final String longstr)
	{
		if(sql==null||sql.trim().equals(""))return;
		if(log.isInfoEnabled()) log.debug(getStackAndSqlInfo(sql));
		PreparedStatement sm = null;
		Connection conn = null;		
		try {
			conn = getConnection();
			sm = conn.prepareStatement(sql);
			sm.setString(1, longstr);
			sm.executeUpdate();
		}
		catch (SQLException e) {
			log.error(e.getMessage());
			log.error(getStackAndSqlInfo(sql));
			throw new DBException(e.getMessage());
		}
		finally{
			try{sm.close();conn.close();}catch(SQLException e){
				log.error("数据库连接关闭失败!");
			}
		}
	}
	public String getLongString(String sql)
	{
		Object result = exeParamsSql(sql,
				new StateHandler(){
					public Object handler(CallableStatement sm) throws SQLException{
						ResultSet rs = sm.executeQuery();
						String longstr = "";
						if(rs.next()){
							ByteArrayInputStream input = (ByteArrayInputStream)rs.getBlob(1).getBinaryStream();
							byte[] bts = new byte[input.available()];
							input.read(bts, 0, bts.length);
							longstr = new String(bts);
						}
						return longstr;
					}
				}
			);
			return (String)result;
	}
	
	public HashSet<String> getSingleColumnAsSet(String sql)
	{
		HashSet<String> hashset=new HashSet<String>();
		JsonA ja=getResultAsJsonA(sql, "");
		for (int i = 0; i < ja.size(); i++) {
			JsonO jo=ja.getJsonO(i);
			String jovalue="";
			for (Iterator<String> iterator = jo.getKeyIterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				jovalue=jo.getString(key);
			}
			hashset.add(jovalue);
		} 
		return hashset;
	}
	
	public JsonA getSingleColumnAsJsonA(String sql)
	{
		JsonA jaret=new JsonA();
		JsonA ja=getResultAsJsonA(sql, "");
		for (int i = 0; i < ja.size(); i++) {
			JsonO jo=ja.getJsonO(i);
			String jovalue="";
			for (Iterator<String> iterator = jo.getKeyIterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				jovalue=jo.getString(key);
			}
			jaret.add(jovalue);
		} 
		return jaret;
	}
	
	public String getSingleValue(String sql){
		Object result = query(sql,
			new ResultHandler(){
				public Object handler(ResultSet rs) throws SQLException{
					if(rs.next())
						return rs.getString(1);
					else
						return null;
				}
			}
		);
		if(result == null)return null;
		else return (String)result;
	}
	@SuppressWarnings("unchecked")
	public Map<String,String> getSingleRowAsMap(String sql){
		Object result = query(sql,
			new ResultHandler(){
				public Object handler(ResultSet rs) throws SQLException{
					if(rs.next()){
						Map<String,String> map = new HashMap<String,String>();
						ResultSetMetaData rsmd = rs.getMetaData();
						int cloums = rsmd.getColumnCount();
						for(int i=1;i<=cloums;i++)
						{
							String key = rsmd.getColumnName(i).toLowerCase();
							String value = rs.getString(key);
							map.put(key, value);
						}
						return map;
					}
					return null;
				}
			}
		);
		if(result == null)return null;
		else return (Map<String,String>)result;
	}
	public JsonO getSingleRowAsJsonO(String sql,String unquoted){
		final String unstr = ","+unquoted+",";
		Object result = query(sql,
			new ResultHandler(){
				public Object handler(ResultSet rs) throws SQLException{
						return resultToJsonO(rs,unstr);
					}
			}
		);
		if(result == null)return null;
		else return (JsonO)result;
	}
	public JsonO getSingleRowAsMerged(String sql ,String unquoted,final String variables)
	{
		final String unstr = ","+unquoted+",";
		Object result = query(sql,
			new ResultHandler(){
				public Object handler(ResultSet rs) throws SQLException{
					return resultToMergedJsonO(rs,unstr,variables);
				}
			}
		);
		if(result ==null)return null;
		else return (JsonO)result;
	}
	public JsonA getResultAsJsonA(String sql,final String unquoted){
		Object result = query(sql,
				new ResultHandler(){
			public Object handler(ResultSet rs) throws SQLException{
				return resultToJsonA(rs,unquoted);
			}
		}
		);
		return (JsonA)result;
	}
	public String getResultAsHtmlTable(String sql){
		log.debug(getStackAndSqlInfo(sql));
		Connection conn = null;
		Statement sm = null;
		try {
			conn = getConnection();
			sm = conn.createStatement();
			ResultSet rs = sm.executeQuery(sql);
			String values = "",keys = "";
			boolean flag = true;
			ResultSetMetaData rsmd = rs.getMetaData();
			int cloums = rsmd.getColumnCount();
			while(rs.next())
			{
				String tr = "<tr>";
				for(int i=1;i<=cloums;i++)
				{
					if(flag){
						String key = rsmd.getColumnName(i).toLowerCase();
						keys += "<td>"+key+"</td>";
					}
					String value = rs.getString(i);
					tr += "<td>"+value+"</td>";
				}
				tr += "<tr>";
				values += tr;
				flag = false;
			}
			return "<table class='resultdata'><tr>"+keys+"</tr>"+values+"</table>";
		}
		catch (SQLException e) {
			return e.getLocalizedMessage();
		}
		finally{
			try{sm.close();conn.close();}catch(SQLException e){
				log.error("数据库连接关闭失败!");
			}
		}
	}
	public JsonA getResultAsMerged(String sql,final String unquoted,final String variables){
		Object result = query(sql,
			new ResultHandler(){
				public Object handler(ResultSet rs) throws SQLException{
					final String unstr = ","+unquoted+",";
					JsonA arr = new JsonA();
					ResultSetMetaData rsmd = rs.getMetaData();
					int cloums = rsmd.getColumnCount();
					while(rs.next())
					{
						JsonO obj = new JsonO();
						for(int i=1;i<=cloums;i++)
						{
							String key = rsmd.getColumnName(i);
							if(key.equals(variables)){
								JsonO tem = new JsonO(rs.getString(key));
								obj.mergeAll(tem);
								continue;
							}
							Object value = null;
							if(unstr.indexOf(","+key+",")<0)
								value = new Quoted(rs.getString(key));
							else
								value = new UnQuoted(rs.getString(key));
							obj.put(key, value);
						}
						arr.add(obj);
					}
					return arr;
				}
			}
		);
		return (JsonA)result;
	}
	public JsonA getResultAsJsonA(final String sql,final String filter,final String unquoted){
		log.debug("call sys_db_filter('"+sql+"','"+filter+"')");
		Object result = exeParamsSql("{call sys_db_filter(?,?)}",
			new StateHandler(){
				public Object handler(CallableStatement sm) throws SQLException{					
					sm.setString(1, sql);
					sm.setString(2, filter);
					ResultSet rs = sm.executeQuery();
					return resultToJsonA(rs,unquoted);
				}
			}
		);
		return (JsonA)result;
	}
	public JsonO getResultAsStore(String sql,String unquoted){
		JsonA arr = getResultAsJsonA(sql,unquoted);
		if(arr!=null){
			JsonO store = new JsonO();
			store.put("root", arr);
			return store;
		}else return null;
	}
	public JsonO getResultAsStore(String sql,String filter,String unquoted){
		JsonA arr = getResultAsJsonA(sql,filter,unquoted);
		if(arr!=null){
			JsonO store = new JsonO();
			store.put("root", arr);
			return store;
		}else return null;
	}
	public JsonO getResultOfPagination(final String sql,final String order,final String start
			,final String limit,final String unquoted){
		String count = getSingleValue(Template.apply(counttpl,sql));
		Map<String,String> params = new HashMap<String,String>();
		params.put("datasql", sql);
		params.put("order", order);
		params.put("start", start);
		params.put("limit", limit);
System.out.println(pagtpl);		
		String nsql = Template.apply(pagtpl, params);

System.out.println(nsql);		
		JsonA data = getResultAsJsonA(nsql, unquoted);
		JsonO store = new JsonO();
		store.putUnQuoted("total", count);
		store.put("root", data);
		return store;
	}
	public JsonO getResultOfPagination(final String sql,final String order,final String start
			,final String limit,final String filter,final String unquoted){		
		String fsql = filter.replace("#table", "("+sql+") ccc ");
		return getResultOfPagination(fsql,order,start,limit,"");
	}
	private String getSqlValue(JsonO obj,String key,String unquoted){
		String tvalue = obj.getString(key);
		if(unquoted.indexOf(","+key+",")>-1){
			if(tvalue.equals(""))return "null";	
			return tvalue;
		}
		return "'"+tvalue.replace("'", "''")+"'";
	}
	public String jsonOToSQL(JsonO obj,String table,String idcol,String unquoted){
		unquoted = ","+idcol+","+unquoted+",";
		String id_key = "";
		if(obj.containsKey(idcol))id_key = obj.getString(idcol);
		if(id_key.equals("")){
		    id_key = "insert_"+getNewID(table,1);
		    obj.putQuoted(idcol, id_key);
		}
		
		Iterator<String> it = obj.getKeyIterator();
		String sqlstr = "insert into "+table+" (#sqlstr1#) values (#sqlstr2#)";
		if(id_key.startsWith("insert_")){
			id_key = id_key.replace("insert_", "");
			obj.putQuoted(idcol, id_key);
			StringBuffer temstr1 = new StringBuffer();
			StringBuffer temstr2 = new StringBuffer();
			while(it.hasNext()){
				String key = it.next();
				String value = getSqlValue(obj,key,unquoted);
				temstr1.append(",").append(key);
				temstr2.append(",").append(value);
			}
			return sqlstr.replace("#sqlstr1#", temstr1.substring(1))
							.replace("#sqlstr2#", temstr2.substring(1));
		}else{
			sqlstr = "update "+table+" set #sqlstr1# where "+idcol+"="+id_key;
			StringBuffer temstr1 = new StringBuffer();
			while(it.hasNext()){
				String key = it.next();
				String value = getSqlValue(obj,key,unquoted);
				temstr1.append(",").append(key).append("=").append(value);
			}
			return sqlstr.replace("#sqlstr1#", temstr1.substring(1));			
		}
	}
	public void saveJsonOToDB(JsonO obj,String table,String idcol,String unquoted){
		String sql = jsonOToSQL(obj,table,idcol,unquoted);
		update(sql);
	}
	public JsonA saveJsonOToDB(JsonO obj,String table,String idcol,String unquoted,String repeat){
		return saveJsonOToDB(obj, table, idcol, unquoted, repeat, "");
	}
	public JsonA saveJsonOToDB(JsonO obj,String table,String idcol,String unquoted,String repeat,String where){ 
		if( !repeat.contains(","))
			{return saveJsonOToDBsingle(obj, table, idcol, unquoted, repeat, where);}
		//1、如果没有改repeat字段，则调用以前的方法。
		boolean hasrepeatcode=false; 
		Iterator<String> it = obj.getKeyIterator();  
		while(it.hasNext()){
			String key = it.next(); 
			if((repeat+",").contains((key+",")))
			{hasrepeatcode=true;  } 
		}   
		if (!hasrepeatcode) {
			String finalstr=jsonOToSQL(obj, table, idcol, unquoted); 
			update(finalstr);
			return new JsonA();
		}
		//2、得到sql和回滚sql
		String[] repeats=repeat.split(",");
		String res[]=jsonOToSQLRepeat(obj, table, idcol, unquoted, repeat);
		String sqlstr=res[0];
		String backstr=res[1]; 

		//3、update上去，并测试是否repeat,是就roll back.不重复就update全部。
		try{
			update(sqlstr);
				JsonA jarepeat=new JsonA();
				if(!where.equals("")){ for (int i = 0; i < repeats.length; i++) {
					JsonA jstp=checkDBRepeat(table, repeats[i], repeats[i],where,""); 
					jarepeat.addAll(jstp);
				}  }
				else{ for (int i = 0; i < repeats.length; i++) {
					JsonA jstp=checkDBRepeat(table, repeats[i], repeats[i]); 
					jarepeat.addAll(jstp);
				} }
			if(jarepeat.size()>0)
			{
				update(backstr);
				return jarepeat;
			}else
			{
				String finalstr=jsonOToSQL(obj, table, idcol, unquoted); 
				update(finalstr);
				return new JsonA();
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			update(backstr);
			return new JsonA();
		}
	}
	private JsonA saveJsonOToDBsingle(JsonO obj,String table,String idcol,String unquoted,String repeat,String where){ 
		//1、如果没有改repeat字段，则调用以前的方法。
		boolean hasrepeatcode=false; 
		Iterator<String> it = obj.getKeyIterator();  
		while(it.hasNext()){
			String key = it.next(); 
			if(key.equals(repeat))
			{hasrepeatcode=true;  } 
		}   
		if (!hasrepeatcode) {
			String finalstr=jsonOToSQL(obj, table, idcol, unquoted); 
			update(finalstr);
			return new JsonA();
		}
		//2、得到sql和回滚sql
		String res[]=jsonOToSQLRepeatsingle(obj, table, idcol, unquoted, repeat);
		String sqlstr=res[0];
		String backstr=res[1]; 

		//3、update上去，并测试是否repeat,是就roll back.不重复就update全部。
		try{
			update(sqlstr);
				JsonA jarepeat=new JsonA();
				if(!where.equals("")){ jarepeat=checkDBRepeat(table, repeat, repeat,where,"");}
				else{ jarepeat=checkDBRepeat(table, repeat, repeat);}
			if(jarepeat.size()>0)
			{
				update(backstr);
				return jarepeat;
			}else
			{
				String finalstr=jsonOToSQL(obj, table, idcol, unquoted); 
				update(finalstr);
				return new JsonA();
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			update(backstr);
			return new JsonA();
		}
	} 
	
	public String[] jsonOToSQLRepeat(JsonO obj,String table,String idcol,String unquoted,String repeat)
	{
		if(!repeat.contains(","))
			{return jsonOToSQLRepeatsingle(obj, table, idcol, unquoted, repeat);}  
		JsonO objold=new JsonO();
		unquoted = ","+idcol+","+unquoted+",";
		String id_key = "";
		if(obj.containsKey(idcol))id_key = obj.getString(idcol);
		if(id_key.equals("")){
		    id_key = "insert_"+getNewID(table,1);
		    obj.putQuoted(idcol, id_key);
		}else
		{
		}
		
		//1、得到sqlstr backstr 
		String sqlstr = "insert into "+table+" (#sqlstr1#) values (#sqlstr2#)";  
		String backstr = "delete from "+table+" where "+idcol+"="+id_key.replace("insert_", ""); 
		Iterator<String> it = obj.getKeyIterator();  
		
		if(id_key.startsWith("insert_")){
			id_key = id_key.replace("insert_", "");
			obj.putQuoted(idcol, id_key);
			StringBuffer temstr1 = new StringBuffer();
			StringBuffer temstr2 = new StringBuffer();
			while(it.hasNext()){
				String key = it.next();
				String value = getSqlValue(obj,key,unquoted); 
				temstr1.append(",").append(key);
				temstr2.append(",").append(value);
			}
			sqlstr= sqlstr.replace("#sqlstr1#", temstr1.substring(1))
							.replace("#sqlstr2#", temstr2.substring(1));
		}else{
			sqlstr = "update "+table+" set #sqlstr1# where "+idcol+"="+id_key;
			backstr = "update "+table+" set #sqlstr1# where "+idcol+"="+id_key; 
			objold=getSingleRowAsJsonO("select * from "+table+"  where "+idcol+" ="+id_key, "");
			Iterator<String> itold = objold.getKeyIterator(); 
			
			StringBuffer temstr1 = new StringBuffer();  
			while(it.hasNext()){
				String key = it.next();
				String value = getSqlValue(obj,key,unquoted);
				if(  (repeat+",").contains((key+","))  )
				{temstr1.append(",").append(key).append("=").append(value);  }
			} 
			sqlstr= sqlstr.replace("#sqlstr1#", temstr1.substring(1));	
			
			StringBuffer temstr1old = new StringBuffer();  
			while(itold.hasNext()){
				String key = itold.next();
				String value = getSqlValue(objold,key,unquoted);
				if(  (repeat+",").contains((key+","))  )
				{temstr1old.append(",").append(key).append("=").append(value);  }
			} 
			backstr= backstr.replace("#sqlstr1#", temstr1old.substring(1));	
		}
		String[] res=new String[2];
		res[0]=sqlstr; res[1]=backstr;
		return res;
	}
	
	public String[] jsonOToSQLRepeatsingle(JsonO obj,String table,String idcol,String unquoted,String repeat)
	{

		JsonO objold=new JsonO();
		unquoted = ","+idcol+","+unquoted+",";
		String id_key = "";
		if(obj.containsKey(idcol))id_key = obj.getString(idcol);
		if(id_key.equals("")){
		    id_key = "insert_"+getNewID(table,1);
		    obj.putQuoted(idcol, id_key);
		}else
		{
		}
		
		//1、得到sqlstr backstr 
		String sqlstr = "insert into "+table+" (#sqlstr1#) values (#sqlstr2#)";  
		String backstr = "delete from "+table+" where "+idcol+"="+id_key.replace("insert_", ""); 
		Iterator<String> it = obj.getKeyIterator();  
		
		if(id_key.startsWith("insert_")){
			id_key = id_key.replace("insert_", "");
			obj.putQuoted(idcol, id_key);
			StringBuffer temstr1 = new StringBuffer();
			StringBuffer temstr2 = new StringBuffer();
			while(it.hasNext()){
				String key = it.next();
				String value = getSqlValue(obj,key,unquoted); 
				temstr1.append(",").append(key);
				temstr2.append(",").append(value);
			}
			sqlstr= sqlstr.replace("#sqlstr1#", temstr1.substring(1))
							.replace("#sqlstr2#", temstr2.substring(1));
		}else{
			sqlstr = "update "+table+" set #sqlstr1# where "+idcol+"="+id_key;
			backstr = "update "+table+" set #sqlstr1# where "+idcol+"="+id_key; 
			objold=getSingleRowAsJsonO("select * from "+table+"  where "+idcol+" ="+id_key, "");
			Iterator<String> itold = objold.getKeyIterator(); 
			
			StringBuffer temstr1 = new StringBuffer();  
			while(it.hasNext()){
				String key = it.next();
				String value = getSqlValue(obj,key,unquoted);
				if(key.equals(repeat))
				{temstr1.append(",").append(key).append("=").append(value);  }
			} 
			sqlstr= sqlstr.replace("#sqlstr1#", temstr1.substring(1));	
			
			StringBuffer temstr1old = new StringBuffer();  
			while(itold.hasNext()){
				String key = itold.next();
				String value = getSqlValue(objold,key,unquoted);
				if(key.equals(repeat))
				{temstr1old.append(",").append(key).append("=").append(value);  }
			} 
			backstr= backstr.replace("#sqlstr1#", temstr1old.substring(1));	
		}
		String[] res=new String[2];
		res[0]=sqlstr; res[1]=backstr;
		return res;
	}
	
	public String[] jsonAToSQLRepeat(JsonA arr,String table,String idcol,String unquoted,String repeat){
		if(!repeat.contains(","))
			{return jsonAToSQLRepeatsingle(arr, table, idcol, unquoted, repeat);}
		int idc = 0;
		for(int i=0;i<arr.size();i++)
			{if(!arr.getJsonO(i).containsKey(idcol)||
					arr.getJsonO(i).getString(idcol).equals("")) 
					{idc++;}	}
		String newid = "";
		if(idc>0) newid = getNewID(table,idc);
		StringBuffer sqlstr = new StringBuffer(""); 
		StringBuffer backstr = new StringBuffer("");
		for(int i=0;i<arr.size();i++)
		{
			JsonO obj = arr.getJsonO(i);
			if(!arr.getJsonO(i).containsKey(idcol)||obj.getString(idcol).equals("")){
				obj.putQuoted(idcol, "insert_"+newid);
				newid = String.valueOf(Integer.parseInt(newid)+1);
			}
			String[] objsql = jsonOToSQLRepeat(obj,table,idcol,unquoted,repeat);
			sqlstr.append(objsql[0]).append(sqlsplit);
			backstr.append(objsql[1]).append(sqlsplit);
		}
		String[] res=new String[2];
		res[0]=sqlstr.toString(); res[1]=backstr.toString();
		return res;
	}
	
	public String[] jsonAToSQLRepeatsingle(JsonA arr,String table,String idcol,String unquoted,String repeat){
		int idc = 0;
		for(int i=0;i<arr.size();i++)
			{if(!arr.getJsonO(i).containsKey(idcol)||
					arr.getJsonO(i).getString(idcol).equals("")) 
					{idc++;}	}
		String newid = "";
		if(idc>0) newid = getNewID(table,idc);
		StringBuffer sqlstr = new StringBuffer(""); 
		StringBuffer backstr = new StringBuffer("");
		for(int i=0;i<arr.size();i++)
		{
			JsonO obj = arr.getJsonO(i);
			if(!arr.getJsonO(i).containsKey(idcol)||obj.getString(idcol).equals("")){
				obj.putQuoted(idcol, "insert_"+newid);
				newid = String.valueOf(Integer.parseInt(newid)+1);
			}
			String[] objsql = jsonOToSQLRepeatsingle(obj,table,idcol,unquoted,repeat);
			sqlstr.append(objsql[0]).append(sqlsplit);
			backstr.append(objsql[1]).append(sqlsplit);
		}
		String[] res=new String[2];
		res[0]=sqlstr.toString(); res[1]=backstr.toString();
		return res;
	}
	
	
	public String jsonAToSQL(JsonA arr,String table,String idcol,String unquoted){
		int idc = 0;
		for(int i=0;i<arr.size();i++)
			if(!arr.getJsonO(i).containsKey(idcol)||
					arr.getJsonO(i).getString(idcol).equals("")) idc++;	
		String newid = ""; 
		if(idc>0) newid = getNewID(table,idc);
		StringBuffer sql = new StringBuffer("");
		for(int i=0;i<arr.size();i++)
		{
			JsonO obj = arr.getJsonO(i);
			if(!arr.getJsonO(i).containsKey(idcol)||obj.getString(idcol).equals("")){
				obj.putQuoted(idcol, "insert_"+newid);
				newid = String.valueOf(Integer.parseInt(newid)+1);
			}
			String objsql = jsonOToSQL(obj,table,idcol,unquoted);
			sql.append(objsql).append(sqlsplit);
		}
		if(!sql.equals("")) return sql.toString();
		return sql.toString();
	}
	
	public void saveJsonAToDB(JsonA arr,String table,String idcol,String unquoted){
		String sql = sqlprefix + " " + jsonAToSQL(arr,table,idcol,unquoted) + " " + sqlsuffix;
		System.out.println(sql);
		update(sql);
	}
	public JsonA saveJsonAToDB(JsonA arr,String table,String idcol,String unquoted,String repeat)
	{
		return saveJsonAToDB(arr, table, idcol, unquoted, repeat, "");
	}
	
	public JsonA saveJsonAToDB(JsonA arr,String table,String idcol,String unquoted,String repeat,String where)
	{
		if( !repeat.contains(","))
			{return saveJsonAToDBsingle(arr, table, idcol, unquoted, repeat, where);}
	
		//1、如果没有改repeat字段，则调用以前的方法。如果有则放入一个临时jsona中，只放有涉及repeat的。
		JsonA jahasrepeat=new JsonA();
		boolean hasrepeatcode=false;
		for (int i = 0; i < arr.size(); i++) {
			JsonO johr=arr.getJsonO(i);
			Iterator<String> it = johr.getKeyIterator(); 
			boolean jsonohasrepeat=false;
			while(it.hasNext()){
				String key = it.next(); 
				if((repeat+",").contains((key+",")))
				{hasrepeatcode=true;  jsonohasrepeat=true;} 
			} 
			if(jsonohasrepeat)
			{  jahasrepeat.add(new JsonO(johr.toString()));jsonohasrepeat=false;} 
		}
		if (!hasrepeatcode) {
			String finalstr=jsonAToSQL(arr, table, idcol, unquoted); 
			update(sqlprefix + " " +finalstr+ " " + sqlsuffix);   
			return new JsonA();
		}
		//2、得到sql和回滚sql
		String[] repeats=repeat.split(",");
		
		String res[]=jsonAToSQLRepeat(jahasrepeat, table, idcol, unquoted, repeat);
		String sqlstr=res[0];
		String backstr=res[1]; 
log.debug("--sqlstr:"+sqlstr);
log.debug("--backstr:"+backstr);
		
		

		//3、update上去，并测试是否repeat,是就roll back.不重复就update全部。
		try{
			update(sqlprefix + " " +sqlstr+ " " + sqlsuffix);    
				JsonA jarepeatret=new JsonA();
				if(!where.equals(""))
					{ for (int i = 0; i < repeats.length; i++) {
						JsonA jstp=checkDBRepeat(table, repeats[i], repeats[i],where,""); 
						  jarepeatret.addAll(jstp);
					}}
				else{ for (int i = 0; i < repeats.length; i++) {
					JsonA jstp=checkDBRepeat(table, repeats[i], repeats[i]); 
					  jarepeatret.addAll(jstp);
				} }
			if(jarepeatret.size()>0)
			{  
				update(sqlprefix + " " +backstr+ " " + sqlsuffix);   
				return jarepeatret;
			}else
			{
				String finalstr=jsonAToSQL(arr, table, idcol, unquoted);  
				update(sqlprefix + " " +backstr+finalstr+ " " + sqlsuffix);   
				return new JsonA();
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			update(sqlprefix + " " +backstr+ " " + sqlsuffix);
			return new JsonA();
		}
	}
	
	private JsonA saveJsonAToDBsingle(JsonA arr,String table,String idcol,String unquoted,String repeat,String where){
		//1、如果没有改repeat字段，则调用以前的方法。如果有则放入一个临时jsona中，只放有涉及repeat的。
		JsonA jahasrepeat=new JsonA();
		boolean hasrepeatcode=false;
		for (int i = 0; i < arr.size(); i++) {
			JsonO johr=arr.getJsonO(i);
			Iterator<String> it = johr.getKeyIterator(); 
			boolean jsonohasrepeat=false;
			while(it.hasNext()){
				String key = it.next(); 
				if(key.equals(repeat))
				{hasrepeatcode=true;  jsonohasrepeat=true;} 
			} 
			if(jsonohasrepeat)
			{  jahasrepeat.add(new JsonO(johr.toString()));jsonohasrepeat=false;} 
		}
		if (!hasrepeatcode) {
			String finalstr=jsonAToSQL(arr, table, idcol, unquoted); 
			update(sqlprefix + " " +finalstr+ " " + sqlsuffix);   
			return new JsonA();
		}
		//2、得到sql和回滚sql
		String res[]=jsonAToSQLRepeatsingle(jahasrepeat, table, idcol, unquoted, repeat);
		String sqlstr=res[0];
		String backstr=res[1]; 
log.debug("--1"+sqlstr);
log.debug("--2"+backstr);
		

		//3、update上去，并测试是否repeat,是就roll back.不重复就update全部。
		try{
			update(sqlprefix + " " +sqlstr+ " " + sqlsuffix);    
				JsonA jarepeatret=new JsonA();
				if(!where.equals(""))
					{ jarepeatret=checkDBRepeat(table, repeat, repeat,where,"");}
				else{ jarepeatret=checkDBRepeat(table, repeat, repeat);}
			if(jarepeatret.size()>0)
			{  
				update(sqlprefix + " " +backstr+ " " + sqlsuffix);   
				return jarepeatret;
			}else
			{
				String finalstr=jsonAToSQL(arr, table, idcol, unquoted);  
				update(sqlprefix + " " +backstr+finalstr+ " " + sqlsuffix);   
				return new JsonA();
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			update(sqlprefix + " " +backstr+ " " + sqlsuffix);
			return new JsonA();
		}
	}
	
	private String getParamValue(Map<String, String> map,ServletRequest request,String key){
		if(map!=null&&map.containsKey(key)){
			return map.get(key).toString();
		}else{
			if(request!=null)
			{return request.getParameter(key);}
			else {return null;}
		}
	}
	public JsonO getAutoStore(String sql,String order,String unquoted,
			Map<String, String> params,ServletRequest request){
		String filter = getParamValue(params,request,"filter");
		String start = getParamValue(params,request,"start");
		String limit =getParamValue(params,request,"limit");
		JsonO store;
		if(filter!=null&&!filter.equals("")){
			if(start!=null&&!start.equals("")){
				store = getResultOfPagination(sql, order, start,limit,filter, unquoted);
			}else{
				store = getResultAsStore(sql, filter, unquoted);						
			}
		}else{
			if(start!=null&&!start.equals("")){
				System.out.println(getParamValue(params,request,"limit"));
				store = getResultOfPagination(sql, order, start, limit, unquoted);
			}else{
				store = getResultAsStore(sql, unquoted);						
			}				
		}
		return store;
	}
	
	/*
	private String testrepeat(final String sql,final String tablenm,
			final String selectcols,final String groupbycols)
	{
		String res ="";
		Statement  pst = null;
		Connection conn = null;
		Savepoint sp1=null; 
		Savepoint sp2=null;
		Savepoint sp3=null;
		try {
            conn=getConnection();          
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            sp1=conn.setSavepoint("sp1");
            pst=conn.createStatement();  
            System.out.println(sql);
            pst.executeUpdate(sql);
            sp2=conn.setSavepoint("sp2");
            pst.executeUpdate(sql);
            sp3=conn.setSavepoint("sp3");
            pst.executeUpdate(sql);
            
            pst.executeUpdate(sql);
            //conn.commit(); 
            String countsql="select rep from ( select '"+selectcols+"' as rep,count(*) as tot from "+tablenm+" group by  "+groupbycols+")  where tot>1";
            
            //Statement stmt = conn.createStatement();    
            ResultSet rs = pst.executeQuery(countsql);    
            res=  resultToJsonA(rs,"").toString();
            System.out.println(res);  
            // conn.rollback(sp3);
            conn.commit(); 
	     } catch (SQLException e) { 
	            e.printStackTrace();
	            try {
	                   conn.rollback(sp1);
	                   conn.commit();
	            } catch (SQLException e1) {
	                   e1.printStackTrace();
	            }
	     }finally{
	            try {
	                   conn.setAutoCommit(true);
	                   conn.close();
	            } catch (SQLException e) {
	                   e.printStackTrace();
	            }
	     }		
		return res;
	}*/
	
	public String checkDBRepeat(final String sql,final String tablenm,
			final String selectcols,final String groupbycols)
	{
		Object result = exeParamsSql("{call sys_db_repeat(?,?,?,?,?)}",
			new StateHandler(){
				public Object handler(CallableStatement sm) throws SQLException{
					sm.registerOutParameter(5, java.sql.Types.VARCHAR);
					sm.setString(1, sql);
					sm.setString(2, tablenm);
					sm.setString(3, selectcols);
					sm.setString(4, groupbycols);
					sm.executeQuery();
					return sm.getString(5);
				}
			}
		);
		return (String)result;	
	}
	public void rebuiltTreeold(final String tablenm,final String cdcolnm,final String idcolnm){
		exeParamsSql("{call sys_tree_rebuilt(?,?,?)}",
			new StateHandler(){
				public Object handler(CallableStatement sm) throws SQLException{
					sm.setString(1, tablenm);
					sm.setString(2, cdcolnm);
					sm.setString(3, idcolnm);
					sm.executeQuery();
					return null;
				}
			}
		);
	}
	
	public void rebuiltTree(final String tablenm,final String id,final String parents)
	{
		  rebuiltTree (tablenm, id, parents, "levels", "sublevels", "leaf");
	}
	public void rebuiltTreetoislast(final String tablenm,final String id,final String parents)
	{ 
		rebuiltTree (tablenm, id, parents, "levels", "sublevels", "islast","1","0");
	}
	
	public void rebuiltTree(final String tablenm,final String id,final String parents,final String levels,final String sublevels,final String leaf)
	{ 
		rebuiltTree (tablenm, id, parents, levels, sublevels, leaf,"true","false");
	}
	
	
	public void rebuiltTreeJson(final String tablenm,final String id,final String parents,final String levels,final String sublevels,final String leaf,final String truestr,final String falsestr)
	{ 
		TreeMap<String, String[]> map=new TreeMap<String, String[]>	();   
		try {  
			String sql="select ltrim(rtrim( TO_CHAR("+id +"))) as id, "+parents+" as parents from "+tablenm+" "; 
			JsonA ja=getResultAsJsonA(sql, "");
			for (int i = 0; i < ja.size(); i++)  
			{  
				String s[]=new String[5]; 
					s[0]=ja.getJsonO(i).getString("id");
					s[1]=ja.getJsonO(i).getString("parents"); 
					s[2]=new String(s[0]);
					s[3]=new String(s[0]);
					s[4]=new String();
				map.put(s[0], s);
			}
	 
			Dotree dt=new Dotree(map);
			for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				map.get(key)[2]= dt.getlevels(key);
				dt.dosublevels(key,new String (key));
				dt.doleaf(key,truestr,falsestr);
			}
			for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				update("update "+tablenm+" a set   "+levels+"='"+map.get(key)[2]+"' ,"+sublevels+" ='"+map.get(key)[3]+"' ,"+leaf+"='"+map.get(key)[4]+"'  where "+id+"= '"+key+"' ");
			}  
			log.debug("[rebuilttree] success");
		}
		catch (Exception e) { 
			e.printStackTrace();
			throw new DBException(e.getMessage());
			
		} 
	}
	
	public void rebuiltTree(final String tablenm,final String id,final String parents,final String levels,final String sublevels,final String leaf,final String truestr,final String falsestr)
	{  
		rebuiltTreeCachedRowSetImpl(tablenm, id, parents, levels, sublevels, leaf, truestr, falsestr);
	}

	
	public void rebuiltTreeCachedRowSetImpl(final String tablenm,final String id,final String parents,final String levels,final String sublevels,final String leaf,final String truestr,final String falsestr)
	{ 
		TreeMap<String, String[]> map=new TreeMap<String, String[]>	();
		Statement sm = null;
		Connection conn = null;
		ResultSet rs=null;
		CachedRowSetImpl crs=null; 
		try { 
			conn = getConnection();
			sm = conn.createStatement(); 
			String sql="select  ("+id +") as id, "+parents+" as parents from "+tablenm+" "; 
			//if(dbtype.indexOf("db2")!=-1){
			//	sql="select ltrim(rtrim( CHAR("+id +"))) as id, "+parents+" as parents from "+tablenm+" "; 
		//	}
			
			rs = sm.executeQuery(sql); 
			crs = new CachedRowSetImpl(); 
			crs.populate(rs);
			rs.close(); 		
			String shuzi="'";
			while(crs.next())
			{  
				String s[]=new String[5]; 
					s[0]=crs.getString(id);   //while(s[0].startsWith("0")) {s[0]=s[0].substring(1, s[0].length());}  while(s[0].endsWith(".")) {s[0]=s[0].substring(0, s[0].length()-1); shuzi="";}
					s[1]=crs.getString(parents); 
					s[2]=new String(s[0]);
					s[3]=new String(s[0]);
					s[4]=new String();
				map.put(s[0], s);  //System.out.println(s[0]+" "+s[1]+" "+s[2]+" "+s[3]+ " "+s[4]);
			}
	 
			Dotree dt=new Dotree(map);
			for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				map.get(key)[2]= dt.getlevels(key);
				dt.dosublevels(key,new String (key));
				dt.doleaf(key,truestr,falsestr);
			}
			for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				//update("update "+tablenm+" a set   "+levels+"='"+map.get(key)[2]+"' ,"+sublevels+" ='"+map.get(key)[3]+"' ,"+leaf+"='"+map.get(key)[4]+"'  where "+id+"= "+shuzi+key+shuzi+" ");
				update("update "+tablenm+" a set   "+levels+"='"+map.get(key)[2]+"' ,"+leaf+"='"+map.get(key)[4]+"'  where "+id+"= "+shuzi+key+shuzi+" ");
			} 
			log.debug("[rebuilttree] success");
		}
		catch (SQLException e) { 
			e.printStackTrace();
			throw new DBException(e.getMessage());
			
		}
		finally{
			try{sm.close();conn.close();crs.close();}
			catch(SQLException e){
				log.error("数据库连接关闭失败!");
			}
		}
	}
	
	
	//0 =id   .1 =parent    ,2 =levels  ,3 =sublevels
	 class Dotree
	{
		 TreeMap<String, String[]> map;
		 Dotree(TreeMap<String, String[]> map)
		 {	 this.map=map;	 }		 
		 String  getlevels(String id)
		 {	 
			 String[] mapid1=map.get(id);
			 System.out.println(mapid1[0]+" "+id);
			 
			 if(mapid1==null)
			 { return id;
			 } else if(mapid1[1].equals("0"))
			 { return id;
			 } else
			 { return getlevels(new String( mapid1[1] )    ) +","+id;		 
			 }
		 }		 
		 void dosublevels(String id,String topid)
		 { 
			 String[] mapid1=map.get(id);
			 if(mapid1==null ||mapid1[1]==null)
			 {  
				 System.out.println("[buildtree-error] id:"+id+"  has no parents or parents is error");
			 } else  if(mapid1[1].equals("0")) { }
			   else if(map.get(mapid1[1])==null)
			 {  
				 System.out.println("[buildtree-error] id:"+mapid1[1]+"  has no parents or parents is error");
			 }   else {//System.out.println(mapid1[1]);
				 map.get(mapid1[1])[3]=map.get(mapid1[1])[3]+","+topid;
				 dosublevels(mapid1[1],new String (topid));
			 }
		 }
		 void doleaf(String id,String truestr,String falsestr)
		 { String rt=truestr;
			for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				if(map.get(key)[1].equals(id))
				{	rt=falsestr;	}
			}
			map.get(id)[4]=rt;
		 }
		 
	}
	 
	 
	 public void rebuiltTreeForInfo(final String tablenm,final String id,final String parents,final String text,final String levels,final String sublevels ,final int ignorelevels,final int ignoreself,final String split)
		{ 
			TreeMap<String, String[]> map=new TreeMap<String, String[]>	();
			Statement sm = null;
			Connection conn = null;
			ResultSet rs=null;
			CachedRowSetImpl crs=null; 
			try { 
				conn = getConnection();
				sm = conn.createStatement();
				String sql="select TO_CHAR("+id +") as id, "+parents+" as parents,TO_CHAR("+text +") as text from "+tablenm+" "; 
				rs = sm.executeQuery(sql); 
				crs = new CachedRowSetImpl(); 
				crs.populate(rs);
				rs.close(); 		
				while(crs.next())
				{  
					String s[]=new String[5]; 
						s[0]=crs.getString("id");
						s[1]=crs.getString("parents"); 
						s[2]=new String(s[0]);
						s[3]=crs.getString("text");
						s[4]=crs.getString("text");
					map.put(s[0], s);
				}
		 
				DotreeForInfo dt=new DotreeForInfo(map);
				for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					map.get(key)[2]= dt.getlevels(key,split);
					dt.dosublevels(key,map.get(key)[4],split); 
					if(ignorelevels!=0){map.get(key)[2]= dt.ignorelevels(map.get(key)[2],ignorelevels,split);};
					if(ignoreself!=0){map.get(key)[2]= dt.ignoreself(map.get(key)[2],ignoreself,split);};
				}
				for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					if(sublevels.equals(""))
					{
						update("update "+tablenm+" a set   "+levels+"='"+map.get(key)[2]+"'     where "+id+"= '"+key+"' ");
					}else{
						update("update "+tablenm+" a set   "+levels+"='"+map.get(key)[2]+"' ,"+sublevels+" ='"+map.get(key)[3]+"'   where "+id+"= '"+key+"' ");
					}
				} 
				log.debug("[rebuilttreeforinfo] success");
			}
			catch (SQLException e) { 
				e.printStackTrace();
				throw new DBException(e.getMessage());
				
			}
			finally{
				try{sm.close();conn.close();crs.close();}
				catch(SQLException e){
					log.error("数据库连接关闭失败!");
				}
			}
		}
		
		
		//0 =id   .1 =parent    ,2 =levels  ,3 =sublevels
		 class DotreeForInfo
		{
			 TreeMap<String, String[]> map;
			 DotreeForInfo(TreeMap<String, String[]> map)
			 {	 this.map=map;	 }		 
			 String  getlevels(String id,String split)
			 {	 
				 String[] mapid1=map.get(id);
				 if(mapid1==null)
				 { return id;
				 } else if(mapid1[1].equals("0"))
				 { return mapid1[4];
				 } else
				 { return getlevels(new String( mapid1[1] ) ,split  ) +split+mapid1[4];		 
				 }
			 }		 
			 void dosublevels(String id,String topid,String split)
			 { 
				 String[] mapid1=map.get(id);
				 if(mapid1==null ||mapid1[1]==null)
				 {  
					 System.out.println("[buildtree-error] id:"+id+"  has no parents or parents is error");
				 } else  if(mapid1[1].equals("0")) { }
				   else if(map.get(mapid1[1])==null)
				 {  
					 System.out.println("[buildtree-error] id:"+mapid1[1]+"  has no parents or parents is error");
				 }   else { //System.out.println(topid);
					 map.get(mapid1[1])[3]=map.get(mapid1[1])[3]+split+topid;
					 dosublevels(mapid1[1],new String (topid),split);
				 }
			 } 
			 
			  String ignorelevels(String mp2,int ignorelevels,String split)
			  {
				  String[] mp2s=mp2.split(split);
				  if(mp2s.length <= ignorelevels) { return "";}
				  else
				  {
					  mp2="";
					  for (int i = ignorelevels; i < mp2s.length; i++) {
						mp2=mp2+split+mp2s[i];
					}
					  mp2=mp2.substring(split.length(), mp2.length());
				  }
				  return mp2;
			  }
			  String ignoreself(String mp2,int ignoreself,String split)
			  {
				  String[] mp2s=mp2.split(split);
				  if(mp2s.length <= ignoreself) { return "";}
				  else
				  {
					  mp2="";
					  for (int i = 0; i < (mp2s.length-ignoreself); i++) {
						mp2=mp2+split+mp2s[i];
					} 
					  mp2=mp2.substring(split.length(), mp2.length() );
				  }
				  return mp2;
			  }
		}
	
	 
	 public void  saveFiletoBlob(String sql, File file)
   { 
		 Connection conn = null;
			CallableStatement sm = null; 
			try {
				FileInputStream fis = new FileInputStream(file);
				InputStream inputStream = new BufferedInputStream(fis);
				conn = getConnection();
				sm = conn.prepareCall(sql);
				sm.setBinaryStream(1, inputStream,new Integer((int)file.length()));
				sm.executeUpdate();
			}
			catch (Exception e) {
				log.error(e.getMessage()); 
			}
			finally{
				try{
					sm.close();
					conn.close();
				}catch(SQLException e){
					log.error(e.getMessage()); 
				}
			} 
	}
	
	 public void  saveFiletoBlob(String sql, byte[] bts)
	   { 
			 Connection conn = null;
				CallableStatement sm = null; 
				try {
					ByteArrayInputStream fis = new ByteArrayInputStream(bts);
					InputStream inputStream = new BufferedInputStream(fis);
					conn = getConnection();
					sm = conn.prepareCall(sql);
					sm.setBinaryStream(1, inputStream,new Integer((int)bts.length));
					sm.executeUpdate();
				}
				catch (Exception e) {
					log.error(e.getMessage()); 
				}
				finally{
					try{
						sm.close();
						conn.close();
					}catch(SQLException e){
						log.error(e.getMessage()); 
					}
				} 
		}
	 
	public  byte[] getFilefromBlob(String sql)
		{
			Connection conn = null;
			Statement sm = null;
			byte[] bytes = null;
			try {
				conn =getConnection();
				sm = conn.createStatement();
				ResultSet rs = sm.executeQuery(sql);
				if(rs.next()){
					bytes = rs.getBytes(1);				
				}
			}
			catch (SQLException e) { 
				log.error(e.getMessage());
			}
			finally{
				try{
					sm.close();
					conn.close();
				}catch(SQLException e){
					log.error(e.getMessage());
				}
			}
			return bytes;
		}	
	
	public boolean isSQLiteMemCacheExist(String tablenm){
		String sql = "select count(*) from sqlite_master where type='table' and name='"+tablenm+"'";
		String count = "";
		try{
			count = getSingleValue(sql);		
		}catch(Exception e){}
		if(count!=null&&count.equals("1"))return true;
		return false;
	}
	public void clearSQLiteMemCache(String tablenm){
		DataInterface di = MultiDataSourceFactory.getDataInterface("java:comp/env/sqlite");
		di.update("drop table if exists "+tablenm);
	}
	public void createSQLiteMemCache(final String sql,final String tablenm,final String unquoted){
		query(sql,
			new ResultHandler(){
				public Object handler(ResultSet rs) throws SQLException{
					Connection conn = null;
					try{
						DataInterface di = MultiDataSourceFactory.getDataInterface("java:comp/env/sqlite");
						conn = di.getConnection();
						conn.setAutoCommit(false);
						Statement sm = conn.createStatement();
						ResultSetMetaData rsmd = rs.getMetaData();
						int cloums = rsmd.getColumnCount();
						boolean ctable = false;
						while(rs.next())
						{
							if(!ctable){
								sm.executeUpdate("drop table if exists "+tablenm);								
								String csql = "create table "+tablenm+" (";
								for(int i=1;i<=cloums;i++){
									String colnm = rsmd.getColumnName(i).toLowerCase();
									csql += (i>1?",":"")+colnm;
								}
								csql += ")";
								sm.executeUpdate(csql);
								ctable = true;
							}
							String isql = "insert into "+tablenm+" values(";
							for(int i=1;i<=cloums;i++)
							{
								String key = rsmd.getColumnName(i);
								String value = rs.getString(i);
								if(value==null){
									isql += (i>1?",":"") + value;																							
								}else{
									if(unquoted.indexOf(","+key+",")!=-1)
										isql += (i>1?",":"") + "'" + value + "'";
									else
										isql += (i>1?",":"") + "'" + value + "'";										
								}
							}
							isql += ")";
							sm.executeUpdate(isql);
						}
						sm.close();
						conn.commit();
						conn.setAutoCommit(true);
						
					}catch(Exception e){
						log.error(e.getMessage());
					}finally{
						conn.close();						
					}
					return null;
				}
			}
		);
	}
	public String getTime(){
		return getSingleValue(timesql);
	}
	public String getTime(String beginIndex,String endIndex){
		return getSingleValue(todaytimesql).substring(Integer.valueOf(beginIndex), Integer.valueOf(endIndex));
	}
	public String getTime(int beginIndex,int endIndex){
		return getSingleValue(todaytimesql).substring(beginIndex, endIndex);
	}
	public String getToday(){
		return getSingleValue(todaysql);
	}
	public String getTodayTime()
	{
		return getSingleValue(todaytimesql);
	}
	public String getFirstDayOfMonth(){
		return getSingleValue(firstdayofmonth);
	}
	public String getLastDayOfMonth(){
		return getSingleValue(lastdayofmonth);
	}
	
	
	
	
	
	
	public static JsonA JsonATodate(JsonA ja,String key,String template)
	{
		JsonA ja2=new   JsonA();
		for (int i = 0; i < ja.size() ; i++) {
			JsonO jo=ja.getJsonO(i);
			if(jo.containsKey(key))
			{ 
				String v= jo.getString(key);
				v=Template.apply(template,v); 
				jo.putQuoted(key, v);
			}
			ja2.add(jo);
		} 
		return ja2;
	}
	
	public static JsonO JsonOTodate(JsonO jo,String key,String template)
	{
			if(jo.containsKey(key))
			{ 
				String v= jo.getString(key);
				v=Template.apply(template,v); 
				jo.putQuoted(key, v);
			} 
		return jo;
	}
	
	 
	
 
	
	//得到table的最大id
	public String getmaxid(String table)
	{
		return getSingleValue("select currentpk from sys_tablepk where tablename='"+table+"'");
	}
	
	//取得一个表的colums，用于辅助生成sql。前提是table中数据不为空，若为空从系统表中取，但是目前没做。
	public  String gettablecolums(String table)
	{
		String sql="select * from "+table;
		JsonO jo=getResultOfPagination(sql, "", "0", "1", "");
		
		String res="";
		if(jo.getJsonA("root").size()>0)
		{			
			JsonO jo1=jo.getJsonA("root").getJsonO(0);
			for (Iterator<String> iterator = jo1.getKeyIterator(); iterator.hasNext();) {
				String  key = (String) iterator.next();
				if(!key.equals("casit_t_rn"))
				{res=res+key+",";}
			}
		}else
		{
			//若表为空，就从系统表取，平时难以用得上，待开发。
		}
		
		return res.substring(0, res.length()-1);
	}
	
	public JsonA checkDBRepeat(String tablename ,String repeatcolum ,String groupcolum)
	{ 
		return checkDBRepeat(tablename, repeatcolum, groupcolum, "", "");
	}
	
	public JsonA checkDBRepeat(String tablename ,String repeatcolum ,String groupcolum,String where,String havingvalue)
	{ 	
		String sql="select rep from ( select "+repeatcolum+" as rep,count(*) as tot from "+tablename+" group by "+groupcolum+") as tot_tab where tot>1";
		if(!where.equals(""))
		{
			sql="select rep from ( select "+repeatcolum+" as rep,count(*) as tot from "+tablename+" where "+where+" group by "+groupcolum+"  ) as tot_tab where tot>1";
		}
		
		JsonA ja=getResultAsJsonA(sql, "");
		JsonA jaret=new JsonA();
		JsonA jaretinner=new JsonA();
		
		for (int i = 0; i < ja.size(); i++) {
			String t=ja.getJsonO(i).getString("rep"); 
			jaretinner.addQuoted(t)  ; 
		}
		if (jaretinner.size()>0) {
			JsonO joret1=new JsonO();
			joret1.putQuoted ("colum", repeatcolum); 
			joret1.put("repeat", jaretinner);
			jaret.add(joret1);  
		}
		return jaret;
	}
	
	
    public static String getTime(String ini)
    {
    	 
           	Calendar cl = Calendar.getInstance(); 
           	//if(format.equals("Y-m-d")){format="yyyy-M-d";}
        		DateFormat df6 = new SimpleDateFormat("yyyy-MM-dd");   
                if ( ini.equals("today"))
                	{ini = df6.format(cl.getTime()) ;}
                //取得当月/年 的第一天或者最后一天:
                else if ( ini.equals("firstdayofmonth"))
                	{cl.set(Calendar.DAY_OF_MONTH, 1);
                	ini = df6.format(cl.getTime()); 	}
                else if ( ini.equals("lastdayofmonth"))
                 	{cl.set(Calendar.DAY_OF_MONTH, 1);
            		cl.set(Calendar.MONTH, cl.get(Calendar.MONTH)+1); 
            		cl.set(Calendar.DAY_OF_YEAR, cl.get(Calendar.DAY_OF_YEAR) -1);
            		ini = df6.format(cl.getTime());}
                else if ( ini.equals("firstdayofyear"))
                	{cl.set(Calendar.DAY_OF_YEAR, 1); 
                	ini = df6.format(cl.getTime());}
                else if ( ini.equals("lastdayofyear"))
                	{cl.set(Calendar.DAY_OF_YEAR, 1);
                	cl.set(Calendar.YEAR, cl.get(Calendar.YEAR)+1); 
                	cl.set(Calendar.DAY_OF_YEAR, cl.get(Calendar.DAY_OF_YEAR) -1);
                	ini = df6.format(cl.getTime());}
                //取得*天/月/年 之前的日期:
                else if ( ini.contains("daybefore") && (!ini.contains("dayof"))  )
                {
               	 int i=Integer.valueOf(ini.replace("daybefore", ""));
               	 cl.set(Calendar.DAY_OF_YEAR, cl.get(Calendar.DAY_OF_YEAR) -i);
               	ini = df6.format(cl.getTime());}
                else if ( ini.contains("monthbefore") && (!ini.contains("dayof"))  )
                {	
               	 int i=Integer.valueOf(ini.replace("monthbefore", ""));
               	 cl.set(Calendar.MONTH, cl.get(Calendar.MONTH) -i);
               	ini = df6.format(cl.getTime());}
                else if ( ini.contains("yearbefore") && (!ini.contains("dayof"))  )
             	{ 
               	 int i=Integer.valueOf(ini.replace("yearbefore", ""));
               	 cl.set(Calendar.YEAR, cl.get(Calendar.YEAR) -i);
               	ini = df6.format(cl.getTime());}
               //取得*月/年 之前的第一天或者最后一天:
                else if ( ini.contains("firstdayof") &&  ini.contains("monthbefore")  )
             	{
               	 int i=Integer.valueOf(ini.replace("firstdayof", "").replace("monthbefore", "")); 
               	 cl.set(Calendar.DAY_OF_MONTH, 1); 
               	 cl.set(Calendar.MONTH, cl.get(Calendar.MONTH)-i);
               	ini = df6.format(cl.getTime());}
                else if (  ini.contains("lastdayof") &&  ini.contains("monthbefore")  )
             	{
               	 int i=Integer.valueOf(ini.replace("lastdayof", "").replace("monthbefore", "")); 
               	 cl.set(Calendar.DAY_OF_MONTH, 1);
             		cl.set(Calendar.MONTH, cl.get(Calendar.MONTH)+1-i); 
             		cl.set(Calendar.DAY_OF_YEAR, cl.get(Calendar.DAY_OF_YEAR) -1);
             		ini = df6.format(cl.getTime());}
                else if (  ini.contains("firstdayof") &&  ini.contains("yearbefore")  )
             	{
               	 int i=Integer.valueOf(ini.replace("firstdayof", "").replace("yearbefore", "")); 
               	 cl.set(Calendar.DAY_OF_YEAR, 1); 
               	 cl.set(Calendar.YEAR, cl.get(Calendar.YEAR)-i);
               	ini = df6.format(cl.getTime());}
                else if (  ini.contains("lastdayof") &&  ini.contains("yearbefore")  )
             	{
               	 int i=Integer.valueOf(ini.replace("lastdayof", "").replace("yearbefore", "")); 
               	 cl.set(Calendar.DAY_OF_YEAR, 1);
                 	cl.set(Calendar.YEAR, cl.get(Calendar.YEAR)+1-i); 
                 	cl.set(Calendar.DAY_OF_YEAR, cl.get(Calendar.DAY_OF_YEAR) -1);
                 	ini = df6.format(cl.getTime());}
               
 
                return ini;
    }
}

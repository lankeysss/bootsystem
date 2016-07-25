package com.casit.suwen.datatool;
 
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet; 
import java.util.Map; 

import javax.servlet.ServletRequest; 
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.casit.json.JsonA;
import com.casit.json.JsonO;
import com.casit.suwen.plugin.PluginInterface;
import com.casit.suwen.security.CookieSession;

public class D 
{
	 Logger log = Logger.getLogger(D.class); 
	private String dbcpname=DB3.dbcpname;
	private DataInterface di=MultiDataSourceFactory.getDataInterface(dbcpname); 
	private String dbtype=di.dbtype;
	private Template tp;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private CookieSession cs;
	
	//得到内部属性
	public String getSource(){
		return this.dbcpname;	}
	public DataInterface getDataInterface(){
		return this.di;	}
	public String getDBType(){
		return this.dbtype;	}
	public Template getTemplate(){
		return this.tp;	}
	public ServletRequest getHttpServletRequest(){
		return this.request;}
	public ServletResponse getHttpServletResponse(){
		return this.response;}
	public CookieSession getCookieSession(){
		return cs;}
	
	
	
	//构造函数，默认数据源
	public D()
		{this(DB3.dbcpname);} 
	public D(Map<String,String> map)
		{this(map, DB3.dbcpname); } 
	public D(HttpServletRequest request)
		{this(request, DB3.dbcpname);} 
	public D(JsonO jo)
		{this(jo, DB3.dbcpname); } 
	public D(Map<String,String> map,HttpServletRequest request ,HttpServletResponse response,JsonO jo)
		{this(map,request,response,jo,DB3.dbcpname);}
	public D(D d)
		{this(d.tp.clonemap(),d.request,d.response,null,d.dbcpname);}
	//构造函数，指定数据源
	public D(String source)
		{tp=new Template();initdi(source); } 
	public D(Map<String,String> map,String source)
		{tp=new Template(map);  initdi(source); } 
	public D(HttpServletRequest request,String source)
		{tp=new Template(request);this.request=request; initdi(source);addcs();} 
	public D(JsonO jo,String source)
		{tp=new Template(jo); initdi(source);} 
	public D(Map<String,String> map,HttpServletRequest request , HttpServletResponse response,JsonO jo,String source)
		{tp=new Template(map,request,jo); this.request=request;this.response=response;initdi(source);addcs();}
	
	
	 
	public void initdi(String sourcestr){
		if(  sourcestr!=null && !sourcestr.equals("") && !sourcestr.equals(dbcpname))  
			{this.dbcpname=sourcestr;di = MultiDataSourceFactory.getDataInterface(sourcestr);}
	}
	 
	private void addcs(){
		if(request!=null)
			{cs=(CookieSession)request.getAttribute("CasitCookieSession_");} 
	}
	
	//进行sql输出
	private   String applysql(String tpl){   
		return PluginInterface. SqlFilter(  tp.applysql(tpl) ,this);
	}
	
	
	
	
	//di方法
	public JsonO getAutoStore(String sql,String order,String unquoted)	{
		return di.getAutoStore(applysql(sql), order, unquoted, null, request);
	}
	
	public Connection getConnection() throws SQLException{
		return di.getConnection();			
	} 
	
	//blob
	public  byte[] getFilefromBlob(String sql)	{ 
		return di.getFilefromBlob(applysql(sql));
	}
	 public void  saveFiletoBlob(String sql, byte[] bts)	{ 
		 di.saveFiletoBlob(applysql(sql), bts);
	}
	
	//longstring
	public String getLongString(String sql)	{
		return di.getLongString(applysql(sql));
	}
	public void saveLongString(String sql,final String longstr){
		 di.saveLongString(sql, longstr);
	}
	
	
	public String getResultAsHtmlTable(String sql){
		return di.getResultAsHtmlTable(applysql(sql));
	}
	
	//jsonA
	public JsonA getResultAsJsonA(String sql,final String unquoted){
		return di.getResultAsJsonA(applysql(sql), unquoted);
	}
	public JsonA getResultAsJsonA(final String sql,final String filter,final String unquoted){
		return di.getResultAsJsonA(sql, filter, unquoted);
	}
	
	
	public JsonA getResultAsMerged(String sql,final String unquoted,final String variables){
		return di.getResultAsMerged(applysql(sql), unquoted, variables);
	}
		//store
	public JsonO getResultAsStore(String sql,String unquoted){
		return di.getResultAsStore(applysql(sql), unquoted);
	}
	public JsonO getResultAsStore(String sql,String filter,String unquoted){
		return di.getResultAsStore(sql, filter, unquoted);
	}
		//page
	public JsonO getResultOfPagination(final String sql,final String order,final String start
			,final String limit,final String unquoted){
		return di.getResultOfPagination(applysql(sql), order, start, limit, unquoted);
	}
	public JsonO getResultOfPagination(final String sql,final String order,final String start
			,final String limit,final String filter,final String unquoted){		
		return di.getResultOfPagination(sql, order, start, limit, filter, unquoted);
	}
	
	//单独行
	public JsonO getSingleRowAsJsonO(String sql,String unquoted){
		return di.getSingleRowAsJsonO(applysql(sql), unquoted);
	}
	public Map<String,String> getSingleRowAsMap(String sql){
		return di.getSingleRowAsMap(applysql(sql));
	}
	public JsonO getSingleRowAsMerged(String sql ,String unquoted,final String variables)	{
		return di.getSingleRowAsMerged(applysql(sql), unquoted, variables);
	}
	
	//单独列
	public HashSet<String> getSingleColumnAsSet(String sql){
		return di.getSingleColumnAsSet(applysql(sql));
	}
	public JsonA getSingleColumnAsJsonA(String sql)	{
		return di.getSingleColumnAsJsonA(applysql(sql));
	}
	
	//单独值
	public String getSingleValue(String sql){
		return di.getSingleValue(applysql(sql));
	}
	
	
	//保存
	public void saveJsonAToDB(JsonA arr,String table,String idcol,String unquoted){
		di.saveJsonAToDB(arr, table, idcol, unquoted);
	}
	public JsonA saveJsonAToDB(JsonA arr,String table,String idcol,String unquoted,String repeat)	{
		return di.saveJsonAToDB(arr, table, idcol, unquoted, repeat);
	}
	public JsonA saveJsonAToDB(JsonA arr,String table,String idcol,String unquoted,String repeat,String where)	{
		return di.saveJsonAToDB(arr, table, idcol, unquoted, repeat, where);
	}
	
	
	
	//执行sql
	public Object exeParamsSql(String sql,StateHandler handler){
		return di.exeParamsSql(applysql(sql), handler);
	}
	public Object query(String sql,ResultHandler handler){
		return di.query(applysql(sql), handler);
	}
	public void update(String sql ){
		 di.update(applysql(sql));
	}
	
	
	//打印sql
	public String getStackAndSqlInfo(String sql){
		String res=di.getStackAndSqlInfo(applysql(sql));
 System.out.println(res);
		return res;
	}
	
	
	
	
	
	//时间
	public String getTime(){
		return di.getTime();
	} 
	public String getTime(String beginIndex,String endIndex){
		return di.getTime(beginIndex, endIndex);
	}
	public String getTime(int beginIndex,int endIndex){
		return di.getTime(beginIndex, endIndex);
	}
	public String getTime(String ini){
		return DataInterface.getTime(ini);
	} 
	public String getToday(){
		return di.getToday();
	}
	public String getTodayTime()	{
		return di.getTodayTime();
	}
	
	
	
	//Json 转换
	public String jsonAToSQL(JsonA arr,String table,String idcol,String unquoted){
		return di.jsonAToSQL(arr, table, idcol, unquoted);
	}
	public String jsonOToSQL(JsonO obj,String table,String idcol,String unquoted){
		return di.jsonOToSQL(obj, table, idcol, unquoted);
	}
	public  JsonA JsonATodate(JsonA ja,String key,String template){
		return DataInterface.JsonATodate(ja, key, template);
	} 
	public static JsonO JsonOTodate(JsonO jo,String key,String template)	{
		return DataInterface.JsonOTodate(jo, key, template);
	}
	
	
	//rebuilttree
	public void rebuiltTree(final String tablenm,final String id,final String parents)	{
		di.rebuiltTree(tablenm, id, parents);
	}
	public void rebuiltTree(final String tablenm,final String id,final String parents,final String levels,final String sublevels,final String leaf)	{ 
		di.rebuiltTree(tablenm, id, parents);
	}
	public void rebuiltTreeForInfo(final String tablenm,final String id,final String parents,final String text,final String levels,final String sublevels ,final int ignorelevels,final int ignoreself,final String split){
		di.rebuiltTreeForInfo(tablenm, id, parents, text, levels, sublevels, ignorelevels, ignoreself, split);
	}
	 
	
	//sqlite
	public boolean isSQLiteMemCacheExist(String tablenm){
		return di.isSQLiteMemCacheExist(tablenm);
	}
	public void clearSQLiteMemCache(String tablenm){
		di.clearSQLiteMemCache(tablenm);
	} 
	public void createSQLiteMemCache(final String sql,final String tablenm,final String unquoted){
		di.createSQLiteMemCache(applysql(sql), tablenm, unquoted);
	}
	

	
	
	
	
	
}

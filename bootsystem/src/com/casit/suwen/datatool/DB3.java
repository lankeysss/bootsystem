package com.casit.suwen.datatool;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletRequest;
import org.apache.log4j.Logger;
import com.casit.json.JsonA;
import com.casit.json.JsonO;
public class DB3 {
	static Logger log = Logger.getLogger(DB3.class);
	private static DataInterface di;
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
			
			//source = prop.getProperty("com.casit.db.default.datasource");
			dbcpname=prop.getProperty("com.casit.db.default.dbcpname");
			di = MultiDataSourceFactory.getDataInterface(dbcpname);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static  DataInterface getdi()
	{
		return di;
	}
	public static String checkDBRepeat(final String sql,final String tablenm,
			final String selectcols,final String groupbycols){
		return di.checkDBRepeat(sql, tablenm, selectcols, groupbycols);
	}
	public static JsonA checkDBRepeat(final String tablenm,
			final String selectcols,final String groupbycols){
		return di.checkDBRepeat(tablenm, selectcols, groupbycols);
	}
	public static Connection getConnection() throws SQLException{
		return di.getConnection();
	}
	public static void update(String sql ){
		di.update(sql);
	}
	public static void  updateMultithread(String[] sqls ,int n){
		  di.updateMultithread(sqls,n);
	}
	public static Object query(String sql,ResultHandler handler){
		return di.query(sql, handler);
	}
	public static Object exeParamsSql(String sql,StateHandler handler){
		return di.exeParamsSql(sql, handler);
	}
	public static String getNewID(final String table,final int count)
	{
		return di.getNewID(table, count);
	}
	public static void saveLongString(String sql,final String longstr)
	{
		di.saveLongString(sql, longstr);
	}
	public static String getLongString(String sql)
	{
		return di.getLongString(sql);
	}
	public static String getSingleValue(String sql){
		return di.getSingleValue(sql);
	}
	public static Map<String,String> getSingleRowAsMap(String sql){
		return di.getSingleRowAsMap(sql);
	}
	public static JsonO getSingleRowAsJsonO(String sql,String unquoted){
		return di.getSingleRowAsJsonO(sql, unquoted);
	}
	public static JsonO getSingleRowAsMerged(String sql ,String unquoted,final String variables)
	{
		return di.getSingleRowAsMerged(sql, unquoted, variables);
	}
	public static JsonA getResultAsJsonA(String sql,final String unquoted){
		return di.getResultAsJsonA(sql, unquoted);
	} 
	public static String getResultAsHtmlTable(String sql){
		return di.getResultAsHtmlTable(sql);
	}
	public static JsonA getResultAsMerged(String sql,final String unquoted,final String variables){
		return di.getResultAsMerged(sql, unquoted, variables);
	}
	public static JsonA getResultAsJsonA(final String sql,final String filter,final String unquoted){
		return di.getResultAsJsonA(sql, filter, unquoted);
	}
	public static JsonO getResultAsStore(String sql,String unquoted){
		return di.getResultAsStore(sql, unquoted);
	}
	public static JsonO getResultAsStore(String sql,String filter,String unquoted){
		return di.getResultAsStore(sql, filter, unquoted);
	}
	public static JsonO getResultOfPagination(final String sql,final String order,final String start
			,final String limit,final String unquoted){
		return di.getResultOfPagination(sql, order, start, limit, unquoted);
	}
	public static JsonO getResultOfPagination(final String sql,final String order,final String start
			,final String limit,final String filter,final String unquoted){		
		return di.getResultOfPagination(sql, order, start, limit, filter, unquoted);
	}
	public static String jsonOToSQL(JsonO obj,String table,String idcol,String unquoted){
		return di.jsonOToSQL(obj, table, idcol, unquoted);
	}
	public static void saveJsonOToDB(JsonO obj,String table,String idcol,String unquoted){
		di.saveJsonOToDB(obj, table, idcol, unquoted);
	}
	public static JsonA saveJsonOToDB(JsonO obj,String table,String idcol,String unquoted,String repeat){
		return di.saveJsonOToDB(obj, table, idcol, unquoted,repeat);
	}
	public static JsonA saveJsonOToDB(JsonO obj,String table,String idcol,String unquoted,String repeat,String where){
		return di.saveJsonOToDB(obj, table, idcol, unquoted,repeat,where);
	}
	public static String jsonAToSQL(JsonA arr,String table,String idcol,String unquoted){
		return di.jsonAToSQL(arr, table, idcol, unquoted);
	}
	public static void saveJsonAToDB(JsonA arr,String table,String idcol,String unquoted){
		di.saveJsonAToDB(arr, table, idcol, unquoted);
	}
	public static JsonA saveJsonAToDB(JsonA arr,String table,String idcol,String unquoted,String repeat){
		return di.saveJsonAToDB(arr, table, idcol, unquoted,repeat);
	}
	public static JsonA saveJsonAToDB(JsonA arr,String table,String idcol,String unquoted,String repeat,String where){
		return di.saveJsonAToDB(arr, table, idcol, unquoted,repeat,where);
	}
	public static JsonO getAutoStore(String sql,String order,String unquoted,
			Map<String,String> params,ServletRequest request){
		return di.getAutoStore(sql, order, unquoted, params, request);
	}
	public static String getTime(){
		return di.getTime();
	}
	public static String getTime(String time){
		return DataInterface.getTime(time);
	}
	public static String getTime(String beginIndex,String endIndex){
		return di.getTime(beginIndex, endIndex);
	}
	public static String getTime(int beginIndex,int endIndex){
		return di.getTime(beginIndex, endIndex);
	}
	public static String getToday(){
		return di.getToday();
	}
	public static String getTodayTime(){
		return di.getTodayTime();
	}
	public static String getFirstDayOfMonth(){
		return di.getFirstDayOfMonth();
	}
	public static String getLastDayOfMonth(){
		return di.getLastDayOfMonth();
	}
	
	public static void rebuiltTree(final String tablenm,final String id,final String parents)
	{
		 di.rebuiltTree(tablenm, id, parents);
	}
	public static void   rebuiltTree(final String tablenm,final String id,final String parents,final String levels,final String sublevels,final String leaf)
	{
		 di.rebuiltTree(tablenm, id, parents, levels, sublevels, leaf);
	}
	
	public static void rebuiltTreetoislast(final String tablenm,final String id,final String parents)
	{ 
		di.rebuiltTreetoislast(tablenm, id, parents);
	}
	
	public static void rebuiltTree(final String tablenm,final String id,final String parents,final String levels,final String sublevels,final String leaf,final String truestr,final String falsestr)
	{ 
		di.rebuiltTree(tablenm, id, parents, levels, sublevels, leaf, truestr, falsestr);
	}
	 public static void rebuiltTreeForInfo(final String tablenm,final String id,final String parents,final String text,final String levels,final String sublevels ,final int ignorelevels,final int ignoreself,final String split)
	{ 
		di.rebuiltTreeForInfo(tablenm, id, parents, text, levels, sublevels, ignorelevels, ignoreself, split) ;
	}
	public  static JsonA resultToJsonA(ResultSet rs,String unquoted) throws SQLException
	{
		return di.resultToJsonA(rs, unquoted);
	}
	
	public  static JsonO resultToJsonO(ResultSet rs,String unquoted) throws SQLException
	{
		return di.resultToJsonO(rs, unquoted);
	}
	
	public  static JsonO resultToMergedJsonO(ResultSet rs,String unquoted,String variables) throws SQLException  
	{
		return di.resultToMergedJsonO(rs, unquoted, variables);
	}
	
	 public static void  saveFiletoBlob(String sql, File file)
	 {
		  di.saveFiletoBlob(sql, file);
	 }
	 public static void  saveFiletoBlob(String sql, byte[] bts)
	 {
		  di.saveFiletoBlob(sql, bts);
	 }
     public static byte[] getFilefromBlob(String sql)
	 {
    	  return  di.getFilefromBlob(sql);
	 }
     
 	public static JsonA JsonATodate(JsonA ja,String key,String template)
	{
		return DataInterface.JsonATodate(ja, key, template);
	}
 	
 	public static JsonO JsonOTodate(JsonO jo,String key,String template)
 	{
 		return DataInterface.JsonOTodate(jo, key, template);
 	}
}

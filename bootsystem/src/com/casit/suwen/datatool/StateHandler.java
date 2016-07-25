package com.casit.suwen.datatool;

import java.sql.CallableStatement;
import java.sql.SQLException;
/**
 * 该接口以sql查询的CallableStatement为参数主要用于调用以类似"{call xxx(?,?)}"的形式调用存储过程<br>
 * 并处理调用存储过程产生的结果集和out 类型参数。引入该接口主要有两个目的：<br>
 * 1、把程序真正的实现部分从try、catch、finally中解脱出来，同时确保数据库连接被关闭。<br>
 * 2、为底层DB提供一个更灵活的接口，使得CASIT平台不拘泥于JSON。
 * @author 陈朝阳
 */
public interface StateHandler {
	public Object handler(CallableStatement sm) throws SQLException;
}

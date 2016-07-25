package com.casit.suwen.datatool;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultHandler {
	public Object handler(ResultSet rs) throws SQLException;
}

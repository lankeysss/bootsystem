package com.casit.suwen.datatool;

import java.util.HashMap;
import java.util.Map;
public class MultiDataSourceFactory {
	private static Map<String,DataInterface> map = new HashMap<String,DataInterface>();
	private static void createDataInterface(String source){
		DataInterface di = new DataInterface(source);
		map.put(source, di);
	}
	public static DataInterface getDataInterface(String source){
		if(!map.containsKey(source)){
			createDataInterface(source);
		}
		return map.get(source);
	}
}

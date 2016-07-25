package com.casit.suwen.autom;
import java.util.ArrayList;
public class TableList {
	private ArrayList<String> list = new ArrayList<String>();
	public void push(String tablenm){
		list.add(tablenm);
	}
	public String getTableInfo(){
		int size = list.size();
		String tem = "";
		for(int i=0;i<size;i++)
		{
			tem += "," + list.get(i);
		}
		return tem;
	}
}

package com.casit.suwen.autom;

import java.util.ArrayList;

public class Stack {
	private ArrayList<Integer> list = new ArrayList<Integer>();
	public void push(int el){
		list.add(el);
	}
	public int pop(){
		int size = list.size();
		int pop = list.get(size-1);
		list.remove(size-1);
		return pop;
	}
	public boolean isEmpty(){
		if(list.size()==0)return true;
		return false;
	}
	public int peek(){
		int size = list.size();
		return list.get(size-1);			
	}
	public String toString(){
		int size = list.size();
		String str = "[";
		for(int i=0;i<size;i++)
		{
			str += (i==0?"":",") + list.get(i);
		}
		str += "]";
		return str;
	}
	public int getLevels(){
		int size = list.size();
		int tot = -1;
		for(int i=0;i<size;i++)
		{
			if(list.get(i)==2)tot++;
		}
		return tot;
	}
}

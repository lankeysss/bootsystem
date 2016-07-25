package com.casit.suwen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PathNode {
	private String path; 
	private Action action = null;
	private List<PathNode> childs = new ArrayList<PathNode>();
	public PathNode(String path){
		this.path = path;
	}
	public String getPath()
	{
		return this.path;
	}
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public void addChild(PathNode node){
		String p = node.getPath();
		if(p.startsWith("{")){
			this.childs.add(node);
		}else{
			this.childs.add(0, node);
		}
	}
	public boolean match(String target){
		boolean match = false;
		if(path.startsWith("{")){
			int i = path.indexOf(":");
			if(i>0){
				String re = path.substring(i,path.length()-2);
				match = Pattern.matches(re, target);
			}else
				match = true;
		}else if(path.equals(target)){
			match = true;
		}
		return match;
	}
	public boolean match(String target,Map<String,String> map){
		boolean match = false;
		if(path.startsWith("{")){
			int i = path.indexOf(":");
			if(i>0){
				String re = path.substring(i,path.length()-1);
				match = Pattern.matches(re, target);
				if(match)map.put(path.substring(1,i), target);
			}else{
				match = true;
				map.put(path.substring(1,path.length()-1), target);
			}
		}else if(path.equals(target)){
			match = true;
		}
		return match;
	}
	public PathNode getOrCreateChild(String uri){
		if(uri.startsWith("/"))uri = uri.substring(1);
		int c = uri.indexOf("/");
		String target;
		if(c>0)target = uri.substring(0, c);
		else target = uri;
		
		PathNode p = null;
		int size = childs.size();
		for(int i=0;i<size;i++)
		{
			PathNode temp = childs.get(i);
			if(temp.match(target)){
				p = temp;break;
			}
		}
		if(p==null){
			p = new PathNode(target);
			this.addChild(p);
		}
		if(c<=0){
			return p;
		}
		return p.getOrCreateChild(uri.substring(c+1));		
	}
	public Action getAction(String uri,Map<String,String> map){
		if(uri.startsWith("/"))uri = uri.substring(1);
		int c = uri.indexOf("/");
		String target;
		if(c>0)target = uri.substring(0, c);
		else target = uri;
		
		int size = childs.size();
		for(int i=0;i<size;i++)
		{ 
			PathNode temp = childs.get(i);
			if(temp.match(target,map)){
				if(c<=0){
					return temp.getAction();
				}else{	
					return temp.getAction(uri.substring(c+1),map);
				}
			}
		}
		return null;
	}
}

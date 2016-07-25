package com.casit.suwen;

import java.lang.reflect.Method;
import java.util.*;

import org.apache.log4j.Logger;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import com.casit.suwen.datatool.DB3;

public class JavaassitTool 
{
	static Logger log=Logger.getLogger(JavaassitTool.class);
	 
	@SuppressWarnings("rawtypes")
	public static String[] getparamnames( Class clas,Method method)
	{
		return getparamnames(clas.getName(),method.getName());
	}
	public static boolean isvalid()
	{
		if(DB3.usejavassist.equals("true"))
		{
			return true;
		}else
		{
			return false;
		}
	}

	public static String[] getparamnames(String classname,String methodname)
	{
		String[] res=null;
		ClassPool pool = ClassPool.getDefault();  
        CtClass cc;
		try { 
			cc = pool.get(classname);
			pool.insertClassPath(new ClassClassPath( Class.forName(classname)));
	        CtMethod cm = cc.getDeclaredMethod(methodname);
	        
	        MethodInfo methodInfo = cm.getMethodInfo();  
	        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();  
	    	LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);  
	    	if (attr == null)  {
	     	    //exception
	      	 }
	    	String[] paramNames = new String[cm.getParameterTypes().length];  
	    	int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;  
	    	for (int i = 0; i < paramNames.length; i++)  {
	    		paramNames[i] = attr.variableName(i + pos);      
	    		} 
	    	res=paramNames;
		} catch (Exception e) { 
			log.error(e.getMessage());
		}  
		
		return res;
	}
	@SuppressWarnings("rawtypes")
	public static Map<String,String[]> getfunctionparamnamesCLS(Class clas)
	{
		return getfunctionparamnamesSTR(clas.getName());
	}
	
	public static Map<String,String[]> getfunctionparamnamesSTR(String classname)
	{
		Map<String,String[]> map=new HashMap<String, String[]>();
		ClassPool pool = ClassPool.getDefault();  
        CtClass cc; 
		try { 
			pool.insertClassPath(new ClassClassPath( Class.forName(classname)));
			cc = pool.get(classname);
	        CtMethod[] cm = cc.getDeclaredMethods();
	        for (int i = 0; i < cm.length; i++) {
	        	MethodInfo methodInfo = cm[i].getMethodInfo();  
		        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();  
		    	LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);  
		    	if (attr == null)  {
		     	    //没有参数
		    		map.put(cm[i].getName(), null);
		      	 }
		    	else
		    	{
		    		String[] paramNames = new String[cm[i].getParameterTypes().length];  		    		
			    	int pos = Modifier.isStatic(cm[i].getModifiers()) ? 0 : 1;  
			    	for (int j = 0; j < paramNames.length; j++)  {
			    		paramNames[j] = attr.variableName(j + pos);     
			    		} 
			    	map.put(cm[i].getName(), paramNames);
		    	}
			}
	        
 
		} catch (Exception e) { 
			log.error(e.getMessage());
		}  
		
		return map;
	}
	
}

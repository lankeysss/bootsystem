package com.casit.suwen.security;

import java.io.File; 
import java.sql.Date; 

import com.casit.suwen.datatool.DB3;

public class FileSave 
{
	public static String filesaveroot="d://TEMP";
	public static String filesaveencode="true";
	
	static
	{
		try{
			filesaveroot=DB3.filesaveroot;
			filesaveencode=DB3.filesaveencode;
		}catch (Exception e) {
			e.printStackTrace(); 
		}
	}
	
	public static String Filepath(String filename)
	{
		return Filepath(filesaveroot, filename);
	}
	
	public static String Filepath(String root,String filename)
	{
		root=getrootstr(root);
		String ret=new String(root);
		String[] ps=gettime();
		long l1=System.currentTimeMillis()%1000;
		long l2=System.nanoTime()%1000000;
		filename=l1+"_"+l2+"_"+filename;
		if(filesaveencode.equals("true"))
		{
			ret=ret+"//"+BASE.encode(ps[0])+"//"+BASE.encode(ps[1])+"//"+BASE.encode(ps[2])+"//"+BASE.encode(filename);
		}else
		{
			ret=ret+"//"+ps[0]+"//"+ps[1]+"//"+ps[2]+"//"+filename;
		} 
		creatdict(ret);
		return ret;
	}
	
	public static String Filepathencode(String filename)
	{
		return Filepathencode(filesaveroot, filename);
	}
	
	public static String Filepathencode(String root,String filename)
	{
		root=getrootstr(root);
		String ret=new String(root);
		String[] ps=gettime();
		long l1=System.currentTimeMillis()%1000;
		long l2=System.nanoTime()%1000000;
		filename=l1+"_"+l2+"_"+filename;
		{
			ret=ret+"//"+BASE.encode(ps[0])+"//"+BASE.encode(ps[1])+"//"+BASE.encode(ps[2])+"//"+BASE.encode(filename);
		} 
		creatdict(ret);
		return ret;
	}
	
	public static String Filepathdecode(String filename)
	{
		return Filepathdecode(filesaveroot, filename);
	}
	
	public static String Filepathdecode(String root,String filename)
	{
		root=getrootstr(root);
		String ret=new String(root);
		String[] ps=gettime();
		long l1=System.currentTimeMillis()%1000;
		long l2=System.nanoTime()%1000000;
		filename=l1+"_"+l2+"_"+filename;
		 
			ret=ret+"//"+ps[0]+"//"+ps[1]+"//"+ps[2]+"//"+filename;
		 
		creatdict(ret);
		return ret;
	}
	
	public static void creatdict(String filename)
	{
		File file=new File(filename);
		creatdict(file);
	}
	
	public static void creatdict(File file)
	{
		File froot=file.getParentFile();
		if(!froot.exists())
		{froot.mkdirs();}
	}
	
 
	
	@SuppressWarnings("deprecation")
	private static String[] gettime()
	{
		Date date=new Date(System.currentTimeMillis());
		String pathnian= String.valueOf(1900+date.getYear());
		String pathri=String.valueOf(format2(date.getMonth())+format2(date.getDay())); 
		java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("hhmm");
		   String fen = String.valueOf(f.format(new java.util.Date()).subSequence(0, 3))+"0"; 
		String path10fen= String.valueOf(  fen);
		
		String[] s=new String[3];
		s[0]=pathnian; 	//System.out.println((pathnian));
		s[1]=pathri;	//System.out.println(pathri);
		s[2]=path10fen;	//System.out.println(path10fen);
		return s;
	}
	
	private static String getrootstr(String paramString)
	{  
	    if (  (paramString.endsWith("/")||paramString.endsWith("\\"))&&(!paramString.endsWith("//"))){
	    	paramString= new String(paramString.substring(0, paramString.length()-1).getBytes());
	    } 
	    else if (paramString.endsWith("//")) {
	    	paramString= new String(paramString.substring(0, paramString.length()-2).getBytes());
	    }  
		return paramString;
 
	}
	
	private static String format2(int i)
	{
		String ret=String.valueOf(i);
		if (ret.length()==1) 
		{ret=0+ret;}
		return ret;
	}

	public static void main(String s[])
	{
		//String s1="河南中烟科技研发管理系统项目周计划201308.xlsx"; 
		String s2="河南中烟科技研发管理系统项目周计划河南中烟科技研发管理系统项目周计划河南中烟科技研发管理系统项目周计划"; 
		
		
		System.out.println(Filepathencode(s2));
		System.out.println(s2.toCharArray().length);
	}
}

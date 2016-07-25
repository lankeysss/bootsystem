package com.casit.suwen;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

import com.casit.suwen.annotation.*;
import com.casit.suwen.datatool.DB3;



public class Scanner {
	static Logger log = Logger.getLogger(Scanner.class);
	private Set<Class<?>> getClasses(String pack) {
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		boolean recursive = true;
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					findAndAddClassesInPackageByFile(packageName, filePath,recursive, classes);
				} else if ("jar".equals(protocol)) {  
                    JarFile jar;  
                    try {  
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();  
                        Enumeration<JarEntry> entries = jar.entries();  
                        while (entries.hasMoreElements()) {  
                            JarEntry entry = entries.nextElement();  
                            String name = entry.getName();  
                            if (name.charAt(0) == '/') {  
                                name = name.substring(1);  
                            }  
                            if (name.startsWith(packageDirName)) {  
                                int idx = name.lastIndexOf('/');  
                                if (idx != -1) {  
                                    packageName = name.substring(0, idx).replace('/', '.');  
                                }  
                                if ((idx != -1) || recursive) {  
                                    if (name.endsWith(".class")&& !entry.isDirectory()) {  
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);  
                                        try {  
                                            classes.add(Class.forName(packageName + '.'  
                                                            + className));  
                                        } catch (ClassNotFoundException e) {  
                                            e.printStackTrace();  
                                        }  
                                    }  
                                }  
                            }  
                        }  
                    } catch (IOException e) {   
                        e.printStackTrace();  
                    }  
                }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classes;
	}
	private void findAndAddClassesInPackageByFile(String packageName,
			String packagePath, final boolean recursive, Set<Class<?>> classes) {
		File dir = new File(packagePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] dirfiles = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return (recursive && file.isDirectory())|| (file.getName().endsWith(".class"));
			}
		});
		for (File file : dirfiles) {
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(
						packageName + "." + file.getName(), file.getAbsolutePath(), recursive,classes);
			} else {
				String className = file.getName().substring(0,file.getName().length() - 6);
				try {
					Class<?> cl = Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className);
					if(cl.getAnnotation(Path.class)!=null)classes.add(cl);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	public void scan2PathTree(PathNode root){
		Set<Class<?>> set = this.getClasses(DB3.scannerpackage);
		Iterator<Class<?>> it = set.iterator();
		if (!DB3.usejavassist.equals("true")) 
		{log.error("【warning】donot hava JavaassitTool,now must use @Param to insert param");}
		while(it.hasNext()){
			Class<?> cl = it.next();
			String uri = "/";
			Path p = cl.getAnnotation(Path.class); 
			if(!p.value().equals(""))
			{uri += p.value();}
			else{uri +=cl.getSimpleName();}
			Map<String,String[]> map=new HashMap<String, String[]>();
			if (DB3.usejavassist.equals("true")) {
				try { 
					Class<?> clz=Class.forName("com.casit.suwen.JavaassitTool"); 
					Method[] ms2 = clz.getDeclaredMethods();
					for (int i = 0; i < ms2.length; i++) {       //System.out.println(ms2.length+ms2[i].getName());
						if (ms2[i].getName().equals("getfunctionparamnamesSTR")) {
							map=(Map<String,String[]> )ms2[i].invoke(clz.newInstance(), cl.getName());
						}
					} 
				} catch (Exception e) { 
					e.printStackTrace();
					if(!e.getMessage().equals("argument type mismatch"))
					{log.error(e.getMessage());}
				}
			} 
			Method[] ms = cl.getDeclaredMethods();
			for(int i=0;i<ms.length;i++) 
			{
				boolean isprivate = Modifier.isPrivate(ms[i].getModifiers());
				//非调试模式下不映射private方法。
				if(!log.isDebugEnabled()&&isprivate)continue;
				
				Listener ls=ms[i].getAnnotation(Listener.class);
				Post ps = ms[i].getAnnotation(Post.class);
				if(ps==null)continue;
				Action as = new Action();
				as.setDclass(cl);
				as.setMethod(ms[i]);
				Annotation[][] params = ms[i].getParameterAnnotations();
				if(params!=null)
				{
					for(int n=0;n<params.length;n++)
					{
						Annotation[] tem = params[n];
						if(tem.length!=0){
							Param pm = (Param)tem[0];
							as.addParam(pm.value());
						}else
						{
							as.addParam(null);
						}
					}	
				}				
				String[] paramjas=null;
				if (map.get(ms[i].getName())!=null) {
					paramjas=map.get(ms[i].getName()); 
				}else
				{
					if (params==null) {
						paramjas=null;
					}else
					{
						paramjas=new String[params.length];
					}	
				}
				if(paramjas!=null)
				{
					for (int j = 0; j < paramjas.length; j++) {
						as.addParamja(paramjas[j]);
					}
				}
				String psv="";
				if(!ps.value().equals(""))
				{psv=ps.value();}
				else{psv=ms[i].getName();}
				root.getOrCreateChild(uri+"/"+psv).setAction(as);
				if(log.isDebugEnabled()){
					if(isprivate){
						ms[i].setAccessible(true);
						log.debug(uri+"/"+psv+"  ->"+cl.getName()+":"+ms[i].getName()+" (debug only)");					
					}else{
						log.debug(uri+"/"+psv+"  ->"+cl.getName()+":"+ms[i].getName());		
					}	
					if (ls!=null) {
						Event.addListener(ls.value(), uri+ "/"+psv);
						log.debug( "[listener]  name:"+ls.value()+"  url:"+ uri+ "/"+psv);
					}
				}
				
			}
		}
		 
		if (!DB3.usejavassist.equals("true")) 
		{log.error("【warning】donot hava JavaassitTool,now must use @Param to insert param");}
	}
	
 
	
}

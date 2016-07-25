package com.casit.suwen.autom;
public class SQLFormat {
	public static String getLevels(int le){
		String tem = "";
		for(int i=0;i<le;i++){
			tem += "    ";
		}
		return tem;
	}
	public static StringBuffer format(String sql){
		if(sql.length()<120){
			return new StringBuffer(sql);
		}
		StringBuffer buf = new StringBuffer();
		Tokener tk = new Tokener(sql);
		int last = 0,now = 0,level = -1;
		Word word = tk.nextWord();
		String before = "";
		while(word!=null){
			String tem = word.getContent().toLowerCase();
			if(tem.equals("select")){
				now = word.getPosition()-tem.length();
				buf.append(getLevels(level)+sql.substring(last,now).trim()+(level>0?"\n":""));
				last = now;
				level += 1;
			}
			if((word.getPosition()-last)>100){
				if(tem.equals(",")||tem.equals("and")||tem.equals("||")){
					now = word.getPosition();
					buf.append(getLevels(level)+sql.substring(last,now).trim()+"\n");
					last = now;						
				}
				if(tem.equals("then")){
					now = word.getPosition()-tem.length();
					buf.append(getLevels(level)+sql.substring(last,now).trim()+"\n");
					last = now;											
				}
			}
			if(tem.equals("from")||tem.equals("where")||tem.equals("group")||
					tem.equals("order")||tem.equals("left")){
				now = word.getPosition()-tem.length();
				buf.append(getLevels(level)+sql.substring(last,now).trim()+"\n");
				last = now;
			}
			if(tem.equals("(")){				
				if(!before.equals("from")){
					Word ww = tk.nextWord();
					int tot = 1;
					while(ww!=null){
						String wd = ww.getContent();
						if(wd.startsWith(")")){
							tot -= 1;
							if(tot==0)break;
						}else if(wd.startsWith("(")){
							tot += 1;
						}
						ww = tk.nextWord();
					}
				}
			}
			if(tem.equals(")")){
				now = word.getPosition()-1;
				buf.append(getLevels(level)+sql.substring(last,now).trim()+"\n");
				last = now;
				level -= 1;
			}
			if(!tem.trim().equals(""))before = tem;
			word = tk.nextWord();				
		}
		buf.append(sql.substring(last));
		return buf;
	}
}

package com.casit.suwen.autom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.casit.suwen.security.PrivilegeFilter;

public class SQLEmbed {
	private Tokener tk;
	private PrivilegeFilter pf = new PrivilegeFilter();
	private Stack stack = new Stack();
	private StringBuffer fsql = new StringBuffer();
	/*
	 * 0.�﷨����
	 * 1.���Բ����κβ���
	 * 2.push��ջ
	 * 3.pop��ջ
	 * 4.popֱ����һ��(
	 * 5.������ݵ�buf
	 * 6.��buf��Ϊtable�����,�����
	 * 7.��word���λ�����ע��
	 * 8.��preword���λ�����ע��
	 * 9.�˳����
	 * */
	private static String[][] dfa = {
//		 		,0	 	�հ�1	select2 from3   where4 		group5  	as6 	(7		)8			left9		right10		outer11	join12	on13    	inner14		cross15		union16		order17		����18	null19
/*,0*/ 			{"0",	"1",	"0",	"0",	"0",		"0",		"0",	"1",	"0",		"0",		"0",		"0",	"0",	"0",		"0",		"0",		"1",		"1",		"3",	"9"},
/*�հ�1*/ 		{"0",	"1",	"0",	"0",	"0",		"0",		"0",	"1",	"0",		"0",		"0",		"0",	"0",	"0",		"0",		"0",		"1",		"1",		"3",	"9"},
/*select2*/		{"1",	"1",	"0",	"2",	"0",		"0",		"1",	"1",	"1",		"0",		"0",		"0",	"0",	"0",		"0",		"0",		"1",		"1",		"1",	"9"},
/*from3*/		{"6",	"5",	"0",	"0",	"6,7,3,2",	"6,8,3,2",	"5",	"2",	"8,6,4",	"6,3,2",	"6,3,2",	"0",	"0",	"0",		"6,3,2",	"6,3,2",	"3,2",		"6,8,3,2",	"5",	"9,6,8"},
/*where4*/		{"1",	"1",	"1",	"1",	"1",		"1",		"1",	"2",	"4",		"1",		"1",		"1",	"1",	"1",		"1",		"1",		"3,2",		"1",		"1",	"9"},
/*group5*/		{"1",	"1",	"1",	"1",	"1",		"1",		"1",	"1",	"4",		"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",		"1",		"1",	"9"},
/*as6*/			{"1",	"1",	"1",	"1",	"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",		"1",		"1",	"9"},
/*(7*/			{"1",	"1",	"2",	"0",	"1",		"1",		"1",	"1",	"3",		"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",		"1",		"1",	"9"},
/*)8*/			{"1",	"1",	"1",	"1",	"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",		"1",		"1",	"9"},
/*left9*/		{"0",	"1",	"0",	"0",	"0",		"0",		"0",	"0",	"0",		"0",		"0",		"3,2",	"3,2",	"0",		"0",		"0",		"0",		"1",		"0",	"9"},
/*right10*/		{"1",	"1",	"1",	"1",	"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",		"1",		"1",	"9"},
/*outer11*/		{"0",	"1",	"0",	"0",	"0",		"0",		"0",	"0",	"0",		"0",		"0",		"0",	"2",	"0",		"0",		"0",		"0",		"1",		"0",	"9"},
/*join12*/		{"0",	"5",	"0",	"0",	"0",		"0",		"0",	"0",	"0",		"0",		"0",		"0",	"0",	"6,3,2",	"0",		"0",		"0",		"1",		"5",	"9"},
/*on13*/		{"1",	"1",	"0",	"0",	"7,3,2",	"8",		"0",	"0",	"4",		"3,2",		"3,2",		"0",	"0",	"0",		"3,2",		"3,2",		"8,3,2",	"8,3,2",	"1",	"9,8"},
/*inner14*/		{"1",	"1",	"1",	"1",	"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",	"3,2",	"1",		"1",		"1",		"1",		"1",		"1",	"9"},
/*cross15*/		{"1",	"1",	"1",	"1",	"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",	"3,2",	"1",		"1",		"1",		"1",		"1",		"1",	"9"},
/*union16*/		{"1",	"1",	"3,2",	"1",	"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",		"1",		"1",	"9"},
/*order17*/		{"1",	"1",	"1",	"1",	"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",		"1",		"1",	"9"},
/*����18*/		{"1",	"1",	"1",	"1",	"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",		"1",		"1",	"9"},
/*null19*/		{"1",	"1",	"1",	"1",	"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",	"1",	"1",		"1",		"1",		"1",		"1",		"1",	"9"},
/*��ʼ״̬20*/	{"0",	"1",	"3,2",	"0",	"0",		"0",		"0",	"0",	"0",		"0",		"0",		"0",	"0",	"0",		"0",		"0",		"0",		"0",		"0",	"9"}
	};
	private static Pattern pmain = Pattern.compile("\\$\\{(.*?)\\}");
	public SQLEmbed(String sql,PrivilegeFilter pf){
		this.tk = new Tokener(sql);
		this.pf = pf;
		stack.push(20);
	}
	private String template(){
		Matcher mmain = pmain.matcher(fsql);
	    StringBuffer mbuf = new StringBuffer();
		while(mmain.find()) {
			String s = mmain.group(1);
			int group = Integer.valueOf(s.replace("where", ""));
			String append = pf.getFilterSQL(group);
			System.out.println(group + ":" +append);              
			if(!append.trim().equals("")){
				if(s.startsWith("where")) append = " where "+append;
				else append += " and ";			
			}
			mmain.appendReplacement(mbuf, append);
		}
		mmain.appendTail(mbuf);
		return mbuf.toString();
	}
	public String embed(){
		Word word = tk.nextWord();
		StringBuffer buf = new StringBuffer();
		while(true){
			int type = this.getWordType(word);
			int state = stack.peek();
			String dealwith = dfa[state][type];
			
//System.out.println("----------("+(word==null?"null":word.getContent())+")"+stack+":"+type+"->"+dealwith);	
			
			String subapp = "";
			boolean quit = false;
			String[] d = dealwith.split(",");
			for(int i=0;i<d.length;i++)
			{
				if(d[i].equals("0")){
					System.out.println("����sql���﷨����");
				}else if(d[i].equals("1")){
					//���ԣ������κβ���
				}else if(d[i].equals("2")){
					stack.push(type);
				}else if(d[i].equals("3")){
					stack.pop();
				}else if(d[i].equals("4")){
					int s = stack.pop();
					while(s!=7&&s!=-1)s = stack.pop();
				}else if(d[i].equals("5")){
					buf.append(word.getContent());
				}else if(d[i].equals("6")){
					pf.addTable(stack.getLevels(), buf.toString());
					buf = new StringBuffer();
				}else if(d[i].equals("7")){
					subapp = " ${"+stack.getLevels()+"} ";
				}else if(d[i].equals("8")){
					 fsql.append(" ${where"+stack.getLevels()+"} ");
				}else if(d[i].equals("9")){
					quit = true;
				}else{
					System.out.println("�Ҳ�����Ӧ�Ĳ���"+d[i]);
				}			
			}
			if(quit)break;
			fsql.append(word.getContent()+subapp);			
			word = tk.nextWord();
		}
		return template();
	}
	public int getWordType(Word word){
		if(word==null) return 19;
		String w = word.getContent().toLowerCase();
		if(w.trim().equals(""))return 1;
		if(w.equals(",")){
			return 0;
		}else if(w.equals("select")){
			return 2;
		}else if(w.equals("from")){
			return 3;
		}else if(w.equals("where")){
			return 4;
		}else if(w.equals("group")){
			return 5;
		}else if(w.equals("as")){
			return 6;
		}else if(w.equals("(")){
			return 7;
		}else if(w.equals(")")){
			return 8;
		}else if(w.equals("left")){
			return 9;
		}else if(w.equals("right")){
			return 10;
		}else if(w.equals("outer")){
			return 11;
		}else if(w.equals("join")){
			return 12;
		}else if(w.equals("on")){
			return 13;
		}else if(w.equals("inner")){
			return 14;
		}else if(w.equals("cross")){
			return 15;
		}else if(w.equals("union")){
			return 16;
		}else if(w.equals("order")){
			return 17;
		}
		return 18;
	}
}

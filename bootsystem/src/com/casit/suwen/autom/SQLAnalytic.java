package com.casit.suwen.autom;


public class SQLAnalytic {
	//private String sql;
	private Tokener tk;
	private Stack stack = new Stack();
	private int[][] dfa = {
	//1:忽略不作任何操作
	//2:弹出堆栈顶部元素，将当前状态压入堆栈
	//3:将字符串信息写入缓存
	//4:打印输出字符串缓存，并清空字符串缓存
	//5:弹出堆栈顶端元素
	//6:打印并输出字符串缓存，清空字符串缓存，同时将当前状态压入堆栈
	//7:弹出堆栈顶部元素，直到（为止。
	//				 逗号0，	select1，from2，	where3，	and4，	left5，	right6，	outer7，	join8， 	on9，	order10，group11，by12，	as13，	union14，(15，	)16，	其它17
	/*逗号*/			{1,		2,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		7,		1},
	/*select*/		{1,		1,		2,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		8,		7,		1},
	/*from*/		{4,		1,		1,		6,		1,		6,		6,		1,		1,		1,		6,		6,		1,		1,		6,		8,		7,		3},
	/*where*/		{1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		2,		8,		7,		1},
	/*and*/			{1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		8,		7,		1},
	/*left*/		{1,		1,		1,		1,		1,		1,		1,		2,		2,		1,		1,		1,		1,		1,		1,		8,		7,		1},
	/*right*/		{1,		1,		1,		1,		1,		1,		1,		2,		2,		1,		1,		1,		1,		1,		1,		8,		7,		1},
	/*outer*/		{1,		1,		1,		1,		1,		1,		1,		1,		2,		1,		1,		1,		1,		1,		1,		8,		7,		1},
	/*join*/		{1,		1,		1,		1,		1,		1,		1,		1,		2,		6,		1,		1,		1,		1,		1,		8,		7,		3},
	/*on*/			{1,		1,		1,		1,		1,		6,		6,		1,		2,		1,		2,		2,		1,		1,		2,		8,		7,		1},
	/*order*/		{1,		1,		1,		1,		1,		1,		1,		1,		2,		1,		1,		1,		1,		1,		1,		8,		7,		1},
	/*group*/		{1,		1,		1,		1,		1,		1,		1,		1,		2,		1,		1,		1,		1,		1,		1,		8,		7,		1},
	/*by*/			{1,		1,		1,		1,		1,		1,		1,		1,		2,		1,		1,		1,		1,		1,		1,		8,		7,		1},
	/*as*/			{1,		1,		1,		1,		1,		1,		1,		1,		2,		1,		1,		1,		1,		1,		1,		8,		7,		1},
	/*union*/		{1,		2,		1,		1,		1,		1,		1,		1,		2,		1,		1,		1,		1,		1,		1,		8,		7,		1},
	/*(*/			{1,		8,		1,		1,		1,		1,		1,		1,		2,		1,		1,		1,		1,		1,		1,		8,		7,		1},
	/*)*/			{1,		2,		1,		1,		1,		1,		1,		1,		2,		1,		1,		1,		1,		1,		1,		8,		7,		1},
	/*其它*/			{1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		1,		8,		7,		1}			
	};
	public SQLAnalytic(String sql){
		//this.sql = sql;
		this.tk = new Tokener(sql);
	}
	public int getWordType(String word){
		String w = word.toLowerCase();
		if(w.equals(",")){
			return 0;
		}else if(w.equals("select")){
			return 1;
		}else if(w.equals("from")){
			return 2;
		}else if(w.equals("where")){
			return 3;
		}else if(w.equals("and")){
			return 4;
		}else if(w.equals("left")){
			return 5;
		}else if(w.equals("right")){
			return 6;
		}else if(w.equals("outer")){
			return 7;
		}else if(w.equals("join")){
			return 8;
		}else if(w.equals("on")){
			return 9;
		}else if(w.equals("order")){
			return 10;
		}else if(w.equals("group")){
			return 11;
		}else if(w.equals("by")){
			return 12;
		}else if(w.equals("as")){
			return 13;
		}else if(w.equals("union")){
			return 14;
		}else if(w.equals("(")){
			return 15;
		}else if(w.equals(")")){
			return 16;
		}else{
			return 17;
		}
	}
	public void Parser(){
		String word = tk.nextWord().getContent();
		StringBuffer buf = new StringBuffer();
		stack.push(0);
		while(word!=null){
			int type = this.getWordType(word);
			int state = stack.peek();
			int dealwith = dfa[state][type];
			System.out.println("------------------------------------"+word+","+stack+"["+state+","+type+"]:"+dealwith);
			if(dealwith==2){
				stack.pop();
				stack.push(type);
			}
			if(dealwith==3){
				buf.append(word);
			}
			if(dealwith==4){
				System.out.println(buf);
				buf = new StringBuffer();
			}
			if(dealwith==5){
				stack.pop();
			}
			if(dealwith==6){
				stack.push(type);
				System.out.println(buf);
				buf = new StringBuffer();
			}
			if(dealwith==7){
				int pop = stack.pop();
				while(!stack.isEmpty()&&pop!=15){
					pop = stack.pop();
				}
			}
			if(dealwith==8){
				stack.push(type);
			}
			word = tk.nextWord().getContent();
		}
		if(!buf.equals("")){
			System.out.println(buf);
		}
	}
}

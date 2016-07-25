package com.casit.suwen.autom;

public class AutoM {

	private int[] trans;
	private int[] acts;
	private int now = 0;
	public AutoM(int[] t,int[] a){
		this.trans = t;
		this.acts = a;
	}
	public void reset(){
		now = 0;
	}
	public boolean next(int ch){
		for(int i=acts[now];i<acts[now+1];i=i+2){
			if(trans[i]<0){
				if(ch!=(-1*trans[i])){
					now = trans[i+1];
					return true;
				}
				
			}else{
				if(trans[i]==ch){
					now = trans[i+1];
					return true;
				}				
			}
		}
		return false;
	}
	
	public int getState(){
		
		
		return now;
	}
	public void setState(int st){
		this.now = st;
	}
}

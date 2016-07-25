package com.casit.suwen;

public class ForwardException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 854108841583220511L;
	public String forward="";
	
	public ForwardException()
	{
		this("ForwardException--get forward ");
	}
	
	public ForwardException(String forward)
	{
		
		super("ForwardException--get forward to :"+forward);
		this.forward=forward;
	} 
 
}

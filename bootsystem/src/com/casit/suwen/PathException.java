package com.casit.suwen;

public class PathException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4087118225810455502L;

	public PathException() 
	{
		this("PathException--has no class to this path");
	} 

	public PathException(String message) {
        super(message);
	}
}

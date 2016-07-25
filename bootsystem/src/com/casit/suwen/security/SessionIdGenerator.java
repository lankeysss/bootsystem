package com.casit.suwen.security;

import java.util.Random;

public class SessionIdGenerator {
	private static String characters = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static int length = characters.length();
	private static Random rdom = new Random();
	public static String newSessionId(){
		char[] text = new char[32];
	    for (int i = 0; i < 32; i++)
	    {
	        text[i] = characters.charAt(rdom.nextInt(length));
	    }
	    return new String(text);
	}
}

package com.casit.suwen.security;

import java.math.BigInteger;

import sun.misc.BASE64Decoder;

public class BASE {
 private static BigInteger key=new BigInteger("719");
 public static String encode(byte[] b) {
  String s = null;
  if (b != null) {
	  BigInteger bi=new BigInteger(b);
	  bi=bi.multiply(key); 
	  s = new sun.misc.BASE64Encoder().encode(bi.toByteArray());
  }
  return s.replace("/", "$").replace("\r\n", "").replace("\r", "").replace("\n", "");  
 }
 public static String encode(String  str) {
	  String s = null;
	  byte[] b=str.getBytes();
	  if (b != null) {
		  BigInteger bi=new BigInteger(b);
		  bi=bi.multiply(key);
		  s = new sun.misc.BASE64Encoder().encode(bi.toByteArray());
	  }
	  return s.replace("/", "$").replace("\r\n", "").replace("\r", "").replace("\n", "");
	 }
 
 public static String decode(String s) {
  byte[] b = null;
  if (s != null) {
   BASE64Decoder decoder = new BASE64Decoder();
   try {
    b = decoder.decodeBuffer(s.replace( "$","/"));
	BigInteger bi=new BigInteger(b);
	bi=bi.divide(key); 
    return  new String(bi.toByteArray());
   } catch (Exception e) {
    e.printStackTrace();
   }
  }
  return null;
 }
 
 
 
 
 public static void main(String[] args)  {
     String s = "请注册时候采用 真实姓名、真实邮件,请注册时候采用 真实姓名、真实邮件,请注册时候采用 真实姓名、真实邮件,请注册时候采用 真实姓名、真实邮件,请注册时候采用 真实姓名、真实邮件"; 
     System.out.println("加密前：" + s);
     String x = encode(s);
     System.out.println("加密后：" + x);
     String x1 = new String(decode(x));
     System.out.println("解密后：" + x1); 
     
 }

 
}



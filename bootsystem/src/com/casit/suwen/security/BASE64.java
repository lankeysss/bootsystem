package com.casit.suwen.security;
 
import sun.misc.BASE64Decoder;

public class BASE64 { 
 public static String encode(byte[] b) {
  String s = null;
  if (b != null) { 
	  s = new sun.misc.BASE64Encoder().encode(b );
  }
  return s;
 }
 public static String encode(String  str) {
	  String s = null;
	  byte[] b=str.getBytes();
	  if (b != null) { 
		  s = new sun.misc.BASE64Encoder().encode(b );
	  }
	  return s;
	 }
 
 public static String decode(String s) {
  byte[] b = null;
  if (s != null) {
   BASE64Decoder decoder = new BASE64Decoder();
   try {
    b = decoder.decodeBuffer(s); 
    return  new String(b );
   } catch (Exception e) {
    e.printStackTrace();
   }
  }
  return null;
 }
 
 
 
 
 public static void main(String[] args)  {
    // String s = "abcd"; 
    // System.out.println("加密前：" + s);
     //String x = encode(s);
   //  System.out.println("加密后：" + x);
     String x1 = new String(decode("ARGDVSXc"));
     System.out.println("解密后：" + x1); 
     
 }

 
}



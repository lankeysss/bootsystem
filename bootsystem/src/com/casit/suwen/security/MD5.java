package com.casit.suwen.security;

import java.security.MessageDigest; 

public class MD5 {
	
	
	public final static String encode(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

	public final static String encode(String s1,String s2) {
        byte[] md1=encode(s1).getBytes();
        byte[] md2=encode(s2).getBytes();
        byte[] md3=new byte[md1.length+md2.length];
        int j=0;
        for (int i = 0; i < md1.length; i++) {
			md3[j]=md1[i];j++;
			md3[j]=md2[i];j++;
		}
        
        return new String (md3);
    }
	public final static String encoderandom(String s1) {
		
        byte[] md1=encode(s1).getBytes();
        byte[] md2=encode(String.valueOf(Math.random() )).getBytes();
        byte[] md3=new byte[md1.length+md2.length];
        int j=0;
        for (int i = 0; i < md1.length; i++) {
			md3[j]=md1[i];j++;
			md3[j]=md2[i];j++;
		}
        
        return new String (md3);
    }
}

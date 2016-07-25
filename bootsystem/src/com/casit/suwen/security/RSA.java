package com.casit.suwen.security;

import java.security.*;

public class RSA {
	/*
	 * Java 加密体系结构包含了 JDK 1.2 安全包中与密码有关的类，包括引擎类。API 用户可通过请求和利用引擎类的实例来执行相应的运算。JDK
	 * 1.2 中定义了以下引擎类：
	 * 
	 * MessageDigest — 用于计算数据的报文摘要（散列）。 Signature — 用于对数据进行签名和校验数字签名。
	 * KeyPairGenerator — 用于生成与指定算法相匹配的公钥和私钥对。 KeyFactory — 用于将类型为
	 * Key（密钥）的不透明密钥转换为密钥规范（密钥信息的透明表示），反之亦然。 CertificateFactory
	 * 用于创建公钥证书和证书撤消清单（CRL）。 KeyStore —
	 * 用于创建和管理密钥仓库。密钥仓库是密钥的数据库。密钥仓库中的私钥有一个与之关联的证书链，用于认证对应的公钥。密钥仓库还含有来自可信实体的证书。
	 * AlgorithmParameters — 用于管理某一特定算法的参数，包括编码和解码参数。
	 * AlgorithmParameterGenerator — 用于生成一组与指定算法相匹配的参数。 SecureRandom —
	 * 用于生成随机数或伪随机数。
	 */
	public static void main(String[] args) {
		String msg = "向";
		System.out.println("源文是：" + msg);
		byte[] msgBytes = msg.getBytes();
		for (byte b : msgBytes) {
			System.out.println(b);
		}
		// 形成RSA密钥对
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024);

			// 生成公钥和私钥
			KeyPair key = keyGen.generateKeyPair();
			System.out.println("生成公钥和私钥:" + key);
			// 实例化Signature，用于产生数字签名，指定用RSA和SHA算法
			Signature sig = Signature.getInstance("SHA1WithRSA");
			System.out.println("实例化Signature得到:" + sig);
			// 得到私钥
			PrivateKey privateKey = key.getPrivate();
			System.out.println("得到私钥是：" + privateKey);

			// 用私钥来签名初始化对象
			sig.initSign(privateKey);
			// System.out.println();
			// 对msgBytes实施签名
			sig.update(msgBytes);

			// 完成签名，将结果放入字节数组signatureBytes

			byte[] signatureBytes = sig.sign();
			String signature = new String(signatureBytes);
			System.out.println("签名是：" + signature);
			// 使用公密验证
			PublicKey publickey = key.getPublic();
			System.out.println("公密验证是" + publickey);
			sig.initVerify(publickey);// certificat

			// 对msgBytes实施重新部署
			sig.update(msgBytes);
			System.out.println("sig is :" + sig);
			System.out.println("signatureBytes is :" + signatureBytes);
			try {
				if (sig.equals(signatureBytes)) {
					System.out.println("签名验证成功");
				} else {
					System.out.println("签名验证失败");

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

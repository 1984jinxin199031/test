/**
 * 
 */
package com.boot.util;




/**
 * @author JX 
 * 【RSA】加密工具
 * 2017-06-22 17:35:22【PHP】用 
 * 2016年11月30日 下午5:13:21
 */
public class EncriptionKitPhp {

	/**
	 * RSA解密
	 * password解密
	 * @param publicKey
	 * @param password
	 * @return
	 * 2016年11月30日 下午5:56:44
	 */
	public static String passwordDecrypt(String publicKey, String password) {
		String result = null;
		try {
			result = ConfigTools.decrypt(publicKey, password);// 解密
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * RSA加密
	 * password解密
	 * @param publicKey
	 * @param password
	 * @return
	 * 2016年11月30日 下午5:56:44
	 */
	public static String passwordEncrypt(String publicKey, String password) {
		String result = null;
		try {
			result = ConfigTools.encrypt(publicKey, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		
	}
	
}

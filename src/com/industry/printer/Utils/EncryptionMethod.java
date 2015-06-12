package com.industry.printer.Utils;

public class EncryptionMethod {

	public static EncryptionMethod mInstance = null;
	
	public static EncryptionMethod getInstance() {
		if (mInstance == null) {
			mInstance = new EncryptionMethod();
		}
		return mInstance;
	}
	public EncryptionMethod() {
		
	}
	
	/**
	 * 通过序列号获取密钥A
	 * @param sn
	 * @return
	 */
	public byte[] getKeyA(byte[] sn) {
		if (sn == null || sn.length == 0) {
			return null;
		}
		byte[] key = new byte[sn.length];
		
		for (int i = 0; i < sn.length; i++) {
			key[i] = (byte) ~(sn[i]);
		}
		for (int i = 0; i < key.length/2; i++) {
			byte k = key[i];
			key[i] = key[key.length - i];
			key[key.length - i] = k;
		}
		return key;
	}
	
	/**
	 * 通过序列号获取密钥B
	 * @param sn
	 * @return
	 */
	public byte[] getKeyB(byte[] sn) {
		if (sn == null || sn.length == 0) {
			return null;
		}
		byte[] key = new byte[sn.length];
		for (int i = 0; i < sn.length; i++) {
			key[i] = (byte) ~(sn[i]);
		}
		for (int i = 0; i < key.length; i++) {
			key[i] = (byte) ((key[i]<<4 & 0x0f0) & (key[i] >> 4 & 0x0f));
		}
		return key;
	}
	
}

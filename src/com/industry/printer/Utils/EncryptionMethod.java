package com.industry.printer.Utils;

import com.industry.printer.hardware.RFIDDevice;

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
		if (sn == null || sn.length != 4) {
			return null;
		}
		byte[] key = new byte[6];
		
		key[0] = (byte) (~(sn[0]) & 0x0ff );
		key[1] = (byte) (~(sn[1]) & 0x0ff );
		key[3] = (byte) (~(sn[2]) & 0x0ff );
		key[4] = (byte) (~(sn[3]) & 0x0ff );
		key[2] = (byte) ((key[0] ^ key[1] ^ key[3] ^ key[4]) & 0x0ff );
		// 计算校验和
		for (int i = 0; i < key.length-1; i++) {
			key[5] += (key[i] & 0x0ff);
		}
		byte tmp = 0;
		// 校验和要经过高低位倒序
		for (int i = 0; i < 8; i++) {
			if ((key[5] >> i & 0x01) == 0x01) {
				tmp |= 0x01 << (8-i);
			}
		}
		key[5] = (byte) (tmp & 0x0ff);
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
	
	
	/**
	 * 解密得到真实的墨水值
	 * 暂时将墨水值放在byte0（高）和byte1（低）
	 */
	public int decryptInkLevel(byte[] level) {
		if (level == null || level.length < 2) {
			return 0;
		}
		return ((level[0] & 0x0ff) * 256 + (level[1] & 0x0ff));
		
	}
	
	public byte[] encryptInkLevel(int level) {
		if (level < RFIDDevice.INK_LEVEL_MIN || level > RFIDDevice.INK_LEVEL_MAX) {
			return null;
		}
		byte[] ink = new byte[16];
		ink[0] = (byte) ((level >> 8) & 0x0ff); 
		ink[1] = (byte) (level & 0x0ff);
		return ink;
	}
}

package com.industry.printer.hardware;

import java.io.Closeable;
import java.nio.ByteBuffer;

import org.apache.http.util.ByteArrayBuffer;

import com.industry.printer.Utils.Debug;
import com.industry.printer.data.RFIDData;

public class RFIDOperation {

	//RFID操作 native接口
	public static native int open(String dev);
	public static native int close(int fd);
	public static native int write(int fd, short[] buf, int len);
	public static native byte[] read(int fd, int len);
	

	/************************
	 * RFID命令操作部分
	 ***********************/
	public static final String TAG = RFIDOperation.class.getSimpleName();
	
	public static RFIDOperation mRfidOperation;
	//串口节点
	public static final String SERIAL_INTERFACE = "/dev/ttyS3";
	//Command
	public static byte RFID_CMD_CONNECT = 0x15;
	public static byte RFID_CMD_TYPEA = 0x3A;
	public static byte RFID_CMD_SEARCHCARD = 0x46;
	public static byte RFID_CMD_MIFARE_CONFLICT_PREVENTION = 0x47;
	public static byte RFID_CMD_MIFARE_CARD_SELECT = 0x48;
	public static byte RFID_CMD_MIFARE_KEY_VERIFICATION = 0x4A;
	public static byte RFID_CMD_MIFARE_READ_BLOCK = 0x4B;
	public static byte RFID_CMD_MIFARE_WRITE_BLOCK = 0x4C;
	public static byte RFID_CMD_MIFARE_WALLET_INIT = 0x4D;
	public static byte RFID_CMD_MIFARE_WALLET_READ = 0x4E;
	public static byte RFID_CMD_MIFARE_WALLET_CHARGE = 0x50;
	public static byte RFID_CMD_MIFARE_WALLET_DEBIT = 0x4F;
	
	//Data
	public static byte[] RFID_DATA_CONNECT = {0x03};
	public static byte[] RFID_DATA_TYPEA = {0x41};
	public static byte[] RFID_DATA_SEARCHCARD_WAKE = {0x26};
	public static byte[] RFID_DATA_SEARCHCARD_ALL = {0x52};
	public static byte[] RFID_DATA_MIFARE_CONFLICT_PREVENTION = {0x04};
	public static byte[] RFID_DATA_MIFARE_KEY_A = {0x60,0x00, 0x00,0x00,0x00,0x00,0x00,0x00};
	public static byte[] RFID_DATA_MIFARE_KEY_B = {0x61,0x00, 0x00,0x00,0x00,0x00,0x00,0x00};
	//返回值
	public static byte	RFID_RESULT_OK = 0x00;
	public static byte[] RFID_RESULT_MIFARE_S50 = {0X04, 0X00};
	public static byte[] RFID_RESULT_MIFARE_S70 = {0X02, 0X00};
	public static byte[] RFID_RESULT_UTRALIGHT = {0X44, 0X00};
	
	//密钥
	public static byte[] RFID_KEY_A = {(byte) 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00};
	public static byte[] RFID_KEY_B = {0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF};
	
	public static RFIDOperation getInstance() {
		if (mRfidOperation == null) {
			mRfidOperation = new RFIDOperation();
		}
		return mRfidOperation;
	}
	/*
	 * 端口连接
	 */
	public boolean connect() {
		RFIDData data = new RFIDData(RFID_CMD_CONNECT, RFID_DATA_CONNECT);
		byte[] readin = writeCmd(data);
		return isCorrect(readin);
			
	}
	/*
	 * 设置读卡器工作模式
	 */
	public boolean setType() {
		RFIDData data = new RFIDData(RFID_CMD_TYPEA, RFID_DATA_TYPEA);
		byte[] readin = writeCmd(data);
		return isCorrect(readin);
	}
	/*
	 * 寻卡
	 */
	public boolean lookForCards() {
		RFIDData data = new RFIDData(RFID_CMD_SEARCHCARD, RFID_DATA_SEARCHCARD_WAKE);
		byte[] readin = writeCmd(data);
		if (readin == null) {
			return false;
		}
		RFIDData rfidData = new RFIDData(readin, true);
		byte[] rfid = rfidData.getData();
		if (rfid == null || rfid[0] != 0 || rfid.length < 3) {
			Debug.d(TAG, "===>rfid data error");
			return false;
		}
		if (rfid[1] == 0x04 && rfid[2] == 0x00) {
			Debug.d(TAG, "===>rfid type S50");
			return true;
		} else if (rfid[1] == 0x02 && rfid[2] == 0x00) {
			Debug.d(TAG, "===>rfid type S70");
			return true;
		} else if (rfid[1] == 0x44 && rfid[2] == 0x00) {
			Debug.d(TAG, "===>rfid type utralight");
			return true;
		} else {
			Debug.d(TAG, "===>unknow rfid type");
			return false;
		}
	}
	/*
	 * 防冲突
	 */
	public byte[] avoidConflict() {
		RFIDData data = new RFIDData(RFID_CMD_MIFARE_CONFLICT_PREVENTION, RFID_DATA_MIFARE_CONFLICT_PREVENTION);
		byte[] readin = writeCmd(data);
		RFIDData rfidData = new RFIDData(readin, true);
		byte[] rfid = rfidData.getData();
		Debug.print(rfid);
		if (rfid == null || rfid[0] != 0 || rfid.length != 5) {
			Debug.d(TAG, "===>rfid data error");
			return null;
		}
		ByteBuffer buffer = ByteBuffer.wrap(rfid);
		buffer.position(1);
		byte[] serialNo = new byte[4]; 
		buffer.get(serialNo, 0, serialNo.length);
		Debug.print(serialNo);
		return serialNo;
	}
	/*
	 * 选卡
	 */ 
	public boolean selectCard(byte[] cardNo) {
		if (cardNo == null || cardNo.length != 4) {
			Debug.e(TAG, "===>select card No is null");
			return false;
		}
		RFIDData data = new RFIDData(RFID_CMD_MIFARE_CARD_SELECT, cardNo);
		byte[] readin = writeCmd(data);
		return isCorrect(readin);
	}
	
	/**
	 * 密钥验证
	 * @param data
	 * @return
	 */
	public boolean keyVerfication(byte block) {
		byte[] keyA = {0x60,block, (byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff,(byte) 0xff};
		RFIDData data = new RFIDData(RFID_CMD_MIFARE_KEY_VERIFICATION, keyA);
		byte[] readin = writeCmd(data);
		return isCorrect(readin);
	}
	
	/**
	 * Mifare one 卡写块
	 * @param block 1字节绝对块号
	 * @param content 16字节内容
	 * @return true 成功， false 失败
	 */
	public boolean writeBlock(byte block, byte[] content) {
		if (block <0 || block > 0x3f || content == null || content.length != 16) {
			Debug.d(TAG, "block no large than 0x3f");
		}
		ByteArrayBuffer buffer = new ByteArrayBuffer(0);
		buffer.append(block);
		buffer.append(content, 0, content.length);
		RFIDData data = new RFIDData(RFID_CMD_MIFARE_WRITE_BLOCK, buffer.toByteArray());
		byte[] readin = writeCmd(data);
		return isCorrect(readin);
		
	}
	
	/**
	 * Mifare one 卡读块
	 * @param block 1字节绝对块号
	 * @return 16字节内容
	 */
	public byte[] readBlock(byte block) {
		byte[] b = {block};
		RFIDData data = new RFIDData(RFID_CMD_MIFARE_READ_BLOCK, b);
		byte[] readin = writeCmd(data);
		if (!isCorrect(readin)) {
			return null;
		}
		RFIDData rfidData = new RFIDData(readin, true);
		byte[] blockData = rfidData.getData();
		return blockData;
	}
	/*
	 * write command to RFID model
	 */
	private byte[] writeCmd(RFIDData data) {
		int fp = open(SERIAL_INTERFACE);
		if (fp <= 0) {
			return null;
		}
		Debug.d(TAG, "************write begin******************");
		for (int i = 0; i < data.mTransData.length; i++) {
			System.out.print("0x"+Integer.toHexString(data.mTransData[i])+" ");
			Debug.d(TAG, "0x"+Integer.toHexString(data.mTransData[i]));
		}
		Debug.d(TAG, "************write end******************");
		int writed = write(fp, data.transferData(), data.getLength());
		if (writed <= 0) {
			close(fp);
			Debug.d(TAG, "===>write err, return");
			return null;
		}
		byte[] readin = read(fp, 64);
		if (readin == null || readin.length == 0) {
			close(fp);
			Debug.d(TAG, "===>read err");
			return null;
		}
		Debug.d(TAG, "************read begin******************");
		for (int i = 0; i < readin.length; i++) {
			System.out.print("0x"+Integer.toHexString(readin[i])+" ");
			Debug.d(TAG,"0x"+Integer.toHexString(readin[i]));
		}
		Debug.d(TAG, "************read end******************");
		close(fp);
		return readin;
	}
	
	private boolean isCorrect(byte[] value) {
		
		if (value == null || value.length == 0) {
			return false;
		}
		RFIDData rfidData = new RFIDData(value, true);
		byte[] rfid = rfidData.getData();
		if (rfid == null || rfid[0] != 0) {
			Debug.d(TAG, "===>rfid data error");
			return false;
		}
		return true;
	}
	
	public class RFIDCardType {
		public static final int TYPE_S50 = 0;
		public static final int TYPE_S70 = 1;
		public static final int TYPE_UTRALIGHT = 2;
	}
}

package com.industry.printer.hardware;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.IllegalFormatCodePointException;

import org.apache.http.util.ByteArrayBuffer;

import android.R.integer;

import com.industry.printer.Utils.Debug;
import com.industry.printer.Utils.EncryptionMethod;
import com.industry.printer.data.RFIDData;

public class RFIDDevice {

	//RFID操作 native接口
	public static native int open(String dev);
	public static native int close(int fd);
	public static native int write(int fd, short[] buf, int len);
	public static native byte[] read(int fd, int len);
	

	/************************
	 * RFID命令操作部分
	 ***********************/
	public static final String TAG = RFIDDevice.class.getSimpleName();
	
	public static RFIDDevice mRfidDevice;
	//串口节点
	public static final String SERIAL_INTERFACE = "/dev/ttyS3";
	
	/*墨水量上下限*/
	public static final int INK_LEVEL_MAX = 1000;
	public static final int INK_LEVEL_MIN = 0;
	
	//block definition
	public static byte SECTOR_FEATURE = 0x04;
	public static byte BLOCK_FEATURE = 0x01;
	public static byte BLOCK_KEY = 0x03;
	
	public static byte SECTOR_COPY_FEATURE = 0x05;
	public static byte BLOCK_COPY_FEATURE = 0x01;
	
	public static byte SECTOR_INKLEVEL = 0x04;
	public static byte BLOCK_INKLEVEL = 0x02;
	public static byte SECTOR_COPY_INKLEVEL = 0x05;
	public static byte BLOCK_COPY_INKLEVEL = 0x02;
	
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
	
	//默认密钥
	public static byte[] RFID_DEFAULT_KEY_A = { (byte) 0x0ff, (byte) 0x0ff, (byte) 0x0ff, (byte) 0x0ff, (byte) 0x0ff, (byte) 0x0ff};
	public static byte[] RFID_DEFAULT_KEY_B = { (byte) 0x0ff, (byte) 0x0ff, (byte) 0x0ff, (byte) 0x0ff, (byte) 0x0ff, (byte) 0x0FF};
	
	//计算得到的密钥
	public byte[] mRFIDKeyA = null;
	public byte[] mRFIDKeyB = null;
	
	
	// 错误码定义
	public static final int RFID_ERRNO_NOERROR = 0;
	public static final int RFID_ERRNO_NOCARD = 1;
	public static final int RFID_ERRNO_SERIALNO_UNAVILABLE = 2;
	public static final int RFID_ERRNO_SELECT_FAIL = 3;
	public static final int RFID_ERRNO_KEYVERIFICATION_FAIL = 4;
	
	//串口
	public static int mFd=0;
	
	public static RFIDDevice getInstance() {
		if (mRfidDevice == null) {
			mRfidDevice = new RFIDDevice();
		}
		if (mFd <= 0) {
			mFd = open(SERIAL_INTERFACE);
		}
		return mRfidDevice;
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
		RFIDData data = new RFIDData(RFID_CMD_SEARCHCARD, RFID_DATA_SEARCHCARD_ALL);
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
	public boolean keyVerfication(byte sector, byte block, byte[] key) {
		
		if (sector >= 16 || block >= 4) {
			Debug.d(TAG, "===>block over");
			return false;
		}
		byte blk = (byte) (sector*4 + block); 
		if (key == null || key.length != 6) {
			Debug.d(TAG, "===>invalide key");
			return false;
		}
		byte[] keyA = {0x60,blk, key[0], key[1], key[2], key[3], key[4], key[5]};
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
	public boolean writeBlock(byte sector, byte block, byte[] content) {
		
		if (sector >= 16 || block >= 4) {
			Debug.d(TAG, "===>block over");
			return false;
		}
		byte blk = (byte) (sector*4 + block); 
		if (content == null || content.length != 16) {
			Debug.d(TAG, "block no large than 0x3f");
		}
		ByteArrayBuffer buffer = new ByteArrayBuffer(0);
		buffer.append(blk);
		buffer.append(content, 0, content.length);
		RFIDData data = new RFIDData(RFID_CMD_MIFARE_WRITE_BLOCK, buffer.toByteArray());
		byte[] readin = writeCmd(data);
		return isCorrect(readin);
		
	}
	
	/**
	 * Mifare one 卡读块
	 * @param sector 1字节区号
	 * @param block 1字节相对块号
	 * @return 16字节内容
	 */
	public byte[] readBlock(byte sector, byte block) {
		if (sector >= 16 || block >= 4) {
			Debug.d(TAG, "===>block over");
			return null;
		}
		byte blk = (byte) (sector*4 + block); 
		byte[] b = {blk};
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
		
		if (mFd <= 0) {
			mFd = open(SERIAL_INTERFACE);
		}
		Debug.d(TAG, "************write begin******************");
		Debug.print(data.mTransData);
		Debug.d(TAG, "************write end******************");
		int writed = write(mFd, data.transferData(), data.getLength());
		//short[] key = {0x0002, 0x0b00,0x604a, (short) 0xff05, (short) 0xffff, (short) 0xffff, (short) 0xb4ff, 0x0003};
		//int writed = write(fp, key, 15);
		if (writed <= 0) {
			Debug.d(TAG, "===>write err, return");
			return null;
		}
		byte[] readin = read(mFd, 64);
		
		if (readin == null || readin.length == 0) {
			Debug.d(TAG, "===>read err");
			return null;
		}
		Debug.d(TAG, "************read begin******************");
		Debug.print(readin);
		Debug.d(TAG, "************read end******************");
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
	
	
	private int cardInit() {
		//寻卡
		if (!lookForCards()) {
			return RFID_ERRNO_NOCARD;
		}
		//防冲突
		byte[] sn = avoidConflict();
		if (sn == null || sn.length == 0) {
			return RFID_ERRNO_SERIALNO_UNAVILABLE;
		}
		Debug.d(TAG, "+++++++++++ SN +++++++++++");
		Debug.print(sn);
		Debug.d(TAG, "+++++++++++ SN +++++++++++");
		//选卡
		if (!selectCard(sn)) {
			return RFID_ERRNO_SELECT_FAIL;
		}
		return RFID_ERRNO_NOERROR;
	}
	/**
	 * read serial No. using default key
	 * @return 4bytes serial No.
	 */
	public byte[] getSerialNo() {
		if (!keyVerfication((byte) 0, (byte) 0, RFID_DEFAULT_KEY_A)) {
			return null;
		}
		
		byte[] block = readBlock((byte)0, (byte) 0);
		Debug.d(TAG, "*********block 0*********");
		Debug.print(block);
		Debug.d(TAG, "*********block 0*********");
		byte[] sn = new byte[4];
		ByteArrayInputStream stream = new ByteArrayInputStream(block);
		stream.skip(1);
		stream.read(sn, 0, 4);
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sn;
	}
	
	/**
	 * 初始化流程：1、寻卡； 2、防冲突； 3、选卡
	 * 使用默认密钥读取uid 和block1的数据
	 * 通过相应算法得到A、B密钥
	 * @return 选卡成功返回true，失败返回false
	 */
	public synchronized int init() {
		int errno = 0;
		if (( errno = cardInit()) != RFID_ERRNO_NOERROR) {
			return errno;
		}
		// 读UID
		byte[] uid = getSerialNo();
		if (uid == null || uid.length != 4) {
			Debug.d(TAG, "===>get uid fail");
		}
		Debug.print(uid);
		EncryptionMethod method = EncryptionMethod.getInstance();
		byte [] key = method.getKeyA(uid);
		setKeyA(key);
		Debug.print(key);
		
		return 0;
	}
	
	
	/**
	 * 寿命值读取，寿命值保存在sector 4， 和 sector5  的 block 2.
	 * 寿命值采用双区备份，因此需要对读取的数据进行校验
	 */
	private int getInkLevel(boolean isBackup) {
		byte sector = 0;
		byte block = 0;
		if (isBackup) {
			sector = SECTOR_INKLEVEL;
			block = BLOCK_INKLEVEL;
		} else {
			sector = SECTOR_COPY_INKLEVEL;
			block = BLOCK_COPY_INKLEVEL;
		}
		if ( !keyVerfication(sector, block, RFID_DEFAULT_KEY_A))
		{
			return 0;
		}
		byte[] ink = readBlock(sector, block);
		Debug.d(TAG, "************ink level***********");
		Debug.print(ink);
		Debug.d(TAG, "************ink level***********"); 
		EncryptionMethod encryt = EncryptionMethod.getInstance();
		return encryt.decryptInkLevel(ink);
	}
	
	public int getInkLevel() {
		//先从主block读取墨水值
		int current = getInkLevel(false);
		if (!isLevelValid(current)) {
			current = getInkLevel(true);
		}
		if (!isLevelValid(current)) {
			return 0;
		}
		return current;
	}
	/**
	 * 寿命值写入
	 */
	private void setInkLevel(int level, boolean isBack) {
		byte sector = 0;
		byte block = 0;
		if (isBack) {
			sector = SECTOR_INKLEVEL;
			block = BLOCK_INKLEVEL;
		} else {
			sector = SECTOR_COPY_INKLEVEL;
			block = BLOCK_COPY_INKLEVEL;
		}
		if ( !keyVerfication(sector, block, RFID_DEFAULT_KEY_A))
		{
			return ;
		}
		EncryptionMethod encryte = EncryptionMethod.getInstance();
		byte[] content = encryte.encryptInkLevel(level);
		if (content == null) {
			return ;
		}
		writeBlock(SECTOR_INKLEVEL, BLOCK_INKLEVEL, content);
	}

	public void updateInkLevel() {
		
		
	}
	/**
	 * 特征码读取
	 */
	public byte[] getFeatureCode() {
		int errno = 0;
		
		if ( !keyVerfication(SECTOR_FEATURE, BLOCK_FEATURE, RFID_DEFAULT_KEY_A))
		{
			return null;
		}
		byte[] feature = readBlock(SECTOR_FEATURE, BLOCK_FEATURE);
		Debug.d(TAG, "************feature code***********");
		Debug.print(feature);
		Debug.d(TAG, "************feature code***********");
		return feature;
	}
	
	public void setKeyA(byte[] key) {
		if (key == null || key.length != 6) {
			return ;
		}
		mRFIDKeyA = key;
		return;
	}
	
	public void makeCard() {
		//修改秘钥
		/*
		if (!keyVerfication(SECTOR_FEATURE, BLOCK_KEY, RFID_DEFAULT_KEY_A)) {
			Debug.d(TAG, "===>makeCard key verify fail");
			return;
		}
		byte[] key = readBlock(SECTOR_FEATURE, BLOCK_KEY);
		for (int i = 0; i < 6; i++) {
			key[i] = (byte) (mRFIDKeyA[i] & 0x0ff);
			key[10+i] = (byte) (mRFIDKeyA[i] & 0x0ff);
		}
		//重写秘钥,秘钥存放在每个sector的block3
		writeBlock(SECTOR_FEATURE, BLOCK_KEY, key);
		*/
		//特征码
//		if (!keyVerfication(SECTOR_FEATURE, BLOCK_FEATURE, mRFIDKeyA)) {
//			Debug.d(TAG, "===>makeCard key verify fail");
//		}
//		byte[] block = readBlock(SECTOR_FEATURE, BLOCK_FEATURE);
//		Debug.print(block);
		Debug.d(TAG, "=============================");
		//byte[] keya = {0x11, 0x11, 0x11, 0x11, 0x11, 0x11};
		if (!keyVerfication((byte)6, (byte)0, RFID_DEFAULT_KEY_A)) {
			Debug.d(TAG, "===>makeCard key verify fail");
		}
		
		byte[] block = readBlock((byte)6, (byte)0);
		Debug.print(block);
		for(int i = 0; i < 6; i++) {
			block[i] = 0x11;
		}
		writeBlock((byte)6, (byte)0, block);
		
		block = readBlock((byte)6, (byte)0);
		Debug.print(block);
		
		//byte[] feature = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,0x0A, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
		//writeBlock(SECTOR_FEATURE, BLOCK_FEATURE, feature);
	}
	
	private boolean isLevelValid(int value) {
		return true;
	}
}

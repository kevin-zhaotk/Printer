/**
 * RFID 存储分配表
 * ————————————————————————————————————————————
 * 	SECTOR    |    BLOCK    |   Description
 * ————————————————————————————————————————————
 *     4      |	     0      |    墨水总量
 * ————————————————————————————————————————————
 *     4      |	     1      |    特征值
 * ————————————————————————————————————————————
 *     4      |      2      |    墨水量
 * ————————————————————————————————————————————
 *     4      |      3      |    秘钥
 * ————————————————————————————————————————————
 *     5      |	     0      |    墨水总量备份
 * ————————————————————————————————————————————
 *     5      |      1      |    特征值备份
 * ————————————————————————————————————————————
 *     5      |      2      |    墨水量备份
 * ————————————————————————————————————————————
 *     5      |      3      |    秘钥
 * ————————————————————————————————————————————
 */

package com.industry.printer.hardware;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.IllegalFormatCodePointException;

import org.apache.http.util.ByteArrayBuffer;

import android.R.integer;

import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.Utils.EncryptionMethod;
import com.industry.printer.Utils.PlatformInfo;
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
	// public static final String SERIAL_INTERFACE = "/dev/ttyS3";
	
	/*墨水量上下限*/
	public static final int INK_LEVEL_MAX = 100000;
	public static final int INK_LEVEL_MIN = 0;
	
	/**
	 * 特征值
	 */
	public static byte SECTOR_FEATURE = 0x04;
	public static byte BLOCK_FEATURE = 0x01;
	
	/**
	 * 秘钥块
	 */
	public static byte BLOCK_KEY = 0x03;
	
	/**
	 * 特征值备份
	 */
	public static byte SECTOR_COPY_FEATURE = 0x05;
	public static byte BLOCK_COPY_FEATURE = 0x01;
	
	/**
	 * 墨水量
	 */
	public static byte SECTOR_INKLEVEL = 0x04;
	public static byte BLOCK_INKLEVEL = 0x02;
	
	/**
	 * 墨水量备份
	 */
	public static byte SECTOR_COPY_INKLEVEL = 0x05;
	public static byte BLOCK_COPY_INKLEVEL = 0x02;
	
	/**
	 * 墨水总量
	 */
	public static byte SECTOR_INK_MAX = 0x04;
	public static byte BLOCK_INK_MAX = 0x00;
	
	
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
	
	// UID
	public byte[] mSN = null;

	// 当前墨水量
	private int mCurInkLevel = 0;
	private int mLastLevel = 0;
	public 	int mInkMax = 0;
	// 错误码定义
	public static final int RFID_ERRNO_NOERROR = 0;
	public static final int RFID_ERRNO_NOCARD = 1;
	public static final int RFID_ERRNO_SERIALNO_UNAVILABLE = 2;
	public static final int RFID_ERRNO_SELECT_FAIL = 3;
	public static final int RFID_ERRNO_KEYVERIFICATION_FAIL = 4;
	
	// tags 
	public static final String RFID_DATA_SEND = "RFID-SEND:";
	public static final String RFID_DATA_RECV = "RFID-RECV:";
	public static final String RFID_DATA_RSLT = "RFID-RSLT:";
	
	//串口
	public static int mFd=0;
	
	public static RFIDDevice getInstance() {
		if (mRfidDevice == null) {
			mRfidDevice = new RFIDDevice();
		}
		if (mFd <= 0) {
			mFd = open(PlatformInfo.getRfidDevice());
		}
		return mRfidDevice;
	}
	
	public RFIDDevice() {
		mCurInkLevel = 0;
		mLastLevel = mCurInkLevel;
	}
	/*
	 * 端口连接
	 */
	public boolean connect() {
		Debug.d(TAG, "--->RFID connect");
		RFIDData data = new RFIDData(RFID_CMD_CONNECT, RFID_DATA_CONNECT);
		byte[] readin = writeCmd(data);
		return isCorrect(readin);
			
	}
	/*
	 * 设置读卡器工作模式
	 */
	public boolean setType() {
		Debug.d(TAG, "--->RFID setType");
		RFIDData data = new RFIDData(RFID_CMD_TYPEA, RFID_DATA_TYPEA);
		byte[] readin = writeCmd(data);
		return isCorrect(readin);
	}
	/*
	 * 寻卡
	 */
	public boolean lookForCards() {
		Debug.d(TAG, "--->RFID lookForCards");
		RFIDData data = new RFIDData(RFID_CMD_SEARCHCARD, RFID_DATA_SEARCHCARD_ALL);
		byte[] readin = writeCmd(data);
		if (readin == null) {
			return false;
		}
		RFIDData rfidData = new RFIDData(readin, true);
		byte[] rfid = rfidData.getData();
		if (rfid == null || rfid[0] != 0 || rfid.length < 3) {
			Debug.e(TAG, "===>rfid data error");
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
			Debug.e(TAG, "===>unknow rfid type");
			return false;
		}
	}
	/*
	 * 防冲突
	 */
	public byte[] avoidConflict() {
		int limit = 0; 
		Debug.d(TAG, "--->RFID avoidConflict");
		RFIDData data = new RFIDData(RFID_CMD_MIFARE_CONFLICT_PREVENTION, RFID_DATA_MIFARE_CONFLICT_PREVENTION);
		byte[] readin = null;
		for (; readin == null && limit < 3; ) {
			readin = writeCmd(data);
			limit++;
		}
		
		RFIDData rfidData = new RFIDData(readin, true);
		byte[] rfid = rfidData.getData();
		if (rfid == null || rfid[0] != 0 || rfid.length != 5) {
			Debug.e(TAG, "===>rfid data error");
			return null;
		}
		ByteBuffer buffer = ByteBuffer.wrap(rfid);
		buffer.position(1);
		byte[] serialNo = new byte[4]; 
		buffer.get(serialNo, 0, serialNo.length);
		Debug.print(RFID_DATA_RSLT, serialNo);
		return serialNo;
	}
	/*
	 * 选卡
	 */ 
	public boolean selectCard(byte[] cardNo) {
		int limit = 0;
		if (cardNo == null || cardNo.length != 4) {
			Debug.e(TAG, "===>select card No is null");
			return false;
		}
		Debug.d(TAG, "--->RFID selectCard");
		RFIDData data = new RFIDData(RFID_CMD_MIFARE_CARD_SELECT, cardNo);
		byte[] readin = null;
		for (;!isCorrect(readin) && limit < 3;) {
			readin = writeCmd(data);
			limit++;
		}
		return isCorrect(readin);
	}
	
	/**
	 * 密钥验证
	 * @param data
	 * @return
	 */
	public boolean keyVerfication(byte sector, byte block, byte[] key) {
		boolean certify = false;
		Debug.d(TAG, "--->keyVerfication sector:" + sector + ", block:" +block);
		if (sector >= 16 || block >= 4) {
			Debug.e(TAG, "===>block over");
			return false;
		}
		byte blk = (byte) (sector*4 + block); 
		if (key == null || key.length != 6) {
			Debug.e(TAG, "===>invalide key");
			// init();
			return false;
		}
		for(int i = 0; i < 3; i++) {
			byte[] keyA = {0x60,blk, key[0], key[1], key[2], key[3], key[4], key[5]};
			RFIDData data = new RFIDData(RFID_CMD_MIFARE_KEY_VERIFICATION, keyA);
			
			byte[] readin = writeCmd(data);
			certify = isCorrect(readin);
			if (certify) {
				break;
			}
		}
		return certify;
	}
	
	/**
	 * Mifare one 卡写块
	 * @param block 1字节绝对块号
	 * @param content 16字节内容
	 * @return true 成功， false 失败
	 */
	public boolean writeBlock(byte sector, byte block, byte[] content) {
		Debug.d(TAG, "--->writeBlock sector:" + sector + ", block:" +block);
		if (sector >= 16 || block >= 4) {
			Debug.e(TAG, "===>block over");
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
		Debug.d(TAG, "--->readBlock sector:" + sector + ", block:" +block);
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
		
		openDevice();
		Debug.print(RFID_DATA_SEND, data.mTransData);
		
		byte[] readin = null;
		for (int i=0;(readin==null || readin.length <= 0)&& i<3; i++ ) {
			int writed = write(mFd, data.transferData(), data.getLength());
			if (writed <= 0) {
				Debug.e(TAG, "===>write err, return");
				reopen();
				continue;
			}
			try {
				Thread.sleep(10);
			}catch (Exception e) {
			}
			readin = read(mFd, 64);
			if (readin == null) {
				reopen();
			} else {
				break;
			}
			Debug.e(TAG, "===>writeCmd 349: readin=" + readin);
		}
		Debug.print(RFID_DATA_RECV, readin);
		if (readin == null || readin.length == 0) {
			Debug.e(TAG, "===>read err");
			closeDevice();
			return null;
		}
		
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
			// 如果操作失败就关闭串口文件
			close(mFd);
			mFd = 0;
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
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		//防冲突
		mSN = avoidConflict();
		if (mSN == null || mSN.length == 0) {
			return RFID_ERRNO_SERIALNO_UNAVILABLE;
		}
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		//选卡
		if (!selectCard(mSN)) {
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
		//byte[] uid = getSerialNo();
		if (mSN == null || mSN.length != 4) {
			Debug.d(TAG, "===>get uid fail");
		}
		EncryptionMethod method = EncryptionMethod.getInstance();
		byte [] key = method.getKeyA(mSN);
		setKeyA(key);
		Debug.print(RFID_DATA_RSLT, key);
		
		mInkMax = getInkMax();
		Debug.d(TAG, "===>max ink: " + mInkMax);
		return 0;
	}
	
	
	/**
	 * 读取墨水总量
	 * @return
	 */
	private int getInkMax() {
		if ( !keyVerfication(SECTOR_INK_MAX, BLOCK_INK_MAX, mRFIDKeyA))
		{
			Debug.d(TAG, "--->key verfy fail,init and try once");
			// 如果秘钥校验失败则重新初始化RFID卡
			//init();
			if (!keyVerfication(SECTOR_INK_MAX, BLOCK_INK_MAX, mRFIDKeyA)) {
				return 0;
			}
			
		}
		byte[] ink = readBlock(SECTOR_INK_MAX, BLOCK_INK_MAX);
		EncryptionMethod encrypt = EncryptionMethod.getInstance();
		return encrypt.decryptInkMAX(ink);
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
		Debug.d(TAG, "--->getInkLevel sector:" + sector + ", block:" + block);
		// 只使用唯一密钥验证
		if ( !keyVerfication(sector, block, mRFIDKeyA))
		{
			Debug.d(TAG, "--->key verfy fail,init and try once");
			// 如果秘钥校验失败则重新初始化RFID卡
			//init();
			if (!keyVerfication(sector, block, mRFIDKeyA)) {
				return 0;
			}
			
		}
		byte[] ink = readBlock(sector, block);
		EncryptionMethod encryt = EncryptionMethod.getInstance();
		Debug.d(TAG, "--->ink level:" + encryt.decryptInkLevel(ink));
		return encryt.decryptInkLevel(ink);
	}
	
	public float getInkLevel() {
		Debug.d(TAG, "--->getInkLevel");
		// 先从主block读取墨水值
		int current = getInkLevel(false);
		if (!isLevelValid(current)) {
			// 如果主block墨水值不合法则从备份区读取
			Debug.e(TAG, "--->read from master block failed, try backup block");
			current = getInkLevel(true);
			if (!isLevelValid(current)) {
				Debug.e(TAG, "--->read from backup block failed");
				mCurInkLevel = 0;
				return 0;
			}
		}
		mCurInkLevel = current;
		Debug.d(TAG, "===>curInk=" + mCurInkLevel + ", max=" + mInkMax);
		return mCurInkLevel;
		/*if (mInkMax <= 0) {
			return 0;
		} else if (mCurInkLevel > mInkMax) {
			return 100;
		} else {
			return (mCurInkLevel * 100)/mInkMax;
		}*/
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
		
		if ( !keyVerfication(sector, block, mRFIDKeyA))
		{
			return ;
		}
		Debug.d(TAG, "--->setInkLevel sector:" + sector + ", block:" + block);
		EncryptionMethod encryte = EncryptionMethod.getInstance();
		byte[] content = encryte.encryptInkLevel(level);
		if (content == null) {
			return ;
		}
		writeBlock(sector, block, content);
	}

	/**
	 *更新墨水值，即当前墨水值减1 
	 */
	public float updateInkLevel() {
		Debug.d(TAG, "--->updateInkLevel");
		/* 为了提高效率，更新墨水量时不再从RFID读取，而是使用上次读出的墨水量
		int level = getInkLevel(false);
		if (!isLevelValid(level)) {
			// 如果主block墨水值不合法则从备份区读取
			level = getInkLevel(true);
			if (!isLevelValid(level)) {
				return 0;
			}
		}
		*/
		
		if (mCurInkLevel > 0) {
			mCurInkLevel = mCurInkLevel -1;
		} else if (mCurInkLevel <= 0) {
			mCurInkLevel = 0;
		}
		
		// 将新的墨水量写回主block
		setInkLevel(mCurInkLevel, false);
		// 将新的墨水量写回备份block
		setInkLevel(mCurInkLevel, true);
		Debug.d(TAG, "===>cur=" + mCurInkLevel + ", max=" + mInkMax);
		return mCurInkLevel;
		/*
		if (mInkMax <= 0) {
			return 0;
		} else if (mCurInkLevel > mInkMax) {
			return 100;
		} else if ((mCurInkLevel * 100)/mInkMax < 0.1) {
			return (float) 0.1;
		} else {
			return (mCurInkLevel * 100)/mInkMax;
		}
		*/
	}
	
	/**
	 *更新墨水值，即当前墨水值减1 
	 */
	public void updateToDevice() {
		Debug.d(TAG, "--->updateInkLevel");
		/* 为了提高效率，更新墨水量时不再从RFID读取，而是使用上次读出的墨水量
		int level = getInkLevel(false);
		if (!isLevelValid(level)) {
			// 如果主block墨水值不合法则从备份区读取
			level = getInkLevel(true);
			if (!isLevelValid(level)) {
				return 0;
			}
		}
		*/
		if (mLastLevel == mCurInkLevel) {
			return;
		}
		if (mCurInkLevel <= 0) {
			mCurInkLevel = 0;
		}
		Debug.d(TAG, "--->updateInkLevel level = " + mCurInkLevel);
		
		// 将新的墨水量写回主block
		setInkLevel(mCurInkLevel, false);
		// 将新的墨水量写回备份block
		setInkLevel(mCurInkLevel, true);
	}
	
	/**
	 * 特征码读取
	 */
	public boolean checkFeatureCode() {
		int errno = 0;
		Debug.d(TAG, "--->RFID getFeatureCode");
		if ( !keyVerfication(SECTOR_FEATURE, BLOCK_FEATURE, mRFIDKeyA))
		{
			return false;
		}
		byte[] feature = readBlock(SECTOR_FEATURE, BLOCK_FEATURE);
		if (feature == null ) {
			return false;
		}
		Debug.d(TAG, "===>feature:" + feature[1] + ", " +feature[2]);
		if (feature[1] == 100 && feature[2] == 1) {
			return true;
		}
		return false;
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
		
		for(int i = 0; i < 6; i++) {
			block[i] = 0x11;
		}
		writeBlock((byte)6, (byte)0, block);
		
		block = readBlock((byte)6, (byte)0);
		
		
		//byte[] feature = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,0x0A, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
		//writeBlock(SECTOR_FEATURE, BLOCK_FEATURE, feature);
	}
	
	private boolean isLevelValid(int value) {
		if (value < INK_LEVEL_MIN || value > INK_LEVEL_MAX) {
			return false;
		}
		return true;
	}
	
	private int openDevice() {
		if (mFd <= 0) {
			mFd = open(PlatformInfo.getRfidDevice());
			//init();
		}
		Debug.d(TAG, "===>mFd=" + mFd);
		return mFd;
	}
	
	private void closeDevice() {
		if (mFd > 0) {
			close(mFd);
		}
		mFd = -1;
	}
	private void reopen() {
		close(mFd);
		mFd = 0;
		openDevice();
	}
}

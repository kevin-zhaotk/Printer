package com.industry.printer.data;

import org.apache.http.util.ByteArrayBuffer;

public class RFIDData {
	
	private static final byte mIdentificator = 0x01;
	private static final byte mHeader = 0x02;
	private static final byte mTailer = 0x03;
	/*已经插入了数据辨识符（0x10）*/
	public byte[] mTransData;
	/*过滤掉数据辨识符（0x10）的真实数据*/
	public byte[] mRealData;
	
	private short mAddress;
	private byte mLength;
	private byte mCommand;
	private byte[] mData;
	private byte mCheckCode;
	private byte mResult;
	
	/**
	 * 通过命令字和数据域构造数据
	 * @param cmd 命令字
	 * @param data 数据域
	 */
	public RFIDData(byte cmd, byte[] data) {
		mAddress = 0x0000;
		mLength = 0x00;
		mCheckCode = 0x00;
		mCommand = cmd;
		mData = data;
		ByteArrayBuffer buffer = new ByteArrayBuffer(0);
		buffer.append(mHeader);
		buffer.append(mAddress);
		buffer.append(mLength);
		buffer.append(mCommand);
		buffer.append(mData, 0, mData.length);
		buffer.append(mCheckCode);
		buffer.append(mTailer);
		mRealData = buffer.buffer();
		getTransferData();
	}
	/**
	 * 构造RFID数据
	 * @param data
	 * @param isfilter 如果为true 表示data为插入了数据辨识符的传输数据，如果为false 表示data为过滤掉数据辨识符的真实数据
	 */
	public RFIDData(byte[] data, boolean isfilter) {
		
	}
	
	private void getTransferData() {
		ByteArrayBuffer buffer = new ByteArrayBuffer(0);
		if( mRealData == null || mRealData.length <= 0) {
			return;
		}
		//添加帧头
		buffer.append(mRealData[0]);
		//数据包内容
		for (int i = 1; i < mRealData.length-1; i++) {
			if (mRealData[i] == mIdentificator || mRealData[i] == mHeader || mRealData[i] == mTailer) {
				buffer.append(0x01);
				buffer.append(mRealData[i]);
			} else {
				buffer.append(mRealData[i]);
			}
		}
		buffer.append(mRealData[mRealData.length-1]);
		
		mTransData = buffer.buffer();
	}
}

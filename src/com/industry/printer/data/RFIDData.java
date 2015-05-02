package com.industry.printer.data;

import org.apache.http.util.ByteArrayBuffer;

import com.industry.printer.Utils.Debug;

import android.R.string;

public class RFIDData {
	
	private static final byte mIdentificator = 0x10;
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
		Debug.d("", "===>cmd:"+cmd +", mCommand:"+mCommand);
		for (int i=0; i<data.length; i++) {
			Debug.d("", "===>data:"+data[i]);
		}
		for (int i=0; i<mData.length; i++) {
			Debug.d("", "===>mData:"+mData[i]);
			
		}
		ByteArrayBuffer buffer = new ByteArrayBuffer(0);
		buffer.append(mHeader);
		buffer.append(mAddress);
		buffer.append(mLength);
		buffer.append(mCommand);
		buffer.append(mData, 0, mData.length);
		buffer.append(mCheckCode);
		buffer.append(mTailer);
		mRealData = buffer.toByteArray();
		for (int i=0; i<mRealData.length; i++) {
			Debug.d("", "===>mRealData:"+mRealData[i]);
			
		}
		//计算长度字
		mLength = (byte) (mRealData.length-4);
		mRealData[3] = mLength;
		//计算校验字
		for (int i = 1; i < mRealData.length-2; i++) {
			mCheckCode += mRealData[i];	
			mRealData[mRealData.length-2] = mCheckCode;
		}
		getTransferData();
	}
	/**
	 * 构造RFID数据
	 * @param data
	 * @param isfilter 如果为true 表示data为插入了数据辨识符的传输数据，如果为false 表示data为过滤掉数据辨识符的真实数据
	 */
	public RFIDData(byte[] data, boolean isfilter) {
		if (isfilter) {
			mTransData = data;
			getRealData();
			//计算长度字
			mLength = (byte) (mRealData.length-4);
			mRealData[3] = mLength;
			//计算校验字
			for (int i = 1; i < mRealData.length-2; i++) {
				mCheckCode += mRealData[i];	
			}
		} else {
			mRealData = data;
			getTransferData();
		}
	}
	
	/**
	 * 根据未添加辨识符的数据得到添加辨识符的数据
	 */
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
				Debug.d("", "===>data:"+0x10);
				buffer.append((byte)0x10);
				Debug.d("", "===>data:"+mRealData[i]);
				buffer.append(mRealData[i]);
			} else {
				buffer.append(mRealData[i]);
				Debug.d("", "===>data:"+mRealData[i]);
			}
		}
		buffer.append(mRealData[mRealData.length-1]);
		Debug.d("", "===>data:"+mRealData[mRealData.length-1]);
		mTransData = buffer.toByteArray();
		
	}
	
	/**
	 * 根据添加辨识符的数据得到未添加辨识符的数据
	 */
	private void getRealData() {
		ByteArrayBuffer buffer = new ByteArrayBuffer(0);
		if( mTransData == null || mTransData.length <= 0) {
			return;
		}
		//添加帧头
		buffer.append(mTransData[0]);
		//数据包内容
		for (int i = 1; i < mTransData.length-1; i++) {
			if (mTransData[i] == mIdentificator) {
				i++;
				buffer.append(mRealData[i]);
			} else {
				buffer.append(mRealData[i]);
			}
		}
		buffer.append(mTransData[mTransData.length-1]);
		
		mRealData = buffer.toByteArray();
	}
	
	@Override
	public String toString() {
		String data = "real: ";
		for (int i=0; i<mRealData.length; i++) {
			data += " " + String.format("0x%1$02x", mRealData[i]);
		}
		data += " , trans:";
		for (int i=0; i<mTransData.length; i++) {
			data += " " + String.format("0x%1$02x", mTransData[i]);
		}
		return data;
	}
}

package com.industry.printer.object.data;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;

import org.apache.http.util.CharArrayBuffer;

import android.content.Context;

import com.industry.printer.BinInfo;
import com.industry.printer.Utils.Debug;
import com.industry.printer.data.BinCreater;
import com.industry.printer.hardware.RFIDDevice;
import com.industry.printer.hardware.RFIDManager;

public class SegmentBuffer {
	
	public static final String TAG = SegmentBuffer.class.getSimpleName();
	
	/**
	 * 打印送数方向 0表示从右往左（buffer的正常顺序）
	 * 	        1表示从左往右（buffer要进行倒序）
	 */
	public static final int DIRECTION_NORMAL = 0;
	public static final int DIRECTION_REVERS = 1;
	
	public int mColumns;
	public int mHight;
	public int mType;
	public CharArrayBuffer mBuffer;
	private Context mContext;
	
	private RFIDDevice mRfid;
	/**
	 * 从BinInfo中提取指定打印头的buffer数据，比如该TLK支持3头打印，从中取出第二个打印头的buffer数据
	 * @param info 从bin文件生成的BinInfo对象
	 * @param type  打印头类型 见 MessageType
	 */
	public SegmentBuffer(Context ctx, char[] info, int type, int heads, int ch) {
		this(ctx, info, type, heads, ch, DIRECTION_NORMAL);
	}
	
	public SegmentBuffer(Context ctx, char[] info, int type, int heads, int ch, int direction) {
		this(ctx, info, type, heads, ch, direction, 0);
	}

	public SegmentBuffer(Context ctx, char[] info, int type, int heads, int ch, int direction, int shift) {
		this(ctx, info, type, heads, ch, direction, shift, 0);
	}

	/**
	 * 从BinInfo中取出的buffer是原始的数据，没有进行补偿和偏移转换计算
	 * 所有的处理工作都是在SegmentBuffer中完成
	 * @param info 
	 * @param type 打印头索引
	 * @param heads 打印头数量
	 * @param ch 补偿后的列高（双字节数）
	 * @param direction 数据方向
	 * @param shift  顺移列数
	 * @param revert 按位反转
	 */
	public SegmentBuffer(Context ctx, char[] info, int type, int heads, int ch, int direction, int shift, int revert) {
		mType = type;
		mBuffer = new CharArrayBuffer(0);
		char feed = 0x0;
		mContext = ctx;
		/*计算info的总列数*/
		mColumns = info.length/ch;	
		/*计算每个打印头的高度*/
		mHight = ch/heads;
		Debug.d(TAG, "--->mHight=" + mHight + ",  columns=" + mColumns );
		Debug.d(TAG, "--->ch=" + ch + ", direction=" + direction + ", shift=" + shift);
		/*计算当前打印头的起始*/
		int start = mHight * type;
		
		/*打印起始位平移shift列*/
		for (int j = 0; j < mHight * shift; j++) {
			mBuffer.append(feed);
		}
		
		for (int i = 0; i < mColumns; i++) {
			
			if (direction == DIRECTION_NORMAL) {
				mBuffer.append(info, i * ch + start, mHight);
			} else if (direction == DIRECTION_REVERS) {
				mBuffer.append(info, (mColumns-i-1) * ch + start, mHight);
			}
		}
		/*原始列数+偏移列数=该buffer的总列数*/
		mColumns += shift;

		reverse(revert);
		mRfid = RFIDManager.getInstance(mContext).getDevice(mType);
	}


	/**
	 *
	 * @param pattern
	 * 	pattern 是位域操作，每个bit代表一个值
	 * 		bit0: 1头反转标志
	 * 		bit1: 2头反转标志
	 * 		bit2: 3头反转标志
	 * 		bit3: 4头反转标志
	 * 	bit0|bit1|bit2|bit3 == 1111   按32bit反转
	 * 	bit0 bit1== 11  按16bit反转
	 * 	bit2 bit3== 11  按16bit反转
	 *
	 */
	public void reverse(int pattern) {

		if ((pattern & 0x0f) == 0x00) {
			return;
		}

		char[] buffer = mBuffer.buffer();
		mBuffer = new CharArrayBuffer(0);

		// 4头整体反转
		if (pattern == 0x0f) {
			for (int i = 0; i < buffer.length/2; i++) {
				int source = buffer[2 * i] | buffer[2*i +1];
				mBuffer.append(source);
			}
			return;
		}

		for (int i = 0; i < buffer.length; i++) {
			// 1-2头数据
			if (i % 2 == 0) {
				// 1-2反转
				if ((pattern & 0x03) > 0) {
					char source = buffer[i];
					mBuffer.append(revert(source));
				} else if ((pattern & 0x03) == 0x01) {		//仅1头反转
					byte low = (byte)(buffer[i] & 0x0ff);
					char output = (char)(buffer[i] & 0x0ff);
					output |= revert(low);
					mBuffer.append(output);
				} else if ((pattern & 0x03) == 0x02) {		//仅2头反转
					byte high = (byte) ((buffer[i] & 0x0ff00) >> 8);
					char output = (char) (buffer[i] & 0x0ff);
					output |= revert(high) << 8;
					mBuffer.append(output);
				} else {
					mBuffer.append(buffer[i]);
				}
			} else {	// 3-4头数据
				// 3-4反转
				if ((pattern & 0x0C) > 0) {
					char source = buffer[i];
					mBuffer.append(revert(source));
				} else if ((pattern & 0x0C) == 0x04) {		//仅3头反转
					byte low = (byte)(buffer[i] & 0x0ff);
					char output = (char)(buffer[i] & 0x0ff);
					output |= revert(low);
					mBuffer.append(output);
				} else if ((pattern & 0x0C) == 0x08) {		//仅4头反转
					byte high = (byte) ((buffer[i] & 0x0ff00) >> 8);
					char output = (char) (buffer[i] & 0x0ff);
					output |= revert(high) << 8;
					mBuffer.append(output);
				} else {
					mBuffer.append(buffer[i]);
				}
			}
		}

	}

	/**
	 * source按bits参数指定的位数进行反转,比如：source=20(0x14), bits = 8 -> 0x28
	 * @param source
	 * @return
	 */
	private byte revert(byte source) {

		byte output = 0;
		for (int i = 0; i < 7; i++) {
			if ((source & (0x01 << i)) > 0) {
				output |= 0x01 << (8 - i);
			}
		}
		return output;
	}

	private char revert(char source) {
		char output = 0;
		for (int i = 0; i < 16; i++) {
			if ((source & (0x01 << i)) > 0) {
				output |= 0x01 << (16 - i);
			}
		}
		return output;
	}

	private int revert(int source) {
		int output = 0;
		for (int i = 0; i < 32; i++) {
			if ((source & (0x01 << i)) > 0) {
				output |= 0x01 << (32 - i);
			}
		}
		return output;
	}


	public void readColumn(char[] buffer, int col, int offset) {
		//如果當前打印頭的鎖無效，則直接返回全零buffer（即該頭無輸出）
		/*
		if (mRfid == null || mRfid.getLocalInk() > 0) {
			for (int i = 0; i < mHight; i++) {
				buffer[offset+i] = 0;
			}
			return;
		}*/
		CharArrayReader reader = new CharArrayReader(mBuffer.buffer());
		// Debug.d(TAG, "--->col=" + col + ", mColumns=" + mColumns);
		if (col < mColumns) {
			try {
				reader.skip(col * mHight);
				reader.read(buffer, offset, mHight);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public int getColumns() {
		if (mHight <= 0) {
			return 0;
		}
		return mBuffer.length()/mHight;
	}
}

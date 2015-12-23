package com.industry.printer.object.data;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;

import org.apache.http.util.CharArrayBuffer;

import com.industry.printer.BinInfo;

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
	public CharArrayBuffer mBuffer;
	/**
	 * 从BinInfo中提取指定打印头的buffer数据，比如该TLK支持3头打印，从中取出第二个打印头的buffer数据
	 * @param info 从bin文件生成的BinInfo对象
	 * @param type  打印头类型 见 MessageType
	 */
	public SegmentBuffer(char[] info, int type, int heads, int ch) {
		this(info, type, heads, ch, DIRECTION_NORMAL);
	}
	
	public SegmentBuffer(char[] info, int type, int heads, int ch, int direction) {
		this(info, type, heads, ch, direction, 0);
	}
	
	public SegmentBuffer(char[] info, int type, int heads, int ch, int direction, int shift) {
		CharArrayBuffer mBuffer = new CharArrayBuffer(0);
		/*计算info的总列数*/
		mColumns = info.length/ch;
		/*计算每个打印头的高度*/
		mHight = ch/heads;
		/*计算当前打印头的起始*/
		int start = mHight * type;
		
		for (int i = 0; i < mColumns; i++) {
			/*打印起始位平移shift列*/
			for (int j = 0; j < mHight * shift; j++) {
				mBuffer.append(0);
			}
			if (direction == DIRECTION_NORMAL) {
				mBuffer.append(info, i * ch + start, mHight);
			} else if (direction == DIRECTION_REVERS) {
				mBuffer.append(info, (mColumns-i) * ch + start, mHight);
			}
		}
		/*原始列数+偏移列数=该buffer的总列数*/
		mColumns += shift;
	}
	
	public void readColumn(char[] buffer, int col, int offset) {
		CharArrayReader reader = new CharArrayReader(mBuffer.buffer());
		
		if (col < mColumns) {
			try {
				reader.mark(col * mHight);
				reader.read(buffer, offset, mHight);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}

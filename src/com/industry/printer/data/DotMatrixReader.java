package com.industry.printer.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;

import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;

public class DotMatrixReader {

	private static final String TAG = DotMatrixReader.class.getSimpleName(); 
	public static DotMatrixReader mInstance;
	
	private File mDotFile;
	private InputStream mReader;
	
	public static DotMatrixReader getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DotMatrixReader(context);
		}
		return mInstance;
	}

	public DotMatrixReader(Context context) {
		
		try {
			mReader = context.getAssets().open("dotmatrix/HZK16");
			mReader.mark(0);
			mReader.reset();
		} catch (FileNotFoundException e) {
			Debug.e(TAG, "===>Excpetion:"+e.getMessage());
		} catch (IOException e) {
			Debug.e(TAG, "===>Excpetion:"+e.getMessage());
		}
	}
	
	public byte[] getDotMatrix(char[] inCodes) {
		
		int offset = 0;
		byte[] buffer = new byte[32];
		for (int i = 0; i < inCodes.length; i++) {
			if (isAscii(inCodes[i])) {
				offset = getOffsetByAscii(inCodes[i]);
			} else {
				offset = getOffsetByGBCode(inCodes[i]);
			}
			try {
				mReader.reset();
				mReader.skip(offset);
				mReader.read(buffer);
				Debug.d(TAG, "----------------------");
				Debug.d(TAG, "===>code:"+Integer.toHexString(inCodes[i])+"   offset:"+offset);
				print(buffer);
				Debug.d(TAG, "----------------------");
			} catch (IOException e) {
			}
			
		}
		return null;
	}
	
	/**
	 * 数字和字符通过ascii码计算字库偏移量
	 * 计算公式：offset = (94*(3-1)+(*(str+i)-0x30+16-1))*200L 
	 * @param ascii
	 * @return 偏移量
	 */
	private int getOffsetByAscii(char ascii) {
		int offset = (94*(3-1)+(ascii-0x30+16-1))*32;
		return offset;
	}
	
	/**
	 * 汉字通过国标码计算字库偏移量
	 * 计算公式 offset =(94*(qh-1)+(wh-1))*32;
	 * @param gbk 国标码
	 * @return 偏移量
	 */
	private int getOffsetByGBCode(char gbk) {
		Debug.d(TAG, "--->gbk:"+Integer.toHexString(gbk));
		int quCode=0, weiCode=0; 
		quCode = (gbk>>8)&0x00ff;
		weiCode = gbk & 0x00ff;
		Debug.d(TAG, "--->gbk qu:"+Integer.toHexString(quCode)+" , wei:"+Integer.toHexString(weiCode));
		return (94*(quCode-1)+(weiCode-1))*32;
	}
	
	private boolean isAscii(char c) {
		return (c < 0xA0);
	}
	
	private void print(byte[] value) {
		if (value == null) {
			return;
		}
		for (int i = 0; i < value.length; i++) {
			Debug.d(TAG, "--->"+Integer.toHexString(value[i]&0x0ff));
		}
	}
}

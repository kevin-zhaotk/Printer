package com.industry.printer.FileFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.RandomAccessFile;

import com.industry.printer.Utils.Configs;

import android.content.Context;

/**
 * 從QR.data文件中讀取二維碼數據
 * 在QRlast.xml裏記錄上次讀取的位置
 * @author kevin
 *
 */
public class QRReader {
	
	public Context mContext;
	
	/* 當前行數 */
	public int mRow;
	
	BufferedReader mReader;
	
	FileOutputStream mWriter;
	
	private static QRReader mInstance;
	
	public static QRReader getInstance(Context ctx) {
		if (mInstance == null) {
			mInstance = new QRReader(ctx);
		}
		return mInstance;
	}
	
	public QRReader(Context ctx) {
		mContext = ctx;
		init();
	}
	
	private void init() {
		File last = new File(Configs.QR_LAST);
		
		try {
			if (!last.exists()) {
				mRow = 0;
				last.createNewFile();
			}
			FileReader reader = new FileReader(last);
			BufferedReader bReader = new BufferedReader(reader);
			String row = bReader.readLine();
			mRow = Integer.parseInt(row);
			if (mRow <= 0) {
				mRow = 1;
			}
			FileReader r = new FileReader(Configs.QR_DATA);
			mReader = new BufferedReader(r);
			for (int i = 0; i < mRow; i++) {
				mReader.readLine();
			}
		} catch(Exception e) {
			mRow = 0;
		}
	}
	
	public String read() {
		if (mReader == null) {
			return  null;
		}
		
		try {
			String line = mReader.readLine();
			int index = line.indexOf(",");
			String content = line.substring(index + 1);
			mWriter = new FileOutputStream(Configs.QR_LAST);
			mWriter.write(String.valueOf(mRow++).getBytes());
			mWriter.close();
			return content;
		} catch (Exception e) {
			return null;
		}
	}
}

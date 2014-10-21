package com.industry.printer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.util.ByteArrayBuffer;

import com.industry.printer.Utils.Debug;
import com.industry.printer.object.BinCreater;

import android.graphics.Bitmap;
import android.util.Log;

public class BinInfo {
	public static final String TAG="BinInfo";
	/**
	 * bin文件的总列数
	 */
	public int mColumn;
	/**
	 * 每一列的点数
	 */
	public int mBitsperColumn;	
	/**
	 * 变量的bin文件中每个元素的点数
	 */
	public int mColOne;
	
	public byte[] mBits;
	//public int[]	mPixels;
	
	public void BinInfo()
	{
		mColumn = 0;
		mBitsperColumn=0;
		mColOne=0;
		mBits=null;
	}
	
    public void getBgBuffer(String f) throws IOException
    {
    	byte[] head = new byte[BinCreater.RESERVED_FOR_HEADER];
    	//mBmpBits
    	/*
    	 
    	 */
    	File file = new File(f);
		FileInputStream fs = new FileInputStream(file);
		fs.read(head, 0, BinCreater.RESERVED_FOR_HEADER);
		Debug.d(TAG, "fs.available()="+fs.available()+", head.length="+head.length);
		mBits=new byte[fs.available()];
		if(mBits == null)
			return;
    	mColumn =  (head[0]&0xff) << 16 | (head[1] & 0xff)<<8 | (head[2]&0xff);
    	mBitsperColumn = (mBits.length/mColumn)*8;
    	//mPixels = new int[columns*row];
    	Debug.d(TAG, "columns = "+mColumn+", mBitsperColumn="+mBitsperColumn+", mBits.len="+mBits.length);
    	fs.read(mBits, 0, mBits.length);
    	fs.close();
    	return;//bmp.createScaledBitmap(bmp, columns, 150, true);
    }
    
    public void getVarBuffer(String var, String f) throws IOException
    {
    	int n;
    	int byteOneCol;
    	byte[] buffer=null;
    	Debug.d(TAG, "getVarBuffer file="+f);
   		File file = new File(f);
   		if(!file.exists() || !file.isFile())
   		{
   			Debug.d(TAG, "file exist ? "+file.exists() + ", isfile? "+file.isFile());
   			return ;
   		}
   		/*
   		 *read out the width of each number
   		 */
   		buffer = new byte[(int) file.length()];
   		FileInputStream fs = new FileInputStream(file);
   		fs.read(buffer);
   		mColumn = buffer[0] << 16| buffer[1] <<8 | buffer[2];
   		mBitsperColumn=(buffer.length/mColumn)*8;
   		mColOne = buffer[6] << 16| buffer[7] <<8 | buffer[8];
   		if(mColOne == 0)
   		{
   			mColOne = mColumn/10;
   		}
   		Debug.d(TAG, "*******f="+f+", mBitsperColumn="+mBitsperColumn);
   		Debug.d(TAG, "*******mColumn="+mColumn+", mColOne ="+mColOne);
   		byteOneCol = mBitsperColumn%8==0? mBitsperColumn/8 : mBitsperColumn/8 +1;
   		Debug.d(TAG, "*******byteOneCol ="+byteOneCol );
   		ByteArrayBuffer ba = new ByteArrayBuffer(mColOne*byteOneCol*var.length());
   		for(int i=0; i<var.length(); i++)
   		{
   			n = Integer.parseInt(var.substring(i, i+1));
   			ba.append(buffer, n*mColOne*byteOneCol+16, mColOne*byteOneCol);
   		}
    	fs.close();	
   		mBits=ba.buffer();
   		Debug.d(TAG, "*******mBits.len="+mBits.length );
    	return ;
    }
}

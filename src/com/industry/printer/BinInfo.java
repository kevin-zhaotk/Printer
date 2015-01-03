package com.industry.printer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.apache.http.util.ByteArrayBuffer;

import com.industry.printer.Utils.Configs;
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
   		mColumn = (int)(buffer[0]&0x0ff << 16)| (int)(buffer[1]&0x0ff) <<8 | (int)(buffer[2]&0x0ff);
   		Debug.d(TAG, "-----buffer[0]="+(int)(buffer[0]&0x0ff)+", buffer[1]="+(int)(buffer[1]&0x0ff)+", buffer[2]="+(int)(buffer[2]&0x0ff));
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
    
    /**
     * 该函数用于将预览得到的buffer进行字节变换，生成880设备的打印buffer
     * 顺序是byte0+ Byte55, byte1+byte56
     * @param buffer 待转换的buffer
     */
    public static void Matrix880(byte[] buffer){
    	byte[] tmp= new byte[110];
    	Debug.d(TAG, "===>Matrix880 : buffer len:"+buffer.length);
    	for(int i=0; i< buffer.length/(Configs.gDots/8); i++){
    		for(int j=0; j<Configs.gDots/(2*8); j++){
    			tmp[2*j] = buffer[i*(Configs.gDots/8)+j];
    			tmp[2*j+1] = buffer[i*(Configs.gDots/8)+j+55]; 
    		}
    		for(int j=0; j<Configs.gDots/8; j++){
    			buffer[i*(Configs.gDots/8)+j] = tmp[j];
    		}
    	}
    }
}

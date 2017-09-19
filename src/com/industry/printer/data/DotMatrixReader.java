package com.industry.printer.data;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.Utils.PlatformInfo;

public class DotMatrixReader {

	private static final String TAG = DotMatrixReader.class.getSimpleName(); 
	public static DotMatrixReader mInstance=null;
	
	private File mDotFile;
	private InputStream mReader;
	
	public static DotMatrixReader getInstance(Context context,float height,float width) 
	{
		if (mInstance == null)
		{
			mInstance = new DotMatrixReader(context,  height,width);
		}
		return mInstance;
	}

	public DotMatrixReader(Context context,float height,float width) 
	{		
;
	}
	
	/**
	 * 根据国标码查询字库点阵
	 * @param inCodes
	 * @return
	 */
	public byte[] getDotMatrix(char[] inCodes,Context context,float height,float width) 
	{
		
		try {  
			
			if (false)//new File(ConfigPath.getFont()).exists())
		{
			mReader = new FileInputStream(ConfigPath.getFont());
			mReader.mark(0);
			mReader.reset();
			Debug.e(TAG, "======================>DotMatrixReader==if (new File(ConfigPath.getFont()).exists())=");
			} else
			{
			//	mReader = context.getAssets().open("dotmatrix/HZK16");
			Debug.e(TAG, "11111===>DotMatrixReader"+height);
				if( (int)(height)==76)
				{
					mReader = context.getAssets().open("dotmatrix/dk16-8_DZK");	
					mReader.mark(0);
					mReader.reset();
					Debug.e(TAG, "11111..11===>DotMatrixReader"+height);
				}				
				else if( (int)(height)==152)
				{
					mReader = context.getAssets().open("dotmatrix/dk16-16_DZK");
					mReader.mark(0);
					mReader.reset();					
				}
				else
				{
					mReader = context.getAssets().open("dotmatrix/dk16-16_DZK");
					mReader.mark(0);
					mReader.reset();					
				}
				Debug.e(TAG, "2222==>DotMatrixReader");
			 }
			
		} catch (FileNotFoundException e) {
			Debug.e(TAG, "===>Excpetion:"+e.getMessage());
		} catch (IOException e) {
			Debug.e(TAG, "===>Excpetion:"+e.getMessage());
		}
		
		
		
		int offset = 0;
		byte[] buffer = new byte[32];
		ByteArrayBuffer matrix = new ByteArrayBuffer(0);
		for (int i = 0; i < inCodes.length; i++) 
		{
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
				//Debug.d(TAG, "===>code:"+Integer.toHexString(inCodes[i])+"   offset:"+offset);
				//Debug.print(buffer);
				Debug.d(TAG, "----------------------");
//addbylk0625	
	 	    	columnTransferSmallend(buffer);
	///			columnTransferBigend(buffer);
//				Debug.print(buffer);
				Debug.e(TAG, "-====columnTransferSmallend----------");
		
				if( (int)(height)==76)
				{	Debug.e(TAG, "-====columnTransferSmallend1111111----------");
					matrix.append(expendTo32Bit_width8(buffer), 0, 4*6);//32高 8行 宽 
					Debug.e(TAG, "-====columnTransferSmallend1111..2222---------");
				}
				if( (int)(height)==152)
				{								
					matrix.append(expendTo32Bit(buffer), 0, 4*12);//32高 16行 宽 
				}
				
				Debug.e(TAG, "-====columnTransferSmallend22222----------");
				
		//		matrix.append(buffer, 0, 32);	
				
			} catch (IOException e) {
			}
			
		}
		return matrix.toByteArray();
	}
	
	public int getDotCount(byte[] dots) {
		int count = 0;
		for (int i = 0; i < dots.length; i++) {
			for (int j = 0; j < 8; j++) {
				if ((dots[i] & (0x01<< j)) > 0)
				{
					count++;
				}
			}
		}
		return count;
	}
	/**
	 * 把字库中取出的行点阵转换成列点阵,高字节在前
	 * @param matrix
	 */
	private void columnTransferBigend(byte[] matrix) {
		if (matrix == null || matrix.length != 32) {
			return;
		}
		byte[] trans = new byte[32];

		for (int i = 0; i < trans.length; i++) {
			for (int j = 0; j < 8; j++) {
				byte data = (byte)((matrix[j*2 + i/16 + (i%2)*16]) & 0x0ff);
				data = (byte) (data  & (0x01 <<(7-(i/2)%8))); 
				// Debug.d(TAG, "i="+i+", j="+j+"-->"+data);
				if ( data != 0) {
					trans[i] |= (0x01 << (7-j));
				}
			}
		}
		Debug.print("", trans);
		ByteBuffer b = ByteBuffer.wrap(trans);
		b.get(matrix);
	}
	
	/**
	 * 把字库中取出的行点阵转换成列点阵，低字节在前，与点阵显示工具对应
	 * @param matrix
	 */
	/*
	private void columnTransferSmallend(byte[] matrix) {
		if (matrix == null || matrix.length != 32) {
			return;
		}
		int  A;
		byte[] trans = new byte[32];

		for (int i = 0; i < trans.length; i++) {
			for (int j = 0; j < 8; j++) {
				byte data = (byte)((matrix[j*2 + i/16 + (i%2)*16]) & 0x0ff);
				data = (byte) (data  & (0x01 <<(7-(i/2)%8))); 
				 Debug.e(TAG, "i="+i+", j="+j+"-->"+data);
				if ( data != 0) {
					trans[i] |= (0x01 << j);
				}
			}
		}
		Debug.print("", trans);
		ByteBuffer b = ByteBuffer.wrap(trans);
		b.get(matrix);
	}
	*/
	/**
	 * 从 列字库 中 获得 点阵 数据  
	 * @param matrix
	 * addbylk170627 
	 */
	private void columnTransferSmallend(byte[] matrix) {
		if (matrix == null || matrix.length != 32) {
			return;
		}
		
		byte[] trans = new byte[32];

		for (int irow = 0; irow < trans.length; irow+=2)
		{
			byte dataBuf1 = (byte)((matrix[irow ]) & 0x0ff);			
			byte dataBuf2 = (byte)((matrix[irow+1 ]) & 0x0ff);	
		//	byte dataTemp;
			
		//	dataTemp = dataBuf1;
		//	dataBuf1 =dataBuf2;
		//	dataBuf2 = dataTemp;	
			for (int ibyte = 0; ibyte < 8; ibyte++)  
			{				
				byte data = (byte) (dataBuf1  & (0x01 << (7-ibyte) ) ); 
				//  Debug.e(TAG, "====irow="+irow+", j="+ibyte+"-->"+ibyte);
				if ( data != 0) 
				{
					trans[irow] |= (0x01 << ibyte);
				}
				  data = (byte) (dataBuf2  & (0x01 << (7-ibyte) ) ); 
				///  Debug.e(TAG, "====irow="+irow+", j="+ibyte+"-->"+ibyte);
				if ( data != 0) 
				{
					trans[irow+1] |= (0x01 << ibyte);
				}			
			}
		}
		Debug.print("", trans);
		ByteBuffer b = ByteBuffer.wrap(trans);
		b.get(matrix);
	}	
	
	private byte[] expendTo32Bit(byte[] sixteen) {
		if (sixteen == null) {
			return null;
		}
		byte[] bit_32 = new byte[4*16];//字模高  × 字模宽 
		
	//	for (int i = 0; i < sixteen.length; i+=2) {
	//		bit_32[2*i] = sixteen[i];
	//		bit_32[2*i+1] = sixteen[i+1];
	//	}
		 	for (int i = 0; i < sixteen.length; i+=2) {
				bit_32[2*i] = sixteen[i];
				bit_32[2*i+1] = sixteen[i+1];
			}		
		
		return bit_32;
	}
	
	private byte[] expendTo32Bit_width8(byte[] DZ_buffer)
	{
		if (DZ_buffer == null) {
			return null;
		}
		byte[] bit_32 = new byte[4*16];//字模高  × 字模宽 
		for (int i = 0; i < DZ_buffer.length; i+=2) //压缩掉的 列 数 8 
		{
		//	Debug.e(TAG, "====DZ_buffer.length-8="+(DZ_buffer.length-8) +"====i"+i );
			bit_32[2*i] = DZ_buffer[i];
			bit_32[2*i+1] = DZ_buffer[i+1];
		}
		return bit_32;
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
	
	
}

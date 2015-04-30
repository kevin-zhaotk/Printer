package com.industry.printer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.http.util.ByteArrayBuffer;

import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.data.BinCreater;

/**
 * @author zhaotongkai
 *
 */
public class BinInfo {
	public static final String TAG="BinInfo";
	
	private File mFile;
	private FileInputStream mFStream;
	/**bin文件的总列数**/
	public int mColumn;

	/**bin文件总长度**/
	public int mLength;
	/**bin文件每列的字节数**/
	public int mBytesPerColumn;
	
	/**bin文件每列的字符数**/
	public int mCharsPerColumn;
	
	/**变量的每个字符所占列数**/
	public int mColPerElement;
	/**bin文件的字节缓存**/
	public byte[] mBufferBytes;
	
	/**bin文件的字符缓存**/
	public char[] mBufferChars;

	public BinInfo(String file)
	{
		mColumn = 0;
		mBufferBytes = null;
		mBufferChars = null;
		/**读取文件头信息**/
		byte[] head = new byte[BinCreater.RESERVED_FOR_HEADER];
		mFile = new File(file);
		try {
			mFStream = new FileInputStream(mFile);
			mFStream.read(head, 0, BinCreater.RESERVED_FOR_HEADER);
			mColumn =  (head[0]&0xff) << 16 | (head[1] & 0xff)<<8 | (head[2]&0xff);
			
			//bin文件总长度
			mLength = mFStream.available();
			
			//文件的总字节数/总列数 = 每列的字节数
			mBytesPerColumn = mLength/mColumn;
			
			//文件的总字符数/总列数/2 = 每列的字符数
			mCharsPerColumn = mBytesPerColumn/2;
			
			//通过文件后缀是否带有v判断是否为变量的bin文件
			if (mFile.getName().contains("v")) {
				mColPerElement = mColumn/10;
			} else {
				mColPerElement = 0;
			}
		} catch (Exception e) {
			Debug.d(TAG, ""+e.getMessage());
		}
	}
	
	public int getCharsPerColumn() {
		return mCharsPerColumn;
	}
	
	public int getBytesPerColumn() {
		return mBytesPerColumn;
	}
	
    public char[] getBgBuffer()
    {
		try {
			mBufferBytes = new byte[mLength];
			mBufferChars = new char[mLength/2];
			if(mBufferBytes == null || mBufferChars == null)
				return null;
			mFStream.read(mBufferBytes, 0, mBufferBytes.length);
	    	//mFStream.close();
	    	//把byte[]存为char[]
	    	for(int i = 0; i < mBufferChars.length; i++) {
	    		mBufferChars[i] = (char) ((char)(mBufferBytes[2*i+1] << 8) | (mBufferBytes[2*i])); 
	    	}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}
    	return mBufferChars;//bmp.createScaledBitmap(bmp, columns, 150, true);
    }
    
    public char[] getVarBuffer(String var)
    {
    	int n;
    	mFStream.mark(BinCreater.RESERVED_FOR_HEADER);
    	byte[] buffer = new byte[mLength];
    	
    	try {
			mFStream.read(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	ByteArrayBuffer ba = new ByteArrayBuffer(0);
   		for(int i=0; i<var.length(); i++)
   		{
   			n = Integer.parseInt(var.substring(i, i+1));
   			ba.append(buffer, n*mColPerElement+16, mColPerElement*mBytesPerColumn);
   		}

   		mBufferBytes = ba.buffer();
   		//把byte[]存为char[]
   		if (mBufferChars == null) {
   			mBufferChars = new char[mBufferBytes.length/2];
   		}
    	for(int i = 0; i < mBufferChars.length; i++) {
    		mBufferChars[i] = (char) ((char)(mBufferBytes[2*i+1] << 8) | (mBufferBytes[2*i])); 
    	}
    	return mBufferChars;
    }
    
    
    public static void overlap(byte[] dst, byte[] src, int x, int high)
    {
    	int len = src.length;
    	if(dst.length < x*high +src.length)
    	{
    		Debug.d(TAG, "dst buffer no enough space!!!!");
    		len = dst.length - x*high;
    		//return;
    	}
    	for(int i = 0; i < len; i++)
    	{
    		dst[x*high+i] |= src[i];
    	}
    }
    
    public static void overlap(char[] dst, char[] src, int x, int high)
    {
    	int len = src.length;
    	if(dst.length < x*high + src.length)
    	{
    		Debug.d(TAG, "dst buffer no enough space!!!!");
    		len = dst.length - x*high;
    		//return;
    	}
    	for(int i=0; i< len; i++)
    	{
    		dst[x*high+i] |= src[i];
    	}
    }
    /**
     * 该函数用于对打印buffer进行字节变换，生成880设备的打印buffer
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
    
    /**
     * 该函数用于将列变换得到的打印buffer进行逆变换，从而得到预览buffer
     * @param buffer 打印buffer
     */
    public static void Matrix880Revert(byte[] buffer) {
    	byte[] tmp= new byte[110];
    	Debug.d(TAG, "===>Matrix880Revert : buffer len:"+buffer.length);
    	for(int i=0; i< buffer.length/(Configs.gDots/8); i++){
    		for(int j=0; j<Configs.gDots/(2*8); j++){
    			tmp[j] = buffer[i*(Configs.gDots/8)+2*j];
    			tmp[j+55] = buffer[i*(Configs.gDots/8)+2*j+1]; 
    		}
    		for(int j=0; j<Configs.gDots/8; j++){
    			buffer[i*(Configs.gDots/8)+j] = tmp[j];
    		}
    	}
    }
}

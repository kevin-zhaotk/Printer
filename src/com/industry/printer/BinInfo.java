package com.industry.printer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.http.util.ByteArrayBuffer;

import android.R.integer;

import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.Utils.PlatformInfo;
import com.industry.printer.data.BinCreater;

/**
 * @author zhaotongkai
 *
 */
public class BinInfo {
	public static final String TAG="BinInfo";
	
	private File mFile;
	private FileInputStream mFStream;
	
	private MessageTask mTask;
	/**bin文件的总列数**/
	public int mColumn;

	/**bin文件总长度**/
	public int mLength;
	
	/** 是否需要对列进行补偿 */
	public boolean mNeedFeed = false;
	
	/**打印头类型，单头，双头，三头和四头， 默认为单头*/
	public int mType = 1;
	
	/**每个打印头每列的字节数*/
	public int mBytesPerH;
	
	/**每个打印头每列的双字节数*/
	public int mCharsPerH;
	
	/**每个打印头每列的字节数*/
	public int mBytesPerHFeed;
	
	/**每个打印头每列的双字节数*/
	public int mCharsPerHFeed;
	
	/**bin文件每列的总字节数**/
	public int mBytesPerColumn;
	
	/**bin文件每列的总字符数**/
	public int mCharsPerColumn;
	
	/**补偿之后的列总字节数*/
	public int mBytesFeed;
	
	/**补偿之后的列总双字节数*/
	public int mCharsFeed;
	
	/**变量的每个字符所占列数**/
	public int mColPerElement;
	
	/**bin文件的字节缓存**/
	public byte[] mBufferBytes;
	
	/**bin文件的字符缓存**/
	public char[] mBufferChars;
	
	public byte[] mBuffer;
	public ByteArrayInputStream mCacheStream;

	public BinInfo(String file) {
		this(file, 1);
	}
	
	public BinInfo(String file, int type)
	{
		mColumn = 0;
		mBufferBytes = null;
		mBufferChars = null;
		mType = type;
		/**读取文件头信息**/
		byte[] head = new byte[BinCreater.RESERVED_FOR_HEADER];
		mFile = new File(file);
		try {
			mFStream = new FileInputStream(mFile);
			mBuffer = new byte[mFStream.available()];
			mFStream.read(mBuffer);
			mFStream.close();
			/*把bin文件内容读入内存*/
			mCacheStream = new ByteArrayInputStream(mBuffer);
			
			mCacheStream.read(head, 0, BinCreater.RESERVED_FOR_HEADER);
			mColumn =  (head[0]&0xff) << 16 | (head[1] & 0xff)<<8 | (head[2]&0xff);
			
			//bin文件总长度
			mLength = mCacheStream.available();
			
			if (type <=0 || type > 4) {
				mType = 1;
			}
			
			//文件的总字节数/总列数 = 每列的字节数
			mBytesPerColumn = mLength/mColumn;
			Debug.d(TAG, "--->mBytesPerColumn =" + mBytesPerColumn);
			//文件的总字符数/总列数/2 = 每列的字符数
			mCharsPerColumn = mBytesPerColumn/2;
			/*如果mBytesPerColumn不是type的整数倍，说明这个bin文件不是一个合法的bin文件
			 *那么我们不会保证打印结果是否正确，所以这里不需要容错
			 */
			mBytesPerH = mBytesPerColumn/mType;
			Debug.d(TAG, "--->mBytesPerH =" + mBytesPerH + ", type=" + mType);
			mCharsPerH = mBytesPerH/2;
			/* 如果每列的字节数为奇数则 +1 变为偶数， 以便于FPGA处理*/
			if (mBytesPerH%2 != 0) {
				mNeedFeed = true;
			}

			/** 计算补偿后的字节数和双字节数 */
			if (mNeedFeed) {
				mBytesPerHFeed = mBytesPerH + 1;
				mBytesFeed = mBytesPerColumn + mType;
				Debug.d(TAG, "--->117 mBytesPerHFeed =" + mBytesPerHFeed + ", mBytesPerFeed=" + mBytesFeed);
			} else {
				mBytesPerHFeed = mBytesPerH;
				mBytesFeed = mBytesPerColumn;
				Debug.d(TAG, "--->120 mBytesPerHFeed =" + mBytesPerHFeed + ", mBytesPerFeed=" + mBytesFeed);
			}
			mCharsPerHFeed = mBytesPerHFeed/2;
			mCharsFeed = mBytesFeed/2;
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
	
	public int getBytesFeed() {
		return mBytesFeed;
	}
	
	public int getCharsFeed() {
		return mCharsFeed;
	}
	
	public int getBytesPerH() {
		return mBytesPerH;
	}
	
	public int getBytesPerHFeed() {
		return mBytesPerHFeed;
	}
	
	public int getCharsPerH() {
		return mCharsPerH;
	}
	
	public int getCharsPerHFeed() {
		return mCharsPerHFeed;
	}
	
    public char[] getBgBuffer()
    {
    	if (mLength <= 0) {
			return null;
		}
    	
		try {
			/*计算整个buffer需要补偿的字节数*/
			int feed = (mNeedFeed==true?mColumn*mType : 0);
			mBufferBytes = new byte[mLength + feed];
			mBufferChars = new char[(mLength + feed)/2];
			if(mBufferBytes == null || mBufferChars == null)
				return null;
			// int bytesPer = mBytesPerColumn + (mNeedFeed==true? mType : 0);
			/** 从当前位置读取mBytesPerH个字节到背景buffer中，由于需要处理多头情况，所以要注意在每个头的列尾要注意补偿问题（双字节对齐）*/
			for(int i=0; i < mColumn; i++) {
				for (int j = 0; j < mType; j++) {
					mCacheStream.read(mBufferBytes, i*mBytesFeed + j*mBytesPerHFeed, mBytesPerH);
				}
				
			}
	    	//mFStream.close();
			/* 如果是奇数列在每列最后添加一个byte */
			
	    	//把byte[]存为char[]
	    	for(int i = 0; i < mBufferChars.length; i++) {
	    		mBufferChars[i] = (char) (((char)(mBufferBytes[2*i+1] << 8) & 0x0ff00) | (mBufferBytes[2*i] & 0x0ff)); 
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
    	return mBufferChars; // bmp.createScaledBitmap(bmp, columns, 150, true);
    }
    
    public char[] getVarBuffer(String var)
    {
    	int n;
    	byte[] feed = {0};
    	if (mCacheStream == null) {
			return null;
		}
    	mCacheStream.mark(BinCreater.RESERVED_FOR_HEADER);
    	byte[] buffer = new byte[mLength];
    	
    	try {
    		mCacheStream.read(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	ByteArrayBuffer ba = new ByteArrayBuffer(0);
   		for(int i=0; i<var.length(); i++)
   		{
   			n = Integer.parseInt(var.substring(i, i+1));
   			for (int k = 0; k < mColPerElement; k++) {
   				for (int j = 0; j < mType; j++) {
   	   				ba.append(buffer, n*mColPerElement+16 + k* mColPerElement + mBytesPerHFeed * mType, mBytesPerH);
   	   	   			if (mNeedFeed) {
   	   					ba.append(feed, 0, 1);
   	   				}
   				}
			}
   		}

   		mBufferBytes = ba.buffer();
   		//把byte[]存为char[]
   		if (mBufferChars == null) {
   			mBufferChars = new char[mBufferBytes.length/2];
   		}
   		Debug.d(TAG, "---->charsize:" + mBufferChars.length + ", byte:" + mBufferBytes.length);
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
    	boolean matrix = PlatformInfo.isBufferFromDotMatrix();
    	for(int i=0; i< len; i++)
    	{
    		if (matrix) {
    			dst[x*high + i] = src[i];
    		} else {
    			dst[x*high + i] |= src[i];
    		}
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

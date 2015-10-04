package com.industry.printer.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.util.ByteArrayBuffer;

import com.industry.printer.R;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.widget.Toast;

public class BinCreater {
	
	public static final String TAG="BinCreater";
	
	//public static final String FILE_PATH="/storage/external_storage/sda1";
	
	
	public static final int RESERVED_FOR_HEADER=16;
	public static int mBmpBytes[];
	public static byte mBmpBits[];
	
	public static void create(Bitmap bmp, int colEach)
	{
		Bitmap scaledImg = scaleHeight(bmp, bmp.getHeight()																																					);
		mBmpBytes = new int[scaledImg.getByteCount()/2];
		mBmpBits = new byte[scaledImg.getWidth()*(scaledImg.getHeight()%8==0 ? scaledImg.getHeight()/8 : scaledImg.getHeight()/8+1)];
		Debug.d(TAG, "width="+scaledImg.getWidth()+", height="+scaledImg.getHeight()+", mBmpBits="+mBmpBits.length);
		convertGreyImg(scaledImg);
		BinCreater.recyleBitmap(scaledImg);
		//Debug.d(TAG, "width *height="+img.getWidth() * img.getHeight());
		//Debug.d(TAG, "byteCount="+img.getByteCount());
		
		//img.getPixels(mBmpBytes, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        
	}
	
	
	public static Bitmap scaleHeight(Bitmap bmp,  int h)
	{
		int width = bmp.getWidth();         
		int height = bmp.getHeight();        
        //Matrix matrix = new Matrix();
        //matrix.postScale(1, ((float)h)/height);
		Debug.d(TAG, "scaleHeight width="+width+", height="+height);
        Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, width, h, true);
        recyleBitmap(bmp);
        return scaledBmp;
    	//return Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
	}
	  
    public static void convertGreyImg(Bitmap bmp) { 
    	int width = bmp.getWidth();         
        int height = bmp.getHeight(); 
        
        int []pixels = new int[width * height]; 
        int colEach = height%8==0?height/8:height/8+1;
        Debug.d(TAG, "=====width="+width+", height="+height+", colEach="+colEach);
        /*move to saveBin for head info fill*/
        //mBmpBits[2] = (byte) (width & 0x0ff);
    	//mBmpBits[1] = (byte) ((width>>8) & 0x0ff);
    	//mBmpBits[0] = (byte) ((width>>16) & 0x0ff);
        
        bmp.getPixels(pixels, 0, width, 0, 0, width, height); 
        //int alpha = 0x00 << 24;  
        for(int i = 0; i < height; i++)  { 
            for(int j = 0; j < width; j++) { 
                int grey = pixels[width * i + j]; 
                
                int red = ((grey  & 0x00FF0000 ) >> 16); 
                int green = ((grey & 0x0000FF00) >> 8); 
                int blue = (grey & 0x000000FF);
                /*`
                if((grey & 0xFF000000) != 0)
                {
                	pixels[width * i + j] = 1;
                }
                else
                {
                	grey = (int)((float) red * 0.3 + (float)green * 0.59 + (float)blue * 0.11);
                	pixels[width * i + j] = 0;
                }
                */
                //grey = alpha | ((grey >128?255:0) << 16) | ((grey >128?255:0) << 8) | (grey >128?255:0); //grey >128?255:0; //
                //grey = alpha | (grey  << 16) | (grey  << 8) | grey ; 
                grey = (int)((float) red * 0.3 + (float)green * 0.59 + (float)blue * 0.11);
                pixels[width * i + j] = grey>128? 0x0:0xffffff;
                if(grey>128)
                	mBmpBits[j*colEach+i/8] &= ~(0x01<<(i%8));
                else
                	mBmpBits[j*colEach+i/8] |= 0x01<<(i%8); 
                //Debug.d(TAG, "pixels["+(width * i + j)+"]=0x" + Integer.toHexString(pixels[width * i + j]));
            }
        }
        //saveBin("/mnt/usb/1.bin", width,height);
        /*swap the high 8bits with low 8bits*/
        //swap(height);
        //Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565); 
        //result.setPixels(pixels, 0, width, 0, 0, width, height);
        /*just for debug*/
        //saveBitmap(result, "bk.png");
        
        return ; 
    }
    
    /*
     * height - pixes per column
     */
    public static void swap(int height)
    {
    	byte tmp;
    	int bytes = height%8==0?height/8:height/8+1;
    	int width = mBmpBits.length/bytes;
    	for(int i=0;i<mBmpBits.length/2; i++)
		{
			tmp = mBmpBits[2*i+1];
    		mBmpBits[2*i+1] = mBmpBits[2*i];
    		mBmpBits[2*i] = tmp;
		}
    	/*
    	for(int i=0; i<width; i++)
    	{
    		for(int j=0;j<height/2; j++)
    		{
    			tmp = mBmpBits[height*i+2*j+1];
        		mBmpBits[height*i+2*j+1] = mBmpBits[height*i+2*j];
        		mBmpBits[height*i+2*j] = tmp;
    		}
    		
    	}
    	*/
    }
    
    public static void saveBitmap(Bitmap bmp, String picName)
    {
    	File f = new File(Configs.USB_ROOT_PATH, picName);
    	//File f = new File("/storage/external_storage/sda1", picName);
    	if(f.exists())
    	{
    		f.delete();
    	}
    	try{
    		FileOutputStream out = new FileOutputStream(f);
    		bmp.compress(CompressFormat.PNG, 90, out);
    		out.flush();
    		out.close();
    		Debug.d(TAG, "PNG save ok");
    	}catch(Exception e)
    	{
    		Debug.d(TAG, "save failed: "+e.getMessage());
    	}
    }
    
    public void saveBytes(int[] map, String fileName)
    {
    	try{
    		//File file = new File("/storage/external_storage/sda1", "2.bin");
        	File file = new File(Configs.USB_ROOT_PATH, fileName);
    		FileOutputStream fs = new FileOutputStream(file);
    		//ByteArrayOutputStream barr = new ByteArrayOutputStream();
    		for(int i=0; i< map.length; i++)
    			fs.write(map[i]);
    		fs.flush();
    		fs.close();
    	}catch(Exception e)
    	{
    		Debug.d(TAG, "Exception: "+e.getMessage());
    		//return false;
    	}
    	//return true;
        
	}
    
    
    public static boolean byte2bit(int height)
    {
    	//int width= mBmpBytes.length/880;
    	int rows=mBmpBytes.length/height;
    	if(mBmpBytes == null || mBmpBits==null || mBmpBits.length < mBmpBytes.length/8)
    	{
    		Debug.d(TAG, "There is no enough space for store bmpbits");
    		return false;
    	}
    	Debug.d(TAG, "rows="+rows+",cols="+height);
    	//int bytesPercol = height%8==0 ? height/8 : (height/8+1);
    	mBmpBits[2] = (byte) (rows & 0x0ff);
    	mBmpBits[1] = (byte) ((rows>>8) & 0x0ff);
    	mBmpBits[0] = (byte) ((rows>>16) & 0x0ff);
    	
    	
    	/*
    	mBmpBits[8] = (byte) (colEach & 0x0ff);
    	mBmpBits[7] = (byte) ((colEach>>8) & 0x0ff);
    	mBmpBits[6] = (byte) ((colEach>>16) & 0x0ff);
    	*/
    	for(int k=0; k<rows; k++)
    	{
    		for(int i=0; i<height; i++)
    		{
    			//Debug.d(TAG, "mBmpBytes["+(i*width+k)+"]=0x"+Integer.toHexString( mBmpBytes[i*width+k]));
        			if((mBmpBytes[i*rows+k] & 0x00ffffff) != 0)
        			{
        				mBmpBits[(k*height)/8 +i/8+RESERVED_FOR_HEADER] |= 0x01<<(i%8);
        			}
        			else
        			{
        				mBmpBits[(k*height)/8 +i/8+RESERVED_FOR_HEADER] &= ~(0x01<<(i%8));
        			}
        			if(k<3)
        				Debug.d(TAG, "mBytes["+(i*rows+k)+"] <--> "+"mBmpBits["+((k*height)/8 +i/8+RESERVED_FOR_HEADER)+"]["+(i%8)+"]");	
    		}
    	}
    	return true;
    }
    
    public static boolean saveBin(String f, int width)
    {
    	byte head[]=new byte[16];
    	Debug.d(TAG, "+++++++++++++saveBin");
    	Debug.d(TAG, "saveBin f="+f+", width="+width);
    	/*save column-width*/
    	head[2] = (byte) (width & 0x0ff);
    	head[1] = (byte) ((width>>8) & 0x0ff);
    	head[0] = (byte) ((width>>16) & 0x0ff);
    	
    	try{
    		File file = new File(f);
    		FileOutputStream fs = new FileOutputStream(f);
    		ByteArrayOutputStream barr = new ByteArrayOutputStream();
    		barr.write(head);
    		barr.write(mBmpBits,0,mBmpBits.length);
    		barr.writeTo(fs);
    		fs.flush();
    		fs.close();
    		barr.close();
    	}catch(Exception e)
    	{
    		Debug.d(TAG, "Exception: "+e.getMessage());
    		return false;
    	}
    	Debug.d(TAG, "+++++++++++++saveBin");
    	return true;
    }
    
    /**
     * 保存bin文件
     * @param f bin文件路径
     * @param width bin文件总列数，单位byte
     * @param single bin文件单列高度，单位bit
     */
    public static boolean saveBin(String f, int width, int single)
    {
    	byte head[]=new byte[16];
    	
    	/*save column-width*/
    	head[2] = (byte) (width & 0x0ff);
    	head[1] = (byte) ((width>>8) & 0x0ff);
    	head[0] = (byte) ((width>>16) & 0x0ff);
    	    	
    	/*save width of single element*/
    	head[5] = (byte) (single & 0x0ff);
    	head[4] = (byte) ((single>>8) & 0x0ff);
    	head[3] = (byte) ((single>>16) & 0x0ff);
    	
    	Debug.d(TAG, "+++++++++++++saveBin Var");
    	Debug.d(TAG, "saveBin f="+f+", width="+width+" ,single="+single);
    	try{
    		File file = new File(f);
    		FileOutputStream fs = new FileOutputStream(f);
    		ByteArrayOutputStream barr = new ByteArrayOutputStream();
    		barr.write(head);
    		barr.write(mBmpBits,0,mBmpBits.length);
    		barr.writeTo(fs);
    		fs.flush();
    		fs.close();
    		barr.close();
    	}catch(Exception e)
    	{
    		Debug.d(TAG, "Exception: "+e.getMessage());
    		return false;
    	}
    	Debug.d(TAG, "+++++++++++++saveBin var");
    	return true;
    }
    
    /**
     * 保存bin文件
     * @param f bin文件存放路径
     * @param dots 点阵buffer
     * @param single bin文件单列高度，单位bit
     * @return 保存成功返回true，保存失败返回false
     */
    public static boolean saveBin(String f, byte[] dots, int single) {
    	int bytesPerCol = 0;
    	bytesPerCol = single%8==0? single/8 : (single/8+1);
    	int columns = dots.length/bytesPerCol;
    	try {
    		
    		File file = new File(f);
    		Debug.d(TAG, "--->saveBin f:" + file.getAbsoluteFile());
    		if (!file.exists() && !file.createNewFile()) {
				Debug.d(TAG, "===>error: create bin file failed");
				return false;
			}
			FileOutputStream stream = new FileOutputStream(file);
			byte head[]=new byte[16];
	    	head[2] = (byte) (columns & 0x0ff);
	    	head[1] = (byte) ((columns>>8) & 0x0ff);
	    	head[0] = (byte) ((columns>>16) & 0x0ff);
	    	
	    	/*save width of single element*/
	    	head[5] = (byte) (single & 0x0ff);
	    	head[4] = (byte) ((single>>8) & 0x0ff);
	    	head[3] = (byte) ((single>>16) & 0x0ff);
	    	stream.write(head);
			stream.write(dots);
			stream.flush();
			stream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Debug.d(TAG, "===>saveBin err:"+e.getMessage());
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Debug.d(TAG, "===>saveBin err:"+e.getMessage());
			return false;
		}
    	return true;
    }
    
    
    public static Bitmap Bin2Bitmap(byte []map)
    {
    	int k=0;
    	Bitmap bmp;
    	
		Debug.d(TAG, "map[0]="+map[0]+",map[1]="+map[1]+", map[2]="+map[2]);
		Debug.d(TAG, "map[3]="+map[3]+",map[4]="+map[4]+", map[5]="+map[5]);
    	int grey =0;
    	int columns =  (map[0]&0xff) << 16 | (map[1] & 0xff)<<8 | (map[2]&0xff);
    	int row = (map[3]&0xff) << 16 | (map[4] & 0xff)<<8 | (map[5]&0xff);
    	int pixels[] = new int[columns*row];
    	Debug.d(TAG, "columns = "+columns+", row="+row);
    	/*110 bytes per Row*/
    	for(int i=0; i< columns; i++)
    	{
    		for(int j=0; j<row; j++)
    		{
    			if( (map[i*(row/8) + j/8+RESERVED_FOR_HEADER]&(0x01 <<(7-j%8))) != 0)
    				grey = 0x0;
    			else 
    				grey = 0xff;
    			pixels[j*columns+i] = 0xff<<24 | grey <<16 | grey<<8 | grey;
    		}
    	}
    	Debug.d(TAG, "===============");
    	bmp = Bitmap.createBitmap(pixels, columns, row, Config.ARGB_8888);
    	Debug.d(TAG, "===============000000");
    	//bmp.setPixels(pixels, 0, columns, 0, 0, columns, row);
    	Debug.d(TAG, "===============111111");
    	return bmp.createScaledBitmap(bmp, columns, 150, true);
    }
    
    public static byte[] getBinBuffer(int n, String f)
    {
    	byte[] buffer=null;
    	try{
    		File file = new File(f);
    		if(!file.exists() || !file.isFile())
    		{
    			Debug.d(TAG, "file exist ? "+file.exists() + ", isfile? "+file.isFile());
    			return null;
    		}
    		/*
    		 *read out the width of each number
    		 */
    		buffer = new byte[(int) file.length()];
    		FileInputStream fs = new FileInputStream(file);
    		fs.read(buffer);
    		int len = buffer[6] << 16| buffer[7] <<8 | buffer[8];
    		ByteArrayBuffer ba = new ByteArrayBuffer(len*110*Integer.bitCount(n));
    		Debug.d(TAG, "---------len ="+len+", n="+n);
    		n = Integer.reverse(n);
    		Debug.d(TAG, "-------reversed n="+n);
    		for(;;)
    		{
    			int i = Integer.lowestOneBit(n);
    			Debug.d(TAG, "-------i="+i);
    			ba.append(buffer, i*len*110+16, len*110);
    			if(Integer.bitCount(n)<=1)
    				break;
    			else {
					n = n/10;
				}
    		}
    		return ba.buffer();
    	}
    	catch(Exception e)
    	{
    		Debug.d(TAG, "Exception e:"+e.getMessage());
    	}
    	return null;
    }
    
    public static void bin2byte(byte[] srcBits, int[] dstBytes)
    {
    	int grey=0;
    	Bitmap bmp=null;
    	if(srcBits == null || srcBits.length==0)
    	{
    		Debug.d(TAG, "==>bin2byte  srcBits len="+srcBits.length);
    		return;
    	}
    	int bpc = Configs.gDots%8==0?Configs.gDots/8:(Configs.gDots/8+1);
    	int columns = srcBits.length/bpc;
    	
    	if(dstBytes==null || dstBytes.length<srcBits.length*8) {
    		dstBytes = new int[srcBits.length*8];
    	}
    		
    	for(int i=0; i< columns; i++)
    	{
    		for(int j=0; j<bpc*8; j++)
    		{
    			if( (srcBits[i*bpc + j/8]&(0x01 <<(j%8))) != 0)
    				grey = 0x0;
    			else 
    				grey = 0xff;
    			dstBytes[j*columns+i] = 0xff<<24 | grey <<16 | grey<<8 | grey;
    		}
    	}
    	//bmp = Bitmap.createBitmap(pixels, columns, 880, Config.ARGB_8888);
    	//Debug.d(TAG, "bin2byte  bmp width="+bmp.getWidth()+", pixels="+pixels.length+", bm.len="+bm.length);
    	//bmp.setPixels(pixels, 0, columns, 0, 0, columns, 880);
    	//return bmp.createScaledBitmap(bmp, columns, 150, true);
    	return ;
    }
    
    
    
    public static void recyleBitmap(Bitmap bmp)
    {
    	if(!bmp.isRecycled())
		{
			bmp.recycle();
			System.gc();
		}
    }
}

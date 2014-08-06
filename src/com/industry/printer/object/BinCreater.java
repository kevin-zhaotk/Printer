package com.industry.printer.object;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.http.util.ByteArrayBuffer;

import com.industry.printer.Utils.Debug;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

public class BinCreater {
	
	public static final String TAG="BinCreater";
	
	//public static final String FILE_PATH="/storage/external_storage/sda1";
	public static final String FILE_PATH="/mnt/usb/";
	
	public static final int RESERVED_FOR_HEADER=16;
	public static int mBmpBytes[];
	public static byte mBmpBits[];
	
	public static void create(Bitmap bmp, int colEach)
	{
		mBmpBytes = new int[bmp.getByteCount()/2];
		mBmpBits = new byte[bmp.getWidth()*(bmp.getHeight()%8==0 ? bmp.getHeight()/8 : bmp.getHeight()/8+1)];
		
		Bitmap img =	convertGreyImg(bmp);
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
        return Bitmap.createScaledBitmap(bmp, width, h, true);
    	//return Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
	}
	  
    public static Bitmap convertGreyImg(Bitmap bmp) { 
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
        /*swap the high 8bits with low 8bits*/
        swap(height);
        Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565); 
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        /*just for debug*/
        //saveBitmap(result, "bk.png");
        //saveBin("/mnt/usb/1.bin", width);
        return result; 
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
    	File f = new File(BinCreater.FILE_PATH, picName);
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
        	File file = new File(FILE_PATH, fileName);
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
    	head[2] = (byte) (width & 0x0ff);
    	head[1] = (byte) ((width>>8) & 0x0ff);
    	head[0] = (byte) ((width>>16) & 0x0ff);
    	try{
    		File file = new File(f);
    		FileOutputStream fs = new FileOutputStream(file);
    		ByteArrayOutputStream barr = new ByteArrayOutputStream();
    		barr.write(head);
    		barr.write(mBmpBits,16,mBmpBits.length);
    		barr.writeTo(fs);
    		fs.flush();
    		fs.close();
    	}catch(Exception e)
    	{
    		Debug.d(TAG, "Exception: "+e.getMessage());
    		return false;
    	}
    	return true;
    }
    
    public static Bitmap Bin2Bitmap(byte []map)
    {
    	int k=0;
    	Bitmap bmp;
    	
		
    	int grey =0;
    	int columns =  (map[0]&0xff) << 16 | (map[1] & 0xff)<<8 | (map[2]&0xff);
    	int row = (map[3]&0xff) << 16 | (map[4] & 0xff)<<8 | (map[5]&0xff);
    	int pixels[] = new int[columns*row];
    	Debug.d(TAG, "columns = "+Integer.toHexString(columns));
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
    	bmp = Bitmap.createBitmap(pixels, columns, row, Config.ARGB_8888);
    	bmp.setPixels(pixels, 0, columns, 0, 0, columns, 880);
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
    		Debug.d(TAG, "len ="+len);
    		ByteArrayBuffer ba = new ByteArrayBuffer(len*110);
    		ba.append(buffer, n*len*110+16, len*110);
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
    	if(srcBits == null)
    		return;
    	int columns = srcBits.length/110;
    	if(dstBytes==null || dstBytes.length<srcBits.length*8)
    		return;
    	//int [] pixels = new int[srcBits.length*8];
    	for(int i=0; i< columns; i++)
    	{
    		for(int j=0; j<880; j++)
    		{
    			if( (srcBits[i*110 + j/8]&(0x01 <<(7-j%8))) != 0)
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
    
    public static void overlap(byte[] dst, byte[] src, int x, int y, int high)
    {
    	Debug.d(TAG, "dst.len="+dst.length+", src.len="+src.length);
    	Debug.d(TAG, "x = "+x+", high="+high);
    	int len = src.length;
    	if(dst.length < x*high/8 +src.length)
    	{
    		Debug.d(TAG, "dst buffer no enough space!!!!");
    		len = dst.length - x*high/8;
    		//return;
    	}
    	for(int i=0; i< len; i++)
    	{
    		dst[x*high/8+i] |= src[i];
    	}
    }
}

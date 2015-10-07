package com.industry.printer.data;

import com.industry.printer.Utils.Debug;

import android.graphics.Bitmap;


/**
 * bitmap图片提取点阵的步骤如下：
 * 1、对图片进行灰度化处理
 * @author kevin
 *
 */
public class BinFromBitmap extends BinCreater {

	public void extract(Bitmap bmp) {
		
	}
	
	public void convertGreyImg(Bitmap bmp) { 
    	int width = bmp.getWidth();         
        int height = bmp.getHeight(); 
        
        int []pixels = new int[width * height]; 
        int colEach = height%8==0?height/8:height/8+1;
        Debug.d(TAG, "=====width="+width+", height="+height+", colEach="+colEach);
        
        
        bmp.getPixels(pixels, 0, width, 0, 0, width, height); 
        //int alpha = 0x00 << 24;  
        for(int i = 0; i < height; i++)  { 
            for(int j = 0; j < width; j++) { 
                int grey = pixels[width * i + j]; 
                
                int red = ((grey  & 0x00FF0000 ) >> 16); 
                int green = ((grey & 0x0000FF00) >> 8); 
                int blue = (grey & 0x000000FF);
                
                grey = (int)((float) red * 0.3 + (float)green * 0.59 + (float)blue * 0.11);
                pixels[width * i + j] = grey>128? 0x0:0xffffff;
                if(grey>128)
                	mBmpBits[j*colEach+i/8] &= ~(0x01<<(i%8));
                else
                	mBmpBits[j*colEach+i/8] |= 0x01<<(i%8); 
                //Debug.d(TAG, "pixels["+(width * i + j)+"]=0x" + Integer.toHexString(pixels[width * i + j]));
            }
        }
        
        return ; 
    }
}

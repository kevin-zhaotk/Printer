package com.industry.printer.object;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.industry.printer.ImageConverter;
import com.industry.printer.Utils.Debug;
import com.industry.printer.data.BinCreater;

public class GraphicObject  extends BaseObject{

	public static String TAG="GraphicObject";
	
	public Bitmap mBitmap;
	
	public GraphicObject(Context context) {
		super(context, BaseObject.OBJECT_TYPE_GRAPHIC, 0);
		// TODO Auto-generated constructor stub
	}
	
	public GraphicObject(Context context, float x) {
		super(context, BaseObject.OBJECT_TYPE_GRAPHIC, x);
		// TODO Auto-generated constructor stub
	}

	public void setImage(String file)
	{
		File f = new File(file);
		if( !f.isFile() || !isPic(file))
		{
			Debug.d(TAG, "please select a correct file");
			return;
		}
		Debug.d(TAG, "setImage file: "+file);
		mContent = file;
		Bitmap b = BitmapFactory.decodeFile(mContent);
		mBitmap = ImageConverter.convertGreyImg(b);
		BinCreater.recyleBitmap(b);
	}
	
	public Bitmap getScaledBitmap(Context context)
	{
		return mBitmap;
	}
	
	public static String[] pic_formats={".png", ".jpg", ".jpeg"};
	
	public static boolean isPic(String f)
	{
		if(f==null)
			return false;
		int index = f.lastIndexOf(".");
		if(index<0 || index >= f.length())
			return false;
		String suffix = f.substring(index);
		for(String s : pic_formats)
		{
			if(s.equalsIgnoreCase(suffix))
				return true;
		}
		return false;
	}
}

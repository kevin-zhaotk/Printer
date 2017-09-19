package com.industry.printer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.industry.printer.FileFormat.SystemConfigFile;
import com.industry.printer.FileFormat.TlkFileWriter;
import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.Utils.FileUtil;
import com.industry.printer.Utils.PlatformInfo;
import com.industry.printer.data.BinFileMaker;
import com.industry.printer.data.BinFromBitmap;
import com.industry.printer.object.BarcodeObject;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.CounterObject;
import com.industry.printer.object.GraphicObject;
import com.industry.printer.object.JulianDayObject;
import com.industry.printer.object.MessageObject;
import com.industry.printer.object.ObjectsFromString;
import com.industry.printer.object.RealtimeDate;
import com.industry.printer.object.RealtimeHour;
import com.industry.printer.object.RealtimeMinute;
import com.industry.printer.object.RealtimeMonth;
import com.industry.printer.object.RealtimeObject;
import com.industry.printer.object.RealtimeYear;
import com.industry.printer.object.ShiftObject;
import com.industry.printer.object.TLKFileParser;
import com.industry.printer.object.TextObject;
import com.industry.printer.object.TlkObject;
import com.industry.printer.object.data.BitmapWriter;


import java.io.BufferedInputStream;  
import java.io.BufferedOutputStream;  
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  

/**
 * MessageTask包含多个object
 * @author zhaotongkai
 *
 */
public class MessageTask {


	private File mFile;
	private FileInputStream mFStream;
	
	private MessageTask mTask;
 

	
	public byte[] bin;
	public ByteArrayInputStream mCacheStream;
	
	
	
	private static final String TAG = MessageTask.class.getSimpleName();
	public static final String MSG_PREV_IMAGE = "/1.bmp";
	private Context mContext;
	private int mDots=0; 
	private String mName;
	private ArrayList<BaseObject> mObjects;

	public int mType;
	private int mIndex;
	public SystemConfigFile mSysconfig; //addbylk0618
	public MessageTask(Context context) {
		mName="";
		mContext = context;
		mObjects = new ArrayList<BaseObject>();
		mSysconfig = SystemConfigFile.getInstance(mContext);//addbylk0618
	}
	
	/**
	 * 通过tlk文件解析构造出Task
	 * @param tlk
	 */
	public MessageTask(Context context, String tlk) {
		this(context);
		String tlkPath="";
		if (tlk.startsWith(ConfigPath.getTlkPath())) {
			File file = new File(tlk);
			setName(file.getName());
		} else {
			setName(tlk);
		}
		
		TLKFileParser parser = new TLKFileParser(context, mName);
		parser.parse(context, this, mObjects);                                      
		mDots = parser.getDots();
	}
	
	/**
	 * 通过字符串解析构造出Task，
	 * @param tlk tlk信息名称
	 * @param content 树莓3上编辑的字符串内容
	 */
	public MessageTask(Context context, String tlk, String content) {
		this(context);
		String tlkPath="";
		setName(tlk);
		
		mObjects = ObjectsFromString.makeObjs(mContext, content);
	}
	/**
	 * set Task name
	 * @param name
	 */
	public void setName(String name) {
		mName = name;
	}
	
	/**
	 * get Task name
	 * @return
	 */
	public String getName() {
		return mName;
	}
	
	public ArrayList<BaseObject> getObjects() {
		Debug.d(TAG, "--->size:" + mObjects.size());
		return mObjects;
	}
	/**
	 * 添加object到Task
	 * @param object
	 */
	public void addObject(BaseObject object) {
		if (mObjects == null) {
			mObjects = new ArrayList<BaseObject>();
		}
		object.setTask(this);
		mObjects.add(object);
		Debug.d(TAG, "--->size:" + mObjects.size());
	}
	
	/**
	 * 从Task中删除指定object
	 * @param object
	 */
	public void removeObject(BaseObject object) {
		if (mObjects == null) {
			return;
		}
		mObjects.remove(object);
	}
	
	/**
	 * 删除Task中所有objects
	 */
	public void removeAll() {
		if (mObjects == null) {
			return;
		}
		mObjects.clear();
	}
	
	/**
	 * 获取当前打印信息的墨点数
	 * 此API只对树莓小板系统有效
	 * @return
	 */
	public int getDots() {
		return mDots;
	}
	
	/**
	 * 获取打印对象的内容缩略信息
	 * 暂时只支持文本对象，后续会添加对变量的支持
	 */
	public String getAbstract() {
		
		String objString="";
		String content="";
		if (mObjects == null) {
			return null;
		}
		for (BaseObject item : mObjects) {
			if (item instanceof TextObject) {
        		objString = item.getContent();
        	} else if (item instanceof RealtimeObject) {
				objString = item.getContent();
			} else if (item instanceof CounterObject) {
				objString = item.getContent();
			} else {
				continue;
			}
        	content += objString;
		}
		return content;
	}
	
	public void saveTlk(Context context) {
		for(BaseObject o:mObjects)
		{
			if((o instanceof MessageObject)	) {
				((MessageObject) o).setDotCount(mDots);
				break;
			}
		}
		TlkFileWriter tlkFile = new TlkFileWriter(context, this);
		tlkFile.write();
	}
	
	public boolean createTaskFolderIfNeed() {
		File dir = new File(ConfigPath.getTlkDir(mName));
		if(!dir.exists() && !dir.mkdirs())
		{
			Debug.d(TAG, "create dir error "+dir.getPath());
			return false;
		}
		return true;
	}
	
	public void saveVarBin() 
	{
		int N=mSysconfig.getParam(42);//设置 43
		if( N==0)
		{		
			if (mObjects == null || mObjects.size() <= 0) {
				return;
			}
			for (BaseObject object : mObjects) {
				if( 
					//	(object instanceof RealtimeObject) ||
						(object instanceof JulianDayObject) || (object instanceof ShiftObject)	)
				{
					if(PlatformInfo.isBufferFromDotMatrix()!=0) {
						
						object.generateVarbinFromMatrix(ConfigPath.getTlkDir(mName),object.mHeight,object.mWidth);
						Debug.e(TAG, ConfigPath.getTlkDir(mName)   );						
					} else {
						Debug.e(TAG, "===mDots += object.drawVarBitmap()     "  );	
						mDots += object.drawVarBitmap();
					}
				} else if (object instanceof BarcodeObject && object.getSource() == true) 
				{
					int dots = ((BarcodeObject) object).getDotcount();
					MessageObject msg = getMsgObject();
					if (msg.getType() == MessageType.MESSAGE_TYPE_1_INCH_FAST 
							|| msg.getType() == MessageType.MESSAGE_TYPE_1_INCH ) {
						mDots += dots * 2;
					} else if (msg.getType() == MessageType.MESSAGE_TYPE_1_INCH_DUAL 
							|| msg.getType() == MessageType.MESSAGE_TYPE_1_INCH_DUAL_FAST ) {
						mDots += dots * 2 * 4;
					} else {
						mDots += dots/2;
					}
					
				}
			}
		}else
		{
			if (mObjects == null || mObjects.size() <= 0) {
				return;
			}
			for (BaseObject object : mObjects) {
				
				if( // (object instanceof RealtimeObject) ||
						(object instanceof CounterObject) ||(object instanceof JulianDayObject) || (object instanceof ShiftObject)	
						)
				{
					if(PlatformInfo.isBufferFromDotMatrix()!=0) {
						object.generateVarbinFromMatrix(ConfigPath.getTlkDir(mName),object.mHeight,object.mWidth);
					} else {
						Debug.e(TAG, "=============mDots += object.drawVarBitmapN(N+1)    "  );
					     mDots += object.drawVarBitmapN(N+1);
					}
				} else if (object instanceof BarcodeObject && object.getSource() == true) {
					int dots = ((BarcodeObject) object).getDotcount();
					MessageObject msg = getMsgObject();
					if (msg.getType() == MessageType.MESSAGE_TYPE_1_INCH_FAST 
							|| msg.getType() == MessageType.MESSAGE_TYPE_1_INCH ) {
						mDots += dots * 2;
					} else if (msg.getType() == MessageType.MESSAGE_TYPE_1_INCH_DUAL 
							|| msg.getType() == MessageType.MESSAGE_TYPE_1_INCH_DUAL_FAST ) {
						mDots += dots * 2 * 4;
					} else {
						mDots += dots/2;
					}
					
				}
			}	
		}
	}	
 
	
	public void saveBin() 
	{
		int N=mSysconfig.getParam(42);//设置 43
		Debug.e(TAG, "-saveBin 00000" + N);
		if( N==0)
		{		Debug.e(TAG, "-saveBin 11111" + N);
			if (PlatformInfo.isBufferFromDotMatrix()!=0) 
			{
				saveBinDotMatrix();
			} else 
			{
				saveObjectBin();
			}
		}
		else
		{		Debug.e(TAG, "-saveBin 22222" + N);
			if (PlatformInfo.isBufferFromDotMatrix()!=0) 
			{	Debug.e(TAG, "-saveBin 33333333333333" + N);
				saveBinDotMatrix();
			} else 
			{	Debug.e(TAG, "-saveBin 444444444444444" + N);
				saveObjectBinN(N+1);
			}			
			
		}
	}
	
	/**
	 * 使用系统字库，生成bitmap，然后通过灰度化和二值化之后提取点阵生成buffer
	 * @param f
	 */
	private void saveObjectBin()
	{
		int width=0;
		Paint p=new Paint();
		if(mObjects==null || mObjects.size() <= 0)
			return ;
		for(BaseObject o:mObjects)
		{
			if (o instanceof MessageObject) {
				continue;
			}
			width = (int)(width > o.getXEnd() ? width : o.getXEnd());
		}
		float div = (float) (2.0/getHeads());
		
		Bitmap bmp = Bitmap.createBitmap(width , Configs.gDots, Bitmap.Config.ARGB_8888);
		Debug.e(TAG, "drawAllBmp width="+width+", height="+Configs.gDots);
		Canvas can = new Canvas(bmp);
		can.drawColor(Color.WHITE);
		for(BaseObject o:mObjects)
		{
			if((o instanceof MessageObject)	)
				continue;
			
			if(o instanceof CounterObject)
			{
				// o.drawVarBitmap();
			}
			else if(o instanceof RealtimeObject)
			{		
				Bitmap t = ((RealtimeObject)o).getBgBitmap(mContext);
				can.drawBitmap(t, o.getX(), o.getY(), p);
				BinFromBitmap.recyleBitmap(t);
 
			} else if(o instanceof JulianDayObject) {
				// o.drawVarBitmap();
			} else if (o instanceof BarcodeObject && o.getSource()) {
				
			} else if(o instanceof ShiftObject)	{
				// o.drawVarBitmap();
			} else if (o instanceof BarcodeObject) {
				Bitmap t = ((BarcodeObject) o).getScaledBitmap(mContext);
				can.drawBitmap(t, o.getX(), o.getY(), p);
			} else if (o instanceof GraphicObject) {
				Bitmap t = ((GraphicObject) o).getScaledBitmap(mContext);
				if (t != null) {
					can.drawBitmap(t, o.getX(), o.getY(), p);
				}
			} else {
				//o.mWidth=64;
				//o.mHeight=16;
				Bitmap t = o.getScaledBitmap(mContext);
				can.drawBitmap(t, o.getX(), o.getY(), p);
				// BinFromBitmap.recyleBitmap(t);
			}
		//can.drawText(mContent, 0, height-30, mPaint);
		}
		/**
		 * 爲了兼容128點，152點和376點高的三種列高信息，需要計算等比例縮放比例
		 */
		float dots = 152;///SystemConfigFile.getInstance(mContext).getParam(39);
		/*對於320列高的 1 Inch打印頭，不使用參數40的設置*/
		MessageObject msg = getMsgObject();
		if (msg != null && (msg.getType() == MessageType.MESSAGE_TYPE_1_INCH
				|| msg.getType() == MessageType.MESSAGE_TYPE_1_INCH_FAST
				|| msg.getType() == MessageType.MESSAGE_TYPE_1_INCH_DUAL
				|| msg.getType() == MessageType.MESSAGE_TYPE_1_INCH_DUAL_FAST)) {
			dots = 304;
		}
		Debug.d(TAG, "+++dots=" + dots);
		float prop = dots/Configs.gDots;
		Debug.d(TAG, "+++prop=" + prop);
		/** 生成bin的bitmap要进行处理，高度根据message的类型调整
		 * 注： 为了跟PC端保持一致，生成的bin文件宽度为1.tlk中坐标的四分之一，在提取点阵之前先对原始Bitmap进行X坐标缩放（为原图的1/4）
		 * 	  然后进行灰度和二值化处理；
		 */
		Debug.d(TAG, "--->div=" + div + "  h=" + bmp.getHeight() + "  prop = " + prop);
		Bitmap bitmap = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth()/div * prop), (int) (bmp.getHeight() * getHeads() * prop), true);
		/*對於320列高的 1 Inch打印頭，不使用參數40的設置*/
		if (msg != null && (msg.getType() == MessageType.MESSAGE_TYPE_1_INCH || msg.getType() == MessageType.MESSAGE_TYPE_1_INCH_FAST)) {
			Bitmap b = Bitmap.createBitmap(bitmap.getWidth(), 320, Bitmap.Config.ARGB_8888);
			can.setBitmap(b);
			can.drawColor(Color.WHITE);
			can.drawBitmap(bitmap, 0, 0, p);
			bitmap.recycle();
			bitmap = b;
		} else if (msg != null && (msg.getType() == MessageType.MESSAGE_TYPE_1_INCH_DUAL || msg.getType() == MessageType.MESSAGE_TYPE_1_INCH_DUAL_FAST)) {
			Bitmap b = Bitmap.createBitmap(bitmap.getWidth(), 640, Bitmap.Config.ARGB_8888);
			// can.setBitmap(b);
			Canvas c = new Canvas(b);
			c.drawColor(Color.WHITE);
			int h = bitmap.getHeight()/2;
			int dstH = b.getHeight()/2;
			c.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), h), new Rect(0, 0, b.getWidth(), 308), p);
			c.drawBitmap(bitmap, new Rect(0, h, bitmap.getWidth(), h*2), new Rect(0, 320, b.getWidth(), 320 + 308), p);
			// c.drawBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight()/2), new Matrix(), null);
			// c.drawBitmap(Bitmap.createBitmap(bitmap, 0, bitmap.getHeight()/2, bitmap.getWidth(), bitmap.getHeight()/2), 0, 320, null);
			bitmap.recycle();
			bitmap = b;
		}else if (msg != null && (msg.getType() == MessageType.MESSAGE_TYPE_16_3  ) )
		{ //add by lk 170418
			bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() , 128, true);
			Bitmap b = Bitmap.createBitmap( bitmap.getWidth() , 128, Bitmap.Config.ARGB_8888);
			can.setBitmap(b);
			can.drawColor(Color.WHITE); 
			can.drawBitmap(bitmap, 0, 0, p);
			bitmap.recycle();
			bitmap = b;
  		//add by lk 170418 end	
			
			
			
			
		}
		// 生成bin文件
		BinFileMaker maker = new BinFileMaker(mContext);
		mDots = maker.extract(bitmap);
		
		// 保存bin文件
		maker.save(ConfigPath.getBinAbsolute(mName));
		
		return ;
	}
	
	public byte Bin[];
	/**
	 * 使用系统字库，生成bitmap，然后通过灰度化和二值化之后提取点阵生成buffer
	 * @param f
	 */
	private void saveObjectBinN(int N)
	{	
		Debug.e(TAG, "=====================d00000000000=");
		int width=0;
		Paint p=new Paint();
		if(mObjects==null || mObjects.size() <= 0)
			return ;
		for(BaseObject o:mObjects)
		{
			if (o instanceof MessageObject) {
				continue;
			}
			width = (int)(width > o.getXEnd() ? width : o.getXEnd());
		}
		float div = (float) (2.0);
		
		Bitmap bmp = Bitmap.createBitmap(width , Configs.gDots, Bitmap.Config.ARGB_8888);
		Debug.e(TAG, "=====================drawAllBmp width="+width+", height="+Configs.gDots);
		Canvas can = new Canvas(bmp);
		can.drawColor(Color.WHITE);
		for(BaseObject o:mObjects)
		{
			if((o instanceof MessageObject)	)
				continue;
			
			if(o instanceof CounterObject)
			{
				// o.drawVarBitmap();
			}
			else if(o instanceof RealtimeObject)
			{
					Bitmap t = ((RealtimeObject)o).getBgBitmapN(mContext,N);
					can.drawBitmap(t, o.getX(), o.getY(), p);
					BinFromBitmap.recyleBitmap(t);				
			} else if(o instanceof JulianDayObject) {
				// o.drawVarBitmap();
			} else if (o instanceof BarcodeObject && o.getSource()) {
				
			} else if(o instanceof ShiftObject)	{
				// o.drawVarBitmap();
			} else if (o instanceof BarcodeObject) {
				Bitmap t = ((BarcodeObject) o).getScaledBitmap(mContext);
				can.drawBitmap(t, o.getX(), o.getY(), p);
			} else if (o instanceof GraphicObject) {
				Bitmap t = ((GraphicObject) o).getScaledBitmap(mContext);
				if (t != null) {
					can.drawBitmap(t, o.getX(), o.getY(), p);
				}
			} else {
				Bitmap t = o.getScaledBitmap(mContext);
				can.drawBitmap(t, o.getX(), o.getY(), p);
				// BinFromBitmap.recyleBitmap(t);
			}
		//can.drawText(mContent, 0, height-30, mPaint);
		}
		/**
		 * 爲了兼容128點，152點和376點高的三種列高信息，需要計算等比例縮放比例
		 */
		// float dots = 152;///SystemConfigFile.getInstance(mContext).getParam(39);

	//	Debug.d(TAG, "+++dots=" + dots);
		// float prop = dots/Configs.gDots;
	//	Debug.d(TAG, "+++prop=" + prop);
 
	//	Debug.d(TAG, "--->div=" + div + "  h=" + bmp.getHeight() + "  prop = " + prop);
		
		Bitmap bitmap = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth()/div ), (int) (bmp.getHeight() ), true);


			bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()*N, bitmap.getHeight(), true);
			Bitmap b = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
			can.setBitmap(b);
			can.drawColor(Color.WHITE);
			can.drawBitmap(bitmap, 0, 0, p);
			bitmap.recycle();
			bitmap = b;
						

		// 生成bin文件
		BinFileMaker maker = new BinFileMaker(mContext);
		mDots = maker.extract(bitmap);
		// 保存bin文件
		maker.save(ConfigPath.getBinAbsolute(mName));
		
	////////////////////////////////////////////////////////////////////////////
		int ret=0;
		int len=0;
		
		///读入	
			File	mFile = new File(ConfigPath.getBinAbsolute(mName) );
			try {
				mFStream = new FileInputStream(mFile);
				bin = new byte[mFStream.available()];
				mFStream.read(bin);
				mFStream.close();
				Debug.d(TAG, "--->buffer.size=" + bin.length);
			//	resolve();
			} catch (Exception e) {
				Debug.d(TAG, ""+e.getMessage());
			}
		
	        //修改
	        
	        int w=(bin[0]&0xff)<<16 | (bin[1]&0xff)<<8 | (bin[2]&0xff);
			Debug.e(TAG, "w=============================" +w+bin[2]+ bin[1] +bin[0]   ); 
			Debug.e(TAG, "w=============================" + ((bin[0]&0xff)<<16)    ); 	
			Debug.e(TAG, "w=============================" + ((bin[1]&0xff)<<8)    ); 				
			Debug.e(TAG, "w=============================" + ((bin[2]&0xff))    ); 				
			w=(int)(w/N);
			
	        int h=(bin[3]&0xff)<<16 | (bin[4]&0xff)<<8 | (bin[5]&0xff);
			Debug.e(TAG, "h=============================" +h+bin[5]+ bin[4] +bin[3]   );
			h=h*N;        
 
	
			bin[2] = (byte) (w & 0x0ff);
			bin[1] = (byte) ((w>>8) & 0x0ff);
			bin[0] = (byte) ((w>>16) & 0x0ff);
	    	
	    	 
			bin[5] = (byte) (h & 0x0ff);
			bin[4] = (byte) ((h>>8) & 0x0ff);
			bin[3] = (byte) ((h>>16) & 0x0ff);	
		
		     //保存	
	    	try{
	    		File file = new File(ConfigPath.getBinAbsolute(mName)  );
	    		FileOutputStream fs = new FileOutputStream(ConfigPath.getBinAbsolute(mName)   );
	    		ByteArrayOutputStream barr = new ByteArrayOutputStream();
	    		//barr.write(head);
	    		barr.write(bin,0,bin.length);
	    		barr.writeTo(fs);
	    		fs.flush();
	    		fs.close();
	    		barr.close();
	    	}catch(Exception e)
	    	{
	    		Debug.d(TAG, "Exception: "+e.getMessage());
	    		return ;
	    	}
	        ///////////////////////////////////////////////
		

	}	
	
	
	
	/**
	 * 从16*16的点阵字库中提取点阵，生成打印buffer
	 * @param f
	 */
	private void saveBinDotMatrix() {
		if(mObjects==null || mObjects.size() <= 0)
			return ;
		
		String content="";
		float  height=0;
		float  width=0;
		byte [] g_1binbits=null;
		int     g_1bincol_from_tlk=0;
		// 生成bin文件
		BinFileMaker maker = new BinFileMaker(mContext);
		
	 
		int leftwidthall =0;
		float fleftwidthall=0;
		for(BaseObject o:mObjects)
		{
			if (o instanceof MessageObject) {
				continue;
			} 		
			
			if(o instanceof TextObject )
			{
				content = o.getContent();
				fleftwidthall +=  content.length()* (16*9.5) ;//得到 最大 宽度 项目  作为 整体 宽度 
			
			}else
			{
			fleftwidthall = (fleftwidthall > o.getXEnd() ? fleftwidthall : o.getXEnd());//得到 最大 宽度 项目  作为 整体 宽度 
			Debug.e(TAG, "1===== leftwidth"+leftwidthall  +"o.getXEnd"+  o.getXEnd() );	
			}
	

		
		}

		 leftwidthall=(int)( (fleftwidthall)/9.5+0.5 );		
		leftwidthall= leftwidthall<16?16:leftwidthall; //总宽度  不少于 一个 字符 				
		leftwidthall+=19;	//尾部 多加  16+3列 		
		Debug.e(TAG, "===== leftwidth"+leftwidthall   );	
		
		 g_1binbits  =new byte [4* (leftwidthall) ]; //32高4字节 ×宽 
		 
		int  leftwidth =2; 
		int leftmov=3; 
		for(BaseObject o:mObjects)
		{
			if((o instanceof MessageObject)	)
			{
				continue;
			}
			
			if(o instanceof CounterObject)
			{
				// content += o.getContent();
				o.generateVarbinFromMatrix(ConfigPath.getTlkDir(mName),o.mHeight,o.mWidth);
				if( o.mHeight==76)
				{						 
					 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.8);
				}else if( o.mHeight==152)
				{
					 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.1);				
				}	
			}
			else if(o instanceof RealtimeObject)
			{
			//	Bitmap t = ((RealtimeObject)o).getBgBitmapN(mContext,N);
			//	can.drawBitmap(t, o.getX(), o.getY(), p);
			//	BinFromBitmap.recyleBitmap(t);
				       //  9999-99-99

				o.generateVarbinFromMatrix(ConfigPath.getTlkDir(mName),o.mHeight,o.mWidth);
				
				 
			//	Debug.e(TAG, "--->realtime: " + content);
				/*
				content ="  -  -  ";// o.getContent();				
				height = o.mHeight;
				width = o.mWidth; 
				
			    Debug.e(TAG, "====DZ_buffer 000="+height+ width);
		  	   mDots = maker.extract(content,height,width);
			
								 
				byte[] bit_32 = new byte[ mDots ];//字模高  × 字模宽 
				 Debug.e(TAG, "====DZ_buffer 111=maker.getBuffer().length "+maker.getBuffer().length );
				 bit_32 = maker.getBuffer() ;	
			 	
				 if( height ==76)
				 {
					for (int i = 0; i < bit_32.length; i+=4) //压缩掉的 列 数 8 
					{
							int objcew=  ((int)(o.mXcor /10 )*4 );
							g_1binbits[(i +   objcew ) ] = bit_32[i];
							g_1binbits[(i+1 + objcew  ) ] = bit_32[i+1];
							g_1binbits[(i+2 + objcew ) ] = bit_32[i+2];
							g_1binbits[(i+3 + objcew  ) ] = bit_32[i+3];						
					}
				 }
				 if( height ==120)
				 {
					for (int i = 0; i < bit_32.length; i+=4) //压缩掉的 列 数 8 
					{
							int objcew=  ((int)(o.mXcor /10 )*4 );
							g_1binbits[(i +   objcew ) ] = bit_32[i];
							g_1binbits[(i+1 + objcew  ) ] = bit_32[i+1];
							g_1binbits[(i+2 + objcew ) ] = bit_32[i+2];
							g_1binbits[(i+3 + objcew  ) ] = bit_32[i+3];
							
					}
				 }	
				 */
				
				Debug.e(TAG, " ======000000000000000"   );
				String substr=null;
				char[] var;		
				Vector<BaseObject> rt = ((RealtimeObject) o).getSubObjs();
				
				for(BaseObject rtSub : rt)
				{							
					if(rtSub instanceof TextObject)
					{ 
						content=	substr = ((TextObject)rtSub).getContent();
						 Debug.e(TAG, " ======111" + substr );	
						
						//content ="  -  -  ";// o.getContent();				
						height = rtSub.mHeight;
						width = rtSub.mWidth; 
						
					    Debug.e(TAG, "====DZ_buffer 000="+height+ width);
				  	   mDots = maker.extract(substr,height,width);
					
										 
						byte[] bit_32 = new byte[ mDots ];//字模高  × 字模宽 
						 Debug.e(TAG, "====DZ_buffer 111=maker.getBuffer().length "+maker.getBuffer().length );
						 bit_32 = maker.getBuffer() ;	
					 	
						 
						 if( height ==76)
						 { 		 //Y 移动 		
							 int objtop =  ((int)(rtSub.mYcor/9.5+0.5)); // Y 移动 坐标 数值 
							 Debug.e(TAG, " ====objtop" + objtop );	
							 int len= bit_32.length; 	
							int[] isrc=null;
			    			int tempint=0;
			    			isrc  =new int  [len ]; //32高4字节 ×宽 
			    	    	for( int i2=0;i2<len/4;i2++)
			    	    	{
			    	    		tempint =(int) ( bit_32[i2*4+1 ] );//1
			    	    		isrc[i2]=(tempint<<24)&0xff000000;   
			    	    		
			    	    		tempint =(int) ( bit_32[i2*4 ] );//0
			    	    		isrc[i2]|= (tempint<<16)&0x00ff0000;   
			    	    		
			    	    		tempint =(int) ( bit_32[i2*4+3 ] );//3
			    	    		isrc[i2]|= (tempint<<8)&0x0000ff00;    
			    	    		
			    	    		tempint =(int) ( bit_32[i2*4+2 ] );//2
			    	    		isrc[i2]|= tempint      &0x000000ff;   
			    	    		
			    	    		isrc[i2]= isrc[i2]<<objtop;
			    	    	}
			    	    	
			    	    	for( int i2=0;i2<len/4;i2++)
			    	    	{
			    	    		bit_32[i2*4+1]=(byte) ( (isrc[i2] >>24)&0x000000ff ); 			    	    		
			    	    		bit_32[i2*4+0]=(byte) ( (isrc[i2] >>16)&0x000000ff ); 			    	    		
			    	    		bit_32[i2*4+3]=(byte) ( (isrc[i2] >>8)&0x0000000ff  ); 
			    	    		bit_32[i2*4+2]=(byte) ( isrc[i2 ] &    0x0000000ff      );			    	    		    	    		
			    	    	} 
			    	    	// Y end 
	    	    	
							for (int i = 0; i < bit_32.length; i+=4) //压缩掉的 列 数 8 
							{
								int objcew=  ((int)(rtSub.mXcor/9.5+0.5))*4;
								   objcew += leftmov*4 ;
									g_1binbits[(i +   objcew ) ] |= bit_32[i];
									g_1binbits[(i+1 + objcew  ) ]|= bit_32[i+1];
									g_1binbits[(i+2 + objcew ) ] |= bit_32[i+2];
									g_1binbits[(i+3 + objcew  ) ] |= bit_32[i+3];						
							}
						 }
						 if( height ==152)
						 {
							 //Y 移动 		
							 int objtop =  ((int)(rtSub.mYcor/9.5+0.5)); // Y 移动 坐标 数值 
							 Debug.e(TAG, " ====objtop" + objtop );	
							 int len= bit_32.length; 	
							int[] isrc=null;
			    			int tempint=0;
			    			isrc  =new int  [len ]; //32高4字节 ×宽 
			    	    	for( int i2=0;i2<len/4;i2++)
			    	    	{
			    	    		tempint =(int) ( bit_32[i2*4+1 ] );//1
			    	    		isrc[i2]=(tempint<<24)&0xff000000;   
			    	    		
			    	    		tempint =(int) ( bit_32[i2*4 ] );//0
			    	    		isrc[i2]|= (tempint<<16)&0x00ff0000;   
			    	    		
			    	    		tempint =(int) ( bit_32[i2*4+3 ] );//3
			    	    		isrc[i2]|= (tempint<<8)&0x0000ff00;    
			    	    		
			    	    		tempint =(int) ( bit_32[i2*4+2 ] );//2
			    	    		isrc[i2]|= tempint      &0x000000ff;   
			    	    		
			    	    		isrc[i2]= isrc[i2]<<objtop;
			    	    	}
			    	    	
			    	    	for( int i2=0;i2<len/4;i2++)
			    	    	{
			    	    		bit_32[i2*4+1] =(byte) ( (isrc[i2] >>24)&0x000000ff ); 			    	    		
			    	    		bit_32[i2*4+0]=(byte) ( (isrc[i2] >>16)&0x000000ff ); 			    	    		
			    	    		bit_32[i2*4+3]=(byte) ( (isrc[i2] >>8)&0x0000000ff  ); 
			    	    		bit_32[i2*4+2]=(byte) ( isrc[i2 ] &    0x0000000ff      );			    	    		    	    		
			    	    	} 
			    	    	// Y end 							 
							for (int i = 0; i < bit_32.length; i+=4) //压缩掉的 列 数 8 
							{
								int objcew=  ((int)(rtSub.mXcor/9.5+0.5))*4;
								   objcew += leftmov*4 ;
									g_1binbits[(i +   objcew ) ] |= bit_32[i];
									g_1binbits[(i+1 + objcew  ) ] |= bit_32[i+1];
									g_1binbits[(i+2 + objcew ) ] |= bit_32[i+2];
									g_1binbits[(i+3 + objcew  ) ] |= bit_32[i+3];								
							}
						 }	
					}
					if( rtSub.mHeight==76)
					{						 
						 leftwidth += (int)((rtSub.mXcor_end-rtSub.mXcor)/8.8);
					}else if( rtSub.mHeight==152)
					{
						 leftwidth += (int)((rtSub.mXcor_end-rtSub.mXcor)/8.1);				
					}	

				}	
					 			
			}
			else if(o instanceof JulianDayObject)
			{
			//	content += o.getContent();
				o.generateVarbinFromMatrix(ConfigPath.getTlkDir(mName),o.mHeight,o.mWidth);
				if( o.mHeight==76)
				{						 
					 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.8);
				}else if( o.mHeight==152)
				{
					 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.1);				
				}
				
			}
			else if(o instanceof ShiftObject)
			{
				// content += o.getContent();
				o.generateVarbinFromMatrix(ConfigPath.getTlkDir(mName),o.mHeight,o.mWidth);
				if( o.mHeight==76)
				{						 
					 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.8);
				}else if( o.mHeight==152)
				{
					 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.1);				
				}	
			}
			else if(o instanceof TextObject )
			{
				content = o.getContent();
				height = o.mHeight;
				width = o.mWidth; 
				
			  Debug.e(TAG, "====DZ_buffer 000="+height+ width);
			  mDots = maker.extract(content,height,width);
			  Debug.e(TAG, "====DZ_buffer 111="+ mDots  );
								 
			 byte[] bit_32 = new byte[ maker.getBuffer().length ];//字模高  × 字模宽 
			 Debug.e(TAG, "====DZ_buffer 111=maker.getBuffer().length "+maker.getBuffer().length );
	    	 bit_32 = maker.getBuffer() ;	
		    Debug.e(TAG, "====DZ_buffer 222=");
	
	  
			 if( height ==76) 
			 {
				 //Y 移动 		
				 int objtop =  ((int)(o.mYcor/9.5+0.5)); // Y 移动 坐标 数值 
				 Debug.e(TAG, " ====objtop" + objtop );	
				 int len= bit_32.length; 	
				int[] isrc=null;
    			int tempint=0;
    			isrc  =new int  [len ]; //32高4字节 ×宽 
    	    	for( int i2=0;i2<len/4;i2++)
    	    	{
    	    		tempint =(int) ( bit_32[i2*4+1 ] );//1
    	    		isrc[i2]=(tempint<<24)&0xff000000;   
    	    		
    	    		tempint =(int) ( bit_32[i2*4 ] );//0
    	    		isrc[i2]|= (tempint<<16)&0x00ff0000;   
    	    		
    	    		tempint =(int) ( bit_32[i2*4+3 ] );//3
    	    		isrc[i2]|= (tempint<<8)&0x0000ff00;    
    	    		
    	    		tempint =(int) ( bit_32[i2*4+2 ] );//2
    	    		isrc[i2]|= tempint &0x000000ff;   
    	    		
    	    		isrc[i2]= isrc[i2]<<objtop;
    	    	}
    	    	
    	    	for( int i2=0;i2<len/4;i2++)
    	    	{
    	    		bit_32[i2*4+1]=(byte) ( (isrc[i2] >>24)&0x000000ff ); 			    	    		
    	    		bit_32[i2*4+0]=(byte) ( (isrc[i2] >>16)&0x000000ff ); 			    	    		
    	    		bit_32[i2*4+3]=(byte) ( (isrc[i2] >>8)&0x0000000ff  ); 
    	    		bit_32[i2*4+2]=(byte) ( isrc[i2 ] &    0x0000000ff      );			    	    		    	    		
    	    	} 
    	    	// Y end 				 
				for (int i = 0; i < bit_32.length-1; i+=4) //压缩掉的 列 数 8 
				{
						Debug.e(TAG, "====DZ_buffer.length-76=");
						int objcew=  ((int)(o.mXcor/9.5+0.5))*4;
						  objcew += leftmov*4;
						g_1binbits[(i +   objcew ) ] |= bit_32[i];
						g_1binbits[(i+1 + objcew  ) ] |= bit_32[i+1];
						g_1binbits[(i+2 + objcew ) ] |= bit_32[i+2];
						g_1binbits[(i+3 + objcew  ) ] |= bit_32[i+3];						
				}
			 }
			 if( height ==152)
			 {
				 //Y 移动 		
				 int objtop =  ((int)(o.mYcor/9.5+0.5)); // Y 移动 坐标 数值 
				 Debug.e(TAG, " ====objtop" + objtop );	
				 int len= bit_32.length; 	
				int[] isrc=null;
    			int tempint=0;
    			isrc  =new int  [len ]; //32高4字节 ×宽 
    	    	for( int i2=0;i2<len/4;i2++)
    	    	{
    	    		tempint =(int) ( bit_32[i2*4+1 ] );//1
    	    		isrc[i2]=(tempint<<24)&0xff000000;   
    	    		
    	    		tempint =(int) ( bit_32[i2*4 ] );//0
    	    		isrc[i2]|= (tempint<<16)&0x00ff0000;   
    	    		
    	    		tempint =(int) ( bit_32[i2*4+3 ] );//3
    	    		isrc[i2]|= (tempint<<8)&0x0000ff00;    
    	    		
    	    		tempint =(int) ( bit_32[i2*4+2 ] );//2
    	    		isrc[i2]|= tempint      &0x000000ff;   
    	    		
    	    		isrc[i2]= isrc[i2]<<objtop;
    	    	}
    	    	
    	    	for( int i2=0;i2<len/4;i2++)
    	    	{
    	    		bit_32[i2*4+1]=(byte) ( (isrc[i2] >>24)&0x000000ff ); 			    	    		
    	    		bit_32[i2*4+0]=(byte) ( (isrc[i2] >>16)&0x000000ff ); 			    	    		
    	    		bit_32[i2*4+3]=(byte) ( (isrc[i2] >>8)&0x0000000ff  ); 
    	    		bit_32[i2*4+2]=(byte) ( isrc[i2 ] &    0x0000000ff      );			    	    		    	    		
    	    	} 
    	    	// Y end 				 
				for (int i = 0; i < bit_32.length-1; i+=4) //压缩掉的 列 数 8 
				{
						Debug.e(TAG, "====DZ_buffer.length-152="+ bit_32.length+"="+i+"="+ (leftwidth*4 ) );
						int objcew=  ((int)(o.mXcor/9.5+0.5))*4;
						  objcew += leftmov*4;
						g_1binbits[(i +   objcew ) ] |= bit_32[i];
						g_1binbits[(i+1 + objcew  ) ] |= bit_32[i+1];
						g_1binbits[(i+2 + objcew ) ] |= bit_32[i+2];
						g_1binbits[(i+3 + objcew  ) ] |= bit_32[i+3];
						
				}
			 }	
				if( o.mHeight==76)
				{						 
					 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.8);
				}else if( o.mHeight==152)
				{
					 leftwidth += (int)((o.mXcor_end-o.mXcor)/8.1);				
				}	
 	 
				 				
			}
		
		//can.drawText(mContent, 0, height-30, mPaint);
		}
		Debug.e(TAG, " ======9999" + leftwidth  );	
		//	maker.setBuffer(g_1binbits);
			// 保存bin文件
			maker.saveBin(ConfigPath.getTlkDir(mName) + "/1.bin",g_1binbits,32);	
			
		for(BaseObject o:mObjects)
		{
			if((o instanceof MessageObject)	) {
				((MessageObject) o).setDotCount(mDots);
				break;
			}
		}
		
		return ;
	}


	public void savePreview()
	{
		int width=0;
		Paint p=new Paint();
		if(mObjects==null || mObjects.size() <= 0)
		return ;
		for(BaseObject o:mObjects)
		{
		width = (int)(width > o.getXEnd() ? width : o.getXEnd());
		}

		Bitmap bmp = Bitmap.createBitmap(width , Configs.gDots, Bitmap.Config.ARGB_8888);
		Debug.d(TAG, "drawAllBmp width="+width+", height="+Configs.gDots);
		Canvas can = new Canvas(bmp);
		can.drawColor(Color.WHITE);

		String content="";

		for(BaseObject o:mObjects)
		{
		if (o instanceof MessageObject) {
		continue;
		}

		if(o instanceof CounterObject)
		{
//			o.setContent2(str_new_content)	 ;
		Bitmap  t  = o.getpreviewbmp();

		if (t== null) {
		continue;
		}

		can.drawBitmap(t, o.getX(), o.getY(), p);
		}

		else if(o instanceof RealtimeObject)
		{	Debug.e(TAG, "RealtimeObject");
		Bitmap  t  = o.getpreviewbmp();

		if (t== null) {
		continue;
		}
		can.drawBitmap(t, o.getX(), o.getY(), p);
		}
		else if(o instanceof JulianDayObject)
		{
		Bitmap  t  = o.getpreviewbmp();

		if (t== null) {
		continue;
		}
		can.drawBitmap(t, o.getX(), o.getY(), p);
		}else if(o instanceof TextObject)
		{
		Bitmap  t  = o.getpreviewbmp();

		if (t== null) {
		continue;
		}
		can.drawBitmap(t, o.getX(), o.getY(), p);
		}
		else //addbylk
		{
		Bitmap t = o.getScaledBitmap(mContext);
		if (t== null) {
		continue;
		}
		can.drawBitmap(t, o.getX(), o.getY(), p);


		}

		/*
		TextObject
		{
		Bitmap t  = o.getScaledBitmap(mContext);
		if (t== null) {
		continue;
		}
		can.drawBitmap(t, o.getX(), o.getY(), p);

		}*/

		/*
		else if(o instanceof ShiftObject)
		{
		content += o.getContent();
		}
		*/

		//can.drawText(mContent, 0, height-30, mPaint);

//			if(o instanceof CounterObject)
//			{
//			 o.setContent("lkaa");//mContext="liukun";
//			}

		///	String o.setContent2("lkaa");//mContext="liukun";

		}
		// Bitmap.createScaledBitmap();
		float scale = bmp.getHeight() / 100;
		width = (int) (width / scale);

		width=width/2; //addbylk 减半输出 

		Bitmap nBmp = Bitmap.createScaledBitmap(bmp, width, 100, false);
		BitmapWriter.saveBitmap(nBmp, ConfigPath.getTlkDir(getName()), "1.bmp");
	}
	/**
	 * save picture to tlk dir
	 */
	private void saveExtras() {
		for (BaseObject object : getObjects()) {
			if (object instanceof GraphicObject) {
				String source = ConfigPath.getPictureDir() + ((GraphicObject)object).getImage();
				String dst = getPath() + "/" + ((GraphicObject)object).getImage();
				Debug.d(TAG, "--->source: " + source);
				Debug.d(TAG, "--->dst: " + dst);
				// if file is exist, dont copy again
				File file = new File(dst);
				if (file.exists()) {
					continue;
				} else {
					FileUtil.copyFile(source, dst);
					((GraphicObject)object).setImage(dst);
				}
			}
		}
	}

	public void save() {
		 
		Debug.e(TAG, "-11111111111111 " );
		resetIndexs();
		//保存1.TLK文件
		  saveTlk(mContext);
		//保存1.bin文件
	 	saveBin();
		Debug.e(TAG, "-2222222222 " );	
		//保存其他必要的文件
	  	saveExtras();
		Debug.e(TAG, "-1333333333333 " );	
		//保存vx.bin文件
	 	saveVarBin();
		Debug.e(TAG, "-444444444444 " );	
		//保存1.TLK文件
		saveTlk(mContext);
		Debug.e(TAG, "-1444444444444444" );			
		//保存1.bmp文件
		savePreview();
	 
	}
	
	private void resetIndexs() {
		int i = 0;
		for (BaseObject o : mObjects) {
			o.setIndex(i + 1);
			if (o instanceof RealtimeObject) {
				i = o.getIndex() + ((RealtimeObject) o).getSubObjs().size();
			} else {
				i = o.getIndex();
			}
		}
	}
	
	
	public MessageObject getMsgObject() {
		MessageObject msg = null;
		for (BaseObject object: mObjects) {
			if (object instanceof MessageObject) {
				msg = (MessageObject) object;
				break;
			}
		}
		return msg;
	}
	
	public int getHeads() {
		int height = 1;
		MessageObject obj = getMsgObject();
		if (obj == null) {
			return height;
		}
		// Debug.d(TAG, "--->head type: " + obj.getType());
		switch (obj.getType()) {
			case MessageType.MESSAGE_TYPE_12_7:
			case MessageType.MESSAGE_TYPE_12_7_S:
			case MessageType.MESSAGE_TYPE_16_3:
			case MessageType.MESSAGE_TYPE_1_INCH:
			case MessageType.MESSAGE_TYPE_1_INCH_FAST:
				height = 1;
				break;
			case MessageType.MESSAGE_TYPE_25_4:
			case MessageType.MESSAGE_TYPE_33:
			case MessageType.MESSAGE_TYPE_1_INCH_DUAL:
			case MessageType.MESSAGE_TYPE_1_INCH_DUAL_FAST:
				height = 2;
				break;
			case MessageType.MESSAGE_TYPE_38_1:
				height = 3;
				break;
			case MessageType.MESSAGE_TYPE_50_8:
				height = 4;
				break;
			default:
				break;
		}
		return height;
	}

	public String getPreview() {
		return ConfigPath.getTlkDir(mName) + MSG_PREV_IMAGE;
	}
	
	public String getPath() {
		return ConfigPath.getTlkDir(mName);
	}
	
	public float getDiv() {
		return 4f/getHeads();
	}

	
	public static class MessageType {
		public static final int MESSAGE_TYPE_12_7 	= 0;
		public static final int MESSAGE_TYPE_12_7_S = 1;
		public static final int MESSAGE_TYPE_25_4 	= 2;
		public static final int MESSAGE_TYPE_16_3 	= 3;
		public static final int MESSAGE_TYPE_33	   	= 4;
		public static final int MESSAGE_TYPE_38_1  	= 5;
		public static final int MESSAGE_TYPE_50_8  	= 6;
		public static final int MESSAGE_TYPE_HZK_16_8  = 7;
		public static final int MESSAGE_TYPE_HZK_16_16  = 8;				
		public static final int MESSAGE_TYPE_1_INCH = 9; //320點每列的噴頭
		public static final int MESSAGE_TYPE_1_INCH_FAST = 10; //320點每列的噴頭
		public static final int MESSAGE_TYPE_1_INCH_DUAL = 11; //320點每列的噴頭,雙頭
		public static final int MESSAGE_TYPE_1_INCH_DUAL_FAST = 12; //320點每列的噴頭,雙頭
	}
	
}

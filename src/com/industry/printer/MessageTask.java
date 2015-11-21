package com.industry.printer;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.industry.printer.FileFormat.TlkFileWriter;
import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.Utils.PlatformInfo;
import com.industry.printer.data.BinFileMaker;
import com.industry.printer.data.BinFromBitmap;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.CounterObject;
import com.industry.printer.object.JulianDayObject;
import com.industry.printer.object.MessageObject;
import com.industry.printer.object.ObjectsFromString;
import com.industry.printer.object.RealtimeObject;
import com.industry.printer.object.ShiftObject;
import com.industry.printer.object.TLKFileParser;
import com.industry.printer.object.TextObject;

/**
 * MessageTask包含多个object
 * @author zhaotongkai
 *
 */
public class MessageTask {

	private static final String TAG = MessageTask.class.getSimpleName();
	
	private Context mContext;
	private int mDots=0; 
	private String mName;
	private ArrayList<BaseObject> mObjects;
	
	public MessageTask(Context context) {
		mName="";
		mContext = context;
		mObjects = new ArrayList<BaseObject>();
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
		mObjects.add(object);
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
	
	public void saveVarBin() {
		if (mObjects == null || mObjects.size() <= 0) {
			return;
		}
		for (BaseObject object : mObjects) {
			if((object instanceof CounterObject) || (object instanceof RealtimeObject))
			{
				object.generateVarbinFromMatrix(ConfigPath.getTlkDir(mName));
			}
		}
	}
	
	public void saveBin() {
		if (PlatformInfo.isBufferFromDotMatrix()) {
			saveBinDotMatrix();
		} else {
			saveObjectBin();
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
			width = (int)(width > o.getXEnd() ? width : o.getXEnd());
		}
		
		Bitmap bmp = Bitmap.createBitmap(width , Configs.gDotsTotal, Bitmap.Config.ARGB_8888);
		Debug.d(TAG, "drawAllBmp width="+width+", height="+Configs.gDotsTotal);
		Canvas can = new Canvas(bmp);
		can.drawColor(Color.WHITE);
		for(BaseObject o:mObjects)
		{
			if((o instanceof MessageObject)	)
				continue;
			
			if(o instanceof CounterObject)
			{
				o.drawVarBitmap(ConfigPath.getTlkDir(mName));
			}
			else if(o instanceof RealtimeObject)
			{
				Bitmap t = ((RealtimeObject)o).getBgBitmap(mContext,ConfigPath.getTlkDir(mName));
				can.drawBitmap(t, o.getX(), o.getY(), p);
				BinFromBitmap.recyleBitmap(t);
			}
			else if(o instanceof JulianDayObject)
			{
				o.drawVarBitmap(ConfigPath.getTlkDir(mName));
			}
			else if(o instanceof ShiftObject)
			{
				o.drawVarBitmap(ConfigPath.getTlkDir(mName));
			}
			else
			{
				Bitmap t = o.getScaledBitmap(mContext);
				can.drawBitmap(t, o.getX(), o.getY(), p);
				BinFromBitmap.recyleBitmap(t);
			}
		//can.drawText(mContent, 0, height-30, mPaint);
		}
		// 生成bin文件
		BinFileMaker maker = new BinFileMaker(mContext);
		maker.extract(bmp);
		// 保存bin文件
		maker.save(ConfigPath.getBinAbsolute(mName));
		
		return ;
	}
	
	/**
	 * 从16*16的点阵字库中提取点阵，生成打印buffer
	 * @param f
	 */
	private void saveBinDotMatrix() {
		if(mObjects==null || mObjects.size() <= 0)
			return ;
		
		String content="";
		for(BaseObject o:mObjects)
		{
			if((o instanceof MessageObject)	)
				continue;
			
			if(o instanceof CounterObject)
			{
				content += o.getContent();
			}
			else if(o instanceof RealtimeObject)
			{
				content += o.getContent();
				Debug.d(TAG, "--->realtime: " + content);
			}
			else if(o instanceof JulianDayObject)
			{
				content += o.getContent();
			}
			else if(o instanceof ShiftObject)
			{
				content += o.getContent();
			}
			else
			{
				content += o.getContent();
			}
		//can.drawText(mContent, 0, height-30, mPaint);
		}
		// 生成bin文件
		BinFileMaker maker = new BinFileMaker(mContext);
		mDots = maker.extract(content);
		// 保存bin文件
		maker.save(ConfigPath.getTlkDir(mName) + "/1.bin");
		for(BaseObject o:mObjects)
		{
			if((o instanceof MessageObject)	) {
				((MessageObject) o).setDotCount(mDots);
				break;
			}
		}
		
		return ;
	}
	
	
	
}
package com.industry.printer.object;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.util.Vector;

import com.industry.printer.EditTabActivity;
import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;

import android.content.Context;
import android.util.Log;

public class TLKFileParser {
	public static final String TAG="Fileparser";
	public static Context mContext;
	
	public String mPath;
	private int mDots = 0;
	
	public TLKFileParser(String file) {
		setTlk(file);
	}
	
	
	public void parse(Context context, String name, Vector<BaseObject> objlist)
	{
		int i;
		BaseObject pObj;
		mContext = context;
		if(objlist == null)
			objlist = new Vector<BaseObject>();
		objlist.clear();
		File file = new File(name);
		if(file.isDirectory())
		{
			Log.d(TAG, "this is a directory");
			return;
		}
		try{
			 InputStream instream = new FileInputStream(file); 
			 if(instream != null)
			 {
				 InputStreamReader inputreader = new InputStreamReader(instream,"UTF-8");
                 BufferedReader buffreader = new BufferedReader(inputreader);
                 String line;
                 
                 while (( line = buffreader.readLine()) != null) {
                     Log.d(TAG, "line="+line);
                     pObj = parseLine(line);
                     if (pObj == null) {
                    	 continue;
                     }
                     objlist.add(pObj);
                     if(pObj instanceof RealtimeObject)
                     {
                    	 i = ((RealtimeObject) pObj).mSubObjs.size();
                    	 
                    	 for(int j=0; j<i ; j++)
                    	 {
                    		 line = buffreader.readLine();
                    		 Log.d(TAG, "line="+line);
                    		 for(int k=0; k<i; k++)
                    		 {
                    			 BaseObject obj =  ((RealtimeObject) pObj).mSubObjs.get(k);
                    			 if(obj.getId().equals(line.substring(4, 7)))
                    			 {
                    				 ((RealtimeObject) pObj).mSubObjs.get(j).setIndex(Integer.parseInt(line.substring(0, 3)));
                            		 Log.d(TAG, "pObj "+((RealtimeObject) pObj).mSubObjs.get(j).getId()+",index="+((RealtimeObject) pObj).mSubObjs.get(j).getIndex()); 
                            		 break;
                    			 }
                    		 }
                    		 
                    	 }
                     }
                 }
                 instream.close();
			 }
		}catch(Exception e)
		{
			Log.d(TAG, "parse error: "+e.getMessage());
		}
	}
	
	public BaseObject parseLine(String str)
	{
		Log.d(TAG, "*************************");
		BaseObject obj = null;
		String [] attr = str.split("\\^",0);
		Log.d(TAG,"index="+str.indexOf("^"));
		/*
		for(int i=0; i< attr.length; i++)
		{
			Log.d(TAG, "attr["+i+"]="+attr[i]);
		}
		*/
		Log.d(TAG, "attr[1]="+attr[1]);
		if(BaseObject.OBJECT_TYPE_BARCODE.equals(attr[1]))	//barcode
		{
			obj = new BarcodeObject(mContext, 0);
			((BarcodeObject) obj).setCode(attr[9]);
			((BarcodeObject) obj).setShow(Boolean.parseBoolean(attr[11])); 
			((BarcodeObject) obj).setContent(attr[12]);
			Log.d(TAG, "Barcode object: ");
			//Log.d(TAG, ""+((BarcodeObject) obj).getCode());
			//Log.d(TAG, ""+((BarcodeObject) obj).getShow());
			//Log.d(TAG, ""+((BarcodeObject) obj).getContent());
		}
		else if(BaseObject.OBJECT_TYPE_CNT.equals(attr[1]))		//cnt
		{
			obj = new CounterObject(mContext, 0);
			((CounterObject) obj).setBits(Integer.parseInt(attr[8]));
			((CounterObject) obj).setMax(Integer.parseInt(attr[13]));
			((CounterObject) obj).setMin(Integer.parseInt(attr[14]));
			//((CounterObject) obj).setContent(attr[8]);
			Log.d(TAG, "Counter object");
			//Log.d(TAG, ""+((CounterObject) obj).getBits());
			//Log.d(TAG, ""+((CounterObject) obj).getMax());
			//Log.d(TAG, ""+((CounterObject) obj).getMin());
			//Log.d(TAG, ""+((CounterObject) obj).getContent());
		}
		else if(BaseObject.OBJECT_TYPE_ELLIPSE.equals(attr[1]))	//ellipse
		{
			obj = new EllipseObject(mContext, 0);
			((EllipseObject) obj).setLineWidth(Integer.parseInt(attr[8]));
			((EllipseObject) obj).setLineType(Integer.parseInt(attr[9]));
			Log.d(TAG, "Ellipse object");
			//Log.d(TAG, "line type="+((EllipseObject) obj).getLineType());
			//Log.d(TAG, "line width="+((EllipseObject) obj).getLineWidth());
		}
		else if(BaseObject.OBJECT_TYPE_GRAPHIC.equals(attr[1]))	//graphic
		{
			
		}
		else if(BaseObject.OBJECT_TYPE_JULIAN.equals(attr[1]))		//julian day
		{
			obj = new JulianDayObject(mContext, 0);
			Log.d(TAG, "Julian day");
		}
		else if(BaseObject.OBJECT_TYPE_LINE.equals(attr[1]))			//line
		{
			obj = new LineObject(mContext, 0);
			((LineObject) obj).setLineWidth(Integer.parseInt(attr[8]));
			((LineObject) obj).setLineType(Integer.parseInt(attr[9]));
			Log.d(TAG, "line object");
			//Log.d(TAG, "line type="+((LineObject) obj).getLineType());
			//Log.d(TAG, "line width="+((LineObject) obj).getLineWidth());
		}
		else if(BaseObject.OBJECT_TYPE_MsgName.equals(attr[1]))		//msg name
		{
			obj = new MessageObject(mContext, 0);
			((MessageObject)obj).setDotCount(Integer.parseInt(attr[13]));
			mDots = Integer.parseInt(attr[13]);
		}
		else if(BaseObject.OBJECT_TYPE_RECT.equals(attr[1]))			//rect
		{
			obj = new RectObject(mContext, 0);
			((RectObject) obj).setLineWidth(Integer.parseInt(attr[8]));
			((RectObject) obj).setLineType(Integer.parseInt(attr[9]));
			Log.d(TAG, "Rect object");
			Log.d(TAG, "line type="+((RectObject) obj).getLineType());
			Log.d(TAG, "line width="+((RectObject) obj).getLineWidth());
		}
		else if(BaseObject.OBJECT_TYPE_RT.equals(attr[1]))				//realtime
		{
			Log.d(TAG, "Real time object");
			obj = new RealtimeObject(mContext, 0);
			((RealtimeObject) obj).setFormat(attr[21]);
		}
		else if(BaseObject.OBJECT_TYPE_TEXT.equals(attr[1]))			//text
		{
			obj = new TextObject(mContext, 0);
			try {
				obj.setContent(new String(attr[21].getBytes(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			Log.d(TAG, "Txt object");
			Log.d(TAG, "content="+obj.getContent());
		}
		else if(BaseObject.OBJECT_TYPE_RT_SECOND.equals(attr[1]))
		{
			obj = new RTSecondObject(mContext, 0);
		}
		else
		{
			Log.d(TAG, "Unknown object type: "+attr[1]);
			return null;
		}
		if(obj != null && !(obj instanceof MessageObject) )
		{
			obj.setIndex(Integer.parseInt(attr[0]));
			if((obj instanceof CounterObject)||
					obj instanceof JulianDayObject)
			{
				obj.setX(Integer.parseInt(attr[2])*2);
				obj.setWidth(Integer.parseInt(attr[4])*2-Integer.parseInt(attr[2])*2);
			}
			else
			{
				obj.setX(Integer.parseInt(attr[2]));
				obj.setWidth(Integer.parseInt(attr[4])-Integer.parseInt(attr[2]));
			}
			
			obj.setY(Integer.parseInt(attr[3]));
			
			obj.setHeight(Integer.parseInt(attr[5])-Integer.parseInt(attr[3]));
			obj.setDragable(Boolean.parseBoolean(attr[7]));
		}
//		Log.d(TAG, "index = "+obj.getIndex());
//		Log.d(TAG, "x = "+obj.getX());
//		Log.d(TAG, "y = "+obj.getY());
//		Log.d(TAG, "x end = "+obj.getXEnd());
//		Log.d(TAG, "y end = "+obj.getYEnd());
//		Log.d(TAG, "dragable = "+obj.getDragable());
//		Log.d(TAG, "*************************");
		return obj;
	}
	
	

	/**
	 * 设置需要解析的tlk文件名，可以是绝对路径或相对路径
	 */
	public void setTlk(String file) {
		if (file == null || file.isEmpty())
			return;
		if (file.startsWith(Configs.USB_ROOT_PATH) || file.startsWith(Configs.USB_ROOT_PATH2)) {
			mPath = file;
		} else {
			mPath = ConfigPath.getTlkPath() + file;
		}
	}
	/**
	 * 获取打印对象的内容缩略信息
	 * 暂时只支持文本对象，后续会添加对变量的支持
	 */
	public String getContentAbatract() {
		String content = "";
		BaseObject pObj;
		if (mPath == null || mPath.isEmpty()) {
			return null;
		}
		if (!new File(mPath + "/1.bin").exists()) {
			return null;
		}
		File file = new File(mPath+"/1.TLK");
		InputStream instream;
		try {
			instream = new FileInputStream(file);
			if(instream != null)
			 {
				InputStreamReader inputreader = new InputStreamReader(instream,"UTF-8");
	            BufferedReader buffreader = new BufferedReader(inputreader);
	            String line;
	            while ( (line = buffreader.readLine()) != null) {
	            	pObj = parseLine(line);
	            	if (pObj instanceof TextObject) {
	            		content += pObj.getContent();
	            	}
	            }
			 }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return content;
	}
	
	public int getDots() {
		return mDots;
	}
}

package com.industry.printer.object;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import com.industry.printer.EditTabActivity;

import android.content.Context;
import android.util.Log;

public class Fileparser {
	public static final String TAG="Fileparser";
	public static Context mContext;
	public static void parse(Context context, String name, Vector<BaseObject> objlist)
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
				 InputStreamReader inputreader = new InputStreamReader(instream);
                 BufferedReader buffreader = new BufferedReader(inputreader);
                 String line;
                 
                 while (( line = buffreader.readLine()) != null) {
                     Log.d(TAG, "line="+line);
                     pObj = parseLine(line);
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
	
	public static BaseObject parseLine(String str)
	{
		Log.d(TAG, "*************************");
		BaseObject obj = null;
		String [] attr = str.split("\\^",0);
		Log.d(TAG,"index="+str.indexOf("^"));
		for(int i=0; i< attr.length; i++)
		{
			//Log.d(TAG, "attr["+i+"]="+attr[i]);
		}
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
			obj.setSelected(true);
			obj.setContent(attr[21]);
			Log.d(TAG, "Message object");
			Log.d(TAG, "Message name="+obj.getContent());
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
			obj.setContent(attr[21]);
			Log.d(TAG, "Txt object");
			Log.d(TAG, "content="+obj.getContent());
		}
		else if(BaseObject.OBJECT_TYPE_RT_SECOND.equals(attr[1]))
		{
			
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
		Log.d(TAG, "index = "+obj.getIndex());
		Log.d(TAG, "x = "+obj.getX());
		Log.d(TAG, "y = "+obj.getY());
		Log.d(TAG, "x end = "+obj.getXEnd());
		Log.d(TAG, "y end = "+obj.getYEnd());
		Log.d(TAG, "dragable = "+obj.getDragable());
		Log.d(TAG, "*************************");
		return obj;
	}
}

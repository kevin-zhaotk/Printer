package com.industry.printer;

import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

import com.industry.printer.FileBrowserDialog.OnPositiveListener;
import com.industry.printer.ObjectInfoDialog.OnPositiveBtnListener;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.hardware.FpgaGpioOperation;
import com.industry.printer.hardware.HardwareJni;
import com.industry.printer.object.BarcodeObject;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.BinCreater;
import com.industry.printer.object.CounterObject;
import com.industry.printer.object.EllipseObject;
import com.industry.printer.object.Fileparser;
import com.industry.printer.object.GraphicObject;
import com.industry.printer.object.JulianDayObject;
import com.industry.printer.object.LineObject;
import com.industry.printer.object.MessageObject;
import com.industry.printer.object.RTSecondObject;
import com.industry.printer.object.RealtimeObject;
import com.industry.printer.object.RectObject;
import com.industry.printer.object.ShiftObject;
import com.industry.printer.object.TextObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class EditTabActivity extends Activity implements OnClickListener {
	public static final String TAG="EditTabActivity";
	
	public Context mContext;
	public EditScrollView mObjView;
	public HorizontalScrollView mHScroll;
	
	public String mObjName;
	/*************************
	 * file operation buttons
	 * ***********************/
	public Button mBtnNew;
	public Button mBtnSave;
	public Button mBtnSaveas;
	public Button mBtnOpen;
	
	/*************************
	 * object operation buttons
	 ************************/
	public ImageButton mBtnLeft;			//move left
	public ImageButton mBtnRight;			//move right 
	public ImageButton mBtnUp;				//move up
	public ImageButton mBtnDown;		//move down 
	public ImageButton mBtnZoomoutX;//zoom out
	public ImageButton mBtnZoominX;	//zoom in
	public ImageButton mBtnZoomoutY;//zoom out
	public ImageButton mBtnZoominY;	//zoom in
	public ImageButton mDel;
	public ImageButton mTrans;
	
	/************************
	 * 鏍戣帗娲�3-缂栬緫鐣岄潰鐩稿叧鎸夐挳
	 ***********************/
	public Button	mInsert;
	public Button	mTest;
	/************************
	 * create Object buttons
	 * **********************/
	public ImageButton 	mBtnText;
	public ImageButton 	mBtnCnt;
	public ImageButton 	mBtnBar;
	public ImageButton	mImage;
	public ImageButton 	mBtnDay;
	public ImageButton 	mBtnTime;
	public ImageButton 	mBtnLine;
	public ImageButton 	mBtnRect;
	public ImageButton 	mBtnEllipse;
	public ImageButton	mShift;
	public ImageButton	mScnd;
	/**********************
	 * Object Information Table
	 * **********************/
	public ScrollView mViewInfo;
	public Spinner mObjList;
	Button mShowInfo;
	
	public static Vector<BaseObject> mObjs;
	public ArrayAdapter<String> mNameAdapter;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_frame);
		this.setVisible(false);
		mContext = this.getApplicationContext();
		
		mObjs = new Vector<BaseObject>();
		mObjs.add(new MessageObject(mContext, 0));
		mHScroll = (HorizontalScrollView) findViewById(R.id.scrollView1);
		

//		mObjList = (Spinner) findViewById(R.id.object_list);
//		mNameAdapter = new ArrayAdapter<String>(this, R.layout.edit_spinner_item);//R.layout.object_list_item);
//		mNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		mObjList.setAdapter(mNameAdapter);
//		
//		mObjList.setOnItemSelectedListener(new OnItemSelectedListener(){
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				Debug.d(TAG,"==========objlist item " + position +" clicked"+" of "+mObjList.getCount());
//				clearCurObj();
//				setCurObj(position);
//				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_JUST);
//				
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//				// TODO Auto-generated method stub
//				Debug.d(TAG, "======onNothing selected");
//			}
//			
//		});
		
		mBtnNew = (Button) findViewById(R.id.btn_new);
		mBtnNew.setOnClickListener(this);
				
		mTest = (Button) findViewById(R.id.btn_temp_4);
		mTest.setOnClickListener(this);
		
		/*initialize the object list spinner*/
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		Debug.d(TAG, "event:"+event.toString());
		InputMethodManager manager = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
		Debug.d(TAG, "ime is active? "+manager.isActive());
		manager.hideSoftInputFromWindow(EditTabActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//			manager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
		return true;
	}
	
	/**
	 * REFRESH_OBJECT_CHANGED
	 *   some object changes, need to resave the tlk&bin files
	 */
	public static final int REFRESH_OBJECT_CHANGED=0;
	/**
	 * REFRESH_OBJECT_PROPERTIES
	 *   the object properties changed
	 */
	public static final int REFRESH_OBJECT_PROPERTIES=1;
	/**
	 * REFRESH_OBJECT_JUST
	 *   just refresh the object list, no need to resave tlk or bin files
	 */
	public static final int REFRESH_OBJECT_JUST=2;
	
	public Handler mObjRefreshHandler = new Handler(){
		@Override
		public void  handleMessage (Message msg)
		{
			
			switch (msg.what) {
			
			case REFRESH_OBJECT_CHANGED:	
			
				mNameAdapter.clear();
				for(BaseObject o:mObjs)
				{
					if(o instanceof MessageObject)
						mNameAdapter.add(o.getContent());
					else if(o instanceof TextObject)
						mNameAdapter.add(mContext.getString(R.string.object_text));
					else if(o instanceof CounterObject)
						mNameAdapter.add(mContext.getString(R.string.object_counter));
					else if(o instanceof BarcodeObject)
						mNameAdapter.add(mContext.getString(R.string.object_bar));
					else if(o instanceof GraphicObject)
						mNameAdapter.add(mContext.getString(R.string.object_pic));
					else if(o instanceof JulianDayObject)
						mNameAdapter.add(mContext.getString(R.string.object_julian));
					else if(o instanceof RealtimeObject)
						mNameAdapter.add(mContext.getString(R.string.object_realtime));
					else if(o instanceof LineObject)
						mNameAdapter.add(mContext.getString(R.string.object_line));
					else if(o instanceof RectObject)
						mNameAdapter.add(mContext.getString(R.string.object_rect));
					else if(o instanceof EllipseObject)
						mNameAdapter.add(mContext.getString(R.string.object_ellipse));
					else if(o instanceof ShiftObject)
						mNameAdapter.add(mContext.getString(R.string.object_shift));
					else if(o instanceof RTSecondObject)
						mNameAdapter.add(mContext.getString(R.string.object_second));
					else
						System.out.println("Unknown Object type");
				}
				//mNameAdapter.notifyDataSetChanged();
				//mObjList.setAdapter(mNameAdapter);
				//mObjList.invalidate();
				//selfInfoEnable(obj);
				OnPropertyChanged(true);
				break;
			case REFRESH_OBJECT_PROPERTIES:
				OnPropertyChanged(true);
			case REFRESH_OBJECT_JUST:
				mNameAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
			
			BaseObject obj = getCurObj();
			//Debug.d(TAG, "=====obj:"+obj.mId);
			if(obj != null) {
				makeObjToCenter((int)obj.getX());
			}
			Debug.d(TAG, "=========");
		}
	};
	
	private void makeObjToCenter(int x)
	{
		Debug.d(TAG, "current scrollX="+mHScroll.getScrollX());
		if(x - mHScroll.getScrollX() > 500)
		{
			mHScroll.scrollTo(x-300, 0);
		}
	}
	
	public static BaseObject getCurObj()
	{
	
		for(BaseObject obj : mObjs)
		{
			if(obj.getSelected())
				return obj;
		}
		if(mObjs!=null&& mObjs.size()>0)
		{
			BaseObject object=mObjs.get(0);
			object.setSelected(true);
			return object;
		}
		return null;
	}
	
	public static void clearCurObj()
	{
		for(BaseObject obj : mObjs)
		{
			obj.setSelected(false);
		}
	}
	public static void setCurObj(int i)
	{
		if(i >= mObjs.size())
			return;
		BaseObject obj=mObjs.get(i);
		obj.setSelected(true);
	}
	
	public static float getNextXcor()
	{
		float x=0;
		for(BaseObject obj : mObjs)
		{
			if(obj instanceof MessageObject)
				continue;
			x = obj.getXEnd()>x ? obj.getXEnd() : x;
		}
		return x;
	}


	/**
	 * 鍝嶅簲save/saveas鎿嶄綔锛屼繚瀛榯lk瀵硅薄
	 * @param path 鏂囦欢璺緞锛堟瘡鏉′俊鎭殑鐩綍锛�
	 * @param create 鏄惁闇�瑕佸垱寤轰竴涓俊鎭洰褰�
	 */
	public void saveObjFile(String path, boolean create)
	{
		int i=1;
		FileWriter fw=null;
		BufferedWriter bw=null;
		File file = new File(path);
		Debug.d(TAG, "=====>saveObjFile path="+path);
		if(create==true)
		{
			if(file.exists()){
				Toast.makeText(mContext, R.string.str_tlk_already_exist, Toast.LENGTH_LONG);
				return;
			}
			if(!file.mkdirs())
			{
				Debug.d(TAG, "create dir error "+file.getPath());
				Toast.makeText(mContext, R.string.str_createfile_failure, Toast.LENGTH_LONG);
				return;
			}
		}
		
		File tlk = new File(path+"/1.TLK");
		try {
			if(!tlk.exists() && !tlk.createNewFile()){
				Debug.d(TAG, "create error "+tlk.getPath());
				return;
			}
			fw = new FileWriter(tlk);
			bw = new BufferedWriter(fw);
			for(BaseObject o: mObjs)
			{
				if(o instanceof RealtimeObject)
				{
					System.out.println("******"+BaseObject.intToFormatString(i, 3)+"^"+o.toString());
					bw.write(BaseObject.intToFormatString(i, 3)+"^"+o.toString());
					bw.newLine();
					o.setIndex(i++);
					for(BaseObject so : ((RealtimeObject) o).getSubObjs())
					{
						System.out.println("******"+BaseObject.intToFormatString(i, 3)+"^"+so.toString());
						bw.write(BaseObject.intToFormatString(i, 3)+"^"+so.toString());
						bw.newLine();
						so.setIndex(i++);
					}
				}
				else
				{
					Debug.d(TAG, "filestr="+BaseObject.intToFormatString(i, 3) +"^"+o.toString());
					bw.write(BaseObject.intToFormatString(i, 3)+"^"+o.toString());
					bw.newLine();
					o.setIndex(i++);
				}
				
			}
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * HANDLER_MESSAGE_OPEN
	 *  Handler message for open tlk file
	 */
	public static final int HANDLER_MESSAGE_OPEN=0;
	/**
	 * HANDLER_MESSAGE_SAVE
	 *   Handler message for save event happens
	 */
	public static final int HANDLER_MESSAGE_SAVE=1;
	/**
	 * HANDLER_MESSAGE_SAVEAS
	 *   Handler message for saveas event happens
	 */
	public static final int HANDLER_MESSAGE_SAVEAS=2;
	/**
	 * HANDLER_MESSAGE_IMAGESELECT
	 *   Handler message for image object selected
	 */
	public static final int HANDLER_MESSAGE_IMAGESELECT=3;
	/**
	 * HANDLER_MESSAGE_DISMISSDIALOG
	 *   Handler message for dismiss loading dialog
	 */
	public static final int HANDLER_MESSAGE_DISMISSDIALOG=4;
	
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {  
			//	String f;
			boolean createfile=false;
            switch (msg.what) {   
            	case HANDLER_MESSAGE_OPEN:		//open
            		Debug.d(TAG, "open file="+FileBrowserDialog.file());
            		String tlkfile=FileBrowserDialog.file();
            		File tlk=new File(tlkfile);
            		mObjName = tlk.getParent();
            		if(tlkfile != null && tlk.isFile())
            		{
	    				Fileparser.parse(mContext, tlkfile, mObjs);
	    				clearCurObj();
	    				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
            		}
            		break;
            	case HANDLER_MESSAGE_SAVE:		//saveas
            		Debug.d(TAG, "save as file="+FileBrowserDialog.file()+"/"+FileBrowserDialog.getObjName());
            		mObjName = FileBrowserDialog.file()+"/"+FileBrowserDialog.getObjName();
            		createfile=true;
            	case HANDLER_MESSAGE_SAVEAS:    //save
            		progressDialog();
            		if(mObjName != null)
            		{
            			saveObjFile(mObjName, createfile);
            		}
            		//drawAllBmp(mObjName);
            		dismissProgressDialog();
            		OnPropertyChanged(false);
            		break;
            	case HANDLER_MESSAGE_IMAGESELECT:		//select image
            		File file = new File(FileBrowserDialog.file());
            		if(!file.exists() || !GraphicObject.isPic(FileBrowserDialog.file()))
            		{
            			Debug.d(TAG, "Image file error");
            			return;
            		}
            		clearCurObj();
            		GraphicObject o = new GraphicObject(mContext, getNextXcor());
            		o.setImage(FileBrowserDialog.file());
    				mObjs.add(o);    	
    				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_JUST);
            		break;
            	case HANDLER_MESSAGE_DISMISSDIALOG:
            		mProgressDialog.dismiss();
            		break;
            }   
            super.handleMessage(msg);   
       } 
	};
	
	
	public final int LEFT_KEY=1;
	public final int RIGHT_KEY=2;
	public final int UP_KEY=3;
	public final int DOWN_KEY=4;
	public final int ZOOMX_IN_KEY=5;
	public final int ZOOMX_OUT_KEY=6;
	public final int ZOOMY_IN_KEY=7;
	public final int ZOOMY_OUT_KEY=8;
	
	Handler mKeyRepeatHandler = new Handler(){
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case LEFT_KEY:
					Debug.d(TAG, "left key pressed");
					leftKeyPressed();
					break;
				case RIGHT_KEY:
					Debug.d(TAG, "right key pressed");
					rightKeyPressed();
					break;
				case UP_KEY:
					Debug.d(TAG, "up key pressed");
					upKeyPressed();
					break;
				case	DOWN_KEY:
					Debug.d(TAG, "down key pressed");
					downKeyPressed();
					break;
				case ZOOMX_IN_KEY:
					Debug.d(TAG, "zoom x  in key pressed");
					zoomInXKeyPressed();
					break;
				case ZOOMX_OUT_KEY:
					Debug.d(TAG, "zoom x out key pressed");
					zoomOutXKeyPressed();
					break;
				case ZOOMY_IN_KEY:
					Debug.d(TAG, "zoom y in key pressed");
					zoomInYKeyPressed();
					break;
				case ZOOMY_OUT_KEY:
					Debug.d(TAG, "zoom y out key pressed");
					zoomOutYKeyPressed();
					break;
				default:
					Debug.d(TAG, "unknow key repeat ");
					break;
			}
			mKeyRepeatHandler.sendEmptyMessageDelayed(msg.what, 200);
		}
	};
	int getTouchedObj(float x, float y)
	{
		int i=1;
		BaseObject o;
		for(i=1; mObjs!= null &&i< mObjs.size(); i++)
		{
			o = mObjs.get(i);
			if(x>= o.getX() && x<= o.getXEnd() && y >=o.getY() && y <= o.getYEnd())
			{
				Debug.d(TAG, "Touched obj = "+i);
				return i;
			}
		}
		Debug.d(TAG, "no object Touched");
		return -1;
	}
	
	public void drawAllBmp(String f)
	{
		int width=0;
		Paint p=new Paint();
		if(mObjs==null || mObjs.size() <= 0)
			return ;
		for(BaseObject o:mObjs)
		{
			width = (int)(width > o.getXEnd() ? width : o.getXEnd());
		}
		
		Bitmap bmp = Bitmap.createBitmap(width , Configs.gFixedRows, Bitmap.Config.ARGB_8888);
		Debug.d(TAG, "drawAllBmp width="+width+", height="+880);
		Canvas can = new Canvas(bmp);
		can.drawColor(Color.WHITE);
		for(BaseObject o:mObjs)
		{
			if((o instanceof MessageObject)	)
				continue;
			
			if(o instanceof CounterObject)
			{
				o.drawVarBitmap(f);
			}
			else if(o instanceof RealtimeObject)
			{
				Bitmap t = ((RealtimeObject)o).getBgBitmap(mContext,f);
				can.drawBitmap(t, o.getX(), o.getY(), p);
				BinCreater.recyleBitmap(t);
			}
			else if(o instanceof JulianDayObject)
			{
				o.drawVarBitmap(f);
			}
			else if(o instanceof ShiftObject)
			{
				o.drawVarBitmap(f);
			}
			else
			{
				Bitmap t = o.getScaledBitmap(mContext);
				can.drawBitmap(t, o.getX(), o.getY(), p);
				BinCreater.recyleBitmap(t);
			}
		//can.drawText(mContent, 0, height-30, mPaint);
		}
		//BinCreater.saveBitmap(bmp, "back.png");
		Debug.d(TAG,"******background png width="+bmp.getWidth()+"height="+bmp.getHeight());
		BinCreater.create(bmp, 0);
		BinCreater.saveBin(f+"/1.bin", width, Configs.gDots);
		return ;
	}
	
	
	public ProgressDialog mProgressDialog;
	public Thread mProgressThread;
	public boolean mProgressShowing;
	public void progressDialog()
	{
		mProgressDialog = ProgressDialog.show(EditTabActivity.this, "", getResources().getString(R.string.strSaving), true,false);
		mProgressShowing = true;
		
		mProgressThread = new Thread(){
			
			@Override
			public void run(){
				
				try{
					for(;mProgressShowing==true;)
					{
						Thread.sleep(2000);
					}
					mHandler.sendEmptyMessage(HANDLER_MESSAGE_DISMISSDIALOG);
				}catch(Exception e)
				{
					
				}
			}
		};
		mProgressThread.start();
	}
	
	public void dismissProgressDialog()
	{
		mProgressShowing=false;
		//Thread thread = mProgressThread;
		//thread.interrupt();
		
	}
	
	private void leftKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setX(obj.getX() - 4);
		}
		else
			obj.setX(obj.getX() - 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_PROPERTIES);
	}
	
	private void rightKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setX(obj.getX() + 4);
		}
		else
			obj.setX(obj.getX() + 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_PROPERTIES);
	}
	
	private void upKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setY(obj.getY() - 4);
		}
		else
			obj.setY(obj.getY() - 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_PROPERTIES);
	}
	
	private void downKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setY(obj.getY() + 4);
		}
		else
			obj.setY(obj.getY() + 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_PROPERTIES);
	}
	
	private void zoomOutXKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setWidth(obj.getWidth() + 4);
		}
		else
			obj.setWidth(obj.getWidth() + 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_PROPERTIES);
	}
	
	private void zoomInXKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setWidth(obj.getWidth() - 4);
		}
		else
			obj.setWidth(obj.getWidth() - 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_CHANGED);
	}
	
	private void zoomOutYKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setHeight(obj.getHeight() + 4);
		}
		else
			obj.setHeight(obj.getHeight() + 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_PROPERTIES);
	}
	
	private void zoomInYKeyPressed()
	{
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		if(obj instanceof RealtimeObject)
		{
			((RealtimeObject)obj).setHeight(obj.getHeight() - 4);
		}
		else
			obj.setHeight(obj.getHeight() - 4);
		mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_PROPERTIES);
	}
	
	public boolean mPropertyChanged=false;
	private void OnPropertyChanged(boolean state)
	{
		mPropertyChanged=state;
	}
	
	public boolean isPropertyChanged()
	{
		return mPropertyChanged;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
			case R.id.btn_new:
				
				break;
			case R.id.btn_open:	//test fpga-gpio write
				
				
				break;
			case R.id.btn_save:
				HardwareJni.open("/dev/rtc0");
				break;
			case R.id.btn_saveas:
				FpgaGpioOperation.open("/dev/fpga-gpio");
				break;
			case R.id.btn_temp_4:
				new Thread(){
					@Override
					public void run() {
						char[] buffer = new char[1024*1024];
						for(int i=0; i<buffer.length; i++) {
							buffer[i] = 0x55;
						}
						FpgaGpioOperation.write(buffer);
					}
				}.start();
				break;
			default:
				break;
		}
	}
}

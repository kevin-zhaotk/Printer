package com.industry.printer;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.graphics.YuvImage;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.BaseObj;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.industry.printer.FileFormat.SystemConfigFile;
import com.industry.printer.Utils.Debug;
import com.industry.printer.hardware.ExtGpio;
import com.industry.printer.hardware.PWMAudio;
import com.industry.printer.object.BarcodeObject;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.CounterObject;
import com.industry.printer.object.EllipseObject;
import com.industry.printer.object.GraphicObject;
import com.industry.printer.object.JulianDayObject;
import com.industry.printer.object.LetterHourObject;
import com.industry.printer.object.LineObject;
import com.industry.printer.object.MessageObject;
import com.industry.printer.object.RealtimeObject;
import com.industry.printer.object.RectObject;
import com.industry.printer.object.TextObject;
import com.industry.printer.object.WeekDayObject;
import com.industry.printer.object.WeekOfYearObject;
import com.industry.printer.ui.CustomerAdapter.PopWindowAdapter;
import com.industry.printer.ui.CustomerAdapter.PopWindowAdapter.IOnItemClickListener;
import com.industry.printer.ui.CustomerDialog.CustomerDialogBase;
import com.industry.printer.ui.CustomerDialog.CustomerDialogBase.OnPositiveListener;
import com.industry.printer.ui.CustomerDialog.LoadingDialog;
import com.industry.printer.ui.CustomerDialog.MessageBrowserDialog;
import com.industry.printer.ui.CustomerDialog.MessageSaveDialog;
import com.industry.printer.ui.CustomerDialog.ObjectInfoDialog;
import com.industry.printer.ui.CustomerDialog.ObjectInfoDialog.OnPositiveBtnListener;
import com.industry.printer.ui.CustomerDialog.ObjectInfoDialog.onDeleteListener;
import com.industry.printer.ui.CustomerDialog.ObjectInsertDialog;
import com.industry.printer.ui.MessageDisplayManager;
import com.industry.printer.widget.PopWindowSpiner;

public class EditTabSmallActivity extends Fragment implements OnClickListener, OnTouchListener {
	public static final String TAG="EditTabSmallActivity";
	
	public Context mContext;
	public EditScrollView mObjView;
	public MessageDisplayManager mMsgManager;
	private RelativeLayout mRelatively;
	
	public String mObjName;
	public MessageTask mMsgTask;
	/*************************
	 * file operation buttons
	 * ***********************/
	private RelativeLayout mBtnNew;
	private RelativeLayout mBtnSave;
	private RelativeLayout mBtnSaveAs;
	private RelativeLayout mBtnCursor;
	private RelativeLayout mBtnInsert;
	private RelativeLayout mBtnOpen;
	
	private RelativeLayout mDel;
	private ImageButton mTrans;
	
	private RelativeLayout mBtnUp;
	private RelativeLayout mBtnDown;
	private RelativeLayout mBtnLeft;
	private RelativeLayout mBtnRight;
	private RelativeLayout mBtnDetail;
	private RelativeLayout mBtnList;
	private RelativeLayout mBtnZout;
	private RelativeLayout mBtnZin;
	private ImageView mBtnWide;
	private ImageView mBtnNarrow;
	/************************
	 * create Object buttons
	 * **********************/
	private ImageButton 	mBtnText;
	private ImageButton 	mBtnCnt;
	private ImageButton 	mBtnBar;
	private ImageButton	mImage;
	private ImageButton 	mBtnDay;
	private ImageButton 	mBtnTime;
	private ImageButton 	mBtnLine;
	private ImageButton 	mBtnRect;
	private ImageButton 	mBtnEllipse;
	private ImageButton	mShift;
	private ImageButton	mScnd;
	
	/********************************
	 * when configure changed
	 *******************************/
	private TextView tv_btnNew;
	private TextView tv_btnOpen;
	private TextView tv_btnSave;
	private TextView tv_btnSaveas;
	private TextView tv_btnInsert;
	private TextView tv_btnCursor;
	private TextView tv_btnDetail;
	private TextView tv_btnList;
	/**********************
	 * Object Information Table
	 * **********************/
	private ScrollView mViewInfo;
	private PopWindowSpiner mObjSpiner;
	private PopWindowAdapter mNameAdapter;
	private RelativeLayout mShowInfo;
	
	
	public EditTabSmallActivity() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.edit_small_frame, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);

		mContext = getActivity();

		mBtnNew = (RelativeLayout) getView().findViewById(R.id.btn_new);
		mBtnNew.setOnClickListener(this);
		mBtnNew.setOnTouchListener(this);
		
		mBtnSave = (RelativeLayout) getView().findViewById(R.id.btn_save);
		mBtnSave.setOnClickListener(this);
		mBtnSave.setOnTouchListener(this);

		mBtnSaveAs = (RelativeLayout) getView().findViewById(R.id.btn_saveas);
		mBtnSaveAs.setOnClickListener(this);
		mBtnSaveAs.setOnTouchListener(this);
		
		mBtnInsert = (RelativeLayout) getView().findViewById(R.id.btn_insert);
		mBtnInsert.setOnClickListener(this);
		mBtnInsert.setOnTouchListener(this);
		
		mBtnOpen = (RelativeLayout) getView().findViewById(R.id.btn_open);
		mBtnOpen.setOnClickListener(this);
		mBtnOpen.setOnTouchListener(this);
		
		mBtnCursor = (RelativeLayout) getView().findViewById(R.id.btn_cursor);
		mBtnCursor.setOnClickListener(this);
		mBtnCursor.setOnTouchListener(this);
		
		mBtnList = (RelativeLayout) getView().findViewById(R.id.btn_list);
		mBtnList.setOnClickListener(this);
		
//		mHScroll = (HorizontalScrollView) getView().findViewById(R.id.scrollView1);
//		mObjView = (EditScrollView) getView().findViewById(R.id.editView);
//		mObjView.setParent(mHScroll);
//		mObjView.setOnTouchListener(this);
//		mObjView.setTask(mMsgTask);

		mMsgTask = new MessageTask(mContext);

		MessageObject msgObject = new MessageObject(mContext, 0);
		msgObject.setType(SystemConfigFile.getInstance(mContext).getParam(30));
		mMsgTask.addObject(msgObject);  //
		mRelatively = (RelativeLayout) getView().findViewById(R.id.edit_view);
		mMsgManager = new MessageDisplayManager(mContext, mRelatively, mMsgTask);

		/*
		mObjList = (Spinner) getView().findViewById(R.id.object_list);
		mNameAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item);//R.layout.object_list_item);
		mNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mObjList.setAdapter(mNameAdapter);
		
		mObjList.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Debug.d(TAG,"==========objlist item " + position +" clicked"+" of "+mObjList.getCount());
				clearCurObj();
				setCurObj(position);
				mObjRefreshHandler.sendEmptyMessage(REFRESH_OBJECT_JUST);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				Debug.d(TAG, "======onNothing selected");
			}
			
		});
*/
		mShowInfo = (RelativeLayout) getView().findViewById(R.id.btn_detail);
		mShowInfo.setOnClickListener(this);
		mShowInfo.setOnTouchListener(this);
		
		mBtnUp = (RelativeLayout) getView().findViewById(R.id.btn_up);
		mBtnUp.setOnClickListener(this);
		mBtnUp.setOnTouchListener(this);
		
		mBtnDown = (RelativeLayout) getView().findViewById(R.id.btn_down);
		mBtnDown.setOnClickListener(this);
		mBtnDown.setOnTouchListener(this);
		
		mBtnLeft = (RelativeLayout) getView().findViewById(R.id.btn_left);
		mBtnLeft.setOnClickListener(this);
		mBtnLeft.setOnTouchListener(this);
		
		mBtnRight = (RelativeLayout) getView().findViewById(R.id.btn_right);
		mBtnRight.setOnClickListener(this);
		mBtnRight.setOnTouchListener(this);
		
		mBtnZout = (RelativeLayout) getView().findViewById(R.id.btn_zoomOut);
		mBtnZout.setOnClickListener(this);
		mBtnZout.setOnTouchListener(this);
		
		mBtnZin = (RelativeLayout) getView().findViewById(R.id.btn_zoomIn);
		mBtnZin.setOnClickListener(this);
		mBtnZin.setOnTouchListener(this);

		mBtnWide = (ImageView) getView().findViewById(R.id.wide_btn);
		mBtnWide.setOnClickListener(this);
		mBtnWide.setOnTouchListener(this);

		mBtnNarrow = (ImageView) getView().findViewById(R.id.narrow_btn);
		mBtnNarrow.setOnClickListener(this);
		mBtnNarrow.setOnTouchListener(this);
		// mTrans = (ImageButton) getView().findViewById(R.id.btn_trans);
				
		mObjName = null;
		/*initialize the object list spinner*/

		initAdapter();
	}
	
	
	
	public void onConfigureChanged() {
		tv_btnNew = (TextView) getView().findViewById(R.id.btn_new_tv);
		tv_btnNew.setText(R.string.str_btn_new);

		tv_btnOpen = (TextView) getView().findViewById(R.id.btn_open_tv);
		tv_btnOpen.setText(R.string.str_btn_open);
		
		tv_btnSave = (TextView) getView().findViewById(R.id.btn_save_tv);
		tv_btnSave.setText(R.string.str_btn_save);
		
		tv_btnSaveas = (TextView) getView().findViewById(R.id.btn_saveas_tv);
		tv_btnSaveas.setText(R.string.str_btn_saveas);
		
		tv_btnInsert = (TextView) getView().findViewById(R.id.btn_insert_tv);
		tv_btnInsert.setText(R.string.str_edit_btn_insert);
		
		tv_btnCursor = (TextView) getView().findViewById(R.id.btn_cursor_tv);
		tv_btnCursor.setText(R.string.cursor);
		
		tv_btnDetail = (TextView) getView().findViewById(R.id.btn_detail_tv);
		tv_btnDetail.setText(R.string.str_btn_detail);
		
		tv_btnList = (TextView) getView().findViewById(R.id.btn_list_tv);
		tv_btnList.setText(R.string.strInfoname);
	}
	
	private void initAdapter() {
		mObjSpiner = new PopWindowSpiner(mContext);
		mObjSpiner.setFocusable(true);
		mObjSpiner.setWidth(300);
		mNameAdapter = new PopWindowAdapter(mContext, null);
		
		mObjSpiner.setAdapter(mNameAdapter);
		// mObjSpiner.setAttachedView(mBtnList);
		mObjSpiner.setOnItemClickListener(new IOnItemClickListener() {
			
			@Override
			public void onItemClick(int index) {
				mMsgManager.setSelect(index);
			}
		});
	}


	public static final int OBJECT_INSERT = 1;

	public static final int OBJECT_REMOVE = 2;

	public static final int OBJECT_UPDATE = 3;

	public static final int OBJECT_UPDATE_CONTENT = 4;
	
	
	public Handler mObjRefreshHandler = new Handler(){
		@Override
		public void  handleMessage (Message msg)
		{
			Debug.d(TAG, "====== 44444");
			BaseObject obj = (BaseObject) msg.obj;
			switch (msg.what) {

				case OBJECT_INSERT:
					mMsgManager.add(obj);
					mMsgTask.addObject(obj);
					break;
				case OBJECT_REMOVE:

					mMsgManager.remove(obj);
					break;
				case OBJECT_UPDATE:
					mMsgManager.update(obj);
					break;
				case OBJECT_UPDATE_CONTENT:
					mMsgManager.updateDraw(obj);
					break;
				default:
					break;
			}
			OnPropertyChanged(true);
		}
	};
	
	private void makeObjToCenter(int x)
	{
	}
	
	public BaseObject getCurObj()
	{
		Debug.d(TAG, "--->getcurobj");
		ArrayList<BaseObject> objects = mMsgTask.getObjects();
		for(BaseObject obj : objects)
		{
			if(obj.getSelected())
				return obj;
		}
		if(objects!=null&& objects.size()>0)
		{
			BaseObject object=objects.get(0);
			object.setSelected(true);
			return object;
		}
		return null;
	}

	public void setCurObj(int i)
	{
		ArrayList<BaseObject> objects = mMsgTask.getObjects();
		if(i >= objects.size())
			return;
		for(BaseObject obj : objects)
		{
			obj.setSelected(false);
		}
		BaseObject obj=objects.get(i);
		obj.setSelected(true);
	}

	
	public float[] getNextXcor()
	{
		float[] x = new float[2];
		ArrayList<BaseObject> objects = mMsgTask.getObjects();
		MessageObject msgobj = mMsgTask.getMsgObject();
		// 如果使用了光標，就以光標的座標爲基準
		if(msgobj.getSelected()) {
			Debug.d(TAG, "--->!!!!fuck msg selected");
			x[0] = msgobj.getX();
			x[1] = msgobj.getY();
			return x;
		}
		for(BaseObject obj : objects)
		{
			if(obj instanceof MessageObject)
				continue;
			Debug.d(TAG, "--->obj: " + obj.mId + " -- xend = " + obj.getXEnd() + " x[0]=" + x[0]);
			x[0] = obj.getXEnd()>x[0] ? obj.getXEnd() : x[0];
			x[1] = 0;
			Debug.d(TAG, "--->x[0] = " + x[0]);
		}
		return x;
	}
	
	public void deleteSelected() {
		onDelete();
	}

	/**
	 * HANDLER_MESSAGE_OPEN
	 *  Handler message for open tlk file
	 */
	public static final int HANDLER_MESSAGE_OPEN=0;
	
	public static final int HANDLER_MESSAGE_OPEN_SUCCESS = 11;
	/**
	 * HANDLER_MESSAGE_SAVEAS
	 *   Handler message for save event happens
	 */
	public static final int HANDLER_MESSAGE_SAVEAS=1;
	/**
	 * HANDLER_MESSAGE_SAVE
	 *   Handler message for saveas event happens
	 */
	public static final int HANDLER_MESSAGE_SAVE=2;
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
	
	/**
	 * HANDLER_MESSAGE_INSERT_MSG
	 * Handler message for insert a new object
	 */
	public static final int HANDLER_MESSAGE_INSERT_OBJECT = 6;
	
	/**
	 * HANDLER_MESSAGE_SAVE_SUCCESS 
	 * this message will be sent out when MessageTask Async save-task run success
	 */
	public static final int HANDLER_MESSAGE_SAVE_SUCCESS = 10;
	
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {  
			//	String f;
			boolean createfile=false;
            switch (msg.what) {   
            	case HANDLER_MESSAGE_OPEN:		//open
            		Debug.d(TAG, "open file="+ MessageBrowserDialog.getSelected());
            		mObjName = MessageBrowserDialog.getSelected();
            		if (mObjName == null || mObjName.isEmpty()) {
						break;
					}
            		((MainActivity)mContext).onEditTitleChanged(mObjName);
            		progressDialog();
            		new Thread(new Runnable() {
						@Override
						public void run() {
							mMsgTask = new MessageTask(mContext, mObjName);
							mHandler.sendEmptyMessage(HANDLER_MESSAGE_OPEN_SUCCESS);
						}
					}).start();
            		break;
            	case HANDLER_MESSAGE_OPEN_SUCCESS:
            		mMsgManager.fill(mMsgTask);
					// 默認選中第一個非消息對象
					mMsgManager.setSelect(1);
					dismissProgressDialog();
            		break;
            	case HANDLER_MESSAGE_SAVEAS:		//saveas
            		Debug.d(TAG, "save as file="+MessageSaveDialog.getTitle());
            		mObjName = MessageSaveDialog.getTitle();
            		createfile=true;
            	case HANDLER_MESSAGE_SAVE:    //save
            		Debug.d(TAG, "--->save");
            		progressDialog();
            		if (mObjName == null || mMsgTask == null) {
            			Debug.e(TAG, "--->nothing to save");
            			dismissProgressDialog();
						break;
					}
            		// mMsgTask = new MessageTask(mContext, mObjName);
            		mMsgTask.setName(mObjName);
            		mMsgTask.createTaskFolderIfNeed();
            		((MainActivity)mContext).onEditTitleChanged(mObjName);
            		mMsgTask.save(mHandler);
            		break;
            	case HANDLER_MESSAGE_SAVE_SUCCESS:
            		dismissProgressDialog();
            		OnPropertyChanged(false);
            		break;
            	case HANDLER_MESSAGE_IMAGESELECT:		//select image
            		
            		break;
            	case HANDLER_MESSAGE_DISMISSDIALOG:
            		mProgressDialog.dismiss();
            		break;
            	case HANDLER_MESSAGE_INSERT_OBJECT:
            		Bundle bundle = msg.getData();
            		if (bundle == null) {
						break;
					}
            		Debug.d(TAG, "--->mContext: " + mContext);
            		String type = bundle.getString(ObjectInsertDialog.OBJECT_TYPE);
            		String format = bundle.getString(ObjectInsertDialog.OBJECT_FORMAT);
            		float[] cur = getNextXcor();
            		if (BaseObject.OBJECT_TYPE_TEXT.equals(type)) {
            			TextObject text = new TextObject(mContext, cur[0]);
            			text.setY(cur[1]);
						onInsertObject(text);
					} else if (BaseObject.OBJECT_TYPE_CNT.equals(type)) {
						CounterObject counter = new CounterObject(mContext, cur[0]);
						counter.setY(cur[1]);
						onInsertObject(counter);
					} else if (BaseObject.OBJECT_TYPE_RT.equals(type)) {
						RealtimeObject time = new RealtimeObject(mContext, cur[0]);
						time.setY(cur[1]);
						onInsertObject(time);
					} else if (BaseObject.OBJECT_TYPE_JULIAN.equals(type)) {
						JulianDayObject julian = new JulianDayObject(mContext, cur[0]);
						julian.setY(cur[1]);
						onInsertObject(julian);
					} else if (BaseObject.OBJECT_TYPE_RECT.equals(type)) {
						RectObject rect = new RectObject(mContext, cur[0]);
						rect.setY(cur[1]);
						onInsertObject(rect);
					} else if (BaseObject.OBJECT_TYPE_LINE.equals(type)) {
						LineObject line = new LineObject(mContext, cur[0]);
						line.setY(cur[1]);
						onInsertObject(line);
					} else if (BaseObject.OBJECT_TYPE_ELLIPSE.equals(type)) {
						EllipseObject ellipse = new EllipseObject(mContext, cur[0]);
						ellipse.setY(cur[1]);
						onInsertObject(ellipse);
					} else if (BaseObject.OBJECT_TYPE_BARCODE.equals(type)) {
						BarcodeObject bar = new BarcodeObject(mContext, cur[0]);
						boolean source = bundle.getBoolean(ObjectInsertDialog.OBJECT_SOURCE, false);
						bar.setY(cur[1]);
						if (source) {
							bar.setSource(source);
							bar.setCode("QR");
						}
						onInsertObject(bar);
					} else if (BaseObject.OBJECT_TYPE_GRAPHIC.equalsIgnoreCase(type)) {
						GraphicObject image = new GraphicObject(mContext, cur[0]);
						image.setY(cur[1]);
						onInsertObject(image);
					} else if (BaseObject.OBJECT_TYPE_LETTERHOUR.equalsIgnoreCase(type)) {
						LetterHourObject lh = new LetterHourObject(mContext, cur[0]);
						lh.setY(cur[1]);
						onInsertObject(lh);
					} else if (BaseObject.OBJECT_TYPE_WEEKOFYEAR.equalsIgnoreCase(type)) {
						WeekOfYearObject lh = new WeekOfYearObject(mContext, cur[0]);
						lh.setY(cur[1]);
						onInsertObject(lh);
					} else if (BaseObject.OBJECT_TYPE_WEEKDAY.equalsIgnoreCase(type)) {
						WeekDayObject lh = new WeekDayObject(mContext, cur[0]);
						lh.setY(cur[1]);
						onInsertObject(lh);
					}
            		
            		break;
            }   
            super.handleMessage(msg);   
       } 
	};
	
	int getTouchedObj(float x, float y)
	{
		int i=1;
		BaseObject o;
		ArrayList<BaseObject> objects = mMsgTask.getObjects();
		for(i=1; objects!= null &&i< objects.size(); i++)
		{
			o = objects.get(i);
			if(x>= o.getX() && x<= o.getXEnd() && y >=o.getY() && y <= o.getYEnd())
			{
				Debug.d(TAG, "Touched obj = "+i);
				return i;
			}
		}
		Debug.d(TAG, "no object Touched");
		return -1;
	}
	
	public LoadingDialog mProgressDialog;
	public Thread mProgressThread;
	public boolean mProgressShowing;
	public void progressDialog()
	{
		mProgressDialog = LoadingDialog.show(mContext, R.string.strSaving);
		Debug.d(TAG, "--->progress dialog show");
//		mProgressShowing = true;
//		
//		mProgressThread = new Thread(){
//			
//			@Override
//			public void run(){
//				
//				try{
//					for(;mProgressShowing==true;)
//					{
//						Thread.sleep(2000);
//					}
//					mHandler.sendEmptyMessage(HANDLER_MESSAGE_DISMISSDIALOG);
//				}catch(Exception e)
//				{
//					
//				}
//			}
//		};
//		mProgressThread.start();
	}
	
	public void dismissProgressDialog()
	{
		mProgressShowing=false;
		mProgressDialog.cancel();
		Debug.d(TAG, "--->progress dialog cancel");
		//Thread thread = mProgressThread;
		//thread.interrupt();
		
	}
	
	public boolean mPropertyChanged=false;

	private void OnPropertyChanged(boolean state)
	{
		mPropertyChanged=state;
	}
	
	public boolean isPropertyChanged()
	{
		Debug.d(TAG, "--->changed : " + mPropertyChanged);
		return mPropertyChanged;
	}

	@Override
	public void onClick(View v) {
		ExtGpio.playClick();
		switch (v.getId()) {
			case R.id.btn_new:
				onNew();
				break;
			case R.id.btn_save:
				onSave();
				break;
			case R.id.btn_saveas:
				onSaveAs();
				break;
			case R.id.btn_insert:
				onInsert();
				break;
			case R.id.btn_open:
				onOpen();
				break;
			case R.id.btn_detail:
				onShowInfo();
				break;
			case R.id.btn_up:
				upKeyPressed();
				break;
			case R.id.btn_down:
				downKeyPressed();
				break;
			case R.id.btn_left:
				leftKeyPressed();
				break;
			case R.id.btn_right:
				rightKeyPressed();
				break;
			case R.id.btn_zoomIn:
				onZoomInPressed();
				break;
			case R.id.btn_zoomOut:
				onZoomOutPressed();
				break;
			case R.id.wide_btn:
				onWidePressed();
				break;
			case R.id.narrow_btn:
				onNarrowPressed();
				break;
			case R.id.btn_cursor:
				onCursorPressed();
				break;
			case R.id.btn_list:
				onListPressed();
				break;
			default:
				break;
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.editView:
			onObjectTouch(event);
			break;
		case R.id.btn_left:
			onLeftTouch(event);
			break;
		case R.id.btn_right:
			onRightTouch(event);
			break;
		case R.id.btn_up:
			onUpTouch(event);
			break;
		case R.id.btn_down:
			onDownTouch(event);
			break;
		case R.id.btn_zoomOut:
			// onZoomOutXTouch(event);
			break;
		case R.id.btn_zoomIn:
			// onZoomInXTouch(event);
			break;
		case R.id.wide_btn:
			onWideTouch(event);
			break;
		case R.id.narrow_btn:
			onNarrowTouch(event);
			break;
		default:
			break;
		}
		if (v.getId() == R.id.scrollView1) {
			return false;
		}
		return false;
	}
	
	private void onNew() {
		mObjName = null;
		mMsgManager.removeAll();
		MessageObject msgObject = new MessageObject(mContext, 0);
		msgObject.setType(SystemConfigFile.getInstance(mContext).getParam(30));
		mMsgManager.add(msgObject);
		mMsgTask.addObject(msgObject);
		((MainActivity)mContext).onEditTitleChanged(mContext.getString(R.string.str_filename_no));
	}
	
	private void onSave() {
		if(!isPropertyChanged())
			return;
		if(mObjName != null)
		{
			mHandler.sendEmptyMessage(HANDLER_MESSAGE_SAVE);
			return;
		}
		onSaveAs();
	}
	
	private void onSaveAs() {
		CustomerDialogBase dialog;
		dialog = new MessageSaveDialog(mContext);
		dialog.setOnPositiveClickedListener(new OnPositiveListener() {
			
			@Override
			public void onClick() {
				mHandler.sendEmptyMessage(HANDLER_MESSAGE_SAVEAS);
			}

			@Override
			public void onClick(String content) {
				
			}
		});
		dialog.show();
	}

	
	private void onInsert() {
		ObjectInsertDialog dialog1 = new ObjectInsertDialog(getActivity());
		dialog1.show();
		Message msg = mHandler.obtainMessage(HANDLER_MESSAGE_INSERT_OBJECT);
		dialog1.setDismissMessage(msg);
	}
	
	private void onOpen() {
		MessageBrowserDialog dialog = new MessageBrowserDialog(mContext);
		dialog.setOnPositiveClickedListener(new OnPositiveListener() {
			
			@Override
			public void onClick() {
				mHandler.sendEmptyMessage(HANDLER_MESSAGE_OPEN);
			}

			@Override
			public void onClick(String content) {
			}
		});
		dialog.show();
	}
	
	private void onShowInfo() {
		BaseObject object = getCurObj();
		// onShowInfo(object);
		final ObjectInfoDialog objDialog = new ObjectInfoDialog(mContext, object);
		objDialog.setOnPositiveBtnListener(new OnPositiveBtnListener(){
			@Override
			public void onClick() {
				Debug.d(TAG, "===>onShowinfo  clicked");
				Message msg = mObjRefreshHandler.obtainMessage(OBJECT_UPDATE_CONTENT);
				msg.obj = objDialog.getObject();
				msg.sendToTarget();
			}
		});
		objDialog.setOnDeleteListener(new onDeleteListener() {

			@Override
			public void onClick() {
				onDelete();
			}
		});
		objDialog.show();
	}
	
	private void onShowInfo(BaseObject object) {
		
		final ObjectInfoDialog objDialog = new ObjectInfoDialog(mContext, object);
		objDialog.setOnPositiveBtnListener(new OnPositiveBtnListener(){
			@Override
			public void onClick() {
				Debug.d(TAG, "===>onShowinfo  clicked");
				Message msg = mObjRefreshHandler.obtainMessage(OBJECT_INSERT);
				msg.obj = objDialog.getObject();
				msg.sendToTarget();
			}
		});
		objDialog.setOnDeleteListener(new onDeleteListener() {
			
			@Override
			public void onClick() {
				onDelete();
			}
		});
		objDialog.show();
	}
	
	private void onDelete() {
		// TODO Auto-generated method stub
		BaseObject obj = getCurObj();
		if(obj == null || obj instanceof MessageObject)
			return;
//		mMsgTask.removeObject(obj);

		Message msg = mObjRefreshHandler.obtainMessage(OBJECT_REMOVE);
		msg.obj = obj;
		msg.sendToTarget();
		mMsgManager.setSelect(0);
	}
	
	private void onInsertObject(BaseObject object) {
		//mMsgTask.addObject(object);
//		mMsgManager.add(object);
//		setCurObj(object);
		/**
		 * display object info dialog 
		 */
		// mObjRefreshHandler.sendMessage(msg);
		// clearCurObj();
		object.setTask(mMsgTask);
		onShowInfo(object);
	}
	
	private boolean onObjectTouch(MotionEvent event) {
		Debug.d(TAG, "onTouch x="+event.getX()+", y="+event.getY());
		int ret = getTouchedObj(event.getX(), event.getY());
		if(ret != -1)
		{
			ExtGpio.playClick();
			mMsgManager.setSelect(ret);

		}
		return false;
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
		if (obj instanceof MessageObject) {
			cursorMove((MessageObject)obj);
		}
		Message msg = mObjRefreshHandler.obtainMessage(OBJECT_UPDATE);
		msg.obj = obj;
		msg.sendToTarget();
	}
	
	private boolean onLeftTouch(MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			Debug.d(TAG, "======Down button pressed!!");
			mKeyRepeatHandler.sendEmptyMessageDelayed(LEFT_KEY, 800);
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			Debug.d(TAG, "======Down button released!!");
			mKeyRepeatHandler.removeMessages(LEFT_KEY);
		}
		return false;
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
		if (obj instanceof MessageObject) {
			cursorMove((MessageObject)obj);
		}
		Message msg = mObjRefreshHandler.obtainMessage(OBJECT_UPDATE);
		msg.obj = obj;
		msg.sendToTarget();
	}
	
	private void cursorMove(MessageObject object) {
		
	}
	
	private boolean onRightTouch(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			Debug.d(TAG, "======Down button pressed!!");
			mKeyRepeatHandler.sendEmptyMessageDelayed(RIGHT_KEY, 800);
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			Debug.d(TAG, "======Down button released!!");
			mKeyRepeatHandler.removeMessages(RIGHT_KEY);
		}
		return false;         
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
		if (obj instanceof MessageObject) {
			cursorMove((MessageObject)obj);
		}

		Message msg = mObjRefreshHandler.obtainMessage(OBJECT_UPDATE);
		msg.obj = obj;
		msg.sendToTarget();
	}
	
	private boolean onUpTouch(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			Debug.d(TAG, "======Down button pressed!!");
			mKeyRepeatHandler.sendEmptyMessageDelayed(UP_KEY, 800);
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			Debug.d(TAG, "======up button released!!");
			mKeyRepeatHandler.removeMessages(UP_KEY);
		}
		return false;
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
		if (obj instanceof MessageObject) {
			cursorMove((MessageObject)obj);
		}
		Message msg = mObjRefreshHandler.obtainMessage(OBJECT_UPDATE);
		msg.obj = obj;
		msg.sendToTarget();
	}
	
	private boolean onDownTouch(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			Debug.d(TAG, "======Down button pressed!!");
			mKeyRepeatHandler.sendEmptyMessageDelayed(DOWN_KEY, 800);
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			Debug.d(TAG, "======Down button released!!");
			mKeyRepeatHandler.removeMessages(DOWN_KEY);
		}
		return false;
	}
	
	
	private void onZoomInPressed() {
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		float h = obj.getHeight();
		if(h<30)
		return;
		h = h - MessageObject.PIXELS_PER_MM;
		obj.setHeight(h);
	/*	if ((obj instanceof RealtimeObject)) {
			obj.resizeByHeight();
		}
		*/

		Message msg = mObjRefreshHandler.obtainMessage(OBJECT_UPDATE);
		msg.obj = obj;
		msg.sendToTarget();
	}
	
	private void onZoomOutPressed() {
		BaseObject obj = getCurObj();
		if(obj == null)
			return;
		float h = obj.getHeight();
		
		h = h + MessageObject.PIXELS_PER_MM;
		obj.setHeight(h);
		/*
		if ((obj instanceof RealtimeObject)) {
			obj.resizeByHeight();
		}
		*/

		Message msg = mObjRefreshHandler.obtainMessage(OBJECT_UPDATE);
		msg.obj = obj;
		msg.sendToTarget();
	}
	
	private boolean onWideTouch(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			Debug.d(TAG, "======Down button pressed!!");
			mKeyRepeatHandler.sendEmptyMessageDelayed(WIDE_KEY, 800);
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			Debug.d(TAG, "======Down button released!!");
			mKeyRepeatHandler.removeMessages(WIDE_KEY);
		}
		return false;
	}
	private void onWidePressed() {
		BaseObject object = getCurObj();
		if (object == null || object instanceof MessageObject) {
			return;
		}
		if (object instanceof RealtimeObject) {
			((RealtimeObject)object).wide();
		} else {
			object.setWidth(object.getWidth() + 4);
		}
		Message msg = mObjRefreshHandler.obtainMessage(OBJECT_UPDATE);
		msg.obj = object;
		msg.sendToTarget();
	}
	
	private boolean onNarrowTouch(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			Debug.d(TAG, "======Down button pressed!!");
			mKeyRepeatHandler.sendEmptyMessageDelayed(NARROW_KEY, 800);
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			Debug.d(TAG, "======Down button released!!");
			mKeyRepeatHandler.removeMessages(NARROW_KEY);
		}
		return false;
	}
	private void onNarrowPressed() {
		BaseObject object = getCurObj();
		if (object == null || object instanceof MessageObject) {
			return;
		}
		if (object instanceof RealtimeObject) {
			((RealtimeObject)object).narrow();
		} else {
			object.setWidth(object.getWidth() - 4);
		}

		Message msg = mObjRefreshHandler.obtainMessage(OBJECT_UPDATE);
		msg.obj = object;
		msg.sendToTarget();
	}
	
	private void onListPressed() {
		ArrayList<BaseObject> list = mMsgTask.getObjects();
		mNameAdapter.removeAll();
		for (BaseObject object : list) {
			mNameAdapter.addItem(object.getTitle());
		}
		mObjSpiner.showAsDropUp(mBtnList);
	}
	
	private void onCursorPressed() {
		/*顯示十字線時選中第一個對象，即MessageObject對象*/
		MessageObject o = mMsgTask.getMsgObject();
		
		/*如果當前選中的不是cursor或者cursor的座標爲（0,0） 重置座標*/
		if (!o.getSelected() || o.getX() == 0 || o.getY() == 0) {
			/* 光标显示在ScrollView中间  */
			mMsgManager.setSelect(0);
		} else {
			o.setSelected(false);
		}

		Message msg = mObjRefreshHandler.obtainMessage(OBJECT_UPDATE);
		msg.obj = o;
		msg.sendToTarget();
	}
	
	public void scrollPageFore() {
	}
	
	public void scrollPageBack() {
	}
	
	public final int LEFT_KEY=1;
	public final int RIGHT_KEY=2;
	public final int UP_KEY=3;
	public final int DOWN_KEY=4;
	public final int ZOOM_IN_KEY=5;
	public final int ZOOM_OUT_KEY=6;
	public final int WIDE_KEY=7;
	public final int NARROW_KEY=8;
	
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
				case ZOOM_IN_KEY:
					Debug.d(TAG, "zoom x  in key pressed");
					// zoomInXKeyPressed();
					break;
				case ZOOM_OUT_KEY:
					Debug.d(TAG, "zoom x out key pressed");
					// zoomOutXKeyPressed();
					break;
				case WIDE_KEY:
					Debug.d(TAG, "zoom x out key pressed");
					onWidePressed();
					break;
				case NARROW_KEY:
					Debug.d(TAG, "zoom x out key pressed");
					onNarrowPressed();
					break;
				default:
					Debug.d(TAG, "unknow key repeat ");
					break;
			}
			mKeyRepeatHandler.sendEmptyMessageDelayed(msg.what, 100);
		}
	};
}

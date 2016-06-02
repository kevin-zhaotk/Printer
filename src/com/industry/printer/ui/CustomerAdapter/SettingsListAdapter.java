package com.industry.printer.ui.CustomerAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.industry.printer.R;
import com.industry.printer.FileFormat.SystemConfigFile;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.ui.CustomerAdapter.PopWindowAdapter.IOnItemClickListener;
import com.industry.printer.ui.CustomerDialog.NewMessageDialog;
import com.industry.printer.widget.PopWindowSpiner;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsListAdapter extends BaseAdapter implements OnClickListener, IOnItemClickListener {

	private final static String TAG = SettingsListAdapter.class.getSimpleName();
	
	private Context mContext;
	private ItemViewHolder mHolder;
	
	/**
	 * An inflater for inflate the view
	 */
	private LayoutInflater mInflater;
	
	private String[] mTitles;
	
	public PopWindowSpiner mSpiner;
	public PopWindowAdapter mEncoderAdapter;
	public PopWindowAdapter mTrigerMode;
	private PopWindowAdapter mDirection;
	private PopWindowAdapter mResolution;
	private PopWindowAdapter mPhotocell;
	private PopWindowAdapter mRepeat;
	private PopWindowAdapter mNozzle;
	private PopWindowAdapter mPen1Mirror;
	private PopWindowAdapter mPen2Mirror;
	private PopWindowAdapter mPen3Mirror;
	private PopWindowAdapter mPen4Mirror;

	private PopWindowAdapter mPen1Invert;
	private PopWindowAdapter mPen2Invert;
	private PopWindowAdapter mPen3Invert;
	private PopWindowAdapter mPen4Invert;
	
	private PopWindowAdapter mPens;
	private PopWindowAdapter mAutoVol;
	private PopWindowAdapter mAutoPulse;
	
	private ItemViewHolder mEncoderHolder;
	private HashMap<Integer, ItemViewHolder> mHoldMap;
	
	private ItemOneLine[] mSettingItems = new ItemOneLine[64];
	
	/**
	 * A customerized view holder for widgets 
	 * @author zhaotongkai
	 */
	private class ItemViewHolder{
		public TextView	mTitleL;		//message title
		public TextView	mValueLTv;
		public EditText	mValueLEt;
		public TextView	mUnitL;
		
		public TextView	mTitleR;		//message title
		public TextView	mValueRTv;
		public EditText	mValueREt;
		public TextView	mUnitR;
	}
	
	private class ItemOneLine {
		public int mTitle;
		public String mValue;
		public int mUnit;
		private boolean isSWitch;
		
		public ItemOneLine(int id, String value, int unit) {
			mTitle = id;
			mValue = value;
			mUnit = unit;
			isSWitch = false;
		}
		
		public ItemOneLine(int id, String value, int unit, boolean spinner) {
			this(id, value, unit);
			isSWitch = spinner;
		}
	}
	
	public SettingsListAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// mTitles = mContext.getResources().getStringArray(R.array.str_settings_params);
		loadSettings();
		mHoldMap = new HashMap<Integer, ItemViewHolder>();
		
		mEncoderAdapter = new PopWindowAdapter(mContext, null);
		mTrigerMode = new PopWindowAdapter(mContext, null);
		mDirection = new PopWindowAdapter(mContext, null);
		mResolution = new PopWindowAdapter(mContext, null);
		mPhotocell = new PopWindowAdapter(mContext, null);
		mRepeat = new PopWindowAdapter(mContext, null);
		mNozzle = new PopWindowAdapter(mContext, null);
		mPen1Mirror = new PopWindowAdapter(mContext, null);
		mPen2Mirror = new PopWindowAdapter(mContext, null);
		mPen3Mirror = new PopWindowAdapter(mContext, null);
		mPen4Mirror = new PopWindowAdapter(mContext, null);
		
		mPen1Invert = new PopWindowAdapter(mContext, null);
		mPen2Invert = new PopWindowAdapter(mContext, null);
		mPen3Invert = new PopWindowAdapter(mContext, null);
		mPen4Invert = new PopWindowAdapter(mContext, null);
		
		mPens = new PopWindowAdapter(mContext, null);
		
		mAutoVol = new PopWindowAdapter(mContext, null);
		mAutoPulse = new PopWindowAdapter(mContext, null);
		initAdapters();
	}
	
	@Override
	public int getCount() {
		Debug.d(TAG, "--->getCount=" + mSettingItems.length);
		return mSettingItems.length/2;
		//return Configs.gParams;
	}

	@Override
	public Object getItem(int arg0) {
		Debug.d(TAG, "--->getItem");
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView != null) {
			mHolder = (ItemViewHolder) convertView.getTag();
		}
		else
		{
			//prepare a empty view 
			convertView = mInflater.inflate(R.layout.settings_frame_item, null);
			mHolder = new ItemViewHolder();
			//Left 
			mHolder.mTitleL = (TextView) convertView.findViewById(R.id.setting_title_left);
			mHolder.mValueLTv = (TextView) convertView.findViewById(R.id.setting_value_left_tv);
			mHolder.mValueLEt = (EditText) convertView.findViewById(R.id.setting_value_left_et);
			mHolder.mUnitL = (TextView) convertView.findViewById(R.id.setting_unit_left);
			
			//Right
			mHolder.mTitleR = (TextView) convertView.findViewById(R.id.setting_title_right);
			mHolder.mValueRTv = (TextView) convertView.findViewById(R.id.setting_value_right_tv);
			mHolder.mValueREt = (EditText) convertView.findViewById(R.id.setting_value_right_et);
			mHolder.mUnitR = (TextView) convertView.findViewById(R.id.setting_unit_right);
			
			
			convertView.setTag(mHolder);
			mHoldMap.put(position, mHolder);
			
		}
		mHolder.mValueLEt.setTag(2*position);
		mHolder.mValueLTv.setTag(2*position);
		mHolder.mValueREt.setTag(2*position+1);
		mHolder.mValueRTv.setTag(2*position+1);
		mHolder.mValueLEt.addTextChangedListener(new SelfTextWatcher(mHolder.mValueLEt));
		mHolder.mValueREt.addTextChangedListener(new SelfTextWatcher(mHolder.mValueREt));
		
		if (mSettingItems[2*position].mUnit > 0) {
			mHolder.mUnitL.setText(mSettingItems[2*position].mUnit);
		} else {
			mHolder.mUnitL.setText("");
		}
		if (mSettingItems[2*position+1].mUnit > 0) {
			mHolder.mUnitR.setText(mSettingItems[2*position+1].mUnit);
		} else {
			mHolder.mUnitR.setText("");
		}
		mHolder.mTitleL.setText(mContext.getString(mSettingItems[2*position].mTitle));
		mHolder.mTitleR.setText(mContext.getString(mSettingItems[2*position+1].mTitle));
		Debug.d(TAG, "===>getView position=" + position);
		
		if (mSettingItems[2*position].isSWitch) {
			mHolder.mValueLTv.setVisibility(View.VISIBLE);
			mHolder.mValueLEt.setVisibility(View.GONE);
			mHolder.mValueLTv.setText(mSettingItems[2*position].mValue);
			mHolder.mValueLTv.setOnClickListener(this);
		} else {
			mHolder.mValueLTv.setVisibility(View.GONE);
			mHolder.mValueLEt.setVisibility(View.VISIBLE);
			Debug.d(TAG, "--->getView:left=" + mSettingItems[2*position].mValue + "---right=" + mSettingItems[2*position+1].mValue);
			mHolder.mValueLEt.setText(mSettingItems[2*position].mValue);
		}
		
		if (mSettingItems[2*position + 1].isSWitch) {
			mHolder.mValueRTv.setVisibility(View.VISIBLE);
			mHolder.mValueREt.setVisibility(View.GONE);
			mHolder.mValueRTv.setText(mSettingItems[2*position+1].mValue);
			mHolder.mValueRTv.setOnClickListener(this);
		} else {
			mHolder.mValueRTv.setVisibility(View.GONE);
			mHolder.mValueREt.setVisibility(View.VISIBLE);
			mHolder.mValueREt.setText(mSettingItems[2*position+1].mValue);
		}
		
		return convertView;
	}
	
	public void loadSettings() {
		Debug.d(TAG, "--->loadSettings");
		String param = String.valueOf(SystemConfigFile.mParam1);
		mSettingItems[0] = new ItemOneLine(R.string.str_textview_param1, 	param, R.string.str_time_unit_mm_s);
		param = getEntry(R.array.direction_item_entries, SystemConfigFile.mParam2);
		mSettingItems[1] = new ItemOneLine(R.string.str_textview_param2, 	param, 0, true);
		param = getEntry(R.array.resolution_item_entries, SystemConfigFile.mParam3);
		mSettingItems[2] = new ItemOneLine(R.string.str_textview_param3, 	param, R.string.strResunit, true);
		param = String.valueOf(SystemConfigFile.mParam4);
		mSettingItems[3] = new ItemOneLine(R.string.str_textview_param4, 	param, R.string.str_time_unit_ms);
		param = getEntry(R.array.switch_item_entries, SystemConfigFile.mParam5);
		mSettingItems[4] = new ItemOneLine(R.string.str_textview_param5, 	param, R.string.str_time_unit_100us, true);
		param = getEntry(R.array.switch_item_entries, SystemConfigFile.mParam6);
		mSettingItems[5] = new ItemOneLine(R.string.str_textview_param6, 	param, 0, true);
		param = getEntry(R.array.direction_item_entries, SystemConfigFile.mParam7);
		mSettingItems[6] = new ItemOneLine(R.string.str_textview_param7, 	param, 	R.string.str_time_unit_mm, true);
		param = getEntry(R.array.direction_item_entries, SystemConfigFile.mParam8);
		mSettingItems[7] = new ItemOneLine(R.string.str_textview_param8, 	param, 0, true);
		param = String.valueOf(SystemConfigFile.mParam9);
		mSettingItems[8] = new ItemOneLine(R.string.str_textview_param9, 	param, R.string.str_time_unit_mm);
		param = String.valueOf(SystemConfigFile.mParam10);
		mSettingItems[9] = new ItemOneLine(R.string.str_textview_param10, 	param, 0);
		param = String.valueOf(SystemConfigFile.mResv11);
		mSettingItems[10] = new ItemOneLine(R.string.str_textview_param11, 	param, R.string.str_time_unit_mm);
		param = String.valueOf(SystemConfigFile.mResv12);
		mSettingItems[11] = new ItemOneLine(R.string.str_textview_param12, 	param, R.string.str_time_unit_mm);
		param = getEntry(R.array.switch_item_entries, SystemConfigFile.mResv13);
		mSettingItems[12] = new ItemOneLine(R.string.str_textview_param13, 	param, 0, true);
		param = getEntry(R.array.switch_item_entries, SystemConfigFile.mResv14);
		mSettingItems[13] = new ItemOneLine(R.string.str_textview_param14, 	param, 0, true);
		param = getEntry(R.array.switch_item_entries, SystemConfigFile.mResv15);
		mSettingItems[14] = new ItemOneLine(R.string.str_textview_param15, 	param, 0, true);
		param = getEntry(R.array.switch_item_entries, SystemConfigFile.mResv16);
		mSettingItems[15] = new ItemOneLine(R.string.str_textview_param16, 	param, 0, true);
		param = getEntry(R.array.switch_item_entries, SystemConfigFile.mResv17);
		mSettingItems[16] = new ItemOneLine(R.string.str_textview_param17, 	param, 0, true);
		param = String.valueOf(SystemConfigFile.mResv18);
		mSettingItems[17] = new ItemOneLine(R.string.str_textview_param18, 	param, 0);
		param = String.valueOf(SystemConfigFile.mResv19);
		mSettingItems[18] = new ItemOneLine(R.string.str_textview_param19, 	param, R.string.str_time_unit_mm);
		param = String.valueOf(SystemConfigFile.mResv20);
		mSettingItems[19] = new ItemOneLine(R.string.str_textview_param20, 	param, R.string.str_time_unit_mm);
		param = getEntry(R.array.switch_item_entries, SystemConfigFile.mResv21);
		mSettingItems[20] = new ItemOneLine(R.string.str_textview_param21, 	param, 0, true);
		param = getEntry(R.array.switch_item_entries, SystemConfigFile.mResv22);
		mSettingItems[21] = new ItemOneLine(R.string.str_textview_param22, 	param, 0, true);
		param = getEntry(R.array.switch_item_entries, SystemConfigFile.mResv23);
		mSettingItems[22] = new ItemOneLine(R.string.str_textview_param23, 	param, 0, true);
		param = getEntry(R.array.switch_item_entries, SystemConfigFile.mResv24);
		mSettingItems[23] = new ItemOneLine(R.string.str_textview_param24, 	param, 0, true);
		param = getEntry(R.array.switch_item_entries, SystemConfigFile.mResv25);
		mSettingItems[24] = new ItemOneLine(R.string.str_textview_param25, 	param, 0, true);
		param = String.valueOf(SystemConfigFile.mResv26);
		mSettingItems[25] = new ItemOneLine(R.string.str_textview_param26, 	param, R.string.str_time_unit_0_1v);
		param = getEntry(R.array.switch_item_entries, SystemConfigFile.mResv27);
		mSettingItems[26] = new ItemOneLine(R.string.str_textview_param27, 	param, 0, true);
		param = String.valueOf(SystemConfigFile.mResv28);
		mSettingItems[27] = new ItemOneLine(R.string.str_textview_param28, 	param, R.string.str_time_unit_0_1us);
		mSettingItems[28] = new ItemOneLine(R.string.str_textview_param29, 	String.valueOf(SystemConfigFile.mResv29), 								R.string.str_time_unit_ms);
		mSettingItems[29] = new ItemOneLine(R.string.str_textview_param30, 	String.valueOf(SystemConfigFile.mResv30), 								R.string.str_time_unit_ms);
		mSettingItems[30] = new ItemOneLine(R.string.str_textview_param31, 	String.valueOf(SystemConfigFile.mResv31), 0);
		mSettingItems[31] = new ItemOneLine(R.string.str_textview_param32, 	String.valueOf(SystemConfigFile.mResv32), 0);
		mSettingItems[32] = new ItemOneLine(R.string.str_textview_param33, String.valueOf(SystemConfigFile.mResv33), 0);
		mSettingItems[33] = new ItemOneLine(R.string.str_textview_param34, String.valueOf(SystemConfigFile.mResv34), 0);
		mSettingItems[34] = new ItemOneLine(R.string.str_textview_param35, String.valueOf(SystemConfigFile.mResv35), 0);
		mSettingItems[35] = new ItemOneLine(R.string.str_textview_param36, String.valueOf(SystemConfigFile.mResv36), 0);
		mSettingItems[36] = new ItemOneLine(R.string.str_textview_param37, String.valueOf(SystemConfigFile.mResv37), 0);
		mSettingItems[37] = new ItemOneLine(R.string.str_textview_param38, String.valueOf(SystemConfigFile.mResv38), 0);
		mSettingItems[38] = new ItemOneLine(R.string.str_textview_param39, String.valueOf(SystemConfigFile.mResv39), 0);
		mSettingItems[39] = new ItemOneLine(R.string.str_textview_param40, String.valueOf(SystemConfigFile.mResv40), 0);
		mSettingItems[40] = new ItemOneLine(R.string.str_textview_param41, String.valueOf(SystemConfigFile.mResv41), 0);
		mSettingItems[41] = new ItemOneLine(R.string.str_textview_param42, String.valueOf(SystemConfigFile.mResv42), 0);
		mSettingItems[42] = new ItemOneLine(R.string.str_textview_param43, String.valueOf(SystemConfigFile.mResv43), 0);
		mSettingItems[43] = new ItemOneLine(R.string.str_textview_param44, String.valueOf(SystemConfigFile.mResv44), 0);
		mSettingItems[44] = new ItemOneLine(R.string.str_textview_param45, String.valueOf(SystemConfigFile.mResv45), 0);
		mSettingItems[45] = new ItemOneLine(R.string.str_textview_param46, String.valueOf(SystemConfigFile.mResv46), 0);
		mSettingItems[46] = new ItemOneLine(R.string.str_textview_param47, String.valueOf(SystemConfigFile.mResv47), 0);
		mSettingItems[47] = new ItemOneLine(R.string.str_textview_param48, String.valueOf(SystemConfigFile.mResv48), 0);
		mSettingItems[48] = new ItemOneLine(R.string.str_textview_param49, String.valueOf(SystemConfigFile.mResv49), 0);
		mSettingItems[49] = new ItemOneLine(R.string.str_textview_param50, String.valueOf(SystemConfigFile.mResv50), 0);
		mSettingItems[50] = new ItemOneLine(R.string.str_textview_param51, String.valueOf(SystemConfigFile.mResv51), 0);
		mSettingItems[51] = new ItemOneLine(R.string.str_textview_param52, String.valueOf(SystemConfigFile.mResv52), 0);
		mSettingItems[52] = new ItemOneLine(R.string.str_textview_param53, String.valueOf(SystemConfigFile.mResv53), 0);
		mSettingItems[53] = new ItemOneLine(R.string.str_textview_param54, String.valueOf(SystemConfigFile.mResv54), 0);
		mSettingItems[54] = new ItemOneLine(R.string.str_textview_param55, String.valueOf(SystemConfigFile.mResv55), 0);
		mSettingItems[55] = new ItemOneLine(R.string.str_textview_param56, String.valueOf(SystemConfigFile.mResv56), 0);
		mSettingItems[56] = new ItemOneLine(R.string.str_textview_param57, String.valueOf(SystemConfigFile.mResv57), 0);
		mSettingItems[57] = new ItemOneLine(R.string.str_textview_param58, String.valueOf(SystemConfigFile.mResv58), 0);
		mSettingItems[58] = new ItemOneLine(R.string.str_textview_param59, String.valueOf(SystemConfigFile.mResv59), 0);
		mSettingItems[59] = new ItemOneLine(R.string.str_textview_param60, String.valueOf(SystemConfigFile.mResv60), 0);
		mSettingItems[60] = new ItemOneLine(R.string.str_textview_param61, String.valueOf(SystemConfigFile.mResv61), 0);
		mSettingItems[61] = new ItemOneLine(R.string.str_textview_param62, String.valueOf(SystemConfigFile.mResv62), 0);
		mSettingItems[62] = new ItemOneLine(R.string.str_textview_param63, String.valueOf(SystemConfigFile.mResv63), 0);
		mSettingItems[63] = new ItemOneLine(R.string.str_textview_param64, String.valueOf(SystemConfigFile.mResv64), 0);
		Debug.d(TAG, "--->loadSettings");
	}
	
	private void initAdapters() {
		
		mSpiner = new PopWindowSpiner(mContext);
		mSpiner.setFocusable(true);
		mSpiner.setOnItemClickListener(this);
		
		String[] items = mContext.getResources().getStringArray(R.array.switch_item_entries); 
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, R.id.textView_id, items);
		for (int i = 0; i < items.length; i++) {
			mEncoderAdapter.addItem(items[i]);
		}
		
		items = mContext.getResources().getStringArray(R.array.array_triger_mode); 
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, R.id.textView_id, items);
		for (int i = 0; i < items.length; i++) {
			mTrigerMode.addItem(items[i]);
		}
		
		items = mContext.getResources().getStringArray(R.array.direction_item_entries);
		for (int i = 0; i < items.length; i++) {
			mDirection.addItem(items[i]);
		}
		
		items = mContext.getResources().getStringArray(R.array.resolution_item_entries);
		for (int i = 0; i < items.length; i++) {
			mResolution.addItem(items[i]);
		}

		items = mContext.getResources().getStringArray(R.array.photo_item_entries);
		for (int i = 0; i < items.length; i++) {
			mPhotocell.addItem(items[i]);
		}
		
		items = mContext.getResources().getStringArray(R.array.direction_item_entries);
		for (int i = 0; i < items.length; i++) {
			mRepeat.addItem(items[i]);
		}
		for (int i = 0; i < items.length; i++) {
			mNozzle.addItem(items[i]);
		}
		
		items = mContext.getResources().getStringArray(R.array.switch_item_entries);
		for (int i = 0; i < items.length; i++) {
			mPen1Mirror.addItem(items[i]);
		}
		for (int i = 0; i < items.length; i++) {
			mPen2Mirror.addItem(items[i]);
		}
		for (int i = 0; i < items.length; i++) {
			mPen3Mirror.addItem(items[i]);
		}
		for (int i = 0; i < items.length; i++) {
			mPen4Mirror.addItem(items[i]);
		}
		for (int i = 0; i < items.length; i++) {
			mPen1Invert.addItem(items[i]);
		}
		for (int i = 0; i < items.length; i++) {
			mPen2Invert.addItem(items[i]);
		}
		for (int i = 0; i < items.length; i++) {
			mPen3Invert.addItem(items[i]);
		}
		for (int i = 0; i < items.length; i++) {
			mPen4Invert.addItem(items[i]);
		}
		for (int i = 0; i < items.length; i++) {
			mAutoVol.addItem(items[i]);
		}
		for (int i = 0; i < items.length; i++) {
			mAutoPulse.addItem(items[i]);
		}
		
		items = mContext.getResources().getStringArray(R.array.pens_item_entries);
		for (int i = 0; i < items.length; i++) {
			mPens.addItem(items[i]);
		}
	}
	private String getEntry(int id,int index) {
		String entries[] = mContext.getResources().getStringArray(id);
		Debug.d(TAG, "--->getEncoder:entries[" + index + "]=" + entries[index]);
		if (entries == null || entries.length <= 0) {
			return null;
		}
		if (index<0 || index >= entries.length) {
			return entries[0];
		}
		return entries[index];
	}
	
	private int getIndexByEntry(int id, String entry) {
		String entries[] = mContext.getResources().getStringArray(id);
		if (entry == null || entries == null || entries.length <= 0) {
			return 0;
		}
		for (int i = 0; i < entries.length; i++) {
			if (entry.equalsIgnoreCase(entries[i])) {
				return i;
			}
		}
		return 0;
	}
	
	

	@Override
	public void onClick(View view) {
		Debug.d(TAG, "===>onclick " + view);
		int position = 0;
		if (view != null) {
			position = (Integer)view.getTag();
		} else {
			return;
		}
		boolean s = mSettingItems[position].isSWitch;
		if (!s) {
			return;
		}
		mSpiner.setAttachedView(view);
		if (position == 1) { //參數2
			mSpiner.setAdapter(mDirection);
		} else if (position == 2) { //參數3
			mSpiner.setAdapter(mResolution);
		} else if (position == 4) { //參數5
			mSpiner.setAdapter(mPhotocell);
		} else if (position == 5) { //參數6
			mSpiner.setAdapter(mEncoderAdapter);
		} else if (position == 6) { //參數7
			mSpiner.setAdapter(mRepeat);
		} else if (position == 7) { //參數8
			mSpiner.setAdapter(mNozzle);
		} else if (position == 12) { //參數13
			mSpiner.setAdapter(mPen1Mirror);
		} else if (position == 13) { //參數14
			mSpiner.setAdapter(mPen2Mirror);
		} else if (position == 14) { //參數15
			mSpiner.setAdapter(mPen1Invert);
		} else if (position == 15) { //參數16
			mSpiner.setAdapter(mPen2Invert);
		} else if (position == 16) { //參數17
			mSpiner.setAdapter(mPens);
		} else if (position == 20) { //參數21
			mSpiner.setAdapter(mPen3Mirror);
		} else if (position == 21) { //參數22
			mSpiner.setAdapter(mPen4Mirror);
		} else if (position == 22) { //參數23
			mSpiner.setAdapter(mPen3Invert);
		} else if (position == 23) { //參數24
			mSpiner.setAdapter(mPen4Invert);
		} else if (position == 24) { //參數25
			mSpiner.setAdapter(mAutoVol);
		} else if (position == 26) { //參數27
			mSpiner.setAdapter(mAutoPulse);
		}
		mSpiner.setWidth(view.getWidth());
		mSpiner.showAsDropDown(view);
	}

	@Override
	public void onItemClick(int index) {
		TextView view = mSpiner.getAttachedView();
		int position = (Integer) view.getTag();
		String value = null;
		if (position == 1) {
			value = (String)mDirection.getItem(index);
			SystemConfigFile.mParam2 = getDirectionvalue(index);
		} else if (position == 2) { //參數3
			value = (String)mResolution.getItem(index);
			SystemConfigFile.mParam3 = getValue(mResolution, index);
		} else if (position == 4) { //參數5
			value = (String)mPhotocell.getItem(index);
			SystemConfigFile.mParam5 = getSwitchvalue(index);
		} else if (position == 5) { //參數6
			value = (String)mEncoderAdapter.getItem(index);
			SystemConfigFile.mParam6 = getSwitchvalue(index);
		} else if (position == 6) { //參數7
			value = (String)mRepeat.getItem(index);
			SystemConfigFile.mParam7 = getSwitchvalue(index);
		} else if (position == 7) { //參數8
			value = (String)mNozzle.getItem(index);
			SystemConfigFile.mParam8 = getValue(mNozzle, index);
		} else if (position == 12) { //參數13
			value = (String)mPen1Mirror.getItem(index);
			SystemConfigFile.mResv13 = getSwitchvalue(index);
		} else if (position == 13) { //參數14
			value = (String)mPen2Mirror.getItem(index);
			SystemConfigFile.mResv14 = getSwitchvalue(index);
		} else if (position == 14) { //參數15
			value = (String)mPen1Invert.getItem(index);
			SystemConfigFile.mResv15 = getSwitchvalue(index);
		} else if (position == 15) { //參數16
			value = (String)mPen2Invert.getItem(index);
			SystemConfigFile.mResv16 = getSwitchvalue(index);
		} else if (position == 16) { //參數17
			value = (String)mPens.getItem(index);
			SystemConfigFile.mResv17 = getValue(mPens, index);
		} else if (position == 20) { //參數21
			value = (String)mPen3Mirror.getItem(index);
			SystemConfigFile.mResv21 = getSwitchvalue(index);
		} else if (position == 21) { //參數22
			value = (String)mPen4Mirror.getItem(index);
			SystemConfigFile.mResv22 = getSwitchvalue(index);
		} else if (position == 22) { //參數23
			value = (String)mPen3Invert.getItem(index);
			SystemConfigFile.mResv23 = getSwitchvalue(index);
		} else if (position == 23) { //參數24
			value = (String)mPen4Invert.getItem(index);
			SystemConfigFile.mResv24 = getSwitchvalue(index);
		} else if (position == 24) { //參數25
			value = (String)mAutoVol.getItem(index);
			SystemConfigFile.mResv25 = getSwitchvalue(index);
		} else if (position == 26) { //參數27
			mSpiner.setAdapter(mAutoPulse);
			value = (String)mAutoPulse.getItem(index);
			SystemConfigFile.mResv27 = getSwitchvalue(index);
		}
		view.setText(value);
		mSettingItems[position].mValue = value;
	}
	
	/**
	 * 检查已给定参数是否越界，如果越界就重置为默认值	
	 */
	public void checkParams() {
		Debug.d(TAG, "===>checkParams");
		SystemConfigFile.mParam2 = SystemConfigFile.checkParam(2, SystemConfigFile.mParam2);
		mSettingItems[1].mValue = String.valueOf(SystemConfigFile.mParam2); 
		
		SystemConfigFile.mParam3 = SystemConfigFile.checkParam(3, SystemConfigFile.mParam3);
		mSettingItems[2].mValue = String.valueOf(SystemConfigFile.mParam3);
		
		SystemConfigFile.mParam4 = SystemConfigFile.checkParam(4, SystemConfigFile.mParam4);
		mSettingItems[3].mValue = String.valueOf(SystemConfigFile.mParam4);
		
		SystemConfigFile.mParam5 = SystemConfigFile.checkParam(5, SystemConfigFile.mParam5);
		mSettingItems[4].mValue = String.valueOf(SystemConfigFile.mParam5);
		
		SystemConfigFile.mParam6 = SystemConfigFile.checkParam(6, SystemConfigFile.mParam6);
		mSettingItems[5].mValue = String.valueOf(SystemConfigFile.mParam6);
		
		SystemConfigFile.mParam7 = SystemConfigFile.checkParam(7, SystemConfigFile.mParam7);
		mSettingItems[6].mValue = String.valueOf(SystemConfigFile.mParam7);
		
		SystemConfigFile.mParam8 = SystemConfigFile.checkParam(8, SystemConfigFile.mParam8);
		mSettingItems[7].mValue = String.valueOf(SystemConfigFile.mParam8);
		
		SystemConfigFile.mParam9 = SystemConfigFile.checkParam(9, SystemConfigFile.mParam9);
		mSettingItems[8].mValue = String.valueOf(SystemConfigFile.mParam9);
		
		SystemConfigFile.mParam10 = SystemConfigFile.checkParam(10, SystemConfigFile.mParam10);
		if (SystemConfigFile.mParam10 < SystemConfigFile.mParam5) {
			SystemConfigFile.mParam10 = SystemConfigFile.mParam5;
		}
		mSettingItems[9].mValue = String.valueOf(SystemConfigFile.mParam10);
		
		SystemConfigFile.mResv12 = SystemConfigFile.checkParam(12, SystemConfigFile.mResv12);
		mSettingItems[11].mValue = String.valueOf(SystemConfigFile.mResv12);
		
		SystemConfigFile.mResv13 = SystemConfigFile.checkParam(13, SystemConfigFile.mResv13);
		mSettingItems[12].mValue = String.valueOf(SystemConfigFile.mResv13);
		
		SystemConfigFile.mResv14 = SystemConfigFile.checkParam(14, SystemConfigFile.mResv14);
		mSettingItems[13].mValue = String.valueOf(SystemConfigFile.mResv14);
		
		SystemConfigFile.mResv15 = SystemConfigFile.checkParam(15, SystemConfigFile.mResv15);
		mSettingItems[14].mValue = String.valueOf(SystemConfigFile.mResv15);

		SystemConfigFile.mResv16 = SystemConfigFile.checkParam(16, SystemConfigFile.mResv16);
		mSettingItems[15].mValue = String.valueOf(SystemConfigFile.mResv16);
		refresh();
	}
	
	public void refresh() {
		notifyDataSetChanged();
	}
	
	private class SelfTextWatcher implements TextWatcher {
		
		private EditText mEditText;
		
		public SelfTextWatcher(EditText e) {
			mEditText = e;
		}
		
		@Override
		public void afterTextChanged(Editable arg0) {
			int pos = (Integer) mEditText.getTag();
			
			Debug.d(TAG, "===>afterTextChanged, position=" + mEditText.getTag());
			
			mSettingItems[pos].mValue = arg0.toString();
			
			switch (pos) {
			case 2:
				SystemConfigFile.mParam3 = getValueFromEditText(arg0);
				break;
			case 3:
				SystemConfigFile.mParam4 = getValueFromEditText(arg0);
				break;
			case 4:
				SystemConfigFile.mParam5 = getValueFromEditText(arg0);
				break;
			case 5:
				SystemConfigFile.mParam6 = getValueFromEditText(arg0);
				break;
			case 6:
				SystemConfigFile.mParam7 = getValueFromEditText(arg0);
				break;
			case 7:
				SystemConfigFile.mParam8 = getValueFromEditText(arg0);
				break;
			case 8:
				SystemConfigFile.mParam9 = getValueFromEditText(arg0);
				break;
			case 9:
				SystemConfigFile.mParam10 = getValueFromEditText(arg0);
				break;
			case 10:
				SystemConfigFile.mResv11 = getValueFromEditText(arg0);
				Debug.d(TAG, "--->mResv11=" + SystemConfigFile.mResv11);
				break;
			case 11:
				SystemConfigFile.mResv12 = getValueFromEditText(arg0);
				break;
			case 12:
				SystemConfigFile.mResv13 = getValueFromEditText(arg0);
				break;
			case 13:
				SystemConfigFile.mResv14 = getValueFromEditText(arg0);
				break;
			case 14:
				SystemConfigFile.mResv15 = getValueFromEditText(arg0);
				break;
			case 15:
				SystemConfigFile.mResv16 = getValueFromEditText(arg0);
				break;
			case 16:
				SystemConfigFile.mResv17 = getValueFromEditText(arg0);
			case 17:
				SystemConfigFile.mResv18 = getValueFromEditText(arg0);
				break;
			case 18:
				SystemConfigFile.mResv19 = getValueFromEditText(arg0);
				break;
			case 19:
				SystemConfigFile.mResv20 = getValueFromEditText(arg0);
				break;
			case 20:
				SystemConfigFile.mResv21 = getValueFromEditText(arg0);
				break;
			case 21:
				SystemConfigFile.mResv22 = getValueFromEditText(arg0);
				break;
			case 22:
				SystemConfigFile.mResv23 = getValueFromEditText(arg0);
				break;
			case 23:
				SystemConfigFile.mResv24 = getValueFromEditText(arg0);
				break;
			case 24:
				SystemConfigFile.mResv25 = getValueFromEditText(arg0);
				break;
			case 25:
				SystemConfigFile.mResv26 = getValueFromEditText(arg0);
				break;
			case 26:
				SystemConfigFile.mResv27 = getValueFromEditText(arg0);
				break;
			case 27:
				SystemConfigFile.mResv28 = getValueFromEditText(arg0);
				break;
			case 28:
				SystemConfigFile.mResv29 = getValueFromEditText(arg0);
				break;
			case 29:
				SystemConfigFile.mResv30 = getValueFromEditText(arg0);
				break;
			case 30:
				SystemConfigFile.mResv31 = getValueFromEditText(arg0);
				break;
			case 31:
				SystemConfigFile.mResv32 = getValueFromEditText(arg0);
				break;
			case 32:
				SystemConfigFile.mResv33 = getValueFromEditText(arg0);
				break;
			case 33:
				SystemConfigFile.mResv34 = getValueFromEditText(arg0);
				break;
			case 34:
				SystemConfigFile.mResv35 = getValueFromEditText(arg0);
				break;
			case 35:
				SystemConfigFile.mResv36 = getValueFromEditText(arg0);
				break;
			case 36:
				SystemConfigFile.mResv37 = getValueFromEditText(arg0);
				break;
			case 37:
				SystemConfigFile.mResv38 = getValueFromEditText(arg0);
				break;
			case 38:
				SystemConfigFile.mResv39 = getValueFromEditText(arg0);
				break;
			case 39:
				SystemConfigFile.mResv40 = getValueFromEditText(arg0);
				break;
			case 40:
				SystemConfigFile.mResv41 = getValueFromEditText(arg0);
				break;
			case 41:
				SystemConfigFile.mResv42 = getValueFromEditText(arg0);
				break;
			case 42:
				SystemConfigFile.mResv43 = getValueFromEditText(arg0);
				break;
			case 43:
				SystemConfigFile.mResv44 = getValueFromEditText(arg0);
				break;
			case 44:
				SystemConfigFile.mResv45 = getValueFromEditText(arg0);
				break;
			case 45:
				SystemConfigFile.mResv46 = getValueFromEditText(arg0);
				break;
			case 46:
				SystemConfigFile.mResv47 = getValueFromEditText(arg0);
				break;
			case 47:
				SystemConfigFile.mResv48 = getValueFromEditText(arg0);
				break;
			case 48:
				SystemConfigFile.mResv49 = getValueFromEditText(arg0);
				break;
			case 49:
				SystemConfigFile.mResv50 = getValueFromEditText(arg0);
				break;
			case 50:
				SystemConfigFile.mResv51 = getValueFromEditText(arg0);
				break;
			case 51:
				SystemConfigFile.mResv52 = getValueFromEditText(arg0);
				break;
			case 52:
				SystemConfigFile.mResv53 = getValueFromEditText(arg0);
				break;
			case 53:
				SystemConfigFile.mResv54 = getValueFromEditText(arg0);
				break;
			case 54:
				SystemConfigFile.mResv55 = getValueFromEditText(arg0);
				break;
			case 55:
				SystemConfigFile.mResv56 = getValueFromEditText(arg0);
				break;
			case 56:
				SystemConfigFile.mResv57 = getValueFromEditText(arg0);
				break;
			case 57:
				SystemConfigFile.mResv58 = getValueFromEditText(arg0);
				break;
			case 58:
				SystemConfigFile.mResv59 = getValueFromEditText(arg0);
				break;
			case 59:
				SystemConfigFile.mResv60 = getValueFromEditText(arg0);
				break;
			case 60:
				SystemConfigFile.mResv61 = getValueFromEditText(arg0);
				break;
			case 61:
				SystemConfigFile.mResv62 = getValueFromEditText(arg0);
				break;
			case 62:
				SystemConfigFile.mResv63 = getValueFromEditText(arg0);
				break;
			case 63:
				SystemConfigFile.mResv64 = getValueFromEditText(arg0);
				break;
				
			default:
				break;
			}
			
			
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}
	}

	private int getValueFromEditText(Editable s) {
		int iv = 0;
		String value = s.toString();
		try {
			iv = Integer.parseInt(value);
			Debug.d(TAG, "--->getValueFromEditText:" + iv);
		} catch (Exception e) {
			
		}
		// mHandler.removeMessages(PRINTER_SETTINGS_CHANGED);
		// mHandler.sendEmptyMessageDelayed(PRINTER_SETTINGS_CHANGED, 10000);
		return iv;
	}

	private int getSwitchvalue(int index) {
		return index;
	}
	
	private int getDirectionvalue(int index) {
		return index;
	}
	
	private int getValue(PopWindowAdapter adapter, int index) {
		int v = Integer.parseInt((String) adapter.getItem(index));
		return v;
	}
	
}

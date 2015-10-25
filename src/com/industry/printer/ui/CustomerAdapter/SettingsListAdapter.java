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

import android.R.integer;
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
	
	private ItemViewHolder mEncoderHolder;
	private HashMap<Integer, ItemViewHolder> mHoldMap;
	
	private ItemOneLine[] mSettingItems = {
		new ItemOneLine(R.string.str_textview_param1, "", 0, 	R.string.str_textview_param2, String.valueOf(SystemConfigFile.mParam2), 0),
		new ItemOneLine(R.string.str_textview_param3, String.valueOf(SystemConfigFile.mParam3), R.string.str_time_unit_ms,		R.string.str_textview_param4, String.valueOf(SystemConfigFile.mParam4), R.string.str_time_unit_ms),
		new ItemOneLine(R.string.str_textview_param5, String.valueOf(SystemConfigFile.mParam5), R.string.str_time_unit_100us,		R.string.str_textview_param6, String.valueOf(SystemConfigFile.mParam6), R.string.str_time_unit_ms),
		new ItemOneLine(R.string.str_textview_param7, String.valueOf(SystemConfigFile.mParam7), 0,		R.string.str_textview_param8, String.valueOf(SystemConfigFile.mParam8), 0),
		new ItemOneLine(R.string.str_textview_param9, String.valueOf(SystemConfigFile.mParam9), 0,		R.string.str_textview_param10, String.valueOf(SystemConfigFile.mParam10), R.string.str_time_unit_us),
		new ItemOneLine(R.string.str_textview_param11, String.valueOf(SystemConfigFile.mResv11), 0,	R.string.str_textview_param12, String.valueOf(SystemConfigFile.mResv12), 0),
		new ItemOneLine(R.string.str_textview_param13, String.valueOf(SystemConfigFile.mResv13), 0,		R.string.str_textview_param14, String.valueOf(SystemConfigFile.mResv14), 0),
		new ItemOneLine(R.string.str_textview_param15, String.valueOf(SystemConfigFile.mResv15), 0,		R.string.str_textview_param16, String.valueOf(SystemConfigFile.mResv16), 0),
		new ItemOneLine(R.string.str_textview_param17, String.valueOf(SystemConfigFile.mResv17), 0,		R.string.str_textview_param18, String.valueOf(SystemConfigFile.mResv18), 0),
		new ItemOneLine(R.string.str_textview_param19, String.valueOf(SystemConfigFile.mResv19), 0,		R.string.str_textview_param20, String.valueOf(SystemConfigFile.mResv20), 0),
		new ItemOneLine(R.string.str_textview_param21, String.valueOf(SystemConfigFile.mResv21), 0,		R.string.str_textview_param22, String.valueOf(SystemConfigFile.mResv22), 0),
		new ItemOneLine(R.string.str_textview_param23, String.valueOf(SystemConfigFile.mResv23), 0,		R.string.str_textview_param24, String.valueOf(SystemConfigFile.mResv24), 0),
		new ItemOneLine(R.string.str_textview_param25, String.valueOf(SystemConfigFile.mResv25), 0,		R.string.str_textview_param26, String.valueOf(SystemConfigFile.mResv26), 0),
		new ItemOneLine(R.string.str_textview_param27, String.valueOf(SystemConfigFile.mResv27), 0,		R.string.str_textview_param28, String.valueOf(SystemConfigFile.mResv28), 0),
		new ItemOneLine(R.string.str_textview_param29, String.valueOf(SystemConfigFile.mResv29), 0,		R.string.str_textview_param30, String.valueOf(SystemConfigFile.mResv30), 0),
		new ItemOneLine(R.string.str_textview_param31, String.valueOf(SystemConfigFile.mResv31), 0,		R.string.str_textview_param32, String.valueOf(SystemConfigFile.mResv32), 0),
		new ItemOneLine(R.string.str_textview_param33, String.valueOf(SystemConfigFile.mResv33), 0,		R.string.str_textview_param34, String.valueOf(SystemConfigFile.mResv34), 0),
		new ItemOneLine(R.string.str_textview_param35, String.valueOf(SystemConfigFile.mResv35), 0,		R.string.str_textview_param36, String.valueOf(SystemConfigFile.mResv36), 0),
		new ItemOneLine(R.string.str_textview_param37, String.valueOf(SystemConfigFile.mResv37), 0,		R.string.str_textview_param38, String.valueOf(SystemConfigFile.mResv38), 0),
		new ItemOneLine(R.string.str_textview_param39, String.valueOf(SystemConfigFile.mResv39), 0,		R.string.str_textview_param40, String.valueOf(SystemConfigFile.mResv40), 0),
		new ItemOneLine(R.string.str_textview_param41, String.valueOf(SystemConfigFile.mResv41), 0,		R.string.str_textview_param42, String.valueOf(SystemConfigFile.mResv42), 0),
		new ItemOneLine(R.string.str_textview_param43, String.valueOf(SystemConfigFile.mResv43), 0,		R.string.str_textview_param44, String.valueOf(SystemConfigFile.mResv44), 0),
		new ItemOneLine(R.string.str_textview_param45, String.valueOf(SystemConfigFile.mResv45), 0,		R.string.str_textview_param46, String.valueOf(SystemConfigFile.mResv46), 0),
		new ItemOneLine(R.string.str_textview_param47, String.valueOf(SystemConfigFile.mResv47), 0,		R.string.str_textview_param48, String.valueOf(SystemConfigFile.mResv48), 0),
		new ItemOneLine(R.string.str_textview_param49, String.valueOf(SystemConfigFile.mResv49), 0,		R.string.str_textview_param50, String.valueOf(SystemConfigFile.mResv50), 0),
		new ItemOneLine(R.string.str_textview_param51, String.valueOf(SystemConfigFile.mResv51), 0,		R.string.str_textview_param52, String.valueOf(SystemConfigFile.mResv52), 0),
		new ItemOneLine(R.string.str_textview_param53, String.valueOf(SystemConfigFile.mResv53), 0,		R.string.str_textview_param54, String.valueOf(SystemConfigFile.mResv54), 0),
		new ItemOneLine(R.string.str_textview_param55, String.valueOf(SystemConfigFile.mResv55), 0,		R.string.str_textview_param56, String.valueOf(SystemConfigFile.mResv56), 0),
		new ItemOneLine(R.string.str_textview_param57, String.valueOf(SystemConfigFile.mResv57), 0,		R.string.str_textview_param58, String.valueOf(SystemConfigFile.mResv58), 0),
		new ItemOneLine(R.string.str_textview_param59, String.valueOf(SystemConfigFile.mResv59), 0,		R.string.str_textview_param60, String.valueOf(SystemConfigFile.mResv60), 0),
		new ItemOneLine(R.string.str_textview_param61, String.valueOf(SystemConfigFile.mResv61), 0,		R.string.str_textview_param62, String.valueOf(SystemConfigFile.mResv62), 0),
		new ItemOneLine(R.string.str_textview_param63, String.valueOf(SystemConfigFile.mResv63), 0,		R.string.str_textview_param64, String.valueOf(SystemConfigFile.mResv64), 0),
	};
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
		public int mTitleL;
		public String mValueL;
		public int mUnitL;
		public int mTitleR;
		public String mValueR;
		public int mUnitR;
		
		public ItemOneLine(int id, String vl, int ul, int id2, String vr, int ur) {
			mTitleL = id;
			mValueL = vl;
			mUnitL = ul;
			mTitleR = id2;
			mValueR = vr;
			mUnitR = ur;
			
		}
	}
	
	public SettingsListAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// mTitles = mContext.getResources().getStringArray(R.array.str_settings_params);
		mHoldMap = new HashMap<Integer, ItemViewHolder>();
		mSettingItems[0].mValueL = getEncoder(SystemConfigFile.mParam1);
	}
	
	@Override
	public int getCount() {
		Debug.d(TAG, "--->getCount=" + mSettingItems.length);
		return mSettingItems.length;
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
			
			if (position == 0) {
				mSpiner = new PopWindowSpiner(mContext);
				mEncoderAdapter = new PopWindowAdapter(mContext, null);
				String[] items = mContext.getResources().getStringArray(R.array.encoder_item_entries); 
				// ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, R.id.textView_id, items);
				for (int i = 0; i < items.length; i++) {
					mEncoderAdapter.addItem(items[i]);
				}
				mSpiner.setAdapter(mEncoderAdapter);
				mSpiner.setFocusable(true);
				mHolder.mValueLTv.setOnClickListener(this);
				mSpiner.setOnItemClickListener(this);
			}
			convertView.setTag(mHolder);
			mHoldMap.put(position, mHolder);
			
			if (mSettingItems[position].mUnitL > 0) {
				mHolder.mUnitL.setText(mSettingItems[position].mUnitL);
			}
			if (mSettingItems[position].mUnitR > 0) {
				mHolder.mUnitR.setText(mSettingItems[position].mUnitR);
			}
			mHolder.mTitleL.setText(mContext.getString(mSettingItems[position].mTitleL));
			mHolder.mTitleR.setText(mContext.getString(mSettingItems[position].mTitleR));
			mHolder.mValueREt.setText(mSettingItems[position].mValueR);
			
			mHolder.mValueLEt.addTextChangedListener(new SelfTextWatcher(position, mHolder.mValueLEt));
			mHolder.mValueREt.addTextChangedListener(new SelfTextWatcher(position, mHolder.mValueREt));
		}
		
		
		if (position == 0) {
			mHolder.mValueLTv.setVisibility(View.VISIBLE);
			mHolder.mValueLEt.setVisibility(View.GONE);
			mHolder.mValueLTv.setText(mSettingItems[position].mValueL);
		} else {

			mHolder.mValueLTv.setVisibility(View.GONE);
			mHolder.mValueLEt.setVisibility(View.VISIBLE);
			mHolder.mValueLEt.setText(mSettingItems[position].mValueL);
		}
		
		
		Debug.d(TAG, "--->getView:position=" + position);
		return convertView;
	}
	
	private String getEncoder(int index) {
		mContext.getResources();
		String entries[] = mContext.getResources().getStringArray(R.array.encoder_item_entries);
		Debug.d(TAG, "--->getEncoder:entries[" + index + "]=" + entries[index]);
		if (entries == null || entries.length <= 0) {
			return null;
		}
		if (index<0 || index >= entries.length) {
			return entries[0];
		}
		return entries[index];
	}
	
	private int getEncoderIndex(String entry) {
		String entries[] = mContext.getResources().getStringArray(R.array.encoder_item_entries);
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
	public void onClick(View arg0) {
		ItemViewHolder holder = mHoldMap.get(0);
		if (holder != null) {
			mSpiner.setWidth(holder.mValueLTv.getWidth());
			mSpiner.showAsDropDown(holder.mValueLTv);
		}
		
	}

	@Override
	public void onItemClick(int index) {
		ItemViewHolder holder = mHoldMap.get(0);
		if (holder != null) {
			holder.mValueLTv.setText(getEncoder(index));
			SystemConfigFile.mParam1 = index;
		}
	}
	
	public void checkParams() {
		/*触发模式,有效值1,2,3,4*/
		ItemViewHolder holder = mHoldMap.get(0);
		if (SystemConfigFile.mParam2 < 1 || SystemConfigFile.mParam2 > 4) {
			Toast.makeText(mContext, R.string.str_toast_ink_unvalid, Toast.LENGTH_LONG);
			if (holder != null) {
				holder.mValueREt.setText("1");
				SystemConfigFile.mParam2 = 1;
			}
		}
		
		holder = mHoldMap.get(1);
		/*光电防抖(毫秒)	下发FPGA-S3	有效值0-600, */
		if (SystemConfigFile.mParam3 < 0 || SystemConfigFile.mParam3 > 600) {
			Toast.makeText(mContext, R.string.str_toast_photoelectricity_delay, Toast.LENGTH_LONG);
			if (holder != null) {
				holder.mValueLEt.setText("20");
				SystemConfigFile.mParam3 = 20;
			}
		}
		
		/*光电延时(毫秒）下发FPGA-S4	有效值0-65535*/
		if (SystemConfigFile.mParam4 < 0 || SystemConfigFile.mParam4 > 65535) {
			Toast.makeText(mContext, R.string.str_toast_photoelectricity_antishake, Toast.LENGTH_LONG);
			if (holder != null) {
				holder.mValueREt.setText("0");
				SystemConfigFile.mParam4 = 0;
			}
		}
		
		holder = mHoldMap.get(2);
		/*字宽(毫秒） 下发FPGA-S5 0-65535*/
		if (SystemConfigFile.mParam5 < 0 || SystemConfigFile.mParam5 > 65535) {
			Toast.makeText(mContext, R.string.str_toast_timingprint, Toast.LENGTH_LONG);
			if (holder != null) {
				holder.mValueLEt.setText("0");
				SystemConfigFile.mParam6 = 0;
			}
		}
		
		/*定时打印(毫秒) 下发FPGA- S6	0-65535*/
		if (SystemConfigFile.mParam6 < 0 || SystemConfigFile.mParam6 > 65535) {
			Toast.makeText(mContext, R.string.str_toast_timingprint, Toast.LENGTH_LONG);
			if (holder != null) {
				holder.mValueREt.setText("1000");
				SystemConfigFile.mParam6 = 1000;
			}
		}
		
		holder = mHoldMap.get(3);
		/*列间脉冲 下发FPGA- S7	1-50*/
		if (SystemConfigFile.mParam7 < 1 || SystemConfigFile.mParam7 > 50) {
			Toast.makeText(mContext, R.string.str_toast_timingprint, Toast.LENGTH_LONG);
			if (holder != null) {
				holder.mValueLEt.setText("0");
				SystemConfigFile.mParam7 = 0;
			}
		}
		
		/*定长脉冲 下发FPGA-S8 	1-65535*/
		if (SystemConfigFile.mParam8 < 1 || SystemConfigFile.mParam8 > 65535) {
			Toast.makeText(mContext, R.string.str_toast_timingprint, Toast.LENGTH_LONG);
			if (holder != null) {
				holder.mValueREt.setText("0");
				SystemConfigFile.mParam8 = 0;
			}
		}
		
		holder = mHoldMap.get(4);
		/*脉冲延时 下发FPGA-S9 	1-65535*/
		if (SystemConfigFile.mParam9 < 1 || SystemConfigFile.mParam9 > 65535) {
			Toast.makeText(mContext, R.string.str_toast_timingprint, Toast.LENGTH_LONG);
			if (holder != null) {
				holder.mValueLEt.setText("0");
				SystemConfigFile.mParam9 = 0;
			}
		}
		/*墨点大小(微秒)	下发FPGA-S10	有效值200-2000, */
		if (SystemConfigFile.mParam10 < 200 || SystemConfigFile.mParam10 > 2000) {
			Toast.makeText(mContext, R.string.str_toast_ink_unvalid, Toast.LENGTH_LONG);
			if (holder != null) {
				holder.mValueREt.setText("800");
				SystemConfigFile.mParam10 = 800;
			}
		}
		
		holder = mHoldMap.get(7);
		if (SystemConfigFile.mResv16 < 0 || SystemConfigFile.mResv16 > 9) {
			Toast.makeText(mContext, R.string.str_toast_timingprint, Toast.LENGTH_LONG);
			if (holder != null) {
				holder.mValueLEt.setText("0");
				SystemConfigFile.mResv16 = 0;
			}
		}
	}
	private class SelfTextWatcher implements TextWatcher {
		
		private int position;
		private EditText mEditText;
		
		public SelfTextWatcher(int pos, EditText e) {
			mEditText = e;
			position = pos;
		}
		
		@Override
		public void afterTextChanged(Editable arg0) {
			ItemViewHolder holder = mHoldMap.get(position);
			switch (position) {
			case 0:
				if (mEditText == holder.mValueLEt) {
					// SystemConfigFile.mParam2 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mParam2 = getValueFromEditText(arg0);
				}
				break;
			case 1:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mParam3 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mParam4 = getValueFromEditText(arg0);
				}
				break;
			case 2:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mParam5 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mParam6 = getValueFromEditText(arg0);
				}
				break;
			case 3:
				if (mEditText == holder.mValueLEt){
					SystemConfigFile.mParam7 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt){
					SystemConfigFile.mParam8 = getValueFromEditText(arg0);
				} 
				break;
			case 4:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mParam9 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mParam10 = getValueFromEditText(arg0);
				}
				break;
			case 5:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv11 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv12 = getValueFromEditText(arg0);
				} 
				break;
			case 6:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv13 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv14 = getValueFromEditText(arg0);
				} 
				break;
			case 7:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv15 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv16 = getValueFromEditText(arg0);
				} 
				break;
			case 8:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv17 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv18 = getValueFromEditText(arg0);
				} 
				break;
			case 9:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv19 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv20 = getValueFromEditText(arg0);
				} 
				break;
			case 10:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv21 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv22 = getValueFromEditText(arg0);
				} 
				break;
			case 11:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv23 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv24 = getValueFromEditText(arg0);
				} 
				break;
			case 12:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv25 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv26 = getValueFromEditText(arg0);
				} 
				break;
			case 13:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv27 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv28 = getValueFromEditText(arg0);
				} 
				break;
			case 14:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv29 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv30 = getValueFromEditText(arg0);
				} 
				break;
			case 15:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv31 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv32 = getValueFromEditText(arg0);
				} 
				break;
			case 16:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv33 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv34 = getValueFromEditText(arg0);
				} 
				break;
			case 17:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv35 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv36 = getValueFromEditText(arg0);
				} 
				break;
			case 18:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv37 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv38 = getValueFromEditText(arg0);
				} 
				break;
			case 19:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv39 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv40 = getValueFromEditText(arg0);
				} 
				break;
			case 20:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv41 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv42 = getValueFromEditText(arg0);
				} 
				break;
			case 21:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv43 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv44 = getValueFromEditText(arg0);
				} 
				break;
			case 22:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv45 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv46 = getValueFromEditText(arg0);
				} 
				break;
			case 23:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv47 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv48 = getValueFromEditText(arg0);
				} 
				break;
			case 24:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv49 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv50 = getValueFromEditText(arg0);
				} 
				break;
			case 25:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv51 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv52 = getValueFromEditText(arg0);
				} 
				break;
			case 26:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv53 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv54 = getValueFromEditText(arg0);
				} 
				break;
			case 27:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv55 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv56 = getValueFromEditText(arg0);
				} 
				break;
			case 28:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv57 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv58 = getValueFromEditText(arg0);
				} 
				break;
			case 29:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv59 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv60 = getValueFromEditText(arg0);
				} 
				break;
			case 30:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv61 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv62 = getValueFromEditText(arg0);
				} 
				break;
			case 31:
				if (mEditText == holder.mValueLEt) {
					SystemConfigFile.mResv63 = getValueFromEditText(arg0);
				} else if (mEditText == holder.mValueREt) {
					SystemConfigFile.mResv64 = getValueFromEditText(arg0);
				} 
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

}

package com.industry.printer;

import java.util.Collection;
import java.util.MissingResourceException;

import com.industry.printer.R.string;
import com.industry.printer.FileFormat.SystemConfigFile;
import com.industry.printer.Utils.Debug;
import com.industry.printer.ui.CustomerAdapter.PopWindowAdapter;
import com.industry.printer.ui.CustomerAdapter.PopWindowAdapter.IOnItemClickListener;
import com.industry.printer.widget.PopWindowSpiner;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PHSettingFragment extends Fragment implements OnItemSelectedListener, OnClickListener, IOnItemClickListener {
	
	private static final String TAG= PHSettingFragment.class.getSimpleName();

	//public Spinner mEncoder;
	public TextView mEncoder;
	public PopWindowSpiner mSpiner;
	public PopWindowAdapter mEncoderAdapter;
	
	public EditText mTrigermode;
	public EditText mPHO_H; //已经改成光电防抖
	public EditText mPHO_L; //已经改成光电延时
	public EditText mOutPeriod;
	public EditText mTimedPeriod; //定时打印
	public EditText mTimedPulse;
	public EditText mLenPulse;
	public EditText mDelayPulse;
	public EditText mHighLen; //墨点大小
	public EditText mResv11;
	public EditText mResv12;
	public EditText mResv13;
	public EditText mResv14;
	public EditText mResv15;
	public EditText mResv16;
	public EditText mResv17;
	public EditText mResv18;
	public EditText mResv19;
	public EditText mResv20;
	public EditText mResv21;
	public EditText mResv22;
	public EditText mResv23;
	public EditText mResv24;
	public EditText mResv25;
	public EditText mResv26;
	public EditText mResv27;
	public EditText mResv28;
	public EditText mResv29;
	public EditText mResv30;
	public EditText mResv31;
	public EditText mResv32;
	public EditText mResv33;
	public EditText mResv34;
	public EditText mResv35;
	public EditText mResv36;
	public EditText mResv37;
	public EditText mResv38;
	public EditText mResv39;
	public EditText mResv40;
	public EditText mResv41;
	public EditText mResv42;
	public EditText mResv43;
	public EditText mResv44;
	public EditText mResv45;
	public EditText mResv46;
	public EditText mResv47;
	public EditText mResv48;
	public EditText mResv49;
	public EditText mResv50;
	public EditText mResv51;
	public EditText mResv52;
	public EditText mResv53;
	public EditText mResv54;
	public EditText mResv55;
	public EditText mResv56;
	public EditText mResv57;
	public EditText mResv58;
	public EditText mResv59;
	public EditText mResv60;
	public EditText mResv61;
	public EditText mResv62;
	public EditText mResv63;
	public EditText mResv64;
	
	InputMethodManager mImm;
	public Context mContext;
	
	public PHSettingFragment(Context context) {
		mContext = context;
	}
	@Override  
    public void onCreate(Bundle savedInstanceState)  
    {  
        super.onCreate(savedInstanceState);  
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "===>onCreateView");
		
		return inflater.inflate(R.layout.phsetting_fragment_layout, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Debug.d(TAG, "--->onActivityCreated");
		SystemConfigFile.parseSystemCofig();
		// mEncoder = (Spinner) getView().findViewById(R.id.ph_set_encoder_value);
		mEncoder = (TextView) getView().findViewById(R.id.ph_set_encoder_value);
		mEncoder.setText(getEncoder(SystemConfigFile.mParam1));
		//mEncoder.setSelection(SystemConfigFile.mParam1);
		//mEncoder.setOnItemSelectedListener(this);
		
		mSpiner = new PopWindowSpiner(getActivity());
		mEncoderAdapter = new PopWindowAdapter(getActivity(), null);
		String[] items = getResources().getStringArray(R.array.encoder_item_entries); 
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, R.id.textView_id, items);
		for (int i = 0; i < items.length; i++) {
			mEncoderAdapter.addItem(items[i]);
		}
		mSpiner.setAdapter(mEncoderAdapter);
		mSpiner.setFocusable(true);
		mEncoder.setOnClickListener(this);
		mSpiner.setOnItemClickListener(this);
		//mEncoder.setAdapter(adapter);
		
		mTrigermode = (EditText) getView().findViewById(R.id.ph_set_trigerMode_value);
		mTrigermode.setText(String.valueOf(SystemConfigFile.mParam2));
		mTrigermode.addTextChangedListener(new SelfTextWatcher(mTrigermode));

		mPHO_H = (EditText) getView().findViewById(R.id.ph_set_PHODelayHigh_value);
		mPHO_H.setText(String.valueOf(SystemConfigFile.mParam3));
		mPHO_H.addTextChangedListener(new SelfTextWatcher(mPHO_H));

		mPHO_L = (EditText) getView().findViewById(R.id.ph_set_PHODelayLow_value);
		mPHO_L.setText(String.valueOf(SystemConfigFile.mParam4));
		mPHO_L.addTextChangedListener(new SelfTextWatcher(mPHO_L));

		mOutPeriod = (EditText) getView().findViewById(R.id.ph_set_PHOOutput_period_value);
		mOutPeriod.setText(String.valueOf(SystemConfigFile.mParam5));
		mOutPeriod.addTextChangedListener(new SelfTextWatcher(mOutPeriod));

		mTimedPeriod = (EditText) getView().findViewById(R.id.ph_set_TimeFixed_period_value);
		mTimedPeriod.setText(String.valueOf(SystemConfigFile.mParam6));
		mTimedPeriod.addTextChangedListener(new SelfTextWatcher(mTimedPeriod));

		mTimedPulse = (EditText) getView().findViewById(R.id.ph_set_EncoderTriger_pulse_value);
		mTimedPulse.setText(String.valueOf(SystemConfigFile.mParam7));
		mTimedPulse.addTextChangedListener(new SelfTextWatcher(mTimedPulse));


		mLenPulse = (EditText) getView().findViewById(R.id.ph_set_EncoderLenFixed_pulse_value);
		mLenPulse.setText(String.valueOf(SystemConfigFile.mParam8));
		mLenPulse.addTextChangedListener(new SelfTextWatcher(mLenPulse));

		mDelayPulse = (EditText) getView().findViewById(R.id.ph_set_EncoderDelay_pulse_value);
		mDelayPulse.setText(String.valueOf(SystemConfigFile.mParam9));
		mDelayPulse.addTextChangedListener(new SelfTextWatcher(mDelayPulse));

		mHighLen = (EditText) getView().findViewById(R.id.ph_set_OutputHight_length_value);
		mHighLen.setText(String.valueOf(SystemConfigFile.mParam10));
		mHighLen.addTextChangedListener(new SelfTextWatcher(mHighLen));
		
		mResv11 = (EditText) getView().findViewById(R.id.ph_set_resolved_value11);
		mResv11.addTextChangedListener(new SelfTextWatcher(mResv11));
		mResv11.setText(String.valueOf(SystemConfigFile.mResv11));

		mResv12 = (EditText) getView().findViewById(R.id.ph_set_resolved_value12);
		mResv12.addTextChangedListener(new SelfTextWatcher(mResv12));
		mResv12.setText(String.valueOf(SystemConfigFile.mResv12));

		mResv13 = (EditText) getView().findViewById(R.id.ph_set_resolved_value13);
		mResv13.addTextChangedListener(new SelfTextWatcher(mResv13));
		mResv13.setText(String.valueOf(SystemConfigFile.mResv13));

		mResv14 = (EditText) getView().findViewById(R.id.ph_set_resolved_value14);
		mResv14.addTextChangedListener(new SelfTextWatcher(mResv14));
		mResv14.setText(String.valueOf(SystemConfigFile.mResv14));

		mResv15 = (EditText) getView().findViewById(R.id.ph_set_resolved_value15);
		mResv15.addTextChangedListener(new SelfTextWatcher(mResv15));
		mResv15.setText(String.valueOf(SystemConfigFile.mResv15));

		mResv16 = (EditText) getView().findViewById(R.id.ph_set_resolved_value16);
		mResv16.addTextChangedListener(new SelfTextWatcher(mResv16));
		mResv16.setText(String.valueOf(SystemConfigFile.mResv16));

		mResv17 = (EditText) getView().findViewById(R.id.ph_set_resolved_value17);
		mResv17.addTextChangedListener(new SelfTextWatcher(mResv17));
		mResv17.setText(String.valueOf(SystemConfigFile.mResv17));

		mResv18 = (EditText) getView().findViewById(R.id.ph_set_resolved_value18);
		mResv18.addTextChangedListener(new SelfTextWatcher(mResv18));
		mResv18.setText(String.valueOf(SystemConfigFile.mResv18));

		mResv19 = (EditText) getView().findViewById(R.id.ph_set_resolved_value19);
		mResv19.addTextChangedListener(new SelfTextWatcher(mResv19));
		mResv19.setText(String.valueOf(SystemConfigFile.mResv19));

		mResv20 = (EditText) getView().findViewById(R.id.ph_set_resolved_value20);
		mResv20.addTextChangedListener(new SelfTextWatcher(mResv20));
		mResv20.setText(String.valueOf(SystemConfigFile.mResv20));

		mResv21 = (EditText) getView().findViewById(R.id.ph_set_resolved_value21);
		mResv21.addTextChangedListener(new SelfTextWatcher(mResv21));
		mResv21.setText(String.valueOf(SystemConfigFile.mResv21));

		mResv22 = (EditText) getView().findViewById(R.id.ph_set_resolved_value22);
		mResv22.addTextChangedListener(new SelfTextWatcher(mResv22));
		mResv22.setText(String.valueOf(SystemConfigFile.mResv22));

		mResv23 = (EditText) getView().findViewById(R.id.ph_set_resolved_value23);
		mResv23.addTextChangedListener(new SelfTextWatcher(mResv23));
		mResv23.setText(String.valueOf(SystemConfigFile.mResv23));

		mResv24 = (EditText) getView().findViewById(R.id.ph_set_resolved_value24);
		mResv24.addTextChangedListener(new SelfTextWatcher(mResv24));
		mResv24.setText(String.valueOf(SystemConfigFile.mResv24));
		
		mResv25 = (EditText) getView().findViewById(R.id.ph_set_resolved_value25);
		mResv25.addTextChangedListener(new SelfTextWatcher(mResv25));
		mResv25.setText(String.valueOf(SystemConfigFile.mResv25));
		
		mResv26 = (EditText) getView().findViewById(R.id.ph_set_resolved_value26);
		mResv26.addTextChangedListener(new SelfTextWatcher(mResv26));
		mResv26.setText(String.valueOf(SystemConfigFile.mResv26));
		
		mResv27 = (EditText) getView().findViewById(R.id.ph_set_resolved_value27);
		mResv27.addTextChangedListener(new SelfTextWatcher(mResv27));
		mResv27.setText(String.valueOf(SystemConfigFile.mResv27));
		
		mResv28 = (EditText) getView().findViewById(R.id.ph_set_resolved_value28);
		mResv28.addTextChangedListener(new SelfTextWatcher(mResv28));
		mResv28.setText(String.valueOf(SystemConfigFile.mResv28));
		
		mResv29 = (EditText) getView().findViewById(R.id.ph_set_resolved_value29);
		mResv29.addTextChangedListener(new SelfTextWatcher(mResv29));
		mResv29.setText(String.valueOf(SystemConfigFile.mResv29));
		
		mResv30 = (EditText) getView().findViewById(R.id.ph_set_resolved_value30);
		mResv30.addTextChangedListener(new SelfTextWatcher(mResv30));
		mResv30.setText(String.valueOf(SystemConfigFile.mResv30));
		
		mResv31 = (EditText) getView().findViewById(R.id.ph_set_resolved_value31);
		mResv31.addTextChangedListener(new SelfTextWatcher(mResv31));
		mResv31.setText(String.valueOf(SystemConfigFile.mResv31));

		mResv32 = (EditText) getView().findViewById(R.id.ph_set_resolved_value32);
		mResv32.addTextChangedListener(new SelfTextWatcher(mResv32));
		mResv32.setText(String.valueOf(SystemConfigFile.mResv32));

		mResv33 = (EditText) getView().findViewById(R.id.ph_set_resolved_value33);
		mResv33.addTextChangedListener(new SelfTextWatcher(mResv33));
		mResv33.setText(String.valueOf(SystemConfigFile.mResv33));

		mResv34 = (EditText) getView().findViewById(R.id.ph_set_resolved_value34);
		mResv34.addTextChangedListener(new SelfTextWatcher(mResv34));
		mResv34.setText(String.valueOf(SystemConfigFile.mResv34));
		
		mResv35 = (EditText) getView().findViewById(R.id.ph_set_resolved_value35);
		mResv35.addTextChangedListener(new SelfTextWatcher(mResv35));
		mResv35.setText(String.valueOf(SystemConfigFile.mResv35));
		
		mResv36 = (EditText) getView().findViewById(R.id.ph_set_resolved_value36);
		mResv36.addTextChangedListener(new SelfTextWatcher(mResv36));
		mResv36.setText(String.valueOf(SystemConfigFile.mResv36));
		
		mResv37 = (EditText) getView().findViewById(R.id.ph_set_resolved_value37);
		mResv37.addTextChangedListener(new SelfTextWatcher(mResv37));
		mResv37.setText(String.valueOf(SystemConfigFile.mResv37));
		
		mResv38 = (EditText) getView().findViewById(R.id.ph_set_resolved_value38);
		mResv38.addTextChangedListener(new SelfTextWatcher(mResv38));
		mResv38.setText(String.valueOf(SystemConfigFile.mResv38));
		
		mResv39 = (EditText) getView().findViewById(R.id.ph_set_resolved_value39);
		mResv39.addTextChangedListener(new SelfTextWatcher(mResv39));
		mResv39.setText(String.valueOf(SystemConfigFile.mResv39));
		
		mResv40 = (EditText) getView().findViewById(R.id.ph_set_resolved_value40);
		mResv40.addTextChangedListener(new SelfTextWatcher(mResv40));
		mResv40.setText(String.valueOf(SystemConfigFile.mResv40));
		
		mResv41 = (EditText) getView().findViewById(R.id.ph_set_resolved_value41);
		mResv41.addTextChangedListener(new SelfTextWatcher(mResv41));
		mResv41.setText(String.valueOf(SystemConfigFile.mResv41));

		mResv42 = (EditText) getView().findViewById(R.id.ph_set_resolved_value42);
		mResv42.addTextChangedListener(new SelfTextWatcher(mResv42));
		mResv42.setText(String.valueOf(SystemConfigFile.mResv42));

		mResv43 = (EditText) getView().findViewById(R.id.ph_set_resolved_value43);
		mResv43.addTextChangedListener(new SelfTextWatcher(mResv43));
		mResv43.setText(String.valueOf(SystemConfigFile.mResv43));

		mResv44 = (EditText) getView().findViewById(R.id.ph_set_resolved_value44);
		mResv44.addTextChangedListener(new SelfTextWatcher(mResv44));
		mResv44.setText(String.valueOf(SystemConfigFile.mResv44));
	
		mResv45 = (EditText) getView().findViewById(R.id.ph_set_resolved_value45);
		mResv45.addTextChangedListener(new SelfTextWatcher(mResv45));
		mResv45.setText(String.valueOf(SystemConfigFile.mResv45));
		
		mResv46 = (EditText) getView().findViewById(R.id.ph_set_resolved_value46);
		mResv46.addTextChangedListener(new SelfTextWatcher(mResv46));
		mResv46.setText(String.valueOf(SystemConfigFile.mResv46));
		
		mResv47 = (EditText) getView().findViewById(R.id.ph_set_resolved_value47);
		mResv47.addTextChangedListener(new SelfTextWatcher(mResv47));
		mResv47.setText(String.valueOf(SystemConfigFile.mResv47));
		
		mResv48 = (EditText) getView().findViewById(R.id.ph_set_resolved_value48);
		mResv48.addTextChangedListener(new SelfTextWatcher(mResv48));
		mResv48.setText(String.valueOf(SystemConfigFile.mResv48));
		
		mResv49 = (EditText) getView().findViewById(R.id.ph_set_resolved_value49);
		mResv49.addTextChangedListener(new SelfTextWatcher(mResv49));
		mResv49.setText(String.valueOf(SystemConfigFile.mResv49));
		
		mResv50 = (EditText) getView().findViewById(R.id.ph_set_resolved_value50);
		mResv50.addTextChangedListener(new SelfTextWatcher(mResv50));
		mResv50.setText(String.valueOf(SystemConfigFile.mResv50));
		
		mResv51 = (EditText) getView().findViewById(R.id.ph_set_resolved_value51);
		mResv51.addTextChangedListener(new SelfTextWatcher(mResv51));
		mResv51.setText(String.valueOf(SystemConfigFile.mResv51));

		mResv52 = (EditText) getView().findViewById(R.id.ph_set_resolved_value52);
		mResv52.addTextChangedListener(new SelfTextWatcher(mResv52));
		mResv52.setText(String.valueOf(SystemConfigFile.mResv52));

		mResv53 = (EditText) getView().findViewById(R.id.ph_set_resolved_value53);
		mResv53.addTextChangedListener(new SelfTextWatcher(mResv53));
		mResv53.setText(String.valueOf(SystemConfigFile.mResv53));

		mResv54 = (EditText) getView().findViewById(R.id.ph_set_resolved_value54);
		mResv54.addTextChangedListener(new SelfTextWatcher(mResv54));
		mResv54.setText(String.valueOf(SystemConfigFile.mResv54));
		
		mResv55 = (EditText) getView().findViewById(R.id.ph_set_resolved_value55);
		mResv55.addTextChangedListener(new SelfTextWatcher(mResv55));
		mResv55.setText(String.valueOf(SystemConfigFile.mResv55));
		
		mResv56 = (EditText) getView().findViewById(R.id.ph_set_resolved_value56);
		mResv56.addTextChangedListener(new SelfTextWatcher(mResv56));
		mResv56.setText(String.valueOf(SystemConfigFile.mResv56));
		
		mResv57 = (EditText) getView().findViewById(R.id.ph_set_resolved_value57);
		mResv57.addTextChangedListener(new SelfTextWatcher(mResv57));
		mResv57.setText(String.valueOf(SystemConfigFile.mResv57));
		
		mResv58 = (EditText) getView().findViewById(R.id.ph_set_resolved_value58);
		mResv58.addTextChangedListener(new SelfTextWatcher(mResv58));
		mResv58.setText(String.valueOf(SystemConfigFile.mResv58));
		
		mResv59 = (EditText) getView().findViewById(R.id.ph_set_resolved_value59);
		mResv59.addTextChangedListener(new SelfTextWatcher(mResv59));
		mResv59.setText(String.valueOf(SystemConfigFile.mResv59));
		
		mResv60 = (EditText) getView().findViewById(R.id.ph_set_resolved_value60);
		mResv60.addTextChangedListener(new SelfTextWatcher(mResv60));
		mResv60.setText(String.valueOf(SystemConfigFile.mResv60));
		
		mResv61 = (EditText) getView().findViewById(R.id.ph_set_resolved_value61);
		mResv61.addTextChangedListener(new SelfTextWatcher(mResv61));
		mResv61.setText(String.valueOf(SystemConfigFile.mResv61));
		
		mResv62 = (EditText) getView().findViewById(R.id.ph_set_resolved_value62);
		mResv62.addTextChangedListener(new SelfTextWatcher(mResv62));
		mResv62.setText(String.valueOf(SystemConfigFile.mResv62));
		
		mResv63 = (EditText) getView().findViewById(R.id.ph_set_resolved_value63);
		mResv63.addTextChangedListener(new SelfTextWatcher(mResv63));
		mResv63.setText(String.valueOf(SystemConfigFile.mResv63));
		
		mResv64 = (EditText) getView().findViewById(R.id.ph_set_resolved_value64);
		mResv64.addTextChangedListener(new SelfTextWatcher(mResv64));
		mResv64.setText(String.valueOf(SystemConfigFile.mResv64));
		
		Debug.d(TAG, "--->onActivityCreated 384");
		
		mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE); 
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Debug.d(TAG, "--->onstart");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Debug.d(TAG, "--->onResume");
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch (view.getId()) {
			case R.id.ph_set_encoder_value:
				SystemConfigFile.mParam1 = getEncoderIndex(mEncoder.getText().toString());
				break;
		}
	}
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
	
	private int getValueFromEditText(Editable s) {
		int iv = 0;
		String value = s.toString();
		try {
			iv = Integer.parseInt(value);
			Debug.d(TAG, "--->getValueFromEditText:" + iv);
		} catch (Exception e) {
			
		}
		mHandler.removeMessages(PRINTER_SETTINGS_CHANGED);
		mHandler.sendEmptyMessageDelayed(PRINTER_SETTINGS_CHANGED, 10000);
		return iv;
	}
	
	public void reloadSettings() {
		SystemConfigFile.parseSystemCofig();
		// mEncoder.setSelection(SystemConfigFile.mParam1);
		
		mEncoder.setText(getEncoder(SystemConfigFile.mParam1));
		mTrigermode.setText(String.valueOf(SystemConfigFile.mParam2));
		mPHO_H.setText(String.valueOf(SystemConfigFile.mParam3));
		mPHO_L.setText(String.valueOf(SystemConfigFile.mParam4));
		mOutPeriod.setText(String.valueOf(SystemConfigFile.mParam5));
		mTimedPeriod.setText(String.valueOf(SystemConfigFile.mParam6));
		mTimedPulse.setText(String.valueOf(SystemConfigFile.mParam7));
		mLenPulse.setText(String.valueOf(SystemConfigFile.mParam8));
		mDelayPulse.setText(String.valueOf(SystemConfigFile.mParam9));
		mHighLen.setText(String.valueOf(SystemConfigFile.mParam10));
		mResv16.setText(String.valueOf(SystemConfigFile.mResv16));
	}
	
	private static final int PRINTER_SETTINGS_CHANGED = 1;
	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) { 
			switch (msg.what) {
			case PRINTER_SETTINGS_CHANGED:
				SystemConfigFile.saveConfig();
				break;

			default:
				break;
			}
		}
	};

	
	private class SelfTextWatcher implements TextWatcher {
		
		private EditText mEditText;
		
		public SelfTextWatcher(EditText e) {
			mEditText = e;
		}
		
		@Override
		public void afterTextChanged(Editable arg0) {
			if (mEditText == mTrigermode) {
				SystemConfigFile.mParam2 = getValueFromEditText(arg0);
			} else if (mEditText == mDelayPulse) {
				SystemConfigFile.mParam9 = getValueFromEditText(arg0);
			} else if (mEditText == mTimedPulse){
				SystemConfigFile.mParam7 = getValueFromEditText(arg0);
			} else if (mEditText == mTimedPeriod) {
				SystemConfigFile.mParam6 = getValueFromEditText(arg0);
			} else if (mEditText == mOutPeriod) {
				SystemConfigFile.mParam5 = getValueFromEditText(arg0);
			} else if (mEditText == mPHO_H) {
				SystemConfigFile.mParam3 = getValueFromEditText(arg0);
			} else if (mEditText == mPHO_L) {
				SystemConfigFile.mParam4 = getValueFromEditText(arg0);
			} else if (mEditText == mLenPulse){
				SystemConfigFile.mParam8 = getValueFromEditText(arg0);
			} else if (mEditText == mHighLen) {
				SystemConfigFile.mParam10 = getValueFromEditText(arg0);
			} else if (mEditText == mResv11) {
				SystemConfigFile.mResv11 = getValueFromEditText(arg0);
			} else if (mEditText == mResv12) {
				SystemConfigFile.mResv12 = getValueFromEditText(arg0);
			} else if (mEditText == mResv13) {
				SystemConfigFile.mResv13 = getValueFromEditText(arg0);
			} else if (mEditText == mResv14) {
				SystemConfigFile.mResv14 = getValueFromEditText(arg0);
			} else if (mEditText == mResv15) {
				SystemConfigFile.mResv15 = getValueFromEditText(arg0);
			} else if (mEditText == mResv16) {
				SystemConfigFile.mResv16 = getValueFromEditText(arg0);
			} else if (mEditText == mResv17) {
				SystemConfigFile.mResv17 = getValueFromEditText(arg0);
			} else if (mEditText == mResv18) {
				SystemConfigFile.mResv18 = getValueFromEditText(arg0);
			} else if (mEditText == mResv19) {
				SystemConfigFile.mResv19 = getValueFromEditText(arg0);
			} else if (mEditText == mResv20) {
				SystemConfigFile.mResv20 = getValueFromEditText(arg0);
			} else if (mEditText == mResv21) {
				SystemConfigFile.mResv21 = getValueFromEditText(arg0);
			} else if (mEditText == mResv22) {
				SystemConfigFile.mResv22 = getValueFromEditText(arg0);
			} else if (mEditText == mResv23) {
				SystemConfigFile.mResv23 = getValueFromEditText(arg0);
			} else if (mEditText == mResv24) {
				SystemConfigFile.mResv24 = getValueFromEditText(arg0);
			} else if (mEditText == mResv25) {
				SystemConfigFile.mResv25 = getValueFromEditText(arg0);
			} else if (mEditText == mResv26) {
				SystemConfigFile.mResv26 = getValueFromEditText(arg0);
			} else if (mEditText == mResv27) {
				SystemConfigFile.mResv27 = getValueFromEditText(arg0);
			} else if (mEditText == mResv28) {
				SystemConfigFile.mResv28 = getValueFromEditText(arg0);
			} else if (mEditText == mResv29) {
				SystemConfigFile.mResv29 = getValueFromEditText(arg0);
			} else if (mEditText == mResv30) {
				SystemConfigFile.mResv30 = getValueFromEditText(arg0);
			} else if (mEditText == mResv31) {
				SystemConfigFile.mResv31 = getValueFromEditText(arg0);
			} else if (mEditText == mResv32) {
				SystemConfigFile.mResv32 = getValueFromEditText(arg0);
			} else if (mEditText == mResv33) {
				SystemConfigFile.mResv33 = getValueFromEditText(arg0);
			} else if (mEditText == mResv34) {
				SystemConfigFile.mResv34 = getValueFromEditText(arg0);
			} else if (mEditText == mResv35) {
				SystemConfigFile.mResv35 = getValueFromEditText(arg0);
			} else if (mEditText == mResv36) {
				SystemConfigFile.mResv36 = getValueFromEditText(arg0);
			} else if (mEditText == mResv37) {
				SystemConfigFile.mResv37 = getValueFromEditText(arg0);
			} else if (mEditText == mResv38) {
				SystemConfigFile.mResv38 = getValueFromEditText(arg0);
			} else if (mEditText == mResv39) {
				SystemConfigFile.mResv39 = getValueFromEditText(arg0);
			} else if (mEditText == mResv40) {
				SystemConfigFile.mResv40 = getValueFromEditText(arg0);
			} else if (mEditText == mResv41) {
				SystemConfigFile.mResv41 = getValueFromEditText(arg0);
			} else if (mEditText == mResv42) {
				SystemConfigFile.mResv42 = getValueFromEditText(arg0);
			} else if (mEditText == mResv43) {
				SystemConfigFile.mResv43 = getValueFromEditText(arg0);
			} else if (mEditText == mResv44) {
				SystemConfigFile.mResv44 = getValueFromEditText(arg0);
			} else if (mEditText == mResv45) {
				SystemConfigFile.mResv45 = getValueFromEditText(arg0);
			} else if (mEditText == mResv46) {
				SystemConfigFile.mResv46 = getValueFromEditText(arg0);
			} else if (mEditText == mResv47) {
				SystemConfigFile.mResv47 = getValueFromEditText(arg0);
			} else if (mEditText == mResv48) {
				SystemConfigFile.mResv48 = getValueFromEditText(arg0);
			} else if (mEditText == mResv49) {
				SystemConfigFile.mResv49 = getValueFromEditText(arg0);
			} else if (mEditText == mResv50) {
				SystemConfigFile.mResv50 = getValueFromEditText(arg0);
			} else if (mEditText == mResv51) {
				SystemConfigFile.mResv51 = getValueFromEditText(arg0);
			} else if (mEditText == mResv52) {
				SystemConfigFile.mResv52 = getValueFromEditText(arg0);
			} else if (mEditText == mResv53) {
				SystemConfigFile.mResv53 = getValueFromEditText(arg0);
			} else if (mEditText == mResv54) {
				SystemConfigFile.mResv54 = getValueFromEditText(arg0);
			} else if (mEditText == mResv55) {
				SystemConfigFile.mResv55 = getValueFromEditText(arg0);
			} else if (mEditText == mResv56) {
				SystemConfigFile.mResv56 = getValueFromEditText(arg0);
			} else if (mEditText == mResv57) {
				SystemConfigFile.mResv57 = getValueFromEditText(arg0);
			} else if (mEditText == mResv58) {
				SystemConfigFile.mResv58 = getValueFromEditText(arg0);
			} else if (mEditText == mResv59) {
				SystemConfigFile.mResv59 = getValueFromEditText(arg0);
			} else if (mEditText == mResv60) {
				SystemConfigFile.mResv60 = getValueFromEditText(arg0);
			} else if (mEditText == mResv61) {
				SystemConfigFile.mResv61 = getValueFromEditText(arg0);
			} else if (mEditText == mResv62) {
				SystemConfigFile.mResv62 = getValueFromEditText(arg0);
			} else if (mEditText == mResv63) {
				SystemConfigFile.mResv63 = getValueFromEditText(arg0);
			} else if (mEditText == mResv64) {
				SystemConfigFile.mResv64 = getValueFromEditText(arg0);
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


	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()) {
			case R.id.ph_set_encoder_value:
				Debug.d(TAG, "--->onclick encoder");
				mSpiner.setWidth(mEncoder.getWidth());
				mSpiner.showAsDropDown(mEncoder);
				break;
		}
	}
	
	private String getEncoder(int index) {
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
	public void onItemClick(int index) {
		mEncoder.setText(getEncoder(index));
		SystemConfigFile.mParam1 = index;
	}
	
	public void checkParams() {
		/*触发模式,有效值1,2,3,4*/
		if (SystemConfigFile.mParam2 < 1 || SystemConfigFile.mParam2 > 4) {
			Toast.makeText(mContext, R.string.str_toast_ink_unvalid, Toast.LENGTH_LONG);
			mTrigermode.setText("1");
			SystemConfigFile.mParam2 = 1;
		}
		
		/*光电防抖(毫秒)	下发FPGA-S3	有效值0-600, */
		if (SystemConfigFile.mParam3 < 0 || SystemConfigFile.mParam3 > 600) {
			Toast.makeText(mContext, R.string.str_toast_photoelectricity_delay, Toast.LENGTH_LONG);
			mPHO_H.setText("20");
			SystemConfigFile.mParam3 = 20;
		}
		
		/*光电延时(毫秒）下发FPGA-S4	有效值0-65535*/
		if (SystemConfigFile.mParam4 < 0 || SystemConfigFile.mParam4 > 65535) {
			Toast.makeText(mContext, R.string.str_toast_photoelectricity_antishake, Toast.LENGTH_LONG);
			mPHO_L.setText("0");
			SystemConfigFile.mParam4 = 0;
		}
		
		/*字宽(毫秒） 下发FPGA-S5 0-65535*/
		if (SystemConfigFile.mParam5 < 0 || SystemConfigFile.mParam5 > 65535) {
			Toast.makeText(mContext, R.string.str_toast_timingprint, Toast.LENGTH_LONG);
			mOutPeriod.setText("0");
			SystemConfigFile.mParam6 = 0;
		}
		
		/*定时打印(毫秒) 下发FPGA- S6	0-65535*/
		if (SystemConfigFile.mParam6 < 0 || SystemConfigFile.mParam6 > 65535) {
			Toast.makeText(mContext, R.string.str_toast_timingprint, Toast.LENGTH_LONG);
			mTimedPeriod.setText("1000");
			SystemConfigFile.mParam6 = 1000;
		}
		
		/*列间脉冲 下发FPGA- S7	1-50*/
		if (SystemConfigFile.mParam7 < 1 || SystemConfigFile.mParam7 > 50) {
			Toast.makeText(mContext, R.string.str_toast_timingprint, Toast.LENGTH_LONG);
			mTimedPulse.setText("0");
			SystemConfigFile.mParam7 = 0;
		}
		
		/*定长脉冲 下发FPGA-S8 	1-65535*/
		if (SystemConfigFile.mParam8 < 1 || SystemConfigFile.mParam8 > 65535) {
			Toast.makeText(mContext, R.string.str_toast_timingprint, Toast.LENGTH_LONG);
			mLenPulse.setText("0");
			SystemConfigFile.mParam8 = 0;
		}
		
		/*脉冲延时 下发FPGA-S9 	1-65535*/
		if (SystemConfigFile.mParam9 < 1 || SystemConfigFile.mParam9 > 65535) {
			Toast.makeText(mContext, R.string.str_toast_timingprint, Toast.LENGTH_LONG);
			mDelayPulse.setText("0");
			SystemConfigFile.mParam9 = 0;
		}
		/*墨点大小(微秒)	下发FPGA-S10	有效值200-2000, */
		if (SystemConfigFile.mParam10 < 200 || SystemConfigFile.mParam10 > 2000) {
			Toast.makeText(mContext, R.string.str_toast_ink_unvalid, Toast.LENGTH_LONG);
			mHighLen.setText("800");
			SystemConfigFile.mParam10 = 800;
		}
		
		if (SystemConfigFile.mResv16 < 0 || SystemConfigFile.mResv16 > 9) {
			Toast.makeText(mContext, R.string.str_toast_timingprint, Toast.LENGTH_LONG);
			mResv16.setText("0");
			SystemConfigFile.mResv16 = 0;
		}
	}
}

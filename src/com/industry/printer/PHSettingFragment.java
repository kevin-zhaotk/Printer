package com.industry.printer;

import java.util.Collection;

import com.industry.printer.FileFormat.SystemConfigFile;
import com.industry.printer.Utils.Debug;

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
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class PHSettingFragment extends Fragment implements OnItemSelectedListener {
	
	private static final String TAG= PHSettingFragment.class.getSimpleName();

	public Spinner mEncoder;
	public EditText mTrigermode;
	public EditText mPHO_H;
	public EditText mPHO_L;
	public EditText mOutPeriod;
	public EditText mTimedPeriod;
	public EditText mTimedPulse;
	public EditText mLenPulse;
	public EditText mDelayPulse;
	public EditText mHighLen;
	
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
		SystemConfigFile.parseSystemCofig();
		mEncoder = (Spinner) getView().findViewById(R.id.ph_set_encoder_value);
		mEncoder.setSelection(SystemConfigFile.mEncoder);
		mEncoder.setOnItemSelectedListener(this);
		
		mTrigermode = (EditText) getView().findViewById(R.id.ph_set_trigerMode_value);
		mTrigermode.setText(String.valueOf(SystemConfigFile.mTrigerMode));
		mTrigermode.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				SystemConfigFile.mTrigerMode = getValueFromEditText(s);
			}
		});

		mPHO_H = (EditText) getView().findViewById(R.id.ph_set_PHODelayHigh_value);
		mPHO_H.setText(String.valueOf(SystemConfigFile.mPHOHighDelay));
		mPHO_H.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				SystemConfigFile.mPHOHighDelay = getValueFromEditText(s);
			}
		});

		mPHO_L = (EditText) getView().findViewById(R.id.ph_set_PHODelayLow_value);
		mPHO_L.setText(String.valueOf(SystemConfigFile.mPHOLowDelay));
		mPHO_L.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				SystemConfigFile.mPHOLowDelay = getValueFromEditText(s);
			}
		});

		mOutPeriod = (EditText) getView().findViewById(R.id.ph_set_PHOOutput_period_value);
		mOutPeriod.setText(String.valueOf(SystemConfigFile.mPHOOutputPeriod));
		mOutPeriod.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				SystemConfigFile.mPHOOutputPeriod = getValueFromEditText(s);
			}
		});

		mTimedPeriod = (EditText) getView().findViewById(R.id.ph_set_TimeFixed_period_value);
		mTimedPeriod.setText(String.valueOf(SystemConfigFile.mTimedPeriod));
		mTimedPeriod.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				SystemConfigFile.mTimedPeriod = getValueFromEditText(s);
			}
		});

		mTimedPulse = (EditText) getView().findViewById(R.id.ph_set_EncoderTriger_pulse_value);
		mTimedPulse.setText(String.valueOf(SystemConfigFile.mTrigerPulse));
		mTimedPulse.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				SystemConfigFile.mTrigerPulse = getValueFromEditText(s);
			}
		});

		mLenPulse = (EditText) getView().findViewById(R.id.ph_set_EncoderLenFixed_pulse_value);
		mLenPulse.setText(String.valueOf(SystemConfigFile.mLenFixedPulse));
		mLenPulse.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				SystemConfigFile.mLenFixedPulse = getValueFromEditText(s);
			}
		});

		mDelayPulse = (EditText) getView().findViewById(R.id.ph_set_EncoderDelay_pulse_value);
		mDelayPulse.setText(String.valueOf(SystemConfigFile.mDelayPulse));
		mDelayPulse.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				SystemConfigFile.mDelayPulse = getValueFromEditText(s);
			}
		});

		mHighLen = (EditText) getView().findViewById(R.id.ph_set_OutputHight_length_value);
		mHighLen.setText(String.valueOf(SystemConfigFile.mHighLen));
		mHighLen.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				SystemConfigFile.mHighLen = getValueFromEditText(s);
			}
		});
		
		mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE); 
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch (view.getId()) {
			case R.id.ph_set_encoder_value:
				SystemConfigFile.mEncoder = mEncoder.getSelectedItemPosition();
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
		} catch (Exception e) {
			
		}
		mHandler.removeMessages(PRINTER_SETTINGS_CHANGED);
		mHandler.sendEmptyMessageDelayed(PRINTER_SETTINGS_CHANGED, 10000);
		return iv;
	}
	
	public void reloadSettings() {
		SystemConfigFile.parseSystemCofig();
		mEncoder.setSelection(SystemConfigFile.mEncoder);
		mTrigermode.setText(String.valueOf(SystemConfigFile.mTrigerMode));
		mPHO_H.setText(String.valueOf(SystemConfigFile.mPHOHighDelay));
		mPHO_L.setText(String.valueOf(SystemConfigFile.mPHOLowDelay));
		mOutPeriod.setText(String.valueOf(SystemConfigFile.mPHOOutputPeriod));
		mTimedPeriod.setText(String.valueOf(SystemConfigFile.mTimedPeriod));
		mTimedPulse.setText(String.valueOf(SystemConfigFile.mTrigerPulse));
		mLenPulse.setText(String.valueOf(SystemConfigFile.mLenFixedPulse));
		mDelayPulse.setText(String.valueOf(SystemConfigFile.mDelayPulse));
		mHighLen.setText(String.valueOf(SystemConfigFile.mHighLen));
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
}

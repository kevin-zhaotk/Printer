package com.industry.printer;

import java.util.Collection;
import java.util.MissingResourceException;

import com.industry.printer.R.string;
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
		
		String[] items = getResources().getStringArray(R.array.encoder_item_entries); 
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, R.id.textView_id, items);
		mEncoder.setAdapter(adapter);
		
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
			Debug.d(TAG, "--->getValueFromEditText:" + iv);
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

	
	private class SelfTextWatcher implements TextWatcher {
		
		private EditText mEditText;
		
		public SelfTextWatcher(EditText e) {
			mEditText = e;
		}
		
		@Override
		public void afterTextChanged(Editable arg0) {
			if (mEditText == mResv11) {
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
}

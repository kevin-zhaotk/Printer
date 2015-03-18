package com.industry.printer;

import java.util.Collection;

import com.industry.printer.FileFormat.SystemConfigFile;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class PHSettingFragment extends Fragment {
	
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
		
		SystemConfigFile.parseSystemCofig();
		mEncoder = (Spinner) getView().findViewById(R.id.ph_set_encoder_value);
		mEncoder.setSelection(SystemConfigFile.mEncoder);
		
		mTrigermode = (EditText) getView().findViewById(R.id.ph_set_trigerMode_value);
		mTrigermode.setText(String.valueOf(SystemConfigFile.mTrigerMode));

		mPHO_H = (EditText) getView().findViewById(R.id.ph_set_PHODelayHigh_value);
		mPHO_H.setText(String.valueOf(SystemConfigFile.mPHOHighDelay));

		mPHO_L = (EditText) getView().findViewById(R.id.ph_set_PHODelayLow_value);
		mPHO_L.setText(String.valueOf(SystemConfigFile.mPHOLowDelay));

		mOutPeriod = (EditText) getView().findViewById(R.id.ph_set_PHOOutput_period_value);
		mOutPeriod.setText(String.valueOf(SystemConfigFile.mPHOOutputPeriod));

		mTimedPeriod = (EditText) getView().findViewById(R.id.ph_set_TimeFixed_period_value);
		mTimedPeriod.setText(String.valueOf(SystemConfigFile.mTimedPeriod));

		mTimedPulse = (EditText) getView().findViewById(R.id.ph_set_EncoderTriger_pulse_value);
		mTimedPulse.setText(String.valueOf(SystemConfigFile.mTrigerPulse));

		mLenPulse = (EditText) getView().findViewById(R.id.ph_set_EncoderLenFixed_pulse_value);
		mLenPulse.setText(String.valueOf(SystemConfigFile.mLenFixedPulse));

		mDelayPulse = (EditText) getView().findViewById(R.id.ph_set_EncoderDelay_pulse_value);
		mDelayPulse.setText(String.valueOf(SystemConfigFile.mDelayPulse));

		mHighLen = (EditText) getView().findViewById(R.id.ph_set_OutputHight_length_value);
		mHighLen.setText(String.valueOf(SystemConfigFile.mHighLen));
		
		mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE); 
	}
	
}

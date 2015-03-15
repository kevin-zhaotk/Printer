package com.industry.printer;

import java.util.Collection;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class PHSettingFragment extends Fragment {
	
	private static final String TAG= PHSettingFragment.class.getSimpleName();

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
}

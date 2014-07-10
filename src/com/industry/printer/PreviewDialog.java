package com.industry.printer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

public class PreviewDialog extends Dialog {

	public PreviewDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.preview_dialog);
		
	}
}

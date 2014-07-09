package com.industry.printer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class PreviewDialog extends Dialog {

	public PreviewDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.preview_dialog);
	}
}

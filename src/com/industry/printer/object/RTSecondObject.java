package com.industry.printer.object;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

public class RTSecondObject extends BaseObject {

	public RTSecondObject(Context context, float x) {
		super( context, BaseObject.OBJECT_TYPE_RT_SECOND, x);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getContent()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("ss");
		mContent = dateFormat.format(new Date());
		return mContent;
	}
}

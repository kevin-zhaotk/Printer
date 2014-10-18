package com.industry.printer.Utils;


import android.content.Context;
import com.industry.printer.R;

public class Configs {
	public static int gDots;
	public static int gFixedRows;
	
	public static void initConfigs(Context context)
	{
		gDots = context.getResources().getInteger(R.integer.dots_per_column);
		gFixedRows = context.getResources().getInteger(R.integer.fixed_rows);
	}
}

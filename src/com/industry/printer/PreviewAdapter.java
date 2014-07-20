package com.industry.printer;

import java.util.List;
import java.util.Map;

import com.industry.printer.Utils.Debug;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class PreviewAdapter extends SimpleAdapter {

	public static final String TAG="PreviewAdapter";
	public int mChecked;
	public int mlastChecked;
	public PreviewAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		mChecked=-1;
		mlastChecked = -1;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View view =null;
		if(convertView != null)
		{
			view = convertView;
		}else
		{
			view = super.getView(position, convertView, parent);
		}
		//int[] color = {Color.WHITE, Color.rgb(0x1c, 0x86, 0xee)};
		//view.setBackgroundColor(color[position%2]);
		//((TextView) view).setTextColor(Color.BLACK);
		Debug.d(TAG,"mChecked="+mChecked+", mlastChecked="+mlastChecked);
		Debug.d(TAG, "position="+position);
		
		if(mChecked == position)
		{
			view.setBackgroundColor(Color.BLUE);
		}
		else
		{
			view.setBackgroundColor(Color.WHITE);
		}
		return super.getView(position, convertView, parent);
	}
	
	public void setChecked(int position)
	{
		mlastChecked = mChecked;
		mChecked = position;
	}
}

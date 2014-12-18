package com.industry.printer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.industry.printer.FileFormat.DotMatrixFont;
import com.industry.printer.Utils.Debug;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class LogoSelectDialog extends Dialog implements OnItemSelectedListener, OnItemClickListener {

	private static final String TAG=LogoSelectDialog.class.getSimpleName();
	public Button mOk;
	public Button mCancel;
	public GridView mView;
	public LogoItemAdapter mAdapter;
	ArrayList<HashMap<String, Object>> mList;
	
	public static String mSelection=null;
	
	public LogoSelectDialog(Context context) {
		super(context);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo_select_dialog);
		setTitle(R.string.logo_dialog_title);
		mOk = (Button)findViewById(R.id.btn_ok);
		mOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSelection = (String)mList.get(mAdapter.getSelection()).get("itemText");
				dismiss();
			}
		});
		
		mCancel = (Button) findViewById(R.id.btn_cancel);
		mCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		mList= new ArrayList<HashMap<String,Object>>();
		mView = (GridView) findViewById(R.id.logo_select_gridview);
		
		mAdapter = new LogoItemAdapter(getContext(), mList);
		mView.setAdapter(mAdapter);
		mView.setOnItemSelectedListener(this);
		mView.setOnItemClickListener(this);
		loadLogo();
	}
	
	public void loadLogo()
	{
		Bitmap bmp;
		int[] bit;
		Paint p = new Paint();
		p.setARGB(255, 0, 0, 0);
		File Dir= new File(DotMatrixFont.LOGO_FILE_PATH);
		File[] files=Dir.listFiles();
		for(File f:files)
		{
			DotMatrixFont font = new DotMatrixFont(f.getAbsolutePath());
			bit = new int[128*8];
			font.getDotbuf(bit);
			bmp=PreviewScrollView.getPicBitmapFrombuffer(bit, p);
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("itemImage", bmp);
			map.put("itemText", f.getName());
			mList.add(map);
		}
		mAdapter.notifyDataSetChanged();
	}
	
	
	public class LogoItemAdapter extends BaseAdapter{

		public Context mContext;
		public int mSelected;
		private LayoutInflater mInflater;
		private List<? extends Map<String, ?>> mList;
		public LogoItemAdapter(Context context, List<? extends Map<String, ?>> data){
			mContext = context;
			mList = data;
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		
		public void setSelection(int position){
			mSelected = position;
			Debug.d(TAG, "======>setSelection: "+mSelected);
			notifyDataSetChanged();
		}
		public int getSelection(){
			Debug.d(TAG, "======>getSelection: "+mSelected);
			return mSelected;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder mHolder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.logo_item, null);
				mHolder = new ViewHolder();
				mHolder.mImageView = (ImageView) convertView.findViewById(R.id.logo_item_img);
				mHolder.mTextView = (TextView) convertView.findViewById(R.id.logo_item_text);
				convertView.setTag(mHolder);
			}
			else{
				mHolder = (ViewHolder) convertView.getTag();
			}
			Bitmap img = (Bitmap)mList.get(position).get("itemImage");
			String title = (String) mList.get(position).get("itemText"); 
			mHolder.mImageView.setImageBitmap(img);
			mHolder.mTextView.setText(title);
			if(mSelected == position){
				convertView.setBackgroundColor(Color.YELLOW);
			}
			else {
				convertView.setBackgroundColor(Color.WHITE);
			}
			return convertView;
		}
		
		
		private class ViewHolder{
			ImageView mImageView;
			TextView mTextView;
		}
	}


	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		Debug.d(TAG, "======onItemSelected  arg2="+arg2+", arg3="+arg3);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Debug.d(TAG, "======onItemSelected  arg2="+arg2+", arg3="+arg3);
		mAdapter.setSelection(arg2);
	}
}

package com.industry.printer;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import com.industry.printer.FileBrowserDialog.OnPositiveListener;
import com.industry.printer.FileFormat.DotMatrixFont;
import com.industry.printer.FileFormat.FilenameSuffixFilter;
import com.industry.printer.FileFormat.Tlk_Parser;
import com.industry.printer.Utils.Debug;
import com.industry.printer.object.BinCreater;
import com.industry.printer.object.TlkObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ManualCtrlActivity extends Activity {

	public final String TAG="ManualCtrlActivity";
	
	public Button mBtnTlkfile;
	public Button mBtnview;
	
	public LinkedList<Map<String, String>>	mMessageMap;
	public PreviewAdapter mMessageAdapter;
	public ListView mMessageList;
	
	public Context mContext;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setLocale();
		setContentView(R.layout.manualctrl_frame);
		
		mContext = this.getApplicationContext();
		
		mMessageMap = new LinkedList<Map<String, String>>();
		mMessageAdapter = new PreviewAdapter(mContext, 
											mMessageMap,
											R.layout.manuallistviewlayout,
											new String[]{"index","text1","text2","text3","text4","text5","text6"},
											new int[]{R.id.manual_index,R.id.manual_text1,R.id.manual_text2,R.id.manual_text3,R.id.manual_text4,
													R.id.manual_text5,R.id.manual_text6});
		
		mMessageList = (ListView) findViewById(R.id.manual_lv_messages);
		mMessageList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mMessageList.requestFocus();
		mMessageList.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Debug.d(TAG, "=======onItemSelected");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				Debug.d(TAG, "=====onNothingSelected");
			}
			
		});
		
		mMessageList.setOnItemClickListener(new ListView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Debug.d(TAG, "******message list clicked: "+position);
				mMessageAdapter.setChecked(position);
				mMessageAdapter.notifyDataSetChanged();
			}
			
		});

		mBtnTlkfile = (Button) findViewById(R.id.manual_btnTlkfile);
		mBtnTlkfile.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FileBrowserDialog dialog = new FileBrowserDialog(ManualCtrlActivity.this, DotMatrixFont.TLK_FILE_PATH, FilenameSuffixFilter.TLK_SUFFIX);
				dialog.setOnPositiveClickedListener(new OnPositiveListener(){

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						String f = FileBrowserDialog.file();
						if(f==null || !f.toLowerCase().endsWith(".tlk"))
						{
							Toast.makeText(mContext, "please select a csv file", Toast.LENGTH_LONG);
							return;
						}
							
						mBtnTlkfile.setText(new File(f).getName());
					}
					
				});
				dialog.show();
			}
			
		});
		
		mBtnview = (Button)findViewById(R.id.manual_btn_preview);
		mBtnview.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//DotMatrixFont dot = new DotMatrixFont("/mnt/usb/font.txt");
				int pos = mMessageList.getCheckedItemPosition();
				Map<String, String> m = (Map<String, String>)mMessageList.getItemAtPosition(pos);
				if(m!= null)
				{
					String index = m.get("index");
					Vector<TlkObject> list = new Vector<TlkObject>();
					//if(mMsgFile!=null)
					{
						//String path = new File(mMsgFile.getText().toString()).getParent();
						Tlk_Parser.parse(DotMatrixFont.TLK_FILE_PATH+mBtnTlkfile.getText().toString(), list);
						setContent(index, list);
						Debug.d(TAG, "list size="+list.size());
					}
					int len = calculateBufsize(list);
					Debug.d(TAG, "bin length="+len);
					//int[] buffer = new int[len+16];
					int bit[];
					Bitmap bmp=null;
					Bitmap gBmp = Bitmap.createBitmap(len, 64, Config.ARGB_8888);
					
					Canvas can = new Canvas(gBmp);
					Paint p = new Paint();
					p.setColor(Color.rgb(128, 128, 128));
					
					for(TlkObject o: list)
					{
						if(o.isTextObject())
						{
							DotMatrixFont font = new DotMatrixFont(DotMatrixFont.FONT_FILE_PATH+o.font+".txt");
							bit = new int[32*o.mContent.length()];
							font.getDotbuf(o.mContent, bit);
							bmp=PreviewScrollView.getTextBitmapFrombuffer(bit, p);
						}
						else if(o.isPicObject()) //each picture object take over 32*32/8=128bytes
						{
							Debug.d(TAG, "=========pic object");
							DotMatrixFont font = new DotMatrixFont(DotMatrixFont.LOGO_FILE_PATH+o.font+".txt");
							bit = new int[128*8];
							font.getDotbuf(bit);
							bmp=PreviewScrollView.getPicBitmapFrombuffer(bit, p);
						}
						can.drawBitmap(bmp, o.x, o.y, p);
					}
					BinCreater.saveBitmap(gBmp, "pre.bmp");
					//set contents of text object
					//BinCreater.create(BitmapFactory.decodeFile("/mnt/usb/11.jpg"), "/mnt/usb/1.bin", 0);
					BinCreater.create(gBmp, "/mnt/usb/1.bin", 0);
					PreviewDialog prv = new PreviewDialog(ManualCtrlActivity.this);
					
					prv.show(list);
				}
				
			}
			
		});
		
		initMsglist();
	}
	
	
	public void initMsglist()
	{
		for(int i=1;i<=4;i++)
		{
			Map<String,String> map = new HashMap<String,String>();
			map.put("index", String.valueOf(i));
			map.put("text1", "");
			map.put("text2", "");
			map.put("text3", "");
			map.put("text4", "");
			map.put("text5", "");
			mMessageMap.add(map);
		}
		mMessageList.setAdapter(mMessageAdapter);
	}
	
	public void setContent(String index,Vector<TlkObject> list)
	{
		for(TlkObject o:list)
		{
			for(int i=1;i<mMessageList.getCount(); i++)
			{
				Map<String, String> m = (Map<String, String>)mMessageList.getItemAtPosition(i);
				Debug.d(TAG, "*******index="+m.get("index"));
				if(m.get("index").equals(index))
				{
					Debug.d(TAG, "index "+o.index+" found");
					o.setContent(m.get("text"+o.index));
					break;
				}
			}
		}
		
	}
	
	public int calculateBufsize(Vector<TlkObject> list)
	{
		int length=0;
		for(int i=0; i<list.size(); i++)
		{
			TlkObject o = list.get(i);
			if(o.isTextObject())	//each text object take over 16*16/8 * length=32Bytes*length
			{
				length = (16*o.mContent.length()+o.x) > length?(16*o.mContent.length()+o.x):length;
			}
			else if(o.isPicObject()) //each picture object take over 32*32/8=128bytes
			{
				length = (o.x+128) > length?(o.x+128):length;
			}
		}
		return length;
	}

	public void setLocale()
	{
		Configuration config = getResources().getConfiguration(); 
		DisplayMetrics dm = getResources().getDisplayMetrics(); 
		config.locale = Locale.SIMPLIFIED_CHINESE; 
		getResources().updateConfiguration(config, dm); 
		
	}
}

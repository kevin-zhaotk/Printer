package com.industry.printer;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.industry.printer.Utils.Debug;
import com.industry.printer.object.BinCreater;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FileBrowserDialog extends Dialog {
	public static final String TAG="FileBrowserDialog";
	
	public SimpleAdapter mFileAdapter;
	public LinkedList<Map<String, Object>> mContent;
	
	public ListView mFileList;
	public EditText mPath;
	public static String mCurPath;
	public static String objDir;
	public Button mSave;
	public Button mCancel;
	public EditText mName;
	
	public View mVSelected=null;
	
	OnPositiveListener pListener;
	OnNagitiveListener nListener;
	
	public FileBrowserDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		mContent = new LinkedList<Map<String, Object>>();
		mFileAdapter = new SimpleAdapter(context, 
				mContent, 
				R.layout.file_browser, 
				new String[]{"icon", "name"}, 
				new int []{R.id.file_icon, R.id.file_name});
		//mCurPath="/storage/external_storage/sda1";
		mCurPath =BinCreater.FILE_PATH;
	}

	public FileBrowserDialog(Context context, String path)
	{
		this(context);
		mCurPath = path;
	}
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	        // TODO Auto-generated method stub
	        super.onCreate(savedInstanceState);
	        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        this.setContentView(R.layout.file_browser_dialog);
	        
	        mSave = (Button) findViewById(R.id.dialog_save);
	        mSave.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Debug.d(TAG,"*****TODO save clicked");
					if(mName.getText() == null)
						return;
					Debug.d(TAG, "*****file save as "+mName.getText().toString());
					objDir = mName.getText().toString();
					//cancel();
					dismiss();
					if(pListener != null)
						pListener.onClick();
				}
	        	
	        });
	        
	        mCancel = (Button) findViewById(R.id.dialog_cancel);
	        mCancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//Debug.d(TAG,"*****TODO cancel clicked");
					cancel();
					if(nListener != null)
						nListener.onClick();
				}
			});
	        
	        mName = (EditText) findViewById(R.id.name_input);
	        
	        mPath = (EditText) findViewById(R.id.file_dialog_path);
	        mFileList =(ListView) findViewById(R.id.file_list);
	        fileOpen(new File(mCurPath));
	        
	        mFileList.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					/*modify the background color when item clicked*/
					if(mVSelected == null)
					{
						view.setBackgroundColor(Color.BLUE);
						mVSelected = view;
					}
					else
					{
						mVSelected.setBackgroundColor(Color.WHITE);
						view.setBackgroundColor(Color.BLUE);
						mVSelected = view;
					}
					Debug.d(TAG,"path="+(String)mContent.get(position).get("path")+", name="+(String)mContent.get(position).get("name"));
					mCurPath = (String)mContent.get(position).get("path");
					File item = new File(mCurPath);
					if(item.isDirectory())
					{
						Debug.d(TAG, "item clicked: "+mCurPath);
						fileOpen(item);
					}
				}
	        	
	        });
	 }
	 
	 public void fileOpen(File file)
	 {
		 mPath.setText(mCurPath);
		 mContent.clear();
		 Debug.d(TAG, ""+file.getPath()+", exists = "+file.exists());
		 File [] files = file.listFiles();
		 if(files == null)
		 {
			 Debug.d(TAG, "Please plugin a USB device ");
			 Toast.makeText(getContext(), "Please plugin a USB device ", Toast.LENGTH_LONG);
			return;
		 }
		 Debug.d(TAG, "files ="+files);
		 Map<String, Object> m = new HashMap<String, Object>();
		 m.put("icon", R.drawable.icon_directory);
		 m.put("name", "..");
		 m.put("path", file.getParent());
		 mContent.add(m);
		 for(int i=0; files != null && i< files.length; i++)
		 {
			 Debug.d(TAG,"file name="+files[i].toString());
			 Map<String, Object> map = new HashMap<String, Object>();
			 if(files[i].isDirectory())
			 {				 
				 map.put("icon", R.drawable.icon_directory);
			 }
			 if(files[i].isFile())
			 {
				 map.put("icon", R.drawable.icon_file);
			 }
			 map.put("name", files[i].getName());
			 map.put("path", files[i].getPath());
			 mContent.add(map);
		 }
		 mFileList.setAdapter(mFileAdapter);
	 }
	 
	 static public String file()
	 {
		 Debug.d(TAG, "file ="+mCurPath);
		 return mCurPath;
	 }
	 
	 static public String getObjName()
	 {
		 Debug.d(TAG, "file ="+objDir);
		 return objDir;
	 }
	 
	 
	 public void setOnPositiveClickedListener(OnPositiveListener listener)
	 {
		 pListener = listener;
	 }
	 
	 public void setOnNagitiveClickedListener(OnNagitiveListener listener)
	 {
		 nListener = listener;
	 }
	 /**
	  *Interface definition when positive button clicked 
	  **/
	 public interface OnPositiveListener{
		 void onClick();
	 }

	 /**
	  *Interface definition when nagitive button clicked 
	  **/
	 public interface OnNagitiveListener{
		 void onClick();
	 }
}

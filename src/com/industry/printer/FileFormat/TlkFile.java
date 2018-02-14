package com.industry.printer.FileFormat;

import android.content.Context;

import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.Utils.PlatformInfo;
import java.util.ArrayList;

public class TlkFile {
	
	private static final String TAG = TlkFile.class.getSimpleName();
	
	public Context mContext;
	public String mPath;
	public String mFile;
	private String mDirectory;
	
	public TlkFile(Context context, String file) {
		mContext = context;
		mFile = file;
		mDirectory = ConfigPath.getTlkDir(file);
		setTlk(ConfigPath.getTlkAbsolute(file));
	}
	 //addbylk 手机使用修改u盘路经    2/2 
	/**
	 * 设置需要解析的tlk文件名，可以是绝对路径或相对路径
	 */
	  // 设备 
	
	/**
	 * 设置需要解析的tlk文件名，可以是绝对路径或相对路径
	 */
	public void setTlk(String file) {
		// String path = ConfigPath.getTlkPath();
		if (file == null || file.isEmpty())
			return;
		if (file.startsWith("/")) {
			mPath = file;
		} else {
			mPath = ConfigPath.getTlkPath() + file;
		}
		Debug.d(TAG, "--->setTlk: " + mPath);
	}
	
 /*
	 public void setTlk(String file) {
			String path = PlatformInfo.getMntPath();
			if (file == null || file.isEmpty())
				return;
			if (path != null && file.startsWith(path)) {
				mPath = file;
			} else {
				mPath = ConfigPath.getTlkPath() + file;
			}
		}
	*/
	
 // 手机 
	
/*
	public void setTlk(String file) {
		ArrayList<String> path = ConfigPath.getMountedUsb();
		if (file == null || file.isEmpty())
			return;
		if (path != null && file.startsWith(path.get(0))) {
			mPath = file;
		} else {
			mPath = ConfigPath.getTlkPath() + file;
		}
	}	 
*/
	
	public String getDirectory() {
		return mDirectory;
	}
}

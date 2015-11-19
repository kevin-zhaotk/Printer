package com.industry.printer.FileFormat;

import android.content.Context;

import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;

public class TlkFile {
	
	private static final String TAG = TlkFile.class.getSimpleName();
	
	public Context mContext;
	public String mPath;
	public String mFile;
	
	public TlkFile(Context context, String file) {
		mContext = context;
		mFile = file;
		setTlk(ConfigPath.getTlkAbsolute(file));
	}
	/**
	 * 设置需要解析的tlk文件名，可以是绝对路径或相对路径
	 */
	public void setTlk(String file) {
		if (file == null || file.isEmpty())
			return;
		if (file.startsWith(Configs.USB_ROOT_PATH) || file.startsWith(Configs.USB_ROOT_PATH2)) {
			mPath = file;
		} else {
			mPath = ConfigPath.getTlkPath() + file;
		}
	}
	
	
}

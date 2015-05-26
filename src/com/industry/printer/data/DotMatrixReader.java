package com.industry.printer.data;

import java.io.File;
import java.util.ArrayList;

import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;

public class DotMatrixReader {

	private static final String TAG = DotMatrixReader.class.getSimpleName(); 
	public static DotMatrixReader mInstance;
	
	private File mDotFile;
	
	public static DotMatrixReader getInstance() {
		if (mInstance == null) {
			mInstance = new DotMatrixReader();
		}
		return mInstance;
	}

	public DotMatrixReader() {
		
		ArrayList<String> paths = ConfigPath.getMountedUsb();
		if (paths == null || paths.size() == 0) {
			Debug.e(TAG, "--->no USB storage found");
			return;
		}
		//use the first usb storage as default
		String path = paths.get(0);
		
		String dotFile = path + Configs.SYSTEM_CONFIG_DIR + "/HZK16";
		mDotFile = new File(dotFile);
	}
	
	public byte[] getDotMatrix(char[] inCodes) {
		Debug.e(TAG, "--->Todo: read dot matrix file");
		int quCode=0, weiCode=0; 
		for (int i = 0; i < inCodes.length; i++) {
			quCode = (inCodes[i]>>8)&0x00ff;
			weiCode = inCodes[i] & 0x00ff;
			Debug.d(TAG, "--->qucode: "+String.valueOf(quCode)+", weiCode:"+String.valueOf(weiCode));
		}
		return null;
	}
}

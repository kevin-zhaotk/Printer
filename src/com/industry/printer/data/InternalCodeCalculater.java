package com.industry.printer.data;

import com.industry.printer.Utils.Debug;

public class InternalCodeCalculater {
	
	public static InternalCodeCalculater mInstance;
	
	public static InternalCodeCalculater getInstance() {
		if (mInstance == null) {
			mInstance = new InternalCodeCalculater();
		}
		return mInstance;
	}
	
	public void getCharCode(String value) {
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			int j = c;
			Integer ii = new Integer(j);
			Debug.d("", "--->"+c+" internal code is: 0x"+Integer.toHexString(j));
		}
		
	}
}

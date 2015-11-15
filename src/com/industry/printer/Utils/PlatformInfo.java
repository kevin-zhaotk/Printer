package com.industry.printer.Utils;

import android.os.SystemProperties;

public class PlatformInfo {

	private static final String TAG = PlatformInfo.class.getSimpleName();
	
	private static final String PROPERTY_PRODUCT = "ro.build.product";
	
	private static final String PRODUCT_SMFY_SUPER3 = "smfy-super3";
	
	
	/**
	 * 判断buffer获取方式，通过BMP图片提取或者点阵字库提取
	 * 目前支持点阵字库提取的设备为：树莓3
	 * 其他设备都是通过BMP提取
	 * @return
	 */
	public static boolean isBufferFromDotMatrix() {
		
		String product = SystemProperties.get(PROPERTY_PRODUCT);
		//Debug.d(TAG, "--->product=" + product);
		if (PRODUCT_SMFY_SUPER3.equalsIgnoreCase(product)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String getProduct() {
		return SystemProperties.get(PROPERTY_PRODUCT);
	}
}

package com.industry.printer.Utils;

import java.lang.reflect.Method;


//import android.os.SystemProperties;
/**
 * 系统属性调用SystemProperties是隐藏的{hide}无法直接调用
 * 因此通过类反射机制调用
 * @author zhaotongkai
 *
 */

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
		// String product = SystemProperties.get(PROPERTY_PRODUCT);
		String product = null;
		
		try {
			Class<?> mClassType = Class.forName("android.os.SystemProperties");
			Method mGetMethod = mClassType.getDeclaredMethod("get", String.class);
			product = (String) mGetMethod.invoke(mClassType, PROPERTY_PRODUCT);
		} catch (Exception e) {
		}
		Debug.d(TAG, "--->product=" + product);
		if (PRODUCT_SMFY_SUPER3.equalsIgnoreCase(product)) {
			return true;
		}
		return false;
	}
	
	public static String getProduct() {
		// return SystemProperties.get(PROPERTY_PRODUCT);
		String product = null;
		
		try {
			Class<?> mClassType = Class.forName("android.os.SystemProperties");
			Method mGetMethod = mClassType.getDeclaredMethod("get", String.class);
			product = (String) mGetMethod.invoke(mClassType, PROPERTY_PRODUCT);
		} catch (Exception e) {
		}
		return product;
	}
}

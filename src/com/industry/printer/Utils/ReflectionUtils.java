package com.industry.printer.Utils;

import java.lang.reflect.Method;

public class ReflectionUtils {

	/**
	 * 通过反射调用平台相关的接口，防止跨平台调用导致的编译问题
	 * @param cls
	 * @param method
	 */
	public static void invokeMethod(Class<?> cls, String method, Object...params) {
		Method mt;
		try {
			mt = cls.getMethod(method);
			if (mt != null) {
				mt.invoke(cls, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

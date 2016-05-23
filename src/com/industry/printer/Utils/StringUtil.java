package com.industry.printer.Utils;

public class StringUtil {

	public static boolean isEmpty(String str) {
		if (str == null || str.isEmpty() || str.length() == 0) {
			return true;
		}
		return false;
	}
}

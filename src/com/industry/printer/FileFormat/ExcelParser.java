package com.industry.printer.FileFormat;

import com.industry.printer.Utils.Debug;

public class ExcelParser {
	public static final String TAG="ExcelParser";
	
	public static String getFontfromSteelKind(String kind)
	{
		//ABS LOGO 
		if(kind!=null && kind.startsWith("AB/"))
			return "0001.txt"; 
		else if(kind!=null && kind.startsWith("BV-"))
			return "0002.txt";
		else if(kind!=null && kind.startsWith("CCS-"))
			return "0003.txt";
		else if(kind!=null && kind.startsWith("LR-"))
			return "0004.txt";
		else if(kind!=null && kind.startsWith("KR-"))
			return "0005.txt";
		else if(kind!=null && kind.startsWith("RINA-"))
			return "0006.txt";
		else if(kind!=null && kind.startsWith("GL-"))
			return "0007.txt";
		else if("KA".equals(kind) || "KB".equals(kind) || 
				"KD".equals(kind) || "KE".equals(kind) || 
				"KA32".equals(kind) || "KD32".equals(kind) || 
				"KE32".equals(kind) || "KA36".equals(kind) || 
				"KD36".equals(kind) || "KE36".equals(kind) )
			return "0008.txt";
		else if(kind!=null && kind.startsWith("NV"))
			return "0009.txt";
		else if(kind!=null && kind.startsWith("IRS-"))
			return "0010.txt";
		else 
		{
			Debug.d(TAG, "Unkonwn steel type "+kind);
			return null;
		}
	}
}

package com.industry.printer.FileFormat;

import com.industry.printer.Utils.Debug;

public class ExcelParser {
	public static final String TAG="ExcelParser";
	
	public static String getFontfromSteelKind(String kind)
	{
		if("AB/A".equals(kind) || "AB/B".equals(kind) ||
				"AB/DN".equals(kind) ||  "AB/E".equals(kind)||
				"AB/AH32".equals(kind) || "AB/DH32N".equals(kind) ||
				"AB/EH32".equals(kind) || "AB/AH36".equals(kind) ||
				"AB/DH36N".equals(kind) || "AB/EH36".equals(kind))
			return "0001.txt"; 
		else if("BV-A".equals(kind) || "BV-B".equals(kind) ||
				"BV-D".equals(kind) || "BV-E".equals(kind) ||
				"BV-AH32".equals(kind) || "BV-EH32".equals(kind) ||
				"BV-EH32".equals(kind) || "BV-AH36".equals(kind) ||
				"BV-DH36".equals(kind) || "BV-EH36".equals(kind))
			return "0002.txt";
		else if("CCS-A".equals(kind) || "CCS-B".equals(kind) ||
				"CCS-D".equals(kind) || "CCS-E".equals(kind) ||
				"CCS-AH32".equals(kind) || "CCS-DH32".equals(kind) ||
				"CCS-EH32".equals(kind) || "CCS-AH36".equals(kind) || 
				"CCS-DH36".equals(kind) || "CCS-EH36".equals(kind))
			return "0003.txt";
		else if("LR-A".equals(kind) || "LR-B".equals(kind) ||
				"LR-D".equals(kind) || "LR-E".equals(kind) || 
				"LR-AH32".equals(kind) || "LR-DH32".equals(kind) ||
				"LR-EH32".equals(kind) || "LR-AH36".equals(kind) ||
				"LR-DH36".equals(kind) || "LR-EH36".equals(kind))
			return "0004.txt";
		else if("KR-A".equals(kind) || "KR-B".equals(kind) ||
				"KR-D".equals(kind) || "KR-E".equals(kind) ||
				"KR-AH32".equals(kind) || "KR-DH32".equals(kind) ||
				"KR-EH32".equals(kind) || "KR-AH36".equals(kind) ||
				"KR-DH36".equals(kind) || "KR-EH36".equals(kind) )
			return "0005.txt";
		else if("RINA-A".equals(kind) || "RINA-B".equals(kind) ||
				"RINA-D".equals(kind) || "RINA-E".equals(kind) || 
				"RINA-AH32".equals(kind) || "RINA-DH32".equals(kind) ||
				"RINA- EH32".equals(kind) || "RINA-AH36".equals(kind) ||
				"RINA-DH36".equals(kind) || "RINA-EH36".equals(kind))
			return "0006.txt";
		else if("GL-A".equals(kind) || "GL-B".equals(kind) || 
				"GL-D".equals(kind) || "GL-E".equals(kind) || 
				"GL-A32".equals(kind) || "GL-D32".equals(kind) || 
				"GL-E32".equals(kind) || "GL-A36".equals(kind) || 
				"GL-D36".equals(kind) || "GL-E36".equals(kind))
			return "0007.txt";
		else if("KA".equals(kind) || "KB".equals(kind) || 
				"KD".equals(kind) || "KE".equals(kind) || 
				"KA32".equals(kind) || "KD32".equals(kind) || 
				"KE32".equals(kind) || "KA36".equals(kind) || 
				"KD36".equals(kind) || "KE36".equals(kind) )
			return "0008.txt";
		else if("NVA".equals(kind) || "NVB".equals(kind) || 
				"NVD".equals(kind) || "NVE".equals(kind) || 
				"NVA32".equals(kind) || "NVD32".equals(kind) || 
				"NVE32".equals(kind) || "NVA36".equals(kind) || 
				"NVD36".equals(kind) || "NVE 36".equals(kind) )
			return "0009.txt";
		else if("IR-A".equals(kind) || "IR-B".equals(kind) || 
				"IR-D".equals(kind) || "IR-E".equals(kind) || 
				"IR-AH32".equals(kind) || "IR-DH32".equals(kind) || 
				"IR-EH32".equals(kind) || "IR-AH36".equals(kind) || 
				"IR-DH36".equals(kind) || "IR-EH36".equals(kind))
			return "0010.txt";
		else 
		{
			Debug.d(TAG, "Unkonwn steel type "+kind);
			return null;
		}
	}
}

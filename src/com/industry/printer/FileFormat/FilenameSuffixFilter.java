package com.industry.printer.FileFormat;

import java.io.File;
import java.io.FilenameFilter;

public class FilenameSuffixFilter implements FilenameFilter {

	public static final String TLK_SUFFIX=".tlk";
	public static final String CSV_SUFFIX=".csv";
	
	public String mSuffix;
	
	public FilenameSuffixFilter(String suffix)
	{
		mSuffix = suffix;
	}
	
	@Override
	public boolean accept(File dir, String filename) {
		// TODO Auto-generated method stub
		if(mSuffix==null || filename==null || !filename.toLowerCase().endsWith(mSuffix))
			return false;
		else 
			return true;
	}

}

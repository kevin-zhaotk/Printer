package com.industry.printer.FileFormat;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.industry.printer.Utils.Debug;


/*
 *dot matrix file format:
 *1st line: dot-matrix size width*height
 *
 */
public class DotMatrixFont {

	public final String TAG="DotMatrixFont";
	
	public int mWidth;
	public int mHeight;
	BufferedReader mReader;
	
	public DotMatrixFont(String file)
	{
		File f = new File(file);
		
		try {
			mReader = new BufferedReader(new FileReader(f));
			String s = mReader.readLine();
			String re = "\\\\[0-9]*x[0-9]*";
			Pattern p = Pattern.compile(re);
			Matcher m = p.matcher(s);
			while(m.find())
				Debug.d(TAG, m.group(1));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}

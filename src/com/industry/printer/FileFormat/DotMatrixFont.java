package com.industry.printer.FileFormat;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	File mfile;
	
	public DotMatrixFont(String file)
	{
		mfile = new File(file);
		
		try {
			mReader = new BufferedReader(new FileReader(mfile));
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
	
	public void getDotbuf(String str, int[] buf)
	{
		int ascii;
		String s;
		try {
			
			for(int i=0; i< str.length(); i++)
			{
				ascii = (int)str.charAt(i);
				mReader.reset();
				
				while((s=mReader.readLine())!=null)
				{
					if(s.startsWith("/*") && s.contains(String.valueOf(ascii))))
					break;
				}
				/*read P1 head*/
				s=mReader.readLine();
				if(s==null || !s.startsWith("P1"))
					break;
				/*read P1 content*/
				s=mReader.readLine();
				if(s==null)
					break;
				String[] dot = s.split("  ");
				for(int k=0;k<8 && k<dot.length; k++)
				{
					buf[i*32] = Integer.parseInt(dot[k]);
				}
				
				/*read P2 head*/
				s=mReader.readLine();
				if(s==null || !s.startsWith("P2"))
					break;
				/*read P2 content*/
				s=mReader.readLine();
				if(s==null)
					break;
				dot = s.split("  ");
				for(int k=0;k<8 && k<dot.length; k++)
				{
					buf[i*32+8] = Integer.parseInt(dot[k]);
				}
				
				/*read P3 head*/
				s=mReader.readLine();
				if(s==null || !s.startsWith("P3"))
					break;
				/*read P3 content*/
				s=mReader.readLine();
				if(s==null)
					break;
				dot = s.split("  ");
				for(int k=0;k<8 && k<dot.length; k++)
				{
					buf[i*32+16] = Integer.parseInt(dot[k]);
				}
				
				/*read P4 head*/
				s=mReader.readLine();
				if(s==null || !s.startsWith("P4"))
					break;
				/*read P4 content*/
				s=mReader.readLine();
				if(s==null)
					break;
				dot = s.split("  ");
				for(int k=0;k<8 && k<dot.length; k++)
				{
					buf[i*32+24] = Integer.parseInt(dot[k]);
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

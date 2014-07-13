package com.industry.printer.FileFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import com.industry.printer.Utils.Debug;
import com.industry.printer.object.TlkObject;

public class Tlk_Parser {

	public static final String TAG="Tlk_Parser";
	
	public static void parse(String f, Vector<TlkObject> list)
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(f)));
			String s;
			while((s = reader.readLine()) != null)
			{
				
				String[] line = s.split("\\^");
				int index = Integer.parseInt(line[1]);
				if((index >= 1 && index <= 16)||(index >=21 && index <=23))	
				{
					TlkObject obj = new TlkObject();
					obj.setIndex(index);
					obj.setX(Integer.parseInt(line[2]));
					obj.setY(Integer.parseInt(line[3]));
					obj.setFont(line[19]);
					Debug.d(TAG,"index="+index+", x="+obj.x+", y="+obj.y+", font="+obj.font);
					
					list.add(obj);
				}
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void parseLine(String line)
	{
		
	}
}

package com.industry.printer.object;

import java.util.ArrayList;
import java.util.List;

import com.industry.printer.Utils.Debug;

import android.R.integer;
import android.content.Context;
import android.util.Log;


/**
 * 解析编辑的字符串，得到打印对象列表
 * @author kevin
 * 用户输入文本字符串规则如下：
 * 文本对象：一串简单字符
 * 计数器对象： 以单引号包围，以N为主键 Nx表示（x表示计数器的位数，比如 ‘N5‘表示一个5位计数器）
 * 图形对象：	以单引号包围，以G为主键
 * 注：所有以分号（；）作为分隔符
 */

public class ObjectsFromString {
	
	public static final String TAG = ObjectsFromString.class.getSimpleName();
	
	//各对象之间以；作为分隔符
	public static final String SPLITOR = ";";
	
	public static List<BaseObject> makeObjs(Context context, String str) {
		
		if (str==null || str.isEmpty()) {
			return null;
		}
		List<BaseObject> objList=new ArrayList<BaseObject>();
		Debug.d(TAG, "===>str: "+str);
		String[] objStrings = str.split(SPLITOR);
		for (String s:objStrings) {
			if (s.startsWith("'N")) { //计数器对象
				if(s.length() != 4 || !s.substring(3, 4).equals("'")) {
					Debug.d(TAG, "makeObjs format not counter, parse as text object");
					TextObject obj = new TextObject(context, 0);
					obj.setContent(s);
					objList.add(obj);
				} else {
					Log.d(TAG, "===>counter: "+s);
					int count = Integer.parseInt(s.substring(2, 3));
					CounterObject obj = new CounterObject(context, 0);
					obj.setBits(count);
					objList.add(obj);
				}
			} else if (s.startsWith("'P")) {	//图形对象
				Debug.d(TAG, "makeObjs image object");
			} else {	//文本对象
				if (s==null || s.isEmpty()) {
					continue;
				}
				Log.d(TAG, "===>text: "+s);
				TextObject obj = new TextObject(context, 0);
				obj.setContent(s);
				objList.add(obj);
			}
		}
		return objList;
	}
}

package com.industry.printer.FileFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


import com.industry.printer.Utils.Debug;

public class XmlInputStream {
	
	private static final String TAG = XmlInputStream.class.getSimpleName();
	
	private FileInputStream mInputStream;
	
	public XmlInputStream(String file) {
		try {
			mInputStream = new FileInputStream(new File(file));
		} catch (FileNotFoundException e) {
			Debug.e(TAG, "file not found:"+file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public XmlInputStream(FileInputStream inputStream) {
		
	}
	
	public List<XmlTag> read() {
		ArrayList<XmlTag> mPairs=null;
		try {
			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput(mInputStream, "utf-8");
			int event = parser.getEventType();
			
			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					mPairs = new ArrayList<XmlTag>();
					break;
				case XmlPullParser.START_TAG:
					String key = parser.getName();
					String value = parser.nextText();
					XmlTag tag = new XmlTag(key, value);
					break;
				case XmlPullParser.END_TAG:
				case XmlPullParser.END_DOCUMENT:
				default:
					break;
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return mPairs;
	}
}

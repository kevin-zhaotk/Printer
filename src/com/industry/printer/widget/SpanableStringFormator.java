package com.industry.printer.widget;

import java.util.ArrayList;
import java.util.List;

import com.industry.printer.object.BaseObject;
import com.industry.printer.object.CounterObject;
import com.industry.printer.object.ObjectsFromString;
import com.industry.printer.object.RealtimeObject;
import com.industry.printer.object.TextObject;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.widget.TextView;

public class SpanableStringFormator {

	private static final String TAG = SpanableStringFormator.class.getSimpleName();
	
	private static SpanableStringFormator mInstanceFormator=null;
	
	private SpannableStringBuilder mBuilder;
	
	public static SpanableStringFormator getInstance() {
		if (mInstanceFormator == null) {
			mInstanceFormator = new SpanableStringFormator();
		}
		return mInstanceFormator;
	}
	
	public SpanableStringFormator() {
		mBuilder = new SpannableStringBuilder();
	}
	
	public void setText(String text) {
		mBuilder.clear();
		mBuilder.clearSpans();
		List<ContentType> list = parseElements(text);
		for (ContentType contentType : list) {
			mBuilder.append(contentType.text);
			if (contentType.isVar) {
				mBuilder.setSpan(new BackgroundColorSpan(Color.YELLOW), contentType.start, contentType.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		
	}
	
	public void setText(List<BaseObject> objlist) {
		
		if (objlist == null) {
			return;
		}
		mBuilder.clear();
		mBuilder.clearSpans();
		List<ContentType> list = parseElements(objlist);
		
		for (ContentType type : list) {
			mBuilder.append(type.text);
			if (type.isVar) {
				mBuilder.setSpan(new BackgroundColorSpan(Color.YELLOW), type.start, type.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}
	
	public void append(String text) {
		mBuilder.append(text);
	}
	
	private List<ContentType> parseElements(String text) {
		int start=0;
		List<ContentType> mElements = new ArrayList<ContentType>();
		ContentType contentType = null;
		String[] elements = text.split(ObjectsFromString.SPLITOR);
		if (elements == null) {
			return mElements;
		}
		for (String element: elements) {
			//实时时钟
			if (element.startsWith(ObjectsFromString.REALTIME_FLAG) || element.startsWith(ObjectsFromString.COUNTER_FLAG)) {
				contentType = new ContentType(start, start + element.length(), element, true);
			} else {
				contentType = new ContentType(start, start + element.length(), element, false);
			}
			start += element.length() + ObjectsFromString.SPLITOR.length();
			mElements.add(contentType);
		}
		return mElements;
		
	}
	
	private List<ContentType> parseElements(List<BaseObject> objlist) {
		int start = 0;
		ContentType contentType;
		List<ContentType> mElements = new ArrayList<ContentType>();
		if (objlist == null) {
			return mElements;
		}
		
		for (BaseObject object : objlist) {
			if (object instanceof RealtimeObject || object instanceof CounterObject) {
				contentType = new ContentType(start, start + object.getContent().length(), object.getContent(), true);
			}
			if (object instanceof TextObject) {
				contentType = new ContentType(start, start + object.getContent().length(), object.getContent(), false);
			}
		}
		return mElements;
	}
	
	public SpanableStringFormator getSpannableString() {
		return this;
	}
			
	public class ContentType {
		public boolean isVar=false;
		public int start = 0;
		public int end = 0;
		public String text="";
		
		public ContentType() {
			
		}
		
		public ContentType(int start, int end, String text, boolean var) {
			this.start = start;
			this.end = end;
			this.text = text;
			this.isVar = var;
		}
	}
}

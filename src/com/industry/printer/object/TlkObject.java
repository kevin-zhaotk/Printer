package com.industry.printer.object;

import java.util.Date;

import com.industry.printer.FileFormat.DotMatrixFont;
import com.industry.printer.FileFormat.ExcelParser;
import com.industry.printer.Utils.Debug;

public class TlkObject {
	public static final String TAG="TlkObject";
	
	public int index;
	public int x;
	public int y;
	public String mSteelStyle;
	public String mLogo;
	public String mStandard;
	public String mNo;
	public String mDate;
	public String mWidth;
	public String mHeight;
	public String mThick;
	public String mSize;
	public String font;
	public String mContent;
	
	public TlkObject()
	{
		index = 0;
		x = 0;
		y = 0;
		font = null;
		Date d= new Date();
		mDate = String.valueOf(d.getYear()+1900)+
				String.valueOf(BaseObject.intToFormatString(d.getMonth()+1,2))+
				String.valueOf(BaseObject.intToFormatString(d.getDate(), 2));
	}
	
	public void setIndex(int i)
	{
		index = i;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public void setFont(String s)
	{
		font = s;
	}
	
	public void setContent(String s)
	{
		mContent = s;
	}
	
	public boolean isTextObject()
	{
		if(index>=1 && index<=16)
			return true;
		else
			return false;
	}
	
	public boolean isPicObject()
	{
		if(index>=21 && index<=24)
			return true;
		else
			return false;
	}
	
	public void setSize(String thick,String width, String height)
	{
		mThick = thick;
		mWidth = width;
		mHeight = height;
		mSize = mThick+"x"+mWidth+"x"+mHeight;
	}
	
	public void setStyle(String style)
	{
		mSteelStyle = style;
		mLogo = ExcelParser.getFontfromSteelKind(mSteelStyle);
	}
	public int getColumns()
	{
		int columns=0;
		//fisrt logo
		DotMatrixFont dot = new DotMatrixFont(DotMatrixFont.LOGO_FILE_PATH+"0011.txt");
		columns = dot.getColumns();
		if(mLogo!=null)
		{
			dot.setFont(DotMatrixFont.LOGO_FILE_PATH+mLogo);
			columns += dot.getColumns();
		}
		//first line text, Number - mNo
		int first=0;
		if(mNo != null)
		{
			dot.setFont(DotMatrixFont.FONT_FILE_PATH+"0001.txt");
			first = dot.getColumns()*mNo.length();
		}
		//2nd line
		int second =0;
		if(mSteelStyle != null)
		{
			second = dot.getColumns()*mSteelStyle.length();
		}
		second += 4;	//split
		if(mStandard != null)
		{
			second += dot.getColumns()*mStandard.length();
		}
		//3rd line
		int third=0;
		if(mSize != null)
		{
			third = dot.getColumns()*mSize.length();
		}
		third += 4;
		if(mSize != null)
		{
			third += dot.getColumns()*mDate.length();
		}
		int len = first;
		len = len > second? len: second;
		len = len > third? len: third;
		columns += 4 + len;
		Debug.d(TAG, "++++++columns="+columns);
		return columns;
	}
	
	public int getRows()
	{
		int rows=0;
		//fisrt logo
		DotMatrixFont dot = new DotMatrixFont(DotMatrixFont.LOGO_FILE_PATH+"0011.txt");
		rows = dot.getRows();
		Debug.d(TAG, "++++++rows1="+rows);
		if(mLogo!=null)
		{
			dot.setFont(DotMatrixFont.LOGO_FILE_PATH+mLogo);
			rows = rows > dot.getRows()?rows:dot.getRows();
		}
		//first line text, Number - mNo
		Debug.d(TAG, "++++++rows2="+rows);
		if(mNo != null)
		{
			dot.setFont(DotMatrixFont.FONT_FILE_PATH+"0001.txt");
			rows = rows > dot.getRows()*3?rows:dot.getRows()*3;
		}
		Debug.d(TAG, "++++++rows="+rows);
		return rows;
	}
}

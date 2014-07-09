package com.industry.printer.object;

public class TlkObject {
	
	public int index;
	public int x;
	public int y;
	public String font;
	
	public TlkObject()
	{
		index = 0;
		x = 0;
		y = 0;
		font = null;
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
}

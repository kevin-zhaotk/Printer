package com.industry.printer;

import java.io.ByteArrayInputStream;

import org.apache.http.util.ByteArrayBuffer;

import android.R.integer;
import android.content.SharedPreferences;
import android.util.Log;

import com.industry.printer.Usb.CRC16;
import com.industry.printer.Utils.Debug;

public class UsbSerial {
	
	public static final String TAG="UsbSerial"; 
	public static final int PACKAGE_MAX_LEN=132;
	/*
	 * jni native methods
	 */
	static public native int open(String dev);
	
	static public native int setBaudrate(int fd, int speed);
	
	static public native int close(int fd);
	
	static public native int write(int fd, short[] buf, int len);
	
	static public native byte[] read(int fd, int len);
	static public native int set_options(int fd, int databits, int stopbits, int parity);
	
	static public native String get_BuildDate();
	
	static public int printStart(String dev)
	{
		short buf[] = { 0x80, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04};
		if(dev == null)
		{
			Debug.d(TAG, "ttyACM0 device node not opened");
			return 0;
		}
		Debug.d(TAG,"====>printStart");
		short [] crcCmd = CRC16.crc(buf);
		for(int i=0; i<crcCmd.length; i++)
		{
			//Debug.d(TAG, "crcCmd["+i+"]="+(int) crcCmd[i]);
		}
		//String buf = "FUCK serial write";
		int fd = open(dev);
		if(fd<=0)
			return 0;
		int ret = UsbSerial.write(fd, crcCmd, buf.length);
		if(ret != buf.length)
		{
			close(fd);
			return 0;
		}
		Debug.d(TAG, "write ret="+ret);
		byte[] response = UsbSerial.read(fd, 10);
		if(response == null)
		{
			Debug.d(TAG, "read return null");
			close(fd);
			return 0;
		}
		for(int i=0; i<response.length; i++)
		{
			Debug.d(TAG, "response["+i+"]="+Integer.toHexString(response[i] & 0x0FF));
		}
		if(response[4] != 0)
			ret = 0;
		else 
			ret = 1;
		close(fd);
		Debug.d(TAG,"<====printStart");
		return ret;
	}
	
	
	static public int printStop(String dev)
	{
		short buf[]={0x80, 0x00, 0x01, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04};
		if(dev == null)
		{
			Debug.d(TAG, "ttyACM0 device node not opened");
			return 0;
		}
		Debug.d(TAG,"====>printStop");
		short[] crcCmd = CRC16.crc(buf);
		for(int i=0; i<crcCmd.length; i++)
		{
			//Debug.d(TAG, "crcCmd["+i+"]="+(int) crcCmd[i]);
		}
		//String buf = "FUCK serial write";
		int fd = open(dev);
		if(fd <= 0)
			return 0;
		int ret = UsbSerial.write(fd, crcCmd, buf.length);
		Debug.d(TAG, "write ret="+ret);
		byte[] response = UsbSerial.read(fd, 10);
		if(response == null)
		{
			Debug.d(TAG, "read return null");
			close(fd);
			return 0;
		}
		for(int i=0; i<response.length; i++)
		{
			Debug.d(TAG, "buf["+i+"]="+Integer.toHexString(response[i] & 0x0FF));
		}
		if(response[4] != 0)
		{
			ret = 0;
			Debug.d(TAG, "this is response error");
		}
		else
			ret = 1;
		Debug.d(TAG,"<====printStop");
		close(fd);
		return ret;
	}
	
	/*clean
	 *	80 00 02 04 00 00 00 00 00 00 04    
	 */
	static public int clean(String dev)
	{
		
		short buf[]={0x80, 0x00, 0x02, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04};
		if(dev == null)
		{
			Debug.d(TAG, "ttyACM0 device node not opened");
			return 0;
		}
		/*
		for(int i=0; i<buf.length; i++)
		{
			//Debug.d(TAG, "buf["+i+"]="+(int) buf[i]);
		}*/
		short[] crcCmd = CRC16.crc(buf);
		
		for(int i=0; i<crcCmd.length; i++)
		{
			//Debug.d(TAG, "crcCmd["+i+"]="+(int) crcCmd[i]);
		}
		//String buf = "FUCK serial write";
		int fd = open(dev);
		if(fd <= 0)
			return 0;
		int ret = UsbSerial.write(fd, crcCmd, buf.length);
		Debug.d(TAG, "clean return = "+ret);
		close(fd);
		return ret;
	}
	
	
	static public int setAllParam(String dev, short param[])
	{
		//short buf[] = new short[132];
		//buf[0] = 0x81;
		
		short cmd[]={0x80, 0x01, 0xFF, 0x04, 0x00, 0x00, 0x00, 0x80, 0x00, 0x00, 0x04};
		if(dev == null)
		{
			Debug.d(TAG, "ttyACM0 device node not opened");
			return 0;
		}
		Debug.d(TAG,"====>setAllParam");
		short [] crcCmd = CRC16.crc(cmd);
		int fd = open(dev);
		if(fd <= 0 )
			return 0;
		int ret = UsbSerial.write(fd, crcCmd, cmd.length);
		Debug.d(TAG, "write ret="+ret);

		byte[] response = UsbSerial.read(fd, PACKAGE_MAX_LEN);
		if(response == null)
		{
			Debug.d(TAG, "read return null");
			close(fd);
			return 0;
		}
		/*
		for(int i=0; i<response.length; i++)
		{
			Debug.d(TAG, "response["+i+"]="+Integer.toHexString(response[i] & 0x0FF));
		}
		*/
		Debug.d(TAG, "write ALL parameters");
		short buf[] = new short[132];
		buf[0] = 0x81;
		buf[131]=0x04;
		crcCmd = CRC16.crc(buf);
		/*
		for(int i=0; i<buf.length; i++)
		{
			Debug.d(TAG, "buf["+i+"]="+(int)(buf[i] & 0x0FF));
		}
		*/
		ret = UsbSerial.write(fd, crcCmd, buf.length);
		Debug.d(TAG, "write param ret="+ret);
		response = UsbSerial.read(fd, PACKAGE_MAX_LEN);
		if(response == null)
		{
			Debug.d(TAG, "read return null");
			close(fd);
			return 0;
		}
		for(int i=0; i<response.length; i++)
		{
			Debug.d(TAG, "response["+i+"]="+Integer.toHexString(response[i] & 0x0FF));
		}
		Debug.d(TAG,"<====setAllParam");
		close(fd);
		return ret;
	}
	
	/**
	 * 
	 * 0x80 	0x01		0xFE	0x04		0x00		0x00		0x00		0x00		CRC16L		CRC16H		0x04
	 **/
	static public int readAllParam(String dev)
	{
		//short buf[] = new short[132];
		//buf[0] = 0x81;
		
		short cmd[]={0x80, 0x01, 0xFE, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04};
		if(dev == null)
		{
			Debug.d(TAG, "ttyACM0 device node not opened");
			return 0;
		}
		short [] crcCmd = CRC16.crc(cmd);
		/*
		for(int i=0; i<crcCmd.length; i++)
		{
			Debug.d(TAG, "crcCmd["+i+"]="+Integer.toHexString(crcCmd[i]));
		}
		*/
		int fd = open(dev);
		if(fd <= 0)
			return 0;
		int ret = UsbSerial.write(fd, crcCmd, cmd.length);
		Debug.d(TAG, "write ret="+ret);
		byte[] response = UsbSerial.read(fd, PACKAGE_MAX_LEN);
		if(response == null)
		{
			Debug.d(TAG, "read return null");
			close(fd);
			return 0;
		}
		/*
		for(int i=0; i<response.length; i++)
		{
			Debug.d(TAG, "response["+i+"]="+Integer.toHexString(response[i] & 0x0FF));
		}
		*/
		close(fd);
		return ret;
	}
	
	/**
	 * set one param
	 * 0x80 	0x01			0x04		0x00		0x00		0x00		0x02		CRC16L		CRC16H		0x04
	 **/
	static public int setParam(String dev, int pIndex)
	{
		//short buf[] = new short[132];
		//buf[0] = 0x81;
		
		short cmd[]={0x80, 0x01, 0x00, 0x04, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x04};
		if(dev == null)
		{
			Debug.d(TAG, "ttyACM0 device node not opened");
			return 0;
		}
		cmd[2] = (short)pIndex;
		short [] crcCmd = CRC16.crc(cmd);
		/*
		for(int i=0; i<crcCmd.length; i++)
		{
			Debug.d(TAG, "crcCmd["+i+"]="+Integer.toHexString(crcCmd[i]));
		}
		*/
		int fd = open(dev);
		if(fd <= 0)
			return 0;
		int ret = UsbSerial.write(fd, crcCmd, cmd.length);
		Debug.d(TAG, "write ret="+ret);
		byte[] response = UsbSerial.read(fd, PACKAGE_MAX_LEN);
		if(response == null)
		{
			Debug.d(TAG, "read return null");
			close(fd);
			return 0;
		}
		/*
		for(int i=0; i<response.length; i++)
		{
			Debug.d(TAG, "response["+i+"]="+Integer.toHexString(response[i] & 0x0FF));
		}
		*/
		close(fd);
		return ret;
	}
	
	/**
	 * read param
	 * 0x80 	0x01		0x80 	0x04		0x00		0x00		0x00		0x00		CRC16L		CRC16H		0x04
	 **/
	static public int readParam(String dev, int pIndex)
	{
		//short buf[] = new short[132];
		//buf[0] = 0x81;
		
		short cmd[]={0x80, 0x01, 0x80, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04};
		if(dev == null)
		{
			Debug.d(TAG, "ttyACM0 device node not opened");
			return 0;
		}
		cmd[2] += pIndex;
		short [] crcCmd = CRC16.crc(cmd);
		/*
		for(int i=0; i<crcCmd.length; i++)
		{
			Debug.d(TAG, "crcCmd["+i+"]="+Integer.toHexString(crcCmd[i]));
		}
		*/
		int fd = open(dev);
		if(fd <= 0)
			return 0;
		int ret = UsbSerial.write(fd, crcCmd, cmd.length);
		Debug.d(TAG, "write ret="+ret);
		byte[] response = UsbSerial.read(fd, PACKAGE_MAX_LEN);
		if(response == null)
		{
			Debug.d(TAG, "read return null");
			close(fd);
			return 0;
		}
		/*
		for(int i=0; i<response.length; i++)
		{
			Debug.d(TAG, "response["+i+"]="+Integer.toHexString(response[i] & 0x0FF));
		}
		*/
		close(fd);		
		return ret;
	}
	
	
	static public int sendDataCtrl(String dev, int len)
	{
		short cmd[]={0x80, 0x02, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04};
		if(dev == null)
		{
			Debug.d(TAG, "ttyACM0 device node not opened");
			return 0;
		}
		Debug.d(TAG,"===>sendDataCtrl");
		
		cmd[4] = (short) (len >> 24 & 0x0ff);
		cmd[5] = (short) (len >> 16 & 0x0ff);
		cmd[6] = (short) (len >> 8 & 0x0ff);
		cmd[7] = (short) (len & 0x0ff);
		
		short [] crcCmd = CRC16.crc(cmd);
		for(int i=0; i<crcCmd.length; i++)
		{
			Debug.d(TAG, "crcCmd["+i+"]="+Integer.toHexString(crcCmd[i]));
		}
		int fd = open(dev);
		if(fd <= 0)
			return 0;
		int ret = UsbSerial.write(fd, crcCmd, cmd.length);
		Debug.d(TAG, "write ret="+ret);
		byte[] response = UsbSerial.read(fd, 10);
		if(response == null)
		{
			Debug.d(TAG, "read return null");
			close(fd);
			return 0;
		}
		/*for(int i=0; i<response.length; i++)
		{
			Debug.d(TAG, "response["+i+"]="+Integer.toHexString(response[i] & 0x0FF));
		}*/
		Debug.d(TAG,"<===sendDataCtrl");
		close(fd);
		return ret;
	}
	
	/*
	 * Printing Data 
	 * sendData firstly, then printData,
	 * 
	 */
	static public int printData(String dev, byte[]data)
	{
		short cmd[];
		//short cmd[]={0x80, 0x02, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04};
		if(dev == null)
		{
			Debug.d(TAG, "ttyACM0 device node not opened");
			return 0;
		}
		
		Debug.d(TAG,"====>printData, data len="+data.length);
		//disable CRC
		cmd = new short[data.length+4];
		cmd[0]=0x81&0x0ff;
		for(int i=0; i <data.length; i++)
		{
			cmd[i+1] = (short) (data[i]&0x0ff);
		}
		cmd[cmd.length-3] = 0x00;
		cmd[cmd.length-2] = 0x00;
		cmd[cmd.length-1] = 0x04;
		
		short [] crcCmd = CRC16.crc(cmd);
		/*
		 * the slave can only receive 2Mbytes data once
		 * so we need split the data and send several times 
		 * when the data length > 2Mbytes
		 */
		int fd = open(dev);
		if(fd <= 0)
			return 0;
		int ret = UsbSerial.write(fd, crcCmd, cmd.length);
		Debug.d(TAG, "=============printDate write ret="+ret);
		byte[] response = UsbSerial.read(fd, 10);
		if(response == null)
		{
			Debug.d(TAG, "read return null");
			close(fd);
			return 0;
		}
		for(int i=0; i<response.length; i++)
		{
			Debug.d(TAG, "response["+i+"]="+Integer.toHexString(response[i] & 0x0FF));
		}
		Debug.d(TAG,"<====printData");
		close(fd);
		return ret;
	}
	
	/*
	* Command - Send setting (0006)      
	*Send this command to inform the print head receive setting data. 
	*After send this command and get right return, start setting Data (0007)
	*/
	public static int sendSetting(String dev)
	{
		short cmd[]={0x80, 0x01, 0xff, 0x04, 0x00, 0x00, 0x00, 0x80, 0x00, 0x00, 0x04};
		if(dev == null)
		{
			Debug.d(TAG, "ttyACM0 device node not opened");
			return 0;
		}
		Debug.d(TAG,"====>sendSetting");
		short [] crcCmd = CRC16.crc(cmd);
		/*
		for(int i=0; i<crcCmd.length; i++)
		{
			Debug.d(TAG, "crcCmd["+i+"]="+Integer.toHexString(crcCmd[i]));
		}
		*/
		int fd = open(dev);
		if(fd <= 0)
			return 0;
		int ret = UsbSerial.write(fd, crcCmd, cmd.length);
		Debug.d(TAG, "write ret="+ret);
		byte[] response = UsbSerial.read(fd, 10);
		if(response == null)
		{
			Debug.d(TAG, "read return null");
			close(fd);
			return 0;
		}
		/*
		for(int i=0; i<response.length; i++)
		{
			Debug.d(TAG, "response["+i+"]="+Integer.toHexString(response[i] & 0x0FF));
		}*/
		Debug.d(TAG,"<====sendSetting");
		close(fd);
		return ret;
	}
	
	
	public static int sendSettingData(String dev, byte data[])
	{
		short[] cmd = new short[132];
		if(dev == null)
		{
			Debug.d(TAG, "ttyACM0 device node not opened");
			return 0;
		}
		Debug.d(TAG,"====>sendSettingData");
		cmd[0] = 0x81;
		for(int i=0; i<data.length && i < 128; i++)
		{
			cmd[i+1] = (short) (data[i]&0x0ff);
		}
		cmd[129] = 0;
		cmd[130] = 0;
		cmd[131] = 4;
		/*for(int i=0; i<cmd.length; i++)
		{
			Debug.d(TAG, "cmd["+i+"]="+Integer.toHexString(cmd[i]));
		}*/
		short [] crcCmd = CRC16.crc(cmd);
		/*for(int i=0; i<crcCmd.length; i++)
		{
			//Debug.d(TAG, "crcCmd["+i+"]="+Integer.toHexString(crcCmd[i]));
		}*/
		int fd = open(dev);
		if(fd <= 0)
			return 0;
		int ret = UsbSerial.write(fd, crcCmd, cmd.length);
		Debug.d(TAG, "write ret="+ret);
		byte[] response = UsbSerial.read(fd, 10);
		if(response == null)
		{
			Debug.d(TAG, "read return null");
			close(fd);
			return 0;
		}
		for(int i=0; i<response.length; i++)
		{
			Debug.d(TAG, "response["+i+"]="+Integer.toHexString(response[i] & 0x0FF));
		}
		Debug.d(TAG,"<====sendSettingData");
		close(fd);
		return ret;
	}
	
	
	static public int getInfo(String dev, byte[] info)
	{
		short cmd[]={0x80, 0x01, 0xfd, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04};
		if(dev == null)
		{
			Debug.d(TAG, "ttyACM0 device node not opened");
			return 0;
		}
		Debug.d(TAG,"====>getInfo");
		short [] crcCmd = CRC16.crc(cmd);
		for(int i=0; i<crcCmd.length; i++)
		{
			//Debug.d(TAG, "crcCmd["+i+"]="+Integer.toHexString(crcCmd[i]));
		}
		int fd = open(dev);
		if(fd <= 0)
			return 0;
		int ret = UsbSerial.write(fd, crcCmd, cmd.length);
		Debug.d(TAG, "write ret="+ret);
		byte[] response = UsbSerial.read(fd, 23);
		if(response == null)
		{
			Debug.d(TAG, "read return null");
			close(fd);
			return 0;
		}
		for(int i=0; i<response.length; i++)
		{
			Debug.d(TAG, "response["+i+"]="+Integer.toHexString(response[i] & 0x0FF));
		}
		if(info == null)
		{
			close(fd);
			return 0;
		}
			
		ByteArrayInputStream s = new ByteArrayInputStream(response);
			
		//Debug.d(TAG, "===avaliable size="+s.available());
		if(s.read(info, 0, 23)==-1)
			ret = 0;
		Debug.d(TAG,"<====getInfo");
		close(fd);
		return ret;
	}
	
}




package com.industry.printer;

import com.industry.printer.Utils.Debug;

import android.os.SystemProperties;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class PrinterBroadcastReceiver extends BroadcastReceiver {
	public static final String TAG="PrinterBroadcastReceiver";
	public static final String BOOT_COMPLETED="android.intent.action.BOOT_COMPLETED";
	
	public boolean usbAlive=false;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		boolean isSerial=false;
		
		Debug.d(TAG, "action="+intent.getAction());
		if(intent.getAction().equals(BOOT_COMPLETED))
		{
			int i=0;
			Intent intnt = new Intent();
			intnt.setClass(context, MainActivity.class);
			//context.startActivity(intnt);
			UsbManager mngr =(UsbManager) context.getSystemService(Context.USB_SERVICE);
			for(UsbDevice d : mngr.getDeviceList().values())
			{
				Debug.d(TAG, "vendor="+d.getVendorId()+",  product="+d.getProductId()+",name="+d.getDeviceName());
				if(d.getVendorId()==2630 && d.getProductId() == 38433)
				{
					Debug.d(TAG, "this is not print header, ignore it");
					usbAlive = true;
				}
			}
		}
		else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED))
		{
			Debug.d(TAG, "usb connected");
			UsbManager mngr =(UsbManager) context.getSystemService(Context.USB_SERVICE);
			
			for(UsbDevice d : mngr.getDeviceList().values())
			{
				Debug.d(TAG, "vendor="+d.getVendorId()+",  product="+d.getProductId()+",name="+d.getDeviceName());
				if(d.getVendorId()==2630 && d.getProductId() == 38433)
				{
					Debug.d(TAG, "this is not print header, ignore it");
					isSerial = true;
				}
			}
			
			if(isSerial==false)
				return;
			//System.setProperty("ctl.start", "mptty");
			SystemProperties.set("ctl.start", "mptty");
			Intent intnt = new Intent();
			intnt.setAction(ControlTabActivity.ACTION_REOPEN_SERIAL);
			context.sendBroadcast(intnt);
		}
		else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED))
		{
			Debug.d(TAG, "usb disconnected");
			UsbManager mngr =(UsbManager) context.getSystemService(Context.USB_SERVICE);
			
			for(UsbDevice d : mngr.getDeviceList().values())
			{
				Debug.d(TAG, "vendor="+d.getVendorId()+",  product="+d.getProductId()+",name="+d.getDeviceName());
				if(d.getVendorId() ==2630 && d.getProductId() == 38433)
				{
					Debug.d(TAG, "this is not print header, ignore it");
					isSerial = true;
				}
			}

			if(isSerial==false)
				return;
			Intent intnt = new Intent();
			intnt.setAction(ControlTabActivity.ACTION_CLOSE_SERIAL);
			context.sendBroadcast(intnt);
		}
	}

}

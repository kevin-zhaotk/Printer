package com.industry.printer;

import java.util.Vector;

import com.industry.printer.Utils.Debug;

//import android.os.SystemProperties;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class PrinterBroadcastReceiver extends BroadcastReceiver {
	public static final String TAG="PrinterBroadcastReceiver";
	public static final String BOOT_COMPLETED="android.intent.action.BOOT_COMPLETED";
	
	public static boolean mUsbAlive=false;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		boolean isSerial=false;
		
		Debug.d(TAG, "action="+intent.getAction());
		if(intent.getAction().equals(BOOT_COMPLETED))
		{
			int i=0;
			Intent intnt = new Intent();
			intnt.setAction(ControlTabActivity.ACTION_BOOT_COMPLETE);
			context.sendBroadcast(intnt);
			UsbManager mngr =(UsbManager) context.getSystemService(Context.USB_SERVICE);
			for(UsbDevice d : mngr.getDeviceList().values())
			{
				Debug.d(TAG, "vendor="+d.getVendorId()+",  product="+d.getProductId()+",name="+d.getDeviceName());
				if(d.getVendorId()==0x3eb && d.getProductId() == 0x6119)
				{
					mUsbAlive = true;
				}
			}
		}
		else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED))
		{
			Debug.d(TAG, "new usb device attached");
			//System.setProperty("ctl.start", "mptty");
//			SystemProperties.set("ctl.start", "mptty");
			Intent intnt = new Intent();
			intnt.setAction(ControlTabActivity.ACTION_REOPEN_SERIAL);
			context.sendBroadcast(intnt);
		}
		else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED))
		{
			Debug.d(TAG, "usb disconnected");
			UsbManager mngr =(UsbManager) context.getSystemService(Context.USB_SERVICE);
			if(mUsbAlive == false)
				return;
			mUsbAlive = false;
			for(UsbDevice d : mngr.getDeviceList().values())
			{
				Debug.d(TAG, "vendor="+d.getVendorId()+",  product="+d.getProductId()+",name="+d.getDeviceName());
				if(d.getVendorId()==0x3eb && d.getProductId() == 0x6119)
				{
					mUsbAlive = true;
				}
			}

			if(mUsbAlive==true)
				return;
			Intent intnt = new Intent();
			intnt.setAction(ControlTabActivity.ACTION_CLOSE_SERIAL);
			context.sendBroadcast(intnt);
		}
	}

}

package com.industry.printer.Usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class UsbPermissionBroadcastReceiver extends BroadcastReceiver {

	public static final String TAG="UsbPermissionBroadcastReceiver";
	public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
        if (ACTION_USB_PERMISSION.equals(action)) {
            synchronized (this) {
                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if(device != null){
                      //call method to set up device communication
                   }
                } 
                else {
                    Log.d(TAG, "permission denied for device " + device);
                }
            }
        }
	}

}

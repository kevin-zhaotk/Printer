package com.industry.printer.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.graphics.Path;

/**
 * ConfigPath 选择系统设置的保持路径
 * @author kevin
 *
 */
public class ConfigPath {
	
	private static final String TAG = ConfigPath.class.getSimpleName();
	
	public static ArrayList<String> getMountedUsb() {
		ArrayList<String> mPaths = new ArrayList<String>();
		Debug.d(TAG, "===>getMountedUsb");
		try {
			FileInputStream file = new FileInputStream(Configs.PROC_MOUNT_FILE);
			BufferedReader reader = new BufferedReader(new InputStreamReader(file));
			String line = reader.readLine();
			for(;line != null;) {
				if (!line.contains("/mnt/usbhost")) {
					line = reader.readLine();
					continue;
				}
				String items[] = line.split(" ");
				mPaths.add(items[1]);
				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mPaths;
	}
}

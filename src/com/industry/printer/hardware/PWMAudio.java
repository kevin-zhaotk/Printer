package com.industry.printer.hardware;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.friendlyarm.AndroidSDK.HardwareControler;

import android.drm.DrmStore.Playback;

public class PWMAudio {

	public static ExecutorService mThreadPoll = Executors.newSingleThreadExecutor();
	
	public static void Play() {
		mThreadPoll.execute(new Runnable() {
			
			@Override
			public void run() {
				HardwareControler.PWMPlay(1000);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				HardwareControler.PWMStop();
			}
		});
		// mThreadPoll.shutdown();
		
	}
}

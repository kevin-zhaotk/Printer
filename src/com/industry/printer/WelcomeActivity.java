package com.industry.printer;

import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.Utils.PackageInstaller;
import com.industry.printer.Utils.PlatformInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;

public class WelcomeActivity extends Activity {
	
	private Context mContext;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mContext = getApplicationContext();
		/*初始化系统配置*/
		Configs.initConfigs(mContext);
		
		if (!upgrade()) {
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onBackPressed() {
		return ;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Debug.d("", "--->onConfigurationChanged");
	}
	
	private boolean upgrade() {
		if (PlatformInfo.PRODUCT_SMFY_SUPER3.equals(PlatformInfo.getProduct())) {
			PackageInstaller installer = PackageInstaller.getInstance(this);
			return installer.silentUpgrade();
		}
		return false;
	}
}

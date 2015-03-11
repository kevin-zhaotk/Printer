package com.industry.printer.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class FPGADeviceSettings {
	
	private static final String TAG= FPGADeviceSettings.class.getSimpleName();
	
	/**
	 * FPGA设置相关属性
	 */
	public static String fPGA_SETTINGS_FILE="fpga_settings";
	
	
	/**
	 * 光电选择
	 */
	public static String FPGA_SETTINGS_PHO 	= "fpga_settings_pho";
	
	/**
	 * 触发模式选择 	=1  mode 1
	 *				=2  mode 2
	 *				=3  mode 3
	 *				=4  mode 4 
	 */
	public static String FPGA_SETTINGS_MODE = "fpga_settings_mode";
	
	/**
	 * PHO延时高位
	 */
	public static String FPGA_SETTINGS_PHO_DELAY_H = "fpga_settings_pho_delay_h";
	/**
	 * PHO延时低位	单位 =0.1ms
	 */
	public static String FPGA_SETTINGS_PHO_DELAY_L = "fpga_settings_pho_delay_l";
	
	/**
	 * PHO 输出周期 单位： 0.1ms
	 */
	public static String FPGA_SETTINGS_PHO_PERIOD = "fpga_settings_pho_period";
	
	/**
	 * 定时输出周期 单位10ms. 
	 */
	public static String FPGA_SETTINGS_TIME_PERIOD = "fpga_settings_time_period";
	
	/**
	 * 编码器触发条件脉冲数
	 */
	public static String FPGA_SETTINGS_ENCODER_TRIGER_PULSE = "fpga_settings_encoder_triger_pulse";
	
	/**
	 * 编码器定长脉数
	 */
	public static String FPGA_SETTINGS_ENCODER_FIXED_PULSE = "fpga_settings_encoder_fixed_pulse";
	
	/**
	 * 编码器延时条件脉冲数
	 */
	public static String FPGA_SETTINGS_ENCODER_DELAY_PULSE = "fpga_settings_encoder_delay_pulse";
	
	/**
	 * 每次输出高时间 单位0.01ms
	 */
	public static String FPGA_SETTINGS_HIGH_LENGTH = "fpga_settings_high_length";
	
	/**
	 * updateSettings 更新FPGA的设置
	 * @param context
	 */
	
	public static void updateSettings(Context context) {
		
		SharedPreferences preferences = context.getSharedPreferences(fPGA_SETTINGS_FILE, Context.MODE_PRIVATE);
		int pho = preferences.getInt(FPGA_SETTINGS_PHO, 0);
		int mode = preferences.getInt(FPGA_SETTINGS_MODE, 0);
		int pho_delay_h = preferences.getInt(FPGA_SETTINGS_PHO_DELAY_H, 0);
		int pho_delay_l = preferences.getInt(FPGA_SETTINGS_PHO_DELAY_L, 0);
		int peroid = preferences.getInt(FPGA_SETTINGS_PHO_PERIOD, 0);
		int time_peroid = preferences.getInt(FPGA_SETTINGS_TIME_PERIOD, 0);
		int triger_pulse = preferences.getInt(FPGA_SETTINGS_ENCODER_TRIGER_PULSE, 0);
		int fixed_pulse = preferences.getInt(FPGA_SETTINGS_ENCODER_FIXED_PULSE, 0);
		int delay_pulse = preferences.getInt(FPGA_SETTINGS_ENCODER_DELAY_PULSE, 0);
		int highLen = preferences.getInt(FPGA_SETTINGS_HIGH_LENGTH, 0);
		
		
	}
}

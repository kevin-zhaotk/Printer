package com.industry.printer.Utils;

import com.industry.printer.FileFormat.SystemConfigFile;
import com.industry.printer.hardware.FpgaGpioOperation;

import android.content.Context;
import android.content.SharedPreferences;

public class FPGADeviceSettings {
	
	private static final String TAG= FPGADeviceSettings.class.getSimpleName();
	
	/**
	 * FPGA璁剧疆鐩稿叧灞炴��
	 */
	public static String fPGA_SETTINGS_FILE="fpga_settings";
	
	
	/**
	 * 鍏夌數閫夋嫨
	 */
	public static String FPGA_SETTINGS_PHO 	= "fpga_settings_pho";
	
	/**
	 * 瑙﹀彂妯″紡閫夋嫨 	=1  mode 1
	 *				=2  mode 2
	 *				=3  mode 3
	 *				=4  mode 4 
	 */
	public static String FPGA_SETTINGS_MODE = "fpga_settings_mode";
	
	/**
	 * PHO寤舵椂楂樹綅
	 */
	public static String FPGA_SETTINGS_PHO_DELAY_H = "fpga_settings_pho_delay_h";
	/**
	 * PHO寤舵椂浣庝綅	鍗曚綅 =0.1ms
	 */
	public static String FPGA_SETTINGS_PHO_DELAY_L = "fpga_settings_pho_delay_l";
	
	/**
	 * PHO 杈撳嚭鍛ㄦ湡 鍗曚綅锛� 0.1ms
	 */
	public static String FPGA_SETTINGS_PHO_PERIOD = "fpga_settings_pho_period";
	
	/**
	 * 瀹氭椂杈撳嚭鍛ㄦ湡 鍗曚綅10ms. 
	 */
	public static String FPGA_SETTINGS_TIME_PERIOD = "fpga_settings_time_period";
	
	/**
	 * 缂栫爜鍣ㄨЕ鍙戞潯浠惰剦鍐叉暟
	 */
	public static String FPGA_SETTINGS_ENCODER_TRIGER_PULSE = "fpga_settings_encoder_triger_pulse";
	
	/**
	 * 缂栫爜鍣ㄥ畾闀胯剦鏁�
	 */
	public static String FPGA_SETTINGS_ENCODER_FIXED_PULSE = "fpga_settings_encoder_fixed_pulse";
	
	/**
	 * 缂栫爜鍣ㄥ欢鏃舵潯浠惰剦鍐叉暟
	 */
	public static String FPGA_SETTINGS_ENCODER_DELAY_PULSE = "fpga_settings_encoder_delay_pulse";
	
	/**
	 * 姣忔杈撳嚭楂樻椂闂� 鍗曚綅0.01ms
	 */
	public static String FPGA_SETTINGS_HIGH_LENGTH = "fpga_settings_high_length";
	
	/**
	 * updateSettings 鏇存柊FPGA鐨勮缃�
	 * @param context
	 */
	
	public static void updateSettings(Context context) {
		
		
		int fd = FpgaGpioOperation.open(FpgaGpioOperation.FPGA_DRIVER_FILE);
		
		char data[] = new char[Configs.gParams];
		
		
		data[0] = (char) SystemConfigFile.mEncoder;
		data[1] = (char) SystemConfigFile.mTrigerMode;
		data[2] = (char) SystemConfigFile.mPHOHighDelay;
		data[3] = (char) SystemConfigFile.mPHOLowDelay;
		data[4] = (char) SystemConfigFile.mPHOOutputPeriod;
		data[5] = (char) SystemConfigFile.mTimedPeriod;
		data[7] = (char) SystemConfigFile.mTrigerPulse;
		data[8] = (char) SystemConfigFile.mLenFixedPulse;
		data[9] = (char) SystemConfigFile.mDelayPulse;
		data[10] = (char) SystemConfigFile.mHighLen;
		
		FpgaGpioOperation.ioctl(fd, FpgaGpioOperation.FPGA_CMD_SETTING, FpgaGpioOperation.FPGA_STATE_SETTING);
		
		FpgaGpioOperation.write(fd, data, data.length);
		
		FpgaGpioOperation.close(fd);
	}
	
	public static int writeData(char data[], int len) {
		
		int fd = FpgaGpioOperation.open(FpgaGpioOperation.FPGA_DRIVER_FILE);
		if(fd<0) {
			return -1;
		}
		FpgaGpioOperation.ioctl(fd, FpgaGpioOperation.FPGA_CMD_SENDDATA, FpgaGpioOperation.FPGA_STATE_OUTPUT);
		
		int wlen = FpgaGpioOperation.write(fd, data, len);
		if(wlen != len) {
			FpgaGpioOperation.close(fd);
			return -1;
		}
		return wlen;
	}
}

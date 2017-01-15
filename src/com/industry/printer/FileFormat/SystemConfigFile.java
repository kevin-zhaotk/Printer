package com.industry.printer.FileFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.R.integer;
import android.R.xml;
import android.app.Application;
import android.content.Context;
import android.util.Xml;

import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.hardware.RFIDDevice;
import com.industry.printer.hardware.RFIDManager;


public class SystemConfigFile{
	private static final String TAG = SystemConfigFile.class.getSimpleName();
	
	
	public static final String PH_SETTING_ENCODER = "encoder";
	public static final String PH_SETTING_TRIGER_MODE = "trigerMode";
	public static final String PH_SETTING_HIGH_DELAY = "PHOHighDelay";
	public static final String PH_SETTING_LOW_DELAY = "PHOLowDelay";
	public static final String PH_SETTING_PHOOUTPUT_PERIOD = "PHOOutputPeriod";
	public static final String PH_SETTING_TIMED_PERIOD = "timedPeriod";
	public static final String PH_SETTING_TRIGER_PULSE = "trigerPulse";
	public static final String PH_SETTING_LENFIXED_PULSE = "lenFixedPulse";
	public static final String PH_SETTING_DELAY_PULSE = "delayPulse";
	public static final String PH_SETTING_HIGH_LEN = "highLen";
	public static final String PH_SETTING_RESERVED_11 = "reserved11";
	public static final String PH_SETTING_RESERVED_12 = "reserved12";
	public static final String PH_SETTING_RESERVED_13 = "reserved13";
	public static final String PH_SETTING_RESERVED_14 = "reserved14";
	public static final String PH_SETTING_RESERVED_15 = "reserved15";
	public static final String PH_SETTING_RESERVED_16 = "bold";
	public static final String PH_SETTING_RESERVED_17 = "reserved17";
	public static final String PH_SETTING_RESERVED_18 = "reserved18";
	public static final String PH_SETTING_RESERVED_19 = "reserved19";
	public static final String PH_SETTING_RESERVED_20 = "reserved20";
	public static final String PH_SETTING_RESERVED_21 = "reserved21";
	public static final String PH_SETTING_RESERVED_22 = "reserved22";
	public static final String PH_SETTING_RESERVED_23 = "reserved23";
	public static final String PH_SETTING_RESERVED_24 = "reserved24";
	public static final String PH_SETTING_RESERVED_25 = "reserved25";
	public static final String PH_SETTING_RESERVED_26 = "reserved26";
	public static final String PH_SETTING_RESERVED_27 = "reserved27";
	public static final String PH_SETTING_RESERVED_28 = "reserved28";
	public static final String PH_SETTING_RESERVED_29 = "reserved29";
	public static final String PH_SETTING_RESERVED_30 = "reserved30";
	public static final String PH_SETTING_RESERVED_31 = "reserved31";
	public static final String PH_SETTING_RESERVED_32 = "reserved32";
	public static final String PH_SETTING_RESERVED_33 = "reserved33";
	public static final String PH_SETTING_RESERVED_34 = "reserved34";
	public static final String PH_SETTING_RESERVED_35 = "reserved35";
	public static final String PH_SETTING_RESERVED_36 = "reserved36";
	public static final String PH_SETTING_RESERVED_37 = "reserved37";
	public static final String PH_SETTING_RESERVED_38 = "reserved38";
	public static final String PH_SETTING_RESERVED_39 = "reserved39";
	public static final String PH_SETTING_RESERVED_40 = "reserved40";
	public static final String PH_SETTING_RESERVED_41 = "reserved41";
	public static final String PH_SETTING_RESERVED_42 = "reserved42";
	public static final String PH_SETTING_RESERVED_43 = "reserved43";
	public static final String PH_SETTING_RESERVED_44 = "reserved44";
	public static final String PH_SETTING_RESERVED_45 = "reserved45";
	public static final String PH_SETTING_RESERVED_46 = "reserved46";
	public static final String PH_SETTING_RESERVED_47 = "reserved47";
	public static final String PH_SETTING_RESERVED_48 = "reserved48";
	public static final String PH_SETTING_RESERVED_49 = "reserved49";
	public static final String PH_SETTING_RESERVED_50 = "reserved50";
	public static final String PH_SETTING_RESERVED_51 = "reserved51";
	public static final String PH_SETTING_RESERVED_52 = "reserved52";
	public static final String PH_SETTING_RESERVED_53 = "reserved53";
	public static final String PH_SETTING_RESERVED_54 = "reserved54";
	public static final String PH_SETTING_RESERVED_55 = "reserved55";
	public static final String PH_SETTING_RESERVED_56 = "reserved56";
	public static final String PH_SETTING_RESERVED_57 = "reserved57";
	public static final String PH_SETTING_RESERVED_58 = "reserved58";
	public static final String PH_SETTING_RESERVED_59 = "reserved59";
	public static final String PH_SETTING_RESERVED_60 = "reserved60";
	public static final String PH_SETTING_RESERVED_61 = "reserved61";
	public static final String PH_SETTING_RESERVED_62 = "reserved62";
	public static final String PH_SETTING_RESERVED_63 = "reserved63";
	public static final String PH_SETTING_RESERVED_64 = "reserved64";
	
	public static final String LAST_MESSAGE = "message";
	
	public static final int INDEX_DAY_START = 35;
	/*
	 * 目前參數使用情況：
	 * 1、參數1~24：分配給FPGA
	 * 2、參數25：每列列高
	 * 3、參數31：是否双列
	 * 4、參數32：双列偏移量
	 */
	public int mParam[] = new int[64]; 
	public int mFPGAParam[] = new int[24];
	
	public Context mContext;
	public static SystemConfigFile mInstance;
	
	public static HashMap<Integer, HashMap<String,Integer>> mParamRange = new HashMap<Integer, HashMap<String,Integer>>();
	
	public static SystemConfigFile getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new SystemConfigFile(context);
		}
		return mInstance;
	}
	
	public SystemConfigFile(Context context) {
		mContext = context;
		init();
	}
	public void init() {
		initParamRange();
		if(parseSystemCofig())
			return;
		//default param
		for (int i = 0; i < mParam.length; i++) {
			mParam[i] = checkParam(i+1, mParam[i]);
		}
	}
	public boolean parseSystemCofig() {
		FileReader reader=null;
		BufferedReader br = null;
		String tag;
		ArrayList<String> paths = ConfigPath.getMountedUsb();
		if (paths == null || paths.isEmpty()) {
			Debug.d(TAG, "--->no usb storage mounted");
			return false;
		}
		/*
		 * use this first usb as default 
		 */
		Debug.d(TAG, "--->usb root path:" + paths.get(0));
		XmlInputStream inStream = new XmlInputStream(paths.get(0)+Configs.SYSTEM_CONFIG_XML);
		List<XmlTag> list = inStream.read();
		if (list == null) {
			Debug.d(TAG, "--->read system_config file fail");
			return false;
		}
		for (XmlTag t : list) {
			tag = t.getKey();
			if (tag.equalsIgnoreCase(PH_SETTING_ENCODER)) {
				mParam[0] = Integer.parseInt(t.getValue());
				mParam[0] = checkParam(1, mParam[0]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_TRIGER_MODE)) {
				mParam[1] = Integer.parseInt(t.getValue());
				mParam[1] = checkParam(2, mParam[1]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_HIGH_DELAY)) {
				mParam[2] = Integer.parseInt(t.getValue());
				mParam[2] = checkParam(3, mParam[2]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_LOW_DELAY)) {
				mParam[3] = Integer.parseInt(t.getValue());
				/*光电延时 0-65535 默认值100*/
				mParam[3] = checkParam(4, mParam[3]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_PHOOUTPUT_PERIOD)) {
				mParam[4] = Integer.parseInt(t.getValue());
				/*字宽(毫秒） 下发FPGA-S5 0-65535*/
				mParam[4] = checkParam(5, mParam[4]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_TIMED_PERIOD)) {
				mParam[5] = Integer.parseInt(t.getValue());
				mParam[5] = checkParam(6, mParam[5]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_TRIGER_PULSE)) {
				mParam[6] = Integer.parseInt(t.getValue());
				/*列间脉冲 下发FPGA- S7	1-50*/
				mParam[6] = checkParam(7, mParam[6]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_LENFIXED_PULSE)) {
				mParam[7] = Integer.parseInt(t.getValue());
				/*定长脉冲 下发FPGA-S8 	1-65535*/
				mParam[7] = checkParam(8, mParam[7]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_DELAY_PULSE)) {
				mParam[8] = Integer.parseInt(t.getValue());
				/*脉冲延时 下发FPGA-S9 	1-65535*/
				mParam[8] = checkParam(9, mParam[8]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_HIGH_LEN)) {
				mParam[9] = Integer.parseInt(t.getValue());
				/*墨点大小 200-2000 默认值800*/
				mParam[9] = checkParam(10, mParam[9]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_11)) {
				mParam[10] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_12)) {
				mParam[11] = Integer.parseInt(t.getValue());
				mParam[11] = checkParam(12, mParam[11]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_13)) {
				mParam[12] = Integer.parseInt(t.getValue());
				mParam[12] = checkParam(13, mParam[12]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_14)) {
				mParam[13] = Integer.parseInt(t.getValue());
				mParam[13] = checkParam(14, mParam[13]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_15)) {
				mParam[14] = Integer.parseInt(t.getValue());
				mParam[14] = checkParam(15, mParam[14]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_16)) {
				mParam[15] = Integer.parseInt(t.getValue());
				/*加重 0-9 默认值0*/
				mParam[15] = checkParam(16, mParam[15]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_17)) {
				mParam[16] = Integer.parseInt(t.getValue());
				mParam[16] = checkParam(17, mParam[16]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_18)) {
				mParam[17] = Integer.parseInt(t.getValue());
				mParam[17] = checkParam(18, mParam[17]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_19)) {
				mParam[18] = Integer.parseInt(t.getValue());
				mParam[18] = checkParam(19, mParam[18]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_20)) {
				mParam[19] = Integer.parseInt(t.getValue());
				mParam[19] = checkParam(20, mParam[19]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_21)) {
				mParam[20] = Integer.parseInt(t.getValue());
				mParam[20] = checkParam(21, mParam[20]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_22)) {
				mParam[21] = Integer.parseInt(t.getValue());
				mParam[21] = checkParam(22, mParam[21]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_23)) {
				mParam[22] = Integer.parseInt(t.getValue());
				mParam[23] = checkParam(23, mParam[22]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_24)) {
				mParam[23] = Integer.parseInt(t.getValue());
				mParam[23] = checkParam(24, mParam[23]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_25)) {
				mParam[24] = Integer.parseInt(t.getValue());
				mParam[24] = checkParam(25, mParam[24]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_26)) {
				mParam[25] = Integer.parseInt(t.getValue());
				mParam[25] = checkParam(26, mParam[25]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_27)) {
				mParam[26] = Integer.parseInt(t.getValue());
				mParam[26] = checkParam(27, mParam[26]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_28)) {
				mParam[27] = Integer.parseInt(t.getValue());
				mParam[27] = checkParam(28, mParam[27]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_29)) {
				mParam[28] = Integer.parseInt(t.getValue());
				mParam[28] = checkParam(29, mParam[28]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_30)) {
				mParam[29] = Integer.parseInt(t.getValue());
				mParam[29] = checkParam(30, mParam[29]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_31)) {
				mParam[30] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_32)) {
				mParam[31] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_33)) {
				mParam[32] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_34)) {
				mParam[33] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_35)) {
				mParam[34] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_36)) {
				mParam[35] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_37)) {
				mParam[36] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_38)) {
				mParam[37] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_39)) {
				mParam[38] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_40)) {
				mParam[39] = Integer.parseInt(t.getValue());
				mParam[39] = checkParam(40, mParam[39]);
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_41)) {
				mParam[40] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_42)) {
				mParam[41] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_43)) {
				mParam[42] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_44)) {
				mParam[43] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_45)) {
				mParam[44] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_46)) {
				mParam[45] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_47)) {
				mParam[46] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_48)) {
				mParam[47] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_49)) {
				mParam[48] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_50)) {
				mParam[49] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_51)) {
				mParam[50] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_52)) {
				mParam[51] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_53)) {
				mParam[52] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_54)) {
				mParam[53] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_55)) {
				mParam[54] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_56)) {
				mParam[55] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_57)) {
				mParam[56] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_58)) {
				mParam[57] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_59)) {
				mParam[58] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_60)) {
				mParam[59] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_61)) {
				mParam[60] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_62)) {
				mParam[61] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_63)) {
				mParam[62] = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_64)) {
				mParam[63] = Integer.parseInt(t.getValue());
			} 
			Debug.d(TAG, "===>tag key:"+tag+", value:"+t.getValue());
		}
		inStream.close();
		return true;
		/*
		try {
			reader = new FileReader(file);
			br = new BufferedReader(reader);
			String line = br.readLine();
			while (line != null) {
				String[] args = line.split(" ");
				if (PH_SETTING_ENCODER.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_ENCODER);
					if (args.length < 2) {
						mParam[0] = 0;
					} else {
						mParam[0] = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_TRIGER_MODE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_TRIGER_MODE);
					if (args.length < 2) {
						mParam[1] = 0;
					} else {
						mParam[1] = Integer.parseInt(args[1]);
					}
				} else if (PH_SETTING_HIGH_DELAY.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_HIGH_DELAY);
					if (args.length < 2) {
						mParam[2] = 0;
					} else {
						mParam[2] = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_LOW_DELAY.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_LOW_DELAY);
					if (args.length < 2) {
						mParam[3] = 0;
					} else {
						mParam[3] = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_PHOOUTPUT_PERIOD.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_PHOOUTPUT_PERIOD);
					if (args.length < 2) {
						mParam[4] = 0;
					} else {
						mParam[4] = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_TIMED_PERIOD.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_TIMED_PERIOD);
					if (args.length < 2) {
						mParam[5] = 0;
					} else {
						mParam[5] = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_TRIGER_PULSE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_TRIGER_PULSE);
					if (args.length < 2) {
						mParam[6] = 0;
					} else {
						mParam[6] = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_LENFIXED_PULSE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_LENFIXED_PULSE);
					if (args.length < 2) {
						mParam[7] = 0;
					} else {
						mParam[7] = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_DELAY_PULSE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_DELAY_PULSE);
					if (args.length < 2) {
						mParam[8] = 0;
					} else {
						mParam[8] = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_HIGH_LEN.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_HIGH_LEN);
					if (args.length < 2) {
						mParam[9] = 0;
					} else {
						mParam[9] = Integer.parseInt(args[1]);
					}
				} else {
					Debug.d(TAG, "===>unknow param: "+args[0]);
				}
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		*/
	}
	
	
	public void saveConfig() {
		
		ArrayList<String> paths = ConfigPath.getMountedUsb();
		if (paths == null || paths.isEmpty()) {
			Debug.d(TAG, "===>saveConfig error");
			return ;
		}
		
		/*
		 * use the first usb as the default device
		 */
		String dev = paths.get(0);
		File dir = new File(dev+Configs.SYSTEM_CONFIG_DIR);
		if (!dir.exists()) {
			if(dir.mkdirs() == false)
				return;
		}
		Debug.d(TAG, "===>dir:"+dir.getAbsolutePath());
		ArrayList<XmlTag> list = new ArrayList<XmlTag>();
		XmlTag tag1 = new XmlTag(PH_SETTING_ENCODER, String.valueOf(mParam[0]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_TRIGER_MODE, String.valueOf(mParam[1]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_HIGH_DELAY, String.valueOf(mParam[2]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_LOW_DELAY, String.valueOf(mParam[3]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_PHOOUTPUT_PERIOD, String.valueOf(mParam[4]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_TIMED_PERIOD, String.valueOf(mParam[5]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_TRIGER_PULSE, String.valueOf(mParam[6]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_LENFIXED_PULSE, String.valueOf(mParam[7]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_DELAY_PULSE, String.valueOf(mParam[8]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_HIGH_LEN, String.valueOf(mParam[9]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_11, String.valueOf(mParam[10]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_12, String.valueOf(mParam[11]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_13, String.valueOf(mParam[12]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_14, String.valueOf(mParam[13]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_15, String.valueOf(mParam[14]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_16, String.valueOf(mParam[15]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_17, String.valueOf(mParam[16]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_18, String.valueOf(mParam[17]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_19, String.valueOf(mParam[18]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_20, String.valueOf(mParam[19]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_21, String.valueOf(mParam[20]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_22, String.valueOf(mParam[21]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_23, String.valueOf(mParam[22]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_24, String.valueOf(mParam[23]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_25, String.valueOf(mParam[24]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_26, String.valueOf(mParam[25]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_27, String.valueOf(mParam[26]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_28, String.valueOf(mParam[27]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_29, String.valueOf(mParam[28]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_30, String.valueOf(mParam[29]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_31, String.valueOf(mParam[30]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_32, String.valueOf(mParam[31]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_33, String.valueOf(mParam[32]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_34, String.valueOf(mParam[33]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_35, String.valueOf(mParam[34]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_36, String.valueOf(mParam[35]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_37, String.valueOf(mParam[36]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_38, String.valueOf(mParam[37]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_39, String.valueOf(mParam[38]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_40, String.valueOf(mParam[39]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_41, String.valueOf(mParam[40]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_42, String.valueOf(mParam[41]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_43, String.valueOf(mParam[42]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_44, String.valueOf(mParam[43]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_45, String.valueOf(mParam[44]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_46, String.valueOf(mParam[45]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_47, String.valueOf(mParam[46]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_48, String.valueOf(mParam[47]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_49, String.valueOf(mParam[48]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_50, String.valueOf(mParam[49]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_51, String.valueOf(mParam[50]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_52, String.valueOf(mParam[51]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_53, String.valueOf(mParam[52]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_54, String.valueOf(mParam[53]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_55, String.valueOf(mParam[54]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_56, String.valueOf(mParam[55]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_57, String.valueOf(mParam[56]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_58, String.valueOf(mParam[57]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_59, String.valueOf(mParam[58]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_60, String.valueOf(mParam[59]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_61, String.valueOf(mParam[60]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_62, String.valueOf(mParam[61]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_63, String.valueOf(mParam[62]));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_64, String.valueOf(mParam[63]));
		list.add(tag1);
		XmlOutputStream stream = new XmlOutputStream(dev+Configs.SYSTEM_CONFIG_XML);
		stream.write(list);
		stream.close();
	}
	
	/*
	public void saveSettings() {
		ArrayList<XmlTag> tags = new ArrayList<XmlTag>();
		tags.add(new XmlTag(PH_SETTING_ENCODER, String.valueOf(mParam1)));
		tags.add(new XmlTag(PH_SETTING_TRIGER_MODE, String.valueOf(mParam2)));
		tags.add(new XmlTag(PH_SETTING_HIGH_DELAY, String.valueOf(mParam[2])));
		tags.add(new XmlTag(PH_SETTING_LOW_DELAY, String.valueOf(mParam[3])));
		tags.add(new XmlTag(PH_SETTING_PHOOUTPUT_PERIOD, String.valueOf(mParam[4])));
		tags.add(new XmlTag(PH_SETTING_TIMED_PERIOD, String.valueOf(mParam[5])));
		tags.add(new XmlTag(PH_SETTING_TRIGER_PULSE, String.valueOf(mParam[6])));
		tags.add(new XmlTag(PH_SETTING_LENFIXED_PULSE, String.valueOf(mParam[7])));
		tags.add(new XmlTag(PH_SETTING_DELAY_PULSE, String.valueOf(mParam[8])));
		tags.add(new XmlTag(PH_SETTING_HIGH_LEN, String.valueOf(mParam[9])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_11, String.valueOf(mParam[10])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_12, String.valueOf(mParam[11])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_13, String.valueOf(mParam[12])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_14, String.valueOf(mParam[13])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_15, String.valueOf(mParam[14])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_16, String.valueOf(mParam[15])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_17, String.valueOf(mParam[16])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_18, String.valueOf(mParam[17])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_19, String.valueOf(mParam[18])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_20, String.valueOf(mParam[19])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_21, String.valueOf(mParam[20])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_22, String.valueOf(mParam[21])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_23, String.valueOf(mParam[22])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_24, String.valueOf(mParam[23])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_25, String.valueOf(mParam[24])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_26, String.valueOf(mParam[25])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_27, String.valueOf(mParam[26])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_28, String.valueOf(mParam[27])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_29, String.valueOf(mParam[28])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_30, String.valueOf(mParam[29])));
		tags.add(new XmlTag(PH_SETTING_RESERVED_31, String.valueOf(mResv31)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_32, String.valueOf(mResv32)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_33, String.valueOf(mResv33)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_34, String.valueOf(mResv34)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_35, String.valueOf(mResv35)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_36, String.valueOf(mResv36)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_37, String.valueOf(mResv37)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_38, String.valueOf(mResv38)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_39, String.valueOf(mResv39)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_40, String.valueOf(mResv40)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_41, String.valueOf(mResv41)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_42, String.valueOf(mResv42)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_43, String.valueOf(mResv43)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_44, String.valueOf(mResv44)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_45, String.valueOf(mResv45)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_46, String.valueOf(mResv46)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_47, String.valueOf(mResv47)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_48, String.valueOf(mResv48)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_49, String.valueOf(mResv49)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_50, String.valueOf(mResv50)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_51, String.valueOf(mResv51)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_52, String.valueOf(mResv52)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_53, String.valueOf(mResv53)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_54, String.valueOf(mResv54)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_55, String.valueOf(mResv55)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_56, String.valueOf(mResv56)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_57, String.valueOf(mResv57)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_58, String.valueOf(mResv58)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_59, String.valueOf(mResv59)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_60, String.valueOf(mResv60)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_61, String.valueOf(mResv61)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_62, String.valueOf(mResv62)));
	}
	*/
	
	public String getLastMsg() {
		
		String tag;
		ArrayList<String> paths = ConfigPath.getMountedUsb();
		if (paths == null || paths.isEmpty()) {
			return null;
		}
		Debug.d(TAG, "===>path:"+paths.get(0));
		XmlInputStream inStream = new XmlInputStream(paths.get(0)+Configs.LAST_MESSAGE_XML);
		List<XmlTag> list = inStream.read();
		if (list == null) {
			inStream.close();
			return null;
		}
		for (XmlTag t : list) {
			tag = t.getKey();
			if (tag.equalsIgnoreCase(LAST_MESSAGE)) {
				inStream.close();
				return t.getValue();
			} 
			Debug.d(TAG, "===>tag key:"+tag+", value:"+t.getValue());
		}
		inStream.close();
		return null;
	}
	
	public void saveLastMsg(String name) {
		
		ArrayList<String> paths = ConfigPath.getMountedUsb();
		if (paths == null || paths.isEmpty() || name == null) {
			Debug.d(TAG, "===>saveConfig error");
			return ;
		}
		File file = new File(name);
		
		/*
		 * use the first usb as the default device
		 */
		String dev = paths.get(0);
		File dir = new File(dev+Configs.SYSTEM_CONFIG_DIR);
		if (!dir.exists()) {
			if(dir.mkdirs() == false)
				return;
		}
		ArrayList<XmlTag> list = new ArrayList<XmlTag>();
		XmlTag tag1 = new XmlTag(LAST_MESSAGE, file.getName());
		list.add(tag1);
		XmlOutputStream stream = new XmlOutputStream(dev+Configs.LAST_MESSAGE_XML);
		stream.write(list);
		
	}
	
	public static final String TAG_PARAMS = "params";
	public static final String TAG_PARAM = "param";
	public static final String TAG_ID = "id";
	public static final String TAG_MIN = "min";
	public static final String TAG_MAX = "max";
	public static final String TAG_DEFAULT = "default";
	
	/**
	 * 初始化已知参数的取值范围和默认值
	 * TODO:后期做成通过xml进行配置
	 */
	public void initParamRange() {
		Debug.d(TAG, "====>initParamRange");
		int id=1;
		HashMap<String, Integer> map = null;
		try {
			InputStream inputStream = mContext.getAssets().open("params.xml");
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(inputStream, "utf-8");
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					
					break;
				case XmlPullParser.START_TAG:
					Debug.d(TAG, "--->tag: " + parser.getName());
					if (TAG_PARAMS.equals(parser.getName())) {
						
					} else if (TAG_PARAM.equals(parser.getName())) {
						map = new HashMap<String, Integer>();
					} else if (TAG_ID.equals(parser.getName())) {
						parser.next();
						Debug.d(TAG, "--->id: " + parser.getText());
						id = Integer.parseInt(parser.getText());
					} else if (TAG_MIN.equals(parser.getName())) {
						parser.next();
						map.put("min", Integer.parseInt(parser.getText()));
					} else if (TAG_MAX.equals(parser.getName())) {
						parser.next();
						map.put("max", Integer.parseInt(parser.getText()));
					} else if (TAG_DEFAULT.equals(parser.getName())) {
						parser.next();
						map.put("default", Integer.parseInt(parser.getText()));
					}
					
					break;
				case XmlPullParser.END_TAG:
					if (TAG_PARAM.equals(parser.getName())) {
						mParamRange.put(id, map);
						Debug.d(TAG, "--->id=" + id + ", map=" + map);
					}
					break;
				default:
					break;
				}
				event = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*编码器,有效值0,1*/
		
/*
		//参数1
		map = new HashMap<String, Integer>();
		map.put("min", 1);
		map.put("max", 65535);
		map.put("default", 1);
		mParamRange.put(1, map);

		//参数2
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 1);
		map.put("default", 0);
		mParamRange.put(2, map);

		//参数3
		map = new HashMap<String, Integer>();
		map.put("min", 150);
		map.put("max", 750);
		map.put("default", 150);
		mParamRange.put(3, map);

		//参数4
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 65535);
		map.put("default", 0);
		mParamRange.put(4, map);

		//参数5
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 2);
		map.put("default", 0);
		mParamRange.put(5, map);

		//参数6
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 1);
		map.put("default", 0);
		mParamRange.put(6, map);

		//参数7 
		map = new HashMap<String, Integer>();
		map.put("min", 1);
		map.put("max", 65535);
		map.put("default", 0);
		mParamRange.put(7, map);

		//参数8
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 1);
		map.put("default", 0);
		mParamRange.put(8, map);

		
		// param9
		map = new HashMap<String, Integer>();
		map.put("min", 1);
		map.put("max", 500);
		map.put("default", 1);
		mParamRange.put(9, map);
		
		// param10
		map = new HashMap<String, Integer>();
		map.put("min", 100);
		map.put("max", 20000);
		map.put("default", 100);
		mParamRange.put(10, map);
		
		// param11
		map = new HashMap<String, Integer>();
		map.put("min", 1);
		map.put("max", 65535);
		map.put("default", 1);
		mParamRange.put(11, map);
		
		// param12
		map = new HashMap<String, Integer>();
		map.put("min", 1);
		map.put("max", 1000);
		map.put("default", 1);
		mParamRange.put(12, map);
		
		// param13
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 1);
		map.put("default", 0);
		mParamRange.put(13, map);
		
		// param14
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 1);
		map.put("default", 0);
		mParamRange.put(14, map);
				
		// param15
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 1);
		map.put("default", 0);
		mParamRange.put(15, map);
				
		// param16
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 1);
		map.put("default", 0);
		mParamRange.put(16, map);
		
		// param17
		map = new HashMap<String, Integer>();
		map.put("min", 1);
		map.put("max", 4);
		map.put("default", 1);
		mParamRange.put(17, map);
		
		// param19
		map = new HashMap<String, Integer>();
		map.put("min", 1);
		map.put("max", 65535);
		map.put("default", 1);
		mParamRange.put(19, map);
				
		// param20
		map = new HashMap<String, Integer>();
		map.put("min", 1);
		map.put("max", 1000);
		map.put("default", 1);
		mParamRange.put(20, map);
				
		// param21
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 1);
		map.put("default", 0);
		mParamRange.put(21, map);
				
		// param22
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 1);
		map.put("default", 0);
		mParamRange.put(22, map);
				
		// param23
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 1);
		map.put("default", 0);
		mParamRange.put(23, map);
		
		// param24
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 1);
		map.put("default", 0);
		mParamRange.put(24, map);
		
		// param25
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 1);
		map.put("default", 0);
		mParamRange.put(25, map);
		
		// param26
		map = new HashMap<String, Integer>();
		map.put("min", 80);
		map.put("max", 120);
		map.put("default", 80);
		mParamRange.put(26, map);
		
		// param27
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 1);
		map.put("default", 0);
		mParamRange.put(27, map);
		// param28
		map = new HashMap<String, Integer>();
		map.put("min", 17);
		map.put("max", 24);
		map.put("default", 17);
		mParamRange.put(28, map);
				
		// param29
		map = new HashMap<String, Integer>();
		map.put("min", 200);
		map.put("max", 60000);
		map.put("default", 200);
		mParamRange.put(29, map);
		
		// param30
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 2000);
		map.put("default", 0);
		mParamRange.put(30, map);
*/		
		
	}
	
	public int checkParam(int param, int value) {
		if (mParamRange == null) {
			return value;
		}
		HashMap<String, Integer> p = mParamRange.get(param);
		if (p == null) {
			return value;
		}
		int min = p.get("min");
		int max = p.get("max");
		int def = p.get("default");
		Debug.d(TAG, "*************Param" + param + "************");
		Debug.d(TAG, "min=" + min + ", max=" + max +", default=" + def);
		if (value < min || value > max) {
			Debug.d(TAG, "resetTo:" + def);
			if (param == 29) {
				if ( value == 0) {
					return value;
				} else if (value > max) {
					return max;
				} else if (value < min) {
					return min;
				}
			}
			
			return def;
		}
		
		return value;
	}
	
	public void paramTrans() {
		// 參數16
		mFPGAParam[15] = mParam[2]/150;
				
		// 參數1
		mFPGAParam[4] = 170000/(mParam[0]*mFPGAParam[15]);
		if (mFPGAParam[4] > 65535) {
			mFPGAParam[4] = 65535;
		} else if (mFPGAParam[4] < 65) {
			mFPGAParam[4] = 65;
		}
		
		// 參數3
		mFPGAParam[6] = (int) (mParam[9]*25.4/(mParam[8] *3.14)/mParam[2]);
		if (mFPGAParam[6] < 1 || mFPGAParam[6] > 20) {
			mFPGAParam[6] = 1;
		}
		// 參數4
		mFPGAParam[3] = mParam[3] * mFPGAParam[15] * 6 * mFPGAParam[4]/1000;
		if (mFPGAParam[3] <= 2) {
			mFPGAParam[3] = 3;
		} else if (mFPGAParam[3] >= 65535) {
			mFPGAParam[3] = 65534;
		}
		mFPGAParam[8] = (int) (mParam[3]*mParam[9]/(mParam[8]*3.14) * mFPGAParam[15]);
		if (mFPGAParam[8] <= 10) {
			mFPGAParam[8] = 11;
		} else if (mFPGAParam[8] >= 65535) {
			mFPGAParam[8] = 65534;
		}
		// 參數5
		if (mParam[4] == 0 || mParam[4] == 1) {
			mFPGAParam[0] = 0;
		} else if (mParam[4] == 2) {
			mFPGAParam[0] = 1;
		}
		
		// 參數6
		if (mParam[4] == 0 && mParam[5] == 0) {
			mFPGAParam[1] = 4;
		} else if (mParam[4] == 0 && mParam[5] == 1) {
			mFPGAParam[1] = 3;
		} else if (mParam[4] != 0 && mParam[5] == 0) {
			mFPGAParam[1] = 2;
		} else if (mParam[4] != 0 && mParam[5] == 1) {
			mFPGAParam[1] = 1;
		}
		// 參數7
		mFPGAParam[5] = mParam[6] * mFPGAParam[15] * 6 * mFPGAParam[4]/1000;
		if (mFPGAParam[5] < 3) {
			mFPGAParam[5] = 3;
		} else if (mFPGAParam[5] > 65534) {
			mFPGAParam[5] = 65534;
		}
		mFPGAParam[7] = (int) (mParam[6]*mParam[9]/(mParam[8]*3.14));
		if (mFPGAParam[7] < 11) {
			mFPGAParam[7] = 11;
		} else if (mFPGAParam[7] > 65534) {
			mFPGAParam[7] = 65534;
		}
		
		// 參數8
		if (mParam[7] == 0) {
			mFPGAParam[17] = mFPGAParam[17] | 0x10;
		} else if (mParam[7] == 1) {
			mFPGAParam[17] = mFPGAParam[17] & 0xef;
		}
		
		if (mParam[14] == 0) {
			mFPGAParam[17] = mFPGAParam[17] & 0xfe;
		} else if (mParam[14] == 1) {
			mFPGAParam[17] = mFPGAParam[17] | 0x01;
		}

		if (mParam[15] == 0) {
			mFPGAParam[17] = mFPGAParam[17] & 0xfd;
		} else if (mParam[15] == 1) {
			mFPGAParam[17] = mFPGAParam[17] | 0x02;
		}

		Debug.d(TAG, "--->param[16]=" + mParam[16] + ", " + (mFPGAParam[16] & 0xe7f));
		if (mParam[16] == 1) {
			mFPGAParam[16] = mFPGAParam[16] & 0xe7f;
		} else if (mParam[16] == 2) {
			mFPGAParam[16] = mFPGAParam[16] & 0xe7f;
			mFPGAParam[16] = mFPGAParam[16] | 0x080;
		} else if (mParam[16] == 3) {
			mFPGAParam[16] = mFPGAParam[16] & 0xe7f;
			mFPGAParam[16] = mFPGAParam[16] | 0x100;
		} else if (mParam[16] == 4) {
			mFPGAParam[16] = mFPGAParam[16] & 0xe7f;
			mFPGAParam[16] = mFPGAParam[16] | 0x180;
		}
		Debug.d(TAG, "--->param[16]=" + mFPGAParam[16]);
		// 参数23
		if (mParam[22] == 0) {
			mFPGAParam[17] = mFPGAParam[17] & 0xfb;
		} else if (mParam[22] == 1) {
			mFPGAParam[17] = mFPGAParam[17] | 0x04;
		}
		// 参数24
	    if (mParam[23] == 0) {
			mFPGAParam[17] = mFPGAParam[17] & 0xf7;
		} else if (mParam[23] == 1) {
			mFPGAParam[17] = mFPGAParam[17] | 0x08;
		}
	    // 参数25
	    int feature = 0;
	    if (mParam[24] == 0) {
			mFPGAParam[18] = mParam[25];
		} else if (mParam[24] == 1) {
			RFIDManager manager = RFIDManager.getInstance();
			RFIDDevice device = manager.getDevice(0);
			if (device == null || !device.isReady()) {
				feature = 50;	//如果參數不合法就按默認值
			} else {
				feature = device.mFeature[4];
				if (feature < 50 || feature > 112) {
					feature = 50;
				}
			}
			mFPGAParam[18] = feature;
		}
	    
	    // 参数27
	    
	    //RFID特征值6
	    int info = 17;
	    // 参数28
	    if (mParam[26] == 0) {
	    	mFPGAParam[16] = mFPGAParam[16] & 0xff8f;
			mFPGAParam[16] = mFPGAParam[16] | ((mParam[27]-17) << 4);
			Debug.d(TAG, "--->param=" + ((mParam[27]-17) << 4));
			Debug.d(TAG, "--->fpgaparam=" + mFPGAParam[16]);
		} else if (mParam[26] == 1) {
			RFIDManager manager = RFIDManager.getInstance();
			RFIDDevice device = manager.getDevice(0);
			if (device == null || !device.isReady()) {
				info = 17;	//如果參數不合法就按默認值
			} else {
				info = device.mFeature[5];
				if (info > 24 || info < 17) {
					info = 17;
				}
			}
			mFPGAParam[16] = mFPGAParam[16] & 0xff8f;
			mFPGAParam[16] = mFPGAParam[16] | ((info-17) << 4);
		}
	    
	    // 参数29
	    mFPGAParam[19] = mParam[28];
	    // 参数30
	    mFPGAParam[2] = mParam[29];
	    for (int i = 0; i < mFPGAParam.length; i++) {
			Debug.d(TAG, "--->mFPGAParam[" + i + "]=" + mFPGAParam[i]);
		}
	    
	}
	
	public int getFPGAParam(int index) {
		if (index >= mFPGAParam.length) {
			return 0;
		}
		return mFPGAParam[index];
	}
	
	public int[] getParams() {
		return mParam;
	}
	public int getParam(int index) {
		if (index >= mParam.length) {
			return 0;
		}
		return mParam[index];
	}
	
	public void setParam(int index, int value) {
		Debug.d(TAG, "--->index=" + index + ", value=" + value);
		if (index >= mParam.length) {
			return ;
		}
		mParam[index] = value;
	}
	
}
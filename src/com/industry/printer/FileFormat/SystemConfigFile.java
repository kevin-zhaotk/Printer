package com.industry.printer.FileFormat;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.industry.printer.R;
import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;

import android.text.StaticLayout;
import android.util.Log;
import android.widget.Toast;

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
	
	public static int mParam1 = 0;
	public static int mParam2 = 0;
	public static int mParam3 = 400;
	public static int mParam4 = 100;
	public static int mParam5 = 0;
	public static int mParam6 = 400;
	public static int mParam7 = 0;
	public static int mParam8 = 0;
	public static int mParam9 = 0;
	public static int mParam10 = 800;
	public static int mResv11 = 0;
	public static int mResv12 = 0;
	public static int mResv13 = 0;
	public static int mResv14 = 0;
	public static int mResv15 = 0;
	public static int mResv16 = 0;
	public static int mResv17 = 0;
	public static int mResv18 = 0;
	public static int mResv19 = 0;
	public static int mResv20 = 0;
	public static int mResv21 = 0;
	public static int mResv22 = 0;
	public static int mResv23 = 0;
	public static int mResv24 = 0;
	public static int mResv25 = 0;
	public static int mResv26 = 0;
	public static int mResv27 = 0;
	public static int mResv28 = 0;
	public static int mResv29 = 0;
	public static int mResv30 = 0;
	public static int mResv31 = 0;
	public static int mResv32 = 0;
	public static int mResv33 = 0;
	public static int mResv34 = 0;
	public static int mResv35 = 0;
	public static int mResv36 = 0;
	public static int mResv37 = 0;
	public static int mResv38 = 0;
	public static int mResv39 = 0;
	public static int mResv40 = 0;
	public static int mResv41 = 0;
	public static int mResv42 = 0;
	public static int mResv43 = 0;
	public static int mResv44 = 0;
	public static int mResv45 = 0;
	public static int mResv46 = 0;
	public static int mResv47 = 0;
	public static int mResv48 = 0;
	public static int mResv49 = 0;
	public static int mResv50 = 0;
	public static int mResv51 = 0;
	public static int mResv52 = 0;
	public static int mResv53 = 0;
	public static int mResv54 = 0;
	public static int mResv55 = 0;
	public static int mResv56 = 0;
	public static int mResv57 = 0;
	public static int mResv58 = 0;
	public static int mResv59 = 0;
	public static int mResv60 = 0;
	public static int mResv61 = 0;
	public static int mResv62 = 0;
	public static int mResv63 = 0;
	public static int mResv64 = 0;

	
	public static void parseSystemCofig() {
		FileReader reader=null;
		BufferedReader br = null;
		String tag;
		ArrayList<String> paths = ConfigPath.getMountedUsb();
		if (paths == null || paths.isEmpty()) {
			return;
		}
		/*
		 * use this first usb as default 
		 */
		XmlInputStream inStream = new XmlInputStream(paths.get(0)+Configs.SYSTEM_CONFIG_XML);
		List<XmlTag> list = inStream.read();
		if (list == null) {
			return;
		}
		for (XmlTag t : list) {
			tag = t.getKey();
			if (tag.equalsIgnoreCase(PH_SETTING_ENCODER)) {
				mParam1 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_TRIGER_MODE)) {
				mParam2 = Integer.parseInt(t.getValue());
				/*触发模式,有效值1,2,3,4*/
				if (mParam2 < 1 || mParam2 > 4) {
					mParam2 = 1;
				}
			} else if (tag.equalsIgnoreCase(PH_SETTING_HIGH_DELAY)) {
				mParam3 = Integer.parseInt(t.getValue());
				/*光电防抖 0-400 默认值20*/
				if (mParam3 < 0 || mParam3 > 600) {
					mParam3 = 20;
				}
			} else if (tag.equalsIgnoreCase(PH_SETTING_LOW_DELAY)) {
				mParam4 = Integer.parseInt(t.getValue());
				/*光电延时 0-65535 默认值100*/
				if (mParam4 < 0 || mParam4 > 65535) {
					mParam4 = 100;
				}
			} else if (tag.equalsIgnoreCase(PH_SETTING_PHOOUTPUT_PERIOD)) {
				mParam5 = Integer.parseInt(t.getValue());
				/*字宽(毫秒） 下发FPGA-S5 0-65535*/
				if (mParam5 < 0 || mParam5 > 65535) {
					mParam5 = 1000;
				}
			} else if (tag.equalsIgnoreCase(PH_SETTING_TIMED_PERIOD)) {
				mParam6 = Integer.parseInt(t.getValue());
				/*墨点大小 0-65535 默认值1000*/
				if (mParam6 < 0 || mParam6 > 65535) {
					mParam6 = 1000;
				}
			} else if (tag.equalsIgnoreCase(PH_SETTING_TRIGER_PULSE)) {
				mParam7 = Integer.parseInt(t.getValue());
				/*列间脉冲 下发FPGA- S7	1-50*/
				if (mParam7 < 1 || mParam7 > 50) {
					mParam7 = 1;
				}
			} else if (tag.equalsIgnoreCase(PH_SETTING_LENFIXED_PULSE)) {
				mParam8 = Integer.parseInt(t.getValue());
				/*定长脉冲 下发FPGA-S8 	1-65535*/
				if (mParam8 < 1 || mParam8 > 65535) {
					mParam8 = 1;
				}
			} else if (tag.equalsIgnoreCase(PH_SETTING_DELAY_PULSE)) {
				mParam9 = Integer.parseInt(t.getValue());
				/*脉冲延时 下发FPGA-S9 	1-65535*/
				if (mParam9 < 1 || mParam9 > 65535) {
					mParam9 = 1;
				}
			} else if (tag.equalsIgnoreCase(PH_SETTING_HIGH_LEN)) {
				mParam10 = Integer.parseInt(t.getValue());
				/*墨点大小 200-2000 默认值800*/
				if (mParam10 < 200 || mParam10 > 2000) {
					mParam10 = 800;
				}
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_11)) {
				mResv11 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_12)) {
				mResv12 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_13)) {
				mResv13 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_14)) {
				mResv14 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_15)) {
				mResv15 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_16)) {
				mResv16 = Integer.parseInt(t.getValue());
				/*加重 0-9 默认值0*/
				if (mResv16 < 0 || mResv16 > 9) {
					mResv16 = 0;
				}
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_17)) {
				mResv17 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_18)) {
				mResv18 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_19)) {
				mResv19 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_20)) {
				mResv20 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_21)) {
				mResv21 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_22)) {
				mResv22 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_23)) {
				mResv23 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_24)) {
				mResv24 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_25)) {
				mResv25 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_26)) {
				mResv26 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_27)) {
				mResv27 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_28)) {
				mResv28 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_29)) {
				mResv29 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_30)) {
				mResv30 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_31)) {
				mResv31 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_32)) {
				mResv32 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_33)) {
				mResv33 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_34)) {
				mResv34 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_35)) {
				mResv35 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_36)) {
				mResv36 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_37)) {
				mResv37 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_38)) {
				mResv38 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_39)) {
				mResv39 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_40)) {
				mResv40 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_41)) {
				mResv41 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_42)) {
				mResv42 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_43)) {
				mResv43 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_44)) {
				mResv44 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_45)) {
				mResv45 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_46)) {
				mResv46 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_47)) {
				mResv47 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_48)) {
				mResv48 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_49)) {
				mResv49 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_50)) {
				mResv50 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_51)) {
				mResv51 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_52)) {
				mResv52 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_53)) {
				mResv53 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_54)) {
				mResv54 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_55)) {
				mResv55 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_56)) {
				mResv56 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_57)) {
				mResv57 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_58)) {
				mResv58 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_59)) {
				mResv59 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_60)) {
				mResv60 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_61)) {
				mResv61 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_62)) {
				mResv62 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_63)) {
				mResv63 = Integer.parseInt(t.getValue());
			} else if (tag.equalsIgnoreCase(PH_SETTING_RESERVED_64)) {
				mResv64 = Integer.parseInt(t.getValue());
			} 
			Debug.d(TAG, "===>tag key:"+tag+", value:"+t.getValue());
		}
		inStream.close();
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
						mParam1 = 0;
					} else {
						mParam1 = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_TRIGER_MODE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_TRIGER_MODE);
					if (args.length < 2) {
						mParam2 = 0;
					} else {
						mParam2 = Integer.parseInt(args[1]);
					}
				} else if (PH_SETTING_HIGH_DELAY.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_HIGH_DELAY);
					if (args.length < 2) {
						mParam3 = 0;
					} else {
						mParam3 = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_LOW_DELAY.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_LOW_DELAY);
					if (args.length < 2) {
						mParam4 = 0;
					} else {
						mParam4 = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_PHOOUTPUT_PERIOD.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_PHOOUTPUT_PERIOD);
					if (args.length < 2) {
						mParam5 = 0;
					} else {
						mParam5 = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_TIMED_PERIOD.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_TIMED_PERIOD);
					if (args.length < 2) {
						mParam6 = 0;
					} else {
						mParam6 = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_TRIGER_PULSE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_TRIGER_PULSE);
					if (args.length < 2) {
						mParam7 = 0;
					} else {
						mParam7 = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_LENFIXED_PULSE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_LENFIXED_PULSE);
					if (args.length < 2) {
						mParam8 = 0;
					} else {
						mParam8 = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_DELAY_PULSE.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_DELAY_PULSE);
					if (args.length < 2) {
						mParam9 = 0;
					} else {
						mParam9 = Integer.parseInt(args[1]);
					}
					
				} else if (PH_SETTING_HIGH_LEN.equals(args[0])) {
					Debug.d(TAG, "===>param: "+PH_SETTING_HIGH_LEN);
					if (args.length < 2) {
						mParam10 = 0;
					} else {
						mParam10 = Integer.parseInt(args[1]);
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
	
	
	public static void saveConfig() {
		
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
		XmlTag tag1 = new XmlTag(PH_SETTING_ENCODER, String.valueOf(mParam1));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_TRIGER_MODE, String.valueOf(mParam2));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_HIGH_DELAY, String.valueOf(mParam3));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_LOW_DELAY, String.valueOf(mParam4));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_PHOOUTPUT_PERIOD, String.valueOf(mParam5));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_TIMED_PERIOD, String.valueOf(mParam6));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_TRIGER_PULSE, String.valueOf(mParam7));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_LENFIXED_PULSE, String.valueOf(mParam8));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_DELAY_PULSE, String.valueOf(mParam9));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_HIGH_LEN, String.valueOf(mParam10));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_11, String.valueOf(mResv11));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_12, String.valueOf(mResv12));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_13, String.valueOf(mResv13));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_14, String.valueOf(mResv14));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_15, String.valueOf(mResv15));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_16, String.valueOf(mResv16));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_17, String.valueOf(mResv17));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_18, String.valueOf(mResv18));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_19, String.valueOf(mResv19));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_20, String.valueOf(mResv20));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_21, String.valueOf(mResv21));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_22, String.valueOf(mResv22));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_23, String.valueOf(mResv23));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_24, String.valueOf(mResv24));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_25, String.valueOf(mResv25));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_26, String.valueOf(mResv26));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_27, String.valueOf(mResv27));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_28, String.valueOf(mResv28));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_29, String.valueOf(mResv29));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_30, String.valueOf(mResv30));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_31, String.valueOf(mResv31));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_32, String.valueOf(mResv32));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_33, String.valueOf(mResv33));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_34, String.valueOf(mResv34));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_35, String.valueOf(mResv35));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_36, String.valueOf(mResv36));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_37, String.valueOf(mResv37));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_38, String.valueOf(mResv38));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_39, String.valueOf(mResv39));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_40, String.valueOf(mResv40));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_41, String.valueOf(mResv41));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_42, String.valueOf(mResv42));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_43, String.valueOf(mResv43));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_44, String.valueOf(mResv44));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_45, String.valueOf(mResv45));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_46, String.valueOf(mResv46));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_47, String.valueOf(mResv47));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_48, String.valueOf(mResv48));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_49, String.valueOf(mResv49));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_50, String.valueOf(mResv50));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_51, String.valueOf(mResv51));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_52, String.valueOf(mResv52));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_53, String.valueOf(mResv53));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_54, String.valueOf(mResv54));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_55, String.valueOf(mResv55));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_56, String.valueOf(mResv56));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_57, String.valueOf(mResv57));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_58, String.valueOf(mResv58));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_59, String.valueOf(mResv59));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_60, String.valueOf(mResv60));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_61, String.valueOf(mResv61));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_62, String.valueOf(mResv62));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_63, String.valueOf(mResv63));
		list.add(tag1);
		tag1 = new XmlTag(PH_SETTING_RESERVED_64, String.valueOf(mResv64));
		list.add(tag1);
		XmlOutputStream stream = new XmlOutputStream(dev+Configs.SYSTEM_CONFIG_XML);
		stream.write(list);
		stream.close();
	}
	
	public void saveSettings() {
		ArrayList<XmlTag> tags = new ArrayList<XmlTag>();
		tags.add(new XmlTag(PH_SETTING_ENCODER, String.valueOf(mParam1)));
		tags.add(new XmlTag(PH_SETTING_TRIGER_MODE, String.valueOf(mParam2)));
		tags.add(new XmlTag(PH_SETTING_HIGH_DELAY, String.valueOf(mParam3)));
		tags.add(new XmlTag(PH_SETTING_LOW_DELAY, String.valueOf(mParam4)));
		tags.add(new XmlTag(PH_SETTING_PHOOUTPUT_PERIOD, String.valueOf(mParam5)));
		tags.add(new XmlTag(PH_SETTING_TIMED_PERIOD, String.valueOf(mParam6)));
		tags.add(new XmlTag(PH_SETTING_TRIGER_PULSE, String.valueOf(mParam7)));
		tags.add(new XmlTag(PH_SETTING_LENFIXED_PULSE, String.valueOf(mParam8)));
		tags.add(new XmlTag(PH_SETTING_DELAY_PULSE, String.valueOf(mParam9)));
		tags.add(new XmlTag(PH_SETTING_HIGH_LEN, String.valueOf(mParam10)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_11, String.valueOf(mResv11)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_12, String.valueOf(mResv12)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_13, String.valueOf(mResv13)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_14, String.valueOf(mResv14)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_15, String.valueOf(mResv15)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_16, String.valueOf(mResv16)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_17, String.valueOf(mResv17)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_18, String.valueOf(mResv18)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_19, String.valueOf(mResv19)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_20, String.valueOf(mResv20)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_21, String.valueOf(mResv21)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_22, String.valueOf(mResv22)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_23, String.valueOf(mResv23)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_24, String.valueOf(mResv24)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_25, String.valueOf(mResv25)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_26, String.valueOf(mResv26)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_27, String.valueOf(mResv27)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_28, String.valueOf(mResv28)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_29, String.valueOf(mResv29)));
		tags.add(new XmlTag(PH_SETTING_RESERVED_30, String.valueOf(mResv30)));
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
	
	
	public static String getLastMsg() {
		
		String tag;
		ArrayList<String> paths = ConfigPath.getMountedUsb();
		if (paths == null || paths.isEmpty()) {
			return null;
		}
		Debug.d(TAG, "===>path:"+paths.get(0));
		XmlInputStream inStream = new XmlInputStream(paths.get(0)+Configs.LAST_MESSAGE_XML);
		List<XmlTag> list = inStream.read();
		if (list == null) {
			return null;
		}
		for (XmlTag t : list) {
			tag = t.getKey();
			if (tag.equalsIgnoreCase(LAST_MESSAGE)) {
				return t.getValue();
			} 
			Debug.d(TAG, "===>tag key:"+tag+", value:"+t.getValue());
		}
		return null;
	}
	
	public static void saveLastMsg(String name) {
		
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
	
	
	public static void initParamRange() {
		HashMap<Integer, HashMap<String,Integer>> mParamRange = new HashMap<Integer, HashMap<String,Integer>>();
		
		// param2
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("min", 1);
		map.put("max", 4);
		mParamRange.put(2, map);
		// param3
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 600);
		map.put("default", 20);
		mParamRange.put(3, map);
		
		// param3
		map = new HashMap<String, Integer>();
		map.put("min", 0);
		map.put("max", 6553);
		map.put("default", 0);
		mParamRange.put(4, map);
				
	}
	
}
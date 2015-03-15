package com.industry.printer.hardware;

import com.industry.printer.Utils.Debug;

/**
 * @author kevin
 * 用于操作Fpga Gpio的类
 */
public class FpgaGpioOperation {
	
	
	/**
	 * 
	 */
	public static final int FPGA_CMD_SETTING	= 0X01;
	public static final int FPGA_CMD_SENDDATA	= 0X02;
	public static final int FPGA_CMD_SYNCDATA	= 0X03;
	/**
	 * 0x00 输出数据状态
	 * 0x01 设置状态
	 * 0x02 保留
	 * 0x03 清空状态
	 */
	public static final int FPGA_STATE_OUTPUT	= 0X00;
	public static final int FPGA_STATE_SETTING	= 0X01;
	public static final int FPGA_STATE_RESERVED	= 0X02;
	public static final int FPGA_STATE_CLEAN	= 0X03;
	
	public static final String FPGA_DRIVER_FILE = "/dev/fpga-gpio";
	/**
	 * GPIO JNI APIs 
	 **/
	/**
	 * 打开GPIO设备文件
	 * @param dev  GPIO驱动设备文件
	 * @return
	 */
	static public native int open(String dev);
	
	/**
	 * 向GPIO写入数据
	 * @param fd	设备句柄
	 * @param buffer	要写到GPIO的数据buffer
	 * @param count	写入数据长度，单位 sizeof（char）
	 * @return
	 */
	static public native int write(int fd, char[] buffer, int count);
	
	/**
	 * 向GPIO写入数据
	 * @param fd	设备句柄
	 * @param buffer	要写到GPIO的数据buffer
	 * @param count	写入数据长度，单位 sizeof（char）
	 * @return
	 */
	static public native int ioctl(int fd, int cmd, long arg);
	
	/**
	 * 关闭GPIO驱动设备文件
	 * @param fd	设备句柄
	 * @return
	 */
	static public native int close(int fd);
	
	
	//TAG
	public final static String TAG = FpgaGpioOperation.class.getSimpleName();
	
	
	
	public static int write(char[] buffer) {
		int fd;
//		fd = HardwareJni.OpenGPIO("/dev/fpga-gpio");
//		int len = HardwareJni.WriteGPIO(fd, buffer, buffer.length);
		fd = open("/dev/fpga-gpio");
		int len = write(fd, buffer, buffer.length);
		Debug.d(TAG, "----gpio write len="+len);
		
		return 1;
	}
}

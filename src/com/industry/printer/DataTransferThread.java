package com.industry.printer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import android.R.color;
import android.app.PendingIntent.OnFinished;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.industry.printer.MessageTask.MessageType;
import com.industry.printer.FileFormat.SystemConfigFile;
import com.industry.printer.Rfid.RfidScheduler;
import com.industry.printer.Rfid.RfidTask;
import com.industry.printer.Utils.ConfigPath;
import com.industry.printer.Utils.Configs;
import com.industry.printer.Utils.Debug;
import com.industry.printer.Utils.PlatformInfo;
import com.industry.printer.data.BinCreater;
import com.industry.printer.data.DataTask;
import com.industry.printer.hardware.FpgaGpioOperation;
import com.industry.printer.object.BaseObject;
import com.industry.printer.object.CounterObject;

/**
 * class DataTransferThread
 * 用一个独立的线程读取fpga的buffer状态，
 * 如果kernel已经把打印数据发送给FPGA，那么kernel的Buffer状态为空，可写
 * 此时，需要把下一条打印数据下发给kernel Buffer；
 * 如果kernel的buffer状态不为空，不可写
 * 此时，线程轮训buffer，直到kernel buffer状态为空；
 * @author kevin
 *
 */
public class DataTransferThread extends Thread {
	
	public static final String TAG = DataTransferThread.class.getSimpleName();
	private static final int MESSAGE_EXCEED_TIMEOUT = 60 * 1000;
	
	public static boolean mRunning;
	
	public static volatile DataTransferThread mInstance;
	
	private Context mContext;
	
	public boolean mNeedUpdate=false;
	private boolean isBufferReady = false;
	
	private int mcountdown = 0;
	/**打印数据buffer**/
	public List<DataTask> mDataTask;
	/* task index currently printing */
	private int mIndex;
	RfidScheduler	mScheduler;
	private static long mInterval = 0;
	private int mThreshold;
	
	private InkLevelListener mInkListener = null;
	
	public static DataTransferThread getInstance() {
		if(mInstance == null) {
			synchronized (DataTransferThread.class) {
				if (mInstance == null) {
					mInstance = new DataTransferThread();
				}
			}
			Debug.d(TAG, "===>new thread");
		}
		return mInstance;
	}
	
	public DataTransferThread() {
	}
	
	 
    public static void sort(char[] in){  
        char temp;  
        for(int i=in.length-1; i>=0; i--){  
            for(int j=0; j<=i-1; j++){  
                temp =in[j];  
                in[j]=in[j+1];  
                in[j+1]=temp;  
            }  
        }  
    }  
	
	/**
	 * 数据更新机制：
	 * 每次发送数据时同时触发一个delay 10s的handler
	 * 如果pollState返回不为0，即数据打印完毕，则remove handlermessage
	 * 否则，处理这个message并置数据更新状态为true
	 * run函数中一旦检测到数据更新状态变为true，就重新生成buffer并下发
	 */
	
	@Override
	public void run() {
		
		char[] buffer;
		long last = 0;
		/*逻辑要求，必须先发数据*/
		buffer = mDataTask.get(mIndex).getPrintBuffer();
		Debug.d(TAG, "--->runing getBuffer ok");
		int type = mDataTask.get(mIndex).getHeadType();
		
		
			SystemConfigFile config = SystemConfigFile.getInstance(mContext);
			int nDirection=  config.getParam(1) ;//  02.打印方向
			int ninvert1   =  config.getParam(14);// 15.1 头倒置
			int ninvert2   =  config.getParam(15);// 16.2 头倒置			
			
			int nmirrorimage1  =  config.getParam(12);// 13.1 头镜像 
			int nmirrorimage2  =  config.getParam(13);// 14.2 头镜像			
			int nmirrorimage3  =  config.getParam(20);// 21.3 头镜像
			int nmirrorimage4  =  config.getParam(21);// 22.4 头镜像 			
			//每列30像素
			if (type == MessageType.MESSAGE_TYPE_HZK_16_8 ||  type == MessageType.MESSAGE_TYPE_HZK_16_16 || type == MessageType.MESSAGE_TYPE_9MM) {	
			if(nDirection==1)
			{
				//char 类型站2个字节 
				char[] trans1 = new char [  buffer.length ]; //新建一个缓冲数据
				//每列2个字节			
				for (int irow = 0; irow < buffer.length-2; irow+=2)
				{
						trans1[buffer.length-1-irow-1] = buffer[irow] ;     //每列的第一个字节
						trans1[buffer.length-1-irow-1+1] = buffer[irow+1] ;	//每列的第二个字节							
							
				}			
				for (int irow = 0; irow < buffer.length; irow++)
				{
						buffer[irow] = trans1[irow] ;
				}	
			}//
			//addbylk_2_1/3_begin↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓				
			if(ninvert1==1 && ninvert2!=1 )
			{
			Debug.e(TAG, "===================="+ buffer.length );	
				char[] trans12 = new char [  buffer.length ];
				
				//每列2个字节
				for (int irow = 0; irow < buffer.length-2; irow+=2)
				{Debug.e(TAG, "====================trans12"+ trans12[irow] );
						  //处理每列的第一个字节：按位颠倒大小端					
						char char_new=0; 	
		
				        char_new |=  (char) ( (buffer[irow] >>6)&0x1 );  
				        char_new |=  (char) ( (buffer[irow] >>4)&0x2 );  
				        char_new |=  (char) ( (buffer[irow] >>2)&0x4 );  				        
				        char_new |=  (char) ( (buffer[irow] ) &0x8 );  				        		        
				        char_new |=  (char) ( (buffer[irow] <<2) &0x10 );  
				        char_new |=  (char) ( (buffer[irow] <<4) &0x20 );
				        char_new |=  (char) ( (buffer[irow] <<6) &0x40 );	
				        
				        char_new |=(char) ( buffer[irow] & 0x80  );							
				        char_new |=(char) ( buffer[irow] & 0x100  ) ; //每列的第一个字节的前7位 复制到缓冲中  其他的不拷贝维持原样
				        char_new |=(char) ( buffer[irow] & 0x200  ) ;  
				        char_new |=(char) ( buffer[irow] & 0x400  ) ;  
				        char_new |=(char) ( buffer[irow] & 0x800 ) ;  
				        char_new |=(char) ( buffer[irow] & 0x1000 ) ;  
				        char_new |=(char) ( buffer[irow] & 0x2000 ) ; 
				        char_new |=(char) ( buffer[irow] & 0x4000 ) ;  
				        char_new |=(char) ( buffer[irow] & 0x8000 ) ;  					        
				        
				        trans12[irow] =  char_new ; 				        						
						trans12[irow+1] = buffer[irow+1] ;      //处理每列的第二个字节：不变化			 						
				}			
				for (int irow = 0; irow < buffer.length; irow++)
				{
						buffer[irow] = trans12[irow] ;
						Debug.e(TAG, "====================trans12 buffer="+ buffer[irow] );
				}	
			}
						
			if(ninvert1!=1 && ninvert2==1)
			{
			Debug.e(TAG, "===================="+ buffer.length );	
				char[] trans12 = new char [  buffer.length ];
				
				//每列2个字节
				for (int irow = 0; irow < buffer.length-2; irow+=2)
				{Debug.e(TAG, "====================trans12"+ trans12[irow] );
						  //处理每列的第一个字节：按位颠倒大小端											
			    			       
						char char_new=0; 		

				        char_new |=(char) ( buffer[irow] & 0x1  ) ; 
				        char_new |=(char) ( buffer[irow] & 0x2) ;  
				        char_new |=(char) ( buffer[irow] & 0x4 ) ;  
				        char_new |=(char) ( buffer[irow] & 0x8 ) ;  
				        char_new |=(char) ( buffer[irow] & 0x10) ;  
				        char_new |=(char) ( buffer[irow] & 0x20 ) ; 
				        char_new |=(char) ( buffer[irow] & 0x40 ) ;  
				        
				        char_new |=  (char) ( (buffer[irow] >>6)&0x100 );  
				        char_new |=  (char) ( (buffer[irow] >>4)&0x200 );  
				        char_new |=  (char) ( (buffer[irow] >>2)&0x400 );  				        
				        char_new |=  (char) ( (buffer[irow] ) &0x800 );  				        		        
				        char_new |=  (char) ( (buffer[irow] <<2) &0x1000 );  
				        char_new |=  (char) ( (buffer[irow] <<4) &0x2000 );
				        char_new |=  (char) ( (buffer[irow] <<6) &0x4000 );	

				        
				        trans12[irow] =  char_new ; 
				        						
						trans12[irow+1] = buffer[irow+1] ;      //处理每列的第二个字节：不变化
				       						
				}			
				for (int irow = 0; irow < buffer.length; irow++)
				{
						buffer[irow] = trans12[irow] ;
						Debug.e(TAG, "====================trans12 buffer="+ buffer[irow] );
				}	
			}
						
			if(ninvert1==1 && ninvert2==1)// 功能：16高的头倒置 触发条件： 15.1 头倒置 16.2 头倒置 同时打开
			{
			Debug.e(TAG, "===================="+ buffer.length );	
				char[] trans12 = new char [  buffer.length ];
				
				//每列2个字节
				for (int irow = 0; irow < buffer.length-2; irow+=2)
				{Debug.e(TAG, "====================trans12"+ trans12[irow] );
		
			      //   16位颠倒代码 				       
						char char_new=0; 		

				        char_new |=  (char) ( (buffer[irow] >>15)&0x01 );  
				        char_new |=  (char) ( (buffer[irow] >>13)&0x02 );  
				        char_new |=  (char) ( (buffer[irow] >>11)&0x04 );  				        
				        char_new |=  (char) ( (buffer[irow] >>9) &0x08 );  				        		        
				        char_new |=  (char) ( (buffer[irow] >>7) &0x10 );  
				        char_new |=  (char) ( (buffer[irow] >>5) &0x20 );
				        char_new |=  (char) ( (buffer[irow] >>3) &0x40 );				        
				        char_new |=  (char) ( (buffer[irow] >>1) &0x80 );
				        				        
				        char_new |=  (char) ( (buffer[irow] <<15)&0x8000 );  
				        char_new |=  (char) ( (buffer[irow] <<13)&0x4000 );  
				        char_new |=  (char) ( (buffer[irow] <<11)&0x2000 );  				        
				        char_new |=  (char) ( (buffer[irow] <<9) &0x1000 );  				        		        
				        char_new |=  (char) ( (buffer[irow] <<7) &0x800 );  
				        char_new |=  (char) ( (buffer[irow] <<5) &0x400 );
				        char_new |=  (char) ( (buffer[irow] <<3) &0x200 );				        
				        char_new |=  (char) ( (buffer[irow] <<1) &0x100 );

				        
				        trans12[irow] =  char_new ; 
				        						
						trans12[irow+1] = buffer[irow+1] ;      //处理每列的第二个字节：不变化
				       						
				}			
				for (int irow = 0; irow < buffer.length; irow++)
				{
						buffer[irow] = trans12[irow] ;
						Debug.e(TAG, "====================trans12 buffer="+ buffer[irow] );
				}	
			}							
			//addbylk_2_1/3_end↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
			
			
			if( nmirrorimage1==1) ///一头镜像 
			{
				//char 类型站2个字节 
				char[] trans1 = new char [  buffer.length ]; //新建一个缓冲数据
				
				for (int irow = 0; irow < buffer.length; irow++)  //先复制 
				{
						 trans1[irow] = buffer[irow] ;
				}	
				//每列2个字节			
				for (int irow = 0; irow < (int)(buffer.length/2)-2; irow+=2)
				{

				        char char_new_left=0; 							
				        char char_new_right=0; 	
				        char char_new_tmp=0; 				        
	 			       	
				        char_new_left =	buffer[irow];			
						char_new_left |=(char) ( buffer[irow] & 0x1 ) ; //每列的第一个字节的前7位 复制到缓冲中  其他的不拷贝维持原样
						char_new_left |=(char) ( buffer[irow] & 0x2) ;  
						char_new_left |=(char) ( buffer[irow] & 0x4) ;  
						char_new_left |=(char) ( buffer[irow] & 0x8 ) ;  
						char_new_left |=(char) ( buffer[irow] & 0x10 ) ;  
						char_new_left |=(char) ( buffer[irow] & 0x20 ) ; 
						char_new_left |=(char) ( buffer[irow] & 0x40 ) ;  
					//	trans1[buffer.length-1-irow-1] |=(char) ( buffer[irow] & 0x100 ) ;  
	 		
				        
				      //  char_new_right =   trans1[buffer.length-1-irow-1] =  char_new;
						
				        char_new_right =(char)  buffer[buffer.length-1-irow-1]    ; 	
				        
				        char_new_right |=(char) ( buffer[buffer.length-1-irow-1] & 0x1 ) ; //每列的第一个字节的前7位 复制到缓冲中  其他的不拷贝维持原样
				        char_new_right |=(char) ( buffer[buffer.length-1-irow-1] & 0x2 ) ;  
				        char_new_right |=(char) ( buffer[buffer.length-1-irow-1] & 0x4 ) ;  
				        char_new_right |=(char) ( buffer[buffer.length-1-irow-1] & 0x8 ) ;  
				        char_new_right |=(char) ( buffer[buffer.length-1-irow-1] & 0x10 ) ;  
				        char_new_right |=(char) ( buffer[buffer.length-1-irow-1] & 0x20 ) ; 
				        char_new_right |=(char) ( buffer[buffer.length-1-irow-1] & 0x40 ) ; 
				        
				        
				        char_new_tmp =char_new_left;
				        char_new_left= char_new_right;				        
				        char_new_right = char_new_tmp;	
				        
				        char_new_right = (char)(  char_new_right | 0x00ff) ; //不变的像素全1供后面 或 处理
				        char_new_left =   (char)(  char_new_left | 0x00ff) ;				        
				        

						trans1[irow]  = (char) (trans1[irow] | 0xff00 ); //删除原有像素 否则会 与原有像素重合哦
						trans1[buffer.length-1-irow-1]  = (char) (trans1[buffer.length-1-irow-1] |0xff00) ;	//每列的第二个字节					        
						
								
						trans1[irow] &= char_new_left ;       //每列的第一个字节					
						trans1[buffer.length-1-irow-1] &= char_new_right ;	//每列的第二个字节					        
				        
						
				}			
				for (int irow = 0; irow < buffer.length; irow++) //把缓冲复制回原BUFF
				{
						buffer[irow] = trans1[irow] ;
				}	
			}//		
		}
			
		/* save print.bin to mounted usb device
		ArrayList<String> usbs = ConfigPath.getMountedUsb();
		if (usbs != null && usbs.size() > 0) {
			String path = usbs.get(0);
			path = path + "/print.bin";
			BinCreater.saveBin(path, buffer, mDataTask.get(mIndex).getInfo().mBytesPerHFeed*8*mDataTask.get(mIndex).getHeads());
		}
		*/
		// save print.bin to /mnt/sdcard/ folder
		// BinCreater.saveBin("/mnt/sdcard/print.bin", buffer, mDataTask.getInfo().mBytesPerHFeed*8*mDataTask.getHeads());
		
		Debug.e(TAG, "--->write data");
		FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_OUTPUT, buffer, buffer.length*2);
		last = SystemClock.currentThreadTimeMillis();
		Debug.e(TAG, "--->start print " + mRunning);
		FpgaGpioOperation.init();
		while(mRunning == true) {
			
			// FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_OUTPUT, buffer, buffer.length*2);
			
			int writable = FpgaGpioOperation.pollState();
			// writable = 1;
			if (writable == 0) { //timeout
			} else if (writable == -1) {
			} else {
				mInterval = SystemClock.currentThreadTimeMillis() - last;
				mHandler.removeMessages(MESSAGE_DATA_UPDATE);
				mNeedUpdate = false;
				
				buffer = mDataTask.get(mIndex).getPrintBuffer();
				if (buffer == null) {
					next();
					continue;
				}
				if (type == MessageType.MESSAGE_TYPE_HZK_16_8 ||  type == MessageType.MESSAGE_TYPE_HZK_16_16 || type == MessageType.MESSAGE_TYPE_9MM) {
		
				if(nDirection==1)
				{
				Debug.e(TAG, "===================="+ buffer.length );	
					char[] trans2 = new char [  buffer.length ];
					
					for (int irow = 0; irow < buffer.length-2; irow+=2)
					{
					//	Debug.e(TAG, "000============"+ buffer.length );	
							trans2[buffer.length-1-irow-1] = buffer[irow] ;
							trans2[buffer.length-1-irow-1+1] = buffer[irow+1] ;						
								
					}
					Debug.e(TAG, "11111===================="+ buffer.length );				
					for (int irow = 0; irow < buffer.length; irow++)
					{
							buffer[irow] = trans2[irow] ;
					}	
				}
				//addbylk_2_2/3_begin↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓			
				if(ninvert1==1 && ninvert2!=1 )
				{
				Debug.e(TAG, "===================="+ buffer.length );	
					char[] trans12 = new char [  buffer.length ];
					
					//每列2个字节
					for (int irow = 0; irow < buffer.length-2; irow+=2)
					{Debug.e(TAG, "====================trans12"+ trans12[irow] );
							  //处理每列的第一个字节：按位颠倒大小端					
							char char_new=0; 	
			
					        char_new |=  (char) ( (buffer[irow] >>6)&0x1 );  
					        char_new |=  (char) ( (buffer[irow] >>4)&0x2 );  
					        char_new |=  (char) ( (buffer[irow] >>2)&0x4 );  				        
					        char_new |=  (char) ( (buffer[irow] ) &0x8 );  				        		        
					        char_new |=  (char) ( (buffer[irow] <<2) &0x10 );  
					        char_new |=  (char) ( (buffer[irow] <<4) &0x20 );
					        char_new |=  (char) ( (buffer[irow] <<6) &0x40 );	
					        
					        char_new |=(char) ( buffer[irow] & 0x80  );							
					        char_new |=(char) ( buffer[irow] & 0x100  ) ; //每列的第一个字节的前7位 复制到缓冲中  其他的不拷贝维持原样
					        char_new |=(char) ( buffer[irow] & 0x200  ) ;  
					        char_new |=(char) ( buffer[irow] & 0x400  ) ;  
					        char_new |=(char) ( buffer[irow] & 0x800 ) ;  
					        char_new |=(char) ( buffer[irow] & 0x1000 ) ;  
					        char_new |=(char) ( buffer[irow] & 0x2000 ) ; 
					        char_new |=(char) ( buffer[irow] & 0x4000 ) ;  
					        char_new |=(char) ( buffer[irow] & 0x8000 ) ;  					        
					        
					        trans12[irow] =  char_new ; 				        						
							trans12[irow+1] = buffer[irow+1] ;      //处理每列的第二个字节：不变化			 						
					}			
					for (int irow = 0; irow < buffer.length; irow++)
					{
							buffer[irow] = trans12[irow] ;
							Debug.e(TAG, "====================trans12 buffer="+ buffer[irow] );
					}	
				}
							
				if(ninvert1!=1 && ninvert2==1)
				{
				Debug.e(TAG, "===================="+ buffer.length );	
					char[] trans12 = new char [  buffer.length ];
					
					//每列2个字节
					for (int irow = 0; irow < buffer.length-2; irow+=2)
					{Debug.e(TAG, "====================trans12"+ trans12[irow] );
							  //处理每列的第一个字节：按位颠倒大小端											
				      //  16位颠倒代码 				       
							char char_new=0; 		

					        char_new |=(char) ( buffer[irow] & 0x1  ) ; 
					        char_new |=(char) ( buffer[irow] & 0x2) ;  
					        char_new |=(char) ( buffer[irow] & 0x4 ) ;  
					        char_new |=(char) ( buffer[irow] & 0x8 ) ;  
					        char_new |=(char) ( buffer[irow] & 0x10) ;  
					        char_new |=(char) ( buffer[irow] & 0x20 ) ; 
					        char_new |=(char) ( buffer[irow] & 0x40 ) ;  
					        
					        char_new |=  (char) ( (buffer[irow] >>6)&0x100 );  
					        char_new |=  (char) ( (buffer[irow] >>4)&0x200 );  
					        char_new |=  (char) ( (buffer[irow] >>2)&0x400 );  				        
					        char_new |=  (char) ( (buffer[irow] ) &0x800 );  				        		        
					        char_new |=  (char) ( (buffer[irow] <<2) &0x1000 );  
					        char_new |=  (char) ( (buffer[irow] <<4) &0x2000 );
					        char_new |=  (char) ( (buffer[irow] <<6) &0x4000 );	

					        
					        trans12[irow] =  char_new ; 
					        						
							trans12[irow+1] = buffer[irow+1] ;      //处理每列的第二个字节：不变化
					       						
					}			
					for (int irow = 0; irow < buffer.length; irow++)
					{
							buffer[irow] = trans12[irow] ;
							Debug.e(TAG, "====================trans12 buffer="+ buffer[irow] );
					}	
				}
							
				if(ninvert1==1 && ninvert2==1)// 功能：16高的头倒置 触发条件： 15.1 头倒置 16.2 头倒置 同时打开
				{
				Debug.e(TAG, "===================="+ buffer.length );	
					char[] trans12 = new char [  buffer.length ];
					
					//每列2个字节
					for (int irow = 0; irow < buffer.length-2; irow+=2)
					{Debug.e(TAG, "====================trans12"+ trans12[irow] );
			
				      //   16位颠倒代码 				       
							char char_new=0; 		

					        char_new |=  (char) ( (buffer[irow] >>15)&0x01 );  
					        char_new |=  (char) ( (buffer[irow] >>13)&0x02 );  
					        char_new |=  (char) ( (buffer[irow] >>11)&0x04 );  				        
					        char_new |=  (char) ( (buffer[irow] >>9) &0x08 );  				        		        
					        char_new |=  (char) ( (buffer[irow] >>7) &0x10 );  
					        char_new |=  (char) ( (buffer[irow] >>5) &0x20 );
					        char_new |=  (char) ( (buffer[irow] >>3) &0x40 );				        
					        char_new |=  (char) ( (buffer[irow] >>1) &0x80 );
					        				        
					        char_new |=  (char) ( (buffer[irow] <<15)&0x8000 );  
					        char_new |=  (char) ( (buffer[irow] <<13)&0x4000 );  
					        char_new |=  (char) ( (buffer[irow] <<11)&0x2000 );  				        
					        char_new |=  (char) ( (buffer[irow] <<9) &0x1000 );  				        		        
					        char_new |=  (char) ( (buffer[irow] <<7) &0x800 );  
					        char_new |=  (char) ( (buffer[irow] <<5) &0x400 );
					        char_new |=  (char) ( (buffer[irow] <<3) &0x200 );				        
					        char_new |=  (char) ( (buffer[irow] <<1) &0x100 );

					        
					        trans12[irow] =  char_new ; 
					        						
							trans12[irow+1] = buffer[irow+1] ;      //处理每列的第二个字节：不变化
					       						
					}			
					for (int irow = 0; irow < buffer.length; irow++)
					{
							buffer[irow] = trans12[irow] ;
							Debug.e(TAG, "====================trans12 buffer="+ buffer[irow] );
					}	
				}
				}
				//addbylk_2_2/3_end↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑//////////////////↑↑↑↑
	
				if (!mDataTask.get(mIndex).isReady) {
					mRunning = false;
					if (mCallback != null) {
						mCallback.OnFinished(CODE_BARFILE_END);
					}
					break;
				}
				// Debug.d(TAG, "===>buffer size="+buffer.length);
				FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_OUTPUT, buffer, buffer.length*2);
				last = SystemClock.currentThreadTimeMillis();
				countDown();
				
				mInkListener.onCountChanged();
				
		//addbylk170918		
				mScheduler.schedule();
				if (mCallback != null) {
					mCallback.onComplete();
				}
				next();
			}
			
			if(mNeedUpdate == true) {
				mHandler.removeMessages(MESSAGE_DATA_UPDATE);
				//在此处发生打印数据，同时

				buffer = mDataTask.get(mIndex).getPrintBuffer();
				if (type == MessageType.MESSAGE_TYPE_HZK_16_8 ||  type == MessageType.MESSAGE_TYPE_HZK_16_16 || type == MessageType.MESSAGE_TYPE_9MM) {
		
				if(nDirection==1)
				{
				Debug.e(TAG, "333===================="+ buffer.length );	
					char[] trans3 = new char [  buffer.length ];
					
					for (int irow = 0; irow < buffer.length-2; irow+=2)
					{
					//	Debug.e(TAG, "000============"+ buffer.length );	
							trans3[buffer.length-1-irow-1] = buffer[irow] ;
							trans3[buffer.length-1-irow-1+1] = buffer[irow+1] ;						
								
					}
					Debug.e(TAG, "444===================="+ buffer.length );				
					for (int irow = 0; irow < buffer.length; irow++)
					{
							buffer[irow] = trans3[irow] ;
					}	
				}
							
				//addbylk_2_2/3_begin↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓			
				if(ninvert1==1 && ninvert2!=1 )
				{
				Debug.e(TAG, "===================="+ buffer.length );	
					char[] trans12 = new char [  buffer.length ];
					
					//每列2个字节
					for (int irow = 0; irow < buffer.length-2; irow+=2)
					{Debug.e(TAG, "====================trans12"+ trans12[irow] );
							  //处理每列的第一个字节：按位颠倒大小端					
							char char_new=0; 	
			
					        char_new |=  (char) ( (buffer[irow] >>6)&0x1 );  
					        char_new |=  (char) ( (buffer[irow] >>4)&0x2 );  
					        char_new |=  (char) ( (buffer[irow] >>2)&0x4 );  				        
					        char_new |=  (char) ( (buffer[irow] ) &0x8 );  				        		        
					        char_new |=  (char) ( (buffer[irow] <<2) &0x10 );  
					        char_new |=  (char) ( (buffer[irow] <<4) &0x20 );
					        char_new |=  (char) ( (buffer[irow] <<6) &0x40 );	
					        
					        char_new |=(char) ( buffer[irow] & 0x80  );							
					        char_new |=(char) ( buffer[irow] & 0x100  ) ; //每列的第一个字节的前7位 复制到缓冲中  其他的不拷贝维持原样
					        char_new |=(char) ( buffer[irow] & 0x200  ) ;  
					        char_new |=(char) ( buffer[irow] & 0x400  ) ;  
					        char_new |=(char) ( buffer[irow] & 0x800 ) ;  
					        char_new |=(char) ( buffer[irow] & 0x1000 ) ;  
					        char_new |=(char) ( buffer[irow] & 0x2000 ) ; 
					        char_new |=(char) ( buffer[irow] & 0x4000 ) ;  
					        char_new |=(char) ( buffer[irow] & 0x8000 ) ;  					        
					        
					        trans12[irow] =  char_new ; 				        						
							trans12[irow+1] = buffer[irow+1] ;      //处理每列的第二个字节：不变化			 						
					}			
					for (int irow = 0; irow < buffer.length; irow++)
					{
							buffer[irow] = trans12[irow] ;
							Debug.e(TAG, "====================trans12 buffer="+ buffer[irow] );
					}	
				}
							
				if(ninvert1!=1 && ninvert2==1)
				{
				Debug.e(TAG, "===================="+ buffer.length );	
					char[] trans12 = new char [  buffer.length ];
					
					//每列2个字节
					for (int irow = 0; irow < buffer.length-2; irow+=2)
					{Debug.e(TAG, "====================trans12"+ trans12[irow] );
							  //处理每列的第一个字节：按位颠倒大小端											
				      //  16位颠倒代码 				       
							char char_new=0; 		

					        char_new |=(char) ( buffer[irow] & 0x1  ) ; 
					        char_new |=(char) ( buffer[irow] & 0x2) ;  
					        char_new |=(char) ( buffer[irow] & 0x4 ) ;  
					        char_new |=(char) ( buffer[irow] & 0x8 ) ;  
					        char_new |=(char) ( buffer[irow] & 0x10) ;  
					        char_new |=(char) ( buffer[irow] & 0x20 ) ; 
					        char_new |=(char) ( buffer[irow] & 0x40 ) ;  
					        
					        char_new |=  (char) ( (buffer[irow] >>6)&0x100 );  
					        char_new |=  (char) ( (buffer[irow] >>4)&0x200 );  
					        char_new |=  (char) ( (buffer[irow] >>2)&0x400 );  				        
					        char_new |=  (char) ( (buffer[irow] ) &0x800 );  				        		        
					        char_new |=  (char) ( (buffer[irow] <<2) &0x1000 );  
					        char_new |=  (char) ( (buffer[irow] <<4) &0x2000 );
					        char_new |=  (char) ( (buffer[irow] <<6) &0x4000 );	

					        
					        trans12[irow] =  char_new ; 
					        						
							trans12[irow+1] = buffer[irow+1] ;      //处理每列的第二个字节：不变化
					       						
					}			
					for (int irow = 0; irow < buffer.length; irow++)
					{
							buffer[irow] = trans12[irow] ;
							Debug.e(TAG, "====================trans12 buffer="+ buffer[irow] );
					}	
				}
							
				if(ninvert1==1 && ninvert2==1)// 功能：16高的头倒置 触发条件： 15.1 头倒置 16.2 头倒置 同时打开
				{
				Debug.e(TAG, "===================="+ buffer.length );	
					char[] trans12 = new char [  buffer.length ];
					
					//每列2个字节
					for (int irow = 0; irow < buffer.length-2; irow+=2)
					{Debug.e(TAG, "====================trans12"+ trans12[irow] );
			
				      //   16位颠倒代码 				       
							char char_new=0; 		

					        char_new |=  (char) ( (buffer[irow] >>15)&0x01 );  
					        char_new |=  (char) ( (buffer[irow] >>13)&0x02 );  
					        char_new |=  (char) ( (buffer[irow] >>11)&0x04 );  				        
					        char_new |=  (char) ( (buffer[irow] >>9) &0x08 );  				        		        
					        char_new |=  (char) ( (buffer[irow] >>7) &0x10 );  
					        char_new |=  (char) ( (buffer[irow] >>5) &0x20 );
					        char_new |=  (char) ( (buffer[irow] >>3) &0x40 );				        
					        char_new |=  (char) ( (buffer[irow] >>1) &0x80 );
					        				        
					        char_new |=  (char) ( (buffer[irow] <<15)&0x8000 );  
					        char_new |=  (char) ( (buffer[irow] <<13)&0x4000 );  
					        char_new |=  (char) ( (buffer[irow] <<11)&0x2000 );  				        
					        char_new |=  (char) ( (buffer[irow] <<9) &0x1000 );  				        		        
					        char_new |=  (char) ( (buffer[irow] <<7) &0x800 );  
					        char_new |=  (char) ( (buffer[irow] <<5) &0x400 );
					        char_new |=  (char) ( (buffer[irow] <<3) &0x200 );				        
					        char_new |=  (char) ( (buffer[irow] <<1) &0x100 );

					        
					        trans12[irow] =  char_new ; 
					        						
							trans12[irow+1] = buffer[irow+1] ;      //处理每列的第二个字节：不变化
					       						
					}			
					for (int irow = 0; irow < buffer.length; irow++)
					{
							buffer[irow] = trans12[irow] ;
							Debug.e(TAG, "====================trans12 buffer="+ buffer[irow] );
					}	
				}
				}
				//addbylk_2_3/3_end↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
				Debug.d(TAG, "===>buffer size="+buffer.length);
				FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_OUTPUT, buffer, buffer.length*2);
				mHandler.sendEmptyMessageDelayed(MESSAGE_DATA_UPDATE, MESSAGE_EXCEED_TIMEOUT);
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Debug.d(TAG, "===>kernel buffer empty, fill it");
			//TO-DO list 下面需要把打印数据下发
		}
		rollback();
		
	}

	private void next() {
		mIndex++;
		if (mIndex >= mDataTask.size()) {
			mIndex = 0;
		}

	}
	
	public void purge(final Context context) {
		ThreadPoolManager.mThreads.execute(new Runnable() {
			
			@Override
			public void run() {
				DataTask task = new DataTask(context, null);
				Debug.e(TAG, "--->task: " + task);
				char[] buffer = task.preparePurgeBuffer();
				Debug.e(TAG, "--->buffer len: " + buffer.length);
				FpgaGpioOperation.updateSettings(context, task, FpgaGpioOperation.SETTING_TYPE_PURGE1);
				FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_PURGE, buffer, buffer.length*2);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				FpgaGpioOperation.updateSettings(context, task, FpgaGpioOperation.SETTING_TYPE_PURGE2);
				FpgaGpioOperation.writeData(FpgaGpioOperation.FPGA_STATE_PURGE, buffer, buffer.length*2);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				FpgaGpioOperation.clean();
			}
		});
	}
	
	public boolean isRunning() {
		return mRunning;
	}
	
	public boolean launch(Context ctx) {
		mRunning = true;

		DataTransferThread thread = getInstance();
		Debug.d(TAG, "--->thread : " + thread.isRunning());
		if (!isBufferReady || mDataTask == null) {
			return false;
		}
		
		if (mScheduler == null) {
			mScheduler = new RfidScheduler(mContext);
		}
		
		SystemConfigFile configFile = SystemConfigFile.getInstance(ctx);
		mScheduler.init();
		int heads = configFile.getHeads();
		/**如果是4合2的打印头，需要修改为4头*/
		heads = configFile.getParam(SystemConfigFile.INDEX_SPECIFY_HEADS) > 0 ? configFile.getParam(42) : heads;
		for (int i = 0; i < heads; i++) {
			mScheduler.add(new RfidTask(i, mContext));
		}
		mScheduler.load();
		
		thread.start();
		return true;
	}
	
	public void finish() {
		mRunning = false;
		
		DataTransferThread t = mInstance;
		mInstance = null;
		mHandler.removeMessages(MESSAGE_DATA_UPDATE);
		if (t != null) {
			t.interrupt();
		}
		if (mScheduler == null) {
			return;
		}
		mScheduler.doAfterPrint();
	}
	
	public void setOnInkChangeListener(InkLevelListener listener) {
		mInkListener = listener;
	}
	
	
	public static final int MESSAGE_DATA_UPDATE = 1;
	
	public Handler mHandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_DATA_UPDATE:
					mNeedUpdate = true;
					break;
			}
		}
	};
	
//	public void initDataBuffer(Context context, MessageTask task) {
//		if (mDataTask == null) {
//			mDataTask = new DataTask(context, task);
//		} else {
//			mDataTask.setTask(task);
//		}
//		Debug.d(TAG, "--->prepare buffer");
//
//		isBufferReady = mDataTask.prepareBackgroudBuffer();
//	}

	public void initDataBuffer(Context context, List<MessageTask> task) {
		if (mDataTask == null) {
			mDataTask = new ArrayList<DataTask>();
		}
		mIndex = 0;
		mDataTask.clear();
		for (MessageTask t : task) {
			DataTask data = new DataTask(mContext, t);
			mDataTask.add(data);
		}
		Debug.d(TAG, "--->prepare buffer");

		for (DataTask tk : mDataTask) {
			isBufferReady |= tk.prepareBackgroudBuffer();
		}
	}


	public List<DataTask> getData() {
		return mDataTask;
	}

	public DataTask getCurData() {
		return mDataTask.get(mIndex);
	}

	public int getIndex() {
		return mIndex;
	}
																																																																																																																
	public void setDotCount(List<MessageTask> messages) {
		for (int i = 0; i < mDataTask.size(); i++) {
			DataTask t = mDataTask.get(i);
			if (messages.size() <= i) {
				break;
			}
			t.setDots(messages.get(i).getDots());
		}
		mcountdown = getInkThreshold();
	}
	
	public int getDotCount(DataTask task) {
		if (task == null) {
			return 1;
		}
			
		return task.getDots();
	}
	
	/**
	 * 倒计数，当计数倒零时表示墨水量需要减1，同时倒计数回归
	 * @return true 墨水量需要减1； false 墨水量不变
	 */
	private boolean countDown() {
		mcountdown--;
		if (mcountdown <= 0) {
			// 赋初值
			mcountdown = getInkThreshold();
			mInkListener.onInkLevelDown();
			return true;
		}
		return false;
	}
	
	public int getCount() {
		
		return mcountdown;
	}
	
	/**
	 * 通过dot count计算RFID减1的阀值
	 * @return
	 */
	public int getInkThreshold() {
		int bold = 1;
		if (mDataTask.size() == 0) {
			return 0;
		} else if (mIndex >= mDataTask.size()) {
			mIndex = 0;
		}
		if (getDotCount(mDataTask.get(mIndex)) <= 0) {
			return 1;
		}
		SystemConfigFile config = SystemConfigFile.getInstance(mContext);
		if (config.getParam(2) <= 0) {
			bold = 1;
		} else {
			bold = config.getParam(2)/150;
		}
		return Configs.DOTS_PER_PRINT*getHeads()/(getDotCount(mDataTask.get(mIndex)) * bold);
	}
	
	public int getHeads() {

		if (mDataTask != null && mDataTask.size() > 0) {
			return mDataTask.get(0).getHeads();
		}
		return 1;
	}
	/**
	 * 打印間隔0~100ms（每秒鐘打印 > 20次），爲高速打印，每個打印間隔只執行1步操作
	 * 打印間隔100~200ms（每秒鐘打印 > 20次），爲高速打印，每個打印間隔只執行2步操作
	 * 打印間隔200~500ms（每秒鐘打印 > 20次），爲高速打印，每個打印間隔只執行4步操作
	 * 打印間隔500~1000ms（每秒鐘打印 > 20次），爲高速打印，每個打印間隔只執行8步操作
	 * @return
	 */
	public static long getInterval() {
		if (mInterval >= 1000) {
			return 8;
		} else if (mInterval >= 500) {
			return 4;
		} else if (mInterval >= 200) {
			return 2;
		} else {
			return 1;
		}
	}

	public void refreshCount() {
		mcountdown = getInkThreshold();
	}
	
	private Callback mCallback;
	
	public void setCallback(Callback callback) {
		mCallback = callback;
	}
	
	
	public interface Callback {
		/**
		 * 整個任務打印完成
		 */
		public void OnFinished(int code);
		/**
		 * 一個任務打印完成
		 */
		public void onComplete();
	}
	
	public static final int CODE_BARFILE_END = 1;
	public static final int CODE_NO_BARFILE = 2;
	
//	private char[] getPrintBuffer() {
//		char[] buffer;
//		int htype = getHeads();
//		// specific process for 9mm header
//		if (htype == MessageType.MESSAGE_TYPE_9MM) {
//			int columns = mDataTask.getBufferColumns();
//			int h = mDataTask.getBufferHeightFeed();
//			char[] b1 = mDataTask.getPrintBuffer();
//			char[] b2 = mDataTask.getPrintBuffer();
//			char[] b3 = mDataTask.getPrintBuffer();
//			char[] b4 = mDataTask.getPrintBuffer();
//			char[] b5 = mDataTask.getPrintBuffer();
//			char[] b6 = mDataTask.getPrintBuffer();
//			buffer = new char[columns * h * 6];
//			for (int i = 0; i < columns; i++) {
//				System.arraycopy(b1, i * h, buffer, i * h *6, h);
//				System.arraycopy(b2, i * h, buffer, i * h * (6 + 1), h);
//				System.arraycopy(b3, i * h, buffer, i * h * (6 + 2), h);
//				System.arraycopy(b4, i * h, buffer, i * h * (6 + 3), h);
//				System.arraycopy(b5, i * h, buffer, i * h * (6 + 4), h);
//				System.arraycopy(b6, i * h, buffer, i * h * (6 + 5), h);
//			}
//		} else {
//			buffer = mDataTask.getPrintBuffer();
//		}
//		return buffer;
//	}
	
	private void rollback() {
		
		if (mDataTask == null) {
			return;
		}
		for (DataTask task : mDataTask) {
			for (BaseObject object : task.getObjList()) {
				if (object instanceof CounterObject) {
					((CounterObject) object).rollback();
				}
			}
		}
	}
}

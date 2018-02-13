package com.industry.printer.Socket_Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.industry.printer.Socket_Control_Activity;
import com.printer.corelib.Debug;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class ClientThread extends Thread {
	
	private final String TAG = ClientThread.class.getSimpleName(); 
	
	private OutputStream outputStream = null;
	private InputStream inputStream = null;
	private Socket socket;
	private SocketAddress socketAddress;
	public static Handler childHandler;
	private boolean key = true;
	PrintInterface printClass;
	private RxThread rxThread;

	public ClientThread(PrintInterface printClass) {

		this.printClass = printClass;
	}

	/**
	 * connect
	 */
	void connect() {
		key = true;
		socketAddress = new InetSocketAddress(Socket_Control_Activity.sIP
				.toString(), Integer.parseInt(Socket_Control_Activity.sPORT));
		socket = new Socket();

		try {

			//socket.connect(socketAddress, 5000);
			socket=new Socket(Socket_Control_Activity.sIP.toString(), Integer.parseInt(Socket_Control_Activity.sPORT));
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();

//			printClass.printf("���ӳɹ�");
			
			rxThread = new RxThread();
			rxThread.start();

		} catch (IOException e) {
			e.printStackTrace();
//			printClass.printf("�޷����ӵ�������");
		} catch (NumberFormatException e) {

		}

	}

	void initChildHandler() {

		Looper.prepare();

		childHandler = new Handler() {
			/**
			 * 
			 */
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:

					try {
						outputStream.write(((String) (msg.obj)).getBytes());
						outputStream.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;

				case 1:

					key = false;
					try {
						inputStream.close();
						outputStream.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					childHandler.getLooper().quit();
					


					break;

				default:
					break;
				}

			}
		};

		Looper.loop();

	}

	public void run() {
		connect();
		initChildHandler();
//		printClass.printf("��������Ͽ�����");

	}

	public class RxThread extends Thread {

		public void run() {

			//printClass.printf("���������߳�");
			byte[] buffer = new byte[1024];

			while (key) {

				try {
					int readSize = inputStream.read(buffer);
					if (readSize > 0) {
						String str = new String(buffer, 0, readSize);

						Log.d("Message:", str);
						printClass.printf("<< " + str);

					} else {
						Debug.d(TAG, "--->no input, sleep");
						Thread.sleep(3000);
//						inputStream.close();
//						printClass.printf("��������Ͽ�����");
//						break;

					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					Debug.d(TAG, "--->e: " + e.getMessage());
				}
			}
			
			try {
				if (socket.isConnected())
					socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			
			

		}

	}

}
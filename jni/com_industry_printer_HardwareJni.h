/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_industry_printer_UsbSerial */

#ifndef _Included_com_industry_printer_UsbSerial
#define _Included_com_industry_printer_UsbSerial
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_industry_printer_UsbSerial
 * Method:    open
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_industry_printer_UsbSerial_open
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_industry_printer_UsbSerial
 * Method:    setBaudrate
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_industry_printer_UsbSerial_setBaudrate
  (JNIEnv *, jclass, jint fd, jint speed);

/*
 * Class:     com_industry_printer_UsbSerial
 * Method:    close
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_industry_printer_UsbSerial_close
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_industry_printer_UsbSerial
 * Method:    write
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_industry_printer_UsbSerial_write
  (JNIEnv *, jclass, jint fd, jshortArray, jint len);

/*
 * Class:     com_industry_printer_UsbSerial
 * Method:    read
 * Signature: ()I
 */
JNIEXPORT jbyteArray JNICALL Java_com_industry_printer_UsbSerial_read
  (JNIEnv *, jclass, jint fd, jint len);

/*
 * Class:     com_industry_printer_UsbSerial
 * Method:    set_options
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_industry_printer_UsbSerial_set_options
  (JNIEnv *, jobject , jint , jint , jint , jint );



/*********************************************
 * GPIO操作接口定义
 *********************************************/

/**
 * Class:     com_industry_printer_GPIO
 * Method:    open
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_industry_printer_GPIO_open
	(JNIEnv *env, jclass arg, jstring dev);

/**
 * Class:     com_industry_printer_GPIO
 * Method:    write
 * Signature: (I[CI)I
 */
JNIEXPORT jint JNICALL Java_com_industry_printer_GPIO_write
	(JNIEnv *env, jclass arg, jint fd, jcharArray buff, jint count);

/**
 * Class:     com_industry_printer_GPIO
 * Method:    ioctl
 * Signature: (IIJ)I
 */
JNIEXPORT jint JNICALL Java_com_industry_printer_GPIO_ioctl
	(JNIEnv *env, jclass arg, jint fd, jint cmd, jlong arg1);

/**
 * Class:     com_industry_printer_GPIO
 * Method:    poll
 * Signature: (IIJ)I
 */
JNIEXPORT jint JNICALL Java_com_industry_printer_GPIO_poll
	(JNIEnv *env, jclass arg, jint fd);


/**
 * Class:     com_industry_printer_GPIO
 * Method:    close
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_industry_printer_GPIO_close
	(JNIEnv *env, jclass arg, jint fd);

/*********************************************
 * RFID操作接口定义
 *********************************************/

/**
 * Class:     com_industry_printer_RFID
 * Method:    open
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_industry_printer_RFID_open
  (JNIEnv *env, jclass arg, jstring dev);

#ifdef __cplusplus
}
#endif
#endif

#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <termios.h>
#include <errno.h>
#include <string.h>
#include <jni.h>
#include <utils/Log.h>
#include "com_industry_printer_HardwareJni.h"


/**
 * Fpga Gpio operation APIs
 **/
JNIEXPORT jint JNICALL Java_com_industry_printer_GPIO_open
	(JNIEnv *env, jclass arg, jstring dev)
{
	int ret=-1;
	ALOGD("===>open gpio\n");
	char *dev_utf = (*env)->GetStringUTFChars(env, dev, JNI_FALSE);
	if(dev_utf == NULL)
	{
		return -1;
	}
	ret = open(dev_utf, O_RDWR);
	if(ret < 0) {
		ALOGD("***** gpio open fail, err=%s\n", strerror(errno));
	}
	(*env)->ReleaseStringUTFChars(env, dev, dev_utf);
	return ret;
}

JNIEXPORT jint JNICALL Java_com_industry_printer_GPIO_write
	(JNIEnv *env, jclass arg, jint fd, jcharArray buff, jint count)
{
	int i,ret;
	jchar *buf_utf = (*env)->GetCharArrayElements(env, buff, NULL);
	ALOGD("=====>gpio write %d\n",sizeof(jchar));
	if(fd <= 0)
		return 0;
	ret = write(fd, buf_utf, count);
	(*env)->ReleaseCharArrayElements(env, buff, buf_utf, 0);
	return ret;
}


JNIEXPORT jint JNICALL Java_com_industry_printer_GPIO_ioctl
	(JNIEnv *env, jclass arg, jint fd, jint cmd, jlong arg1)
{
	int i,ret;

	if(fd <= 0)
		return 0;
	ret = ioctl(fd, cmd, arg1);
	return ret;
}


JNIEXPORT jint JNICALL Java_com_industry_printer_GPIO_close
	(JNIEnv *env, jclass arg, jint fd)
{
	int ret=-1;
	ret = close(fd);
	if(ret < 0) {
		ALOGD("***** gpio close fail\n");
	}
	return ret;
}

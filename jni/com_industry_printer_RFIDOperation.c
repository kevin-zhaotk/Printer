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
#include <com_industry_printer_HardwareJni.h>

#define JNI_TAG "RFID_jni"


JNIEXPORT jint JNICALL Java_com_industry_printer_RFID_open
  (JNIEnv *env, jclass arg, jstring dev)
{
	int ret;
	char *dev_utf = (*env)->GetStringUTFChars(env, dev, JNI_FALSE);
	ret = open(dev_utf, O_RDWR|O_NOCTTY|O_NONBLOCK);

	if( ret == -1)
	{
		ALOGD("can not open Serial port");
	}
	else
	{
		ALOGD("Serial open success");
	}
	(*env)->ReleaseStringUTFChars(env, dev, dev_utf);
	return ret;
}


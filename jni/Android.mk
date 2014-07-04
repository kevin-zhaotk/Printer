LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := libUsbSerial_jni
LOCAL_MODULE_TAGS := optional
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -lm -llog -lcutils
LOCAL_SRC_FILES := com_industry_printer_UsbSerial.c
include $(BUILD_SHARED_LIBRARY)

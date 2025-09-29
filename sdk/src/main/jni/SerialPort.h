#include <jni.h>

#ifndef _Included_com_weloo_serialdemo_tools_SerialPort
#define _Included_com_weloo_serialdemo_tools_SerialPort
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_weloo_serialdemo_tools_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_com_weloo_serialdemo_tools_SerialPort_open
  (JNIEnv *, jclass, jstring, jint, jint);

/*
 * Class:     com_weloo_serialdemo_tools_SerialPort
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_weloo_serialdemo_tools_SerialPort_close
  (JNIEnv *, jobject);


JNIEXPORT jint JNICALL Java_com_weloo_serialdemo_tools_SerialPort_setRTSNative
        (JNIEnv *env, jclass thiz, jobject fdObj, jint enable);

/*
 * Class:     com_weloo_serialport_lib_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_com_weloo_serialport_lib_SerialPort_open
        (JNIEnv *, jclass, jstring, jint, jint);

/*
 * Class:     com_weloo_serialport_lib_SerialPort
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_weloo_serialport_lib_SerialPort_close
        (JNIEnv *, jobject, jobject fdObj);
/*JNIEXPORT void JNICALL Java_com_weloo_serialport_lib_SerialPort_close
        (JNIEnv *, jobject, );*/

#ifdef __cplusplus
}
#endif
#endif

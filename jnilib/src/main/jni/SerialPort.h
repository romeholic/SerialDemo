#include <jni.h>

#ifndef _Included_com_welo_serialdemo_tools_SerialPort
#define _Included_com_welo_serialdemo_tools_SerialPort
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_welo_serialdemo_tools_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_android_1serialport_1api_SerialPort_open
  (JNIEnv *, jclass, jstring, jint, jint);

/*
 * Class:     com_welo_serialdemo_tools_SerialPort
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_android_1serialport_1api_SerialPort_close
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif

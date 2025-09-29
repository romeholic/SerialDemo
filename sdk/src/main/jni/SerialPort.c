#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>

#include "SerialPort.h"

#include "android/log.h"
static const char *TAG="serial_port";
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

static speed_t getBaudrate(jint baudrate)
{
	switch(baudrate) {
	case 0: return B0;
	case 50: return B50;
	case 75: return B75;
	case 110: return B110;
	case 134: return B134;
	case 150: return B150;
	case 200: return B200;
	case 300: return B300;
	case 600: return B600;
	case 1200: return B1200;
	case 1800: return B1800;
	case 2400: return B2400;
	case 4800: return B4800;
	case 9600: return B9600;
	case 19200: return B19200;
	case 38400: return B38400;
	case 57600: return B57600;
	case 115200: return B115200;
	case 230400: return B230400;
	case 460800: return B460800;
	case 500000: return B500000;
	case 576000: return B576000;
	case 921600: return B921600;
	case 1000000: return B1000000;
	case 1152000: return B1152000;
	case 1500000: return B1500000;
	case 2000000: return B2000000;
	case 2500000: return B2500000;
	case 3000000: return B3000000;
	case 3500000: return B3500000;
	case 4000000: return B4000000;
	default: return -1;
	}
}

/*
 * Class:     com_weloo_serialdemo_tools_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_com_weloo_serialdemo_tools_SerialPort_open
  (JNIEnv *env, jclass thiz, jstring path, jint baudrate, jint flags)
{
	int fd;
	speed_t speed;
	jobject mFileDescriptor;

	/* Check arguments */
	{
		speed = getBaudrate(baudrate);
		if (speed == -1) {
			/* TODO: throw an exception */
			LOGE("Invalid baudrate");
			return NULL;
		}
	}

	/* Opening device */
	{
		jboolean iscopy;
		const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
		LOGD("Opening serial port %s with flags 0x%x", path_utf, O_RDWR | flags);
		fd = open(path_utf, O_RDWR | flags);
		LOGD("open() fd = %d", fd);
		(*env)->ReleaseStringUTFChars(env, path, path_utf);
		if (fd == -1)
		{
			/* Throw an exception */
			LOGE("Cannot open port");
			/* TODO: throw an exception */
			return NULL;
		}
	}

	/* Configure device */
	{
		struct termios cfg;
		LOGD("Configuring serial port");
		if (tcgetattr(fd, &cfg))
		{
			LOGE("tcgetattr() failed");
			close(fd);
			/* TODO: throw an exception */
			return NULL;
		}

		cfmakeraw(&cfg);
		cfsetispeed(&cfg, speed);
		cfsetospeed(&cfg, speed);

		if (tcsetattr(fd, TCSANOW, &cfg))
		{
			LOGE("tcsetattr() failed");
			close(fd);
			/* TODO: throw an exception */
			return NULL;
		}
	}

	/* Create a corresponding file descriptor */
	{
		jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
		jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor, "<init>", "()V");
		jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
		mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
		(*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint)fd);
	}

	return mFileDescriptor;
}

/*
 * Class:     com_weloo_serialdemo_tools_SerialPort
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_weloo_serialdemo_tools_SerialPort_close
  (JNIEnv *env, jobject thiz)
{
	jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
	jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

	jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
	jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");

	jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
	jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

	LOGD("close(fd = %d)", descriptor);
	close(descriptor);
}


JNIEXPORT jint JNICALL Java_com_weloo_serialdemo_tools_SerialPort_setRTSNative
        (JNIEnv *env, jclass thiz, jobject fdObj, jint enable) {
    // 获取文件描述符对应的类（C语言中需用 (*env)-> 调用JNI函数）
    jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
    if (cFileDescriptor == NULL) {
        LOGE("setRTSNative: 找不到 FileDescriptor 类");
        return -1;
    }

    // 获取 descriptor 字段的 ID
    jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
    if (descriptorID == NULL) {
        LOGE("setRTSNative: 找不到 descriptor 字段");
        (*env)->DeleteLocalRef(env, cFileDescriptor); // 释放局部引用
        return -1;
    }

    // 获取文件描述符的值
    int fd = (*env)->GetIntField(env, fdObj, descriptorID);
    if (fd == -1) {
        LOGE("setRTSNative: 无效的文件描述符");
        (*env)->DeleteLocalRef(env, cFileDescriptor);
        return -1;
    }

    // 读取当前引脚状态
    int status;
    if (ioctl(fd, TIOCMGET, &status) == -1) {
        LOGE("setRTSNative: TIOCMGET 失败: %s", strerror(errno));
        (*env)->DeleteLocalRef(env, cFileDescriptor);
        return -1;
    }

    // 设置 RTS 状态（根据硬件调试结果确认是否需要反向操作）
    if (enable) {
        status |= TIOCM_RTS;  // 拉低 RTS（若无效，尝试改为 status &= ~TIOCM_RTS）
    } else {
        status &= ~TIOCM_RTS; // 拉高 RTS（若无效，尝试改为 status |= TIOCM_RTS）
    }

    // 应用新的引脚状态
    if (ioctl(fd, TIOCMSET, &status) == -1) {
        LOGE("setRTSNative: TIOCMSET 失败: %s", strerror(errno));
        (*env)->DeleteLocalRef(env, cFileDescriptor);
        return -1;
    }

    // 释放局部引用
    (*env)->DeleteLocalRef(env, cFileDescriptor);
    return 0; // 成功
}


/*
 * Class:     com_weloo_serialport_lib_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_com_weloo_serialport_lib_SerialPort_open
        (JNIEnv *env, jclass thiz, jstring path, jint baudrate, jint flags)
{
    int fd;
    speed_t speed;
    jobject mFileDescriptor;

    /* Check arguments */
    {
        speed = getBaudrate(baudrate);
        if (speed == -1) {
            /* TODO: throw an exception */
            LOGE("Invalid baudrate");
            return NULL;
        }
    }

    /* Opening device */
    {
        jboolean iscopy;
        const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
        LOGD("Opening serial port %s with flags 0x%x", path_utf, O_RDWR | flags);
        fd = open(path_utf, O_RDWR | flags);
        LOGD("open() fd = %d", fd);
        (*env)->ReleaseStringUTFChars(env, path, path_utf);
        if (fd == -1)
        {
            /* Throw an exception */
            LOGE("Cannot open port");
            /* TODO: throw an exception */
            return NULL;
        }
    }

    /* Configure device */
    {
        struct termios cfg;
        LOGD("Configuring serial port");
        if (tcgetattr(fd, &cfg))
        {
            LOGE("tcgetattr() failed");
            close(fd);
            /* TODO: throw an exception */
            return NULL;
        }

        cfmakeraw(&cfg);
        cfsetispeed(&cfg, speed);
        cfsetospeed(&cfg, speed);

        if (tcsetattr(fd, TCSANOW, &cfg))
        {
            LOGE("tcsetattr() failed");
            close(fd);
            /* TODO: throw an exception */
            return NULL;
        }
    }

    /* Create a corresponding file descriptor */
    {
        jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
        jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor, "<init>", "()V");
        jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
        mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
        (*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint)fd);
    }

    return mFileDescriptor;
}

/*
 * Class:     com_weloo_serialport_lib_SerialPort
 * Method:    close
 * Signature: ()V
 */
/*JNIEXPORT void JNICALL Java_com_weloo_serialport_lib_SerialPort_close
        (JNIEnv *env, jobject thiz)
{
    jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
    jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

    jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
    jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");

    jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
    jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

    LOGD("close(fd = %d)", descriptor);
    close(descriptor);
}*/

JNIEXPORT void JNICALL Java_com_weloo_serialport_lib_SerialPort_close
        (JNIEnv *env, jobject thiz, jobject fdObj)
{
    jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");
    if (FileDescriptorClass == NULL) {
        LOGE("FileDescriptor class not found");
        return;
    }

    jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");
    if (descriptorID == NULL) {
        LOGE("descriptor field not found");
        return;
    }

    jint descriptor = (*env)->GetIntField(env, fdObj, descriptorID);
    if (descriptor != -1) {
        LOGD("close(fd = %d)", descriptor);
        close(descriptor);
        (*env)->SetIntField(env, fdObj, descriptorID, -1);
    } else {
        LOGD("fd already closed (fd = %d)", descriptor);
    }
}

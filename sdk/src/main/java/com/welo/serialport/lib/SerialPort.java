package com.welo.serialport.lib;

import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SerialPort {
    private static final String TAG = "SerialPort";
    private FileDescriptor mFd;
    private FileInputStream mInputStream;
    private FileOutputStream mOutputStream;
    private ReadThread mReadThread;
    private OnDataReceivedListener mDataListener;
    private boolean isOpen = false;

    // 数据接收监听接口
    public interface OnDataReceivedListener {
        void onDataReceived(byte[] buffer, int size);
    }

    public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
        if (!device.canRead() || !device.canWrite()) {
            try {
                Process su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
                    throw new SecurityException("无法获取串口权限");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException("获取权限失败");
            }
        }

        mFd = open(device.getAbsolutePath(), baudrate, flags);
        if (mFd == null) {
            Log.e(TAG, "串口打开失败");
            throw new IOException("无法打开串口");
        }
        mInputStream = new FileInputStream(mFd);
        mOutputStream = new FileOutputStream(mFd);
        isOpen = true;
        startReadThread();
    }

    private void startReadThread() {
        if (mReadThread == null) {
            mReadThread = new ReadThread();
            mReadThread.start();
        }
    }

    public void setOnDataReceivedListener(OnDataReceivedListener listener) {
        this.mDataListener = listener;
    }

    public void sendData(byte[] data) throws IOException {
        if (!isOpen || mOutputStream == null) {
            throw new IOException("串口未打开");
        }
        mOutputStream.write(data);
        mOutputStream.flush();
    }

    public void sendDataWithInterval(byte[] data, long interval) {
        new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    sendData(data);
                    Thread.sleep(interval);
                }
            } catch (Exception e) {
                Log.e(TAG, "发送数据失败", e);
            }
        }).start();
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            byte[] buffer = new byte[1024];
            while (!isInterrupted() && isOpen) {
                try {
                    int size = mInputStream.read(buffer);
                    if (size > 0 && mDataListener != null) {
                        mDataListener.onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "读取数据失败", e);
                    break;
                }
            }
        }
    }

    public void close() {
        isOpen = false;
        if (mReadThread != null) {
            mReadThread.interrupt();
            mReadThread = null;
        }
        try {
            if (mInputStream != null) mInputStream.close();
            if (mOutputStream != null) mOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "关闭流失败", e);
        }
        close(mFd);
        mFd = null;
    }

    private native static FileDescriptor open(String path, int baudrate, int flags);
    private native void close(FileDescriptor fd);

    static {
        System.loadLibrary("serial_port");
    }

    public boolean isOpen() {
        return isOpen;
    }
}

package com.welo.serialport.lib;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.File;
import java.security.InvalidParameterException;

public class SerialPortManager {
    private static SerialPortManager sInstance;
    private SerialPort mSerialPort;
    private SerialPortFinder mPortFinder;
    private SharedPreferences mSp;

    private SerialPortManager(Context context) {
        mPortFinder = new SerialPortFinder();
        mSp = context.getSharedPreferences("serial_port_prefs", Context.MODE_PRIVATE);
    }

    public static synchronized SerialPortManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SerialPortManager(context.getApplicationContext());
        }
        return sInstance;
    }

    public SerialPort openSerialPort() throws Exception {
        if (mSerialPort != null && mSerialPort.isOpen()) {
            return mSerialPort;
        }

        String path = mSp.getString("DEVICE", "");
        int baudrate = Integer.parseInt(mSp.getString("BAUDRATE", "115200"));

        if (path.isEmpty()) {
            throw new InvalidParameterException("未设置串口路径");
        }

        mSerialPort = new SerialPort(new File(path), baudrate, 0);
        return mSerialPort;
    }

    public void saveSerialPortConfig(String path, String baudrate) {
        mSp.edit()
                .putString("DEVICE", path)
                .putString("BAUDRATE", baudrate)
                .apply();
    }

    public SerialPortFinder getPortFinder() {
        return mPortFinder;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    public SerialPort getSerialPort() {
        return mSerialPort;
    }
}
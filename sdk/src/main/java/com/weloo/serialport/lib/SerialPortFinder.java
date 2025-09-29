package com.weloo.serialport.lib;

import android.util.Log;
import java.io.File;
import java.util.Vector;

public class SerialPortFinder {
    private static final String TAG = "SerialPortFinder";
    private static final String[] DEVICE_PREFIXES = {
            "/dev/ttyS", "/dev/ttyUSB", "/dev/ttyACM", "/dev/ttyGS"
    };

    public String[] getAllDevices() {
        Vector<String> devices = new Vector<>();
        try {
            File devDir = new File("/dev");
            File[] files = devDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String path = file.getAbsolutePath();
                    for (String prefix : DEVICE_PREFIXES) {
                        if (path.startsWith(prefix) && file.canRead()) {
                            devices.add(path);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "查找设备失败", e);
        }
        return devices.toArray(new String[0]);
    }

    public String[] getAllDevicesPath() {
        return getAllDevices();
    }
}


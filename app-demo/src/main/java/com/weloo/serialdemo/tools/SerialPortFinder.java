package com.weloo.serialdemo.tools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;

import android.util.Log;

public class SerialPortFinder {
	private static final String TAG = "SerialPort";

	private Vector<Driver> mDrivers;

	private static final String[] DEVICE_PREFIXES = {
			"/dev/ttyS",    // 物理串口（如嵌入式设备的 /dev/ttyS0）
			"/dev/ttyUSB",  // USB 转串口（如 CH340 的 /dev/ttyUSB0）
			"/dev/ttyACM",  // ACM 串口（如 Arduino 的 /dev/ttyACM0）
			"/dev/ttyGS"    // 虚拟串口（部分设备）
	};

	private static final String[] KNOWN_SERIAL_DEVICES = {
			"/dev/ttyS0",   // 物理串口1
			"/dev/ttyS1",   // 物理串口2
			"/dev/ttyUSB0"  // USB转串口
	};

	public Vector<Driver> getDrivers() throws IOException {
		Vector<Driver> drivers = new Vector<>();
		for (String devicePath : KNOWN_SERIAL_DEVICES) {
			File deviceFile = new File(devicePath);
			// 检查设备是否存在且可读
			if (deviceFile.exists() && deviceFile.canRead()) {
				drivers.add(new Driver(devicePath, devicePath));
				Log.d(TAG, "Found known serial device: " + devicePath);
			}
		}
		return drivers;
	}

/*	Vector<Driver> getDrivers() throws IOException {
		if (mDrivers == null) {
			mDrivers = new Vector<>();
			// 遍历 /dev 目录，查找符合前缀的串口设备
			File devDir = new File("/dev");
			File[] files = devDir.listFiles();
			if (files != null) {
				for (File file : files) {
					String filePath = file.getAbsolutePath();
					// 匹配串口设备前缀
					for (String prefix : DEVICE_PREFIXES) {
						if (filePath.startsWith(prefix)) {
							// 检查设备是否可读（避免无权限的节点）
							if (file.canRead()) {
								// 驱动名简化为设备路径，设备节点为 filePath
								mDrivers.add(new Driver(filePath, filePath));
								Log.d(TAG, "Found serial device: " + filePath);
							}
							break; // 匹配一个前缀即可，跳出循环
						}
					}
				}
			}
		}
		return mDrivers;
	}*/


/*	Vector<Driver> getDrivers() throws IOException {
		if (mDrivers == null) {
			mDrivers = new Vector<Driver>();
			LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
			String l;
			while((l = r.readLine()) != null) {
				// Issue 3:
				// Since driver name may contain spaces, we do not extract driver name with split()
				String drivername = l.substring(0, 0x15).trim();
				String[] w = l.split(" +");
				if ((w.length >= 5) && (w[w.length-1].equals("serial"))) {
					Log.d(TAG, "Found new driver " + drivername + " on " + w[w.length-4]);
					mDrivers.add(new Driver(drivername, w[w.length-4]));
				}
			}
			r.close();
		}
		return mDrivers;
	}*/

	public String[] getAllDevices() {
		android.util.Log.d("WELO-SerialPortFinder", "getAllDevices");
		Vector<String> devices = new Vector<String>();
		// Parse each driver
		Iterator<Driver> itdriv;
		try {
			itdriv = getDrivers().iterator();
			while(itdriv.hasNext()) {
				Driver driver = itdriv.next();
				Iterator<File> itdev = driver.getDevices().iterator();
				while(itdev.hasNext()) {
					String device = itdev.next().getName();
					String value = String.format("%s (%s)", device, driver.getName());
					devices.add(value);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return devices.toArray(new String[devices.size()]);
	}

	public String[] getAllDevicesPath() {
		android.util.Log.d("WELO-SerialPortFinder", "getAllDevicesPath");
		Vector<String> devices = new Vector<String>();
		// Parse each driver
		Iterator<Driver> itdriv;
		try {
			itdriv = getDrivers().iterator();
			while(itdriv.hasNext()) {
				Driver driver = itdriv.next();
				Iterator<File> itdev = driver.getDevices().iterator();
				while(itdev.hasNext()) {
					String device = itdev.next().getAbsolutePath();
					devices.add(device);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return devices.toArray(new String[devices.size()]);
	}

	public class Driver {
		public Driver(String name, String root) {
			mDriverName = name;
			mDeviceRoot = root;
		}
		private String mDriverName;
		private String mDeviceRoot;
		Vector<File> mDevices = null;
		public Vector<File> getDevices() {
			if (mDevices == null) {
				mDevices = new Vector<File>();
				File dev = new File("/dev");
				File[] files = dev.listFiles();
				int i;
				for (i=0; i<files.length; i++) {
					if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
						Log.d(TAG, "Found new device: " + files[i]);
						mDevices.add(files[i]);
					}
				}
			}
			return mDevices;
		}
		public String getName() {
			return mDriverName;
		}
	}
}

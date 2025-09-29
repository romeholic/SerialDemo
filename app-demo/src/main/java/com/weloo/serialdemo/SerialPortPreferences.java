package com.weloo.serialdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import com.weloo.serialdemo.tools.SerialPortFinder;

public class SerialPortPreferences extends PreferenceActivity {
	private static final String TAG = "WELO-SerialPortPref";
	private static final String WELO_SP_NAME = "com_weloo_serialdemo_preferences";
	private Application mApplication;
	private SerialPortFinder mSerialPortFinder;
	private SharedPreferences mSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		android.util.Log.d(TAG, "onCreate");
		getPreferenceManager().setSharedPreferencesName(WELO_SP_NAME);

		mApplication = (Application) getApplication();
		mSerialPortFinder = mApplication.mSerialPortFinder;

		addPreferencesFromResource(R.xml.serial_port_preferences);

		mSharedPreferences = getPreferenceManager().getSharedPreferences();
		if (mSharedPreferences == null) {
			finish();
			return;
		}
		getCurrentSPValues("getCurrentSPValues when created");

		initDeviceListPreference();
		initBaudrateListPreference();
	}

	private void initDeviceListPreference() {
		final ListPreference devicesPref = (ListPreference) findPreference("DEVICE");
		if (devicesPref == null) {
			android.util.Log.e(TAG, "devicesPref not found");
			return;
		}

		String[] allDevices = mSerialPortFinder.getAllDevices();
		String[] allDevicePaths = mSerialPortFinder.getAllDevicesPath();

		for (int i = 0; i < allDevices.length; i++) {
			android.util.Log.d(TAG, "  device" + (i + 1) + "：name=" + allDevices[i] + "，path=" + allDevicePaths[i]);
		}

		devicesPref.setEntries(allDevices);
		devicesPref.setEntryValues(allDevicePaths);

		String currentDevice = devicesPref.getValue();
		android.util.Log.d(TAG, "「device」currentValue in SP: " + (currentDevice == null ? "not set" : currentDevice));
		devicesPref.setSummary(currentDevice != null ? currentDevice : "please select device");

		devicesPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String newDevicePath = (String) newValue;
				android.util.Log.i(TAG, "device changed");
				android.util.Log.i(TAG, "new device：" + newDevicePath);

				preference.setSummary(newDevicePath);

				new android.os.Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						getCurrentSPValues("getCurrentSPValues after path changed");
					}
				}, 100);

				return true;
			}
		});
		android.util.Log.d(TAG, "initDeviceListPreference succeed");
	}

	private void initBaudrateListPreference() {
		final ListPreference baudratePref = (ListPreference) findPreference("BAUDRATE");
		if (baudratePref == null) {
			android.util.Log.e(TAG, "baudratesPref not found");
			return;
		}

		String currentBaudrate = baudratePref.getValue();
		android.util.Log.d(TAG, "「baudrate」currentValue in SP: " + (currentBaudrate == null ? "not set" : currentBaudrate));
		baudratePref.setSummary(currentBaudrate != null ? currentBaudrate : "please select baudrate");

		baudratePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String newBaudrate = (String) newValue;
				android.util.Log.i(TAG, "baudrate changed");
				android.util.Log.i(TAG, "new baudrate：" + newBaudrate);

				preference.setSummary(newBaudrate);

				new android.os.Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						getCurrentSPValues("getCurrentSPValues after baudrate changed");
					}
				}, 100);

				return true;
			}
		});
		android.util.Log.d(TAG, "initBaudrateListPreference succeed");
	}

	private void getCurrentSPValues(String scene) {
		if (mSharedPreferences == null) {
			android.util.Log.e(TAG, "getCurrentSPValues error：mSharedPreferences is null");
			return;
		}
		String spDevice = mSharedPreferences.getString("DEVICE", null);
		String spBaudrate = mSharedPreferences.getString("BAUDRATE", null);
		android.util.Log.d(TAG, "【" + scene + "】latest value in sp：");
		android.util.Log.d(TAG, "  DEVICE：" + (spDevice == null ? "null" : spDevice));
		android.util.Log.d(TAG, "  BAUDRATE：" + (spBaudrate == null ? "null" : spBaudrate));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		android.util.Log.d(TAG, "onDestroy");
		getCurrentSPValues("getCurrentSPValues before destroy");
	}
}


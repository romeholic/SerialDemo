package com.weloo.serialdemo;

import java.io.IOException;
import java.util.Arrays;
import android.os.Bundle;

public class SendingDataActivity extends SerialPortActivity {
	private final byte[] mStopPacket = new byte[]{
			0x58,
			0x59,
			0x53,
			0x00,
			0x00,
			0x04
	};

/*	private static final long WAKE_LOW_DELAY = 2;    // 拉低时长：2ms
	private static final long WAKE_INTERVAL = 100;   // 唤醒后间隔：100ms*/

    private byte[] mStandardPacket;
	SendingThread mSendingThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		android.util.Log.d("WELO-SendingDataActivity", "onCreate");
		setContentView(R.layout.sending_data);

		buildStandardPacket();
		buildStopPacket();

		if (mSerialPort != null && mOutputStream != null) {
			mSendingThread = new SendingThread();
			mSendingThread.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		android.util.Log.d("WELO-SendingDataActivity", "onPause");
		if (mOutputStream != null && mStopPacket != null) {
			try {
				mOutputStream.write(mStopPacket);
				mOutputStream.flush();
				android.util.Log.d("WELO-SendingDataActivity", "onPause: send stopPacket succeed（" + mStopPacket.length + "byte），content（hex）：" + bytesToHex(mStopPacket));
			} catch (IOException e) {
				android.util.Log.e("WELO-SendingDataActivity", "onPause: send failed", e);
			}
		}
	}

	@Override
	protected void onDataReceived(byte[] buffer, int size) {
		if (size > 0) {
			android.util.Log.d("WELO-SendingDataActivity", "onDataReceived: data received from mcu（" + size + "byte），content：" + bytesToHex(Arrays.copyOf(buffer, size)));
		}
	}

	private String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			String hex = Integer.toHexString(b & 0xFF);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex).append(' ');
		}
		return sb.toString().toUpperCase();
	}

	private void buildStopPacket() {
		android.util.Log.d("WELO-SendingDataActivity", "buildStopPacket succeed: （" + mStopPacket.length + "byte）");
	}

	private void buildStandardPacket() {
		byte[] startBytes = new byte[]{0x58, 0x59, 0x52};
		byte[] dataBytes = new byte[]{0x00,	(byte)0xFE, (byte)0x82, (byte)0x82,
				(byte)0xFE, 0x00, (byte)0xF2, (byte)0x92,
				(byte)0x92, (byte)0x9E, 0x00, (byte)0x92,
				(byte)0x92, (byte)0x92, (byte)0xFE, 0x00,
				(byte)0x1E, 0x10, 0x10, (byte)0xFE,
				0x00, (byte)0x9E, (byte)0x92, (byte)0x92,
				(byte)0xF2, 0x00, (byte)0xFE, (byte)0x92,
				(byte)0x92, (byte)0xF2, 0x00, 0x00};
		byte[] endByte = new byte[]{0x6F};

		mStandardPacket = new byte[startBytes.length + dataBytes.length + endByte.length];
		System.arraycopy(startBytes, 0, mStandardPacket, 0, startBytes.length);
		System.arraycopy(dataBytes, 0, mStandardPacket, startBytes.length, dataBytes.length);
		System.arraycopy(endByte, 0, mStandardPacket, startBytes.length + dataBytes.length, endByte.length);

		android.util.Log.d("WELO-SendingDataActivity", "buildStandardPacket succeed: （" + mStandardPacket.length + "byte）");
	}

	private class SendingThread extends Thread {
		//private static final long SEND_INTERVAL = 100;
		@Override
		public void run() {
			android.util.Log.d("WELO-SendingDataActivity", "SendingThread: send once");
			try {
				if (mOutputStream == null || mStandardPacket == null) {
					return;
				}
				mOutputStream.write(mStandardPacket);
				mOutputStream.flush();
				android.util.Log.d("WELO-SendingDataActivity", "SendingThread: send once succeed（" + mStandardPacket.length + "byte），content（hex）：" + bytesToHex(mStandardPacket));
			} catch (IOException e) {
				android.util.Log.e("WELO-SendingDataActivity", "SendingThread: send failed", e);
			}
			android.util.Log.d("WELO-SendingDataActivity", "SendingThread: sending thread exit");
		}

/*		@Override
		public void run() {
			android.util.Log.d("WELO-SendingDataActivity", "SendingThread: always send，interval=" + SEND_INTERVAL + "ms");
			while (!isInterrupted()) {
				try {
					if (mOutputStream == null) {
						break;
					}

					mOutputStream.write(mStandardPacket);
					mOutputStream.flush();
                    android.util.Log.d("WELO-SendingDataActivity", "SendingThread: always send succeed（" + mStandardPacket.length + "byte）");

					Thread.sleep(SEND_INTERVAL);
				} catch (IOException e) {
					android.util.Log.e("WELO-SendingDataActivity", "SendingThread: send failed", e);
					break;
				} catch (InterruptedException e) {
				    android.util.Log.e("WELO-SendingDataActivity", "SendingThread: sending thread interrupted", e);
					Thread.currentThread().interrupt();
					break;
				}
			}
			android.util.Log.d("WELO-SendingDataActivity", "SendingThread: sending thread exit");
		}*/
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		android.util.Log.d("WELO-SendingDataActivity", "onDestroy");
		if (mSendingThread != null && !mSendingThread.isInterrupted()) {
			mSendingThread.interrupt();
			mSendingThread = null;
		}
		try {
			if (mOutputStream != null) {
				mOutputStream.close();
				mOutputStream = null;
			}
		} catch (IOException e) {
			android.util.Log.e("WELO-SendingDataActivity", "onDestroy: close stream error", e);
		}
	}
}
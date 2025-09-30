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

	private static final byte[] WAKE_PACKET = new byte[8];

/*	private static final long WAKE_LOW_DELAY = 2;    // 拉低时长：2ms
	private static final long WAKE_INTERVAL = 100;   // 唤醒后间隔：100ms*/
    private static final long WAKE_DELAY_MS = 100;
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

	// https://www.metools.info/code/c128.html
	// wake the sub screen: 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00
	// show something: 0x58 0X59 0x52 D0.....D255 Y1
	// turn off the sub screen: 0x58 0x59 0x53 0x00 0x00 0x04
	private void buildStandardPacket() {
		byte[] startBytes = new byte[]{0x58, 0x59, 0x52};

		byte[] weatherBytes0 = new byte[]{
				// 第1行：00 00 00 FF 00 00 00 00 00 00 00 00 FF FF 00 00
				0X00, 0X00, 0X00, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, (byte)0XFF, (byte)0XFF, 0X00, 0X00,
				// 第2行：00 FF FF 00 00 00 FF FF 00 00 00 00 00 00 00 00
				0X00, (byte)0XFF, (byte)0XFF, 0X00, 0X00, 0X00, (byte)0XFF, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
				// 第3行：00 FF 00 00 00 FF 00 00 00 00 00 FF 00 00 FF 00
				0X00, (byte)0XFF, 0X00, 0X00, 0X00, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, (byte)0XFF, 0X00, 0X00, (byte)0XFF, 0X00,
				// 第4行：FF 00 00 00 00 00 00 FF 00 00 00 00 00 00 00 00
				(byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
				// 第5行：00 00 00 FF 00 00 00 00 00 00 00 00 00 00 FF 00
				0X00, 0X00, 0X00, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, (byte)0XFF, 0X00,
				// 第6行：FF 00 00 00 00 00 FF FF 00 00 00 00 00 00 00 00
				(byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, (byte)0XFF, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
				// 第7行：FF 00 FF FF FF 00 FF 00 00 00 00 00 00 00 FF 00
				(byte)0XFF, 0X00, (byte)0XFF, (byte)0XFF, (byte)0XFF, 0X00, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, (byte)0XFF, 0X00,
				// 第8行：FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00
				(byte)0XFF, (byte)0XFF, (byte)0XFF, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
				// 第9行：FF 00 FF FF FF 00 FF 00 00 00 00 00 00 FF 00 00
				(byte)0XFF, 0X00, (byte)0XFF, (byte)0XFF, (byte)0XFF, 0X00, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, (byte)0XFF, 0X00, 0X00,
				// 第10行：00 FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00
				0X00, (byte)0XFF, (byte)0XFF, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
				// 第11行：00 FF 00 FF 00 FF 00 00 00 00 00 00 FF 00 00 00
				0X00, (byte)0XFF, 0X00, (byte)0XFF, 0X00, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, (byte)0XFF, 0X00, 0X00, 0X00,
				// 第12行：00 00 00 FF 00 00 00 00 00 00 00 00 00 00 00 00
				0X00, 0X00, 0X00, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
				// 第13行：00 FF 00 00 00 FF 00 00 00 00 00 FF 00 00 00 00
				0X00, (byte)0XFF, 0X00, 0X00, 0X00, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, (byte)0XFF, 0X00, 0X00, 0X00, 0X00,
				// 第14行：00 00 00 FF 00 00 00 00 00 00 00 00 00 00 00 00
				0X00, 0X00, 0X00, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
				// 第15行：00 00 00 FF 00 00 00 00 00 00 00 FF FF FF FF 00
				0X00, 0X00, 0X00, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, (byte)0XFF, (byte)0XFF, (byte)0XFF, (byte)0XFF, 0X00,
				// 第16行：00 FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00
				0X00, (byte)0XFF, (byte)0XFF, (byte)0XFF, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00
		};
		byte[] weatherEndByte0 = new byte[]{(byte)0xCD};

		byte[] welooBytes0 = new byte[]{
				// 第1行：00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
				0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,
				// 第2行：00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
				0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,
				// 第3行：00 00 00 00 00 00 00 00 00 00 00 FF 00 00 00 00
				0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,(byte)0XFF,0X00,0X00,0X00,0X00,
				// 第4行：00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
				0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,
				// 第5行：00 FF 00 00 00 FF 00 00 00 00 00 FF 00 00 00 00
				0X00,(byte)0XFF,0X00,0X00,0X00,(byte)0XFF,0X00,0X00,0X00,0X00,0X00,(byte)0XFF,0X00,0X00,0X00,0X00,
				// 第6行：00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
				0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,
				// 第7行：00 FF 00 FF 00 FF 00 00 FF FF 00 FF 00 00 FF 00
				0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,(byte)0XFF,0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,
				// 第8行：00 00 FF 00 00 00 00 00 00 00 00 00 00 00 00 00
				0X00,0X00,(byte)0XFF,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,
				// 第9行：00 FF 00 FF 00 FF 00 FF 00 FF 00 FF 00 FF 00 FF
				0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,(byte)0XFF,
				// 第10行：00 FF 00 FF 00 00 00 00 00 00 00 00 00 00 00 00
				0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,
				// 第11行：00 00 FF 00 FF 00 00 FF FF 00 00 FF 00 FF 00 FF
				0X00,0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,(byte)0XFF,
				// 第12行：00 FF 00 FF 00 00 00 00 00 00 00 00 00 00 00 00
				0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,
				// 第13行：00 00 FF 00 FF 00 00 00 FF FF 00 FF 00 00 FF 00
				0X00,0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,0X00,0X00,(byte)0XFF,(byte)0XFF,0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,
				// 第14行：00 00 FF 00 00 00 00 00 00 00 00 00 00 00 00 00
				0X00,0X00,(byte)0XFF,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,
				// 第15行：00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
				0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,
				// 第16行：00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
				0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00
		};
		byte[] welooEndByte0 = new byte[]{(byte)0xDD};

/*		mStandardPacket = new byte[startBytes.length + welooBytes0.length + welooEndByte0.length];
		System.arraycopy(startBytes, 0, mStandardPacket, 0, startBytes.length);
		System.arraycopy(welooBytes0, 0, mStandardPacket, startBytes.length, welooBytes0.length);
		System.arraycopy(welooEndByte0, 0, mStandardPacket, startBytes.length + welooBytes0.length, welooEndByte0.length);*/

		mStandardPacket = new byte[startBytes.length + weatherBytes0.length + weatherEndByte0.length];
		System.arraycopy(startBytes, 0, mStandardPacket, 0, startBytes.length);
		System.arraycopy(weatherBytes0, 0, mStandardPacket, startBytes.length, weatherBytes0.length);
		System.arraycopy(weatherEndByte0, 0, mStandardPacket, startBytes.length + weatherBytes0.length, weatherEndByte0.length);
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

				mOutputStream.write(WAKE_PACKET);
				mOutputStream.flush();
				android.util.Log.d("WELO-SendingDataActivity", "SendingThread: send once succeed（" + WAKE_PACKET.length + "byte），content（hex）：" + bytesToHex(WAKE_PACKET));

				Thread.sleep(WAKE_DELAY_MS);
				android.util.Log.d("WELO-SendingDataActivity", "SendingThread: wake waiting... " + WAKE_DELAY_MS + "ms");

				mOutputStream.write(mStandardPacket);
				mOutputStream.flush();
				android.util.Log.d("WELO-SendingDataActivity", "SendingThread: send once succeed（" + mStandardPacket.length + "byte），content（hex）：" + bytesToHex(mStandardPacket));
			} catch (IOException e) {
				android.util.Log.e("WELO-SendingDataActivity", "SendingThread: send failed", e);
			} catch (InterruptedException e) {
				android.util.Log.e("WELO-SendingDataActivity", "SendingThread: send InterruptedException", e);
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
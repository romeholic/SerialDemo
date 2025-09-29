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

		//023456
		byte[] testBytes = new byte[]{
                0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,
                0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,
                0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,
                (byte)0XFF,0X00,0X00,(byte)0XFF,0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,0X00,
                0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,0X00,0X00,0X00,(byte)0XFF,0X00,0X00,0X00,0X00,(byte)0XFF,0X00,
                (byte)0XFF,0X00,0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,0X00,0X00,0X00,(byte)0XFF,0X00,0X00,0X00,0X00,0X00,
                0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,
                (byte)0XFF,0X00,0X00,(byte)0XFF,0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,0X00,
                0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,0X00,0X00,0X00,0X00,0X00,0X00,(byte)0XFF,0X00,
                (byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,0X00,0X00,0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,0X00,
                0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,0X00,0X00,0X00,0X00,0X00,0X00,(byte)0XFF,0X00,
                0X00,0X00,0X00,(byte)0XFF,0X00,0X00,0X00,0X00,(byte)0XFF,0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,0X00,
                0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,
                0X00,0X00,0X00,(byte)0XFF,0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,0X00,
                0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,
                0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00,0X00};

		//WELOO LOGO
/*		// 第0行（左到右32列）
		        0X00,  // 左侧留白
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // W第0行
				0XFF,0XFF,0XFF,0XFF,0XFF,0X00,  // E第0行
				0XFF,0X00,0X00,0X00,0X00,0X00,  // L第0行
				0X00,0XFF,0XFF,0XFF,0XFF,0X00,  // O第0行
				0X00,0XFF,0XFF,0XFF,0XFF,0X00,  // O第0行
				0X00,  // 右侧留白

		// 第1行
				0X00,
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // W第1行
				0XFF,0X00,0X00,0X00,0X00,0X00,  // E第1行
				0XFF,0X00,0X00,0X00,0X00,0X00,  // L第1行
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // O第1行
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // O第1行
				0X00,

		// 第2行
				0X00,
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // W第2行
				0XFF,0X00,0X00,0X00,0X00,0X00,  // E第2行
				0XFF,0X00,0X00,0X00,0X00,0X00,  // L第2行
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // O第2行
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // O第2行
				0X00,

		// 第3行
				0X00,
				0X00,0XFF,0XFF,0XFF,0XFF,0X00,  // W第3行
				0XFF,0XFF,0XFF,0XFF,0X00,0X00,  // E第3行
				0XFF,0X00,0X00,0X00,0X00,0X00,  // L第3行
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // O第3行
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // O第3行
				0X00,

		// 第4行
				0X00,
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // W第4行
				0XFF,0X00,0X00,0X00,0X00,0X00,  // E第4行
				0XFF,0X00,0X00,0X00,0X00,0X00,  // L第4行
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // O第4行
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // O第4行
				0X00,

		// 第5行
				0X00,
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // W第5行
				0XFF,0X00,0X00,0X00,0X00,0X00,  // E第5行
				0XFF,0X00,0X00,0X00,0X00,0X00,  // L第5行
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // O第5行
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // O第5行
				0X00,

		// 第6行
				0X00,
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // W第6行
				0XFF,0X00,0X00,0X00,0X00,0X00,  // E第6行
				0XFF,0X00,0X00,0X00,0X00,0X00,  // L第6行
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // O第6行
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // O第6行
				0X00,

		// 第7行
				0X00,
				0X00,0XFF,0X00,0X00,0XFF,0X00,  // W第7行
				0XFF,0XFF,0XFF,0XFF,0XFF,0X00,  // E第7行
				0XFF,0XFF,0XFF,0XFF,0XFF,0X00,  // L第7行
				0X00,0XFF,0XFF,0XFF,0XFF,0X00,  // O第7行
				0X00,0XFF,0XFF,0XFF,0XFF,0X00,  // O第7行
				0X00*/

		byte[] welooBytes = new byte[]{
				0X00,
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00, // W第0行
				(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00, // E第0行
				(byte)0XFF,0X00,0X00,0X00,0X00,0X00, // L第0行
				0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00, // O第0行
				0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00, // O第0行
				0X00,

				0X00,
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00, // W第1行
				(byte)0XFF,0X00,0X00,0X00,0X00,0X00, // E第1行
				(byte)0XFF,0X00,0X00,0X00,0X00,0X00, // L第1行
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00, // O第1行
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00, // O第1行
				0X00,

				0X00,
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00, // W第2行
				(byte)0XFF,0X00,0X00,0X00,0X00,0X00, // E第2行
				(byte)0XFF,0X00,0X00,0X00,0X00,0X00, // L第2行
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00, // O第2行
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00, // O第2行
				0X00,

				0X00,
				0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,  // W第3行
				(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,0X00,  // E第3行
				(byte)0XFF,0X00,0X00,0X00,0X00,0X00,  // L第3行
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,  // O第3行
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,  // O第3行
				0X00,

				0X00,
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,  // W第4行
				(byte)0XFF,0X00,0X00,0X00,0X00,0X00,  // E第4行
				(byte)0XFF,0X00,0X00,0X00,0X00,0X00,  // L第4行
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,  // O第4行
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,  // O第4行
				0X00,

				0X00,
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,  // W第5行
				(byte)0XFF,0X00,0X00,0X00,0X00,0X00,  // E第5行
				(byte)0XFF,0X00,0X00,0X00,0X00,0X00,  // L第5行
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,  // O第5行
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,  // O第5行
				0X00,

				0X00,
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,  // W第6行
				(byte)0XFF,0X00,0X00,0X00,0X00,0X00,  // E第6行
				(byte)0XFF,0X00,0X00,0X00,0X00,0X00,  // L第6行
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,  // O第6行
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,  // O第6行
				0X00,

				0X00,
				0X00,(byte)0XFF,0X00,0X00,(byte)0XFF,0X00,  // W第7行
				(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,  // E第7行
				(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,  // L第7行
				0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,  // O第7行
				0X00,(byte)0XFF,(byte)0XFF,(byte)0XFF,(byte)0XFF,0X00,  // O第7行
				0X00
		};

		byte[] endByte = new byte[]{(byte)0xA9};

		byte[] welooEndByte = new byte[]{(byte)0xAA};

		mStandardPacket = new byte[startBytes.length + welooBytes.length + welooEndByte.length];
		System.arraycopy(startBytes, 0, mStandardPacket, 0, startBytes.length);
		System.arraycopy(welooBytes, 0, mStandardPacket, startBytes.length, welooBytes.length);
		System.arraycopy(welooEndByte, 0, mStandardPacket, startBytes.length + welooBytes.length, welooEndByte.length);

/*		mStandardPacket = new byte[startBytes.length + testBytes.length + endByte.length];
		System.arraycopy(startBytes, 0, mStandardPacket, 0, startBytes.length);
		System.arraycopy(testBytes, 0, mStandardPacket, startBytes.length, testBytes.length);
		System.arraycopy(endByte, 0, mStandardPacket, startBytes.length + testBytes.length, endByte.length);*/

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
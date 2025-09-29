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

		byte[] welooBytes0 = new byte[]{
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


		byte[] weatherBytes2 = new byte[]{
				// 第1行数据（16字节）：00 00 00 B6 00 00 00 00 00 00 00 6D 92 24 00 24
				0X00, 0X00, 0X00, (byte)0XB6, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, (byte)0X6D, (byte)0X92, 0X24, 0X00, 0X24,
				// 第2行数据（16字节）：92 92 49 00 00 92 92 00 00 00 00 00 00 00 00 00
				(byte)0X92, (byte)0X92, 0X49, 0X00, 0X00, (byte)0X92, (byte)0X92, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
				// 第3行数据（16字节）：00 B6 00 49 00 B6 00 00 00 00 00 92 49 00 B6 00
				0X00, (byte)0XB6, 0X00, 0X49, 0X00, (byte)0XB6, 0X00, 0X00, 0X00, 0X00, 0X00, (byte)0X92, 0X49, 0X00, (byte)0XB6, 0X00,
				// 第4行数据（16字节）：6D 49 24 00 00 49 49 6D 24 00 00 00 00 00 00 00
				(byte)0X6D, 0X49, 0X24, 0X00, 0X00, 0X49, 0X49, (byte)0X6D, 0X24, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
				// 第5行数据（16字节）：00 49 24 92 24 49 00 00 00 00 00 24 00 00 B6 24
				0X00, 0X49, 0X24, (byte)0X92, 0X24, 0X49, 0X00, 0X00, 0X00, 0X00, 0X00, 0X24, 0X00, 0X00, (byte)0XB6, 0X24,
				// 第6行数据（16字节）：6D 49 00 00 00 00 B6 B6 00 00 00 00 00 00 00 00
				(byte)0X6D, 0X49, 0X00, 0X00, 0X00, 0X00, (byte)0XB6, (byte)0XB6, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
				// 第7行数据（16字节）：92 00 DB FF DB 24 6D 00 00 00 00 00 00 00 B6 00
				(byte)0X92, 0X00, (byte)0XDB, (byte)0XFF, (byte)0XDB, 0X24, (byte)0X6D, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, (byte)0XB6, 0X00,
				// 第8行数据（16字节）：6D 92 6D 6D 00 00 00 00 00 00 00 00 00 00 00 00
				(byte)0X6D, (byte)0X92, (byte)0X6D, (byte)0X6D, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
				// 第9行数据（16字节）：6D 00 FF FF DB 00 6D 00 00 00 00 00 00 92 24 00
				(byte)0X6D, 0X00, (byte)0XFF, (byte)0XFF, (byte)0XDB, 0X00, (byte)0X6D, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, (byte)0X92, 0X24, 0X00,
				// 第10行数据（16字节）：24 6D 6D B6 24 00 00 00 00 00 00 00 00 00 00 00
				0X24, (byte)0X6D, (byte)0X6D, (byte)0XB6, 0X24, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
				// 第11行数据（16字节）：00 6D 24 92 24 6D 00 00 00 00 00 24 B6 24 00 00
				0X00, (byte)0X6D, 0X24, (byte)0X92, 0X24, (byte)0X6D, 0X00, 0X00, 0X00, 0X00, 0X00, 0X24, (byte)0XB6, 0X24, 0X00, 0X00,
				// 第12行数据（16字节）：00 00 00 92 24 00 00 00 00 00 00 00 00 00 00 00
				0X00, 0X00, 0X00, (byte)0X92, 0X24, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
				// 第13行数据（16字节）：00 B6 00 49 00 B6 00 00 00 00 24 DB 24 24 24 00
				0X00, (byte)0XB6, 0X00, 0X49, 0X00, (byte)0XB6, 0X00, 0X00, 0X00, 0X00, 0X24, (byte)0XDB, 0X24, 0X24, 0X24, 0X00,
				// 第14行数据（16字节）：00 24 24 92 24 00 00 00 00 00 00 00 00 00 00 00
				0X00, 0X24, 0X24, (byte)0X92, 0X24, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
				// 第15行数据（16字节）：00 00 00 B6 00 00 00 00 00 00 24 92 92 92 92 24
				0X00, 0X00, 0X00, (byte)0XB6, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X24, (byte)0X92, (byte)0X92, (byte)0X92, (byte)0X92, 0X24,
				// 第16行数据（16字节）：00 92 92 92 00 00 00 00 00 00 00 00 00 00 00 00
				0X00, (byte)0X92, (byte)0X92, (byte)0X92, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00
		};

		byte[] welooBytes = new byte[]{
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



		byte[] endByte = new byte[]{(byte)0xA9};

		byte[] welooEndByte0 = new byte[]{(byte)0xAA};

		byte[] welooEndByte = new byte[]{(byte)0x67};

		byte[] weatherEndByte2 = new byte[]{(byte)0x8A};

		mStandardPacket = new byte[startBytes.length + weatherBytes2.length + weatherEndByte2.length];
		System.arraycopy(startBytes, 0, mStandardPacket, 0, startBytes.length);
		System.arraycopy(weatherBytes2, 0, mStandardPacket, startBytes.length, weatherBytes2.length);
		System.arraycopy(weatherEndByte2, 0, mStandardPacket, startBytes.length + weatherBytes2.length, weatherEndByte2.length);
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
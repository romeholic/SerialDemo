package com.welo.serialdemo;

import java.io.IOException;
import java.util.Arrays;

import android.os.Bundle;

public class SendingDataActivity extends SerialPortActivity {

	SendingThread mSendingThread;
	byte[] mBuffer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		android.util.Log.d("WELO-SendingDataActivity", "onCreate");
		setContentView(R.layout.sending_data);
		mBuffer = new byte[1024];
		Arrays.fill(mBuffer, (byte) 0x55);
		if (mSerialPort != null) {
			mSendingThread = new SendingThread();
			mSendingThread.start();
		}
	}

	@Override
	protected void onDataReceived(byte[] buffer, int size) {
		// ignore incoming data
	}

	private class SendingThread extends Thread {
		@Override
		public void run() {
			android.util.Log.d("WELO-SendingDataActivity", "SendingThread run");
			while (!isInterrupted()) {
				try {
					if (mOutputStream != null) {
						mOutputStream.write(mBuffer);
					} else {
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
}

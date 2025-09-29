package com.welo.serialdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.welo.serialport.lib.SerialPort;
import com.welo.serialport.lib.SerialPortManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WelooTestActivity extends SerialPortActivity {
    private static final String TAG = "WelooTestActivity";
    private SerialPortManager mSerialManager;
    private SerialPort mSerialPort;

    private EditText etDevicePath;
    private EditText etBaudrate;
    private EditText etSendData;
    private TextView tvLog;
    private ScrollView scrollView;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weloo_test);

        initViews();
        mSerialManager = SerialPortManager.getInstance(this);
        appendLog("初始化管理器完成");
    }

    private void initViews() {
        etDevicePath = findViewById(R.id.et_device_path);
        etBaudrate = findViewById(R.id.et_baudrate);
        etSendData = findViewById(R.id.et_send_data);
        tvLog = findViewById(R.id.tv_log);
        scrollView = findViewById(R.id.scroll_view);

        // 默认值
        etDevicePath.setText("/dev/ttyS1");
        etBaudrate.setText("115200");
        etSendData.setText("测试数据");

        // 保存配置按钮
        findViewById(R.id.btn_save_config).setOnClickListener(v -> saveConfig());

        // 打开串口按钮
        findViewById(R.id.btn_open).setOnClickListener(v -> openSerialPort());

        // 发送数据按钮
        findViewById(R.id.btn_send).setOnClickListener(v -> sendData());

        // 关闭串口按钮
        findViewById(R.id.btn_close).setOnClickListener(v -> closeSerialPort());
    }

    private void saveConfig() {
        String path = etDevicePath.getText().toString().trim();
        String baudrate = etBaudrate.getText().toString().trim();

        if (path.isEmpty() || baudrate.isEmpty()) {
            showToast("路径和波特率不能为空");
            return;
        }

        mSerialManager.saveSerialPortConfig(path, baudrate);
        appendLog("保存配置成功: " + path + " " + baudrate);
    }

    private void openSerialPort() {
        try {
            mSerialPort = mSerialManager.openSerialPort();
            if (mSerialPort != null && mSerialPort.isOpen()) {
                appendLog("串口打开成功");

                // 设置接收监听
                mSerialPort.setOnDataReceivedListener((buffer, size) -> {
                    String hexData = bytesToHex(buffer, size);
                    appendLog("收到数据 [" + size + "字节]: " + hexData);
                });
            } else {
                appendLog("串口打开失败");
            }
        } catch (Exception e) {
            appendLog("打开失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendData() {
        if (mSerialPort == null || !mSerialPort.isOpen()) {
            showToast("请先打开串口");
            return;
        }

        String sendStr = etSendData.getText().toString().trim();
        if (sendStr.isEmpty()) {
            showToast("发送数据不能为空");
            return;
        }

        try {
            byte[] data = sendStr.getBytes();
            mSerialPort.sendData(data);
            appendLog("发送数据 [" + data.length + "字节]: " + bytesToHex(data, data.length));
        } catch (Exception e) {
            appendLog("发送失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeSerialPort() {
        mSerialManager.closeSerialPort();
        mSerialPort = null;
        appendLog("串口已关闭");
    }

    // 日志显示
    private void appendLog(String content) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String time = sdf.format(new Date());
        String log = "[" + time + "] " + content + "\n";

        mHandler.post(() -> {
            tvLog.append(log);
            // 自动滚动到底部
            scrollView.fullScroll(View.FOCUS_DOWN);
        });
    }

    // 字节转十六进制字符串
    private String bytesToHex(byte[] buffer, int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(buffer[i] & 0xFF);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex).append(' ');
        }
        return sb.toString().toUpperCase();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        // 由于使用了SDK的监听，这里可以留空或做额外处理
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeSerialPort();
        mHandler.removeCallbacksAndMessages(null);
    }
}

###################################sdk使用指导###################################
// 初始化管理器
SerialPortManager manager = SerialPortManager.getInstance(context);

// 保存配置
manager.saveSerialPortConfig("/dev/ttyS1", "115200");

// 打开串口
try {
    SerialPort serialPort = manager.openSerialPort();
    // 设置数据接收监听
    serialPort.setOnDataReceivedListener((buffer, size) -> {
        // 处理接收数据
    });
    
    // 发送数据
    byte[] data = "test".getBytes();
    serialPort.sendData(data);
    
    // 定时发送
    //serialPort.sendDataWithInterval(data, 1000); // 每隔1秒发送一次
} catch (Exception e) {
    e.printStackTrace();
}

// 关闭串口
manager.closeSerialPort();
###################################sdk使用指导###################################

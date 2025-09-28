cmake_minimum_required(VERSION 3.10.2)

# 定义库名称（如"native-lib"），SHARED表示动态库（生成.so）
add_library(
        serial_port  # 库名称（自定义）
        SHARED      # 动态库类型
        SerialPort.c  # 源码文件（多个文件用空格分隔）
)

# 查找Android系统库（如log库，可选）
find_library(
        log-lib  # 变量名，用于后续链接
        log      # 系统库名称
)

# 链接库（将log库链接到我们的native-lib）
target_link_libraries(
        serial_port  # 目标库
        ${log-lib}  # 依赖的系统库
)
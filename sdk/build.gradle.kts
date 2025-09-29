import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.library")
}

android {
    namespace = "com.weloo.serialport.lib"
    compileSdk = 33  // 替换为你的编译版本

    // 配置 NDK 版本（需与本地安装的一致）
    ndkVersion = "27.2.12479018"  // 示例版本，需改为你安装的 NDK 版本

    defaultConfig {
        minSdk = 21
        targetSdk = 33

        // 配置 CMake 构建参数
        externalNativeBuild {
            cmake {
                // 指定需要编译的 CPU 架构
                abiFilters("arm64-v8a", "armeabi-v7a")  // 按需添加
                cppFlags("-std=c++17")  // C++ 标准
            }
        }
    }

    libraryVariants.all {
        val variant = this
        variant.outputs.forEach { output ->
            val fileName = "weloo-serialport-sdk-v1.0.aar"
            (output as BaseVariantOutputImpl).outputFileName = fileName
        }
    }

    // 配置 CMake 路径（指向你的 CMakeLists.txt）
    externalNativeBuild {
        cmake {
            path = file("src/main/jni/CMakeLists.txt")
            version = "3.22.1"  // 本地安装的 CMake 版本
        }
    }
}

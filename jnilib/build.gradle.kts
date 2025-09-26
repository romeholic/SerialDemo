// 必须使用 Android 库插件（通过它集成 NDK 和 CMake）
plugins {
    id("com.android.library")
}

android {
    namespace = "com.welo.serialport.lib"
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

    // 配置 CMake 路径（指向你的 CMakeLists.txt）
    externalNativeBuild {
        cmake {
            path = file("src/main/jni/CMakeLists.txt")
            version = "3.22.1"  // 本地安装的 CMake 版本
        }
    }

    // 禁用 AAR 打包（仅输出 SO 文件）
/*    libraryVariants.all { variant ->
        variant.outputs.all {
            // 取消 AAR 生成任务
            (this as com.android.build.gradle.internal.api.LibraryVariantOutputImpl).assembleProvider.get().enabled = false
        }
    }*/
}

// 可选：添加任务，复制 SO 到指定目录（方便主项目引用）
tasks.register<Copy>("copySoToApp") {
    // SO 源目录（编译后自动生成的路径）
    from("${buildDir}/intermediates/cmake/debug/obj")
    // 目标目录（主 App 的 jniLibs 目录，需手动创建）
    into("${project.rootDir}/app/src/main/jniLibs")
    // 仅复制 SO 文件
    include("**/*.so")
    // 执行时机：在编译 SO 之后
    dependsOn("externalNativeBuildDebug")
}

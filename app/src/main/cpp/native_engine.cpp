#include <jni.h>
#include <android/log.h>

namespace {
constexpr char kLogTag[] = "CastawayDepths";
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_castawaydepths_editor_MainActivity_nativeBootstrapReport(JNIEnv* env, jclass) {
    __android_log_print(ANDROID_LOG_INFO, kLogTag, "JNI bootstrap reached");
    constexpr char report[] =
        "ANDROID APP: loaded\n"
        "ABI: arm64-v8a\n"
        "NATIVE LIBRARY: libcastaway_engine.so\n"
        "JNI ENTRYPOINT: PASS\n"
        "ENGINE CORE: bootstrap only\n"
        "VULKAN: not initialized";
    return env->NewStringUTF(report);
}

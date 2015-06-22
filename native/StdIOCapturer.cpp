#include "StdIOCapturer.h"
#include "com_onsite_chic_StdIOCapturer.h"
#include <stdio.h>
#include <unistd.h>
#include <stdexcept>
#include <iostream>
#include <fstream>

using namespace std;

StdIOCapturer::StdIOCapturer(int descriptor) {
    // TODO: Make sure O_CLOEXEC is set on the new stream
    // TODO: Tee the stdout instead of consuming it
    if ((descriptor != 1) && (descriptor != 2)) {
        throw std::runtime_error("Can only redirect stdout or stderr!");
    }

    if (-1 == pipe(_fileDescriptors)) {
        throw std::runtime_error("pipe failed");
    }

    if (-1 == dup2(_fileDescriptors[1], descriptor)) {
        close();
        throw std::runtime_error("dup2 failed");
    }

    // set proper buffering
    if (1 == descriptor) {
        setvbuf(stdout, NULL, _IOLBF, 0); // stdout should be line buffered
    } else {
        setvbuf(stderr, NULL, _IONBF, 0); // stderr no buffer
    }
}

string StdIOCapturer::read() {
    ssize_t res = ::read(_fileDescriptors[0], _buffer, 8192);
    string data;

    switch (res) {
    case 0:
        return data; // stream finished, just return empty string
    case -1:
        throw std::runtime_error("read failed");
        break;
    default:
        data = string(_buffer, res);  // copy buf to string and return
        return data;
    }
}

void StdIOCapturer::close() {
    ::close(_fileDescriptors[0]);
    ::close(_fileDescriptors[1]);
}

JNIEXPORT jlong JNICALL Java_com_onsite_chic_StdIOCapturer_initNative(JNIEnv *env, jobject jobj, jint descriptor) {
    StdIOCapturer *obj = new StdIOCapturer((int) descriptor);
    return (long) obj;
}

JNIEXPORT void JNICALL Java_com_onsite_chic_StdIOCapturer_destroyNative(JNIEnv *env, jobject jobj, jlong pointer) {
    StdIOCapturer *obj = (StdIOCapturer *) pointer;
    delete obj;
}

JNIEXPORT jstring JNICALL Java_com_onsite_chic_StdIOCapturer_read(JNIEnv *env, jobject jobj, jlong pointer) {
    StdIOCapturer *obj = (StdIOCapturer *) pointer;
    return env->NewStringUTF(obj->read().c_str());
}

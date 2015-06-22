#pragma once
#include <string>

class StdIOCapturer {
    int _fileDescriptors[2];
    char _buffer[8192];

    public:

    StdIOCapturer(int descriptor);
    std::string read();
    void close();
};

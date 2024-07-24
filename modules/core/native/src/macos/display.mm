#import "grapl-macos.h"

#import <IOKit/graphics/IOGraphicsLib.h>

#import <Foundation/Foundation.h>
#import <IOKit/IOKitLib.h>

#include <cstring>

extern "C" {
    typedef CFTypeRef IOAVServiceRef;
    extern IOAVServiceRef IOAVServiceCreateWithService(CFAllocatorRef allocator, io_service_t service);
    extern IOReturn IOAVServiceCopyEDID(IOAVServiceRef service, CFDataRef* edidData);
}

static CGDirectDisplayID getDisplayID(NSScreen* screen){
    NSDictionary *description = [screen deviceDescription];
    return (CGDirectDisplayID)[[description objectForKey:@"NSScreenNumber"] unsignedIntValue];
}

static CFArrayRef getDisplayModes(CGDirectDisplayID displayId){
    CFStringRef keys[1]{
        kCGDisplayShowDuplicateLowResolutionModes
    };
    CFBooleanRef values[1]{
        kCFBooleanTrue
    };
    CFDictionaryRef dictionary = CFDictionaryCreate(
        kCFAllocatorDefault, (const void **) keys, (const void **) values, 1,
        &kCFCopyStringDictionaryKeyCallBacks,
        &kCFTypeDictionaryValueCallBacks);

    CFArrayRef modes = CGDisplayCopyAllDisplayModes(displayId, dictionary);
    CFRelease(dictionary);
    return modes;
}

static int getDisplayBitsPerPixel(CGDisplayModeRef mode){
    int result = 32;
#if MAC_OS_X_VERSION_MAX_ALLOWED == 101100
    CFStringRef format = CGDisplayModeCopyPixelEncoding(mode);
    if (CFStringCompare(format, CFSTR(IO16BitDirectPixels), 0) == 0)
        result = 16;
    CFRelease(format);
#endif
    return result;
}


jni_macos_display(jlong, nGetMainScreen)(JNIEnv* env, jobject) {
    return (jlong)[NSScreen mainScreen];
}

jni_macos_display(jlongArray, nGetScreens)(JNIEnv* env, jobject) {
    NSArray<NSScreen*>* screensArray = [NSScreen screens];

    int count = [screensArray count];
    NSScreen** screens = new NSScreen*[count];
    [screensArray getObjects:screens range:NSMakeRange(0, count)];
    jlongArray result = createLongArray(env, count, (jlong*)screens);
    delete[] screens;
    return result;
}

jni_macos_display(jdoubleArray, nGetSize)(JNIEnv* env, jobject, jlong _screen) {
    NSScreen* screen = (NSScreen*)_screen;
    return createDoubleArray(env, {
        screen.frame.size.width,
        screen.frame.size.height
    });
}

jni_macos_display(jdoubleArray, nGetPosition)(JNIEnv* env, jobject, jlong _screen) {
    NSScreen* screen = (NSScreen*)_screen;
    return createDoubleArray(env, {
        screen.frame.origin.x,
        screen.frame.origin.y
    });
}

jni_macos_display(jdoubleArray, nGetPhysicalSize)(JNIEnv* env, jobject, jlong _screen) {
    NSScreen* screen = (NSScreen*)_screen;
    CGSize displayPhysicalSize = CGDisplayScreenSize(getDisplayID(screen));

    return createDoubleArray(env, {
        displayPhysicalSize.width,
        displayPhysicalSize.height
    });
}

jni_macos_display(jdouble, nGetDpi)(JNIEnv* env, jobject, jlong _screen) {
    NSScreen* screen = (NSScreen*)_screen;
    return [screen backingScaleFactor];
}

jni_macos_display(jdouble, nGetFrequency)(JNIEnv* env, jobject, jlong _screen) {
    NSScreen* screen = (NSScreen*)_screen;
    return [screen maximumFramesPerSecond];
}

jni_macos_display(jint, nGetIndex)(JNIEnv* env, jobject, jlong _screen) {
    NSScreen* screen = (NSScreen*)_screen;
    return (jint)getDisplayID(screen);
}

jni_macos_display(jintArray, nGetDisplayModes)(JNIEnv* env, jobject, jlong _screen) {
    NSScreen* screen = (NSScreen*)_screen;

    CGDirectDisplayID displayId = getDisplayID(screen);
    CFArrayRef modes = getDisplayModes(displayId);

    const int fields = 4;
    jint* result = new jint[CFArrayGetCount(modes) * fields];
    for(int i = 0; i < CFArrayGetCount(modes); i++){
        CGDisplayModeRef mode = (CGDisplayModeRef)CFArrayGetValueAtIndex(modes, i);
        result[fields*i  ] = CGDisplayModeGetWidth(mode);
        result[fields*i+1] = CGDisplayModeGetHeight(mode);
        result[fields*i+2] = getDisplayBitsPerPixel(mode);
        result[fields*i+3] = CGDisplayModeGetRefreshRate(mode);
    }
    return createIntArray(env, CFArrayGetCount(modes) * fields, result);
}

jni_macos_display(jintArray, nGetCurrentDisplayMode)(JNIEnv* env, jobject, jlong _screen) {
    NSScreen* screen = (NSScreen*)_screen;
    CGDisplayModeRef mode = CGDisplayCopyDisplayMode(getDisplayID(screen));

    return createIntArray(env, {
        (jint) CGDisplayModeGetWidth(mode),
        (jint) CGDisplayModeGetHeight(mode),
        (jint) getDisplayBitsPerPixel(mode),
        (jint) CGDisplayModeGetRefreshRate(mode)
    });
}

jni_macos_display(jbyteArray, nGetEDID)(JNIEnv* env, jobject, jlong _screen) {
    NSScreen* screen = (NSScreen*)_screen;
    CGDirectDisplayID displayId = getDisplayID(screen);

    if(CGDisplayIsBuiltin(displayId)){
        // Emulate EDID for built-in display

        char* edid = new char[128];
        memset(edid, 0, 128);

        // Fixed header pattern
        edid[1] = 0xFF;
        edid[2] = 0xFF;
        edid[3] = 0xFF;
        edid[4] = 0xFF;
        edid[5] = 0xFF;
        edid[6] = 0xFF;

        // Manufacturer ID
        uint32_t manufacturer = CGDisplayVendorNumber(displayId);
        edid[9] = manufacturer;
        edid[8] = manufacturer >> 8;

        // Product ID
        uint32_t modelNumber = CGDisplayModelNumber(displayId);
        edid[10] = modelNumber;
        edid[11] = modelNumber >> 8;

        // Serial number
        uint32_t serialNumber = CGDisplaySerialNumber(displayId);
        edid[12] = serialNumber;
        edid[13] = serialNumber >> 8;
        edid[14] = serialNumber >> 16;
        edid[15] = serialNumber >> 24;

        // EDID version
        edid[18] = 1;
        edid[19] = 3;

        // Is digital
        edid[20] = 0b10000000;

        // Size (mm)
        CGSize displayPhysicalSize = CGDisplayScreenSize(displayId);
        edid[21] = displayPhysicalSize.width / 10;
        edid[22] = displayPhysicalSize.height / 10;
        edid[54] = 1;
        edid[55] = 1;
        edid[54 + 12] = displayPhysicalSize.width;
        edid[54 + 13] = displayPhysicalSize.height;
        edid[54 + 14] = ((int)displayPhysicalSize.width >> 4) & 0b11110000;
        edid[54 + 14] |= ((int)displayPhysicalSize.height >> 8) & 0b00001111;

        // Name
        const char* name = "Built-in";
        const int len = strlen(name);
        edid[72 + 3] = 0xFC;
        for(int i = 0; i < (len > 13 ? 13 : len); i++)
            edid[72 + 5 + i] = name[i];
        if(len < 13)
            edid[72 + 5 + len] = 10;
        if(len < 12)
            edid[72 + 5 + len + 1] = ' ';

        return createByteArray(env, 128, (jbyte*)edid);
    }

    // Iterate over external displays

    uint32_t vendorId = CGDisplayVendorNumber(displayId);
    uint32_t productId = CGDisplayModelNumber(displayId);

    io_iterator_t iterator;
    io_service_t service;

#ifndef TARGET_CPU_ARM64    // For x86 (intel)

    CFDictionaryRef info;

    if (IOServiceGetMatchingServices(MACH_PORT_NULL, IOServiceMatching("IODisplayConnect"), &iterator) != 0)
        return createByteArray(env, 0, (jbyte*)0);

    while ((service = IOIteratorNext(iterator)) != 0){
        info = IODisplayCreateInfoDictionary(service, kIODisplayOnlyPreferredName);

        CFNumberRef vendorIDRef = (CFNumberRef)CFDictionaryGetValue(info, CFSTR(kDisplayVendorID));
        CFNumberRef productIDRef = (CFNumberRef)CFDictionaryGetValue(info, CFSTR(kDisplayProductID));
        if (!vendorIDRef || !productIDRef) {
            CFRelease(info);
            continue;
        }

        unsigned int currentVendorId, currentProductId;
        CFNumberGetValue(vendorIDRef, kCFNumberIntType, &currentVendorId);
        CFNumberGetValue(productIDRef, kCFNumberIntType, &currentProductId);

        if (vendorId != currentVendorId || productId != currentProductId) {
            CFRelease(info);
            continue;
        }

        CFDataRef edidData = CFDictionaryGetValue(dict, CFSTR(kIODisplayEDIDKey));

        jbyteArray result = createByteArray(env, CFDataGetLength(edidData), (jbyte*)CFDataGetBytePtr(edidData));
        CFRelease(info);
        IOObjectRelease(iterator);
        return result;
    }

#else   // For apple-silicon

    if (IOServiceGetMatchingServices(kIOMasterPortDefault, IOServiceMatching("DCPAVServiceProxy"), &iterator) != 0)
        return createByteArray(env, 0, (jbyte*)0);

    while ((service = IOIteratorNext(iterator)) != 0) {
        CFStringRef location = (CFStringRef)IORegistryEntrySearchCFProperty(service, kIOServicePlane, CFSTR("Location"), kCFAllocatorDefault, kIORegistryIterateRecursively);
        if (location == NULL || CFStringCompare(CFSTR("External"), location, 0) != 0)
            continue;

        IOAVServiceRef avService = IOAVServiceCreateWithService(kCFAllocatorDefault, service);
        if (avService == NULL)
            continue;

        CFDataRef edidData = NULL;
        IOReturn edidResult = IOAVServiceCopyEDID(avService, &edidData);
        if (edidResult != kIOReturnSuccess || edidData == NULL)
            continue;

        const char* data = (const char*)CFDataGetBytePtr(edidData);
        int currentVendorId = (int)data[8] << 8 | data[9];
        int currentProductId = ((int)data[10] & 0xFF) | ((int)data[11] << 8);

        if(currentVendorId != vendorId || currentProductId != productId)
            continue;

        jbyteArray result = createByteArray(env, CFDataGetLength(edidData), (jbyte*)data);
        IOObjectRelease(iterator);
        return result;
    }
#endif
    IOObjectRelease(iterator);

    return createByteArray(env, 0, (jbyte*)0);
}
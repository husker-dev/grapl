#import "grapl-macos.h"

#import <IOKit/graphics/IOGraphicsLib.h>

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
    CGDirectDisplayID displayId = getDisplayID(screen);

    CFArrayRef modes = getDisplayModes(displayId);

    int actualWidth = 0;
    for(int i = 0; i < CFArrayGetCount(modes); i++){
        CGDisplayModeRef mode = (CGDisplayModeRef)CFArrayGetValueAtIndex(modes, i);

        int width = CGDisplayModeGetPixelWidth(mode);
        if(width == CGDisplayModeGetWidth(mode) && actualWidth < width)
            actualWidth = width;
    }
    return (double)actualWidth / CGDisplayPixelsWide(displayId);
}

jni_macos_display(jdouble, nGetFrequency)(JNIEnv* env, jobject, jlong _screen) {
    NSScreen* screen = (NSScreen*)_screen;
    return [screen maximumFramesPerSecond];
}

jni_macos_display(jstring, nGetName)(JNIEnv* env, jobject, jlong _screen) {
    NSScreen* screen = (NSScreen*)_screen;

    if ([screen respondsToSelector:@selector(localizedName)]){
        NSString* name = [screen valueForKey:@"localizedName"];
        if (name)
            return env->NewStringUTF((const char*)[name UTF8String]);
    }

#ifndef TARGET_CPU_ARM64

    CGDirectDisplayID displayId = getDisplayID(screen);

    io_iterator_t it;
    io_service_t service;
    CFDictionaryRef info;

    // This may happen if a desktop Mac is running headless
    if (IOServiceGetMatchingServices(MACH_PORT_NULL, IOServiceMatching("IODisplayConnect"), &it) != 0)
        return env->NewStringUTF("Display");

    while ((service = IOIteratorNext(it)) != 0){
        info = IODisplayCreateInfoDictionary(service, kIODisplayOnlyPreferredName);

        CFNumberRef vendorIDRef = (CFNumberRef)CFDictionaryGetValue(info, CFSTR(kDisplayVendorID));
        CFNumberRef productIDRef = (CFNumberRef)CFDictionaryGetValue(info, CFSTR(kDisplayProductID));
        if (!vendorIDRef || !productIDRef) {
            CFRelease(info);
            continue;
        }

        unsigned int vendorID, productID;
        CFNumberGetValue(vendorIDRef, kCFNumberIntType, &vendorID);
        CFNumberGetValue(productIDRef, kCFNumberIntType, &productID);

        // Info dictionary is used and freed below
        if (CGDisplayVendorNumber(displayId) == vendorID &&
            CGDisplayModelNumber(displayId) == productID
        ) break;

        CFRelease(info);
    }
    IOObjectRelease(it);

    if (!service)
        return env->NewStringUTF("Display");

    CFDictionaryRef names = (CFDictionaryRef)CFDictionaryGetValue(info, CFSTR(kDisplayProductName));
    CFStringRef nameRef;

    // This may happen if a desktop Mac is running headless
    if (!names || !CFDictionaryGetValueIfPresent(names, CFSTR("en_US"), (const void**) &nameRef)){
        CFRelease(info);
        return env->NewStringUTF("Display");
    }

    const CFIndex size = CFStringGetMaximumSizeForEncoding(CFStringGetLength(nameRef), kCFStringEncodingUTF8);
    char* name = new char[size + 1];
    CFStringGetCString(nameRef, name, size, kCFStringEncodingUTF8);

    jstring result = env->NewStringUTF(name);

    CFRelease(info);
    return result;
#else
    return env->NewStringUTF("Display");
#endif
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
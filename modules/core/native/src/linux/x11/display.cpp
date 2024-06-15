#include "grapl-x11.h"

#include <math.h>
#include <cstring>
#include <stdio.h>

jni_x11_display(jlong, nGetPrimaryScreen)(JNIEnv* env, jobject, jlong _display) {
    Display* display = (Display*)_display;
   	return (jlong)XDefaultScreenOfDisplay(display);
}

jni_x11_display(jlongArray, nGetAllScreens)(JNIEnv* env, jobject, jlong _display) {
    Display* display = (Display*)_display;
    Window root = DefaultRootWindow(display);

    XRRScreenResources* sr = XRRGetScreenResourcesCurrent(display, root);
    int count = sr->noutput;
    XRRFreeScreenResources(sr);

    jlong* screens = new jlong[count];
    for(int i = 0; i < count; i++)
        screens[i] = i;

    jlongArray result = createLongArray(env, count, screens);
    delete[] screens;
    return result;
}

jni_x11_display(jintArray, nGetSize)(JNIEnv* env, jobject, jlong _display, jlong index) {
    Display* display = (Display*)_display;
    Window root = DefaultRootWindow(display);

    XRRScreenResources* sr = XRRGetScreenResourcesCurrent(display, root);
    XRROutputInfo* oi = XRRGetOutputInfo(display, sr, sr->outputs[index]);
    XRRCrtcInfo* ci = XRRGetCrtcInfo(display, sr, oi->crtc);

    jintArray result = createIntArray(env, {
        ci->width,
        ci->height
    });

    XRRFreeOutputInfo(oi);
    XRRFreeCrtcInfo(ci);
    XRRFreeScreenResources(sr);
    return result;
}

jni_x11_display(jintArray, nGetPhysicalSize)(JNIEnv* env, jobject, jlong _display, jlong index) {
    Display* display = (Display*)_display;
    Window root = DefaultRootWindow(display);

    XRRScreenResources* sr = XRRGetScreenResourcesCurrent(display, root);
    XRROutputInfo* oi = XRRGetOutputInfo(display, sr, sr->outputs[index]);
    XRRCrtcInfo* ci = XRRGetCrtcInfo(display, sr, oi->crtc);

    int widthMM, heightMM;

    if (ci->rotation == RR_Rotate_90 || ci->rotation == RR_Rotate_270){
        widthMM  = oi->mm_height;
        heightMM = oi->mm_width;
    } else {
        widthMM  = oi->mm_width;
        heightMM = oi->mm_height;
    }
    if (widthMM <= 0 || heightMM <= 0) {
        widthMM  = (int) (ci->width * 25.4f / 96.f);
        heightMM = (int) (ci->height * 25.4f / 96.f);
    }

    XRRFreeOutputInfo(oi);
    XRRFreeScreenResources(sr);
    XRRFreeCrtcInfo(ci);

    return createIntArray(env, {
       widthMM,
       heightMM
   });;
}

jni_x11_display(jintArray, nGetPosition)(JNIEnv* env, jobject, jlong _display, jlong index) {
    Display* display = (Display*)_display;
    Window root = DefaultRootWindow(display);

    XRRScreenResources* sr = XRRGetScreenResourcesCurrent(display, root);
    XRROutputInfo* oi = XRRGetOutputInfo(display, sr, sr->outputs[index]);
    XRRCrtcInfo* ci = XRRGetCrtcInfo(display, sr, oi->crtc);

    jintArray result = createIntArray(env, {
        ci->x,
        ci->y
    });

    XRRFreeOutputInfo(oi);
    XRRFreeCrtcInfo(ci);
    XRRFreeScreenResources(sr);
    return result;
}

jni_x11_display(jint, nGetFrequency)(JNIEnv* env, jobject, jlong _display, jlong index) {
    Display* display = (Display*)_display;
    Window root = DefaultRootWindow(display);

    XRRScreenResources* sr = XRRGetScreenResourcesCurrent(display, root);
    XRROutputInfo* oi = XRRGetOutputInfo(display, sr, sr->outputs[index]);
    XRRCrtcInfo* ci = XRRGetCrtcInfo(display, sr, oi->crtc);

    RRMode modeId = ci->mode;

    double rate = 0;
    for (int i = 0; i < sr->nmode; i++) {
        XRRModeInfo mode = sr->modes[i];
        if (mode.id == modeId) {
            rate = (double)mode.dotClock / ((double)mode.hTotal * (double)mode.vTotal);
            break;
        }
    }

    XRRFreeOutputInfo(oi);
    XRRFreeCrtcInfo(ci);
    XRRFreeScreenResources(sr);
    return round(rate);
}

jni_x11_display(jstring, nGetName)(JNIEnv* env, jobject, jlong _display, jlong index) {
    Display* display = (Display*)_display;
    Window root = DefaultRootWindow(display);

    XRRScreenResources* sr = XRRGetScreenResourcesCurrent(display, root);

    int nprop = 0;
    Atom* props = XRRListOutputProperties(display, sr->outputs[index], &nprop);

    for (int i = 0; i < nprop; i++) {
        char* atom_name = XGetAtomName(display, props[i]);
        Bool is_edid = strcmp(atom_name, "EDID") == 0;

        if(is_edid){
            unsigned char* prop;
            int actual_format;
            unsigned long nitems, bytes_after;
            Atom actual_type;

            XRRGetOutputProperty(display, sr->outputs[index], props[i],
                      0, 100, False, False,
                      AnyPropertyType,
                      &actual_type, &actual_format,
                      &nitems, &bytes_after, &prop);

            for(int r = 0; r < nitems - 4; r++){
                if(prop[r] == 0 && prop[r+1] == 0 && prop[r+2] == 0 && (prop[r+3] == 0xFC || prop[r+3] == 0xFE)){
                    r += 4;
                    int a = 0;

                    while(prop[r+a] != 0 && prop[r+a+1] != 0 && prop[r+a+2] != 0){
                        a++;
                    }

                    char* name = new char[a];
                    memcpy(name, &prop[r], a);

                    XRRFreeScreenResources(sr);
                    return env->NewStringUTF(name);
                }
                // 00 00 00 FC
            }
        }
    }

    XRRFreeScreenResources(sr);
    return env->NewStringUTF("Virtual screen");
}

jni_x11_display(jstring, nGetSystemName)(JNIEnv* env, jobject, jlong _display, jlong index) {
    Display* display = (Display*)_display;
    Window root = DefaultRootWindow(display);

    XRRScreenResources* sr = XRRGetScreenResourcesCurrent(display, root);
    XRROutputInfo* oi = XRRGetOutputInfo(display, sr, sr->outputs[index]);

    jstring result = env->NewStringUTF(oi->name);

    XRRFreeOutputInfo(oi);
    XRRFreeScreenResources(sr);

    return result;
}
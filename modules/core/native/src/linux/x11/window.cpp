#include "grapl-x11.h"



jni_x11_window(jlong, nCreateWindow)(JNIEnv* env, jobject, jlong _display) {
/*

	unsigned long black,white;
    Display* display = (Display*)_display;

   	int screen = DefaultScreen(display);

   	Window window = XCreateSimpleWindow(
   	    display,
   	    DefaultRootWindow(display),
   	    0, 0,
		200, 300,
		5,
		WhitePixel(display, screen), BlackPixel(display, screen)
    );

	XSetStandardProperties(display, window, "My Window", "HI!", None, NULL, 0, NULL);

	XSelectInput(display, window, ExposureMask | ButtonPressMask | KeyPressMask);

	XMapRaised(display, window);
	*/
	return 0;
}

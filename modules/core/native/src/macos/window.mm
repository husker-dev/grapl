#include "macos-shared.h"
#import <Cocoa/Cocoa.h>

static NSAutoreleasePool* pool;


@interface View : NSView <NSWindowDelegate> {
    JNIEnv* env;
    jobject object;
    jmethodID onCloseCallback;
    jmethodID onResizeCallback;
    jmethodID onMoveCallback;
    jmethodID getCursorCallback;

    @public
    NSCursor* cursor;
    NSTrackingArea* trackingArea;
}
@end

@implementation View
-(void) bindCallback:(jobject)_callbackObject env:(JNIEnv*)_env {
    env = _env;
    object = env->NewGlobalRef(_callbackObject);

    jclass callbackClass = env->GetObjectClass(_callbackObject);

    onCloseCallback = env->GetMethodID(callbackClass, "onCloseCallback", "()V");
    onResizeCallback = env->GetMethodID(callbackClass, "onResizeCallback", "(DD)V");
    onMoveCallback = env->GetMethodID(callbackClass, "onMoveCallback", "(DD)V");
    //getCursorCallback = env->GetMethodID(callbackClass, "getCursorCallback", "()I");
}
- (void) dealloc {
    [super dealloc];
    // Cause crash for some reason...
	//env->DeleteGlobalRef(object);
}

/* ====================
          Events
   ==================== */

-(void) windowWillClose:(NSNotification*)notification {
    env->CallVoidMethod(object, onCloseCallback);
}

- (void) windowDidResize:(NSNotification*)notification {
    NSSize size = [self window].frame.size;
	env->CallVoidMethod(object, onResizeCallback, size.width, size.height);
}

- (void) windowDidMove:(NSNotification*)notification {
    NSPoint origin = [self window].frame.origin;
	env->CallVoidMethod(object, onMoveCallback, origin.x, origin.y);
}

/* ====================
          Cursor
   ==================== */

- (BOOL) acceptsFirstResponder{
    return YES;
}

- (BOOL) wantsUpdateLayer{
    return YES;
}

- (BOOL) acceptsFirstMouse:(NSEvent*)event{
    return YES;
}

- (void) cursorUpdate:(NSEvent*)event{
    if(cursor != nil)
        [cursor set];
    else
        [[NSCursor arrowCursor] set];
}

- (void) updateTrackingAreas{
    if (trackingArea != nil) {
        [self removeTrackingArea:trackingArea];
        [trackingArea release];
    }

    const NSTrackingAreaOptions options = NSTrackingMouseEnteredAndExited |
                                          NSTrackingActiveInKeyWindow |
                                          NSTrackingEnabledDuringMouseDrag |
                                          NSTrackingCursorUpdate |
                                          NSTrackingInVisibleRect |
                                          NSTrackingAssumeInside;

    trackingArea = [[NSTrackingArea alloc] initWithRect:[self bounds]
                                                options:options
                                                  owner:self
                                               userInfo:nil];

    [self addTrackingArea:trackingArea];
    [super updateTrackingAreas];
}

@end

jni_macos_window(void, nInitApplication)(JNIEnv* env, jobject) {
    pool = [[NSAutoreleasePool alloc] init];

    [NSApplication sharedApplication];
    [NSApp setActivationPolicy:NSApplicationActivationPolicyRegular];
}

jni_macos_window(jlong, nCreateWindow)(JNIEnv* env, jobject, jobject callbackObject) {
    NSRect windowRect = NSMakeRect(0, 0, 800, 600);

    NSWindow* window = [[NSWindow alloc] initWithContentRect:windowRect
                                                   styleMask:NSWindowStyleMaskTitled | NSWindowStyleMaskClosable | NSWindowStyleMaskResizable
                                                     backing:NSBackingStoreBuffered
                                                       defer:NO];

    NSWindowController* windowController = [[NSWindowController alloc] initWithWindow:window];
    [windowController autorelease];

    [window setCollectionBehavior: NSWindowCollectionBehaviorFullScreenPrimary];
    [window autorelease];

    [window setAcceptsMouseMovedEvents:YES];
    [window setRestorable:NO];

    View* view = [[[View alloc] initWithFrame:windowRect] autorelease];
    [view bindCallback:callbackObject env:env];
    [window setContentView:view];
    [window setDelegate:view];
    [window makeFirstResponder:view];

    return (jlong)window;
}

jni_macos_window(void, nPeekMessage)(JNIEnv* env, jobject, jlong _windowPtr) {
    NSWindow* window = (NSWindow*)_windowPtr;

    @autoreleasepool {
        for (;;) {
            NSEvent* event = [NSApp nextEventMatchingMask:NSEventMaskAny
                                                untilDate:[NSDate distantPast]
                                                   inMode:NSDefaultRunLoopMode
                                                  dequeue:YES];
            if (event == nil)
                break;

            [NSApp sendEvent:event];
        }
    }
}

jni_macos_window(void, nCloseWindow)(JNIEnv* env, jobject, jlong _windowPtr) {
    NSWindow* window = (NSWindow*)_windowPtr;
    [window close];
}

jni_macos_window(void, nSetVisible)(JNIEnv* env, jobject, jlong _windowPtr, jboolean visible) {
    NSWindow* window = (NSWindow*)_windowPtr;

    @autoreleasepool {
        if(visible) {
            [window orderFrontRegardless];
            //[window orderFront:nil];
            //[window makeKeyAndOrderFront:nil];
        }else
            [window orderOut:nil];
    }
}

jni_macos_window(void, nSetTitle)(JNIEnv* env, jobject, jlong _windowPtr, jobject _title) {
    NSWindow* window = (NSWindow*)_windowPtr;
    char* title = (char*)env->GetDirectBufferAddress(_title);

    @autoreleasepool {
        NSString* string = @(title);
        [window setTitle:string];
        [window setMiniwindowTitle:string];
    }
}

jni_macos_window(void, nSetPosition)(JNIEnv* env, jobject, jlong _windowPtr, jint x, jint y) {
    NSWindow* window = (NSWindow*)_windowPtr;

    @autoreleasepool {
        NSPoint origin = { x, y };
        [window setFrameOrigin:origin];
    }
}

jni_macos_window(void, nSetSize)(JNIEnv* env, jobject, jlong _windowPtr, jint width, jint height) {
    NSWindow* window = (NSWindow*)_windowPtr;

    @autoreleasepool {
        NSPoint location = window.frame.origin;
        [window setFrame:NSMakeRect(location.x, location.y, width, height) display:YES animate:YES];
    }
}

jni_macos_window(void, nSetCursor)(JNIEnv* env, jobject, jlong _windowPtr, jint index) {
    NSWindow* window = (NSWindow*)_windowPtr;
    View* view = [window contentView];

    if(index == 0)       view->cursor = [NSCursor arrowCursor];
    else if(index == 1)  view->cursor = [NSCursor pointingHandCursor];
    else if(index == 2)  view->cursor = [NSCursor IBeamCursor];
    //else if(index == 3)  view->cursor = [NSCursor performSelector:@selector(_waitCursor)];
    //else if(index == 4)  view->cursor = [NSCursor performSelector:@selector(_waitCursor)];
    else if(index == 5)  view->cursor = [NSCursor crosshairCursor];
    else if(index == 6)  view->cursor = [NSCursor operationNotAllowedCursor];
    //else if(index == 7)  view->cursor = [NSCursor arrowCursor];                             // Fallback
    else if(index == 8)  view->cursor = [NSCursor resizeLeftRightCursor];
    else if(index == 9)  view->cursor = [NSCursor resizeUpDownCursor];
    else if(index == 10) view->cursor = [NSCursor performSelector:@selector(_windowResizeNorthEastSouthWestCursor)];
    else if(index == 11) view->cursor = [NSCursor performSelector:@selector(_windowResizeNorthWestSouthEastCursor)];
    else if(index == 12) view->cursor = [NSCursor openHandCursor];
    else                 view->cursor = [NSCursor arrowCursor];
}
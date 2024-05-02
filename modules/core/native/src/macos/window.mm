#import "grapl-macos.h"

static NSAutoreleasePool* pool;
static JNIEnv* env;


@interface View : NSView <NSWindowDelegate> {
    jobject object;
    jmethodID onCloseCallback;
    jmethodID onResizeCallback;
    jmethodID onMoveCallback;

    jmethodID onPointerMoveCallback;
    jmethodID onPointerDownCallback;
    jmethodID onPointerUpCallback;
    jmethodID onPointerEnterCallback;
    jmethodID onPointerLeaveCallback;

    @public
    NSCursor* cursor;
    NSTrackingArea* trackingArea;
}
@end

@implementation View
-(void) bindCallback:(jobject)_callbackObject {
    object = env->NewGlobalRef(_callbackObject);

    jclass callbackClass = env->GetObjectClass(_callbackObject);

    onCloseCallback = env->GetMethodID(callbackClass, "onCloseCallback", "()V");
    onResizeCallback = env->GetMethodID(callbackClass, "onResizeCallback", "(DD)V");
    onMoveCallback = env->GetMethodID(callbackClass, "onMoveCallback", "(DD)V");

    onPointerMoveCallback = env->GetMethodID(callbackClass, "onPointerMoveCallback", "(IDD)V");
    onPointerDownCallback = env->GetMethodID(callbackClass, "onPointerDownCallback", "(IDDI)V");
    onPointerUpCallback = env->GetMethodID(callbackClass, "onPointerUpCallback", "(IDDI)V");
    onPointerEnterCallback = env->GetMethodID(callbackClass, "onPointerEnterCallback", "(IDD)V");
    onPointerLeaveCallback = env->GetMethodID(callbackClass, "onPointerLeaveCallback", "(IDD)V");
}
-(void) dealloc {
    [super dealloc];
    // Cause crash for some reason...
	//env->DeleteGlobalRef(object);
}

/* ====================
      Window events
   ==================== */

-(void) windowWillClose:(NSNotification*)notification {
    env->CallVoidMethod(object, onCloseCallback);
}

-(void) windowDidResize:(NSNotification*)notification {
    NSSize size = [self window].frame.size;
	env->CallVoidMethod(object, onResizeCallback, size.width, size.height);
}

-(void) windowDidMove:(NSNotification*)notification {
    NSPoint origin = [self window].frame.origin;
	env->CallVoidMethod(object, onMoveCallback, origin.x, origin.y);
}

/* ====================
       Mouse events
   ==================== */

-(void) sendMouseEvent:(NSEvent*)event callback:(jmethodID)callback pointerId:(int)pointerId {
    const NSPoint pos = [event locationInWindow];
    const NSSize size = [self window].contentView.frame.size;
    env->CallVoidMethod(object, callback, pointerId, pos.x, size.height - pos.y);
}

-(void) sendMouseButtonEvent:(NSEvent*)event callback:(jmethodID)callback pointerId:(int)pointerId button:(int)button {
    const NSPoint pos = [event locationInWindow];
    const NSSize size = [self window].contentView.frame.size;
    env->CallVoidMethod(object, callback, pointerId, pos.x, size.height - pos.y, button);
}

-(void) mouseEntered:(NSEvent*)event {
    [self sendMouseEvent:event callback:onPointerEnterCallback pointerId:0];
}

-(void) mouseMoved:(NSEvent*)event {
    [self sendMouseEvent:event callback:onPointerMoveCallback pointerId:0];
}

-(void) mouseExited:(NSEvent*)event {
    env->CallVoidMethod(object, onPointerLeaveCallback, 0, 0, 0);
}

// Left button
-(void) mouseDown:(NSEvent*)event{
    [self sendMouseButtonEvent:event callback:onPointerDownCallback pointerId:0 button:1];
}

-(void) mouseDragged:(NSEvent*)event{
    [self sendMouseEvent:event callback:onPointerMoveCallback pointerId:0];
}

-(void) mouseUp:(NSEvent*)event{
    [self sendMouseButtonEvent:event callback:onPointerUpCallback pointerId:0 button:1];
}

// Right button
-(void) rightMouseDown:(NSEvent*)event{
    [self sendMouseButtonEvent:event callback:onPointerDownCallback pointerId:0 button:3];
}

-(void) rightMouseDragged:(NSEvent*)event{
    [self sendMouseEvent:event callback:onPointerMoveCallback pointerId:0];
}

-(void) rightMouseUp:(NSEvent*)event{
    [self sendMouseButtonEvent:event callback:onPointerUpCallback pointerId:0 button:3];
}

// Other button
-(void) otherMouseDown:(NSEvent*)event{
    [self sendMouseButtonEvent:event callback:onPointerDownCallback pointerId:0 button:(int)[event buttonNumber]+1];
}

-(void) otherMouseDragged:(NSEvent*)event{
    [self sendMouseEvent:event callback:onPointerMoveCallback pointerId:0];
}

-(void) otherMouseUp:(NSEvent*)event{
    [self sendMouseButtonEvent:event callback:onPointerUpCallback pointerId:0 button:(int)[event buttonNumber]+1];
}

/* ====================
          Cursor
   ==================== */

-(BOOL) acceptsFirstResponder{
    return YES;
}

-(BOOL) wantsUpdateLayer{
    return YES;
}

-(BOOL) acceptsFirstMouse:(NSEvent*)event{
    return YES;
}

-(void) cursorUpdate:(NSEvent*)event{
    if(cursor != nil)
        [cursor set];
    else
        [[NSCursor arrowCursor] set];
}

-(void) updateTrackingAreas{
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

jni_macos_window(void, nInitApplication)(JNIEnv* _env, jobject) {
    JavaVM* jvm;
    _env->GetJavaVM(&jvm);

    ON_MAIN_THREAD(
        jvm->AttachCurrentThread((void**)&env, NULL);

        pool = [[NSAutoreleasePool alloc] init];

        [NSApplication sharedApplication];
        [NSApp setActivationPolicy:NSApplicationActivationPolicyRegular];
    );
}

jni_macos_window(jlong, nCreateWindow)(JNIEnv* env, jobject, jobject callbackObject) {
    __block NSWindow* window = nil;

    ON_MAIN_THREAD(
        NSRect windowRect = NSMakeRect(0, 0, 100, 100);

        window = [[NSWindow alloc] initWithContentRect:windowRect
                                                       styleMask:NSWindowStyleMaskTitled | NSWindowStyleMaskClosable | NSWindowStyleMaskResizable | NSWindowStyleMaskMiniaturizable
                                                         backing:NSBackingStoreBuffered
                                                           defer:NO];

        NSWindowController* windowController = [[NSWindowController alloc] initWithWindow:window];
        [windowController autorelease];

        [window setCollectionBehavior: NSWindowCollectionBehaviorFullScreenPrimary | NSWindowCollectionBehaviorManaged];
        //[window autorelease];

        [window setAcceptsMouseMovedEvents:YES];
        [window setRestorable:NO];
        [window setReleasedWhenClosed: NO];

        View* view = [[[View alloc] initWithFrame:windowRect] autorelease];
        [view bindCallback:callbackObject];
        [window setContentView:view];
        [window setDelegate:view];
        [window makeFirstResponder:view];
    );
    return (jlong)window;
}

jni_macos_window(void, nPeekMessage)(JNIEnv* env, jobject) {
    ON_MAIN_THREAD(
        for (;;) {
            NSEvent* event = [NSApp nextEventMatchingMask:NSEventMaskAny
                                                untilDate:[NSDate distantPast]
                                                   inMode:NSDefaultRunLoopMode
                                                  dequeue:YES];
            if (event == nil)
                break;
            [NSApp sendEvent:event];
        }
    );
}

jni_macos_window(void, nCloseWindow)(JNIEnv* env, jobject, jlong _windowPtr) {
    ON_MAIN_THREAD(
        NSWindow* window = (NSWindow*)_windowPtr;
        [window close];
    );
}

jni_macos_window(void, nSetVisible)(JNIEnv* env, jobject, jlong _windowPtr, jboolean visible) {
    ON_MAIN_THREAD(
        NSWindow* window = (NSWindow*)_windowPtr;
        if(visible)
            [window orderFrontRegardless];
        else
            [window orderOut:nil];
    );
}

jni_macos_window(void, nSetTitle)(JNIEnv* env, jobject, jlong _windowPtr, jobject _title) {
    char* title = (char*)env->GetDirectBufferAddress(_title);
    ON_MAIN_THREAD(
        NSWindow* window = (NSWindow*)_windowPtr;
        NSString* string = @(title);
        [window setTitle:string];
        [window setMiniwindowTitle:string];
    );
}

jni_macos_window(void, nSetPosition)(JNIEnv* env, jobject, jlong _windowPtr, jdouble x, jdouble y) {
    ON_MAIN_THREAD(
        NSWindow* window = (NSWindow*)_windowPtr;
        NSPoint origin = { x, y };
        [window setFrameOrigin:origin];
    );
}

jni_macos_window(void, nSetSize)(JNIEnv* env, jobject, jlong _windowPtr, jdouble width, jdouble height) {
    ON_MAIN_THREAD(
        NSWindow* window = (NSWindow*)_windowPtr;

        NSRect contentRect = [window contentRectForFrameRect:[window frame]];
        contentRect.origin.y += contentRect.size.height - height;
        contentRect.size = NSMakeSize(width, height);

        [window setFrame:[window frameRectForContentRect:contentRect] display:YES animate:YES];
    );
}

jni_macos_window(void, nSetMinSize)(JNIEnv* env, jobject, jlong _windowPtr, jint width, jint height) {
    ON_MAIN_THREAD(
        NSWindow* window = (NSWindow*)_windowPtr;
        [window setMinSize:NSMakeSize(width, height)];
    );
}

jni_macos_window(void, nSetMaxSize)(JNIEnv* env, jobject, jlong _windowPtr, jint width, jint height) {
    ON_MAIN_THREAD(
        NSWindow* window = (NSWindow*)_windowPtr;
        [window setMaxSize:NSMakeSize(width, height)];
    );
}

jni_macos_window(void, nSetCursor)(JNIEnv* env, jobject, jlong _windowPtr, jint index) {
    ON_MAIN_THREAD(
        NSWindow* window = (NSWindow*)_windowPtr;
        View* view = [window contentView];

        if(index == 0)       view->cursor = [NSCursor arrowCursor];
        else if(index == 1)  view->cursor = [NSCursor pointingHandCursor];
        else if(index == 2)  view->cursor = [NSCursor IBeamCursor];
        //else if(index == 3)
        //else if(index == 4)
        else if(index == 5)  view->cursor = [NSCursor crosshairCursor];
        else if(index == 6)  view->cursor = [NSCursor operationNotAllowedCursor];
        //else if(index == 7)
        else if(index == 8)  view->cursor = [NSCursor resizeLeftRightCursor];
        else if(index == 9)  view->cursor = [NSCursor resizeUpDownCursor];
        else if(index == 10) view->cursor = [NSCursor performSelector:@selector(_windowResizeNorthEastSouthWestCursor)];
        else if(index == 11) view->cursor = [NSCursor performSelector:@selector(_windowResizeNorthWestSouthEastCursor)];
        else if(index == 12) view->cursor = [NSCursor openHandCursor];
        else                 view->cursor = [NSCursor arrowCursor];
    );
}

jni_macos_window(jlong, nGetScreen)(JNIEnv* env, jobject, jlong _windowPtr) {
    __block NSScreen* result;
    ON_MAIN_THREAD(
        NSWindow* window = (NSWindow*)_windowPtr;
        result = [window screen];
    );
    return (jlong)result;
}

jni_macos_window(void, nSetMinimizable)(JNIEnv* env, jobject, jlong _windowPtr, jboolean value) {
    ON_MAIN_THREAD(
        NSWindow* window = (NSWindow*)_windowPtr;
        if(value) window.styleMask |= NSWindowStyleMaskMiniaturizable;
        else      window.styleMask &= ~NSWindowStyleMaskMiniaturizable;
    );
}

jni_macos_window(void, nSetMaximizable)(JNIEnv* env, jobject, jlong _windowPtr, jboolean value) {
    ON_MAIN_THREAD(
        NSWindow* window = (NSWindow*)_windowPtr;
        if(value) [window setCollectionBehavior: NSWindowCollectionBehaviorFullScreenPrimary | NSWindowCollectionBehaviorManaged];
        else      [window setCollectionBehavior: 0];
    );
}
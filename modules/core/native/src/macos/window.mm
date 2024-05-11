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

    jmethodID onPointerScrollCallback;

    jmethodID onPointerZoomBeginCallback;
    jmethodID onPointerZoomCallback;
    jmethodID onPointerZoomEndCallback;

    jmethodID onPointerRotationBeginCallback;
    jmethodID onPointerRotationCallback;
    jmethodID onPointerRotationEndCallback;

    float sumScale;
    float sumRotation;

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

    onPointerMoveCallback = env->GetMethodID(callbackClass, "onPointerMoveCallback", "(IDDI)V");
    onPointerDownCallback = env->GetMethodID(callbackClass, "onPointerDownCallback", "(IDDII)V");
    onPointerUpCallback = env->GetMethodID(callbackClass, "onPointerUpCallback", "(IDDII)V");
    onPointerEnterCallback = env->GetMethodID(callbackClass, "onPointerEnterCallback", "(IDDI)V");
    onPointerLeaveCallback = env->GetMethodID(callbackClass, "onPointerLeaveCallback", "(IDDI)V");

    onPointerScrollCallback = env->GetMethodID(callbackClass, "onPointerScrollCallback", "(IDDDDI)V");

    onPointerZoomBeginCallback = env->GetMethodID(callbackClass, "onPointerZoomBeginCallback", "(IDDI)V");
    onPointerZoomCallback = env->GetMethodID(callbackClass, "onPointerZoomCallback", "(IDDDI)V");
    onPointerZoomEndCallback = env->GetMethodID(callbackClass, "onPointerZoomEndCallback", "(IDDI)V");

    onPointerRotationBeginCallback = env->GetMethodID(callbackClass, "onPointerRotationBeginCallback", "(IDDI)V");
    onPointerRotationCallback = env->GetMethodID(callbackClass, "onPointerRotationCallback", "(IDDDI)V");
    onPointerRotationEndCallback = env->GetMethodID(callbackClass, "onPointerRotationEndCallback", "(IDDI)V");
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
-(jint) getModifierKeys {
    jint result = 0;
    NSEventModifierFlags flags = NSEvent.modifierFlags;
    if(NSEventModifierFlagOption & flags)  result |= 1;
    if(NSEventModifierFlagControl & flags) result |= 2;
    if(NSEventModifierFlagShift & flags)   result |= 4;
    if(NSEventModifierFlagCommand & flags) result |= 8;
    return result;
}

-(void) sendMouseEvent:(NSEvent*)event callback:(jmethodID)callback {
    const NSPoint pos = [event locationInWindow];
    const NSSize size = [self window].contentView.frame.size;
    env->CallVoidMethod(object, callback, 0, pos.x, size.height - pos.y, [self getModifierKeys]);
}

-(void) sendMouseButtonEvent:(NSEvent*)event callback:(jmethodID)callback button:(int)button {
    const NSPoint pos = [event locationInWindow];
    const NSSize size = [self window].contentView.frame.size;
    env->CallVoidMethod(object, callback, event.pointingDeviceID, pos.x, size.height - pos.y, button, [self getModifierKeys]);
}

-(void) mouseEntered:(NSEvent*)event {
    [self sendMouseEvent:event callback:onPointerEnterCallback];
}

-(void) mouseMoved:(NSEvent*)event {
    [self sendMouseEvent:event callback:onPointerMoveCallback];
}

-(void) mouseExited:(NSEvent*)event {
    env->CallVoidMethod(object, onPointerLeaveCallback, 0, 0, 0, [self getModifierKeys]);
}

// Left button
-(void) mouseDown:(NSEvent*)event{
    [self sendMouseButtonEvent:event callback:onPointerDownCallback button:1];
}

-(void) mouseDragged:(NSEvent*)event{
    [self sendMouseEvent:event callback:onPointerMoveCallback];
}

-(void) mouseUp:(NSEvent*)event{
    [self sendMouseButtonEvent:event callback:onPointerUpCallback button:1];
}

// Right button
-(void) rightMouseDown:(NSEvent*)event{
    [self sendMouseButtonEvent:event callback:onPointerDownCallback button:3];
}

-(void) rightMouseDragged:(NSEvent*)event{
    [self sendMouseEvent:event callback:onPointerMoveCallback];
}

-(void) rightMouseUp:(NSEvent*)event{
    [self sendMouseButtonEvent:event callback:onPointerUpCallback button:3];
}

// Other button
-(void) otherMouseDown:(NSEvent*)event{
    [self sendMouseButtonEvent:event callback:onPointerDownCallback button:(int)[event buttonNumber]+1];
}

-(void) otherMouseDragged:(NSEvent*)event{
    [self sendMouseEvent:event callback:onPointerMoveCallback];
}

-(void) otherMouseUp:(NSEvent*)event{
    [self sendMouseButtonEvent:event callback:onPointerUpCallback button:(int)[event buttonNumber]+1];
}

// Gestures
- (void) magnifyWithEvent:(NSEvent *)event {
    const NSPoint pos = [event locationInWindow];
    const NSSize size = [self window].contentView.frame.size;
    if ([event phase] == NSEventPhaseBegan){
        sumScale = 1;
        [self sendMouseEvent:event callback:onPointerZoomBeginCallback];
    }
    sumScale += [event magnification];
    env->CallVoidMethod(object, onPointerZoomCallback,
            0,
            pos.x,
            size.height - pos.y,
            (jdouble)sumScale,
            [self getModifierKeys]);
    if ([event phase] == NSEventPhaseEnded)
        [self sendMouseEvent:event callback:onPointerZoomEndCallback];
}

- (void) rotateWithEvent:(NSEvent *)event {
    const NSPoint pos = [event locationInWindow];
    const NSSize size = [self window].contentView.frame.size;
    if ([event phase] == NSEventPhaseBegan){
        sumRotation = 0;
        [self sendMouseEvent:event callback:onPointerRotationBeginCallback];
    }
    sumRotation += [event rotation];
    env->CallVoidMethod(object, onPointerRotationCallback,
            0,
            pos.x,
            size.height - pos.y,
            (jdouble)sumRotation,
            [self getModifierKeys]);
    if ([event phase] == NSEventPhaseEnded)
        [self sendMouseEvent:event callback:onPointerRotationEndCallback];
}

- (void) scrollWheel:(NSEvent *)event {
    const NSPoint pos = [event locationInWindow];
    const NSSize size = [self window].contentView.frame.size;

    env->CallVoidMethod(object, onPointerScrollCallback,
            0,
            pos.x,
            size.height - pos.y,
            (jdouble)[event deltaX],
            (jdouble)[event deltaY],
            [self getModifierKeys]);
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
        jvm->AttachCurrentThreadAsDaemon((void**)&env, NULL);

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
#import "grapl-macos.h"

static NSAutoreleasePool* pool;
static JNIEnv* env;


/* ====================
      App delegate
   ==================== */
@interface GraplAppDelegate : NSObject <NSApplicationDelegate>
-(BOOL) applicationSupportsSecureRestorableState:(NSApplication*)app;
@end

@implementation GraplAppDelegate : NSObject
-(BOOL) applicationSupportsSecureRestorableState:(NSApplication*)app{
    return YES;
}
@end


/* ====================
           View
   ==================== */
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

    jmethodID onKeyDownCallback;
    jmethodID onKeyUpCallback;

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

    onKeyDownCallback = env->GetMethodID(callbackClass, "onKeyDownCallback", "(III)V");
    onKeyUpCallback = env->GetMethodID(callbackClass, "onKeyUpCallback", "(III)V");
}
-(void) dealloc {
    [super dealloc];
    // Cause crash for some reason...
	//env->DeleteGlobalRef(object);
}

-(jint) getModifierKeys {
    jint result = 0;
    NSEventModifierFlags flags = NSEvent.modifierFlags;
    if(NSEventModifierFlagOption & flags)  result |= 1;
    if(NSEventModifierFlagControl & flags) result |= 2;
    if(NSEventModifierFlagShift & flags)   result |= 4;
    if(NSEventModifierFlagCommand & flags) result |= 8;
    return result;
}
-(NSUInteger) translateKeyToModifierFlag:(int)key {
    switch (key) {
        case kVK_Shift:
        case kVK_RightShift:
            return NSEventModifierFlagShift;
        case kVK_Control:
        case kVK_RightControl:
            return NSEventModifierFlagControl;
        case kVK_Option:
        case kVK_RightOption:
            return NSEventModifierFlagOption;
        case kVK_Command:
        case kVK_RightCommand:
            return NSEventModifierFlagCommand;
        case kVK_CapsLock:
            return NSEventModifierFlagCapsLock;
    }
    return 0;
}

-(jint) translateKey:(int)key {
    switch(key){
        case kVK_Space:             return 32; // SPACE
        case kVK_ANSI_Quote:        return 39; // APOSTROPHE
        case kVK_ANSI_Comma:        return 44; // COMMA
        case kVK_ANSI_Minus:        return 45; // MINUS
        case kVK_ANSI_Period:       return 46; // PERIOD
        case kVK_ANSI_Slash:        return 47; // SLASH
        case kVK_ANSI_0:            return 48; // 0
        case kVK_ANSI_1:            return 49; // 1
        case kVK_ANSI_2:            return 50; // 2
        case kVK_ANSI_3:            return 51; // 3
        case kVK_ANSI_4:            return 52; // 4
        case kVK_ANSI_5:            return 53; // 5
        case kVK_ANSI_6:            return 54; // 6
        case kVK_ANSI_7:            return 55; // 7
        case kVK_ANSI_8:            return 56; // 8
        case kVK_ANSI_9:            return 57; // 9
        case kVK_ANSI_Semicolon:    return 59; // ;
        case kVK_ANSI_Equal:        return 61; // =
        case kVK_ANSI_A:            return 65; // A
        case kVK_ANSI_B:            return 66; // B
        case kVK_ANSI_C:            return 67; // C
        case kVK_ANSI_D:            return 68; // D
        case kVK_ANSI_E:            return 69; // E
        case kVK_ANSI_F:            return 70; // F
        case kVK_ANSI_G:            return 71; // G
        case kVK_ANSI_H:            return 72; // H
        case kVK_ANSI_I:            return 73; // I
        case kVK_ANSI_J:            return 74; // J
        case kVK_ANSI_K:            return 75; // K
        case kVK_ANSI_L:            return 76; // L
        case kVK_ANSI_M:            return 77; // M
        case kVK_ANSI_N:            return 78; // N
        case kVK_ANSI_O:            return 79; // O
        case kVK_ANSI_P:            return 80; // P
        case kVK_ANSI_Q:            return 81; // Q
        case kVK_ANSI_R:            return 82; // R
        case kVK_ANSI_S:            return 83; // S
        case kVK_ANSI_T:            return 84; // T
        case kVK_ANSI_U:            return 85; // U
        case kVK_ANSI_V:            return 86; // V
        case kVK_ANSI_W:            return 87; // W
        case kVK_ANSI_X:            return 88; // X
        case kVK_ANSI_Y:            return 89; // Y
        case kVK_ANSI_Z:            return 90; // Z
        case kVK_ANSI_LeftBracket:  return 91; // [
        case kVK_ANSI_Backslash:    return 92; // \.
        case kVK_ANSI_RightBracket: return 93; // ]
        case kVK_ANSI_Grave:        return 96; // GRAVE_ACCENT
        case kVK_Escape:            return 256; // ESC
        case kVK_Return:            return 257; // ENTER
        case kVK_Tab:               return 258; // TAB
        case kVK_Delete:            return 259; // BACKSPACE
        case kVK_ForwardDelete:     return 261; // DELETE
        case kVK_RightArrow:        return 262; // RIGHT
        case kVK_LeftArrow:         return 263; // LEFT
        case kVK_DownArrow:         return 264; // DOWN
        case kVK_UpArrow:           return 265; // UP
        case kVK_PageUp:            return 266; // PAGE_UP
        case kVK_PageDown:          return 267; // PAGE_DOWN
        case kVK_Home:              return 268; // HOME
        case kVK_End:               return 269; // END
        case kVK_CapsLock:          return 280; // CAPS_LOCK
        case kVK_F1:                return 290; // F1
        case kVK_F2:                return 291; // F2
        case kVK_F3:                return 292; // F3
        case kVK_F4:                return 293; // F4
        case kVK_F5:                return 294; // F5
        case kVK_F6:                return 295; // F6
        case kVK_F7:                return 296; // F7
        case kVK_F8:                return 297; // F8
        case kVK_F9:                return 298; // F9
        case kVK_F10:               return 299; // F10
        case kVK_F11:               return 300; // F11
        case kVK_F12:               return 301; // F12
        case kVK_F13:               return 302; // F13
        case kVK_F14:               return 303; // F14
        case kVK_F15:               return 304; // F15
        case kVK_F16:               return 305; // F16
        case kVK_F17:               return 306; // F17
        case kVK_F18:               return 307; // F18
        case kVK_F19:               return 308; // F19
        case kVK_F20:               return 309; // F20
        case kVK_ANSI_Keypad0:      return 320; // KP_0
        case kVK_ANSI_Keypad1:      return 321; // KP_1
        case kVK_ANSI_Keypad2:      return 322; // KP_2
        case kVK_ANSI_Keypad3:      return 323; // KP_3
        case kVK_ANSI_Keypad4:      return 324; // KP_4
        case kVK_ANSI_Keypad5:      return 325; // KP_5
        case kVK_ANSI_Keypad6:      return 326; // KP_6
        case kVK_ANSI_Keypad7:      return 327; // KP_7
        case kVK_ANSI_Keypad8:      return 328; // KP_8
        case kVK_ANSI_Keypad9:      return 329; // KP_9
        case kVK_ANSI_KeypadDecimal:  return 330; // KP_DECIMAL
        case kVK_ANSI_KeypadDivide:   return 331; // KP_DIVIDE
        case kVK_ANSI_KeypadMultiply: return 332; // KP_MULTIPLY
        case kVK_ANSI_KeypadMinus:    return 333; // KP_SUBTRACT
        case kVK_ANSI_KeypadPlus:     return 334; // KP_ADD
        case kVK_ANSI_KeypadEnter:    return 335; // KP_ENTER
        case kVK_ANSI_KeypadEquals:   return 336; // KP_EQUAL
        case kVK_Shift:             return 340; // LEFT_SHIFT
        case kVK_Control:           return 341; // LEFT_CONTROL
        case kVK_Option:            return 342; // LEFT_ALT
        case kVK_RightShift:        return 344; // RIGHT_SHIFT
        case kVK_RightControl:      return 345; // RIGHT_CONTROL
        case kVK_RightOption:       return 346; // RIGHT_ALT

        case kVK_Command:           return 349; // LEFT_COMMAND
        case kVK_RightCommand:      return 350; // RIGHT_COMMAND
    }
    return key;
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

/* ====================
          Keyboard
   ==================== */

-(void) keyDown:(NSEvent*)event{
    env->CallVoidMethod(object, onKeyDownCallback, [self translateKey:event.keyCode], [self getModifierKeys]);
}

- (void)flagsChanged:(NSEvent*)event{
    const NSUInteger key = [self translateKeyToModifierFlag:event.keyCode];
    const unsigned int modifierFlags = [event modifierFlags] & NSEventModifierFlagDeviceIndependentFlagsMask;

    if(key & modifierFlags)
        env->CallVoidMethod(object, onKeyDownCallback, [self translateKey:event.keyCode], event.keyCode, [self getModifierKeys]);
    else
        env->CallVoidMethod(object, onKeyUpCallback, [self translateKey:event.keyCode], event.keyCode, [self getModifierKeys]);
}

-(void) keyUp:(NSEvent*)event{
    env->CallVoidMethod(object, onKeyUpCallback, [self translateKey:event.keyCode], [self getModifierKeys]);
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

        GraplAppDelegate* appDelegate = [[GraplAppDelegate alloc] init];
        if (![NSApp delegate])
            [(NSApplication *)NSApp setDelegate:appDelegate];
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
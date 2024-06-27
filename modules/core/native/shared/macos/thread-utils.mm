#import "thread-utils.h"

/*
    Based on JDK's AWT implementation
    https://github.com/openjdk/jdk/blob/master/src/java.desktop/macosx/native/libosxapp/ThreadUtilities.m
*/

@implementation ThreadUtilities
static NSArray<NSString*> *javaModes = [[NSArray alloc] initWithObjects:
        NSDefaultRunLoopMode, NSModalPanelRunLoopMode, NSEventTrackingRunLoopMode, @"grapl", nil];

+ (void)invokeBlock:(void (^)())block {
    block();
}

+ (void)invokeBlockCopy:(void (^)(void))blockCopy {
    blockCopy();
    Block_release(blockCopy);
}

+ (void)performOnMainThread:(BOOL)wait block:(void (^)())block {
    if (![NSThread isMainThread]){
        [self
            performSelectorOnMainThread:    wait == YES ? @selector(invokeBlock:) : @selector(invokeBlockCopy:)
            withObject:                     wait == YES ? block : Block_copy(block)
            waitUntilDone:                  wait
            modes:                          javaModes
        ];
    } else
        block();
}
@end




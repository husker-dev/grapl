#import <Cocoa/Cocoa.h>


@interface ThreadUtilities_Core : NSObject { }
+ (void)performOnMainThread:(BOOL)wait block:(void (^)())block;
@end

#define ON_MAIN_THREAD(...) \
[ThreadUtilities_Core performOnMainThread:YES block:^() { \
    @autoreleasepool { \
        __VA_ARGS__ \
    }; \
}];


#import <Cocoa/Cocoa.h>


@interface ThreadUtilities : NSObject { }
+ (void)performOnMainThread:(BOOL)wait block:(void (^)())block;
@end

#define ON_MAIN_THREAD(...) \
[ThreadUtilities performOnMainThread:YES block:^() { \
    @autoreleasepool { \
        __VA_ARGS__ \
    }; \
}];


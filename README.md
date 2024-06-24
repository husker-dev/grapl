# offscreen-jgl

<a href="LICENSE"><img src="https://img.shields.io/github/license/husker-dev/offscreen-jgl?style=flat-square"></a>
<a href="https://jitpack.io/#husker-dev/offscreen-jgl"><img src="https://img.shields.io/jitpack/v/github/husker-dev/offscreen-jgl?style=flat-square"></a>
<a href="https://github.com/husker-dev/offscreen-jgl/releases/latest"><img src="https://img.shields.io/github/v/release/husker-dev/offscreen-jgl?style=flat-square"></a>

Java tool for creating thread-independent offscreen OpenGL contexts

## Features

- Thread independent
- Doesn't create a window if possible

## Usage

```kotlin
val context = GLContext.create()
```

#### Create shared
```kotlin
val context = GLContext.create(anotherContext)
```

#### Create with Core profile
```kotlin
val context = GLContext.create(true)
```

#### Get current
```kotlin
val context = GLContext.current()
```

#### Make current
```kotlin
context.makeCurrent()
```

#### Clear current
```kotlin
GLContext.clear()
```

## Linux deps
Add i386
- sudo dpkg --add-architecture i386

Install g++ multilib
- g++-multilib

Install (+ with :i386)
- libx11-dev
- libwayland-dev
- libxrandr-dev
- libxcursor-dev
- libgl1-mesa-dev
#define UNICODE

#include "../shared.h"

#include <d3d9.h>
#include <jni.h>
#include <iostream>


d3d9fun(jlong, nCreateD3D9)(JNIEnv* env, jobject) {
	return (jlong)Direct3DCreate9(D3D_SDK_VERSION);
}

d3d9fun(jlong, nCreateDevice)(JNIEnv* env, jobject, jlong _d3d) {
	IDirect3D9* d3d = (IDirect3D9*)_d3d;

	D3DPRESENT_PARAMETERS pp = {};
	pp.Windowed = TRUE;
	pp.SwapEffect = D3DSWAPEFFECT_DISCARD;
	pp.hDeviceWindow = NULL;
	pp.PresentationInterval = D3DPRESENT_INTERVAL_IMMEDIATE;
	pp.BackBufferFormat = D3DFMT_A8R8G8B8;
	pp.BackBufferCount = 1;
	pp.BackBufferWidth = 10;
	pp.BackBufferHeight = 10;

	IDirect3DDevice9* device;
	d3d->CreateDevice(
		D3DADAPTER_DEFAULT, D3DDEVTYPE_HAL,
		NULL,
		D3DCREATE_HARDWARE_VERTEXPROCESSING | D3DCREATE_MULTITHREADED | D3DCREATE_PUREDEVICE,
		&pp, &device);

	return (jlong)device;
}

package com.huskerdev.grapl.d3d9

class D3D9Device(
    val handle: Long
) {

    companion object {
        @JvmStatic private external fun nCreateD3D9(): Long
        @JvmStatic private external fun nCreateDevice(d3d: Long): Long
    }
}
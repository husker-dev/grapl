module grapl.gl {

    requires transitive grapl;

    exports com.huskerdev.grapl.gl;
    exports com.huskerdev.grapl.gl.platforms.linux.egl;
    exports com.huskerdev.grapl.gl.platforms.linux.glx;
    exports com.huskerdev.grapl.gl.platforms.win;
    exports com.huskerdev.grapl.gl.platforms.macos;
}
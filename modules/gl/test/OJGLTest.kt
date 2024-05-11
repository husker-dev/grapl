import com.huskerdev.grapl.core.input.Cursor
import com.huskerdev.grapl.core.x
import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLWindow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*

fun main(){
    //Window.useBackgroundMessageHandler = false
    val window = GLWindow()

    //window.maximizable = false
    //window.minimizable = false
    window.size = 100 x 100
    //window.maxSize = 200 x 200
    window.alignToCenter()

    window.pointerScrollListeners += {
        println("scroll: at ${it.pointer.x}x${it.pointer.y} with deltaX: ${it.deltaX}, deltaY: ${it.deltaY}")
    }

    window.pointerZoomBeginListeners += {
        println("begin zoom: at ${it.pointer.x}x${it.pointer.y} with ${it.zoom}, delta: ${it.deltaZoom}")
    }
    window.pointerZoomListeners += {
        println("zoom: at ${it.pointer.x}x${it.pointer.y} with ${it.zoom}, delta: ${it.deltaZoom}")
    }
    window.pointerZoomEndListeners += {
        println("end zoom: at ${it.pointer.x}x${it.pointer.y} with ${it.zoom}, delta: ${it.deltaZoom}")
    }

    window.pointerRotationBeginListeners += {
        println("begin rotation: at ${it.pointer.x}x${it.pointer.y} with ${it.angle}, delta: ${it.deltaAngle}")
    }
    window.pointerRotationListeners += {
        println("rotation: at ${it.pointer.x}x${it.pointer.y} with ${it.angle}, delta: ${it.deltaAngle}")
    }
    window.pointerRotationEndListeners += {
        println("end rotation: at ${it.pointer.x}x${it.pointer.y} with ${it.angle}, delta: ${it.deltaAngle}")
    }

    window.title = "UTF? Да"
    window.cursor = Cursor.HAND
    window.visible = true

    window.updateListener += {
        window.context.makeCurrent()
        GL.createCapabilities()
        glClearColor(1f, 0f, 0f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        window.swapBuffers()
    }
}

class OJGLTest {

    @Test
    fun createWindow() {

    }

    @Test
    fun createAndDeleteInstance() {
        val ctx = GLContext.create()
        ctx.delete()
    }

    @Test
    fun makeCurrent() {
        val ctx = GLContext.create()
        assertEquals(ctx.makeCurrent(), true)
        ctx.delete()
    }

    @Test
    fun createShared() {
        val first = GLContext.create()
        val second = GLContext.create(first)
        assertEquals(second.makeCurrent(), true)
    }

    @Test
    fun fromCurrent() {
        GLContext.create().makeCurrent()
        val second = GLContext.current()
        assertEquals(second.makeCurrent(), true)
    }

    @Test
    fun clearContext() {
        GLContext.create().makeCurrent()
        assertEquals(GLContext.clear(), true)
    }

    @Test
    fun deleteContext() {
        GLContext.create().makeCurrent()
        GLContext.current().delete()
    }

}
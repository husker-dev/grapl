import com.huskerdev.grapl.core.window.x
import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLWindow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*

class OJGLTest {

    @Test
    fun createWindow() {
        val window = GLWindow.create()
        window.size = 800 x 600
        window.title = "UTF? Да"
        window.visible = true

        window.context.makeCurrent()
        GL.createCapabilities()
        glClearColor(1f, 0f, 0f, 1f)

        window.runEventLoop {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
            window.swapBuffers()
        }
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
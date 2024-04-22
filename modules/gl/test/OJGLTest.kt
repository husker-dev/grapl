import com.huskerdev.grapl.core.Cursor
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.x
import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLWindow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*


class OJGLTest {

    @Test
    fun createWindow() {
        Display.list.forEach { display ->
            println("""
                handle: ${display.peer.handle}
                name: ${display.name} (${display.systemName})
                frequency: ${display.frequency}
                x: ${display.x} (${display.absoluteX})
                y: ${display.y} (${display.absoluteY})
                width: ${display.width} (${display.absoluteWidth})
                height: ${display.height} (${display.absoluteHeight})
                dpi: ${display.dpi}
                width (mm): ${display.physicalWidth}
                height (mm): ${display.physicalHeight}
            """.trimIndent())
        }

        val window = GLWindow.create()
        window.size = 800 x 600
        window.minSize = 300 x 300
        window.alignToCenter()
        window.title = "UTF? Да"
        window.cursor = Cursor.TEXT
        window.visible = true

        println(window.display)

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
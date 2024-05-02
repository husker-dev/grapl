import com.huskerdev.grapl.core.Cursor
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLWindow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*


class OJGLTest {

    @Test
    fun createWindow() {
        /*
        Display.list.forEach { display ->
            println("""
                ===========
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
            println("""
                --- Current ----
                    width: ${display.mode.size.width}
                    height: ${display.mode.size.height}
                    frequency: ${display.mode.frequency}
            """.trimIndent())
        }
        */
        Display.primary.modes.forEach {
            println("""
                ------------
                size: ${it.size.width} x ${it.size.height}
                frequency: ${it.frequency}
                bits: ${it.bits}
            """.trimIndent())
        }
        Display.primary.mode.apply {
            println("""
                ------Current------
                size: ${this.size.width} x ${this.size.height}
                frequency: ${this.frequency}
                bits: ${this.bits}
            """.trimIndent())
        }


        val window = GLWindow.create()
        //window.displayState = WindowDisplayState.ScaledFullscreen(DisplayMode(Display.primary, 800 x 600, 32, 60))
        //window.size = 800 x 600
        window.pointerEnterListeners += {
            println("enter: at ${it.pointer.x}x${it.pointer.y}")
        }
        window.pointerLeaveListeners += {
            println("leave: at ${it.pointer.x}x${it.pointer.y}")
        }
        window.pointerMoveListeners += {
            println("moved: from ${it.oldX}x${it.oldY} to ${it.pointer.x}x${it.pointer.y} with delta ${it.deltaX}x${it.deltaY}")
        }
        window.pointerDragListeners += {
            println("drag: from ${it.oldX}x${it.oldY} to ${it.pointer.x}x${it.pointer.y} with delta ${it.deltaX}x${it.deltaY} by ${it.pointer.buttons}")
        }
        window.pointerClickListeners += {
            println("clicked: at ${it.pointer.x}x${it.pointer.y} by ${it.pointer.buttons} ${it.clicks} times")
        }
        window.pointerDownListeners += {
            println("down: at ${it.pointer.x}x${it.pointer.y} by ${it.pointer.buttons}")
        }
        window.pointerUpListeners += {
            println("up: at ${it.pointer.x}x${it.pointer.y}")
        }
        //window.minSize = 300 x 300
        //window.alignToCenter()
        window.title = "UTF? Да"
        window.cursor = Cursor.HAND
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
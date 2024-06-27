
import com.huskerdev.grapl.core.input.Cursor
import com.huskerdev.grapl.core.x
import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLWindow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*

fun main(){
    GLWindow().apply {
        title = "UTF? Да"
        cursor = Cursor.HAND
        size = 100 x 100
        alignToCenter()

        onInit = {
            context.makeCurrent()
            swapInterval = 1
            GL.createCapabilities()
            glClearColor(1f, 0f, 0f, 1f)
        }
        var lastTime = System.currentTimeMillis()
        var counter = 0

        onUpdate = {
            val current = System.currentTimeMillis()
            if(current - lastTime >= 1000){
                println("FPS: $counter")
                counter = 0
                lastTime = current
            }
            counter++

            glClear(GL_COLOR_BUFFER_BIT)
            swapBuffers()
        }

        visible = true
    }
}

class OJGLTest {

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
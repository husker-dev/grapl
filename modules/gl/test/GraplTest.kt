
import com.huskerdev.grapl.core.input.Cursor
import com.huskerdev.grapl.core.window.windowEventConsumer
import com.huskerdev.grapl.core.x
import com.huskerdev.grapl.gl.GLContext
import com.huskerdev.grapl.gl.GLWindow
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*

fun main(){

    GLWindow(debug = true).apply {
        title = "UTF? Да"
        cursor = Cursor.HAND
        size = 100 x 100
        alignToCenter()

        eventConsumer = windowEventConsumer {
            onInit {
                swapInterval = 1
                GL.createCapabilities()
                glClearColor(1f, 0f, 0f, 1f)

                GLContext.bindDebugCallback(::println)
                glBindTexture(GL_2D, 100)
            }
            onUpdate {
                glClear(GL_COLOR_BUFFER_BIT)
                swapBuffers()
            }
        }


        visible = true
    }
}
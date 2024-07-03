
import com.huskerdev.grapl.core.display.manufacturerID
import com.huskerdev.grapl.core.display.manufacturerName
import com.huskerdev.grapl.core.input.Cursor
import com.huskerdev.grapl.core.window.windowEventConsumer
import com.huskerdev.grapl.core.x
import com.huskerdev.grapl.gl.GLWindow
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*

fun main(){
    GLWindow().apply {
        title = "UTF? Да"
        cursor = Cursor.HAND
        size = 100 x 100
        alignToCenter()

        eventConsumer = windowEventConsumer {
            onInit {
                context.makeCurrent()
                swapInterval = 1
                GL.createCapabilities()
                glClearColor(1f, 0f, 0f, 1f)
            }
            onUpdate {
                glClear(GL_COLOR_BUFFER_BIT)
                swapBuffers()
            }
        }

        println("Display manufacturer: ${display.manufacturerName} (${display.manufacturerID})")

        visible = true
    }
}
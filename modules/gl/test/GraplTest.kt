
import com.huskerdev.grapl.core.display.Display
import com.huskerdev.grapl.core.input.Cursor
import com.huskerdev.grapl.core.window.WindowStyle
import com.huskerdev.grapl.core.window.windowEventConsumer
import com.huskerdev.grapl.core.x
import com.huskerdev.grapl.ext.display.*
import com.huskerdev.grapl.gl.GLWindow
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*

fun main(){

    fun printProperty(property: String, value: Any) =
        println("${property.padEnd(25)}: $value")


    Display.list.forEach { display ->
        //Files.write(File("./edid_${display.manufacturerId}.bin").toPath(), display.edid.toByteArray())
        println("=====================================================")
        printProperty("manufacturer", "${display.manufacturerName} (${display.manufacturerId})")
        //printProperty("name", display.name)
        printProperty("manufacture year", display.manufactureYear)
        printProperty("manufacture week", display.manufactureWeek)
        printProperty("manufacture date", display.manufactureDate)
        printProperty("product ID", display.productId)
        printProperty("serial number", display.serialNumber)
        printProperty("digital", display.isDigital)
        printProperty("video interface", display.videoInterface)
        printProperty("edid version", display.edidVersion)
        printProperty("width mm", display.mmSize)
    }

    GLWindow().apply {
        title = "UTF? Да"
        cursor = Cursor.HAND
        size = 100 x 100
        style = WindowStyle.NO_TITLEBAR
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


        visible = true
    }
}
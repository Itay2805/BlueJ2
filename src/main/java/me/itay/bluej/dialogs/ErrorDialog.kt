package me.itay.bluej.dialogs

import com.mrcrayfish.device.api.app.Layout
import com.mrcrayfish.device.core.Wrappable
import me.itay.bluej.BlueJMod
import me.itay.bluej.api.components.BlueJMessageDialog
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import java.awt.Color
import java.io.OutputStream
import java.io.PrintStream

enum class ErrorCode(val errorname: String, val code: Int){
    INFO("info", 0),
    WARN("warning", 1),
    ERROR("error", 2),
    FATAL("fatal", 3)
}

class ErrorDialog(
        errorcode: ErrorCode,
        override val message: String
) : BlueJMessageDialog(
        message,
        forground = when(errorcode.code){
            0 -> Color.DARK_GRAY
            1 -> Color.YELLOW
            2 -> Color.ORANGE
            3 -> Color.RED
            else -> Color.DARK_GRAY
        }
){
    private var h = 40
    private val layout = Layout(150, h)
    init{
        layout.width = 150
        this.dtitle = errorcode.errorname
    }

    override fun init() {
        super.init()
        val lines = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(message, this.width)
        h += (lines.size - 1) * 9

        this.defaultLayout.setBackground{ _, _, x, y, w, h, _, _, _ ->
            Gui.drawRect(x, y, x + width, y + height, Color.GRAY.rgb)
        }
    }
}
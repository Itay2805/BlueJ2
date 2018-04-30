package me.itay.bluej.api.components

import com.mrcrayfish.device.api.app.Component
import com.mrcrayfish.device.api.app.Dialog
import com.mrcrayfish.device.api.app.Layout
import com.mrcrayfish.device.api.app.component.Button
import com.mrcrayfish.device.api.app.component.Text
import com.mrcrayfish.device.api.app.listener.ClickListener
import com.mrcrayfish.device.core.Wrappable
import me.itay.bluej.api.DialogAttributeError
import me.itay.bluej.api.DialogAttributeValueOutOfBoundsError
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import java.awt.Color

open class BlueJDialog : Dialog(){
    var dwidth: Int = 0
        get() = widthattr.attrVal
    private val widthattr = UnsignedIntegerAttr("width", this.dwidth)
        get(){
            if(field.bounds){
                return field
            }
            throw DialogAttributeValueOutOfBoundsError(field)
        }

    var dheight: Int = 0
        get() = heightattr.attrVal
    val heightattr = UnsignedIntegerAttr("height", this.dheight)
        get(){
            if(field.bounds){
                return field
            }
            throw DialogAttributeValueOutOfBoundsError(field)
        }

    var dtitle: String = ""
        get() = titleattr.attrVal
    val titleattr = NonBlankStringAttr("title", dtitle)
        get(){
            if(field.bounds){
                return field
            }
            throw DialogAttributeValueOutOfBoundsError(field)
        }

    init{
        this.setTitle(this.dtitle)
        this.setLayout(Layout(this.dwidth, this.dheight))
    }

    operator fun plusAssign(c: Component){
        this.addComponent(c)
    }
}

open class BlueJMessageDialog(
        open val message: String,
        val background: Color = Color.LIGHT_GRAY,
        val forground: Color = Color.DARK_GRAY
) : BlueJDialog(){
    var positiveText = "Close"
    var positiveListener: ClickListener? = null

    override fun init() {
        super.init()

        val lines = Minecraft.
                getMinecraft().
                fontRenderer.
                listFormattedStringToWidth(
                        this.message,
                        this.dwidth - 10
                ).size
        this.defaultLayout.height += (lines - 1) * 9

        this.defaultLayout.setBackground { _, _, x, y, width, height, _, _, _ ->
            Gui.drawRect(x, y, x + width, y + height, this.background.rgb)
        }

        val text = Text(this.message, 5, 5, this.dwidth - 10)
        text.setTextColor(this.forground)
        this += text

        val positiveButton = Button(
                this.dwidth - 41,
                this.dheight - 20,
                this.positiveText
        )
        positiveButton.setClickListener { mouseX, mouseY, mouseButton ->
            this.positiveListener!!.onClick(mouseX, mouseY, mouseButton)
            close()
        }
        this += positiveButton
    }
}
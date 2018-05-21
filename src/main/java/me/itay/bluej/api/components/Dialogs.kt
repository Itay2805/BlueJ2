package me.itay.bluej.api.components

import com.mrcrayfish.device.api.app.Component
import com.mrcrayfish.device.api.app.Dialog
import com.mrcrayfish.device.api.app.Layout
import com.mrcrayfish.device.api.app.component.*
import com.mrcrayfish.device.api.app.listener.ClickListener
import com.mrcrayfish.device.api.io.Folder
import com.mrcrayfish.device.core.Wrappable
import com.mrcrayfish.device.core.io.FileSystem
import com.mrcrayfish.device.programs.system.component.FileBrowser
import me.itay.bluej.api.error.DialogAttributeValueOutOfBoundsError
import me.itay.bluej.languages.BlueJLanguage
import me.itay.bluej.languages.BlueJRuntimeManager
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.nbt.NBTTagCompound
import java.awt.Color
import javax.annotation.Nullable

open class BlueJDialog : Dialog() {
    val components = ArrayList<Component>()

    val widthattr = UnsignedIntegerAttr("width", this.width)
        get() {
            if (field.bounds) {
                return field
            }
            throw DialogAttributeValueOutOfBoundsError(field)
        }

    val heightattr = UnsignedIntegerAttr("height", this.height)
        get() {
            if (field.bounds) {
                return field
            }
            throw DialogAttributeValueOutOfBoundsError(field)
        }

    var dtitle = ""
    val titleattr = NonBlankStringAttr("title", dtitle)
        get() {
            if (field.bounds) {
                return field
            }
            throw DialogAttributeValueOutOfBoundsError(field)
        }

    operator fun plusAssign(c: Component){
        this.components += c
    }
}

open class BlueJMessageDialog(
        open val message: String,
        val background: Color = Color.LIGHT_GRAY,
        val forground: Color = Color.DARK_GRAY
) : BlueJDialog(){
    var positiveText = "Close"
    var positiveListener: ClickListener? = null

    val positiveButton = Button(
            this.width - 41,
            this.height - 20,
            this.positiveText
    )
    val text = Text(this.message, 5, 5, this.width - 10)

    override fun init(intent: NBTTagCompound?) {
        super.init(intent)

        val lines = Minecraft.
                getMinecraft().
                fontRenderer.
                listFormattedStringToWidth(
                        this.message,
                        this.width - 10
                ).size
        this.defaultLayout.height += (lines - 1) * 9

        this.defaultLayout.setBackground { _, _, x, y, width, height, _, _, _ ->
            Gui.drawRect(x, y, x + width, y + height, this.background.rgb)
        }

        text.setTextColor(this.forground)
        this += text


        positiveButton.setClickListener { mouseX, mouseY, mouseButton ->
            this.positiveListener?.onClick(mouseX, mouseY, mouseButton)
            close()
        }
        this += positiveButton
    }
}

abstract class BasicDialog<T> : BlueJDialog(){
    private val positiveText = "Select"
    private val negativeText = "Cancel"

    var main = Layout(211, 145)
    private var buttonPositive = Button(169, 125, positiveText)
    private var buttonNegative = Button(123, 125, negativeText)
    val clickListeners = ArrayList<ClickListener>()
    var responseHandler: ResponseHandler<T>? = null

    abstract fun render()

    override fun init(intent: NBTTagCompound?) {
        super.init(intent)
        this.render()
        main.addComponent(buttonPositive)
        this.buttonPositive.setClickListener { x, y, b->
            this.clickListeners.forEach {
                it.onClick(x, y, b)
            }
        }
        buttonNegative.setClickListener { _, _, _ -> close() }
        main.addComponent(buttonNegative)
        this.components.forEach(main::addComponent)

        this.setLayout(main)
    }
}

class BlueJSelectFromListDialog : BasicDialog<String>(){
    var list = ComboBox.List<String>(5, 5, arrayOf<String>())

    override fun render() {
        list.setItems(BlueJRuntimeManager.getRuntimes().keys.toTypedArray())
        this += list
        this.clickListeners += ClickListener { _, _, _ ->
            this.responseHandler?.onResponse(true, list.selectedItem)
            close()
        }
    }

}

@Suppress("UNCHECKED_CAST")
open class BrowserDialog<T : Resource> : BasicDialog<T>(){
    private var browser: FileBrowser? = null

    override fun render() {
        browser = FileBrowser(0, 0, this, FileBrowser.Mode.BASIC)
        val b = browser!!
        b.openFolder(FileSystem.DIR_HOME)
        main.addComponent(b)
        this.clickListeners.add(ClickListener { _, _, _ ->
            if(responseHandler != null) {
                if (b.selectedFile != null) {
                    val resource =
                            if (b.selectedFile is Folder)
                                ResourceFolder(b.selectedFile as Folder)
                            else
                                ResourceFile(b.selectedFile)
                    responseHandler?.onResponse(true, resource as T)
                }
            }
            close()
        })

    }
}

open class BlueJTextInputDialog : BasicDialog<String>(){
    private val textfield = TextField(50, 50, 100)
    val click = ClickListener { _, _, _ ->
        this.responseHandler?.onResponse(true, this.textfield.text)
        close()
    }

    override fun render() {
        this.clickListeners += click
        this += textfield
    }

}

class BlueJCreateFileDialog : BlueJTextInputDialog(){
    private val isStartupSelect = CheckBox("Startup", 2, 10)
    var isStartup = false

    override fun render() {
        super.render()
        this.dtitle = "Create File"
        this.isStartupSelect.setClickListener { _, _, _ ->
            this.isStartup = !isStartup
        }
        this += isStartupSelect
    }
}

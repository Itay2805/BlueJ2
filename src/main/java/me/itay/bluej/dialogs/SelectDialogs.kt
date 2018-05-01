package me.itay.bluej.dialogs

import com.mrcrayfish.device.api.app.Layout
import com.mrcrayfish.device.api.app.component.Button
import com.mrcrayfish.device.api.app.component.CheckBox
import com.mrcrayfish.device.api.app.component.TextField
import com.mrcrayfish.device.api.app.listener.ClickListener
import com.mrcrayfish.device.api.io.Folder
import com.mrcrayfish.device.core.io.FileSystem
import com.mrcrayfish.device.programs.system.component.FileBrowser
import me.itay.bluej.api.components.*

abstract class BasicDialog<T> : BlueJDialog(){
    private val positiveText = "Select"
    private val negativeText = "Cancel"

    var main = Layout(211, 145)
    private var buttonPositive = Button(169, 125, positiveText)
    private var buttonNegative = Button(123, 125, negativeText)
    var clickListeners = ArrayList<ClickListener>()
    var responseHandler: ResponseHandler<T>? = null

    abstract fun render()

    override fun init() {
        super.init()
        this.render()
        main.addComponent(buttonPositive)
        this.buttonPositive.setClickListener { x, y, b->
            this.clickListeners.forEach {
                it.onClick(x, y, b)
            }
        }
        buttonNegative.setClickListener { _, _, _ -> close() }
        main.addComponent(buttonNegative)

        this.setLayout(main)
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
        this.clickListeners + ClickListener { _, _, _ ->
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
        }

    }
}

open class BlueJTextInputDialog : BasicDialog<String>(){
    private val textfield = TextField(10, 15, 30)
    val click = ClickListener { _, _, _ ->
        this.responseHandler?.onResponse(true, this.textfield.text)
    }

    override fun render() {
        this.clickListeners + click
        this += textfield
    }

}

class BlueJCreateFileDialog : BlueJTextInputDialog(){
    private val isStartupSelect = CheckBox("Startup", 10, 10)
    var isStartup = false

    override fun render() {
        this.dtitle = "Create File"
        this.isStartupSelect.setClickListener { _, _, _ ->
            this.isStartup = !isStartup
        }
        this += isStartupSelect
    }
}

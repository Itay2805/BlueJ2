package me.itay.bluej.dialogs

import com.mrcrayfish.device.api.ApplicationManager
import com.mrcrayfish.device.api.app.Dialog
import com.mrcrayfish.device.api.app.Layout
import com.mrcrayfish.device.api.app.component.Button
import com.mrcrayfish.device.api.app.component.TextField
import com.mrcrayfish.device.api.io.File
import com.mrcrayfish.device.api.io.Folder
import com.mrcrayfish.device.core.io.FileSystem
import com.mrcrayfish.device.programs.system.component.FileBrowser
import me.itay.bluej.BlueJApp
import me.itay.bluej.api.components.Resource
import me.itay.bluej.api.components.ResourceFile
import me.itay.bluej.api.components.ResourceFolder

abstract class SelectDialog : Dialog(){
    private val positiveText = "Select"
    private val negativeText = "Cancel"

    var main = Layout(211, 145)
    var buttonPositive = Button(169, 125, positiveText)
    var buttonNegative = Button(123, 125, negativeText)

    val path = FileSystem.DIR_HOME

    abstract fun render()

    override fun init() {
        super.init()
        this.render()
        main.addComponent(buttonPositive)
        buttonNegative.setClickListener { _, _, _ -> close() }
        main.addComponent(buttonNegative)

        this.setLayout(main)
    }
}

@Suppress("UNCHECKED_CAST")
open class BrowserDialog<T : Resource> : SelectDialog(){
    var tResponseHandler: ResponseHandler<T>? = null
    private var browser: FileBrowser? = null

    override fun render() {
        browser = FileBrowser(0, 0, this, FileBrowser.Mode.BASIC)
        val b = browser!!
        b.openFolder(path)
        main.addComponent(browser)
        buttonPositive.setClickListener { _, _, _ ->
            if(tResponseHandler != null) {
                if (b.selectedFile != null) {
                    val resource =
                            if (b.selectedFile is Folder)
                                ResourceFolder(b.selectedFile as Folder)
                            else
                                ResourceFile(b.selectedFile)
                    tResponseHandler?.onResponse(true, resource as T)
                }
            }
            close()
        }

    }
}

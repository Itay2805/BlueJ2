package me.itay.bluej.utils

import com.mrcrayfish.device.api.app.Dialog
import com.mrcrayfish.device.api.app.component.ComboBox
import com.mrcrayfish.device.api.io.Folder
import me.itay.bluej.BlueJApp
import me.itay.bluej.BlueJConsoleDialog
import me.itay.bluej.api.components.BlueJCreateFileDialog
import me.itay.bluej.api.components.BlueJSelectFromListDialog
import me.itay.bluej.api.components.BrowserDialog
import me.itay.bluej.api.components.ResourceFolder
import me.itay.bluej.api.error.BlueJComponentToggleException
import me.itay.bluej.api.error.ErrorCode
import me.itay.bluej.api.error.displayError
import me.itay.bluej.dialogs.*
import me.itay.bluej.languages.BlueJRuntimeManager
import me.itay.bluej.project.*

fun BlueJApp.createProjectHandler(mx: Int, my: Int, mb: Int){
    val folderselect = BrowserDialog<ResourceFolder>()
    folderselect.responseHandler = Dialog.ResponseHandler { s, e ->
        if (s) {
            val input = Dialog.Input("Project Name")
            input.setResponseHandler { s1, e1 ->
                if (s1) {
                    val langselect = BlueJSelectFromListDialog()
                    langselect.list = ComboBox.List(5, 5, this.listFiles.items.toTypedArray())
                    langselect.responseHandler = Dialog.ResponseHandler { s2, e2 ->
                        if (s2) {
                            this.currentProject = createProject(e.folder, e1, BlueJRuntimeManager.getLanguage(e2))!!
                            this.refreshFilesList()
                        }
                        true
                    }
                    this.openDialog(langselect)
                }
                true
            }
            this.openDialog(input)
        }
        true
    }
    this.openDialog(folderselect)
}

fun BlueJApp.openProjectHandler(mx: Int, my: Int, mb: Int){
    val folderselect = BrowserDialog<ResourceFolder>()
    folderselect.responseHandler = Dialog.ResponseHandler { success, e ->
        if (success) {
            val projectfile = e.folder.getFileSync(FILE_BLUEJ_PROJECT)
            if(projectfile != null){
                this.currentProject = loadFromProjectFile(projectfile!!)
                refreshFilesList()
                return@ResponseHandler true
            }
            displayError("No project exists in this folder!", ErrorCode.ERROR)
        }
        false
    }
    openDialog(folderselect)
}

fun BlueJApp.createSourceFileHandler(mx: Int, my: Int, mb: Int){
    if(this.projectToggled) {
        val createSourceFileDialog = BlueJCreateFileDialog()
        createSourceFileDialog.responseHandler = Dialog.ResponseHandler { success, e ->
            val sourcefile = this.currentProject create e
            this.currentProject?.startupFile = if(createSourceFileDialog.isStartup)
                                                    sourcefile
                                                else
                                                    null
            this.refreshFilesList()
            success
        }
        openDialog(createSourceFileDialog)
        return
    }
    throw BlueJComponentToggleException("project")
}

infix fun Project?.create(name: String): SourceFile? = this?.createSourceFile(name, null)

fun BlueJApp.deleteSourceFileHandler(mx: Int, my: Int, mb: Int){
    if(this.editorToggled) {
        val item = this.listFiles.selectedItem!!
        this.currentProject -= item
        this.listFiles - item
        this.refreshFilesList()
        return
    }
    throw BlueJComponentToggleException("editor")
}

fun BlueJApp.saveProjectHandler(mx: Int, my: Int, mb: Int){
    if(editorToggled){
        this.currentProject?.save()
        return
    }
    throw BlueJComponentToggleException("editor")
}

fun BlueJApp.runProjectHandler(mx: Int, my: Int, mb: Int){
    if(this.runtimeToggled) {
        val response = this.currentProject?.projectLanguage?.run(this.currentProject)!!
        val console = BlueJConsoleDialog()
        console.setOutput(response.output)
        return
    }
    throw BlueJComponentToggleException("runtime")
}


fun BlueJApp.fileSelectedHandler(item: String, index: Int, mb: Int){
    if(this.projectToggled) {
        this.currentSourceFile = this.currentProject?.getSourceFile(item)
        return
    }
    throw BlueJComponentToggleException("editor")
}

fun BlueJApp.refreshFilesList(){
    this.listFiles.items.clear()
    this.currentProject?.src?.forEach {
        this.listFiles.addItem(it.name)
    }
}
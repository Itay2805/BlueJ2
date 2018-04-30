package me.itay.bluej.utils

import com.mrcrayfish.device.api.app.Dialog
import com.mrcrayfish.device.api.io.Folder
import me.itay.bluej.BlueJApp
import me.itay.bluej.BlueJConsoleDialog
import me.itay.bluej.api.BlueJComponentToggleException
import me.itay.bluej.api.components.ResourceFolder
import me.itay.bluej.dialogs.*
import me.itay.bluej.languages.BlueJRuntimeManager
import me.itay.bluej.project.Project
import me.itay.bluej.project.createProject

fun BlueJApp.createProjectHandler(mx: Int, my: Int, mb: Int){
    if(this.projectToggled) {
        val folderselect = BrowserDialog<ResourceFolder>()
        folderselect.tResponseHandler = Dialog.ResponseHandler { s, e ->
            if (s) {
                val input = Dialog.Input()
                input.setResponseHandler { s1, e1 ->
                    if (s1) {
                        val langselect = SelectLanguageDialog()
                        langselect.setResponseHandler { s2, e2 ->
                            if (s2) {
                                createProject(e.folder, e1, BlueJRuntimeManager.getLanguage(e2))!!.load()
                            }
                            true
                        }
                    }
                    true
                }
            }
            true
        }
        this.openDialog(folderselect)
        return
    }
    throw BlueJComponentToggleException("project")
}

fun BlueJApp.openProjectHandler(mx: Int, my: Int, mb: Int){
    if(this.projectToggled) {
        val folderselect = BrowserDialog<ResourceFolder>()
        folderselect.tResponseHandler = Dialog.ResponseHandler { success, e ->
            if (success) {
                val folder = e.folder
                val projectfile = folder.getFile(Project.FILE_BLUEJ_PROJECT)!!
                val nbt = projectfile.data!!
                if (nbt.hasKey(Project.FIELD_NAME) && nbt.hasKey(Project.FIELD_ROOT) && nbt.hasKey(Project.FIELD_LANG)) {
                    val name = nbt.getString(Project.FIELD_NAME)
                    val root = nbt.getString(Project.FIELD_ROOT)
                    val lang = nbt.getString(Project.FIELD_LANG)
                    this.currentProject = Project(Folder(root), name, BlueJRuntimeManager.getLanguage(lang))
                }
            }
            success
        }
        openDialog(folderselect)
    }
}

fun BlueJApp.createSourceFileHandler(mx: Int, my: Int, mb: Int){
    if(this.projectToggled) {
        val inputselect = Dialog.Input()
        inputselect.setResponseHandler { success, e ->
            if (success) {
                if (this.currentProject != null) {
                    this.currentProject?.createSourceFile(e, null)
                } else {
                    displayError("No project currently open! This should not happen! Please report this to the author!", ErrorCode.FATAL)
                }
            }
            success
        }
        openDialog(inputselect)
        return
    }
    throw BlueJComponentToggleException("project")
}

fun BlueJApp.deleteSourceFileHandler(mx: Int, my: Int, mb: Int){
    if(this.editorToggled) {
        val item = this.listFiles.selectedItem!!
        this.currentProject?.deleteSourceFile(item, null)
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
    if(this.editorToggled) {
        this.currentSourceFile = this.currentProject?.getSourceFile(item)
        return
    }
    throw BlueJComponentToggleException("editor")
}

fun Project.load(){
    this.load(null)
}


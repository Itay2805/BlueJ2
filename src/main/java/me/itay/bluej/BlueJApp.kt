package me.itay.bluej

import com.mrcrayfish.device.api.ApplicationManager
import com.mrcrayfish.device.api.app.Application
import com.mrcrayfish.device.api.app.Icons
import com.mrcrayfish.device.api.app.component.Button
import com.mrcrayfish.device.api.app.component.ItemList
import com.mrcrayfish.device.api.app.component.TextArea
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer
import com.mrcrayfish.device.api.io.Folder
import com.mrcrayfish.device.api.utils.RenderUtil
import me.itay.bluej.api.error.NoProjectLoadedException
import me.itay.bluej.api.error.NoSourceFileSelectedException
import me.itay.bluej.project.FILE_BLUEJ_PROJECT
import me.itay.bluej.project.Project
import me.itay.bluej.project.SourceFile
import me.itay.bluej.project.loadFromProjectFile
import me.itay.bluej.utils.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.nbt.NBTTagCompound
import java.awt.Color

class BlueJApp : Application(){
    companion object {
        var id: String? = null
    }

    private var WIDTH = 362
    private var HEIGHT = 164

    val btnNewProject = Button(getNextBtnPos(), 1, Icons.NEW_FOLDER)
    val btnOpenProject = Button(getNextBtnPos(), 1, Icons.LOAD)
    val btnExportProject = Button(getNextBtnPos(), 1, Icons.EXPORT)
    val btnNewFile = Button(getNextBtnPos(), 1, Icons.NEW_FILE)
    val btnDeleteFile = Button(getNextBtnPos(), 1, Icons.TRASH)
    val btnSaveProject = Button(getNextBtnPos(), 1, Icons.SAVE)
    val btnCopyAll = Button(getNextBtnPos(), 1, Icons.COPY)
    val btnPaste = Button(getNextBtnPos(), 1, Icons.CLIPBOARD)
    val btnRun = Button(getNextBtnPos(), 1, Icons.PLAY)
    val btnStop = Button(getNextBtnPos(), 1, Icons.STOP)
    val btnSettings = Button(getNextBtnPos(), 1, Icons.WRENCH)

    var editorToggled = false
    set(t){
        this.btnCopyAll.setEnabled(t)
        this.btnPaste.setEnabled(t)
        this.txtCodeEditor.setEnabled(t)
        this.btnDeleteFile.setEnabled(t)
        field = t
    }
    var projectToggled = false
    set(t){
        this.btnNewFile.setEnabled(t)
        this.btnSaveProject.setEnabled(t)
        this.btnExportProject.setEnabled(t)
        field = t
    }
    var runtimeToggled = false
    set(t){
        this.btnRun.setEnabled(t)
        this.btnStop.setEnabled(t)
        field = t
    }

    private var leftPanelWidth = 80
    private var middlePanelWidth = 280
    val listFiles = ItemList<String>(1, 18, leftPanelWidth, (HEIGHT - 18) / 15 + 1)
    val txtCodeEditor = TextArea(1 + leftPanelWidth, 18, middlePanelWidth, HEIGHT - 23)

    // TODO add terminal

    private var x: Int = 0

    private fun resetLayout() {
        x = 1
        leftPanelWidth = 80
        middlePanelWidth = 280
    }

    private fun getNextBtnPos(): Int {
        val curr = x
        x += 16
        return curr
    }

    private fun addSeperator() {
        x += 2
    }

    var currentProject: Project? = null
        get(){
            if(field != null){
                return field!!
            }
            throw NoProjectLoadedException()
        }
        set(f){
            this.projectToggled = true
            field = f
        }
    var currentSourceFile: SourceFile? = null
        get(){
            if(field != null){
                return field!!
            }
            throw NoSourceFileSelectedException()
        }
        set(f){
            field = f
            this.editorToggled = true
        }

    override fun init(intent: NBTTagCompound?) {
        setDefaultWidth(WIDTH)
        setDefaultHeight(HEIGHT)

        // setup buttons

        resetLayout()

        btnNewProject.setToolTip("New Project", "Create new project")
        btnNewProject.setClickListener(this::createProjectHandler)

        btnOpenProject.setToolTip("Open Project", "Open an exsting project")
        btnOpenProject.setClickListener(this::openProjectHandler)

        btnExportProject.setToolTip("Export Project", "Export the project as a runnable")

        addComponent(btnNewProject)
        addComponent(btnOpenProject)
        addComponent(btnExportProject)

        addSeperator()

        btnNewFile.setToolTip("New File", "Create new file")
        btnNewFile.setClickListener(this::createSourceFileHandler)

        btnDeleteFile.setToolTip("Delete File", "Delete selected file")
        btnDeleteFile.setClickListener(this::deleteSourceFileHandler)

        btnSaveProject.setToolTip("Save File", "Save current file")
        btnSaveProject.setClickListener(this::saveProjectHandler)

        addComponent(btnNewFile)
        addComponent(btnDeleteFile)
        addComponent(btnSaveProject)

        addSeperator()

        btnCopyAll.setToolTip("Copy All", "Copy all the contents of the current file to the clipboard")
        btnPaste.setToolTip("Paste", "Paste the contents of the clipboard to the current file")

        addComponent(btnCopyAll)
        addComponent(btnPaste)

        addSeperator()

        btnRun.setToolTip("Run", "Run code")
        btnRun.setClickListener(this::runProjectHandler)

        btnStop.setToolTip("Stop", "Stop running code")

        addComponent(btnRun)
        addComponent(btnStop)

        addSeperator()

        btnSettings.setToolTip("Settings", "Open and edit project settings")

        addComponent(btnSettings)

        // setup layout
        listFiles.setItemClickListener(this::fileSelectedHandler)

        txtCodeEditor.setKeyListener { _ ->
            this.currentSourceFile?.source = this.txtCodeEditor.text
            btnSaveProject.setEnabled(true)
            true
        }

        addComponent(listFiles)
        addComponent(txtCodeEditor)

        // disable project buttons until a project is loaded

        id = getInfo().formattedId
        projectToggled = false
        editorToggled = false
        runtimeToggled = false

        this.btnNewProject.setEnabled(true)
        this.btnOpenProject.setEnabled(true)
        this.btnSettings.setEnabled(true)
    }

    override fun onClose() {
        super.onClose()
        this.listFiles.items.clear()
        this.txtCodeEditor.clear()
    }

    override fun save(tagCompound: NBTTagCompound) {

    }

    override fun load(tagCompound: NBTTagCompound) {

    }
}
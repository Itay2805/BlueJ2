package me.itay.bluej.project

import com.mrcrayfish.device.api.io.File
import com.mrcrayfish.device.api.io.Folder
import com.mrcrayfish.device.core.io.FileSystem
import me.itay.bluej.BlueJApp
import me.itay.bluej.api.error.NoSourceFileSelectedException
import me.itay.bluej.languages.BlueJLanguage
import me.itay.bluej.utils.Extensions
import me.itay.bluej.utils.getFileSync
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import java.util.*
import java.util.function.Consumer

const val FIELD_ROOT = "root"
const val FIELD_NAME = "name"
const val FIELD_LANG = "lang"
const val FIELD_STARTUP = "startup"
const val FIELD_FILES_LIST = "files"
const val FIELD_FILE = "field_"

const val FILE_BLUEJ_PROJECT = "bjproj"

class Project(val projectRoot: Folder, private val name: String, val projectLanguage: BlueJLanguage) {
    val src = ArrayList<SourceFile>()
    var startupFile: SourceFile? = null
        set(startupFile) {
            field = startupFile

            val projectFile = projectRoot.getFile(FILE_BLUEJ_PROJECT)
            val compound = Objects.requireNonNull<File>(projectFile).data

            if (startupFile == null) {
                Objects.requireNonNull<NBTTagCompound>(compound).removeTag(FIELD_STARTUP)
            } else {
                Objects.requireNonNull<NBTTagCompound>(compound).setString(FIELD_STARTUP, startupFile.name)
            }

            projectFile!!.data = compound
        }
    private val projectFile: File?
        get() = this.projectRoot.getFileSync(FILE_BLUEJ_PROJECT)

    fun save(): NBTTagCompound {
        val projectdata = NBTTagCompound()
        projectdata.setString(FIELD_ROOT, this.projectRoot.name)
        projectdata.setString(FIELD_NAME, this.name)
        projectdata.setString(FIELD_LANG, this.projectLanguage.name)
        if (this.startupFile != null) {
            val startupnbt = this.startupFile!!.data
            startupnbt.setString("name", this.startupFile!!.name)
            projectdata.setTag(FIELD_STARTUP, startupnbt)
        }
        if (!src.isEmpty()) {
            val files = NBTTagList()
            for (i in src.indices) {
                val f = src[i]
                if (this.projectRoot.getFolder("src")!!.hasFile(f.name)) {
                    val fd = NBTTagCompound()
                    fd.setString("name", f.name)
                    fd.setTag(FIELD_FILE + i, f.data)
                    files.appendTag(fd)
                    continue
                }
                src.remove(f)
            }
            projectdata.setTag(FIELD_FILES_LIST, files)
        }
        this.projectFile!!.data = projectdata
        return projectdata
    }

    fun getSourceFile(name: String): SourceFile? {
        for (src in src) {
            if (src.name == name) {
                return src
            }
        }
        return null
    }

    fun addSourceFile(sourcefile: SourceFile) {
        src.add(sourcefile)
    }

    /**
     * Use this for creating and adding a source file.
     * If you already have a source file, use [Project.addSourceFile] instead.
     * @param name name of the file
     * @param runnable a runnable lambda function to be executed during the creation of the source file
     * @return the source file
     */
    fun createSourceFile(name: String, runnable: Runnable?): SourceFile {
        val f = File(name, BlueJApp.id, NBTTagCompound())
        projectRoot.getFolder("src")?.add(f) { resp, ok ->
            if (ok) {
                addSourceFile(SourceFile(f))
                runnable?.run()
            } else {
                System.err.println("[ERROR] could not create source file: " + Objects.requireNonNull<FileSystem.Response>(resp).message)
            }
        }
        return SourceFile(f)
    }

    fun deleteSourceFile(name: String, runnable: Runnable?) {
        val srcF = getSourceFile(name)
        src.remove(srcF)
        srcF!!.file.delete { _, ok ->
            if (ok) {
                runnable?.run()
            } else {
                try {
                    throw NoSourceFileSelectedException()
                } catch (e: NoSourceFileSelectedException) {
                    println(e.message + e.cause?.message)
                    e.printStackTrace()
                }

            }
        }
    }

    fun load(consumer: Consumer<Project>): Boolean {
        var projectFolder: Folder? = this.projectRoot
        projectFolder!!.files.forEach { f -> println("\t" + f.name) }
        if (!projectFolder.hasFile(FILE_BLUEJ_PROJECT)) {
            if (Objects.requireNonNull<Folder>(projectFolder.parent).hasFile(FILE_BLUEJ_PROJECT)) {
                projectFolder = projectFolder.parent
            } else {
                // @Todo return proper errors
                return false
            }
        }

        val projectFile = projectFolder!!.getFile(FILE_BLUEJ_PROJECT)
        val project = loadFromProjectFile(projectFile!!)
        if (project != null) {
            consumer.accept(project)
            return true
        }
        return false
    }
}
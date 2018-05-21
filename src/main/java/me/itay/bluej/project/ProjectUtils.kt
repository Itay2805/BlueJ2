package me.itay.bluej.project

import com.mrcrayfish.device.api.io.File
import com.mrcrayfish.device.api.io.Folder
import me.itay.bluej.BlueJApp
import me.itay.bluej.languages.BlueJLanguage
import me.itay.bluej.languages.BlueJRuntimeManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.Constants
import java.util.*

fun loadFromProjectFile(file: File): Project?{
    if(file.name != FILE_BLUEJ_PROJECT){
        System.err.println("[ERROR] File is not a project file!")
        return null
    }
    val nbt = file.data!!
    if(nbt.hasKey(FIELD_NAME) && nbt.hasKey(FIELD_LANG)){
        val rootfolder = file.parent
        val name = nbt.getString(FIELD_NAME)
        val lang = nbt.getString(FIELD_LANG)
        var startup: File? = null
        val sourceFiles = ArrayList<File>()
        if(nbt.hasKey(FIELD_STARTUP)){
            val startupnbt = nbt.getCompoundTag(FIELD_STARTUP)
            val startupname = if(nbt.hasKey("name")) nbt.getString("name") else ""
            if(startupname.isNotBlank()){
                startup = File.fromTag(startupname, startupnbt)
            }
        }
        if(nbt.hasKey(FIELD_FILES_LIST)){
            val filesnbt = nbt.getTagList(FIELD_FILES_LIST, Constants.NBT.TAG_COMPOUND)
            for(tag in filesnbt){
                if(tag is NBTTagCompound){
                    val f = File.fromTag(tag.getString("name"), tag.getCompoundTag(FIELD_FILE + filesnbt.indexOf(tag)))
                    sourceFiles += f
                }
            }
        }
        val project = Project(rootfolder!!, name, BlueJRuntimeManager.getLanguage(lang))
        if(startup != null)
            project.startupFile = SourceFile(startup)
        if(sourceFiles.isNotEmpty()){
            for(f in sourceFiles){
                project.addSourceFile(SourceFile(f))
            }
        }
        return project
    }
    return null
}

fun createProject(projectRoot: Folder, name: String, lang: BlueJLanguage): Project? {
    createFolder(projectRoot, "src", Runnable{
        createFolder(projectRoot, "res", Runnable{
            createFolder(projectRoot, "build", Runnable {
                val projtag = NBTTagCompound()
                projtag.setString("root", projectRoot.name)
                projtag.setString(FIELD_NAME, name)
                projtag.setString(FIELD_LANG, lang.name)
                val file = File(FILE_BLUEJ_PROJECT, BlueJApp.id, projtag)
                projectRoot.add(file) { resp, ok ->
                    if(!ok) {
                        throw IllegalStateException(
                                """Failed to add project file to root folder.
                                    |This is a required operation!
                                    |Please report to the author!""".trimMargin())
                    }
                }
            })
        })
    })
    return Project(projectRoot, name, lang)
}

private fun createFolder(parent: Folder, name: String, runnable: Runnable) {
    if (!parent.hasFolder(name)) {
        parent.add(Folder(name)) { resp, ok ->
            if (ok) {
                runnable.run()
            } else {
                // @Todo do proper error handling
                System.err.println("[ERROR] could not create folder: " + resp?.message)
            }
        }
    } else {
        runnable.run()
    }
}
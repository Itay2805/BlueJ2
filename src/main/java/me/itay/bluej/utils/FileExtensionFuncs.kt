@file:JvmName("Folder")

package me.itay.bluej.utils

import com.mrcrayfish.device.api.io.File
import com.mrcrayfish.device.api.io.Folder
import com.mrcrayfish.device.api.task.TaskManager
import com.mrcrayfish.device.core.Laptop
import com.mrcrayfish.device.core.io.task.TaskGetFiles
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.Constants

/**
 * Sends a task (device mod mc network message) to the server from the client retrieving the files on the server
 * This way all the files on the server are found by  and thus fixing the crash with Project since Folder cannot find
 * all files since all files are saved on the server and must be retrieved by a task.
 */
fun Folder.getFilesSync(): List<File>{
    val files = arrayListOf<File>()
    val task = TaskGetFiles(this, Laptop.getPos())
    task.setCallback { nbt, success ->
        if(success){
            if(nbt?.hasKey("files", Constants.NBT.TAG_LIST)!!){
                val list = nbt.getTagList("files", Constants.NBT.TAG_COMPOUND)
                for(file in list){
                    file as NBTTagCompound
                    val f = File.fromTag(file.getString("file_name"), file.getCompoundTag("data"))
                    files.add(f)
                }
            }
        }
    }
    TaskManager.sendTask(task)
    return files
}

/**
 * Gets a file from the receiver object (Folder) by looking on the server for all files instead of just the client,
 * which only has folders.
 */
fun Folder.getFileSync(name: String): File?{
    val files = getFilesSync()
    for(file in files){
        if(file.name == name){
            return file
        }
    }
    return null
}

/**
 * Not to self: Do I need this anymore? I'll keep it just in case
 *
 * This is for better java interop since extensions are kind tricky.
 *
 * The way that kotlin extensions work is that when you declare them top-level,
 * as I have, they are compiled like java static. Though it has a type receiver (Folder),
 * it doesn't compile as though that class has that function within it, it's only accessible as though it has been.
 * So when java code accesses a function extension, it's not found, especially when it's top level.
 * You can access that same function differently now with this object. The reason for creating this object wrapper
 * opposed to documenting how to properly access a top-level declaration from java, is to ease the way people using java
 * interop with kotlin. People understand objects and types better rather than using
 * <pre>
 *     FileExtensionFuncsKt#someTopLevelFunction()
 * </pre>
 * Doing this will avoid hassle with people whom don't know anything about kotlin
 * and to avoid confusion and complications.
 */
object Extensions{
    fun getFileSync(receiver: Folder, name: String): File? = receiver.getFileSync(name)
}

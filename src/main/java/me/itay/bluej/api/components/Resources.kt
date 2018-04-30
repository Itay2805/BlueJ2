package me.itay.bluej.api.components

import com.mrcrayfish.device.api.io.File
import com.mrcrayfish.device.api.io.Folder

sealed class Resource
class ResourceFile(val file: File) : Resource()
class ResourceFolder(val folder: Folder) : Resource()
//TODO: Add more resources
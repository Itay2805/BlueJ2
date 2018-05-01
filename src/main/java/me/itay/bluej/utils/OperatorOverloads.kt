package me.itay.bluej.utils

import com.mrcrayfish.device.api.app.component.ItemList
import com.mrcrayfish.device.api.io.File
import me.itay.bluej.project.Project
import me.itay.bluej.project.SourceFile

operator fun Project?.plusAssign(file: SourceFile){
    this?.addSourceFile(file)
}

operator fun Project?.plusAssign(file: File){
    this?.addSourceFile(SourceFile(file))
}

operator fun Project?.plusAssign(filename: String){
    this?.createSourceFile(filename, null)
}

operator fun Project?.minusAssign(filename: String){
    this?.deleteSourceFile(filename, null)
}

operator fun ItemList<String>.plus(item: String){
    this.addItem(item)
}

operator fun ItemList<String>.minus(index: Int){
    this.removeItem(index)
}
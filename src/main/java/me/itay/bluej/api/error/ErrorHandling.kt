package me.itay.bluej.api.error

import com.mrcrayfish.device.core.Wrappable
import me.itay.bluej.dialogs.ErrorCode
import me.itay.bluej.dialogs.ErrorDialog
import net.minecraft.realms.Tezzelator.t
import java.io.OutputStream
import java.io.PrintStream

fun initErrorHandler(){
    System.setOut(BlueJPrintStream(System.out))
    System.setErr(BlueJErrorStream(System.err))
}

abstract class BlueJCustomStream(val ridingStream: OutputStream) : PrintStream(ridingStream){
    val sb = StringBuilder()

    override fun println(any: Any){
        this.print("$any\n")
    }

    abstract override fun print(any: Any)

    override fun write(b: Int) {
        this.ridingStream.write(b)
    }
}

class BlueJPrintStream(ridingStream: OutputStream) : BlueJCustomStream(ridingStream){
    override fun write(b: Int) {
        ridingStream.write(b)
    }

    override fun print(any: Any) {
        val string = t.toString()
        string.forEach {
            this.write(it.toInt())
        }
        System.out.print(sb)
    }

}

class BlueJErrorStream(ridingStream: OutputStream) : BlueJCustomStream(ridingStream){
    private var prefix = ""

    override fun print(any: Any) {
        val string = any.toString()
        sb.append("[$prefix]")
        string.forEach {
            this.write(it.toInt())
        }
        System.out.print(sb)
    }

    override fun println(any: Any){
        this.print("[$prefix] $any")
    }

    fun print(any: Any, code: ErrorCode){
        this.prefix = code.errorname.toUpperCase()
        this.print(any)
    }

    fun printError(any: Any){
        this.print(any, ErrorCode.ERROR)
    }

    fun printlnError(any: Any){
        this.println(any, ErrorCode.ERROR)
    }

    fun crash(any: Any){
        this.println(any, ErrorCode.FATAL)
    }

    fun println(any: Any, code: ErrorCode){
        this.prefix = code.errorname.toUpperCase()
        this.println(any)
    }

}

fun Wrappable.displayError(message: String, code: ErrorCode){
    when(code.code){
        0 -> this.openDialog(ErrorDialog(code, message))
        1 -> {
            this.openDialog(ErrorDialog(code, message))
        }
    }
}
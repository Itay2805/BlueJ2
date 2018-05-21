package me.itay.bluej

import com.mrcrayfish.device.api.ApplicationManager

import me.itay.bluej.languages.js.JavaScriptRuntime
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import me.itay.bluej.languages.BlueJRuntimeManager.registerLanguage

const val MODID = "bluej"
const val VERSION = "2.0.0"
val LOGGER = LogManager.getLogger(MODID)
val APP_ID = ResourceLocation("bluej", "ide")

@Mod(modid=MODID, version=VERSION)
class BlueJMod {
    @EventHandler
    fun init(event: FMLInitializationEvent) {
        registerLanguage(JavaScriptRuntime())
        ApplicationManager.registerApplication(APP_ID, BlueJApp::class.java)
    }
}

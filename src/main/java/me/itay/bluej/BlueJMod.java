package me.itay.bluej;

import com.mrcrayfish.device.api.ApplicationManager;

import me.itay.bluej.languages.js.JavaScriptRuntime;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static me.itay.bluej.languages.BlueJRuntimeManager.registerLanguage;

@Mod(modid = BlueJMod.MODID, version = BlueJMod.VERSION)
public class BlueJMod
{
    public static final String MODID = "bluej";
    public static final String VERSION = "2.0.0";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        registerLanguage(new JavaScriptRuntime());
    	ApplicationManager.registerApplication(new ResourceLocation("bluej", "ide"), BlueJApp.class);
    }
}

package me.itay.bluej;

import com.mrcrayfish.device.api.ApplicationManager;

import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = BlueJMod.MODID, version = BlueJMod.VERSION)
public class BlueJMod
{
    public static final String MODID = "bluej";
    public static final String VERSION = "2.0.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	ApplicationManager.registerApplication(new ResourceLocation("bluej", "ide"), BlueJApp.class);
    }
}

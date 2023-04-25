package com.yuluo.dglab;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "dglab-controller", name = "DGLab Controller", version = "1.0.0", acceptedMinecraftVersions = "[1.8.9]")
public class DGLabController {
    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new CommandDglabPunish());
        System.out.println("punish registered!");
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        try {
            event.registerServerCommand(new CommandDglab());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

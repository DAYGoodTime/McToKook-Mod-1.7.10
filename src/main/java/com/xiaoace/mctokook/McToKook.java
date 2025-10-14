package com.xiaoace.mctokook;

import java.util.Map;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xiaoace.mctokook.listener.OnPlayerMessage;
import com.xiaoace.mctokook.listener.PlayerEvents;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import snw.kookbc.impl.KBCClient;

@Mod(
    modid = McToKook.MODID,
    version = McToKook.Version,
    name = McToKook.MODNAME,
    acceptedMinecraftVersions = "[1.7.10]",
    acceptableRemoteVersions = "*")
public class McToKook {

    public static final String MODID = "mctokook";
    public static final String MODNAME = "McToKook";
    public static final String Version = "2.0.0";

    public static final Logger LOG = LogManager.getLogger(McToKook.MODID);

    public static KBCClient kbcClient = null;
    public static McToKook instance = null;

    public static Map<String, Long> playerOnlineTime;

    @SidedProxy(clientSide = "com.xiaoace.mctokook.ClientProxy", serverSide = "com.xiaoace.mctokook.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event, this);
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance()
            .bus()
            .register(new PlayerEvents());
        MinecraftForge.EVENT_BUS.register(new OnPlayerMessage());
        proxy.init(event);
    }

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

}

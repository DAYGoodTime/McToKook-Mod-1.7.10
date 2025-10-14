package com.xiaoace.mctokook;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.

    public void preInit(FMLPreInitializationEvent event, McToKook modInstance) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        McToKook.LOG.info(Config.configList());
        McToKook.LOG.info("Hello Forge! Here is McToKooK 1710");
        McToKook.LOG.info("Detected ClientSide Run,McToKooK will not enable!");
    }
}

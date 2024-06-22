package com.day.mctokook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.day.mctokook.commands.kook.KookCommands;
import com.day.mctokook.listener.KookListener;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import snw.jkook.JKook;
import snw.jkook.command.CommandManager;
import snw.jkook.config.file.YamlConfiguration;
import snw.jkook.entity.channel.TextChannel;
import snw.kookbc.impl.CoreImpl;
import snw.kookbc.impl.KBCClient;
import snw.kookbc.impl.plugin.InternalPlugin;

public class ServerProxy extends CommonProxy {

    // 让xiaoACE emo的 emoji
    // private EmojiHandler emojiHandler;

    private static final File kbcSetting = new File(".", "config/McToKook/kbc.yml");
    private static final File configFolder = new File(".", "config/McToKook");

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event, McToKook modInstance) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        McToKook.LOG.info(Config.configList());
        McToKook.LOG.info("Hello Forge! Here is McToKooK 1710");
        // emojiHandler = new EmojiHandler(this);
        if (!configFolder.exists()) {
            configFolder.mkdir();
        }
        // KookBC保存基础配置文件
        saveKBCConfig();

        YamlConfiguration config = new YamlConfiguration();

        config.set("mode", "websocket");
        CoreImpl core = new CoreImpl();
        JKook.setCore(core);

        // 读取配置拿必要的东西
        String bot_token = Config.bot_token;
        String channel_ID = Config.channel_ID;

        if (bot_token.equals("No token provided")) {
            McToKook.LOG.info("你没有提供bot-token或者bot-token不正确");
            McToKook.LOG.info("McToKook-Mod将会停用");
            throw new Error("你没有提供bot-token或者bot-token不正确,McToKook-Mod将会停用,服务端即将崩溃");
        } else {
            if (channel_ID.equals("No channel ID provided")) {
                McToKook.LOG.info("你没有提供channel ID或channel ID不正确");
                McToKook.LOG.info("你所提供的channel_ID: " + channel_ID);
                throw new Error("你没有提供channel ID或channel ID不正确,McToKook-Mod将会停用,服务端即将崩溃");
            }
        }

        McToKook.kbcClient = new KBCClient(core, config, null, bot_token);

        McToKook.kbcClient.start();
        TextChannel channel = (TextChannel) McToKook.kbcClient.getCore()
            .getHttpAPI()
            .getChannel(channel_ID);

        // 注册KOOK消息监听器
        // 夏夜说: 不要用InternalPlugin,但是我摆了！
        McToKook.kbcClient.getCore()
            .getEventManager()
            .registerHandlers(McToKook.kbcClient.getInternalPlugin(), new KookListener(modInstance));
        // 注册KOOK指令
        CommandManager commandManager = McToKook.kbcClient.getCore()
            .getCommandManager();
        InternalPlugin plugin = McToKook.kbcClient.getInternalPlugin();
        KookCommands commands = new KookCommands();
        commandManager.registerCommand(plugin, commands.list);
        commandManager.registerCommand(plugin, commands.info);
        commandManager.registerCommand(plugin, commands.order);
        commandManager.registerCommand(plugin, commands.dict_upload);
        commandManager.registerCommand(plugin, commands.dict);
        // ClientCommandHandler.instance.registerCommand(new SetProxyCommand());
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        if (Config.record_player_time) {
            int max = FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .getMaxPlayers();
            if (max > 30) {
                McToKook.playerOnlineTime = new HashMap<>();
            } else {
                McToKook.playerOnlineTime = new HashMap<>(max);
            }
        }
    }

    // KookBC保存配置文件 爱来自夏夜
    private static void saveKBCConfig() {
        try (final InputStream stream = McToKook.class.getResourceAsStream("/kbc.yml")) {
            if (stream == null) {
                throw new Error("Unable to find kbc.yml");
            }
            if (kbcSetting.exists()) {
                return;
            }
            // noinspection ResultOfMethodCallIgnored
            kbcSetting.createNewFile();

            try (final FileOutputStream out = new FileOutputStream(kbcSetting)) {
                int index;
                byte[] bytes = new byte[1024];
                while ((index = stream.read(bytes)) != -1) {
                    out.write(bytes, 0, index);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

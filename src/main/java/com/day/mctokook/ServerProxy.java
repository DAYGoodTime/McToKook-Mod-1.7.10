package com.day.mctokook;

import com.day.mctokook.commands.kook.CommandInit;
import com.day.mctokook.commands.mc.ReInitCommand;
import com.day.mctokook.listener.KookListener;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import snw.jkook.JKook;
import snw.jkook.config.file.YamlConfiguration;
import snw.jkook.entity.channel.TextChannel;
import snw.kookbc.impl.CoreImpl;
import snw.kookbc.impl.KBCClient;
import snw.kookbc.impl.plugin.InternalPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

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
        McToKook.instance = modInstance;
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
        connectKBC(core, config, bot_token, modInstance, channel_ID);
        //registry server command
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null || server.getCommandManager()==null) {
            McToKook.LOG.error("无法注册命名，是否在服务端环境内?");
            return;
        }
        ICommandManager cm = server.getCommandManager();
        if(cm instanceof ServerCommandManager) {
            ServerCommandManager scm = (ServerCommandManager)cm;
            scm.registerCommand(new ReInitCommand());
        }
    }

    public static void connectKBC(CoreImpl core, YamlConfiguration config, String bot_token, McToKook modInstance,
        String channel_ID) {
        McToKook.kbcClient = new KBCClient(core, config, null, bot_token);

        McToKook.kbcClient.start();
        //主动尝试获取channel,如果任何
        TextChannel channel = (TextChannel) McToKook.kbcClient.getCore()
            .getHttpAPI()
            .getChannel(channel_ID);
        // 注册KOOK消息监听器
        // 夏夜说: 不要用InternalPlugin,但是我摆了！
        McToKook.kbcClient.getCore()
            .getEventManager()
            .registerHandlers(McToKook.kbcClient.getInternalPlugin(), new KookListener(modInstance));
        // 注册KOOK指令
        InternalPlugin plugin = McToKook.kbcClient.getInternalPlugin();
        CommandInit.InitKookCommand(plugin);
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
            McToKook.LOG.error("保存配置文件失败:{}", e.getLocalizedMessage(), e);
        }
    }
}

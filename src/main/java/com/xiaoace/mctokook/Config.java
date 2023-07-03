package com.xiaoace.mctokook;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import net.minecraftforge.common.config.Configuration;

public class Config {

    public static String bot_token = "No token provided";

    public static String channel_ID = "No channel ID provided";

    public static String to_Minecraft_Message = "用户: {nickName} 说: {message}";

    public static String to_Kook_Message = "玩家: {playerName} 说: {message}";

    public static String player_Join_Message = "{playerName}偷偷的溜进了服务器";

    public static String player_Quit_Message = "肝帝{playerName}歇逼了";

    public static Boolean to_Minecraft = true;

    public static Boolean to_Kook = true;

    public static Boolean join_Message = true;

    public static Boolean quit_Message = true;

    public static Boolean record_player_time = true;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);
        bot_token = configuration
            .getString("bot_token", Configuration.CATEGORY_GENERAL, bot_token, "这里应该填入bot的token\n注意! 如果不填写此项服务端会直接崩溃");
        channel_ID = configuration.getString(
            "channel_ID",
            Configuration.CATEGORY_GENERAL,
            channel_ID,
            "这里应该填入要进行McToKook的Kook频道ID,\n注意! 如果不填写此项服务端会直接崩溃");
        join_Message = configuration
            .getBoolean("join_Message", Configuration.CATEGORY_GENERAL, join_Message, "是否启用玩家进入游戏提示\n");
        player_Join_Message = configuration.getString(
            "player_Join_Message",
            Configuration.CATEGORY_GENERAL,
            player_Join_Message,
            "自定义MC玩家上线消息格式 你可以按照默认给出的样式去自定义，记得保留带{}的内容\n");
        quit_Message = configuration
            .getBoolean("quit_Message", Configuration.CATEGORY_GENERAL, quit_Message, "是否启用玩家退出游戏提示\n");
        player_Quit_Message = configuration.getString(
            "player_Quit_Message",
            Configuration.CATEGORY_GENERAL,
            player_Quit_Message,
            "自定义MC玩家下线消息格式 你可以按照默认给出的样式去自定义，记得保留带{}的内容,\n");
        to_Kook = configuration
            .getBoolean("to_Kook", Configuration.CATEGORY_GENERAL, to_Kook, "是否启用Minecraft to Kook\n");
        to_Kook_Message = configuration.getString(
            "to_Kook_Message",
            Configuration.CATEGORY_GENERAL,
            to_Kook_Message,
            "自定义KOOK消息格式 你可以按照默认给出的样式去自定义，记得保留带{}的内容\n");
        to_Minecraft = configuration
            .getBoolean("to_Minecraft", Configuration.CATEGORY_GENERAL, to_Minecraft, "是否启用Kook to Minecraft\n");
        to_Minecraft_Message = configuration.getString(
            "to_Minecraft_Message",
            Configuration.CATEGORY_GENERAL,
            to_Minecraft_Message,
            "自定义MC消息格式 你可以按照默认给出的样式去自定义，记得保留带{}的内容\n");
        record_player_time = configuration.getBoolean(
            "record_player_time",
            Configuration.CATEGORY_GENERAL,
            record_player_time,
            "是否记录每位玩家的游玩时间,默认true");
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    // only for DEV
    public static String configList() {
        Class<Config> cfg = Config.class;
        return Arrays.stream(cfg.getDeclaredFields())
            .map(Field::getName)
            .collect(Collectors.toList())
            .toString();
    }
}

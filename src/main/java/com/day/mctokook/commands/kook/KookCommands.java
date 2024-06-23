package com.day.mctokook.commands.kook;

import snw.jkook.command.JKookCommand;

public class KookCommands {

    public final JKookCommand list;
    public final JKookCommand order;
    public final JKookCommand info;
    public final JKookCommand dict;
    public final JKookCommand dict_upload;
    public final JKookCommand sync;

    public KookCommands() {
        list = new JKookCommand("list", "/").setDescription("用法: /list ; 作用: 返回当前服务器内的玩家列表")
            .setHelpContent("用法: /list ; 作用: 返回当前服务器内的玩家列表")
            .executesUser(new ListCommand())
            .addAlias("人头");
        order = new JKookCommand("order", "/").setDescription("尝试向AE发送合成请求")
            .setHelpContent("用法: /order 物品id meta 数量 ; 例如: /order minecraft:stone 0 10")
            .executesUser(new OrderCommand())
            .addAlias("下单");
        info = new JKookCommand("info", "/").setDescription("查看AE单子")
            .setHelpContent("用法: /info ; 或者 /单子")
            .executesUser(new InfoCommand())
            .addAlias("单子");
        dict = new JKookCommand("dict", "/").setDescription("查看物品名映射表")
            .setHelpContent("用法: /dict")
            .executesUser(new QueryDictionaryCommand())
            .addAlias("字典");
        dict_upload = new JKookCommand("dictUpload", "/").setDescription("上传映射")
            .setHelpContent("用法:/dictUpload 映射值 !原物品名! (原物品名必须用感叹号包围)")
            .executesUser(new UploadDictionaryCommand())
            .addAlias("dp")
            .addAlias("字典上传");
        sync = new JKookCommand("sync","/").setDescription("同步代码")
            .setHelpContent("用法: /sync")
            .executesUser(new SyncRemoteCodeCommand());
    }
}

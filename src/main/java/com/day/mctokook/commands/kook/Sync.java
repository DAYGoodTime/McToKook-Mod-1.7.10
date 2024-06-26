package com.day.mctokook.commands.kook;

import com.day.mctokook.Config;
import com.day.mctokook.McToKook;

import cn.hutool.http.HttpUtil;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import snw.jkook.message.Message;
import snw.kookbc.impl.command.litecommands.annotations.prefix.Prefix;

@Command(name = "sync", aliases = { "同步代码" })
@Prefix("/")
@Description("更新OC代码")
public class Sync {

    @Execute
    public void sync(@Context Message message) {
        try {
            String response = HttpUtil.get(Config.SYNC_API);
            if (Boolean.parseBoolean(response)) {
                message.reply("更新成功");
            } else {
                message.reply("更新失败");
            }
        } catch (Throwable e) {
            message.reply("插件出现意外错误:" + e.getLocalizedMessage());
            McToKook.LOG.error("内部错误:{}", e.getLocalizedMessage(), e);
        }
    }
}

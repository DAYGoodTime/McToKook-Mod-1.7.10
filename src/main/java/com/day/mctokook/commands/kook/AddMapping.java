package com.day.mctokook.commands.kook;

import java.util.HashMap;
import java.util.Map;

import com.day.mctokook.Config;
import com.day.mctokook.McToKook;

import cn.hutool.http.HttpUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.quoted.Quoted;
import snw.jkook.message.Message;
import snw.kookbc.impl.command.litecommands.annotations.prefix.Prefix;

@Command(name = "添加映射", aliases = { "am", "addMap" })
@Prefix("/")
@Description("为物品名添加映射,参数:<当前名字> <映射名字> (名字如果有空格都需要用双引号包裹,例如\"A item name\" \"B item name\")")
public class AddMapping {

    @Execute
    public void addMapping(@Context Message message, @Arg @Quoted String[] args) {
        try {
            if (args.length != 2) {
                message.reply("参数错误");
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put(args[0], args[1]);
            String response = HttpUtil.post(Config.LABEL_DICTION_API + "/upload", params);
            boolean success = Boolean.parseBoolean(response);
            if (success) {
                message.reply("添加成功");
            } else {
                message.reply("添加失败,可能该字典已被添加");
            }
        } catch (Throwable e) {
            message.reply("插件内部异常:{}" + e.getLocalizedMessage());
            McToKook.LOG.error("插件内部异常:{}", e.getLocalizedMessage(), e);
        }
    }
}

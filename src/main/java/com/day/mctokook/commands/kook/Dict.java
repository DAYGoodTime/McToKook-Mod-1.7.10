package com.day.mctokook.commands.kook;

import java.util.Map;

import com.day.mctokook.Config;
import com.day.mctokook.McToKook;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import snw.jkook.message.Message;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.module.DividerModule;
import snw.jkook.message.component.card.module.HeaderModule;
import snw.jkook.message.component.card.module.SectionModule;
import snw.kookbc.impl.command.litecommands.annotations.prefix.Prefix;
import snw.kookbc.impl.network.exceptions.BadResponseException;

@Command(name = "物品映射", aliases = { "dict", "字典" })
@Prefix("/")
@Description("查看物品映射名")
public class Dict {

    @Execute
    public void dict(@Context Message message) {
        String json;
        try {
            json = HttpUtil.get(Config.LABEL_DICTION_API);
            if (json == null) {
                message.reply("服务器无响应");
                return;
            }
            Map<String, Object> result = JSONUtil.parseObj(json)
                .getRaw();
            CardBuilder builder = new CardBuilder().setTheme(Theme.INFO)
                .setSize(Size.LG);
            builder.addModule(new HeaderModule("目前映射的物品名如下:"));
            for (Map.Entry<String, Object> entry : result.entrySet()) {
                builder.addModule(
                    new SectionModule(
                        new MarkdownElement(
                            (StrUtil.format("源:{} 映射后:{}", entry.getKey(), (String) entry.getValue())))));
                builder.addModule(DividerModule.INSTANCE);
            }
            message.reply(builder.build());
        } catch (BadResponseException e) {
            message.reply("kook消息发送失败:" + e.getLocalizedMessage());
            McToKook.LOG.warn("kook消息发送失败:{},消息内容{}", e.getLocalizedMessage(), e);
        } catch (Throwable e) {
            message.reply("插件内部异常:{}" + e.getLocalizedMessage());
            McToKook.LOG.error("插件内部异常:{}", e.getLocalizedMessage(), e);
        }
    }

}

package com.day.mctokook.commands.kook;

import java.util.Optional;

import com.day.mctokook.modules.ae.AEHelper;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import snw.jkook.message.Message;
import snw.kookbc.impl.command.litecommands.annotations.prefix.Prefix;
import snw.kookbc.impl.network.exceptions.BadResponseException;

@Command(name = "order", aliases = "下单")
@Prefix("/")
@Description("请求AE合成,参数: <物品名(如果要带空格,请用'-'分割)> <物品数量> <物品Meta值> ")
public class Order {

    @Execute
    public void order(@Context Message message, @Join(separator = "-") String name, @Arg Integer count,
        @Arg Optional<Integer> meta) {
        try {
            boolean success = AEHelper.OrderItem(name, meta.orElse(0), count);
            if (success) {
                message.reply("下单成功!");
            } else {
                message.reply("下单失败!");
            }
        } catch (BadResponseException e) {
            message.reply("KOOK消息异常:" + e.getLocalizedMessage());
        }
    }
}

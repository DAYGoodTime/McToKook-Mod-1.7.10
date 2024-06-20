package com.day.mctokook.commands.kook;

import org.jetbrains.annotations.Nullable;

import com.day.mctokook.modules.ae.AEHelper;
import com.day.mctokook.utils.arg.ArgumentsProcessor;

import snw.jkook.command.UserCommandExecutor;
import snw.jkook.entity.User;
import snw.jkook.message.Message;
import snw.kookbc.impl.network.exceptions.BadResponseException;

public class OrderCommand implements UserCommandExecutor {

    @Override
    public void onCommand(User user, Object[] arguments, @Nullable Message message) {
        try {
            if (message == null) return;
            ArgumentsProcessor arg = new ArgumentsProcessor(arguments, message, null, null);
            String item = arg.when(3, 0)
                .getAsString("物品id错误");
            int meta = arg.when(3, 1)
                .getAsInt("meta格式错误");
            int count = arg.when(3, 2)
                .getAsInt("物品数量错误");
            boolean success = AEHelper.OrderItem(item, meta, count);
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

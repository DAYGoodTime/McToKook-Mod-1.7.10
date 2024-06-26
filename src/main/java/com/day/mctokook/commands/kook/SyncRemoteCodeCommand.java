package com.day.mctokook.commands.kook;

import org.jetbrains.annotations.Nullable;

import com.day.mctokook.Config;
import com.day.mctokook.McToKook;

import cn.hutool.http.HttpUtil;
import snw.jkook.command.UserCommandExecutor;
import snw.jkook.entity.User;
import snw.jkook.message.Message;

public class SyncRemoteCodeCommand implements UserCommandExecutor {

    @Override
    public void onCommand(User user, Object[] objects, @Nullable Message message) {
        if (message == null) return;
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

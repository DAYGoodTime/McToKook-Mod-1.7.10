package com.day.mctokook.commands.kook;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.day.mctokook.Config;
import com.day.mctokook.McToKook;

import cn.hutool.http.HttpUtil;
import snw.jkook.command.UserCommandExecutor;
import snw.jkook.entity.User;
import snw.jkook.message.Message;

public class UploadDictionaryCommand implements UserCommandExecutor {

    @Override
    public void onCommand(User user, Object[] arguments, @Nullable Message message) {
        try {
            if (message == null) {
                return;
            }
            if (arguments.length < 2) {
                message.reply("请输入正确的参数数量");
                return;
            }
            // 拼接参数
            if (!((String) arguments[1]).startsWith("!") || !((String) arguments[arguments.length - 1]).endsWith("!")) {
                message.reply("该参数请用 ! 包围参数的开头和结尾");
                return;
            }
            StringBuffer name = new StringBuffer();
            for (int i = 1; i < arguments.length; i++) {
                String part = (String) arguments[i];
                if (part.endsWith("!")) break;
                name.append(part);
            }
            // 删除头尾标识符
            name.deleteCharAt(0);
            name.deleteCharAt(name.length() - 1);
            String value = (String) arguments[0];
            Map<String, Object> params = new HashMap<>();
            params.put(name.toString(), value);
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

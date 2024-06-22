package com.day.mctokook.commands.kook;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.day.mctokook.Config;
import com.day.mctokook.McToKook;
import org.jetbrains.annotations.Nullable;
import snw.jkook.command.UserCommandExecutor;
import snw.jkook.entity.User;
import snw.jkook.message.Message;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.module.DividerModule;
import snw.jkook.message.component.card.module.HeaderModule;
import snw.jkook.message.component.card.module.SectionModule;
import snw.kookbc.impl.network.exceptions.BadResponseException;

import java.util.Map;

public class QueryDictionaryCommand implements UserCommandExecutor {
    @Override
    public void onCommand(User user, Object[] objects, @Nullable Message message) {
        String json;
        try{
            if(message==null) return;
            json = HttpUtil.get(Config.LABEL_DICTION_API);
            if(json==null){
                message.reply("服务器无响应");
                return;
            }
            Map<String,Object> result = JSONUtil.parseObj(json).getRaw();
            CardBuilder builder = new CardBuilder().setTheme(Theme.INFO).setSize(Size.MD);
            builder.addModule(new HeaderModule("目前映射的物品名如下:"));
            for(Map.Entry<String, Object> entry : result.entrySet()){
                builder.addModule(new SectionModule(new MarkdownElement((StrUtil.format("源:{} 映射后:{}",entry.getKey(),(String)entry.getValue())))));
                builder.addModule(DividerModule.INSTANCE);
            }
        }catch (BadResponseException e){
          message.reply("kook消息发送失败:"+e.getLocalizedMessage());
          McToKook.LOG.warn("kook消息发送失败:{},消息内容{}",e.getLocalizedMessage(),e);
        } catch (Throwable e){
            message.reply("插件内部异常:{}"+e.getLocalizedMessage());
            McToKook.LOG.error("插件内部异常:{}",e.getLocalizedMessage(),e);
        }
    }
}

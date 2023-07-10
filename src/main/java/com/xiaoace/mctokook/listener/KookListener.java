package com.xiaoace.mctokook.listener;

import static com.xiaoace.mctokook.utils.MinecraftTextConverter.convertToMinecraftFormat;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import net.minecraft.util.ChatComponentText;

import com.xiaoace.mctokook.Config;
import com.xiaoace.mctokook.McToKook;

import cpw.mods.fml.common.FMLCommonHandler;
import snw.jkook.entity.User;
import snw.jkook.event.EventHandler;
import snw.jkook.event.Listener;
import snw.jkook.event.channel.ChannelMessageEvent;
import snw.jkook.message.TextChannelMessage;
import snw.jkook.message.component.BaseComponent;
import snw.jkook.message.component.TextComponent;

import java.util.HashMap;
import java.util.Map;

public class KookListener implements Listener {

    private final McToKook mod;

    public KookListener(McToKook mod) {
        this.mod = mod;
    }

    // Kook消息监听器
    @EventHandler
    public void onKookMessage(ChannelMessageEvent channelMessageEvent) {

        if (!Config.to_Minecraft) {
            return;
        }

        User kookUser = null;
        TextChannelMessage kookMessage = null;

        if (channelMessageEvent.getChannel()
            .getId()
            .equals(Config.channel_ID)) {

            String KookUserUUID = channelMessageEvent.getMessage()
                .getSender()
                .getId();
            // Kook消息发送者
            kookUser = channelMessageEvent.getMessage()
                .getSender();
            //判断是否是机器人消息
            if(kookUser.isBot()){
                return;
            }

            kookMessage = channelMessageEvent.getMessage();

            String kookUserNickName = kookUser.getNickName(
                channelMessageEvent.getChannel()
                    .getGuild());

            BaseComponent component = kookMessage.getComponent();
            // 将要发送至mc里的消息
            // 没错，只有文字消息会被发到mc
            if (component instanceof TextComponent) {

                TextComponent textComponent = (TextComponent) component;

                // 先将kook的消息里的emoji转换成短码形式
                // String the_message_from_kook = EmojiUtil.toAlias(component.toString());
                // // 然后再将它转成对应的韩文
                // the_message_from_kook = mod.getEmojiHandler()
                // .toEmoji(the_message_from_kook);

                // 测试用的
                McToKook.LOG.info("来自KOOK的消息: " + textComponent);

                String needFormatMessage = Config.to_Minecraft_Message;
                Map<String,String> formatMap = MapUtil.builder(new HashMap<String,String>())
                    .put("nickName",kookUserNickName)
                    .put("message",convertToMinecraftFormat(component.toString()))
                        .map();
                String formattedMessage = StrUtil.format(needFormatMessage,formatMap);


//                String formattedMessage = needFormatMessage.replaceAll("\\{nickName}", kookUserNickName)
//                    .replaceAll("\\{message}", convertToMinecraftFormat(component.toString()));

                FMLCommonHandler.instance()
                    .getMinecraftServerInstance()
                    .getConfigurationManager()
                    .sendChatMsg(new ChatComponentText(formattedMessage));
            }
        }
    }

}

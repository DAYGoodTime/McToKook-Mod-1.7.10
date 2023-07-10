package com.xiaoace.mctokook.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import net.minecraftforge.event.ServerChatEvent;

import com.xiaoace.mctokook.Config;
import com.xiaoace.mctokook.McToKook;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import snw.jkook.entity.channel.Channel;
import snw.jkook.entity.channel.TextChannel;

import static com.xiaoace.mctokook.utils.MinecraftTextConverter.convertToMinecraftFormat;

// well this Event need use MinecraftForge Event_Bus,so
public class OnPlayerMessage {

    @SubscribeEvent
    public void onChat(ServerChatEvent event) {

        CompletableFuture.runAsync(() -> {

            String message = event.message;
            String playerName = event.player.getDisplayName();

            // 测试用的
            McToKook.LOG.info("来自游戏内的消息: " + message);

            String needFormatMessage = Config.to_Kook_Message;
            Map<String,String> formatMap = MapUtil.builder(new HashMap<String,String>())
                .put("nickName",playerName)
                .put("message",message)
                .map();
            Channel channel = McToKook.kbcClient.getCore()
                .getHttpAPI()
                .getChannel(Config.channel_ID);

            if (channel instanceof TextChannel) {
                TextChannel textChannel = (TextChannel) channel;
                textChannel.sendComponent(StrUtil.format(needFormatMessage,formatMap));
            }
        });
    }
}

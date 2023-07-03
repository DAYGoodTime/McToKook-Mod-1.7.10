package com.xiaoace.mctokook.listener;

import java.util.concurrent.CompletableFuture;

import net.minecraftforge.event.ServerChatEvent;

import com.xiaoace.mctokook.Config;
import com.xiaoace.mctokook.McToKook;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import snw.jkook.entity.channel.Channel;
import snw.jkook.entity.channel.TextChannel;

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
            // TODO format issue with '$'
            String formattedMessage = needFormatMessage.replaceAll("\\{playerName}", playerName)
                .replaceAll("\\{message}", message);

            Channel channel = McToKook.kbcClient.getCore()
                .getHttpAPI()
                .getChannel(Config.channel_ID);

            if (channel instanceof TextChannel) {
                TextChannel textChannel = (TextChannel) channel;
                textChannel.sendComponent(formattedMessage);
            }
        });
    }
}

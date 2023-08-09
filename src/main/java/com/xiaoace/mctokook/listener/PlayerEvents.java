package com.xiaoace.mctokook.listener;

import static com.xiaoace.mctokook.utils.PlayerIcon.getPlayerIconUr;

import java.util.concurrent.CompletableFuture;

import com.xiaoace.mctokook.Config;
import com.xiaoace.mctokook.McToKook;

import cn.hutool.core.map.MapUtil;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import snw.jkook.entity.abilities.Accessory;
import snw.jkook.entity.channel.Channel;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.ImageElement;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.module.SectionModule;

public class PlayerEvents {

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent loggedInEvent) {

        if (!Config.join_Message) {
            return;
        }
        CompletableFuture.runAsync(() -> {

            String playerName = loggedInEvent.player.getDisplayName();
            String playerUUID = loggedInEvent.player.getGameProfile()
                .getId()
                .toString();

            if (Config.record_player_time) {
                McToKook.playerOnlineTime.put(playerUUID, System.currentTimeMillis());
            }
            Channel channel = McToKook.kbcClient.getCore()
                .getHttpAPI()
                .getChannel(Config.channel_ID);

            if (channel instanceof TextChannel) {
                TextChannel textChannel = (TextChannel) channel;
                textChannel.sendComponent(buildCard(playerName, playerUUID, false));
            }
        });

    }

    @SubscribeEvent
    public void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent loggedOutEvent) {

        if (!Config.quit_Message) {
            return;
        }

        CompletableFuture.runAsync(() -> {

            String playerName = loggedOutEvent.player.getDisplayName();
            String playerUUID = loggedOutEvent.player.getGameProfile()
                .getId()
                .toString();

            Channel channel = McToKook.kbcClient.getCore()
                .getHttpAPI()
                .getChannel(Config.channel_ID);

            if (channel instanceof TextChannel) {
                TextChannel textChannel = (TextChannel) channel;
                textChannel.sendComponent(buildCard(playerName, playerUUID, true));
            }
        });
    }

    private static MultipleCardComponent buildCard(String playerName, String playerUUID, boolean isQuited) {
        String needFormatMessage = Config.player_Join_Message;
        String imageUrl = getPlayerIconUr(playerUUID);
        CardBuilder cardBuilder = new CardBuilder();
        cardBuilder.setTheme(Theme.SUCCESS)
            .setSize(Size.LG);
        if (isQuited) {
            needFormatMessage = Config.player_Quit_Message;
            cardBuilder.setTheme(Theme.DANGER);
        }
        StringBuilder formattedMessage = new StringBuilder();
        formattedMessage.append(needFormatMessage.replaceAll("\\{playerName}", playerName));
        if (isQuited) {
            Long loginTime = MapUtil.getLong(McToKook.playerOnlineTime, playerUUID, 0L);
            long playingTime = (System.currentTimeMillis() - loginTime) / 60000;
            if (playingTime>0) {
                formattedMessage.append(" 游玩时长:")
                    .append(playingTime)
                    .append("分钟");
                McToKook.playerOnlineTime.remove(playerUUID);
            }
        }
        cardBuilder.addModule(
            new SectionModule(
                new MarkdownElement(formattedMessage.toString()),
                new ImageElement(imageUrl, null, Size.SM, false),
                Accessory.Mode.LEFT));
        return cardBuilder.build();
    }
}

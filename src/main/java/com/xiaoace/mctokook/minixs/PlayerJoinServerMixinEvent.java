package com.xiaoace.mctokook.minixs;

import java.util.concurrent.CompletableFuture;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xiaoace.mctokook.Config;
import com.xiaoace.mctokook.McToKook;
import com.xiaoace.mctokook.utils.PlayerIcon;

import cn.hutool.core.map.MapUtil;
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

@Mixin(value = ServerConfigurationManager.class)
public class PlayerJoinServerMixinEvent {

    @Unique
    private static final Logger mcToKook_Mod_1_7_10$LOG = McToKook.LOG;

    @Inject(method = "playerLoggedIn", at = @At("RETURN"))
    public void AfterPlayerJoined(EntityPlayerMP player, CallbackInfo ci) {
        mcToKook_Mod_1_7_10$LOG.info("[mixin]玩家:" + player.getDisplayName() + "进入了服务器");
        if (!Config.join_Message) {
            return;
        }
        CompletableFuture.runAsync(() -> {

            String playerName = player.getDisplayName();
            String playerUUID = player.getGameProfile()
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

    @Inject(method = "playerLoggedOut", at = @At("RETURN"))
    public void AfterPlayerLeave(EntityPlayerMP player, CallbackInfo ci) {
        mcToKook_Mod_1_7_10$LOG.info("[mixin]玩家:" + player.getDisplayName() + "离开了服务器");
        if (!Config.quit_Message) {
            return;
        }

        CompletableFuture.runAsync(() -> {

            String playerName = player.getDisplayName();
            String playerUUID = player.getGameProfile()
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

    @Unique
    private static MultipleCardComponent buildCard(String playerName, String playerUUID, boolean isQuited) {
        String needFormatMessage = Config.player_Join_Message;
        String imageUrl = PlayerIcon.getPlayerIconUr(playerUUID);
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
            if (playingTime > 0) {
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

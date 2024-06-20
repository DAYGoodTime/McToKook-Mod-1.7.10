package com.day.mctokook.commands.kook;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import org.jetbrains.annotations.Nullable;

import snw.jkook.command.UserCommandExecutor;
import snw.jkook.entity.User;
import snw.jkook.entity.abilities.Accessory;
import snw.jkook.message.Message;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.element.PlainTextElement;
import snw.jkook.message.component.card.module.DividerModule;
import snw.jkook.message.component.card.module.HeaderModule;
import snw.jkook.message.component.card.module.SectionModule;
import snw.kookbc.impl.network.exceptions.BadResponseException;

public class ListCommand implements UserCommandExecutor {

    @Override
    public void onCommand(User user, Object[] objects, @Nullable Message message) {
        try {
            if (message == null) return;
            StringBuilder players = new StringBuilder();
            List<EntityPlayerMP> playerList = MinecraftServer.getServer()
                .getConfigurationManager().playerEntityList;
            playerList.forEach(
                p -> players.append(p.getDisplayName())
                    .append(" "));
            MultipleCardComponent playerListCard = new CardBuilder().setTheme(Theme.SUCCESS)
                .setSize(Size.LG)
                .addModule(new HeaderModule(new PlainTextElement("在线玩家列表: ")))
                .addModule(DividerModule.INSTANCE)
                .addModule(new SectionModule(new MarkdownElement(players.toString()), null, Accessory.Mode.LEFT))
                .build();
            message.reply(playerListCard);
        } catch (BadResponseException e) {
            message.reply("KOOK消息异常:" + e.getLocalizedMessage());
        }
    }
}

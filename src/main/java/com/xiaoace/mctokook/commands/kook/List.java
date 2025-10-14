package com.xiaoace.mctokook.commands.kook;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import snw.jkook.entity.abilities.Accessory;
import snw.jkook.exceptions.BadResponseException;
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
import snw.kookbc.impl.command.litecommands.annotations.prefix.Prefix;

@Command(name = "玩家列表", aliases = { "列表", "list" })
@Prefix("/")
@Description("查询当前服务器在线玩家")
public class List {

    @Execute
    public void list(@Context Message message) {
        try {
            StringBuilder players = new StringBuilder();
            java.util.List<EntityPlayerMP> playerList = MinecraftServer.getServer()
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

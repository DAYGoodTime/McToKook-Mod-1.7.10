package com.day.mctokook.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.day.mctokook.Config;
import com.day.mctokook.McToKook;
import com.day.mctokook.models.ae.AEHelper;
import com.day.mctokook.models.ae.entity.AEItem;
import com.day.mctokook.models.ae.entity.CPUInfo;
import com.day.mctokook.models.ae.entity.CPUInfoResponse;
import com.day.mctokook.utils.NumberFormatter;
import com.day.mctokook.utils.arg.ArgumentsProcessor;

import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import snw.jkook.command.JKookCommand;
import snw.jkook.entity.abilities.Accessory;
import snw.jkook.entity.channel.Channel;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.ImageElement;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.element.PlainTextElement;
import snw.jkook.message.component.card.module.DividerModule;
import snw.jkook.message.component.card.module.HeaderModule;
import snw.jkook.message.component.card.module.SectionModule;
import snw.kookbc.impl.network.exceptions.BadResponseException;

public class KookCommands {

    public JKookCommand list = new JKookCommand("list", "/").setDescription("用法: /list ; 作用: 返回当前服务器内的玩家列表")
        .setHelpContent("用法: /list ; 作用: 返回当前服务器内的玩家列表")
        .executesUser((sender, arguments, message) -> {
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
        });
    public JKookCommand order = new JKookCommand("order", "/").setDescription("尝试向AE发送合成请求")
        .setHelpContent("用法: /order 物品id meta 数量 ; 例如: /order minecraft:stone 0 10")
        .executesUser((sender, arguments, message) -> {
            try {
                if (message == null) return;
                ArgumentsProcessor arg = new ArgumentsProcessor(arguments, message, null, null);
                String item = arg.when(3, 0)
                    .getAsString("物品id错误");
                int meta = arg.when(3, 1)
                    .getAsInt("meta格式错误");
                int count = arg.when(3, 2)
                    .getAsInt("物品数量错误");
                boolean success = AEHelper.OrderItem(item, meta, count);
                if (success) {
                    message.reply("下单成功!");
                } else {
                    message.reply("下单失败!");
                }
            } catch (BadResponseException e) {
                message.reply("KOOK消息异常:" + e.getLocalizedMessage());
            }
        })
        .addAlias("下单");

    public JKookCommand info = new JKookCommand("info", "/").setDescription("查看AE单子")
        .setHelpContent("用法: /info ; 或者 /单子")
        .executesUser((sender, arguments, message) -> {
            MultipleCardComponent card = null;
            try {
                if (message == null) return;
                CPUInfoResponse info = AEHelper.getBusyCPUs();
                Channel channel = McToKook.kbcClient.getCore()
                    .getHttpAPI()
                    .getChannel(Config.channel_ID);
                if (!(channel instanceof TextChannel)) return;
                CardBuilder builder = new CardBuilder().setTheme(Theme.INFO)
                    .setSize(Size.LG)
                    .addModule(new HeaderModule(new PlainTextElement("单子情况: ")))
                    .addModule(DividerModule.INSTANCE)
                    .addModule(
                        new SectionModule(
                            new MarkdownElement(StrUtil.format("**{}** : {}", "当前CPU数量", info.total_cpus)),
                            null,
                            Accessory.Mode.LEFT))
                    .addModule(
                        new SectionModule(
                            new MarkdownElement(StrUtil.format("**{}** : {}", "当前空闲CPU数量", info.idle_cpus)),
                            null,
                            Accessory.Mode.LEFT))
                    .addModule(DividerModule.INSTANCE)
                    .addModule(new HeaderModule(new PlainTextElement("正在合成的物品: ")));
                List<CPUInfo> busy = info.infos.stream()
                    .filter(cpu -> cpu.busy)
                    .collect(Collectors.toList());
                List<String> images = new ArrayList<>();
                List<AEItem> items = new ArrayList<>();
                for (CPUInfo cpuInfo : busy) {
                    AEItem item = cpuInfo.CraftingItem;
                    if (item == null) {
                        item = new AEItem();
                        item.label = "该CPU未放置合成监控器";
                        item.damage = 0;
                        item.size = -1L;
                    }
                    String name = item.label;
                    if (name.contains("液滴") || name.contains("drop of")) name = "___液滴";
                    if (name.contains("/t")) name = name.replaceAll("/", "_");
                    String url = URLEncodeUtil.encode(StrUtil.format(Config.ITEM_API + "{}/{}", item.damage, name));
                    byte[] bytes = HttpUtil.downloadBytes(url);
                    String kookUrl = McToKook.kbcClient.getCore()
                        .getHttpAPI()
                        .uploadFile(name, bytes);
                    images.add(kookUrl);
                    items.add(item);
                }
                for (int i = 0; i < images.size(); i++) {
                    AEItem item = items.get(i);
                    ImageElement image = new ImageElement(images.get(i), item.label, Size.SM, true);
                    builder.addModule(
                        new SectionModule(
                            new MarkdownElement(
                                StrUtil
                                    .format("物品名:{}\n合成数量:{}", item.label, NumberFormatter.format(item.size, null, 2))),
                            image,
                            Accessory.Mode.LEFT));
                    if (i != images.size() - 1) {
                        builder.addModule(DividerModule.INSTANCE);
                    }
                }
                card = builder.build();
                ((TextChannel) channel).sendComponent(card);
            } catch (BadResponseException e) {
                message.reply("KOOK消息异常:" + e.getLocalizedMessage());
                if (card != null) {
                    McToKook.LOG.warn(card.toString());
                }
            }
        })
        .addAlias("单子");
}

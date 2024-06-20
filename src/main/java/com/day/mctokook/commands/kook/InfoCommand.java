package com.day.mctokook.commands.kook;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.jetbrains.annotations.Nullable;

import com.day.mctokook.Config;
import com.day.mctokook.McToKook;
import com.day.mctokook.modules.ae.AEHelper;
import com.day.mctokook.modules.ae.entity.AEItem;
import com.day.mctokook.modules.ae.entity.CPUInfo;
import com.day.mctokook.modules.ae.entity.CPUInfoResponse;
import com.day.mctokook.utils.NumberFormatter;

import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import snw.jkook.command.UserCommandExecutor;
import snw.jkook.entity.User;
import snw.jkook.entity.abilities.Accessory;
import snw.jkook.entity.channel.Channel;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.message.Message;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.BaseElement;
import snw.jkook.message.component.card.element.ImageElement;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.element.PlainTextElement;
import snw.jkook.message.component.card.module.ContextModule;
import snw.jkook.message.component.card.module.DividerModule;
import snw.jkook.message.component.card.module.HeaderModule;
import snw.jkook.message.component.card.module.SectionModule;
import snw.kookbc.impl.network.exceptions.BadResponseException;

public class InfoCommand implements UserCommandExecutor {

    @Override
    public void onCommand(User user, Object[] arguments, @Nullable Message message) {
        MultipleCardComponent card = null;
        try {
            if (message == null) return;
            CPUInfoResponse info = AEHelper.getBusyCPUs();
            if (info == null) {
                message.reply("无法获取到ae信息,请确认lua执行状态和后端状态");
                return;
            }
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
                // 名称转换
                String name = item.label;
                if (name.contains("液滴") || name.contains("drop of")) name = "___液滴";
                if (name.contains("/t")) name = name.replaceAll("/", "_");
                String url = URLEncodeUtil.encode(StrUtil.format(Config.ITEM_API + "{}/{}", item.damage, name));
                byte[] bytes = HttpUtil.downloadBytes(url);
                // 物品图片上传至Kook
                String ItemImageKookUrl = McToKook.kbcClient.getCore()
                    .getHttpAPI()
                    .uploadFile(name, bytes);
                images.add(ItemImageKookUrl);
                // 渲染label为图片
                byte[] LabelImageByte = renderText(name);
                item.labelImage = McToKook.kbcClient.getCore()
                    .getHttpAPI()
                    .uploadFile(item.label, LabelImageByte);
                items.add(item);
            }
            for (int i = 0; i < images.size(); i++) {
                AEItem item = items.get(i);
                ImageElement image = new ImageElement(images.get(i), item.label, Size.SM, false);
                ImageElement ItemLabel = new ImageElement(item.labelImage, item.label, Size.SM, false);
                // builder.addModule(
                // new SectionModule(
                // new MarkdownElement(
                // StrUtil
                // .format("物品名:{}\n合成数量:{}", item.label, NumberFormatter.format(item.size, null, 2))),
                // image,
                // Accessory.Mode.LEFT));
                List<BaseElement> line = new ArrayList<>();
                line.add(image);
                line.add(new PlainTextElement("物品名: "));
                line.add(ItemLabel);
                line.add(new PlainTextElement(StrUtil.format(" 合成数量:{}", NumberFormatter.format(item.size, null, 2))));
                builder.addModule(new ContextModule(line));
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] renderText(String text) throws IOException {
        Font font = new Font("微软雅黑", Font.PLAIN, 24);
        int width = 360;
        int height = 30;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        // 设置透明背景
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, width, height);
        // 设置回原设置
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        int rawWidth = fm.stringWidth(text);
        if (rawWidth + 20 < width) {
            image = new BufferedImage(rawWidth + 10, height, BufferedImage.TYPE_INT_ARGB);
            g2d = image.createGraphics();
            g2d.setFont(font);
            g2d.setColor(Color.WHITE);
            fm = g2d.getFontMetrics();
            // 设置透明背景
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, width, height);
            // 设置回原设置
            g2d.setComposite(AlphaComposite.SrcOver);
        }
        StringBuffer line = new StringBuffer();
        int x = 5;
        int y = 24;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String word = String.valueOf(c);
            if (fm.stringWidth(word + line) < 340) {
                line.append(word);
            } else {
                g2d.drawString(line.toString(), x, y);
                y += fm.getHeight() + 1;
                line = new StringBuffer(word);
            }
        }
        g2d.drawString(line.toString(), x, y);
        g2d.dispose();
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", bos);
            return bos.toByteArray();
        }
    }
}

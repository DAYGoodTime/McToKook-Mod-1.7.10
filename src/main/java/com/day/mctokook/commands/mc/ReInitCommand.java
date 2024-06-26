package com.day.mctokook.commands.mc;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import com.day.mctokook.Config;
import com.day.mctokook.McToKook;
import com.day.mctokook.ServerProxy;

import snw.jkook.JKook;
import snw.jkook.config.file.YamlConfiguration;
import snw.kookbc.impl.CoreImpl;

public class ReInitCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "kinit";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "重新初始化kbc，用于重启bot";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        try {
            YamlConfiguration config = new YamlConfiguration();

            config.set("mode", "websocket");
            CoreImpl core = new CoreImpl();
            JKook.setCore(core);

            // 读取配置拿必要的东西
            String bot_token = Config.bot_token;
            String channel_ID = Config.channel_ID;
            ServerProxy.connectKBC(core, config, bot_token, McToKook.instance, channel_ID);
        } catch (Throwable e) {
            McToKook.LOG.error("kookBC初始化失败:", e);
            throw new WrongUsageException("初始化出现异常:" + e.getMessage());
        }
    }
}

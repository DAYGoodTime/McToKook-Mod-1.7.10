package com.xiaoace.mctokook.commands.kook;

import snw.jkook.plugin.Plugin;
import snw.kookbc.impl.command.litecommands.LiteKookFactory;

public class CommandInit {

    public static void InitKookCommand(Plugin plugin) {
        LiteKookFactory.builder(plugin)
            .commands(new List())
            .build();

    }
}

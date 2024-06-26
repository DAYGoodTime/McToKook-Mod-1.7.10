package com.day.mctokook.commands.kook;

import snw.jkook.plugin.Plugin;
import snw.kookbc.impl.command.litecommands.LiteKookFactory;

public class CommandInit {

    public static void InitKookCommand(Plugin plugin) {
        LiteKookFactory.builder(plugin)
            .commands(new AddMapping())
            .commands(new Dict())
            .commands(new List())
            .commands(new Order())
            .commands(new Sync())
            .commands(new Info())
            .build();

    }
}

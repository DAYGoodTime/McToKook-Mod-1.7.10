package com.xiaoace.mctokook.minixs;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xiaoace.mctokook.McToKook;

@Mixin(ServerConfigurationManager.class)
public class TestMinxEvent {

    @Inject(method = "playerLoggedIn", at = @At("RETURN"))
    public void AfterPlayerJoined(EntityPlayerMP player, CallbackInfo ci) {
        McToKook.LOG.info("[Mixin]:{}加入了游戏", player.getDisplayName());
    }

    @Inject(method = "playerLoggedOut", at = @At("RETURN"))
    public void AfterPlayerLeave(EntityPlayerMP player, CallbackInfo ci) {
        McToKook.LOG.info("[Mixin]:{}离开了游戏", player.getDisplayName());
    }

}

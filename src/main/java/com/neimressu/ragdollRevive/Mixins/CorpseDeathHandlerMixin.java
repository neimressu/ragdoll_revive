package com.neimressu.ragdollRevive.Mixins;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.leo.ragdollcorpse.corpse.CorpseDeathHandler", remap = false)
@Pseudo
public class CorpseDeathHandlerMixin {

    @Inject(method = "onPlayerDeath", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onPlayerDeathCheck(LivingDeathEvent event, CallbackInfo ci) {
        if (event.getEntity() instanceof ServerPlayer player) {
            boolean mustDie = player.getPersistentData().getBoolean("mustDie");
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) return;
            int playerCount = server.getPlayerCount();
            if (playerCount <= 1) {
                return;
            }
            if (!mustDie) {
                ci.cancel();
            }
        }
    }
}
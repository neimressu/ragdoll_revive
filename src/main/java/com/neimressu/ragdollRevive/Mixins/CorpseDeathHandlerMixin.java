package com.neimressu.ragdollRevive.Mixins;

import com.neimressu.ragdollRevive.ReviveManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
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
            MinecraftServer server = event.getEntity().getServer();
            if (server == null) return;

            if (ReviveManager.isLonePlayer(server)) return;

            if (!player.getPersistentData().getBoolean("isDying")) {
                ci.cancel();
            } else {
                player.removeEffect(MobEffects.DARKNESS);
                player.removeEffect(MobEffects.BLINDNESS);
                player.getPersistentData().putBoolean("mustDie", true);
                player.setDeltaMovement(Vec3.ZERO);
                ReviveManager.DYING.remove(player.getUUID());
            }
        }
    }
}
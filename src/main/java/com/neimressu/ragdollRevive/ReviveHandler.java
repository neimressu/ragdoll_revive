package com.neimressu.ragdollRevive;

import dev.leo.sableplayerragdoll.api.RagdollAPI;
import dev.leo.sableplayerragdoll.api.RagdollSession;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

@EventBusSubscriber(modid = RagdollRevive.MODID)
public class ReviveHandler {

    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.RightClickItem event) {

        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        if (player.getPersistentData().getBoolean("isDying")) {
            return;
        }
        String itemId =
                BuiltInRegistries.ITEM
                        .getKey(event.getItemStack().getItem())
                        .toString();

        if (!ReviveManager.isValidReviveItem(itemId))
            return;
        double radius = 4.0;
        List<ServerPlayer> nearbyPlayers = player.level().getEntitiesOfClass(
                ServerPlayer.class,
                player.getBoundingBox().inflate(radius)
        );

        Vec3 lookVec = player.getLookAngle();
        Vec3 eyePos = player.getEyePosition();

        ServerPlayer bestTarget = null;
        double highestDot = -1.0;

        for (ServerPlayer target : nearbyPlayers) {

            if (target == player)
                continue;

            if (!target.getPersistentData().getBoolean("isDying"))
                continue;

            if (!RagdollAPI.isRagdolled(target))
                continue;

            Vec3 toTarget = target.position().subtract(eyePos);
            double length = toTarget.length();

            if (length > 0.1) {
                toTarget = toTarget.normalize();
            } else {
                toTarget = lookVec;
            }
            double dot = lookVec.dot(toTarget);
            if (dot > 0.0 && dot > highestDot) {
                highestDot = dot;
                bestTarget = target;
            }
        }

        if (bestTarget != null) {
            boolean success = ReviveAPI.revivePlayer(bestTarget, player);

            if (success) {
                bestTarget.sendSystemMessage(
                        Component.literal("You have been revived by " + player.getName().getString())
                                .withStyle(ChatFormatting.GREEN)
                );
                player.sendSystemMessage(
                        Component.literal("You revived " + bestTarget.getName().getString())
                                .withStyle(ChatFormatting.GREEN)
                );

                if (!player.isCreative()) {
                    event.getItemStack().shrink(1);
                    player.addItem(event.getItemStack().getCraftingRemainingItem());
                }
            }
        }
    }
}
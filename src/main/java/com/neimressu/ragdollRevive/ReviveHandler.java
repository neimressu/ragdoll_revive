package com.neimressu.ragdollRevive;

import dev.leo.sableplayerragdoll.api.RagdollAPI;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@EventBusSubscriber(modid = RagdollRevive.MODID)
public class ReviveHandler {

    private static final Logger log = LogManager.getLogger(ReviveHandler.class);

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
        boolean isReviveItem = ReviveManager.isValidReviveItem(itemId);
        boolean isExtendItem = ReviveManager.isValidExtendItem(itemId);

        if (!isReviveItem && !isExtendItem) {
            return;
        }
        double reach = 4.0;
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F); // Вектор взгляда
        Vec3 endPos = eyePos.add(lookVec.scale(reach));

        HitResult hitResult = player.level().clip(new net.minecraft.world.level.ClipContext(
                eyePos, endPos,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE,
                player
        ));

        double actualReach = (hitResult.getType() == HitResult.Type.BLOCK) ? hitResult.getLocation().distanceTo(eyePos) : reach;

        List<ServerPlayer> targets = player.level().getEntitiesOfClass(
                ServerPlayer.class,
                new AABB(eyePos, eyePos.add(lookVec.scale(actualReach))).inflate(0.5) // Небольшая область вокруг луча
        );

        ServerPlayer bestTarget = null;
        double minDistance = Double.MAX_VALUE;

        for (ServerPlayer target : targets) {
            if (target == player) continue;
            if (!target.getPersistentData().getBoolean("isDying")) continue;
            if (!RagdollAPI.isRagdolled(target)) continue;

            Vec3 targetPos = target.getEyePosition();
            Vec3 toTarget = targetPos.subtract(eyePos).normalize();

            double dot = lookVec.dot(toTarget);
            if (dot > 0.4) {
                double dist = eyePos.distanceTo(targetPos);
                if (dist < minDistance) {
                    minDistance = dist;
                    bestTarget = target;
                }
            }
        }

        if (bestTarget != null) {
            if (isExtendItem) {
                ReviveAPI.extendReviveTime(bestTarget, ReviveManager.getExtensionTicks(itemId));
                bestTarget.sendSystemMessage(Component.literal("Your revive time has been extended").withStyle(ChatFormatting.YELLOW));
                player.sendSystemMessage(Component.literal("You extended revive time of " + bestTarget.getName().getString() +
                                " by " + ReviveManager.getExtensionTicks(itemId) / 20 +
                                " seconds")
                        .withStyle(ChatFormatting.YELLOW));
                ReviveManager.removeItem(event.getItemStack(),player);
                return;
            }
            ReviveAPI.revivePlayer(bestTarget, player);
            bestTarget.sendSystemMessage(Component.literal("You have been revived by " + player.getName().getString()).withStyle(ChatFormatting.GREEN));
            bestTarget.sendSystemMessage(Component.literal("You revived " + bestTarget.getName().getString()).withStyle(ChatFormatting.GREEN));
            ReviveManager.removeItem(event.getItemStack(),player);
        }
    }
}
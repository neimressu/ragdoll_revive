package com.neimressu.ragdollRevive;

import dev.leo.sableplayerragdoll.api.RagdollAPI;

import dev.leo.sableplayerragdoll.block.entity.RagdollPartBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        HitResult hit = player.pick(5.0D, 0.0F, false);
        if (hit.getType() != HitResult.Type.BLOCK) return;

        BlockHitResult blockHit = (BlockHitResult) hit;
        BlockEntity be = player.level().getBlockEntity(blockHit.getBlockPos());

        if (!(be instanceof RagdollPartBlockEntity ragdollPart)) return;

        var provider = be.getLevel().registryAccess();
        log.info("Block NBT: {}", be.saveWithFullMetadata(provider).getAsString());
        String skinName = ragdollPart.saveWithFullMetadata(provider).getString("SkinName");
        log.debug(skinName);

        ServerPlayer target = player.server.getPlayerList().getPlayerByName(skinName);

        if (target != null && target.getPersistentData().getBoolean("isDying")) {
            if (isExtendItem) {
                ReviveAPI.extendReviveTime(target, ReviveManager.getExtensionTicks(itemId));
                target.sendSystemMessage(Component.literal("Your revive time has been extended").withStyle(ChatFormatting.YELLOW));
                player.sendSystemMessage(Component.literal("You extended revive time of " + target.getName().getString() +
                                " by " + ReviveManager.getExtensionTicks(itemId) / 20 +
                                " seconds")
                        .withStyle(ChatFormatting.YELLOW));
                ReviveManager.removeItem(event.getItemStack(),player);
                return;
            }
            ReviveAPI.revivePlayer(target, player);
            target.sendSystemMessage(Component.literal("You have been revived by " + player.getName().getString()).withStyle(ChatFormatting.GREEN));
            target.sendSystemMessage(Component.literal("You revived " + target.getName().getString()).withStyle(ChatFormatting.GREEN));
            ReviveManager.removeItem(event.getItemStack(),player);
        }
    }
}
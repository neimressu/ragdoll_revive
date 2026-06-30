package com.neimressu.ragdollRevive;

import com.neimressu.ragdollRevive.Network.TimerPayLoad;
import dev.leo.sableplayerragdoll.block.entity.RagdollPartBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber(modid = RagdollRevive.MODID)
public class ReviveHandler {

    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.RightClickItem event) {

        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
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

        assert be.getLevel() != null;
        var provider = be.getLevel().registryAccess();
        String skinName = ragdollPart.saveWithFullMetadata(provider).getString("SkinName");

        ServerPlayer target = player.server.getPlayerList().getPlayerByName(skinName);

        assert target != null;
        if (player.distanceTo(target)>5) { return; }

        if (target.getPersistentData().getBoolean("isDying")) {
            if (!isExtendItem) {
                ReviveAPI.revivePlayer(target, player);
                target.sendSystemMessage(Component.translatable("text.player.target.revived",player.getName().getString()).withStyle(ChatFormatting.GREEN));
                player.sendSystemMessage(Component.translatable("text.player.reviver.revived", target.getName().getString()).withStyle(ChatFormatting.GREEN));
                ReviveManager.removeItem(event.getItemStack(),player, player.getUsedItemHand());
                ReviveManager.playReviveSound(target);
                event.setCanceled(true);
                return;
            }
            ReviveAPI.extendReviveTime(target, ReviveManager.getExtensionTicks(itemId));
            target.sendSystemMessage(Component.translatable("text.player.target.extended").withStyle(ChatFormatting.YELLOW));
            player.sendSystemMessage(Component.translatable("text.player.revived.extended",
                            target.getName().getString(),
                            ReviveManager.getExtensionTicks(itemId) / 20
                    ).withStyle(ChatFormatting.YELLOW));
            ReviveManager.removeItem(event.getItemStack(),player, player.getUsedItemHand());
            PacketDistributor.sendToPlayer(target, new TimerPayLoad(ReviveManager.getExtensionTicks(itemId),3));
            event.setCanceled(true);
        }
    }
}
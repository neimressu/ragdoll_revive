package com.neimressu.ragdollRevive;

import dev.leo.sableplayerragdoll.api.RagdollSession;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ReviveManager {

    public static final Map<UUID, DyingPlayer> DYING = new HashMap<>();
    public static final Map<UUID, GivingUp> GIVINGUP = new HashMap<>();
    private static final Logger log = LogManager.getLogger(ReviveManager.class);

    public record DyingPlayer(
            UUID uuid,
            int expireTick,
            RagdollSession session,
            DamageSource damageSource
    ) {}

    public record GivingUp(
            UUID uuid,
            int givingTick,
            boolean oldKeyState
    ) {}

    public static int getReviveTime() {
        return CommonConfig.REVIVE_TIME.get();
    }

    public static boolean isValidReviveItem(String itemId) {
        return CommonConfig.REVIVE_ITEMS.get().contains(itemId);
    }

    public static boolean isValidExtendItem(String itemId) {
        for (String entry : CommonConfig.EXTEND_ITEMS.get()) {
            String[] parts = entry.split(":");
            String itemInConfig = parts[0] + ":" + parts[1];
            if (itemInConfig.equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRagdollCorpseEnabled() {
        return ModList.get().isLoaded("ragdoll_corpse");
    }

    public static boolean isExcludedDamage(String damageType) {
        return CommonConfig.EXCLUDED_DAMAGE.get().contains(damageType);
    }

    public static boolean isInvulnerableInCritState() {
        return CommonConfig.INVULNERABLE_IN_CRIT_STATE.getAsBoolean();
    }

    public static float getHealthAfterRevive() { return CommonConfig.HEALTH_AFTER_REVIVE.get().floatValue(); }

    public static int getExtensionTicks(String itemId) {
        for (String entry : CommonConfig.EXTEND_ITEMS.get()) {
            String[] parts = entry.split(":");
            String entryItemId = parts[0] + ":" + parts[1];

            if (entryItemId.equals(itemId)) {
                try {
                    return Integer.parseInt(parts[2]);
                } catch (Exception e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    public static void removeItem(ItemStack item, ServerPlayer player, InteractionHand hand) {
        if (player.isCreative()) { return; }
        ItemStack remainder = item.getCraftingRemainingItem();
        if (item.getMaxDamage()>1) {
            log.debug("{}:{}", item.getDamageValue(), item.getMaxDamage());
            item.hurtAndBreak(
                    1,
                    player,
                    LivingEntity.getSlotForHand(hand)
            );
        } else item.shrink(1);
        if (!remainder.isEmpty()) {
            player.addItem(remainder);
        }
    }

    public static void playReviveSound(ServerPlayer player) {
        if (CommonConfig.REVIVE_SOUND.get().equalsIgnoreCase("DISABLE")) return;
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT
                .get(ResourceLocation.parse(CommonConfig.REVIVE_SOUND.get()));

        if (sound == null) return;
        player.playNotifySound(
                sound,
                SoundSource.PLAYERS,
                0.7f,
                1.0f
        );
    }

    public static boolean isLonePlayer(MinecraftServer server) {
        if (!CommonConfig.CAN_BE_REVIVED_IN_SP.getAsBoolean()) {
            return server.getPlayerCount() <= 1;
        } else return false;
    }
}
package com.neimressu.ragdollRevive;

import dev.leo.sableplayerragdoll.api.RagdollSession;
import net.minecraft.world.damagesource.DamageSource;

import java.util.*;

public final class ReviveManager {

    public static final Map<UUID, DyingPlayer> DYING = new HashMap<>();

    public record DyingPlayer(
            UUID uuid,
            long expireTick,
            RagdollSession session,
            DamageSource damageSource
    ) {}

    // Удобные методы-геттеры для конфига
    public static int getReviveTime() {
        return Config.REVIVE_TIME.get();
    }

    public static boolean isValidReviveItem(String itemId) {
        return Config.REVIVE_ITEMS.get().contains(itemId);
    }

    public static boolean isExcludedDamage(String damageType) {
        return Config.EXCLUDED_DAMAGE.get().contains(damageType);
    }

    public static float getHealthAfterRevive() { return Config.HEALTH_AFTER_REVIVE.get().floatValue();}
}
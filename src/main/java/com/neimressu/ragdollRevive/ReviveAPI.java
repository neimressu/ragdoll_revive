package com.neimressu.ragdollRevive;

import dev.leo.sableplayerragdoll.api.RagdollAPI;
import dev.leo.sableplayerragdoll.api.RagdollSession;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public final class ReviveAPI {

    /**
     * A method for programmatically reviving a player. It can be called from anywhere, including commands or other mods.
     * * @param target The player to be revived.
     * @param reviver The player performing the revival (set to null if the system or a command is performing the revival).
     * @return true if the player was indeed dead and was successfully revived; false otherwise.
     */
    public static boolean revivePlayer(ServerPlayer target, @Nullable ServerPlayer reviver) {
        if (!target.getPersistentData().getBoolean("isDying")) {
            return false;
        }
        target.removeEffect(MobEffects.DARKNESS);
        target.removeEffect(MobEffects.BLINDNESS);

        target.setInvulnerable(false);
        target.getPersistentData().putBoolean("isDying", false);
        target.getPersistentData().putBoolean("mustDie", false);

        if (RagdollAPI.isRagdolled(target)) {
            RagdollSession session = RagdollAPI.activeSession(target);
            if (session != null) {
                session.setDismountLocked(false);
                session.release();
            }
        }

        ReviveManager.DYING.remove(target.getUUID());

        target.setHealth(ReviveManager.getHealthAfterRevive());

        NeoForge.EVENT_BUS.post(new PlayerRevivedEvent(target, reviver));
        return true;
    }
}
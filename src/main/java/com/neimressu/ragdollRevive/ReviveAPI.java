package com.neimressu.ragdollRevive;

import com.neimressu.ragdollRevive.Network.TimerPayLoad;
import dev.leo.sableplayerragdoll.api.RagdollAPI;
import dev.leo.sableplayerragdoll.api.RagdollSession;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class ReviveAPI {

    /**
     * A method for programmatically reviving a player. It can be called from anywhere, including commands or other mods.
     * * @param target The player to be revived.
     *
     * @param reviver The player performing the revival (set to null if the system or a command is performing the revival).
     */
    public static void revivePlayer(ServerPlayer target, @Nullable ServerPlayer reviver) {
        if (!target.getPersistentData().getBoolean("isDying")) {
            return;
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
        ReviveManager.GIVINGUP.remove(
                target.getUUID()
        );
        PacketDistributor.sendToPlayer(target, new TimerPayLoad(0,1));
        target.setHealth(ReviveManager.getHealthAfterRevive());

        NeoForge.EVENT_BUS.post(new PlayerRevivedEvent(target, reviver));
    }

    public static void extendReviveTime(ServerPlayer target, int ticks) {
        if (!target.getPersistentData().getBoolean("isDying")) {
            return;
        }
        ReviveManager.DYING.computeIfPresent(target.getUUID(), (k, data) -> new ReviveManager.DyingPlayer(
                data.uuid(),
                data.expireTick() + ticks,
                data.session(),
                data.damageSource()
        ));
        PacketDistributor.sendToPlayer(target, new TimerPayLoad(ticks,3));
    }

    public static void killDyingPlayer(ServerPlayer target) {
        if (!target.getPersistentData().getBoolean("isDying")) {
            return;
        }

        target.getPersistentData()
                .putBoolean("mustDie", true);

        target.setInvulnerable(false);

        target.kill();
        target.setDeltaMovement(Vec3.ZERO);

        ReviveManager.DYING.remove(
                target.getUUID()
        );
        ReviveManager.GIVINGUP.remove(
                target.getUUID()
        );
        PacketDistributor.sendToPlayer(target, new TimerPayLoad(0,1));
    }
}
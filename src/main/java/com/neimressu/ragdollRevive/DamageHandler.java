package com.neimressu.ragdollRevive;

import com.neimressu.ragdollRevive.Network.TimerPayLoad;
import dev.leo.sableplayerragdoll.api.RagdollAPI;
import dev.leo.sableplayerragdoll.api.RagdollSession;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber(modid = RagdollRevive.MODID)
public class DamageHandler {

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        MinecraftServer server = event.getEntity().getServer();
        if (server == null) return;
        if (ReviveManager.isLonePlayer(server)) return;

        if (player.getPersistentData().getBoolean("isDying")) return;

        if (player.getPersistentData().getBoolean("mustDie")) {
            player.getPersistentData().putBoolean("mustDie", false);
            player.getPersistentData().putBoolean("isDying", false);
            return;
        }

        if (!ReviveManager.isInvulnerableInCritState() && player.getPersistentData().getBoolean("isDying")) {
            RagdollSession session = RagdollAPI.activeSession(player);
            if (session == null) return;
            session.setDismountLocked(false);
            session.release();
            player.removeEffect(MobEffects.DARKNESS);
            player.removeEffect(MobEffects.BLINDNESS);
            player.getPersistentData().putBoolean("mustDie", true);
            player.setDeltaMovement(Vec3.ZERO);
            ReviveManager.DYING.remove(player.getUUID());
            ReviveManager.GIVINGUP.remove(player.getUUID());
            PacketDistributor.sendToPlayer(player, new TimerPayLoad(0,1));
            return;
        }
        if (player.getPersistentData().getBoolean("isDying") && ReviveManager.isInvulnerableInCritState()) {
            return;
        }

        boolean isCanceled = NeoForge.EVENT_BUS.post(new PlayerCritStateEvent(player)).isCanceled();

        if (isCanceled) {
            return;
        }

        DamageSource source = event.getSource();
        String damageType = source.typeHolder().getRegisteredName();
        if (ReviveManager.isExcludedDamage(damageType))
            return;
        RagdollSession session;
        if (!RagdollAPI.isRagdolled(player)) {
            session = RagdollAPI.launch(
                    player,
                    player.getDeltaMovement().scale(15)
            );
        } else {
            session = RagdollAPI.activeSession(player);
        }
        if (session == null) return;
        session.setDismountLocked(true);

        if (ReviveManager.isInvulnerableInCritState()) {
            player.setInvulnerable(true);
        }
        player.setHealth(6.0F);
        player.getPersistentData().putBoolean("isDying", true);

        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, Integer.MAX_VALUE, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, Integer.MAX_VALUE, 0, false, false));

        ReviveManager.DYING.put(
                player.getUUID(),
                new ReviveManager.DyingPlayer(
                        player.getUUID(),
                        (int) (player.serverLevel().getGameTime() + ReviveManager.getReviveTime()),
                        session,
                        source
                )
        );
        ReviveManager.GIVINGUP.put(
                player.getUUID(),
                new ReviveManager.GivingUp(
                        player.getUUID(),
                        CommonConfig.GIVE_UP_TIMER.get(),
                        false
                )
        );

        player.sendSystemMessage(Component.translatable("text.player.is_dying",
                ReviveManager.getReviveTime() / 20
                ).withStyle(ChatFormatting.RED)
        );
        if (CommonConfig.CAN_PLAYER_GIVE_UP.getAsBoolean()) {
            player.sendSystemMessage(
                    Component.translatable("text.player.can_give_up",
                            Component.translatable("key.sneak"),
                            CommonConfig.GIVE_UP_TIMER.getAsInt()/20
                    ).withStyle(ChatFormatting.RED)
            );
        }
        PacketDistributor.sendToPlayer(player, new TimerPayLoad(ReviveManager.getReviveTime(),1));
        event.setCanceled(true);
    }
}
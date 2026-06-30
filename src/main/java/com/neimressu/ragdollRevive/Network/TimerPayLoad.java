package com.neimressu.ragdollRevive.Network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.neimressu.ragdollRevive.RagdollRevive.MODID;

public record TimerPayLoad(int timerLeft, int timerType) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TimerPayLoad> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "timer_left"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TimerPayLoad> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    TimerPayLoad::timerLeft,
                    ByteBufCodecs.INT,
                    TimerPayLoad::timerType,
                    TimerPayLoad::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
package com.neimressu.ragdollRevive.Network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import static com.neimressu.ragdollRevive.RagdollRevive.MODID;

public record GiveUpPayload(boolean down) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GiveUpPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "give_up_key"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GiveUpPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    GiveUpPayload::down,
                    GiveUpPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
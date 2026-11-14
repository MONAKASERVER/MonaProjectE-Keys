package net.monacraft.projecte.client;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.nio.charset.StandardCharsets;

public record AbilityPayload(String action) implements CustomPacketPayload {
    public static final Type<AbilityPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("monaprojecte", "input"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityPayload> CODEC = StreamCodec.of(
            (buffer, payload) -> buffer.writeBytes(payload.action.getBytes(StandardCharsets.US_ASCII)),
            buffer -> {
                int length = buffer.readableBytes();
                if (length <= 0 || length > 32) throw new IllegalArgumentException("Invalid MonaProjectE action payload length: " + length);
                byte[] data = new byte[length];
                buffer.readBytes(data);
                return new AbilityPayload(new String(data, StandardCharsets.US_ASCII));
            }
    );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}

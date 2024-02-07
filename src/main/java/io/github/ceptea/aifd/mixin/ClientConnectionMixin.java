package io.github.ceptea.aifd.mixin;

import io.github.ceptea.aifd.Core;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "handlePacket", cancellable = true, at = @At("HEAD"))
    private static void handle(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        if (packet instanceof WorldTimeUpdateS2CPacket) {
            Core.server_response = System.currentTimeMillis();
        }
    }
}

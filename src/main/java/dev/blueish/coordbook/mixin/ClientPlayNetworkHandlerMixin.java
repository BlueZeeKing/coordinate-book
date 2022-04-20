package dev.blueish.coordbook.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;

import dev.blueish.coordbook.CoordinateBook;
import dev.blueish.coordbook.data.Book;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
  @Inject(method = "onGameJoin", at = @At("TAIL"))
  public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
    CoordinateBook.book = new Book(CoordinateBook.client);
  }
}

package dev.blueish.coordbook.mixin;

import dev.blueish.coordbook.util.TextCreator;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import dev.blueish.coordbook.CoordinateBook;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public class ChatMixin {
  @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
  public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
    Matcher sender = Pattern.compile("(<.*?> )").matcher(packet.getMessage().getString());
    String text = sender.replaceFirst("");

    if (text.startsWith("Coordinate Book: ")) {
      text = text.replaceFirst("Coordinate Book: ", "");

      Matcher wName = Pattern.compile("(.*) - ([0-9,\\-]*)/([0-9,\\-]*)/([0-9,\\-]*)").matcher(text);
      Matcher woName = Pattern.compile("([0-9,\\-]*)/([0-9,\\-]*)/([0-9,\\-]*)").matcher(text);

      if (wName.matches()) {
        NetworkThreadUtils.forceMainThread(packet, ((ClientPlayNetworkHandler)(Object)this), CoordinateBook.client);
        CoordinateBook.client.inGameHud.addChatMessage(packet.getType(), new TextCreator(sender.group(1)).addNoFormat(new TextCreator("Coordinate Book: ").format(Formatting.BOLD).addNoFormat(wName.group(1)).filler("-").addNoFormat(wName.group(2)+"/"+wName.group(3)+"/"+wName.group(4))).hover("Click to add").send(String.format("/coordbook %d %d %d %s", Integer.parseInt(wName.group(2)), Integer.parseInt(wName.group(3)), Integer.parseInt(wName.group(4)), wName.group(1))).raw(), packet.getSender());
        ci.cancel();
      } else if (woName.matches()) {
        NetworkThreadUtils.forceMainThread(packet, ((ClientPlayNetworkHandler)(Object)this), CoordinateBook.client);
        CoordinateBook.client.inGameHud.addChatMessage(packet.getType(), new TextCreator(sender.group(1)).addNoFormat(new TextCreator("Coordinate Book: ").format(Formatting.BOLD).addNoFormat(woName.group(1)+"/"+woName.group(2)+"/"+woName.group(3))).hover("Click to add").send(String.format("/coordbook %d %d %d %s", Integer.parseInt(woName.group(1)), Integer.parseInt(woName.group(2)), Integer.parseInt(woName.group(3)), "coordinatebook-empty-this-is-too-long")).raw(), packet.getSender());
        ci.cancel();
      }
    }
  }
}

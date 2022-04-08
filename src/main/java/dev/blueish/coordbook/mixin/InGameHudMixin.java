package dev.blueish.coordbook.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;

import dev.blueish.coordbook.CoordinateBook;
import dev.blueish.coordbook.util.TextCreator;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import java.util.UUID;

@Mixin(InGameHud.class)
public class InGameHudMixin {
  @ModifyConstant(method = "addChatMessage", constant = @Constant(intValue = 4))
  public Text addChatMessage(Text message) {
    String text = message.getString().replaceFirst("<.*?> ", "");
    if (text.startsWith("Coordinate Book:")) {
      return new TextCreator("new text").raw();
    }
    return message;
  }
}

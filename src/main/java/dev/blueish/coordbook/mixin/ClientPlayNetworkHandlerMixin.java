package dev.blueish.coordbook.mixin;

import dev.blueish.coordbook.util.TextCreator;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import dev.blueish.coordbook.CoordinateBook;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import dev.blueish.coordbook.data.Book;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
  @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
  public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
    Matcher sender = Pattern.compile("(.*)Coordinate Book: ").matcher(packet.getMessage().getString());
    String text = sender.replaceFirst("");
    Matcher anyCoords = Pattern.compile("(.*?)(-?\\d+)[ \\-/]+?(-?\\d+)[ \\-/]+?(-?\\d+)(.*)").matcher(packet.getMessage().getString());

    sender.reset();

    if (sender.find()) {
      Matcher wName = Pattern.compile("(.*) - (-?\\d+)/(-?\\d+)/(-?\\d+)").matcher(text);
      Matcher woName = Pattern.compile("(-?\\d+)/(-?\\d+)/(-?\\d+)").matcher(text);

      if (wName.matches()) {
        NetworkThreadUtils.forceMainThread(packet, ((ClientPlayNetworkHandler) (Object) this), CoordinateBook.client);
        CoordinateBook.client.inGameHud.addChatMessage(packet.getType(),
            new TextCreator(sender.group(1)).style(packet.getMessage().getStyle())
                .addNoFormat(new TextCreator("Coordinate Book: ").format(Formatting.BOLD).addNoFormat(wName.group(1))
                    .filler("-").addNoFormat(wName.group(2) + "/" + wName.group(3) + "/" + wName.group(4)))
                .hover("Click to add")
                .send(String.format(
                    "/coordbook %d %d %d %s",
                    Integer.parseInt(wName.group(2)),
                    Integer.parseInt(wName.group(3)),
                    Integer.parseInt(wName.group(4)),
                    wName.group(1)
                  )
                )
                .raw(),
            packet.getSender());
        ci.cancel();
      } else if (woName.matches()) {
        NetworkThreadUtils.forceMainThread(packet, ((ClientPlayNetworkHandler) (Object) this), CoordinateBook.client);
        CoordinateBook.client.inGameHud
            .addChatMessage(packet.getType(),
                new TextCreator(sender.group(1)).style(packet.getMessage().getStyle())
                    .addNoFormat(new TextCreator("Coordinate Book: ").format(Formatting.BOLD)
                    .addNoFormat(woName.group(1) + "/" + woName.group(2) + "/" + woName.group(3)))
                    .hover("Click to add")
                    .send(
                      String.format(
                        "/coordbook %d %d %d %s",
                        Integer.parseInt(woName.group(1)),
                        Integer.parseInt(woName.group(2)),
                        Integer.parseInt(woName.group(3)),
                        "coordinatebook-empty-this-is-too-long"
                      )
                    )
                    .raw(),
                packet.getSender());
        ci.cancel();
      }
    } else if (anyCoords.find()) {
      NetworkThreadUtils.forceMainThread(packet, ((ClientPlayNetworkHandler) (Object) this), CoordinateBook.client);
      CoordinateBook.client.inGameHud.addChatMessage(packet.getType(),
          new TextCreator(anyCoords.group(1)).style(packet.getMessage().getStyle())
            .addNoFormat(
              new TextCreator(String.format("%s/%s/%s", anyCoords.group(2), anyCoords.group(3), anyCoords.group(4)))
                .format(Formatting.AQUA)
                .format(Formatting.BOLD)
                .hover("Click to add")
                  .send(
                    String.format(
                      "/coordbook %d %d %d %s",
                      Integer.parseInt(anyCoords.group(2)),
                      Integer.parseInt(anyCoords.group(3)),
                      Integer.parseInt(anyCoords.group(4)),
                      "coordinatebook-empty-this-is-too-long"
                    )
                  )
            )
            .addNoFormat(anyCoords.group(5))
            .raw(),
          packet.getSender());
      ci.cancel();
    }
  }

  @Inject(method = "onGameJoin", at = @At("TAIL"))
  public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
    CoordinateBook.book = new Book(CoordinateBook.client);
  }
}

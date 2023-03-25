package dev.blueish.coordbook.mixin;

import dev.blueish.coordbook.CoordinateBook;
import dev.blueish.coordbook.data.Book;
import dev.blueish.coordbook.util.Config;
import dev.blueish.coordbook.util.TextCreator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;onChatMessage(Lnet/minecraft/network/message/SignedMessage;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/message/MessageType$Parameters;)V"), cancellable = true)
    public void handleMessage(ChatMessageS2CPacket packet, CallbackInfo ci) {
        Text unsignedContent = packet.unsignedContent();
        Matcher initial = Pattern.compile("(.*)Coordinate Book: ").matcher(unsignedContent.getString());
        String text = initial.replaceFirst("");
        Matcher anyCoords = Pattern.compile("(.*?)(-?\\d+)[ \\-/]+?(-?\\d+)[ \\-/]+?(-?\\d+)(.*)").matcher(unsignedContent.getString());

        initial.reset();

        TextCreator newMessage = null;

        if (initial.find() && Config.chatReplace) {
            Matcher wName = Pattern.compile("(.*) - (-?\\d+)/(-?\\d+)/(-?\\d+)").matcher(text);
            Matcher woName = Pattern.compile("(-?\\d+)/(-?\\d+)/(-?\\d+)").matcher(text);

            if (wName.matches()) {
                newMessage = new TextCreator(initial.group(1)).style(unsignedContent.getStyle())
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
                    );
            } else if (woName.matches()) {
                newMessage = new TextCreator(initial.group(1)).style(unsignedContent.getStyle())
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
                    );
            }
        } else if (anyCoords.find() && Config.allChatReplace) {
            newMessage = new TextCreator(anyCoords.group(1)).style(unsignedContent.getStyle())
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
                .addNoFormat(anyCoords.group(5));
        }

        if (newMessage != null) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(newMessage.raw());
            ci.cancel();
        }
    }

    @Inject(method = "onGameJoin", at = @At("TAIL"))
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        CoordinateBook.book = new Book();
    }
}

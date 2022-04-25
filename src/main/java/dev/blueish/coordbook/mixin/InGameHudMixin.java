package dev.blueish.coordbook.mixin;

import dev.blueish.coordbook.gui.CoordOverlay;
import dev.blueish.coordbook.util.TextCreator;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import dev.blueish.coordbook.CoordinateBook;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import dev.blueish.coordbook.data.Book;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    public CoordOverlay overlay = new CoordOverlay();

    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (CoordinateBook.lastPage > 0) {
            overlay.render(matrices);
        }
    }
}

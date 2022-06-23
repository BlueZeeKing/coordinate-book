package dev.blueish.coordbook.mixin;

import dev.blueish.coordbook.CoordinateBook;
import dev.blueish.coordbook.gui.CoordOverlay;
import dev.blueish.coordbook.util.Config;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    public CoordOverlay overlay = new CoordOverlay();

    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (CoordinateBook.lastPage > 0 && Config.overlay) {
            overlay.render(matrices);
        }
    }
}

package dev.blueish.coordbook.gui;

import dev.blueish.coordbook.CoordinateBook;
import dev.blueish.coordbook.data.Coord;
import dev.blueish.coordbook.util.TextCreator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CoordOverlay extends DrawableHelper {
    public String lastName = "this-text-is-far-too-long-and-is-impossible";
    public Text cachedText;

    public void render(MatrixStack matrices) {
        Coord coord = CoordinateBook.book.coordList.get(CoordinateBook.lastPage - CoordinateBook.book.listPageCount);
        if (!coord.name.equals(lastName)) {
            lastName = coord.name;
            cachedText = new TextCreator(coord.name).format(coord.color == Formatting.BLACK ? Formatting.WHITE : coord.color).format(Formatting.BOLD).addNoFormat(new TextCreator(String.format(": %d/%d/%d", coord.coords.x, coord.coords.y, coord.coords.z)).format(Formatting.WHITE)).raw();
        }
        MinecraftClient.getInstance().textRenderer.draw(matrices, cachedText, 10, 10, Formatting.WHITE.getColorValue());
    }
}

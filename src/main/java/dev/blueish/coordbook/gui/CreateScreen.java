package dev.blueish.coordbook.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import dev.blueish.coordbook.util.TextCreator;
import dev.blueish.coordbook.data.Position;

import net.minecraft.client.gui.widget.TextFieldWidget;

@Environment(value=EnvType.CLIENT)
public class CreateScreen extends BookScreen {
    private String name = "";
    private final Position pos;

    private TextFieldWidget nameEntry;
    private TextFieldWidget colorEntry;

    public CreateScreen(Position pos) {
        super();
        this.pos = pos;
    }

    public CreateScreen(Position pos, String name) {
        super();
        this.name = name;
        this.pos = pos;
    }

    @Override
    protected void init() {
        addWidgets();
        super.init();
    }

    private void addWidgets() {
        int bookXPos = (this.width - WIDTH)/2;
        this.nameEntry = this.addDrawableChild(new TextFieldWidget(this.textRenderer, bookXPos + 35, 60, 100, 10, new LiteralText("Name")));
        this.colorEntry = this.addDrawableChild(new TextFieldWidget(this.textRenderer, bookXPos + 35, 87, 100, 10, new LiteralText("Name")));
        this.nameEntry.setText(name);
        //this.nameEntry.setDrawsBackground(false);
        //this.colorEntry.setDrawsBackground(false);
    }

    @Override
    public StringVisitable getPage(int index) {
        return new TextCreator("Coords")
                .format(Formatting.BOLD)
                .addNewline(
                        new TextCreator(String.format("%d/%d/%d", pos.x, pos.y, pos.z))
                ).addNewline(
                        new TextCreator("Name\n").format(Formatting.BOLD)
                ).add("\n").addNewline(
                        new TextCreator("Color\n").format(Formatting.BOLD)
                )
                .raw();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        //fill(matrices, this.colorEntry.x - 1, this.colorEntry.y - 2, this.colorEntry.x + this.colorEntry.getWidth() + 1, this.colorEntry.y + this.colorEntry.getHeight() + 1, -16777216);
        //fill(matrices, this.colorEntry.x, this.colorEntry.y - 1, this.colorEntry.x + this.colorEntry.getWidth(), this.colorEntry.y + this.colorEntry.getHeight(), -1298);
        //this.colorEntry.render(matrices, mouseX, mouseY, delta);

        //fill(matrices, this.nameEntry.x - 1, this.nameEntry.y - 2, this.nameEntry.x + this.nameEntry.getWidth() + 1, this.nameEntry.y + this.nameEntry.getHeight() + 1, -16777216);
        //fill(matrices, this.nameEntry.x, this.nameEntry.y - 1, this.nameEntry.x + this.nameEntry.getWidth(), this.nameEntry.y + this.nameEntry.getHeight(), -1298);
        //this.nameEntry.render(matrices, mouseX, mouseY, delta);
    }
}

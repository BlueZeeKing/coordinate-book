package dev.blueish.coordbook.gui;

import dev.blueish.coordbook.CoordinateBook;
import dev.blueish.coordbook.data.Coord;
import dev.blueish.coordbook.data.Position;
import dev.blueish.coordbook.util.TextCreator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(value = EnvType.CLIENT)
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
        int bookXPos = (this.width - WIDTH) / 2;
        this.nameEntry = this.addDrawableChild(new TextFieldWidget(this.textRenderer, bookXPos + 35, 60, 100, 10, Text.of("Name")));
        this.colorEntry = this.addDrawableChild(new TextFieldWidget(this.textRenderer, bookXPos + 35, 87, 100, 10, Text.of("Color")));
        this.nameEntry.setText(name);
    }

    @Override
    protected void addCloseButton() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 105, 196, 100, 20, ScreenTexts.CANCEL, (button) -> { this.client.setScreen(null); }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, 196, 100, 20, ScreenTexts.DONE, (button) -> { this.client.setScreen(null); if (nameEntry.getText() != "") { CoordinateBook.book.add(new Coord(pos, nameEntry.getText(), Formatting.byName(colorEntry.getText())  == null ? Formatting.BLACK : Formatting.byName(colorEntry.getText()).isColor() ? Formatting.byName(colorEntry.getText()) : Formatting.BLACK, this.client.player.getWorld().getRegistryKey().getValue().toString())); } }));
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

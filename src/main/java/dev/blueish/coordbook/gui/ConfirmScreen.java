package dev.blueish.coordbook.gui;

import dev.blueish.coordbook.data.Book;
import dev.blueish.coordbook.util.TextCreator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Formatting;

@Environment(value = EnvType.CLIENT)
public class ConfirmScreen extends BookScreen {
    private final int index;
    private final Book book;
    private final int returnIndex;

    public ConfirmScreen(int index, Book book, int returnIndex) {
        super();
        this.index = index;
        this.book = book;
        this.returnIndex = returnIndex;
    }

    @Override
    protected void init() {
        this.addDrawableChild(
            ButtonWidget.builder(new TextCreator("Cancel").format(Formatting.GRAY).raw(), (button) -> this.client.setScreen(new ListScreen(book, index)))
                .position(this.width / 2 - 60, 60).size(50, 20).build());
        this.addDrawableChild(
            ButtonWidget.builder(new TextCreator("Confirm").format(Formatting.RED).raw(), (button) -> { book.delete(index); this.client.setScreen(new ListScreen(book, returnIndex)); })
                .position(this.width / 2, 60).size(50, 20).build());
        this.addCloseButton();
    }

    protected void addCloseButton() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.client.setScreen(new ListScreen(book, index)))
            .position(this.width / 2 - 100, 196).size(200, 20).build());
    }

    @Override
    public StringVisitable getPage(int index) {
        return new TextCreator("Are you absolutely sure?")
                .format(Formatting.BOLD)
                .format(Formatting.RED)
                .raw();
    }
}

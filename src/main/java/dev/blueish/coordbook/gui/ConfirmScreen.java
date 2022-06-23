package dev.blueish.coordbook.gui;

import dev.blueish.coordbook.data.Book;
import dev.blueish.coordbook.util.TextCreator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.*;
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
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 60, 60, 50, 20, new TextCreator("Cancel").format(Formatting.GRAY).raw(), (button) -> this.client.setScreen(new ListScreen(book, index))));
        this.addDrawableChild(new ButtonWidget(this.width / 2, 60, 50, 20, new TextCreator("Confirm").format(Formatting.RED).raw(), (button) -> {
            book.delete(index);
            this.client.setScreen(new ListScreen(book, returnIndex));
        }));
        this.addCloseButton();
    }

    protected void addCloseButton() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, 196, 200, 20, ScreenTexts.DONE, (button) -> this.client.setScreen(new ListScreen(book, index))));
    }

    @Override
    public StringVisitable getPage(int index) {
        return new TextCreator("Are you absolutely sure?")
                .format(Formatting.BOLD)
                .format(Formatting.RED)
                .raw();
    }
}

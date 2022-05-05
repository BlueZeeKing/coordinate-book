package dev.blueish.coordbook.gui;

import dev.blueish.coordbook.data.Book;
import dev.blueish.coordbook.util.TextCreator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class ListScreen extends BookScreen {
    private final Book contents;
    private int lastListPage = 0;
    private ButtonWidget deleteButton;

    public ListScreen(Book pageProvider, int startPage) {
        super();
        this.contents = pageProvider;
        this.pageIndex = startPage;
    }

    @Override
    protected void init() {
        addDeleteButton();
        super.init();
    }

    @Override
    public void setPage(int index) {
        if (this.pageIndex >= getPageCount()) {
            super.setPage(lastListPage);
        } else {
            if (index < getPageCount()) {
                lastListPage = index;
            }
            super.setPage(index);
        }
        updateDeleteButton();
    }

    @Override
    public int getPageCount() {
        return this.contents.listPageCount;
    }

    @Override
    public StringVisitable getPage(int index) {
        return this.contents.getPage(index);
    }

    private void addDeleteButton() {
        this.deleteButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 55, 135, 100, 20, new TextCreator("DELETE").format(Formatting.BOLD).format(Formatting.RED).raw(), button -> { this.client.setScreen(new ConfirmScreen(this.pageIndex, this.contents, this.lastListPage)); }));
        updateDeleteButton();
    }

    private void updateDeleteButton() {
        this.deleteButton.visible = this.pageIndex >= getPageCount();
    }
}

package dev.blueish.coordbook.gui;

import dev.blueish.coordbook.CoordinateBook;
import dev.blueish.coordbook.data.Book;
import dev.blueish.coordbook.util.TextCreator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Formatting;

@Environment(value = EnvType.CLIENT)
public class ListScreen extends BookScreen {
    private final Book contents;
    private int lastListPage = 0;
    private ButtonWidget deleteButton;
    private ButtonWidget favoriteButton;

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
        this.deleteButton = this.addDrawableChild(ButtonWidget.builder(new TextCreator("DELETE").format(Formatting.BOLD).format(Formatting.RED).raw(), button -> this.client.setScreen(new ConfirmScreen(this.pageIndex, this.contents, this.lastListPage)))
            .position(this.width / 2 - 55, 135).size(100, 20).build());
        this.favoriteButton = this.addDrawableChild(ButtonWidget.builder(new TextCreator("Toggle favorite").format(Formatting.GOLD).raw(), button -> {this.contents.toggleFavorite(this.pageIndex); updateDeleteButton(); })
            .position(this.width / 2 - 55, 110).size(100, 20).build());
        updateDeleteButton();
    }

    private void updateDeleteButton() {
        boolean isIndividual = this.pageIndex >= getPageCount();
        this.deleteButton.visible = isIndividual;
        this.favoriteButton.visible = isIndividual;
        if (isIndividual) {
            boolean favorite = this.contents.getFavorite(this.pageIndex);
            this.favoriteButton.setMessage(new TextCreator(favorite ? "Remove favorite" : "Add favorite").format(favorite ? Formatting.RED : Formatting.GOLD).raw());
            CoordinateBook.lastPage = this.pageIndex;
        }
    }
}

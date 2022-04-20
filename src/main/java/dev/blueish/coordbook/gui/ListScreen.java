package dev.blueish.coordbook.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;

import dev.blueish.coordbook.CoordinateBook;
import dev.blueish.coordbook.data.Book;
import dev.blueish.coordbook.data.Coord;
import dev.blueish.coordbook.data.Position;
import dev.blueish.coordbook.util.TextCreator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ListScreen
extends Screen {
    public static final int field_32328 = 16;
    public static final int field_32329 = 36;
    public static final int field_32330 = 30;
    public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
    protected static final int MAX_TEXT_WIDTH = 114;
    protected static final int MAX_TEXT_HEIGHT = 128;
    protected static final int WIDTH = 192;
    protected static final int HEIGHT = 192;
    private Book contents;
    private int pageIndex;
    private List<OrderedText> cachedPage = Collections.emptyList();
    private int cachedPageIndex = -1;
    private Text pageIndexText = LiteralText.EMPTY;
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;
    private ButtonWidget deleteButton;
    private final boolean pageTurnSound;
    private int lastListScreen = 0;

    public ListScreen(Book pageProvider, int startPage) {
        super(NarratorManager.EMPTY);
        this.contents = pageProvider.init();
        this.pageTurnSound = true;
        this.pageIndex = startPage;
    }

    public boolean setPage(int index) {
        CoordinateBook.LOGGER.info(String.valueOf(index));
        int i = MathHelper.clamp(index, 0, this.contents.pageCount - 1);
        if (i != this.pageIndex) {
            this.pageIndex = i;
            this.updatePageButtons();
            this.cachedPageIndex = -1;
            if (this.pageIndex >= this.getPageCount()) {
                CoordinateBook.lastPage = i;
            }
            return true;
        }
        return false;
    }

    protected boolean jumpToPage(int page) {
        return this.setPage(page);
    }

    @Override
    protected void init() {
        this.addCloseButton();
        this.addPageButtons();
    }

    protected void addCloseButton() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, 196, 200, 20, ScreenTexts.DONE, button -> this.client.setScreen(null)));
    }

    protected void addPageButtons() {
        int i = (this.width - 192) / 2;
        this.nextPageButton = this.addDrawableChild(new PageTurnWidget(i + 116, 159, true, button -> this.goToNextPage(), this.pageTurnSound));
        this.previousPageButton = this.addDrawableChild(new PageTurnWidget(i + 43, 159, false, button -> this.goToPreviousPage(), this.pageTurnSound));
        this.deleteButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 55, 135, 100, 20, new TextCreator("DELETE").format(Formatting.BOLD).format(Formatting.RED).raw(), button -> { this.client.setScreen(new ConfirmScreen(this.pageIndex, this.contents, this.lastListScreen)); }));
        this.updatePageButtons();
    }

    private int getPageCount() {
        return this.contents.listPageCount;
    }

    protected void goToPreviousPage() {
        if (this.pageIndex >= this.getPageCount()) {
            this.pageIndex = lastListScreen;
            this.cachedPageIndex = -1;
        } else if (this.pageIndex > 0) {
            --this.pageIndex;
        }
        this.updatePageButtons();
    }

    protected void goToNextPage() {
        if (this.pageIndex < this.getPageCount() - 1) {
            ++this.pageIndex;
        }
        this.updatePageButtons();
    }

    private void updatePageButtons() {
        this.nextPageButton.visible = this.pageIndex < this.getPageCount() - 1;
        this.previousPageButton.visible = this.pageIndex > 0;
        this.deleteButton.visible = this.pageIndex >= this.getPageCount();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        switch (keyCode) {
            case 266: {
                this.previousPageButton.onPress();
                return true;
            }
            case 267: {
                this.nextPageButton.onPress();
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BOOK_TEXTURE);
        int i = (this.width - 192) / 2;
        this.drawTexture(matrices, i, 2, 0, 0, 192, 192);
        if (this.cachedPageIndex != this.pageIndex) {
            StringVisitable stringVisitable = this.contents.getPage(this.pageIndex);
            this.cachedPage = this.textRenderer.wrapLines(stringVisitable, 114);
            this.pageIndexText = this.pageIndex < this.getPageCount() ? new TranslatableText("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1)) : new LiteralText("");
        }
        this.cachedPageIndex = this.pageIndex;
        int k = this.textRenderer.getWidth(this.pageIndexText);
        this.textRenderer.draw(matrices, this.pageIndexText, (float)(i - k + 192 - 44), 18.0f, 0);
        int l = Math.min(128 / this.textRenderer.fontHeight, this.cachedPage.size());
        for (int m = 0; m < l; ++m) {
            OrderedText orderedText = this.cachedPage.get(m);
            this.textRenderer.draw(matrices, orderedText, (float)(i + 36), (float)(32 + m * this.textRenderer.fontHeight), 0);
        }
        Style style = this.getTextStyleAt(mouseX, mouseY);
        if (style != null) {
            this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
        }
        if (this.pageIndex < this.getPageCount()) {
            this.lastListScreen = this.pageIndex;
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Style style;
        if (button == 0 && (style = this.getTextStyleAt(mouseX, mouseY)) != null && this.handleTextClick(style)) {
            return true;
        }
        Position mouse = this.absolutePositionToScreenPosition(mouseX, mouseY);
        if (mouse.y >= this.textRenderer.fontHeight * 7 && mouse.y <= this.textRenderer.fontHeight * 8 && this.pageIndex >= this.getPageCount()) {
            this.pageIndex = this.contents.toggleFavorite(this.pageIndex);
            this.cachedPageIndex = -1;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private Position absolutePositionToScreenPosition(double x, double y) {
        return new Position(x - (this.width - 192) / 2 - 36, y - 32, 5);
    }

    @Override
    public boolean handleTextClick(Style style) {
        CoordinateBook.LOGGER.info("text click");
        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent == null) {
            return false;
        }
        if (clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String string = clickEvent.getValue();
            try {
                int i = Integer.parseInt(string) - 1;
                return this.jumpToPage(i);
            }
            catch (Exception exception) {
                return false;
            }
        }
        boolean bl = super.handleTextClick(style);
        if (bl && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.closeScreen();
        }
        return bl;
    }

    protected void closeScreen() {
        this.client.setScreen(null);
    }

    @Nullable
    public Style getTextStyleAt(double x, double y) {
        if (this.cachedPage.isEmpty()) {
            return null;
        }
        int i = MathHelper.floor(x - (double)((this.width - 192) / 2) - 36.0);
        int j = MathHelper.floor(y - 2.0 - 30.0);
        if (i < 0 || j < 0) {
            return null;
        }
        int k = Math.min(128 / this.textRenderer.fontHeight, this.cachedPage.size());
        if (i <= 114 && j < this.client.textRenderer.fontHeight * k + k) {
            int l = j / this.client.textRenderer.fontHeight;
            if (l >= 0 && l < this.cachedPage.size()) {
                OrderedText orderedText = this.cachedPage.get(l);
                return this.client.textRenderer.getTextHandler().getStyleAt(orderedText, i);
            }
            return null;
        }
        return null;
    }
}

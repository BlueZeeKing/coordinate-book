package dev.blueish.coordbook.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.blueish.coordbook.CoordinateBook;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class BookScreen extends Screen {
    private final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
    protected static final int WIDTH = 192;
    protected static final int HEIGHT = 192;
    protected static final int MAX_TEXT_WIDTH = 114;
    protected static final int MAX_TEXT_HEIGHT = 128;
    private List<OrderedText> cachedPage = Collections.emptyList();
    int cachedPageIndex = -1;
    int pageIndex = 0;
    private Text pageIndexText;
    int pageCount;

    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;

    public BookScreen() {
        super(NarratorManager.EMPTY);
    }

    @Override
    protected void init() {
        this.addCloseButton();
        this.addPageButtons();
    }

    protected void addCloseButton() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.client.setScreen(null)).position(this.width / 2 - 100, 196).size(200, 20).build());
    }

    protected void addPageButtons() {
        int posX = (this.width - 192) / 2;
        this.nextPageButton = this.addDrawableChild(new PageTurnWidget(posX + 116, 159, true, button -> setPage(pageIndex + 1), true));
        this.previousPageButton = this.addDrawableChild(new PageTurnWidget(posX + 43, 159, false, button -> setPage(pageIndex - 1), true));
        this.updatePageButtons();
    }

    private void updatePageButtons() {
        this.nextPageButton.visible = this.pageIndex < this.getPageCount() - 1;
        this.previousPageButton.visible = this.pageIndex > 0;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        switch (keyCode) {
            case 266 -> {
                this.previousPageButton.onPress();
                return true;
            }
            case 267 -> {
                this.nextPageButton.onPress();
                return true;
            }
        }
        return false;
    }

    public void setPage(int index) {
        if (index != this.pageIndex) {
            this.pageIndex = index;
            this.updatePageButtons();
            this.cachedPageIndex = -1;
        }
    }

    public StringVisitable getPage(int i) {
        return null;
    }

    public int getPageCount() {
        return pageCount;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices); // render the background
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BOOK_TEXTURE);

        int bookXPos = (this.width - WIDTH) / 2; // get the book position
        this.drawTexture(matrices, bookXPos, 2, 0, 0, WIDTH, HEIGHT);

        if (pageIndex != cachedPageIndex) { // if the page has changed
            cachedPage = this.textRenderer.wrapLines(getPage(pageIndex), MAX_TEXT_WIDTH); // regenerate the content
            this.pageIndexText = Text.translatable("book.pageIndicator", this.pageIndex + 1, this.getPageCount()); // create the page number indicator
        }
        this.cachedPageIndex = pageIndex;

        if (pageIndex < getPageCount()) {
            int pageIndexTextWidth = this.textRenderer.getWidth(this.pageIndexText); // get the page number indicator width
            this.textRenderer.draw(matrices, this.pageIndexText, (float) (bookXPos - pageIndexTextWidth + WIDTH - 44), 18.0f, 0); // render the page number indicator
        }

        int numLines = Math.min(MAX_TEXT_HEIGHT / this.textRenderer.fontHeight, this.cachedPage.size()); // render the main content
        for (int line = 0; line < numLines; line++) {
            this.textRenderer.draw(matrices, this.cachedPage.get(line), (float) (bookXPos + 36), (float) (32 + line * this.textRenderer.fontHeight), 0);
        }

        Style style = this.getTextStyleAt(mouseX, mouseY); // render the text hover
        if (style != null) {
            this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
        }

        super.render(matrices, mouseX, mouseY, delta); // render anything extra from the main screen
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Style style;
        if (button == 0 && (style = this.getTextStyleAt(mouseX, mouseY)) != null && this.handleTextClick(style)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean handleTextClick(Style style) {
        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent == null) {
            return false;
        }

        CoordinateBook.LOGGER.info("click");

        if (clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String string = clickEvent.getValue();
            try {
                int i = Integer.parseInt(string) - 1;
                this.setPage(i);
                return true;
            } catch (Exception exception) {
                return false;
            }
        }

        boolean returnValue = super.handleTextClick(style);
        if (returnValue && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.client.setScreen(null);
        }
        return returnValue;
    }

    @Nullable
    public Style getTextStyleAt(double mouseX, double mouseY) {
        if (this.cachedPage.isEmpty()) { // if the current text is empty cancel
            return null;
        }

        int x = MathHelper.floor(mouseX - (double) ((this.width - WIDTH) / 2) - 36.0); // get the mouse pos relative to the book
        int y = MathHelper.floor(mouseY - 32.0);
        if (x < 0 || y < 0) { // check if the mouse is in the book
            return null;
        }

        int numLines = Math.min(MAX_TEXT_HEIGHT / this.textRenderer.fontHeight, this.cachedPage.size()); // check to make sure the mouse is in the screen and on some text
        if (x <= MAX_TEXT_WIDTH && y < this.client.textRenderer.fontHeight * numLines + numLines) {
            int line = y / this.client.textRenderer.fontHeight;
            if (line < this.cachedPage.size()) {
                OrderedText orderedText = this.cachedPage.get(line); // get the text
                return this.client.textRenderer.getTextHandler().getStyleAt(orderedText, x); // get the style for that text
            }
            return null;
        }
        return null;
    }
}

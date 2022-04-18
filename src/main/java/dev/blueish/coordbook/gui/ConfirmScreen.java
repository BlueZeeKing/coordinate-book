package dev.blueish.coordbook.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.blueish.coordbook.data.Book;
import dev.blueish.coordbook.util.TextCreator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@Environment(value=EnvType.CLIENT)
public class ConfirmScreen
extends Screen {
    public static final int field_32328 = 16;
    public static final int field_32329 = 36;
    public static final int field_32330 = 30;
    public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
    protected static final int MAX_TEXT_WIDTH = 114;
    protected static final int MAX_TEXT_HEIGHT = 128;
    protected static final int WIDTH = 192;
    protected static final int HEIGHT = 192;
    private int pageIndex;
    private List<OrderedText> cachedPage = Collections.emptyList();
    private int cachedPageIndex = -1;
    private Text pageIndexText = LiteralText.EMPTY;
    private int index;
    private Book book;
    private int returnIndex;

    public ConfirmScreen(int index, Book book, int returnIndex) {
        super(NarratorManager.EMPTY);
        this.index = index;
        this.book = book;
        this.returnIndex = returnIndex;
    }

    @Override
    protected void init() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 60, 60, 50, 20, new TextCreator("Cancel").format(Formatting.GRAY).raw(), (button) -> this.client.setScreen(new ListScreen(book, index))));
        this.addDrawableChild(new ButtonWidget(this.width / 2, 60, 50, 20, new TextCreator("Confirm").format(Formatting.RED).raw(), (button) -> { book.delete(index); this.client.setScreen(new ListScreen(book, returnIndex)); }));
        this.addCloseButton();
    }

    protected void addCloseButton() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, 196, 200, 20, ScreenTexts.DONE, (button) -> this.client.setScreen(new ListScreen(book, index))));
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
            StringVisitable stringVisitable = new TextCreator("Are you absolutely sure?")
                .format(Formatting.BOLD)
                .format(Formatting.RED)
                .raw();
            this.cachedPage = this.textRenderer.wrapLines(stringVisitable, 114);
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
        super.render(matrices, mouseX, mouseY, delta);
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

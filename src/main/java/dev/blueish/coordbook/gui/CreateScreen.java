package dev.blueish.coordbook.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.gui.DrawableHelper;

import dev.blueish.coordbook.util.TextCreator;
import dev.blueish.coordbook.CoordinateBook;
import dev.blueish.coordbook.util.Position;

@Environment(value=EnvType.CLIENT)
public class CreateScreen
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

    private Position coords;
    private String name = "";

    private Position cursor;
    private int line;

    public CreateScreen(Position coords) {
        super(NarratorManager.EMPTY);
        this.coords = coords;
        this.cursor = new Position(10, 10, 10);
        this.line = 2;
    }

    @Override
    protected void init() {
        this.addCloseButton();
    }

    protected void addCloseButton() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, 196, 200, 20, ScreenTexts.DONE, button -> this.client.setScreen(null)));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
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
        int j = 2;
        this.drawTexture(matrices, i, 2, 0, 0, 192, 192);
        if (this.cachedPageIndex != this.pageIndex) {
            StringVisitable stringVisitable = new TextCreator("Coords")
                .addNewline(
                    new TextCreator(String.format("%d/%d/%d", coords.x, coords.y, coords.z))
                        .format(Formatting.RED)
                        .center()
                ).addNewline(
                    new TextCreator("Name")
                ).addNewline(
                    (name == "" ? new TextCreator("Enter name").format(Formatting.GRAY) : new TextCreator(name)).format(Formatting.UNDERLINE).center()
                ).addNewline(
                    new TextCreator("Color")
                ).addNewline(
                    (name == "" ? new TextCreator("Enter color").format(Formatting.GRAY) : new TextCreator(name)).format(Formatting.UNDERLINE).center()
                )
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
        this.drawCursor(matrices);
    }

    private void drawCursor(MatrixStack matrices) {
        Position pos = screenPositionToAbsolutePosition(new Position(cursor.x, line == 1 ? this.textRenderer.fontHeight * 3 : (line == 1 ? this.textRenderer.fontHeight * 5 : null), 10));
        DrawableHelper.fill(matrices, pos.x, pos.y - 1, pos.x + 1, pos.y + this.textRenderer.fontHeight, -16777216);
    }

    private Position screenPositionToAbsolutePosition(Position position) {
        return new Position(position.x + (this.width - 192) / 2 + 36, position.y + 32, 5);
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

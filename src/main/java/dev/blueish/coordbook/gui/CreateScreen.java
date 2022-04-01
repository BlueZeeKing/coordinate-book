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
import net.minecraft.client.util.SelectionManager;
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
import net.minecraft.SharedConstants;

import dev.blueish.coordbook.util.TextCreator;
import dev.blueish.coordbook.CoordinateBook;
import dev.blueish.coordbook.util.Position;
import dev.blueish.coordbook.util.Coord;

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
    private String color = "";
    private boolean cursorVisible = false; 

    private Position cursor;

    public CreateScreen(Position coords) {
        super(NarratorManager.EMPTY);
        this.coords = coords;
        this.cursor = new Position(0, 2, 0);
    }

    @Override
    protected void init() {
        this.addCloseButton();
    }

    protected void addCloseButton() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, 196, 200, 20, ScreenTexts.DONE, (button) -> { this.client.setScreen(null); if (name != "") { new Coord(coords, name, Formatting.byName(color)  == null ? Formatting.BLACK : Formatting.byName(color), this.client.player.getWorld().getRegistryKey().getValue().toString()); } }));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        switch (keyCode) {
            case 261:
            case 259: {
                if (cursor.x > 0 && cursorVisible) {
                    if (cursor.y == 1) {
                        this.name = new StringBuilder(name).deleteCharAt(cursor.x-1).toString();
                    } else if (cursor.y == 2) {
                        this.color = new StringBuilder(color).deleteCharAt(cursor.x-1).toString();
                    }
                    cursor.x--;
                    this.cachedPageIndex = -1;
                }
                return true;
            }
            case 263: {
                if (cursor.x > 0) {
                    cursor.x--;
                }
                return true;
            }
            case 262: {
                if (cursor.x < (cursor.y == 1 ? name : color).length()) {
                    cursor.x++;
                }
                return true;
            }
            case 265: {
                if (cursor.y == 2) {
                    cursor.y--;
                    cursor.x = Math.min(cursor.x, name.length());
                }
                return true;
            }
            case 264: {
                if (cursor.y == 1) {
                    cursor.y++;
                    cursor.x = Math.min(cursor.x, color.length());
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (super.charTyped(chr, modifiers)) {
            return true;
        }
        if (SharedConstants.isValidChar(chr) && cursorVisible) {
            if (cursor.y == 1) {
                this.name = name.substring(0, cursor.x) + chr + name.substring(cursor.x);
            } else if (cursor.y == 2) {
                this.color = color.substring(0, cursor.x) + chr + color.substring(cursor.x);
            }
            cursor.x++;
            this.cachedPageIndex = -1;
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
        this.drawTexture(matrices, i, 2, 0, 0, 192, 192);
        if (this.cachedPageIndex != this.pageIndex) {
            StringVisitable stringVisitable = new TextCreator("Coords")
                //.format(Formatting.BOLD)
                .addNewline(
                    new TextCreator(String.format("%d/%d/%d", coords.x, coords.y, coords.z))
                        .format(Formatting.RED)
                ).addNewline(
                    new TextCreator("Name")//.format(Formatting.BOLD)
                ).addNewline(
                    (name == "" ? new TextCreator("Enter name").format(Formatting.GRAY) : new TextCreator(name)).format(Formatting.UNDERLINE)
                ).addNewline(
                    new TextCreator("Color")//.format(Formatting.BOLD)
                ).addNewline(
                    (color == "" ? new TextCreator("Enter color").format(Formatting.GRAY) : new TextCreator(color).format(Formatting.byName(color)  == null ? Formatting.BLACK : Formatting.byName(color))).format(Formatting.UNDERLINE)
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
        if (cursor.y == 1 && cursorVisible) {
            Position pos = screenPositionToAbsolutePosition(new Position(
                this.textRenderer.getWidth(name.substring(0, cursor.x)),
                this.textRenderer.fontHeight * 3,
                10
            ));

            DrawableHelper.fill(matrices, pos.x, pos.y - 1, pos.x + 1, pos.y + this.textRenderer.fontHeight, -16777216);
        } else if (cursor.y == 2 && cursorVisible) {
            Position pos = screenPositionToAbsolutePosition(new Position(
                this.textRenderer.getWidth(color.substring(0, cursor.x)),
                this.textRenderer.fontHeight * 5,
                10
            ));

            DrawableHelper.fill(matrices, pos.x, pos.y - 1, pos.x + 1, pos.y + this.textRenderer.fontHeight, -16777216);
        }
    }

    private Position screenPositionToAbsolutePosition(Position position) {
        return new Position(position.x + (this.width - 192) / 2 + 36, position.y + 32, 5);
    }

    private Position absolutePositionToScreenPosition(double x, double y) {
        return new Position(x - (this.width - 192) / 2 - 36, y - 32, 5);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Style style;
        if (button == 0 && (style = this.getTextStyleAt(mouseX, mouseY)) != null && this.handleTextClick(style)) {
            return true;
        }
        Position mouse = this.absolutePositionToScreenPosition(mouseX, mouseY);
        if (mouse.y >= this.textRenderer.fontHeight * 3 && mouse.y <= this.textRenderer.fontHeight * 4) {
            cursor.y = 1;
            cursor.x = this.textRenderer.trimToWidth(name, mouse.x).length();
            this.cursorVisible = true;
        } else if (mouse.y >= this.textRenderer.fontHeight * 5 && mouse.y <= this.textRenderer.fontHeight * 6) {
            cursor.y = 2;
            cursor.x = this.textRenderer.trimToWidth(color, mouse.x).length();
            this.cursorVisible = true;
        } else {
            this.cursorVisible = false;
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

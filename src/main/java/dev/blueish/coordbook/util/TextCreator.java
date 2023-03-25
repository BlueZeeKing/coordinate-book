package dev.blueish.coordbook.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class TextCreator {
    private MutableText text;
    private Style defaultStyle = Style.EMPTY;

    private void setStyle() {
        this.defaultStyle = defaultStyle.withBold(false);
        this.defaultStyle = defaultStyle.withColor(Formatting.BLUE);
        this.defaultStyle = defaultStyle.withUnderline(false);
    }

    public TextCreator(String message) {
        text = (MutableText) Text.of(message);
        this.setStyle();
    }

    public TextCreator add(String message) {
        text.append(message);
        return this;
    }

    public TextCreator add(TextCreator message) {
        text.append(message.raw());
        return this;
    }

    public TextCreator addNoFormat(TextCreator message) {
        this.text = ((MutableText) Text.of("")).append(text).append(message.raw());
        return this;
    }

    public TextCreator addNoFormat(String message) {
        this.text = ((MutableText) Text.of("")).append(text).append(Text.of(message));
        return this;
    }

    public TextCreator addNewline(MutableText message) {
        this.text = ((MutableText) Text.of("")).append(text);
        text.append("\n");
        text.append(message);
        return this;
    }

    public TextCreator addNewline() {
        this.text = ((MutableText) Text.of("")).append(text);
        text.append("\n");
        return this;
    }

    public TextCreator addNewline(TextCreator message) {
        return this.addNewline(message.raw());
    }

    public TextCreator format(Formatting... format) {
        text.formatted(format);
        return this;
    }

    public TextCreator style(Style style) {
        text.setStyle(style);
        return this;
    }

    public TextCreator hover(String message) {
        text.styled(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of(message))));
        return this;
    }

    public TextCreator click(int page) {
        text.styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, Integer.toString(page))));
        return this;
    }

    public TextCreator command(String command) {
        text.styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
        return this;
    }

    public TextCreator say(String command) {
        text.styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/say " + command)));
        return this;
    }

    public TextCreator copy(String command) {
        text.styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, command)));
        return this;
    }

    public TextCreator filler(String symbol) {
        text.append(Text.of(" " + Formatting.RESET + Formatting.GRAY + symbol + Formatting.RESET + " "));
        return this;
    }

    public TextCreator center() {
        String finished = MinecraftClient.getInstance().textRenderer.trimToWidth("                    ", 114 / 2 - MinecraftClient.getInstance().textRenderer.getWidth(text) / 2);
        text = ((MutableText) Text.of(finished)).append(text);
        return this;
    }

    public MutableText raw() {
        return text;
    }
}
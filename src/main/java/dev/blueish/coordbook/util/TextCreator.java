package dev.blueish.coordbook.util;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import java.lang.Math;

public class TextCreator {
  private MutableText text;

  public TextCreator(String message) {
    text = new LiteralText(message);
  }

  public TextCreator(String message, Object... args) {
    text = new LiteralText(String.format(message, args));
  }

  public TextCreator add(String message) {
    text.append(message);
    return this;
  }

  public TextCreator add(String message, Object... args) {
    text.append(String.format(message, args));
    return this;
  }

  public TextCreator add(TextCreator message) {
    text.append(message.raw());
    return this;
  }

  public TextCreator addNewline(MutableText message) {
    text.append("\n");
    text.append(message);
    return this;
  }

  public TextCreator addNewline(TextCreator message) {
    text.append("\n");
    text.append(message.raw());
    return this;
  }

  public TextCreator format(Formatting... format) {
    text.formatted(format);
    return this;
  }

  public TextCreator fail() {
    text.formatted(Formatting.RED);
    return this;
  }

  public TextCreator hover(String message) {
    text.styled(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(message))));
    return this;
  }

  public TextCreator click(String message) {
    text.styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message)));
    return this;
  }

  public TextCreator filler(String symbol) {
    text.append(new LiteralText(" " + Formatting.RESET + Formatting.DARK_GRAY + symbol + Formatting.RESET + " "));
    return this;
  }

  public TextCreator center() {
    this.text = new LiteralText("    ").append(text);
    return this;
  }

  public MutableText raw() {
    return text;
  }
}
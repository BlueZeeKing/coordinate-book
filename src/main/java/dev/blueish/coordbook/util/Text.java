package dev.blueish.coordbook.util;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import java.lang.Math;

public class Text {
  private MutableText text;

  public Text(String message) {
    text = new LiteralText(message);
  }

  public Text(String message, Object... args) {
    text = new LiteralText(String.format(message, args));
  }

  public Text add(String message) {
    text.append(message);
    return this;
  }

  public Text add(String message, Object... args) {
    text.append(String.format(message, args));
    return this;
  }

  public Text add(Text message) {
    text.append(message.raw());
    return this;
  }

  public Text format(Formatting... format) {
    text.formatted(format);
    return this;
  }

  public Text fail() {
    text.formatted(Formatting.RED);
    return this;
  }

  public Text hover(String message) {
    text.styled(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(message))));
    return this;
  }

  public Text click(String message) {
    text.styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message)));
    return this;
  }

  public Text filler(String symbol) {
    text.append(new LiteralText(" " + Formatting.RESET + Formatting.DARK_GRAY + symbol + Formatting.RESET + " "));
    return this;
  }

  public Text center() {
    this.text = new LiteralText(new String(new char[Math.round((19 - text.asString().length())/2)]).replace("\0", " ")).append(text);
    return this;
  }

  public MutableText raw() {
    return text;
  }
}
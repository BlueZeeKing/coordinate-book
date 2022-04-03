package dev.blueish.coordbook.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;
import java.time.LocalDateTime;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import dev.blueish.coordbook.CoordinateBook;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;

public class Coord {
  public Position coords;
  public String name;
  public Formatting color;
  public String dimension;
  public boolean favorite;
  public LocalDateTime date;
  private JSONFile file;

  public Coord(Position coords, String name, Formatting color, String dimension, String fileName) {
    this.coords = coords;
    this.name = name;
    this.color = color;
    this.dimension = dimension;
    this.favorite = false;
    this.date = LocalDateTime.now();
    this.file = new JSONFile(fileName);

    this.write();
  }

  public Coord(Position coords, String name, Formatting color, String dimension, boolean favorite, LocalDateTime date, String fileName) {
    this.coords = coords;
    this.name = name;
    this.color = color;
    this.dimension = dimension;
    this.favorite = favorite;
    this.date = date;
    this.file = new JSONFile(fileName);
  }

  public void write() {
    file.put(coords, name, color, dimension, favorite, date);
  }

  public MutableText getText(int pageNum) {
    return new TextCreator(new TranslatableText(dimension)).filler("-").add(new TextCreator(name).format(color).hover(String.format("%d/%d/%d", coords.x, coords.y, coords.z))).click(pageNum).raw();
  }

  public MutableText getPage() {
    return new TextCreator(name)
                .format(color)
                .format(Formatting.BOLD)
                .addNewline(
                    new TextCreator(new TranslatableText(dimension)).filler("-").add(new TextCreator(String.format("%d/%d/%d", coords.x, coords.y, coords.z)))
                ).addNewline(
                    new TextCreator(date.format(DateTimeFormatter.ofPattern("MM/dd hh:mm a")))
                )
                .raw();
  }
}

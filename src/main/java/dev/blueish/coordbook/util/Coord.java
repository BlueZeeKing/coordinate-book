package dev.blueish.coordbook.util;

import net.minecraft.util.Formatting;
import java.time.LocalDateTime;
import net.minecraft.text.MutableText;
import dev.blueish.coordbook.CoordinateBook;

public class Coord {
  public Position coords;
  public String name;
  public Formatting color;
  public String dimension;
  public boolean favorite;
  public LocalDateTime date;
  private JSONFile file;

  public Coord(Position coords, String name, Formatting color, String dimension) {
    this.coords = coords;
    this.name = name;
    this.color = color;
    this.dimension = dimension;
    this.favorite = false;
    this.date = LocalDateTime.now();
    this.file = new JSONFile("test");

    this.write();
  }

  public Coord(Position coords, String name, Formatting color, String dimension, boolean favorite, LocalDateTime date) {
    this.coords = coords;
    this.name = name;
    this.color = color;
    this.dimension = dimension;
    this.favorite = favorite;
    this.date = date;
    this.file = new JSONFile("test");
  }

  public void write() {
    file.put(coords, name, color, dimension, favorite, date);
  }

  public MutableText getText() {
    CoordinateBook.LOGGER.info(name);
    return new TextCreator(name).format(color).hover(String.format("%d/%d/%d", coords.x, coords.y, coords.z)).raw();
  }
}

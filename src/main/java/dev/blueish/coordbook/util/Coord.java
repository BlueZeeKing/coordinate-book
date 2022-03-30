package dev.blueish.coordbook.util;

import net.minecraft.util.Formatting;
import java.time.LocalDateTime;

public class Coord {
  public Position coords;
  public String name;
  public Formatting color;
  public String dimension;
  public boolean favorite;
  public LocalDateTime date;

  public Coord(Position coords, String name, Formatting color, String dimension) {
    this.coords = coords;
    this.name = name;
    this.color = color;
    this.dimension = dimension;
    this.favorite = false;
    this.date = LocalDateTime.now();

    this.write();
  }

  public void write() {
    new JSONFile("test");
  }
}

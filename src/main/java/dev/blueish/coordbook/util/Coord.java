package dev.blueish.coordbook.util;

import net.minecraft.util.Formatting;
import java.time.LocalDateTime;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import dev.blueish.coordbook.CoordinateBook;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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

  public MutableText getText(int pageNum) {
    return new TextCreator(new TranslatableText(dimension)).filler("-").add(new TextCreator(name).format(color).hover(String.format("%d/%d/%d", coords.x, coords.y, coords.z))).click(pageNum).raw();
  }

  public MutableText getPage() {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    return new TextCreator(name)
                .format(color)
                //.format(Formatting.BOLD)
                .addNewline(
                    new TextCreator(new TranslatableText(dimension)).filler("-").add(new TextCreator(String.format("%d/%d/%d", coords.x, coords.y, coords.z)))
                ).addNewline(
                    //new TextCreator(dateFormat.format(date))
                    new TextCreator("date no work")
                )
                .raw();
  }
}

package dev.blueish.coordbook.data;

import dev.blueish.coordbook.CoordinateBook;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Formatting;
import java.time.LocalDateTime;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import java.time.format.DateTimeFormatter;

import dev.blueish.coordbook.util.JSONFile;
import dev.blueish.coordbook.util.TextCreator;

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
  }

  public Coord(Position coords, String name, Formatting color, String dimension, boolean favorite, LocalDateTime date) {
    this.coords = coords;
    this.name = name;
    this.color = color;
    this.dimension = dimension;
    this.favorite = favorite;
    this.date = date;
  }

  public TextCreator getText(int pageNum) {
    return new TextCreator(new TranslatableText(dimension)).format(Formatting.GRAY).add(" ").add(new TextCreator(name).format(color).hover(String.format("%d/%d/%d", coords.x, coords.y, coords.z))).click(pageNum);
  }

  public MutableText getPage() {
    return new TextCreator(name)
        .format(color)
        .format(Formatting.BOLD)
        .center()
        .addNewline(
            new TextCreator(new TranslatableText(dimension)).filler("-").add(new TextCreator(String.format("%d/%d/%d", coords.x, coords.y, coords.z)))
        ).addNewline(
            new TextCreator(date.format(DateTimeFormatter.ofPattern("MM/dd hh:mm a")))
        ).addNewline(
          new TextCreator("\n\n\n\n")
              .add(new TextCreator(favorite ? "Remove favorite" : "Add favorite").format(favorite ? Formatting.RED : Formatting.GOLD).center().hover(favorite ? "Remove this item from your favorites" : "Add this item to the favorites menu"))
              .addNewline()
              .addNewline(new TextCreator("Send").format(Formatting.BLUE).center().hover("Send to all players").send(
                  String.format("Coordinate Book: %s - %d/%d/%d", name, coords.x, coords.y, coords.z)
              ))
        )
        .raw();
  }
}

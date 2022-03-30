package dev.blueish.coordbook.util;

import java.util.ArrayList;
import dev.blueish.coordbook.util.Coord;
import dev.blueish.coordbook.util.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class Book {
  public ArrayList<Coord> coordList;
  public int pageCount;

  public Book(int pageCount) {
    this.pageCount = pageCount;
  }

  public MutableText getPage(int index) {
    if (index == 0) {
      return new Text("Coordinate Book").center().format(Formatting.BOLD).raw();
    }
    return null;
  }
}

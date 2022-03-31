package dev.blueish.coordbook.util;

import java.util.ArrayList;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class Book {
  public ArrayList<Coord> coordList;
  public int pageCount;
  private JSONFile file;

  public Book(int pageCount) {
    this.pageCount = pageCount;
    this.file = new JSONFile("test");
  }

  public MutableText getPage(int index) {
    if (index == 0) {
      return new Text("Coordinate Book").center().format(Formatting.BOLD).addNewline(file.getAll().get(0).getText()).raw();
    }
    return null;
  }
}

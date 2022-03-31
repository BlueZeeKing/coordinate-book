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
      TextCreator content = new TextCreator("Coordinate Book").center();
      ArrayList<Coord> coords = file.getAll();

      for (int i = 0; i < Math.min(coords.size(), 15); i++) {
        content.addNewline(coords.get(i).getText());
      }

      return content.raw();
    }
    return null;
  }
}

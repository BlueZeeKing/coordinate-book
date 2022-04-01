package dev.blueish.coordbook.util;

import java.util.ArrayList;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import dev.blueish.coordbook.CoordinateBook;

public class Book {
  public ArrayList<Coord> coordList;
  public int pageCount;
  private JSONFile file;
  public int listPageCount;

  public Book(int pageCount) {
    this.file = new JSONFile("test");
    this.coordList = file.getAll();
    this.listPageCount = (int)Math.ceil((coordList.size()+1)/13.0);
    this.pageCount = listPageCount+coordList.size();
  }

  public MutableText getPage(int index) {
    if (index < listPageCount) {
      TextCreator content = new TextCreator("Coordinate Book").center().format(Formatting.BOLD);
      ArrayList<Coord> coords = file.getAll();

      for (int i = 0; i < Math.min(coords.size(), 15); i++) {
        content.addNewline(coords.get(i).getText(listPageCount+i+1));
      }

      return content.raw();
    } else if (index < pageCount) {
      return coordList.get(index - listPageCount).getPage();
    }
    return null;
  }
}

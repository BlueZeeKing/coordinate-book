package dev.blueish.coordbook.util;

import java.util.ArrayList;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

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

  public MutableText getPage(int index, TextRenderer renderer) {
    if (index < listPageCount) {
      TextCreator content = index == 0 ? new TextCreator("Coordinate Book").begin("  ").format(Formatting.BOLD) : new TextCreator("");
      ArrayList<Coord> coords = file.getAll();
      for (int i = 0; i < Math.min(coords.size() - index * 13, index == 0 ? 12 : 13); i++) {
        if (i == 0 && index != 0) {
          content.add(coords.get(i - (index == 0 ? 0 : 1) + index * 13).getText(listPageCount + (index == 0 ? 1 : 0) + i + index * 13));
        } else {
          content.addNewline(coords.get(i - (index == 0 ? 0 : 1) + index * 13).getText(listPageCount + (index == 0 ? 1 : 0) + i + index * 13));
        }
      }

      return content.raw();
    } else if (index < pageCount) {
      return coordList.get(index - listPageCount).getPage();
    }
    return null;
  }
}

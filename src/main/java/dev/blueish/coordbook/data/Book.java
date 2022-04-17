package dev.blueish.coordbook.data;

import java.util.ArrayList;

import dev.blueish.coordbook.CoordinateBook;
import dev.blueish.coordbook.util.JSONFile;
import dev.blueish.coordbook.util.TextCreator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class Book {
  public ArrayList<Coord> coordList;
  public int pageCount;
  private JSONFile file;
  public int listPageCount;

  public Book(MinecraftClient client) {
    CoordinateBook.LOGGER.info(CoordinateBook.ClientToName(client));
    this.file = new JSONFile(CoordinateBook.ClientToName(client));

    this.coordList = file.getAll();
    this.listPageCount = (int)Math.ceil((coordList.size()+1)/13.0);
    this.pageCount = listPageCount+coordList.size();
  }

  public MutableText getPage(int index, TextRenderer renderer) {
    if (index < listPageCount) {
      TextCreator content = index == 0 ? new TextCreator("Coordinate Book").format(Formatting.BOLD).center(renderer) : new TextCreator("");
      for (int i = 0; i < Math.min(coordList.size() - index * 13 + (listPageCount > 1 ? 1 : 0), index == 0 ? 12 : 13); i++) {
        if (i == 0 && index != 0) {
          content.add(coordList.get(i - 1 + index * 13).getText(listPageCount + i + index * 13));
        } else {
          content.addNewline(coordList.get(i - (index == 0 ? 0 : 1) + index * 13).getText(listPageCount + (index == 0 ? 1 : 0) + i + index * 13));
        }
      }

      return content.raw();
    } else if (index < pageCount) {
      return coordList.get(index - listPageCount).getPage(renderer);
    }
    return null;
  }

  public void delete(int index) {
    this.file.delete(index - listPageCount);
    this.coordList = file.getAll();
    this.listPageCount = (int)Math.ceil((coordList.size()+1)/13.0);
    this.pageCount = listPageCount+coordList.size();
    CoordinateBook.lastPage = 0;
  }
}

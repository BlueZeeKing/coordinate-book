package dev.blueish.coordbook.data;

import java.util.ArrayList;
import java.util.List;

import dev.blueish.coordbook.CoordinateBook;
import dev.blueish.coordbook.util.JSONFile;
import dev.blueish.coordbook.util.TextCreator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class Book {
  public ArrayList<Coord> coordList;
  public int pageCount;
  private final JSONFile file;
  public int listPageCount;
  private ArrayList<TextCreator> content;
  private List<Coord> favorites;
  private List<Coord> other;
  private int numLines;

  public Book(MinecraftClient client) {
    this.file = new JSONFile(CoordinateBook.ClientToName(client));

    this.coordList = file.getAll();

    regen();
  }

  public Book init() {
    content = new ArrayList<TextCreator>();
    content.add(new TextCreator("Coordinate Book").format(Formatting.BOLD).center());
    if (favorites.size() > 0) {
      content.add(new TextCreator(""));
      content.add(new TextCreator("Favorites").format(Formatting.GOLD));
    }

    for (Coord coord : favorites) {
      content.add(coord.getText(coordList.indexOf(coord) + listPageCount + 1));
    }

    if (other.size() > 0) {
      content.add(new TextCreator(""));
      content.add(new TextCreator("Other").format(Formatting.DARK_GRAY));
    }

    for (Coord coord : other) {
      content.add(coord.getText(coordList.indexOf(coord) + listPageCount + 1));
    }

    return this;
  }

  public MutableText getPage(int index) {
    if (index < listPageCount) {
      TextCreator content = new TextCreator("");
      for (int i = index * 13 + (index == 0 ? 0 : 1); i < Math.min(index * 13 + 14, this.content.size()); i++) {
        if (i == index * 13) {
          content.addNoFormat(this.content.get(i));
        } else {
          content.addNewline(this.content.get(i));
        }
      }
      return content.raw();

    } else if (index < pageCount) {
      return coordList.get(index - listPageCount).getPage();
    }
    return null;
  }

  public void delete(int index) {
    this.coordList.remove(index - listPageCount);
    this.file.delete(index - listPageCount);
    regen();
    CoordinateBook.lastPage = -1;
  }

  public int toggleFavorite(int i) {
    int index = i - listPageCount;
    Coord coord = this.coordList.get(index);
    coord.favorite = !coord.favorite;
    this.file.rewrite(coordList);

    regen();
    CoordinateBook.lastPage = index + listPageCount;

    return index + listPageCount;
  }

  private void regen() {
    favorites = coordList.stream().filter(coord -> coord.favorite).toList();
    other = coordList.stream().filter(coord -> !coord.favorite).toList();

    this.numLines = 1 + (favorites.size() > 0 ? 2 + favorites.size() : 0) + (other.size() > 0 ? 2 + other.size() : 0);
    this.listPageCount = (int)Math.ceil(numLines/13.0);
    this.pageCount = listPageCount+coordList.size();
    this.init();
  }

  public void add(Coord coord) {
    this.file.put(coord);
    this.file.write();

    this.coordList.add(coord);

    regen();
  }
}

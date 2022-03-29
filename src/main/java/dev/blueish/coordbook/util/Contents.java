package dev.blueish.coordbook.util;

import net.minecraft.text.StringVisitable;
import dev.blueish.coordbook.CoordinateBook;

import java.util.ArrayList;

public class Contents {
  public ArrayList<StringVisitable> text;
  private int pageCount = 0;

  public int getPageCount() {
    return pageCount;
  };

  public StringVisitable getPage(int index) {
    if (index >= 0 && index < this.getPageCount()) {
      return this.text.get(index);
    }
    return StringVisitable.EMPTY;
  }

  public Contents(ArrayList<StringVisitable> text) {
    this.text = text;
    this.pageCount = text.size();
  }
}
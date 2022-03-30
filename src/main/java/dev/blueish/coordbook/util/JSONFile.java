package dev.blueish.coordbook.util;

import java.io.File;
import java.io.IOException;

import dev.blueish.coordbook.CoordinateBook;

public class JSONFile {
  private File file;

  public JSONFile(String name) {
    try {
      new File("./coordbook").mkdir();

      this.file = new File("./coordbook/" + name + ".json");
      this.file.createNewFile();
      
    } catch (IOException e) {
      CoordinateBook.LOGGER.error("An error occurred.");
      e.printStackTrace();
    }
  }
}

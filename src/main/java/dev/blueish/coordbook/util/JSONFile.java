package dev.blueish.coordbook.util;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import org.json.JSONException;
import net.minecraft.util.Formatting;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.ArrayList;
import dev.blueish.coordbook.util.Coord;

import dev.blueish.coordbook.CoordinateBook;

public class JSONFile {
  private String path;
  private File file;
  private String contents = "";
  private JSONArray json;
  private String name;

  public JSONFile(String name) {
    try {
      new File("./coordbook").mkdir();

      this.path = "./coordbook/" + name;
      this.name = name;

      this.file = new File(path + ".json");
      boolean created = file.createNewFile();

      Scanner reader = new Scanner(file);

      while (reader.hasNextLine()) {
        contents = contents + reader.nextLine();
      }
      reader.close();

      if (created || contents == "") {
        this.contents = "[]";
      }

      this.json = new JSONArray(contents);
    } catch (IOException e) {
      CoordinateBook.LOGGER.error("A write error occurred.");
      e.printStackTrace();
    } catch (JSONException e) {
      CoordinateBook.LOGGER.error("A json parse error occurred.");
      e.printStackTrace();
    }
  }

  public void write() {
    try {
      FileWriter writer = new FileWriter(path + ".json");
      writer.write(json.toString());
      writer.close();
    } catch (IOException e) {
      CoordinateBook.LOGGER.error("A write error occurred.");
      e.printStackTrace();
    }
  }

  public void put(Position coords, String name, Formatting color, String dimension, boolean favorite, LocalDateTime date) {
    JSONObject obj = new JSONObject();

    obj.put("coords", coords.getJSON());
    obj.put("name", name);
    obj.put("color", color.getName());
    obj.put("dimension", dimension);
    obj.put("favorite", favorite);
    obj.put("date", date.toEpochSecond(ZoneOffset.UTC));

    json.put(obj);

    this.write();
  }

  public ArrayList<Coord> getAll() {
    ArrayList<Coord> res = new ArrayList<Coord>();

    for (int i = 0; i < json.length(); i++) {
      JSONObject obj = json.getJSONObject(i);
      res.add(new Coord(
        new Position(obj.getJSONObject("coords")),
        obj.getString("name"), 
        Formatting.byName(obj.getString("color")),
        obj.getString("dimension"), obj.getBoolean("favorite"),
        LocalDateTime.ofEpochSecond(
        obj.getInt("date"), 0, ZoneOffset.UTC),
        this.name
      ));
    }

    return res;
  }

  public void delete(int index) {
    this.json.remove(index);
    this.write();
  }
}

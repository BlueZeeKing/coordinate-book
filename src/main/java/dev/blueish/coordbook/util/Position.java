package dev.blueish.coordbook.util;

import org.json.JSONObject;

public class Position {
  public int x;
  public int y;
  public int z;

  public Position(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Position(double x, double y, double z) {
    this.x = (int)x;
    this.y = (int)y;
    this.z = (int)z;
  }

  public Position(JSONObject obj) {
    this.x = obj.getInt("x");
    this.y = obj.getInt("y");
    this.z = obj.getInt("z");
  }

  public JSONObject getJSON() {
    JSONObject obj = new JSONObject();

    obj.put("x", x);
    obj.put("y", y);
    obj.put("z", z);

    return obj;
  }
}

package dev.blueish.coordbook.data;

import com.google.gson.annotations.SerializedName;

public class Position {
    @SerializedName("x")
    public int x;
    @SerializedName("y")
    public int y;
    @SerializedName("z")
    public int z;

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position(double x, double y, double z) {
        this.x = (int) x;
        this.y = (int) y;
        this.z = (int) z;
    }
}

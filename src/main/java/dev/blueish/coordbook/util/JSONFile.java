package dev.blueish.coordbook.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.blueish.coordbook.CoordinateBook;
import dev.blueish.coordbook.data.Coord;
import dev.blueish.coordbook.data.Position;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class JSONFile {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    ;

    public static Coord[] read(String name) {
        try {
            new File("./coordbook").mkdir();

            File file = new File("./coordbook/" + name + ".json");
            file.createNewFile();

            return gson.fromJson(new FileReader(file), Coord[].class);
        } catch (IOException e) {
            CoordinateBook.LOGGER.error("A write error occurred.");
            e.printStackTrace();
        }
        return new Coord[0];
    }

    public static void write(String name, Coord[] data) {
        try {
            new File("./coordbook").mkdir();

            File file = new File("./coordbook/" + name + ".json");
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            gson.toJson(data, writer);
            writer.close();

        } catch (IOException e) {
            CoordinateBook.LOGGER.error("A write error occurred.");
            e.printStackTrace();
        }
    }
}

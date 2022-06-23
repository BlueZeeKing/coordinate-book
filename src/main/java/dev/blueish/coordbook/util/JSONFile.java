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
            .registerTypeAdapter(Formatting.class, new TypeAdapter<Formatting>() {
                @Override
                public Formatting read(JsonReader reader) throws JsonParseException, IOException {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        if (reader.nextName().equals("color")) {
                            return Formatting.byName(reader.nextString());
                        }
                    }

                    return null;
                }

                @Override
                public void write(JsonWriter writer, Formatting formatting) throws JsonParseException, IOException {
                    writer.value(formatting.getName());
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                @Override
                public LocalDateTime read(JsonReader reader) throws JsonParseException, IOException {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        if (reader.nextName().equals("date")) {
                            return LocalDateTime.ofEpochSecond(reader.nextInt(), 0, ZoneOffset.UTC);
                        }
                    }

                    return null;
                }

                @Override
                public void write(JsonWriter writer, LocalDateTime date) throws JsonParseException, IOException {
                    writer.value(date.toEpochSecond(ZoneOffset.UTC));
                }
            })
            .registerTypeAdapter(Position.class, new TypeAdapter<Position>() {
                @Override
                public Position read(JsonReader reader) throws JsonParseException, IOException {
                    reader.beginObject();
                    int x = 0;
                    int y = 0;
                    int z = 0;

                    while (reader.hasNext()) {
                        switch (reader.nextName()) {
                            case "x" -> x = reader.nextInt();
                            case "y" -> y = reader.nextInt();
                            case "z" -> z = reader.nextInt();
                        }
                    }
                    return new Position(x, y, z);
                }

                @Override
                public void write(JsonWriter writer, Position position) throws JsonParseException, IOException {
                    writer.beginObject();
                    writer.name("x").value(position.x);
                    writer.name("y").value(position.y);
                    writer.name("z").value(position.z);
                    writer.endObject();
                }
            })
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

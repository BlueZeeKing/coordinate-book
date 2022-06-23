package dev.blueish.coordbook.data;

import com.google.gson.annotations.SerializedName;
import dev.blueish.coordbook.CoordinateBook;
import net.minecraft.util.Formatting;

import java.time.LocalDateTime;

import net.minecraft.text.MutableText;

import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.blueish.coordbook.util.TextCreator;

public class Coord {
    @SerializedName("coords")
    public Position coords;

    @SerializedName("name")
    public String name;

    @SerializedName("color")
    public Formatting color;

    @SerializedName("dimension")
    public String dimension;

    @SerializedName("favorite")
    public boolean favorite;

    @SerializedName("date")
    public LocalDateTime date;

    public Coord(Position coords, String name, Formatting color, String dimension) {
        this.coords = coords;
        this.name = name;
        this.color = color;
        this.dimension = dimension;
        this.favorite = false;
        this.date = LocalDateTime.now();
    }

    public TextCreator getText(int pageNum) {
        return new TextCreator(convert(dimension)).format(Formatting.GRAY).add(" ").add(new TextCreator(name).format(color).hover(String.format("%d/%d/%d", coords.x, coords.y, coords.z))).click(pageNum);
    }

    public MutableText getPage() {
        return new TextCreator(name)
            .format(color)
            .format(Formatting.BOLD)
            .center()
            .addNewline(
                new TextCreator(convert(dimension)).filler("-").add(new TextCreator(String.format("%d/%d/%d", coords.x, coords.y, coords.z)))
            ).addNewline(
                new TextCreator(date.format(DateTimeFormatter.ofPattern("MM/dd hh:mm a")))
            ).addNewline(
                new TextCreator("\n\n\n\n")
                    .add(new TextCreator(favorite ? "Remove favorite" : "Add favorite").format(favorite ? Formatting.RED : Formatting.GOLD).center().hover(favorite ? "Remove this item from your favorites" : "Add this item to the favorites menu"))
                    .addNewline()
                    .addNewline(
                        new TextCreator("Send").format(Formatting.BLUE).hover("Send to all players").send(
                                String.format("Coordinate Book: %s - %d/%d/%d", name, coords.x, coords.y, coords.z)
                            )
                            .addNoFormat("  ")
                            .addNoFormat(
                                new TextCreator("Copy").format(Formatting.BLUE).hover("Copy message to send to a player").copy(
                                    String.format("Coordinate Book: %s - %d/%d/%d", name, coords.x, coords.y, coords.z)
                                )
                            ).center()
                )
            )
            .raw();
    }

    private String convert(String input) {
        CoordinateBook.LOGGER.info(input);
        Matcher regex = Pattern.compile(".*?:(?:the_)*(.).*").matcher(input);
        if (regex.matches()) {
            CoordinateBook.LOGGER.info("Match");
            return regex.group(1).toUpperCase();
        }
        return "O";
    }
}

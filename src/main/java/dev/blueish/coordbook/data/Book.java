package dev.blueish.coordbook.data;

import dev.blueish.coordbook.CoordinateBook;
import dev.blueish.coordbook.util.JSONFile;
import dev.blueish.coordbook.util.TextCreator;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class Book {
    public ArrayList<Coord> coordList;
    public int pageCount;
    public int listPageCount;
    private ArrayList<TextCreator> content;

    public Book() {
        Coord[] arr = JSONFile.read(CoordinateBook.clientToName());
        arr = arr != null ? arr : new Coord[0];
        this.coordList = new ArrayList<>(List.of(arr));

        regen();
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
        regen();
        CoordinateBook.lastPage = -1;
    }

    public int toggleFavorite(int i) {
        int index = i - listPageCount;
        Coord coord = this.coordList.get(index);
        coord.favorite = !coord.favorite;

        regen();
        CoordinateBook.lastPage = index + listPageCount;

        return index + listPageCount;
    }

    private void regen() {
        List<Coord> favorites = coordList.stream().filter(coord -> coord.favorite).toList();
        List<Coord> other = coordList.stream().filter(coord -> !coord.favorite).toList();

        int numLines = 1 + (favorites.size() > 0 ? 2 + favorites.size() : 0) + (other.size() > 0 ? 2 + other.size() : 0);
        this.listPageCount = (int) Math.ceil(numLines / 13.0);
        this.pageCount = listPageCount + coordList.size();

        Coord[] arr = new Coord[coordList.size()];
        arr = coordList.toArray(arr);
        JSONFile.write(CoordinateBook.clientToName(), arr);

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
    }

    public void add(Coord coord) {
        this.coordList.add(coord);

        CoordinateBook.LOGGER.info(String.valueOf(coordList));

        regen();
    }
}

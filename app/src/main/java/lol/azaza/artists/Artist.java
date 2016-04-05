package lol.azaza.artists;

import java.util.List;

/**
 * Created by also0914 on 05.04.2016.
 */
public class Artist {
    long id;
    String name;
    List<String> genres;
    int tracks;
    int albums;
    String link;
    String description;
    String coverBig;
    String coverSmall;

    public Artist(long id, String name, List<String> genres, int tracks, int albums, String link, String description, String coverBig, String coverSmall) {
        this.id = id;
        this.name = name;
        this.genres = genres;
        this.tracks = tracks;
        this.albums = albums;
        this.link = link;
        this.description = description;
        this.coverBig = coverBig;
        this.coverSmall = coverSmall;
    }
}

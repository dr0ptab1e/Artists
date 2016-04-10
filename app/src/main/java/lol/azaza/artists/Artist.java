package lol.azaza.artists;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Artist implements Serializable {
    private long id;
    private String name;
    private List<String> genres;
    private int tracks;
    private int albums;
    private String link;
    private String description;

    private class Cover implements Serializable {
        public Cover(String small, String big) {
            this.small = small;
            this.big = big;
        }

        private String small;
        private String big;
    }

    private Cover cover;

    public Artist(long id, String name, String genres, int tracks, int albums, String link, String description, String coverBig, String coverSmall) {
        this.id = id;
        this.name = name;
        this.genres = Arrays.asList(genres.split("\\s*,\\s*"));
        this.tracks = tracks;
        this.albums = albums;
        this.link = link;
        this.description = description;
        this.cover = new Cover(coverSmall, coverBig);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenres() {
        return genres.toString().replaceAll("[\\[\\]]", "");
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public int getTracks() {
        return tracks;
    }

    public void setTracks(int tracks) {
        this.tracks = tracks;
    }

    public int getAlbums() {
        return albums;
    }

    public void setAlbums(int albums) {
        this.albums = albums;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverBig() {
        return cover.big;
    }

    public void setCover(Cover cover) {
        this.cover = cover;
    }

    public String getCoverSmall() {
        return cover.small;
    }

    public String getInfo() {
        return getTracks() + " tracks, " + getAlbums() + " albums";
    }
}

package com.example.scorely.data.remote;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** אובייקטי DTO לתשובות Deezer API (מטא-דאטה של אלבומים בלבד, ללא הורדת מוזיקה). */
public class DeezerDtos {

    public static class SearchResponse { public List<DeezerAlbum> data; }

    public static class DeezerAlbum {
        public long id;
        public String title;
        @SerializedName("cover_medium") public String coverMedium;
        @SerializedName("nb_tracks") public int nbTracks;
        @SerializedName("release_date") public String releaseDate;   // רק בפרטי אלבום
        public Artist artist;
        public Genres genres;                                        // רק בפרטי אלבום
    }

    public static class Artist { public String name; }
    public static class Genres { public List<Genre> data; }
    public static class Genre { public String name; }
}

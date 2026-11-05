package com.example.scorely.data.remote;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** אובייקטי DTO לתשובות RAWG API (נתוני מידע בלבד). */
public class RawgDtos {

    public static class Response { public List<RawgGame> results; }

    public static class RawgGame {
        public int id;
        public String name;
        public String released;                 // yyyy-MM-dd
        public int metacritic;
        @SerializedName("background_image") public String backgroundImage;
        public List<Named> genres;
        public List<PlatformWrapper> platforms;
        public List<Named> developers;          // מופיע רק בפרטי משחק
    }

    public static class Named { public String name; }

    public static class PlatformWrapper { public Named platform; }
}

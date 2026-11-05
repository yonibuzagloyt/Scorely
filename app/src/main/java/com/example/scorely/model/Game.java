package com.example.scorely.model;

import java.util.ArrayList;
import java.util.List;

/** משחק מחשב. */
public class Game extends MediaItem {

    private String developer;
    private final List<String> platforms = new ArrayList<>();
    private int criticScore; // ציון מבקרים 0-100

    public Game(String id, String title, long releaseDate, String coverUrl,
                String developer, int criticScore) {
        super(id, title, releaseDate, coverUrl);
        this.developer = developer;
        this.criticScore = criticScore;
    }

    public void addPlatform(String platform) { platforms.add(platform); }

    @Override
    public String getSubtitle() {
        String p = platforms.isEmpty() ? "" : " • " + String.join(", ", platforms);
        return (developer == null ? "" : developer) + p;
    }

    @Override public MediaType getType() { return MediaType.GAME; }

    public String getDeveloper() { return developer; }
    public List<String> getPlatforms() { return platforms; }
    public int getCriticScore() { return criticScore; }
}

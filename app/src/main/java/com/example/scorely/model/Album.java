package com.example.scorely.model;

/** אלבום מוזיקה. */
public class Album extends MediaItem {

    private String artist;
    private int trackCount;

    public Album(String id, String title, long releaseDate, String coverUrl,
                 String artist, int trackCount) {
        super(id, title, releaseDate, coverUrl);
        this.artist = artist;
        this.trackCount = trackCount;
    }

    @Override
    public String getSubtitle() {
        return artist + " • " + trackCount + " tracks";
    }

    @Override public MediaType getType() { return MediaType.ALBUM; }

    public String getArtist() { return artist; }
    public int getTrackCount() { return trackCount; }
}

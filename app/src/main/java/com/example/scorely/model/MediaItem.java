package com.example.scorely.model;

import java.util.HashSet;
import java.util.Set;

/**
 * מחלקה אבסטרקטית המייצגת פריט תרבות. ממנה יורשות Game ו-Album.
 * מדגימה הורשה, פולימורפיזם (getSubtitle / getType) ומימוש ממשקים.
 */
public abstract class MediaItem implements Taggable, Syncable {

    protected final String id;          // מזהה ייחודי, למשל "game_123" / "album_456"
    protected String title;             // שם הפריט
    protected long releaseDate;         // תאריך יציאה במילישניות (0 = לא ידוע)
    protected String coverUrl;          // כתובת תמונת עטיפה (להצגה בלבד)
    protected final Set<String> tags = new HashSet<>();
    protected long updatedAt;

    protected MediaItem(String id, String title, long releaseDate, String coverUrl) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.coverUrl = coverUrl;
        this.updatedAt = System.currentTimeMillis();
    }

    /** שורת משנה להצגה ברשימה (מפתח+פלטפורמות / אמן). */
    public abstract String getSubtitle();

    public abstract MediaType getType();

    public boolean isReleased() {
        return releaseDate == 0 || releaseDate <= System.currentTimeMillis();
    }

    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) tags.add(tag.trim().toLowerCase());
    }

    @Override public String getId() { return id; }
    @Override public long getUpdatedAt() { return updatedAt; }
    @Override public Set<String> getTags() { return tags; }

    public String getTitle() { return title; }
    public long getReleaseDate() { return releaseDate; }
    public String getCoverUrl() { return coverUrl; }
}

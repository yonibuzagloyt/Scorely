package com.example.scorely.model;

/** דירוג וביקורת של משתמש על פריט. דירוג בצעדים של חצי כוכב, בין 0.5 ל-5. */
public class Review implements Syncable {

    public static final float MIN_RATING = 0.5f;
    public static final float MAX_RATING = 5f;

    private final String id;
    private final String itemId;
    private final String userId;
    private float rating;
    private String text;
    private final long createdAt;
    private long updatedAt;

    public Review(String id, String itemId, String userId, float rating, String text) {
        if (!isValidRating(rating)) throw new IllegalArgumentException("Invalid rating: " + rating);
        this.id = id;
        this.itemId = itemId;
        this.userId = userId;
        this.rating = rating;
        this.text = text;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
    }

    public static boolean isValidRating(float r) {
        return r >= MIN_RATING && r <= MAX_RATING && (r * 2) == Math.round(r * 2);
    }

    public void edit(float newRating, String newText) {
        if (!isValidRating(newRating)) throw new IllegalArgumentException("Invalid rating");
        this.rating = newRating;
        this.text = newText;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override public String getId() { return id; }
    @Override public long getUpdatedAt() { return updatedAt; }
    public String getItemId() { return itemId; }
    public String getUserId() { return userId; }
    public float getRating() { return rating; }
    public String getText() { return text; }
    public long getCreatedAt() { return createdAt; }
}

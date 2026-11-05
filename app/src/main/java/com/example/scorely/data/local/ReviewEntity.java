package com.example.scorely.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** ביקורת ודירוג. השדה synced מסמן אם הועלתה ל-Firestore. */
@Entity(tableName = "reviews", indices = {@Index("itemId"), @Index("userId")})
public class ReviewEntity {
    @PrimaryKey @NonNull public String id = "";
    public String itemId;
    public String userId;
    public float rating;
    public String text;
    public long createdAt;
    public long updatedAt;
    public boolean synced;
}

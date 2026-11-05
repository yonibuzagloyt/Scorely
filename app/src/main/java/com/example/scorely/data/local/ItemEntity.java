package com.example.scorely.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** ישות Room לפריט (משחק או אלבום). השדה type קובע איזו מחלקה תיווצר ב-ItemMapper. */
@Entity(tableName = "items")
public class ItemEntity {
    @PrimaryKey @NonNull public String id = "";
    public String type;          // "GAME" / "ALBUM"
    public String title;
    public long releaseDate;
    public String coverUrl;
    public String creator;       // מפתח או אמן
    public String platformsCsv;  // רלוונטי למשחקים
    public int trackCount;       // רלוונטי לאלבומים
    public int criticScore;      // רלוונטי למשחקים
    public long updatedAt;
}

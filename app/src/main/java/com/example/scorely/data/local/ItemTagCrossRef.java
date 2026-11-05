package com.example.scorely.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

/** טבלת קישור לקשר many-to-many בין פריטים לתגיות. */
@Entity(tableName = "item_tag", primaryKeys = {"itemId", "tagId"},
        indices = {@Index("tagId")})
public class ItemTagCrossRef {
    @NonNull public String itemId = "";
    public long tagId;

    public ItemTagCrossRef(@NonNull String itemId, long tagId) {
        this.itemId = itemId;
        this.tagId = tagId;
    }
}

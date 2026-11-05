package com.example.scorely.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/** פריט ברשימה אישית של משתמש, כולל תזכורת אופציונלית ליום היציאה. */
@Entity(tableName = "list_entries", primaryKeys = {"userId", "itemId"})
public class ListEntryEntity {
    @NonNull public String userId = "";
    @NonNull public String itemId = "";
    public String status;      // שם של ListStatus
    public long addedAt;
    public long reminderAt;    // 0 = ללא תזכורת
}

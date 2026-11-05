package com.example.scorely.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** תגית (ז'אנר / אווירה) המשותפת למשחקים ולמוזיקה. */
@Entity(tableName = "tags", indices = {@Index(value = "name", unique = true)})
public class TagEntity {
    @PrimaryKey(autoGenerate = true) public long tagId;
    @NonNull public String name = "";
}

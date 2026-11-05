package com.example.scorely.data.local;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;
import java.util.List;

/** פריט יחד עם התגיות שלו (@Embedded + @Relation דרך Junction). */
public class ItemWithTags {
    @Embedded public ItemEntity item;

    @Relation(parentColumn = "id", entityColumn = "tagId",
            associateBy = @Junction(value = ItemTagCrossRef.class,
                    parentColumn = "itemId", entityColumn = "tagId"))
    public List<TagEntity> tags;
}

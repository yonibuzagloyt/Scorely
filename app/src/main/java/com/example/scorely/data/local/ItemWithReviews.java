package com.example.scorely.data.local;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

/** פריט יחד עם כל הביקורות עליו (יחס one-to-many). */
public class ItemWithReviews {
    @Embedded public ItemEntity item;

    @Relation(parentColumn = "id", entityColumn = "itemId")
    public List<ReviewEntity> reviews;
}

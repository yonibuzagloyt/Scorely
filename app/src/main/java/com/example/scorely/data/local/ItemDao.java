package com.example.scorely.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import java.util.List;

/** גישה לפריטים, תגיות, רשימות וסטטיסטיקות (JOIN ו-GROUP BY). */
@Dao
public interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(ItemEntity item);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertTag(TagEntity tag);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCrossRef(ItemTagCrossRef ref);

    @Query("SELECT * FROM tags WHERE name = :name LIMIT 1")
    TagEntity getTagByName(String name);

    @Transaction
    @Query("SELECT * FROM items WHERE title LIKE '%' || :q || '%' ORDER BY title")
    LiveData<List<ItemWithTags>> search(String q);

    @Transaction
    @Query("SELECT * FROM items WHERE id = :id")
    LiveData<ItemWithTags> getById(String id);

    @Transaction
    @Query("SELECT * FROM items")
    List<ItemWithTags> getAllSync();

    /** פריטים ברשימה אישית לפי סטטוס (JOIN בין items ל-list_entries). */
    @Transaction
    @Query("SELECT i.* FROM items i JOIN list_entries e ON e.itemId = i.id " +
           "WHERE e.userId = :uid AND e.status = :status ORDER BY e.addedAt DESC")
    LiveData<List<ItemWithTags>> getByStatus(String uid, String status);

    /** ממוצע דירוג לפי תגית (JOIN משולש + GROUP BY). */
    @Query("SELECT t.name AS tag, AVG(r.rating) AS avgRating, COUNT(r.id) AS cnt " +
           "FROM reviews r JOIN item_tag x ON r.itemId = x.itemId " +
           "JOIN tags t ON t.tagId = x.tagId WHERE r.userId = :uid " +
           "GROUP BY t.tagId ORDER BY avgRating DESC")
    LiveData<List<TagStat>> tagStats(String uid);

    /** כמה פריטים יש בכל סטטוס (GROUP BY). */
    @Query("SELECT status, COUNT(*) AS cnt FROM list_entries WHERE userId = :uid GROUP BY status")
    LiveData<List<StatusCount>> statusCounts(String uid);

    @Query("SELECT title FROM items WHERE id = :id")
    String getTitle(String id);
}

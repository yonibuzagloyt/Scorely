package com.example.scorely.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(ReviewEntity review);

    @Query("SELECT * FROM reviews WHERE itemId = :itemId ORDER BY createdAt DESC")
    LiveData<List<ReviewEntity>> forItem(String itemId);

    @Query("SELECT * FROM reviews WHERE userId = :uid")
    List<ReviewEntity> forUserSync(String uid);

    @Query("SELECT * FROM reviews WHERE userId = :uid AND itemId = :itemId LIMIT 1")
    ReviewEntity find(String uid, String itemId);

    @Query("SELECT * FROM reviews WHERE synced = 0")
    List<ReviewEntity> getUnsynced();

    @Query("UPDATE reviews SET synced = 1 WHERE id = :id")
    void markSynced(String id);

    /** התפלגות הדירוגים של המשתמש (GROUP BY). */
    @Query("SELECT CAST(ROUND(rating) AS INTEGER) AS bucket, COUNT(*) AS cnt " +
           "FROM reviews WHERE userId = :uid GROUP BY bucket ORDER BY bucket")
    LiveData<List<RatingBucket>> ratingDistribution(String uid);
}

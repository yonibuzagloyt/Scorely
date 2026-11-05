package com.example.scorely.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ListEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(ListEntryEntity entry);

    @Query("DELETE FROM list_entries WHERE userId = :uid AND itemId = :itemId")
    void remove(String uid, String itemId);

    @Query("SELECT * FROM list_entries WHERE userId = :uid AND itemId = :itemId LIMIT 1")
    ListEntryEntity find(String uid, String itemId);

    /** תזכורות עתידיות - לשחזור אחרי אתחול המכשיר. */
    @Query("SELECT * FROM list_entries WHERE reminderAt > :now")
    List<ListEntryEntity> upcomingReminders(long now);
}

package com.example.scorely.data.repository;

import androidx.lifecycle.LiveData;
import com.example.scorely.data.local.ItemWithTags;
import com.example.scorely.data.local.RatingBucket;
import com.example.scorely.data.local.ReviewEntity;
import com.example.scorely.data.local.StatusCount;
import com.example.scorely.data.local.TagStat;
import com.example.scorely.model.ListStatus;
import com.example.scorely.model.MediaItem;
import com.example.scorely.model.MediaType;
import com.example.scorely.model.Review;
import com.example.scorely.util.ResultCallback;
import java.util.List;

/**
 * ממשק המאגר (Repository): מקור אמת יחיד לשכבת ה-ViewModel.
 * מסתיר מה-ViewModel מאיפה מגיעים הנתונים (Room / API / Firestore).
 */
public interface MediaRepository {

    // --- חיפוש ופריטים ---
    LiveData<List<ItemWithTags>> searchLocal(String query);
    void searchRemote(String query, MediaType type, ResultCallback<List<MediaItem>> cb);
    LiveData<ItemWithTags> getItem(String itemId);

    // --- ביקורות ---
    LiveData<List<ReviewEntity>> getReviews(String itemId);
    void saveReview(Review review);

    // --- רשימות אישיות ---
    void setListStatus(String uid, String itemId, ListStatus status, long reminderAt);
    void removeFromList(String uid, String itemId);
    LiveData<List<ItemWithTags>> getList(String uid, ListStatus status);

    // --- סטטיסטיקות ---
    LiveData<List<TagStat>> getTagStats(String uid);
    LiveData<List<StatusCount>> getStatusCounts(String uid);
    LiveData<List<RatingBucket>> getRatingDistribution(String uid);

    // --- נתונים לאלגוריתם ההמלצות ---
    List<MediaItem> getAllItemsBlocking();
    List<ReviewEntity> getUserReviewsBlocking(String uid);

    // --- סנכרון ---
    /** מעלה ל-Firestore ביקורות שטרם סונכרנו. מחזיר את מספר הביקורות שהועלו. */
    void syncPendingReviews(ResultCallback<Integer> cb);
}

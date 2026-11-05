package com.example.scorely.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.scorely.data.local.AppDatabase;
import com.example.scorely.data.local.ItemDao;
import com.example.scorely.data.local.ItemTagCrossRef;
import com.example.scorely.data.local.ItemWithTags;
import com.example.scorely.data.local.ListEntryDao;
import com.example.scorely.data.local.ListEntryEntity;
import com.example.scorely.data.local.RatingBucket;
import com.example.scorely.data.local.ReviewDao;
import com.example.scorely.data.local.ReviewEntity;
import com.example.scorely.data.local.StatusCount;
import com.example.scorely.data.local.TagEntity;
import com.example.scorely.data.local.TagStat;
import com.example.scorely.data.remote.DeezerDtos;
import com.example.scorely.data.remote.DtoMapper;
import com.example.scorely.data.remote.FirestoreService;
import com.example.scorely.data.remote.RawgDtos;
import com.example.scorely.data.remote.RetrofitProvider;
import com.example.scorely.model.ListStatus;
import com.example.scorely.model.MediaItem;
import com.example.scorely.model.MediaType;
import com.example.scorely.model.Review;
import com.example.scorely.util.Constants;
import com.example.scorely.util.ResultCallback;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** מימוש המאגר: Room כמטמון מקומי, Retrofit להורדת מידע, Firestore לסנכרון. */
public class MediaRepositoryImpl implements MediaRepository {

    private final ItemDao itemDao;
    private final ReviewDao reviewDao;
    private final ListEntryDao listDao;
    private final FirestoreService firestore = new FirestoreService();
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    public MediaRepositoryImpl(Context ctx) {
        AppDatabase db = AppDatabase.get(ctx);
        itemDao = db.itemDao();
        reviewDao = db.reviewDao();
        listDao = db.listEntryDao();
    }

    // ---------- חיפוש ----------

    @Override public LiveData<List<ItemWithTags>> searchLocal(String q) { return itemDao.search(q); }

    @Override public LiveData<ItemWithTags> getItem(String itemId) { return itemDao.getById(itemId); }

    @Override
    public void searchRemote(String query, MediaType type, ResultCallback<List<MediaItem>> cb) {
        if (type == MediaType.GAME) {
            RetrofitProvider.rawg().searchGames(query, Constants.RAWG_API_KEY, 20)
                    .enqueue(new Callback<RawgDtos.Response>() {
                        @Override public void onResponse(Call<RawgDtos.Response> c, Response<RawgDtos.Response> r) {
                            if (!r.isSuccessful() || r.body() == null || r.body().results == null) {
                                cb.onError(new Exception("HTTP " + r.code())); return;
                            }
                            List<MediaItem> items = new ArrayList<>();
                            for (RawgDtos.RawgGame g : r.body().results) items.add(DtoMapper.toGame(g));
                            cacheItems(items);
                            cb.onSuccess(items);
                        }
                        @Override public void onFailure(Call<RawgDtos.Response> c, Throwable t) {
                            cb.onError(new Exception(t));
                        }
                    });
        } else {
            RetrofitProvider.deezer().searchAlbums(query)
                    .enqueue(new Callback<DeezerDtos.SearchResponse>() {
                        @Override public void onResponse(Call<DeezerDtos.SearchResponse> c, Response<DeezerDtos.SearchResponse> r) {
                            if (!r.isSuccessful() || r.body() == null || r.body().data == null) {
                                cb.onError(new Exception("HTTP " + r.code())); return;
                            }
                            List<MediaItem> items = new ArrayList<>();
                            for (DeezerDtos.DeezerAlbum a : r.body().data) items.add(DtoMapper.toAlbum(a));
                            cacheItems(items);
                            cb.onSuccess(items);
                        }
                        @Override public void onFailure(Call<DeezerDtos.SearchResponse> c, Throwable t) {
                            cb.onError(new Exception(t));
                        }
                    });
        }
    }

    /** שומר פריטים ותגיות ב-Room (עבודה לא מקוונת). */
    private void cacheItems(List<MediaItem> items) {
        io.execute(() -> {
            for (MediaItem m : items) {
                itemDao.insertItem(ItemMapper.toEntity(m));
                for (String tagName : m.getTags()) {
                    TagEntity tag = itemDao.getTagByName(tagName);
                    long tagId;
                    if (tag == null) {
                        TagEntity t = new TagEntity();
                        t.name = tagName;
                        tagId = itemDao.insertTag(t);
                    } else {
                        tagId = tag.tagId;
                    }
                    itemDao.insertCrossRef(new ItemTagCrossRef(m.getId(), tagId));
                }
            }
        });
    }

    // ---------- ביקורות ----------

    @Override public LiveData<List<ReviewEntity>> getReviews(String itemId) { return reviewDao.forItem(itemId); }

    @Override
    public void saveReview(Review r) {
        io.execute(() -> {
            ReviewEntity existing = reviewDao.find(r.getUserId(), r.getItemId());
            // פתרון קונפליקטים: הגרסה החדשה יותר מנצחת (last-write-wins)
            if (existing != null && existing.updatedAt > r.getUpdatedAt()) return;
            ReviewEntity e = new ReviewEntity();
            e.id = existing != null ? existing.id : r.getId();
            e.itemId = r.getItemId();
            e.userId = r.getUserId();
            e.rating = r.getRating();
            e.text = r.getText();
            e.createdAt = existing != null ? existing.createdAt : r.getCreatedAt();
            e.updatedAt = r.getUpdatedAt();
            e.synced = false;
            reviewDao.upsert(e);
        });
    }

    // ---------- רשימות ----------

    @Override
    public void setListStatus(String uid, String itemId, ListStatus status, long reminderAt) {
        io.execute(() -> {
            ListEntryEntity e = new ListEntryEntity();
            e.userId = uid;
            e.itemId = itemId;
            e.status = status.name();
            e.addedAt = System.currentTimeMillis();
            e.reminderAt = reminderAt;
            listDao.upsert(e);
        });
    }

    @Override public void removeFromList(String uid, String itemId) { io.execute(() -> listDao.remove(uid, itemId)); }

    @Override public LiveData<List<ItemWithTags>> getList(String uid, ListStatus s) { return itemDao.getByStatus(uid, s.name()); }

    // ---------- סטטיסטיקות ----------

    @Override public LiveData<List<TagStat>> getTagStats(String uid) { return itemDao.tagStats(uid); }
    @Override public LiveData<List<StatusCount>> getStatusCounts(String uid) { return itemDao.statusCounts(uid); }
    @Override public LiveData<List<RatingBucket>> getRatingDistribution(String uid) { return reviewDao.ratingDistribution(uid); }

    // ---------- נתונים לאלגוריתם (לקרוא רק מת'רד רקע) ----------

    @Override
    public List<MediaItem> getAllItemsBlocking() {
        List<MediaItem> out = new ArrayList<>();
        for (ItemWithTags row : itemDao.getAllSync()) out.add(ItemMapper.toDomain(row));
        return out;
    }

    @Override public List<ReviewEntity> getUserReviewsBlocking(String uid) { return reviewDao.forUserSync(uid); }

    // ---------- סנכרון ----------

    @Override
    public void syncPendingReviews(ResultCallback<Integer> cb) {
        io.execute(() -> {
            List<ReviewEntity> pending = reviewDao.getUnsynced();
            if (pending.isEmpty()) { cb.onSuccess(0); return; }
            AtomicInteger remaining = new AtomicInteger(pending.size());
            AtomicInteger uploaded = new AtomicInteger(0);
            for (ReviewEntity e : pending) {
                Review r = new Review(e.id, e.itemId, e.userId, e.rating, e.text);
                firestore.pushReview(r, new ResultCallback<Void>() {
                    @Override public void onSuccess(Void v) {
                        io.execute(() -> reviewDao.markSynced(e.id));
                        uploaded.incrementAndGet();
                        if (remaining.decrementAndGet() == 0) cb.onSuccess(uploaded.get());
                    }
                    @Override public void onError(Exception ex) {
                        if (remaining.decrementAndGet() == 0) cb.onSuccess(uploaded.get());
                    }
                });
            }
        });
    }
}

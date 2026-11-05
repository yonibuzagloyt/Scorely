package com.example.scorely.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.scorely.ScorelyApp;
import com.example.scorely.data.local.RatingBucket;
import com.example.scorely.data.local.ReviewEntity;
import com.example.scorely.data.local.StatusCount;
import com.example.scorely.data.local.TagStat;
import com.example.scorely.data.repository.MediaRepository;
import com.example.scorely.logic.RecommendationEngine;
import com.example.scorely.logic.TasteVector;
import com.example.scorely.model.MediaItem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** לוגיקת מסך הפרופיל: סטטיסטיקות והמלצות אישיות. */
public class ProfileViewModel extends AndroidViewModel {

    private final MediaRepository repo;
    private final RecommendationEngine engine = new RecommendationEngine();
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<RecommendationEngine.ScoredItem>> recommendations = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application app) {
        super(app);
        repo = ((ScorelyApp) app).getRepository();
    }

    public LiveData<List<TagStat>> getTagStats(String uid) { return repo.getTagStats(uid); }
    public LiveData<List<StatusCount>> getStatusCounts(String uid) { return repo.getStatusCounts(uid); }
    public LiveData<List<RatingBucket>> getRatingDistribution(String uid) { return repo.getRatingDistribution(uid); }
    public LiveData<List<RecommendationEngine.ScoredItem>> getRecommendations() { return recommendations; }

    /** מחשב המלצות בת'רד רקע ומפרסם את התוצאה. */
    public void loadRecommendations(String uid, int n) {
        io.execute(() -> {
            List<MediaItem> all = repo.getAllItemsBlocking();
            List<ReviewEntity> reviews = repo.getUserReviewsBlocking(uid);

            Map<String, Set<String>> tagsById = new HashMap<>();
            for (MediaItem m : all) tagsById.put(m.getId(), m.getTags());

            Map<String, Float> ratings = new HashMap<>();
            Set<String> rated = new HashSet<>();
            for (ReviewEntity r : reviews) { ratings.put(r.itemId, r.rating); rated.add(r.itemId); }

            TasteVector taste = engine.buildTasteVector(ratings, tagsById);
            recommendations.postValue(engine.recommend(taste, all, rated, n));
        });
    }
}

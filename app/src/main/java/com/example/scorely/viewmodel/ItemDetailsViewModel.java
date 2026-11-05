package com.example.scorely.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.scorely.ScorelyApp;
import com.example.scorely.data.local.ItemWithTags;
import com.example.scorely.data.local.ReviewEntity;
import com.example.scorely.data.repository.MediaRepository;
import com.example.scorely.model.ListStatus;
import com.example.scorely.model.Review;
import java.util.List;
import java.util.UUID;

/** לוגיקת דף הפריט: דירוג, כתיבת ביקורת והוספה לרשימה. */
public class ItemDetailsViewModel extends AndroidViewModel {

    private final MediaRepository repo;

    public ItemDetailsViewModel(@NonNull Application app) {
        super(app);
        repo = ((ScorelyApp) app).getRepository();
    }

    public LiveData<ItemWithTags> getItem(String itemId) { return repo.getItem(itemId); }

    public LiveData<List<ReviewEntity>> getReviews(String itemId) { return repo.getReviews(itemId); }

    /** @return false אם הדירוג אינו תקין. */
    public boolean submitReview(String uid, String itemId, float rating, String text) {
        if (!Review.isValidRating(rating)) return false;
        repo.saveReview(new Review(UUID.randomUUID().toString(), itemId, uid, rating, text));
        return true;
    }

    public void addToList(String uid, String itemId, ListStatus status, long reminderAt) {
        repo.setListStatus(uid, itemId, status, reminderAt);
    }

    public void removeFromList(String uid, String itemId) { repo.removeFromList(uid, itemId); }
}

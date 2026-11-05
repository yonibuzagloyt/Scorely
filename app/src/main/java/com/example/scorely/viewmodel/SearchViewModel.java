package com.example.scorely.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.scorely.ScorelyApp;
import com.example.scorely.data.local.ItemWithTags;
import com.example.scorely.data.repository.MediaRepository;
import com.example.scorely.model.MediaItem;
import com.example.scorely.model.MediaType;
import com.example.scorely.util.ResultCallback;
import java.util.List;

/** לוגיקת מסך החיפוש: חיפוש ב-API, שמירה ב-Room והצגת התוצאות מה-Room. */
public class SearchViewModel extends AndroidViewModel {

    private final MediaRepository repo;
    private final MutableLiveData<String> query = new MutableLiveData<>("");
    private final MutableLiveData<UiState> state = new MutableLiveData<>(UiState.IDLE);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final LiveData<List<ItemWithTags>> results;

    public SearchViewModel(@NonNull Application app) {
        super(app);
        repo = ((ScorelyApp) app).getRepository();
        results = Transformations.switchMap(query, repo::searchLocal);
    }

    public void search(String q, MediaType type) {
        query.postValue(q);
        if (q == null || q.trim().length() < 2) { state.postValue(UiState.IDLE); return; }
        state.postValue(UiState.LOADING);
        repo.searchRemote(q.trim(), type, new ResultCallback<List<MediaItem>>() {
            @Override public void onSuccess(List<MediaItem> items) {
                state.postValue(items.isEmpty() ? UiState.EMPTY : UiState.SUCCESS);
            }
            @Override public void onError(Exception e) {
                errorMessage.postValue(e.getMessage());
                state.postValue(UiState.ERROR); // התוצאות המקומיות ממשיכות להופיע
            }
        });
    }

    public LiveData<List<ItemWithTags>> getResults() { return results; }
    public LiveData<UiState> getState() { return state; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}

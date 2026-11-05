package com.example.scorely.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.scorely.R;
import com.example.scorely.data.repository.ItemMapper;
import com.example.scorely.data.local.ItemWithTags;
import com.example.scorely.model.MediaItem;
import com.example.scorely.model.MediaType;
import com.example.scorely.util.Constants;
import com.example.scorely.viewmodel.SearchViewModel;
import java.util.ArrayList;
import java.util.List;

/** מסך חיפוש משחקים ואלבומים: TextWatcher עם השהיה (debounce) כדי לא להציף את ה-API. */
public class SearchFragment extends Fragment {

    private static final long DEBOUNCE_MS = 500;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pending;
    private SearchViewModel vm;
    private EditText etSearch;
    private RadioButton rbGame;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        return inf.inflate(R.layout.fragment_search, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        vm = new ViewModelProvider(this).get(SearchViewModel.class);
        etSearch = v.findViewById(R.id.etSearch);
        rbGame = v.findViewById(R.id.rbGame);
        ProgressBar progress = v.findViewById(R.id.progress);
        TextView txtStatus = v.findViewById(R.id.txtStatus);
        RecyclerView rv = v.findViewById(R.id.rvResults);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        ItemAdapter adapter = new ItemAdapter(item -> {
            Intent i = new Intent(requireContext(), ItemDetailsActivity.class);
            i.putExtra(Constants.EXTRA_ITEM_ID, item.getId());
            startActivity(i);
        });
        rv.setAdapter(adapter);

        vm.getResults().observe(getViewLifecycleOwner(), rows -> {
            List<MediaItem> items = new ArrayList<>();
            if (rows != null) for (ItemWithTags r : rows) items.add(ItemMapper.toDomain(r));
            adapter.submit(items);
        });

        vm.getState().observe(getViewLifecycleOwner(), st -> {
            progress.setVisibility(st == com.example.scorely.viewmodel.UiState.LOADING ? View.VISIBLE : View.GONE);
            switch (st) {
                case EMPTY: txtStatus.setText("לא נמצאו תוצאות"); break;
                case ERROR: txtStatus.setText("שגיאת רשת - מוצגות תוצאות שמורות"); break;
                default: txtStatus.setText(""); break;
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence a, int b, int c, int d) { }
            @Override public void onTextChanged(CharSequence a, int b, int c, int d) { }
            @Override public void afterTextChanged(Editable e) {
                if (pending != null) handler.removeCallbacks(pending);
                pending = () -> vm.search(e.toString(), rbGame.isChecked() ? MediaType.GAME : MediaType.ALBUM);
                handler.postDelayed(pending, DEBOUNCE_MS);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pending != null) handler.removeCallbacks(pending);
    }
}

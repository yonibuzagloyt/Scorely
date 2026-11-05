package com.example.scorely.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.scorely.R;
import com.example.scorely.data.remote.FirestoreService;
import com.example.scorely.logic.RecommendationEngine;
import com.example.scorely.model.MediaItem;
import com.example.scorely.util.Constants;
import com.example.scorely.util.PrefsManager;
import com.example.scorely.viewmodel.ProfileViewModel;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** מסך הבית: המלצות אישיות + פיד ביקורות של חברים בזמן אמת (Firestore Snapshot Listener). */
public class HomeFragment extends Fragment {

    private ListenerRegistration feedRegistration;
    private TextView txtFeed;
    private String uid;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        return inf.inflate(R.layout.fragment_home, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        uid = new PrefsManager(requireContext()).getUserId();
        txtFeed = v.findViewById(R.id.txtFeed);
        RecyclerView rv = v.findViewById(R.id.rvRecommendations);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        ItemAdapter adapter = new ItemAdapter(item -> {
            Intent i = new Intent(requireContext(), ItemDetailsActivity.class);
            i.putExtra(Constants.EXTRA_ITEM_ID, item.getId());
            startActivity(i);
        });
        rv.setAdapter(adapter);

        ProfileViewModel vm = new ViewModelProvider(this).get(ProfileViewModel.class);
        vm.getRecommendations().observe(getViewLifecycleOwner(), scored -> {
            List<MediaItem> items = new ArrayList<>();
            for (RecommendationEngine.ScoredItem si : scored) items.add(si.item);
            adapter.submit(items);
        });
        vm.loadRecommendations(uid, 10);
    }

    @Override
    public void onStart() {
        super.onStart();
        // TODO: לטעון את רשימת העוקבים של המשתמש מ-Firestore ולהעבירה לכאן
        List<String> following = new ArrayList<>();
        feedRegistration = new FirestoreService().listenToFeed(following, new FirestoreService.FeedListener() {
            @Override public void onFeedChanged(List<Map<String, Object>> reviews) {
                txtFeed.setText(reviews.isEmpty() ? "אין עדיין פעילות מחברים"
                        : reviews.size() + " ביקורות חדשות מחברים");
            }
            @Override public void onError(Exception e) { txtFeed.setText("שגיאה בטעינת הפיד"); }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (feedRegistration != null) feedRegistration.remove(); // מניעת דליפת מאזין
    }
}

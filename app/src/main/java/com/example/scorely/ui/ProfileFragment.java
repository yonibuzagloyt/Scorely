package com.example.scorely.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.scorely.R;
import com.example.scorely.data.local.RatingBucket;
import com.example.scorely.data.local.StatusCount;
import com.example.scorely.data.local.TagStat;
import com.example.scorely.model.ListStatus;
import com.example.scorely.util.PrefsManager;
import com.example.scorely.viewmodel.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** פרופיל: סטטיסטיקות (JOIN/GROUP BY), גרף התפלגות דירוגים, תמונת פרופיל (מצלמה/גלריה) והגדרות. */
public class ProfileFragment extends Fragment {

    private ImageView imgProfile;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(), (Uri uri) -> {
                if (uri != null) imgProfile.setImageURI(uri);
            });

    private final ActivityResultLauncher<Void> takePhoto = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(), (Bitmap bmp) -> {
                if (bmp != null) imgProfile.setImageBitmap(bmp);
            });

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        return inf.inflate(R.layout.fragment_profile, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        String uid = new PrefsManager(requireContext()).getUserId();
        ProfileViewModel vm = new ViewModelProvider(this).get(ProfileViewModel.class);

        imgProfile = v.findViewById(R.id.imgProfile);
        TextView txtStatus = v.findViewById(R.id.txtStatusCounts);
        TextView txtTags = v.findViewById(R.id.txtTopTags);
        StatsChartView chart = v.findViewById(R.id.chartRatings);
        Button btnSettings = v.findViewById(R.id.btnSettings);
        Button btnLogout = v.findViewById(R.id.btnLogout);

        imgProfile.setOnClickListener(x -> new AlertDialog.Builder(requireContext())
                .setItems(new String[]{"מצלמה", "גלריה"}, (d, which) -> {
                    if (which == 0) takePhoto.launch(null); else pickImage.launch("image/*");
                }).show());

        vm.getStatusCounts(uid).observe(getViewLifecycleOwner(), (List<StatusCount> rows) -> {
            StringBuilder sb = new StringBuilder();
            for (StatusCount sc : rows)
                sb.append(ListStatus.valueOf(sc.status).getLabel()).append(": ").append(sc.cnt).append("   ");
            txtStatus.setText(sb.toString());
        });

        vm.getTagStats(uid).observe(getViewLifecycleOwner(), (List<TagStat> rows) -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(5, rows.size()); i++)
                sb.append(rows.get(i).tag).append(" (")
                  .append(String.format(Locale.US, "%.1f", rows.get(i).avgRating)).append(")  ");
            txtTags.setText(sb.toString());
        });

        vm.getRatingDistribution(uid).observe(getViewLifecycleOwner(), (List<RatingBucket> rows) -> {
            List<StatsChartView.Bar> bars = new ArrayList<>();
            for (RatingBucket b : rows) bars.add(new StatsChartView.Bar(b.bucket + "★", b.cnt));
            chart.setData(bars);
        });

        btnSettings.setOnClickListener(x -> startActivity(new Intent(requireContext(), SettingsActivity.class)));
        btnLogout.setOnClickListener(x -> {
            FirebaseAuth.getInstance().signOut();
            new PrefsManager(requireContext()).setUserId(null);
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });
    }
}

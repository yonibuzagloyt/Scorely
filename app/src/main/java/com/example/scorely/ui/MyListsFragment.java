package com.example.scorely.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.scorely.R;
import com.example.scorely.ScorelyApp;
import com.example.scorely.data.local.ItemWithTags;
import com.example.scorely.data.repository.ItemMapper;
import com.example.scorely.data.repository.MediaRepository;
import com.example.scorely.model.ListStatus;
import com.example.scorely.model.MediaItem;
import com.example.scorely.util.Constants;
import com.example.scorely.util.PrefsManager;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

/** הרשימות שלי: לשונית לכל סטטוס (ListStatus). */
public class MyListsFragment extends Fragment {

    private MediaRepository repo;
    private String uid;
    private ItemAdapter adapter;
    private LiveData<List<ItemWithTags>> current;
    private final Observer<List<ItemWithTags>> observer = rows -> {
        List<MediaItem> items = new ArrayList<>();
        if (rows != null) for (ItemWithTags r : rows) items.add(ItemMapper.toDomain(r));
        adapter.submit(items);
    };

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        return inf.inflate(R.layout.fragment_my_lists, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        repo = ((ScorelyApp) requireActivity().getApplication()).getRepository();
        uid = new PrefsManager(requireContext()).getUserId();

        RecyclerView rv = v.findViewById(R.id.rvList);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ItemAdapter(item -> {
            Intent i = new Intent(requireContext(), ItemDetailsActivity.class);
            i.putExtra(Constants.EXTRA_ITEM_ID, item.getId());
            startActivity(i);
        });
        rv.setAdapter(adapter);

        TabLayout tabs = v.findViewById(R.id.tabStatus);
        for (ListStatus st : ListStatus.values()) tabs.addTab(tabs.newTab().setText(st.getLabel()));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) { show(ListStatus.values()[tab.getPosition()]); }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });
        show(ListStatus.values()[0]);
    }

    private void show(ListStatus status) {
        if (current != null) current.removeObserver(observer);
        current = repo.getList(uid, status);
        current.observe(getViewLifecycleOwner(), observer);
    }
}

package com.example.scorely.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.scorely.R;
import com.example.scorely.model.MediaItem;
import java.util.ArrayList;
import java.util.List;

/** מתאם RecyclerView להצגת רשימת פריטים (משחקים ואלבומים) - פולימורפיזם דרך getSubtitle(). */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.VH> {

    /** מאזין ללחיצה על פריט. */
    public interface OnItemClickListener { void onItemClick(MediaItem item); }

    private final List<MediaItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public ItemAdapter(OnItemClickListener listener) { this.listener = listener; }

    public void submit(List<MediaItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged(); // לשיפור: DiffUtil
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        MediaItem m = items.get(position);
        h.title.setText(m.getTitle());
        h.subtitle.setText(m.getSubtitle());
        h.tags.setText(String.join(" · ", m.getTags()));
        Glide.with(h.cover).load(m.getCoverUrl()).centerCrop().into(h.cover);
        h.itemView.setOnClickListener(v -> listener.onItemClick(m));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final ImageView cover;
        final TextView title, subtitle, tags;
        VH(View v) {
            super(v);
            cover = v.findViewById(R.id.itemCover);
            title = v.findViewById(R.id.itemTitle);
            subtitle = v.findViewById(R.id.itemSubtitle);
            tags = v.findViewById(R.id.itemTags);
        }
    }
}

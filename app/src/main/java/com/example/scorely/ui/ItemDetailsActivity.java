package com.example.scorely.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.scorely.R;
import com.example.scorely.background.AlarmScheduler;
import com.example.scorely.data.local.ItemWithTags;
import com.example.scorely.data.repository.ItemMapper;
import com.example.scorely.model.ListStatus;
import com.example.scorely.model.MediaItem;
import com.example.scorely.util.Constants;
import com.example.scorely.util.PrefsManager;
import com.example.scorely.viewmodel.ItemDetailsViewModel;
import java.util.Calendar;

/** דף פריט: פרטים, דירוג בכוכבים, ביקורת, הוספה לרשימה ותזכורת יום יציאה. */
public class ItemDetailsActivity extends AppCompatActivity {

    private ItemDetailsViewModel vm;
    private MediaItem current;
    private String itemId, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        itemId = getIntent().getStringExtra(Constants.EXTRA_ITEM_ID);
        uid = new PrefsManager(this).getUserId();
        vm = new ViewModelProvider(this).get(ItemDetailsViewModel.class);

        TextView txtTitle = findViewById(R.id.txtTitle);
        TextView txtSubtitle = findViewById(R.id.txtSubtitle);
        StarRatingView stars = findViewById(R.id.starRating);
        EditText etReview = findViewById(R.id.etReview);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnAddList = findViewById(R.id.btnAddList);
        Button btnReminder = findViewById(R.id.btnReminder);

        vm.getItem(itemId).observe(this, (ItemWithTags row) -> {
            if (row == null) return;
            current = ItemMapper.toDomain(row);
            txtTitle.setText(current.getTitle());
            txtSubtitle.setText(current.getSubtitle());
            btnReminder.setEnabled(!current.isReleased()); // תזכורת רק לפריט שטרם יצא
        });

        btnSave.setOnClickListener(v -> {
            boolean ok = vm.submitReview(uid, itemId, stars.getRating(), etReview.getText().toString());
            Toast.makeText(this, ok ? "הביקורת נשמרה" : "בחר דירוג", Toast.LENGTH_SHORT).show();
        });

        btnAddList.setOnClickListener(v -> showStatusDialog());
        btnReminder.setOnClickListener(v -> showReminderPicker());
    }

    /** תיבת דו-שיח לבחירת סטטוס ברשימה. */
    private void showStatusDialog() {
        ListStatus[] all = ListStatus.values();
        String[] labels = new String[all.length];
        for (int i = 0; i < all.length; i++) labels[i] = all[i].getLabel();
        new AlertDialog.Builder(this)
                .setTitle("הוסף לרשימה")
                .setItems(labels, (d, which) -> vm.addToList(uid, itemId, all[which], 0))
                .show();
    }

    /** DatePickerDialog לבחירת תאריך התזכורת וקביעת AlarmManager. */
    private void showReminderPicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            Calendar when = Calendar.getInstance();
            when.set(y, m, d, 9, 0, 0);
            AlarmScheduler.schedule(this, itemId, current.getTitle(), when.getTimeInMillis());
            vm.addToList(uid, itemId, ListStatus.WANT, when.getTimeInMillis());
            Toast.makeText(this, "התזכורת נקבעה", Toast.LENGTH_SHORT).show();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
}

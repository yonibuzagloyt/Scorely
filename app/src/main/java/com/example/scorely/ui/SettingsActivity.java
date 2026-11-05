package com.example.scorely.ui;

import android.os.Bundle;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.os.LocaleListCompat;
import com.example.scorely.R;
import com.example.scorely.util.PrefsManager;

/** הגדרות: מצב כהה ושפה. הערכים נשמרים ב-SharedPreferences. */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        PrefsManager prefs = new PrefsManager(this);

        SwitchCompat swDark = findViewById(R.id.swDark);
        RadioButton rbHe = findViewById(R.id.rbHebrew);
        RadioButton rbEn = findViewById(R.id.rbEnglish);

        swDark.setChecked(prefs.isDarkMode());
        rbHe.setChecked("he".equals(prefs.getLanguage()));
        rbEn.setChecked("en".equals(prefs.getLanguage()));

        swDark.setOnCheckedChangeListener((b, on) -> {
            prefs.setDarkMode(on);
            AppCompatDelegate.setDefaultNightMode(on
                    ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
        rbHe.setOnClickListener(v -> setLang(prefs, "he"));
        rbEn.setOnClickListener(v -> setLang(prefs, "en"));
    }

    private void setLang(PrefsManager prefs, String lang) {
        prefs.setLanguage(lang);
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(lang));
    }
}

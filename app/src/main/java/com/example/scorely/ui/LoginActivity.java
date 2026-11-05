package com.example.scorely.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.scorely.R;
import com.example.scorely.util.PrefsManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

/** מסך התחברות והרשמה (Firebase Authentication). */
public class LoginActivity extends AppCompatActivity {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (auth.getCurrentUser() != null) { goToMain(auth.getCurrentUser()); return; }
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {
            if (!validate()) return;
            auth.signInWithEmailAndPassword(text(etEmail), text(etPassword))
                    .addOnSuccessListener(r -> goToMain(r.getUser()))
                    .addOnFailureListener(e -> toast(e.getMessage()));
        });

        btnRegister.setOnClickListener(v -> {
            if (!validate()) return;
            auth.createUserWithEmailAndPassword(text(etEmail), text(etPassword))
                    .addOnSuccessListener(r -> {
                        FirebaseUser u = r.getUser();
                        Map<String, Object> profile = new HashMap<>();
                        profile.put("displayName", text(etEmail).split("@")[0]);
                        profile.put("following", new java.util.ArrayList<String>());
                        FirebaseFirestore.getInstance().collection("users").document(u.getUid()).set(profile);
                        goToMain(u);
                    })
                    .addOnFailureListener(e -> toast(e.getMessage()));
        });
    }

    private boolean validate() {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(text(etEmail)).matches()) {
            etEmail.setError("אימייל לא תקין"); return false;
        }
        if (text(etPassword).length() < 6) {
            etPassword.setError("לפחות 6 תווים"); return false;
        }
        return true;
    }

    private String text(EditText e) { return e.getText().toString().trim(); }

    private void toast(String m) { Toast.makeText(this, m, Toast.LENGTH_LONG).show(); }

    private void goToMain(FirebaseUser u) {
        new PrefsManager(this).setUserId(u.getUid());
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

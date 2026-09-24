package com.korkutsoftware.ughmedya;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.korkutsoftware.ughmedya.config.MaintenanceActivity;
import com.korkutsoftware.ughmedya.config.UpdateActivity;
import com.korkutsoftware.ughmedya.login.LoginActivity;
import com.korkutsoftware.ughmedya.login.ProfileAddActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView versionTv = findViewById(R.id.versionTv);
        String versionName = BuildConfig.VERSION_NAME;
        versionTv.setText("Versiyon : " + versionName);

        checkFirebaseConfig();
    }

    private void checkFirebaseConfig() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("config").document("ughadminapp");

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Boolean uygulamaDurumu = document.getBoolean("uygulamaDurumu");
                    String bakimBaslik = document.getString("uygulamaDurumuBasligi");
                    String bakimAciklama = document.getString("uygulamaDurumuAciklamasi");
                    String bakimTarih = document.getString("uygulamaDurumuTarih");
                    String guncelVersiyon = document.getString("UygulamaVersiyon");
                    String versiyonAciklama = document.getString("UygulamaVersiyonAciklamasi");

                    if (uygulamaDurumu != null && uygulamaDurumu) {
                        Intent intent = new Intent(MainActivity.this, MaintenanceActivity.class);
                        intent.putExtra("baslik", bakimBaslik);
                        intent.putExtra("aciklama", bakimAciklama);
                        intent.putExtra("tarih", bakimTarih);
                        startActivity(intent);
                        finish();
                    } else if (guncelVersiyon != null && !guncelVersiyon.isEmpty() && !guncelVersiyon.equals(BuildConfig.VERSION_NAME)) {
                        Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                        intent.putExtra("versiyon", guncelVersiyon);
                        intent.putExtra("aciklama", versiyonAciklama);
                        startActivity(intent);

                        checkUserLogin();
                    } else {
                        checkUserLogin();
                    }
                } else {
                    checkUserLogin();
                }
            } else {
                checkUserLogin();
            }
        });
    }

    private void checkUserLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {

            FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String fullName = document.getString("fullName");
                                String username = document.getString("username");
                                String email = document.getString("email");
                                String birthDate = document.getString("birthDate");

                                if (fullName == null || username == null || email == null || birthDate == null) {
                                    // Bazı bilgiler eksik, profil tamamlama ekranına yönlendir
                                    Intent intent = new Intent(MainActivity.this, ProfileAddActivity.class);
                                    intent.putExtra("name", fullName);
                                    intent.putExtra("email", email);
                                    intent.putExtra("birthDate", birthDate);
                                    startActivity(intent);
                                } else {
                                    startActivity(new Intent(MainActivity.this, DashboardAdminActivity.class));
                                }
                            } else {

                                startActivity(new Intent(MainActivity.this, ProfileAddActivity.class));
                            }
                        } else {

                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                        finish();
                    });
        }
    }
}

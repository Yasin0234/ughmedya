package com.korkutsoftware.ughmedya.config;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.korkutsoftware.ughmedya.R;

public class UpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView versionTv = findViewById(R.id.versionTv);
        TextView featuresTv = findViewById(R.id.featuresTv);
        TextView laterTv = findViewById(R.id.laterTv);

        String versiyon = getIntent().getStringExtra("versiyon");
        String aciklama = getIntent().getStringExtra("aciklama");

        if (versiyon != null) versionTv.setText("Versiyon : " + versiyon);
        if (aciklama != null) featuresTv.setText(aciklama);

        laterTv.setOnClickListener(v -> finish());
    }
}

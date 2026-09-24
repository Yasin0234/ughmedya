package com.korkutsoftware.ughmedya.config;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.korkutsoftware.ughmedya.R;

public class MaintenanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maintenance);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView titleTv = findViewById(R.id.titleTv);
        TextView descTv = findViewById(R.id.descTv);
        TextView endDateTv = findViewById(R.id.endDateTv);

        String baslik = getIntent().getStringExtra("baslik");
        String aciklama = getIntent().getStringExtra("aciklama");
        String tarih = getIntent().getStringExtra("tarih");

        if (baslik != null) titleTv.setText(baslik);
        if (aciklama != null) descTv.setText(aciklama);
        if (tarih != null) endDateTv.setText(tarih);

        findViewById(R.id.closeBtn).setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });
    }
}

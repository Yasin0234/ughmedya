package com.korkutsoftware.ughmedya.ughhaber;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.korkutsoftware.ughmedya.R;
import com.korkutsoftware.ughmedya.ughhaber.fragments.EditFragment;
import com.korkutsoftware.ughmedya.ughhaber.fragments.HaberlerFragment;
import com.korkutsoftware.ughmedya.ughhaber.fragments.HomeFragment;
import com.korkutsoftware.ughmedya.ughhaber.fragments.KategorilerFragment;
import com.korkutsoftware.ughmedya.ughhaber.fragments.PaylasFragment;

public class DashboardHaber extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_haber);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        
        // Initial fragment
        loadFragment(new HomeFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_haberler) {
                selectedFragment = new HaberlerFragment();
            } else if (itemId == R.id.nav_edit) {
                selectedFragment = new EditFragment();
            } else if (itemId == R.id.nav_paylas) {
                selectedFragment = new PaylasFragment();
            } else if (itemId == R.id.nav_kategoriler) {
                selectedFragment = new KategorilerFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    public void switchToEditFragment(String title, String content, String mediaUrl, String source) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_edit);
        loadFragment(EditFragment.newInstance(title, content, mediaUrl, source));
    }
}
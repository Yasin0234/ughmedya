package com.korkutsoftware.ughmedya.ughhaber.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.korkutsoftware.ughmedya.R;
import com.korkutsoftware.ughmedya.ughhaber.adapters.HaberlerPagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class HaberlerFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_haberler, container, false);
        
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);

        HaberlerPagerAdapter adapter = new HaberlerPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Haber Kaynakları");
            } else {
                tab.setText("Sistem Verisi");
            }
        }).attach();

        return view;
    }
}
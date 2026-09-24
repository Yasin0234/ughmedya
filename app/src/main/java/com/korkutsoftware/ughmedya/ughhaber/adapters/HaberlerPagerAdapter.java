package com.korkutsoftware.ughmedya.ughhaber.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.korkutsoftware.ughmedya.ughhaber.fragments.NewsSourcesFragment;
import com.korkutsoftware.ughmedya.ughhaber.fragments.SystemDataFragment;

public class HaberlerPagerAdapter extends FragmentStateAdapter {

    public HaberlerPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new SystemDataFragment();
        }
        return new NewsSourcesFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
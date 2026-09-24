package com.korkutsoftware.ughmedya;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.korkutsoftware.ughmedya.ughhaber.DashboardHaber;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        view.findViewById(R.id.cardHaber).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DashboardHaber.class);
            startActivity(intent);
        });

        return view;
    }
}
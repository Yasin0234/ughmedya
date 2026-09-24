package com.korkutsoftware.ughmedya;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsFragment extends Fragment {

    private TextView versionTv;
    private MaterialButton updateBtn, clearCacheBtn;
    private MaterialSwitch gallerySwitch, notificationSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        versionTv = view.findViewById(R.id.settingsVersionTv);
        updateBtn = view.findViewById(R.id.updateBtn);
        clearCacheBtn = view.findViewById(R.id.clearCacheBtn);
        gallerySwitch = view.findViewById(R.id.gallerySwitch);
        notificationSwitch = view.findViewById(R.id.notificationSwitch);

        setVersionInfo();

        updateBtn.setOnClickListener(v -> Toast.makeText(getContext(), "Güncellemeler denetleniyor...", Toast.LENGTH_SHORT).show());
        clearCacheBtn.setOnClickListener(v -> Toast.makeText(getContext(), "Önbellek temizlendi", Toast.LENGTH_SHORT).show());

        return view;
    }

    private void setVersionInfo() {
        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            versionTv.setText("Versiyon " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
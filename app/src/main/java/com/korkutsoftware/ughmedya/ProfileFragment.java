package com.korkutsoftware.ughmedya;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.korkutsoftware.ughmedya.login.LoginActivity;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private ShapeableImageView profileIv;
    private TextView usernameTv, emailTv;
    private TextInputEditText nameEt, usernameEt, birthDateEt;
    private MaterialAutoCompleteTextView unitAtv;
    private MaterialButton updateBtn, logoutBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        profileIv = view.findViewById(R.id.profileIv);
        usernameTv = view.findViewById(R.id.usernameTv);
        emailTv = view.findViewById(R.id.emailTv);
        nameEt = view.findViewById(R.id.nameEt);
        usernameEt = view.findViewById(R.id.usernameEt);
        birthDateEt = view.findViewById(R.id.birthDateEt);
        unitAtv = view.findViewById(R.id.unitAtv);
        updateBtn = view.findViewById(R.id.updateBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);

        loadUserData();

        birthDateEt.setOnClickListener(v -> {
            com.google.android.material.datepicker.MaterialDatePicker<Long> datePicker = com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Doğum Tarihinizi Seçin")
                    .setSelection(com.google.android.material.datepicker.MaterialDatePicker.todayInUtcMilliseconds())
                    .setTheme(R.style.ThemeOverlay_Ughmedya_DatePicker)
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                java.util.Calendar calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(selection);
                java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                String formattedDate = format.format(calendar.getTime());
                birthDateEt.setText(formattedDate);
            });

            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        });

        updateBtn.setOnClickListener(v -> updateProfile());
        
        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserData() {
        String uid = mAuth.getUid();
        if (uid == null) return;

        db.collection("users").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    String fullName = doc.getString("fullName");
                    String username = doc.getString("username");
                    String email = doc.getString("email");
                    String birthDate = doc.getString("birthDate");
                    String unit = doc.getString("unit");
                    String profileImageUrl = doc.getString("profileImageUrl");

                    usernameTv.setText(username);
                    emailTv.setText(email);
                    nameEt.setText(fullName);
                    usernameEt.setText(username);
                    birthDateEt.setText(birthDate);
                    unitAtv.setText(unit, false);
                    
                    if (fullName == null || email == null || birthDate == null || username == null || unit == null) {
                        Toast.makeText(getContext(), "Lütfen eksik bilgilerinizi güncelleyin!", Toast.LENGTH_LONG).show();
                    }

                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        com.bumptech.glide.Glide.with(this).load(profileImageUrl).into(profileIv);
                    }
                }
            }
        });
    }

    private void updateProfile() {
        String uid = mAuth.getUid();
        if (uid == null) return;

        String name = nameEt.getText().toString().trim();
        String username = usernameEt.getText().toString().trim();
        String birthDate = birthDateEt.getText().toString().trim();
        String unit = unitAtv.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(birthDate) || TextUtils.isEmpty(unit)) {
            Toast.makeText(getContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        updateBtn.setEnabled(false);

        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", name);
        updates.put("username", username);
        updates.put("birthDate", birthDate);
        updates.put("unit", unit);

        db.collection("users").document(uid).update(updates).addOnCompleteListener(task -> {
            updateBtn.setEnabled(true);
            if (task.isSuccessful()) {
                usernameTv.setText(username);
                Toast.makeText(getContext(), "Profil başarıyla güncellendi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Güncelleme hatası: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

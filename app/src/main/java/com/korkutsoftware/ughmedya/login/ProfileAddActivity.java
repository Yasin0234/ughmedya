package com.korkutsoftware.ughmedya.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.korkutsoftware.ughmedya.DashboardAdminActivity;
import com.korkutsoftware.ughmedya.R;

import java.util.HashMap;
import java.util.Map;

public class ProfileAddActivity extends AppCompatActivity {

    private TextInputEditText usernameEt, nameEt, emailEt, birthDateEt;
    private MaterialAutoCompleteTextView unitAtv;
    private MaterialButton completeBtn;
    private ImageView profileIv;
    private FloatingActionButton addPhotoFab;
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    
    private Uri imageUri = null;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    pickImage();
                } else {
                    Toast.makeText(this, "Galeri izni reddedildi", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    profileIv.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        usernameEt = findViewById(R.id.usernameEt);
        nameEt = findViewById(R.id.nameEt);
        emailEt = findViewById(R.id.emailEt);
        birthDateEt = findViewById(R.id.birthDateEt);
        unitAtv = findViewById(R.id.unitAtv);
        completeBtn = findViewById(R.id.completeBtn);
        profileIv = findViewById(R.id.profileIv);
        addPhotoFab = findViewById(R.id.addPhotoFab);

        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String birthDate = getIntent().getStringExtra("birthDate");

        if (name != null) nameEt.setText(name);
        if (email != null) emailEt.setText(email);
        else if (mAuth.getCurrentUser() != null) emailEt.setText(mAuth.getCurrentUser().getEmail());
        if (birthDate != null) birthDateEt.setText(birthDate);

        if (name == null || email == null || birthDate == null) {
            Toast.makeText(this, "Lütfen eksik profil bilgilerinizi tamamlayın", Toast.LENGTH_LONG).show();
        }

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

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        addPhotoFab.setOnClickListener(v -> checkPermissionAndPickImage());
        
        completeBtn.setOnClickListener(v -> saveProfile());
    }

    private void checkPermissionAndPickImage() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        } else {
            showPermissionDialog(permission);
        }
    }

    private void showPermissionDialog(String permission) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Galeri Erişimi")
                .setMessage("Profil resmi seçebilmek için galerinizdeki görsellere erişmemiz gerekiyor. Lütfen izin verin.")
                .setPositiveButton("İZİN VER", (dialog, which) -> requestPermissionLauncher.launch(permission))
                .setNegativeButton("İPTAL", null)
                .show();
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void saveProfile() {
        String name = nameEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String birthDate = birthDateEt.getText().toString().trim();
        String username = usernameEt.getText().toString().trim();
        String unit = unitAtv.getText().toString().trim();
        String uid = mAuth.getUid();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(birthDate) || TextUtils.isEmpty(username) || TextUtils.isEmpty(unit)) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        if (uid == null) return;

        completeBtn.setEnabled(false);

        if (imageUri != null) {
            uploadImageAndSaveProfile(uid, name, email, birthDate, username, unit);
        } else {
            saveToFirestore(uid, name, email, birthDate, username, unit, "");
        }
    }

    private void uploadImageAndSaveProfile(String uid, String name, String email, String birthDate, String username, String unit) {
        StorageReference ref = storage.getReference().child("profile_images/" + uid + ".jpg");
        ref.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveToFirestore(uid, name, email, birthDate, username, unit, uri.toString());
                });
            } else {
                completeBtn.setEnabled(true);
                Toast.makeText(ProfileAddActivity.this, "Resim yükleme hatası", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToFirestore(String uid, String name, String email, String birthDate, String username, String unit, String imageUrl) {
        Map<String, Object> user = new HashMap<>();
        user.put("uid", uid);
        user.put("fullName", name);
        user.put("email", email);
        user.put("birthDate", birthDate);
        user.put("username", username);
        user.put("unit", unit);
        user.put("profileImageUrl", imageUrl);

        db.collection("users").document(uid).set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(ProfileAddActivity.this, DashboardAdminActivity.class));
                finishAffinity();
            } else {
                completeBtn.setEnabled(true);
                Toast.makeText(ProfileAddActivity.this, "Hata: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

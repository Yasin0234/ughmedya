package com.korkutsoftware.ughmedya.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.korkutsoftware.ughmedya.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailEt;
    private MaterialButton submitBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        emailEt = findViewById(R.id.emailEt);
        submitBtn = findViewById(R.id.submitBtn);
        TextView backToLoginTv = findViewById(R.id.backToLoginTv);

        submitBtn.setOnClickListener(v -> resetPassword());

        backToLoginTv.setOnClickListener(v -> finish());
    }

    private void resetPassword() {
        String email = emailEt.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Lütfen e-posta adresinizi girin", Toast.LENGTH_SHORT).show();
            return;
        }

        submitBtn.setEnabled(false);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            submitBtn.setEnabled(true);
            if (task.isSuccessful()) {
                Toast.makeText(ForgotPasswordActivity.this, "Şifre sıfırlama bağlantısı e-posta adresinize gönderildi", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(ForgotPasswordActivity.this, "Hata: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

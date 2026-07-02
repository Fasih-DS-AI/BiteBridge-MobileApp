package com.malak.bitebridge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.malak.bitebridge.R;
import com.malak.bitebridge.database.UserRepository;
import com.malak.bitebridge.utils.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etPassword;
    private Button btnRegister;
    private TextView tvLogin;

    private UserRepository userRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userRepository = new UserRepository(this);
        sessionManager = SessionManager.getInstance(this);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() ||
                phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (userRepository.emailExists(email)) {
            Toast.makeText(this,
                    "Email already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        long userId = userRepository.registerUser(name, email, password, phone);

        if (userId != -1) {
            // First registered user (id=1) becomes admin
            boolean isAdmin = (userId == 1);
            if (isAdmin) {
                userRepository.makeAdmin(email);
            }
            sessionManager.createLoginSession((int) userId, name, isAdmin);
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this,
                    "Registration failed, try again",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
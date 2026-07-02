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
import com.malak.bitebridge.models.User;
import com.malak.bitebridge.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    private UserRepository userRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRepository = new UserRepository(this);
        sessionManager = SessionManager.getInstance(this);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = userRepository.loginUser(email, password);

        if (user != null) {
            sessionManager.createLoginSession(
                    user.getUserId(), user.getName(), user.isAdmin());
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this,
                    "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}
package com.manasa.olympiadedgeai.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.manasa.olympiadedgeai.R;
import com.manasa.olympiadedgeai.auth.CognitoAuthManager;

public class SignUpActivity extends AppCompatActivity {

    private EditText mEditTextUsername;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private CognitoAuthManager mAuthManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuthManager = new CognitoAuthManager(this);

        mEditTextUsername = findViewById(R.id.editTextUsername);
        mEditTextEmail = findViewById(R.id.editTextEmail);
        mEditTextPassword = findViewById(R.id.editTextPassword);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        Button buttonBackToLogin = findViewById(R.id.buttonBackToLogin);

        buttonSignUp.setOnClickListener(v -> {
            String username = mEditTextUsername.getText().toString().trim();
            String email = mEditTextEmail.getText().toString().trim();
            String password = mEditTextPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuthManager.signUp(username, password, email, new CognitoAuthManager.AuthCallback() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignUpActivity.this, VerifyActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_LONG).show());
                }
            });
        });

        buttonBackToLogin.setOnClickListener(v -> finish());
    }
}

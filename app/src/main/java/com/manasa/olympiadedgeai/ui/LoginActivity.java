package com.manasa.olympiadedgeai.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.manasa.olympiadedgeai.MainActivity;
import com.manasa.olympiadedgeai.R;
import com.manasa.olympiadedgeai.auth.CognitoAuthManager;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditTextUsername;
    private EditText mEditTextPassword;
    private CognitoAuthManager mAuthManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuthManager = new CognitoAuthManager(this);

        mEditTextUsername = findViewById(R.id.editTextUsername);
        mEditTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonGoToSignUp = findViewById(R.id.buttonGoToSignUp);

        buttonLogin.setOnClickListener(v -> {
            String username = mEditTextUsername.getText().toString().trim();
            String password = mEditTextPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuthManager.login(username, password, new CognitoAuthManager.AuthCallback() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show());
                }
            });
        });

        buttonGoToSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}

package com.manasa.olympiadedgeai.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.manasa.olympiadedgeai.R;
import com.manasa.olympiadedgeai.auth.CognitoAuthManager;

public class VerifyActivity extends AppCompatActivity {

    private EditText mEditTextCode;
    private String mUsername;
    private CognitoAuthManager mAuthManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        mAuthManager = new CognitoAuthManager(this);
        mUsername = getIntent().getStringExtra("username");

        mEditTextCode = findViewById(R.id.editTextVerifyCode);
        Button buttonVerify = findViewById(R.id.buttonVerify);

        buttonVerify.setOnClickListener(v -> {
            String code = mEditTextCode.getText().toString().trim();

            if (code.isEmpty() || code.length() < 6) {
                Toast.makeText(this, "Please enter the 6-digit code", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuthManager.confirmSignUp(mUsername, code, new CognitoAuthManager.AuthCallback() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(VerifyActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish(); // Go back to login
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(VerifyActivity.this, error, Toast.LENGTH_LONG).show());
                }
            });
        });
    }
}

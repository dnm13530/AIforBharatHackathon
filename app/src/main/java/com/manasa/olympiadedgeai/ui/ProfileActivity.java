package com.manasa.olympiadedgeai.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.manasa.olympiadedgeai.MainActivity;
import com.manasa.olympiadedgeai.R;
import com.manasa.olympiadedgeai.auth.CognitoAuthManager;

public class ProfileActivity extends AppCompatActivity {

    private CognitoAuthManager mAuthManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuthManager = new CognitoAuthManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Profile");
        }

        TextView textViewUsername = findViewById(R.id.textViewUsername);
        TextView textViewUserEmail = findViewById(R.id.textViewUserEmail);
        Button buttonLogout = findViewById(R.id.buttonLogout);

        String username = mAuthManager.getCurrentUserId();
        textViewUsername.setText(username != null ? username : "Guest User");
        
        // Note: For full email display, we would need to fetch user attributes from Cognito
        textViewUserEmail.setText("Olympiad Student"); 

        buttonLogout.setOnClickListener(v -> {
            mAuthManager.logout();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.manasa.olympiadedgeai;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.manasa.olympiadedgeai.auth.CognitoAuthManager;
import com.manasa.olympiadedgeai.data.QuestionWithStatus;
import com.manasa.olympiadedgeai.ui.LoginActivity;
import com.manasa.olympiadedgeai.ui.MasteryDashboardActivity;
import com.manasa.olympiadedgeai.ui.QuestionAdapter;
import com.manasa.olympiadedgeai.ui.QuestionDetailActivity;
import com.manasa.olympiadedgeai.ui.QuestionViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private QuestionViewModel mQuestionViewModel;
    private List<QuestionWithStatus> mFullList = new ArrayList<>();
    private QuestionAdapter mAdapter;
    private TextView mStatusIndicator;
    private CognitoAuthManager mAuthManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAuthManager = new CognitoAuthManager(this);
        
        // 1. CHECK LOGIN STATUS
        if (!mAuthManager.isUserLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Prevent user from coming back to main without login
            return;
        }

        setContentView(R.layout.activity_main);

        mStatusIndicator = findViewById(R.id.statusIndicator);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        ChipGroup chipGroup = findViewById(R.id.chipGroupFilters);
        Button buttonViewProgress = findViewById(R.id.buttonViewProgress);

        mAdapter = new QuestionAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mQuestionViewModel = new ViewModelProvider(this).get(QuestionViewModel.class);

        mQuestionViewModel.getAllQuestionsWithStatus().observe(this, questions -> {
            mFullList = questions;
            applyFilter(chipGroup.getCheckedChipId());
        });

        adapterSetup();
        
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                applyFilter(checkedIds.get(0));
            }
        });

        buttonViewProgress.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MasteryDashboardActivity.class);
            startActivity(intent);
        });

        updateNetworkStatus();
    }

    private void adapterSetup() {
        mAdapter.setOnItemClickListener(questionWithStatus -> {
            Intent intent = new Intent(MainActivity.this, QuestionDetailActivity.class);
            intent.putExtra(QuestionDetailActivity.EXTRA_QUESTION_ID, questionWithStatus.question.id);
            startActivity(intent);
        });
    }

    private void applyFilter(int chipId) {
        List<QuestionWithStatus> filteredList = new ArrayList<>();
        
        if (chipId == R.id.chipAll) {
            filteredList.addAll(mFullList);
        } else if (chipId == R.id.chipAlgebra) {
            for (QuestionWithStatus q : mFullList) {
                if (q.question.topic.equalsIgnoreCase("Algebra")) filteredList.add(q);
            }
        } else if (chipId == R.id.chipGeometry) {
            for (QuestionWithStatus q : mFullList) {
                if (q.question.topic.equalsIgnoreCase("Geometry")) filteredList.add(q);
            }
        } else if (chipId == R.id.chipOlympiad) {
            for (QuestionWithStatus q : mFullList) {
                if (q.question.difficulty.equalsIgnoreCase("Olympiad")) filteredList.add(q);
            }
        }
        
        mAdapter.submitList(filteredList);
    }

    private void updateNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = cm.getActiveNetwork();
        NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
        boolean isOnline = caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);

        if (isOnline) {
            mStatusIndicator.setText("● Tutor Mode (Online)");
            mStatusIndicator.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Green
        } else {
            mStatusIndicator.setText("● Practice Mode (Offline)");
            mStatusIndicator.setTextColor(android.graphics.Color.parseColor("#757575")); // Gray
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNetworkStatus();
    }
}

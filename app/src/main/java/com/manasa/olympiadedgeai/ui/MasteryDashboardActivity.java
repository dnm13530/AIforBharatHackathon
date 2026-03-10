package com.manasa.olympiadedgeai.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manasa.olympiadedgeai.R;

public class MasteryDashboardActivity extends AppCompatActivity {

    private QuestionViewModel mQuestionViewModel;
    private MasteryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mastery_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Mastery Dashboard");
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewMastery);
        mAdapter = new MasteryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        mQuestionViewModel = new ViewModelProvider(this).get(QuestionViewModel.class);
        mQuestionViewModel.getTopicMastery().observe(this, masteryList -> {
            if (masteryList != null) {
                mAdapter.setMasteryList(masteryList);
            }
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

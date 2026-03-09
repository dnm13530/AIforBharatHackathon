package com.manasa.olympiadedgeai.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manasa.olympiadedgeai.R;
import com.manasa.olympiadedgeai.ai.AiTutorManager;
import com.manasa.olympiadedgeai.data.Attempt;

import java.util.ArrayList;
import java.util.List;

public class QuestionDetailActivity extends AppCompatActivity {

    public static final String EXTRA_QUESTION_ID = "com.manasa.olympiadedgeai.ui.EXTRA_QUESTION_ID";
    private static final String TAG = "QuestionDetail";

    private QuestionViewModel mQuestionViewModel;
    private WebView mWebViewQuestion;
    private EditText mEditTextAnswer;
    private EditText mEditTextChatInput;
    private RecyclerView mRecyclerViewChat;
    private MessageAdapter mMessageAdapter;
    private AiTutorManager mAiTutorManager;
    private List<Message> mHistory = new ArrayList<>();

    private String mCorrectAnswer;
    private String mCurrentQuestionText = ""; // Ensure it's never null
    private int mQuestionId;
    private int mHintsUsed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        mAiTutorManager = new AiTutorManager();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mWebViewQuestion = findViewById(R.id.webViewQuestion);
        mEditTextAnswer = findViewById(R.id.editTextAnswer);
        mEditTextChatInput = findViewById(R.id.editTextChatInput);
        mRecyclerViewChat = findViewById(R.id.recyclerViewChat);
        
        Button buttonSubmit = findViewById(R.id.buttonSubmit);
        Button buttonGetHint = findViewById(R.id.buttonGetHint);
        Button buttonAskHelp = findViewById(R.id.buttonAskHelp);
        Button buttonChatSubmit = findViewById(R.id.buttonChatSubmit);
        Button buttonNextHint = findViewById(R.id.buttonNextHint);

        WebSettings settings = mWebViewQuestion.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebViewQuestion.setBackgroundColor(0);

        mMessageAdapter = new MessageAdapter();
        mRecyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewChat.setAdapter(mMessageAdapter);

        mQuestionViewModel = new ViewModelProvider(this).get(QuestionViewModel.class);
        mQuestionId = getIntent().getIntExtra(EXTRA_QUESTION_ID, -1);

        if (mQuestionId != -1) {
            mQuestionViewModel.getQuestionById(mQuestionId).observe(this, question -> {
                if (question != null) {
                    mCurrentQuestionText = question.questionText;
                    mCorrectAnswer = question.correctAnswer;
                    loadMathContent(mCurrentQuestionText);
                    
                    if (mMessageAdapter.getItemCount() == 0) {
                        addTutorMessage("Let's break it down into steps. How would you like to approach this problem?");
                    }
                }
            });
        }

        buttonChatSubmit.setOnClickListener(v -> {
            String userMsg = mEditTextChatInput.getText().toString().trim();
            if (userMsg.isEmpty()) return;
            addStudentMessage(userMsg);
            mEditTextChatInput.setText("");
            getAiResponse();
        });

        buttonGetHint.setOnClickListener(v -> {
            mHintsUsed++;
            addStudentMessage("I'm stuck. Can you give me a hint?");
            getAiResponse();
        });

        buttonNextHint.setOnClickListener(v -> {
            mHintsUsed++;
            addStudentMessage("I'm ready for the next step.");
            getAiResponse();
        });
        
        buttonAskHelp.setOnClickListener(v -> {
             addStudentMessage("Can you explain the main concept involved here?");
             getAiResponse();
        });
        
        buttonSubmit.setOnClickListener(v -> {
            String userAnswer = mEditTextAnswer.getText().toString().trim();
            if (userAnswer.isEmpty()) return;
            checkAnswer(userAnswer);
            mEditTextAnswer.setText("");
        });
    }

    private void addStudentMessage(String text) {
        Message msg = new Message(text, Message.TYPE_STUDENT);
        mHistory.add(msg);
        mMessageAdapter.addMessage(msg);
        mRecyclerViewChat.smoothScrollToPosition(mMessageAdapter.getItemCount() - 1);
    }

    private void addTutorMessage(String text) {
        Message msg = new Message(text, Message.TYPE_TUTOR);
        mHistory.add(msg);
        mMessageAdapter.addMessage(msg);
        mRecyclerViewChat.smoothScrollToPosition(mMessageAdapter.getItemCount() - 1);
    }

    private void checkAnswer(String userAnswer) {
        addStudentMessage("My final answer is: " + userAnswer);
        boolean isCorrect = (mCorrectAnswer != null && userAnswer.equalsIgnoreCase(mCorrectAnswer));
        
        Attempt attempt = new Attempt(mQuestionId, isCorrect, mHintsUsed, System.currentTimeMillis());
        mQuestionViewModel.insertAttempt(attempt);

        if (isCorrect) {
            addTutorMessage("That is correct! Excellent work.");
        } else {
            addTutorMessage("Not quite. Let's review the steps. What was your starting point?");
        }
    }

    private void getAiResponse() {
        if (mCurrentQuestionText.isEmpty()) {
            Toast.makeText(this, "Question data not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        mAiTutorManager.getSocraticHint(mQuestionId, mCurrentQuestionText, mHistory, new AiTutorManager.TutorCallback() {
            @Override
            public void onResponse(String response) {
                runOnUiThread(() -> addTutorMessage(response));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e(TAG, "AI Error: " + error);
                    Toast.makeText(QuestionDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadMathContent(String questionText) {
        String html = "<html><head>" +
                "<script type=\"text/javascript\" async src=\"https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML\"></script>" +
                "<style>body { font-family: sans-serif; font-size: 16px; color: #333; line-height: 1.5; margin: 16px; padding: 0; }</style>" +
                "</head><body>" +
                questionText +
                "</body></html>";
        mWebViewQuestion.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
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

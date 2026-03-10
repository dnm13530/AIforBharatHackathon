package com.manasa.olympiadedgeai.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manasa.olympiadedgeai.R;
import com.manasa.olympiadedgeai.ai.AiTutorManager;
import com.manasa.olympiadedgeai.auth.CognitoAuthManager;
import com.manasa.olympiadedgeai.data.Attempt;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuestionDetailActivity extends AppCompatActivity {

    public static final String EXTRA_QUESTION_ID = "com.manasa.olympiadedgeai.ui.EXTRA_QUESTION_ID";
    private static final String TAG = "QuestionDetail";
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    private QuestionViewModel mQuestionViewModel;
    private WebView mWebViewQuestion;
    private EditText mEditTextAnswer;
    private EditText mEditTextChatInput;
    private RecyclerView mRecyclerViewChat;
    private MessageAdapter mMessageAdapter;
    private AiTutorManager mAiTutorManager;
    private CognitoAuthManager mAuthManager;
    private List<Message> mHistory = new ArrayList<>();

    private String mCorrectAnswer;
    private String mCurrentQuestionText = "";
    private int mQuestionId;
    private int mHintsUsed = 0;
    private String mCurrentUserId;
    
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        mAiTutorManager = new AiTutorManager();
        mAuthManager = new CognitoAuthManager(this);
        mCurrentUserId = mAuthManager.getCurrentUserId() != null ? mAuthManager.getCurrentUserId() : "guest";

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
        ImageButton buttonCamera = findViewById(R.id.buttonCamera);

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

        buttonCamera.setOnClickListener(v -> checkCameraPermission());
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            launchCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to snap your work", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error creating image file", ex);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.manasa.olympiadedgeai.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            addStudentMessage("[Photo of my work sent]");
            getVisionAiResponse();
        }
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
        if (mCurrentQuestionText.isEmpty()) return;

        mAiTutorManager.getSocraticHint(mCurrentUserId, mQuestionId, mCurrentQuestionText, mHistory, new AiTutorManager.TutorCallback() {
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

    private void getVisionAiResponse() {
        if (mCurrentQuestionText.isEmpty() || mCurrentPhotoPath == null) return;

        mAiTutorManager.getVisionHint(mCurrentUserId, mQuestionId, mCurrentQuestionText, mHistory, mCurrentPhotoPath, new AiTutorManager.TutorCallback() {
            @Override
            public void onResponse(String response) {
                runOnUiThread(() -> addTutorMessage(response));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Vision AI Error: " + error);
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

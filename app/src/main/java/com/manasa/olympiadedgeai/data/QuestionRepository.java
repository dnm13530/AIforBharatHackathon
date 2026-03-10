package com.manasa.olympiadedgeai.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class QuestionRepository {
    private QuestionDao mQuestionDao;
    private AttemptDao mAttemptDao;
    private LiveData<List<Question>> mAllQuestions;
    private LiveData<List<QuestionWithStatus>> mAllQuestionsWithStatus;

    public QuestionRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mQuestionDao = db.questionDao();
        mAttemptDao = db.attemptDao();
        mAllQuestions = mQuestionDao.getAllQuestions();
        mAllQuestionsWithStatus = mQuestionDao.getAllQuestionsWithStatus();
    }

    public LiveData<List<Question>> getAllQuestions() {
        return mAllQuestions;
    }

    public LiveData<List<QuestionWithStatus>> getAllQuestionsWithStatus() {
        return mAllQuestionsWithStatus;
    }

    public LiveData<Question> getQuestionById(int id) {
        return mQuestionDao.getQuestionById(id);
    }
    
    public void insertAttempt(Attempt attempt) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mAttemptDao.insert(attempt);
        });
    }

    public LiveData<List<Attempt>> getAttemptsForQuestion(int questionId) {
        return mAttemptDao.getAttemptsForQuestion(questionId);
    }

    public LiveData<Integer> getCorrectCount(int questionId) {
        return mAttemptDao.getCorrectCount(questionId);
    }

    public LiveData<List<TopicMastery>> getTopicMastery() {
        return mAttemptDao.getTopicMastery();
    }
}

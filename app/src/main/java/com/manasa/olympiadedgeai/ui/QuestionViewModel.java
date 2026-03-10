package com.manasa.olympiadedgeai.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.manasa.olympiadedgeai.data.Attempt;
import com.manasa.olympiadedgeai.data.Question;
import com.manasa.olympiadedgeai.data.QuestionRepository;
import com.manasa.olympiadedgeai.data.QuestionWithStatus;
import com.manasa.olympiadedgeai.data.TopicMastery;

import java.util.List;

public class QuestionViewModel extends AndroidViewModel {
    private QuestionRepository mRepository;
    private LiveData<List<Question>> mAllQuestions;
    private LiveData<List<QuestionWithStatus>> mAllQuestionsWithStatus;

    public QuestionViewModel(Application application) {
        super(application);
        mRepository = new QuestionRepository(application);
        mAllQuestions = mRepository.getAllQuestions();
        mAllQuestionsWithStatus = mRepository.getAllQuestionsWithStatus();
    }

    public LiveData<List<Question>> getAllQuestions() {
        return mAllQuestions;
    }

    public LiveData<List<QuestionWithStatus>> getAllQuestionsWithStatus() {
        return mAllQuestionsWithStatus;
    }

    public LiveData<Question> getQuestionById(int id) {
        return mRepository.getQuestionById(id);
    }

    public void insertAttempt(Attempt attempt) {
        mRepository.insertAttempt(attempt);
    }

    public LiveData<List<Attempt>> getAttemptsForQuestion(int questionId) {
        return mRepository.getAttemptsForQuestion(questionId);
    }

    public LiveData<Integer> getCorrectCount(int questionId) {
        return mRepository.getCorrectCount(questionId);
    }

    public LiveData<List<TopicMastery>> getTopicMastery() {
        return mRepository.getTopicMastery();
    }
}

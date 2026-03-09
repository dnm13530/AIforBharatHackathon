package com.manasa.olympiadedgeai.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface QuestionDao {
    @Query("SELECT * FROM questions")
    LiveData<List<Question>> getAllQuestions();

    @Transaction
    @Query("SELECT * FROM questions")
    LiveData<List<QuestionWithStatus>> getAllQuestionsWithStatus();

    @Query("SELECT * FROM questions WHERE id = :id")
    LiveData<Question> getQuestionById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Question> questions);

    @Query("DELETE FROM questions")
    void deleteAll();
}

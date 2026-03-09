package com.manasa.olympiadedgeai.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AttemptDao {
    @Insert
    void insert(Attempt attempt);

    @Query("SELECT * FROM attempts WHERE questionId = :questionId ORDER BY timestamp DESC")
    LiveData<List<Attempt>> getAttemptsForQuestion(int questionId);

    @Query("SELECT COUNT(*) FROM attempts WHERE questionId = :questionId AND isCorrect = 1")
    LiveData<Integer> getCorrectCount(int questionId);
}

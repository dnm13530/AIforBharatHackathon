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

    @Query("SELECT q.topic as topic, COUNT(q.id) as totalQuestions, " +
           "SUM(CASE WHEN a.isCorrect = 1 THEN 1 ELSE 0 END) as solvedCorrectly, " +
           "AVG(CAST(a.hintsUsed AS FLOAT)) as averageHints " +
           "FROM questions q LEFT JOIN attempts a ON q.id = a.questionId " +
           "GROUP BY q.topic")
    LiveData<List<TopicMastery>> getTopicMastery();
}

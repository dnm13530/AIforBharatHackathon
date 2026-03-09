package com.manasa.olympiadedgeai.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "attempts")
public class Attempt {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public int questionId;
    public boolean isCorrect;
    public int hintsUsed;
    public long timestamp;

    public Attempt(int questionId, boolean isCorrect, int hintsUsed, long timestamp) {
        this.questionId = questionId;
        this.isCorrect = isCorrect;
        this.hintsUsed = hintsUsed;
        this.timestamp = timestamp;
    }
}

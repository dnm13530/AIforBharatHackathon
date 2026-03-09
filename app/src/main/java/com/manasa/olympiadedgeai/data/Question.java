package com.manasa.olympiadedgeai.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions")
public class Question {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String topic;
    public String difficulty;
    public String questionText;
    public String correctAnswer;
    public String initialHint;
    public String imageUrl; // S3 URL for diagrams

    public Question(String topic, String difficulty, String questionText, String correctAnswer, String initialHint, String imageUrl) {
        this.topic = topic;
        this.difficulty = difficulty;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.initialHint = initialHint;
        this.imageUrl = imageUrl;
    }
}

package com.manasa.olympiadedgeai.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class QuestionWithStatus {
    @Embedded
    public Question question;

    @Relation(
            parentColumn = "id",
            entityColumn = "questionId"
    )
    public List<Attempt> attempts;

    public boolean isSolved() {
        if (attempts == null) return false;
        for (Attempt attempt : attempts) {
            if (attempt.isCorrect) return true;
        }
        return false;
    }
}

package com.manasa.olympiadedgeai.data;

public class TopicMastery {
    public String topic;
    public int totalQuestions;
    public int solvedCorrectly;
    public float averageHints;

    public TopicMastery(String topic, int totalQuestions, int solvedCorrectly, float averageHints) {
        this.topic = topic;
        this.totalQuestions = totalQuestions;
        this.solvedCorrectly = solvedCorrectly;
        this.averageHints = averageHints;
    }

    public int getMasteryPercentage() {
        if (totalQuestions == 0) return 0;
        return (int) ((solvedCorrectly / (float) totalQuestions) * 100);
    }
}

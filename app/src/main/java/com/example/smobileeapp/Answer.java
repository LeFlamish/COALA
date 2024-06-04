package com.example.smobileeapp;

public class Answer {
    public String answerText;
    public String userIdToken;
    public String answerId;
    public int problemNum;

    public Answer() {
        // Default constructor required for calls to DataSnapshot.getValue(Answer.class)
    }

    // 생성자
    public Answer(String answerId, String answerText, String userIdToken, int problemNum) {
        this.answerId = answerId;
        this.answerText = answerText;
        this.userIdToken = userIdToken;
        this.problemNum = problemNum;
    }

    public String getAnswerText() {
        return this.answerText;
    }

    public String getAnswerId() { return this.answerId; }

    public String getUserIdToken() { return this.userIdToken; }

    public int getProblemNum() { return this.problemNum; }
}

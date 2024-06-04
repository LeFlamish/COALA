package com.example.smobileeapp;

public class Question {
    public String questionId;
    public String questionTitle;
    public String questionText;
    public String problemTitle;
    public String problemTier;
    public String problemType;
    public String userId;
    public int problemNum; // problemNum 필드 추가
    public long timePosted; // timePosted 필드 추가

    public Question() {
        // Default constructor required for calls to DataSnapshot.getValue(Question.class)
    }

    public Question(String questionId, String questionTitle, String questionText, String problemTitle, String problemTier, String problemType, String userId, int problemNum, long timePosted) {
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.questionText = questionText;
        this.problemTitle = problemTitle;
        this.problemTier = problemTier;
        this.problemType = problemType;
        this.userId = userId;
        this.problemNum = problemNum; // problemNum 필드 초기화
        this.timePosted = timePosted; // timePosted 필드 초기화
    }

    public String getQuestionText() {
        return this.questionText;
    }

    public String getUserIdToken() { return this.userId; }

    public String toString() {
        return "Problem Num: " + problemNum +
                ", Problem Title: " + problemTitle +
                ", Question Title: " + questionTitle;
    }

    public void setProblemNum(int problemNum) {
        this.problemNum = problemNum;
    }

    public void setProblemTitle(String problemTitle) {
        this.problemTitle = problemTitle;
    }

    public int getProblemNum() { return this.problemNum; }

    public long getTimePosted() { return this.timePosted; }

    public String getProblemType() { return this.problemType; }

    public String getQuestionId() { return this.questionId; }

    public String getProblemTier() { return this.problemTier; }

    public String getProblemTitle() { return this.problemTitle; }

    public String getQuestionTitle() { return this.questionTitle; }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }
}

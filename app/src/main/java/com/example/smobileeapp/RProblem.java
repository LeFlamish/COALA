package com.example.smobileeapp;

public class RProblem {
    public int problemNum;
    public String problemTitle;
    public String difficulty;
    public String problemType;
    public String problemURL;

    public RProblem() {
        // Default constructor required for calls to DataSnapshot.getValue(RProblem.class)
    }

    public RProblem(int problemNum, String problemTitle, String difficulty, String problemType, String problemURL) {
        this.problemNum = problemNum;
        this.problemTitle = problemTitle;
        this.difficulty = difficulty;
        this.problemType = problemType;
        this.problemURL = problemURL;
    }

    public int getProblemNum() { return problemNum; }

    public String getProblemTitle() { return problemTitle; }

    public String getDifficulty() { return difficulty; }

    public String getProblemType() { return problemType; }

    public String getProblemURL() { return problemURL; }
}

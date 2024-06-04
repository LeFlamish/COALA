package com.example.smobileeapp;

public class Problem {
    public int problemNum;
    public String problemTitle;
    public String difficulty;
    public String problemType;
    public String problemMemo;
    public String userIdToken;
    public Long timeposted;
    public String onmyown;

    public Problem() {
        // Default constructor required for calls to DataSnapshot.getValue(Problem.class)
    }

    public Problem(int problemNum, String problemTitle, String difficulty, String problemType, String problemMemo, String userIdToken, Long timeposted, String onmyown) {
        this.problemNum = problemNum;
        this.problemTitle = problemTitle;
        this.difficulty = difficulty;
        this.problemType = problemType;
        this.problemMemo = problemMemo;
        this.userIdToken = userIdToken;
        this.timeposted = timeposted;
        this.onmyown = onmyown;
    }

    public int getProblemNum () { return problemNum; }

    public String getProblemTitle () { return problemTitle; }

    public String getDifficulty () { return difficulty; }

    public String getProblemType () { return problemType; }

    public String getUserIdToken () { return userIdToken; }

    public String getProblemMemo () { return problemMemo; }

    public Long getTimeposted() { return timeposted; }

    public String getOnmyown() { return onmyown; }
}

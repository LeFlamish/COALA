package com.example.smobileeapp;

import java.util.HashMap;
import java.util.Map;

public class Question {
    public String questionId;
    public String questionTitle;
    public String questionText;
    public String problemTitle;
    public String problemTier;
    public String problemType;
    public String userIdToken;
    public int problemNum;
    public long timePosted;
    public HashMap<String, Answer> answers; // answers 필드 추가
    public HashMap<String, Reply> replies; // replies 필드 추가
    public int answerCount; // 답변의 개수를 세는 필드
    public int replyCount; // 댓글의 개수를 세는 필드
    private int goodHelpCount; // goodhelp의 개수를 나타내는 필드
    private boolean deleted;
    private Map<String, Boolean> goodHelpsByUsers; // 사용자별 좋아요 여부를 저장할 Map
    private boolean solved;

    public Question() {
        // Default constructor required for calls to DataSnapshot.getValue(Question.class)
    }

    public Question(String questionId, String questionTitle, String questionText, String problemTitle, String problemTier, String problemType, String userIdToken, int problemNum, long timePosted) {
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.questionText = questionText;
        this.problemTitle = problemTitle;
        this.problemTier = problemTier;
        this.problemType = problemType;
        this.userIdToken = userIdToken;
        this.problemNum = problemNum;
        this.timePosted = timePosted;
        this.answerCount = 0; // 초기화
        this.replyCount = 0; // 초기화
        this.goodHelpCount = 0; // 초기화
        this.deleted = false;
        this.goodHelpsByUsers = new HashMap<>(); // 좋아요 여부를 저장할 Map 초기화
        this.solved = false;
    }

    public void setAnswers(HashMap<String, Answer> answers) {
        this.answers = answers;
        // 답변 개수 갱신
        if (answers != null) {
            this.answerCount = answers.size();
        } else {
            this.answerCount = 0;
        }
    }

    public void setReplies(HashMap<String, Reply> replies) {
        this.replies = replies;
        // 댓글 개수 갱신
        if (replies != null) {
            this.replyCount = replies.size();
        } else {
            this.replyCount = 0;
        }
    }

    public HashMap<String, Answer> getAnswers() { return this.answers; }

    public HashMap<String, Reply> getReplies() { return this.replies; }

    public String getQuestionText() {
        return this.questionText;
    }

    public String getUserIdToken() { return this.userIdToken; }

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

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getGoodHelpCount() {
        return goodHelpCount;
    }

    public void setGoodHelpCount(int goodHelpCount) {
        this.goodHelpCount = goodHelpCount;
    }

    public Map<String, Boolean> getGoodHelpsByUsers() {
        return goodHelpsByUsers;
    }

    public boolean isSolved() { return this.solved; }

    public void setSolved(boolean solved) { this.solved = solved; }

    public void setGoodHelpsByUsers(Map<String, Boolean> goodHelpsByUsers) {
        this.goodHelpsByUsers = goodHelpsByUsers;
    }

    // Method to add or remove like by a user
    public void toggleGoodHelp(String userIdToken) {
        if (goodHelpsByUsers.containsKey(userIdToken)) {
            // User already liked, remove like
            goodHelpsByUsers.remove(userIdToken);
            goodHelpCount--;
        } else {
            // User has not liked, add like
            goodHelpsByUsers.put(userIdToken, true);
            goodHelpCount++;
        }
    }
}
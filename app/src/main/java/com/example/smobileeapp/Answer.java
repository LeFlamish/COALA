package com.example.smobileeapp;

import java.util.HashMap;
import java.util.Map;

public class Answer {
    private boolean deleted;
    public String answerText;
    public String userIdToken;
    public String answerId;
    public Map<String, Reply> replies; // HashMap으로 변경
    public int replyCount; // 댓글 개수를 관리하는 필드 추가

    public Answer() {
        // Default constructor required for calls to DataSnapshot.getValue(Answer.class)
    }

    // 생성자
    public Answer(String answerId, String answerText, String userIdToken) {
        this.answerId = answerId;
        this.answerText = answerText;
        this.userIdToken = userIdToken;
        this.replies = new HashMap<>(); // HashMap 초기화
        this.replyCount = 0; // 초기 댓글 개수는 0으로 설정
    }

    public String getAnswerText() {
        return this.answerText;
    }

    public String getAnswerId() {
        return this.answerId;
    }

    public String getUserIdToken() {
        return this.userIdToken;
    }

    public Map<String, Reply> getReplies() {
        return replies;
    }

    public void setReplies(Map<String, Reply> replies) {
        this.replies = replies;
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
}
package com.example.smobileeapp;

import java.util.HashMap;
import java.util.Map;

public class Answer {
    private boolean deleted;
    private String answerText;
    private String userIdToken;
    private String answerId;
    private Map<String, Reply> replies; // HashMap으로 변경
    private int replyCount; // 댓글 개수를 관리하는 필드 추가
    private int likeCount; // 좋아요 수를 관리하는 필드 추가
    private Map<String, Boolean> likesByUsers; // 사용자별 좋아요 여부를 저장할 Map

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
        this.likeCount = 0; // 초기 좋아요 수는 0으로 설정
        this.likesByUsers = new HashMap<>(); // 사용자별 좋아요 정보를 저장할 Map 초기화
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

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Map<String, Boolean> getLikesByUsers() {
        return likesByUsers;
    }

    public void setLikesByUsers(Map<String, Boolean> likesByUsers) {
        this.likesByUsers = likesByUsers;
    }

    // Method to add or remove like by a user
    public void toggleLike(String userIdToken) {
        if (likesByUsers.containsKey(userIdToken)) {
            // User already liked, remove like
            likesByUsers.remove(userIdToken);
            likeCount--;
        } else {
            // User has not liked, add like
            likesByUsers.put(userIdToken, true);
            likeCount++;
        }
    }
}
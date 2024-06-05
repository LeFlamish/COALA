package com.example.smobileeapp;

public class Reply {
    private String replyText;
    private long timePosted;
    private String userIdToken;
    private String replyIdToken;

    public Reply() {
        // Default constructor required for calls to DataSnapshot.getValue(Reply.class)
    }

    public Reply(String replyText, long timePosted, String userIdToken, String replyIdToken) {
        this.replyText = replyText;
        this.timePosted = timePosted;
        this.userIdToken = userIdToken;
        this.replyIdToken = replyIdToken;
    }

    public String getReplyText() {
        return this.replyText;
    }

    public long getTimePosted() {
        return this.timePosted;
    }

    public String getUserIdToken() {
        return this.userIdToken;
    }

    public String getReplyIdToken() {
        return this.replyIdToken;
    }

    // Setter for replyIdToken
    public void setReplyIdToken(String replyIdToken) {
        this.replyIdToken = replyIdToken;
    }
}

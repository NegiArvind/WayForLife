package com.wayforlife.Models;

public class Comment {
    public String userId;
    public String commentedMessage;
    public String timeDate;

    public Comment() {
    }

    public Comment(String userId, String commentedMessage, String timeDate) {
        this.userId = userId;
        this.commentedMessage = commentedMessage;
        this.timeDate = timeDate;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentedMessage() {
        return commentedMessage;
    }

    public void setCommentedMessage(String commentedMessage) {
        this.commentedMessage = commentedMessage;
    }
}

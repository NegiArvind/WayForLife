package com.wayforlife.Models;

import java.util.ArrayList;

public class Post {
    public String title;
    public String content;
    public boolean isPost;
    public int noOfLikes;
    public String userId;
    public String timeDate;
    public ArrayList<Comment> commentArrayList;
    public Post() {
    }

    public Post(String title, String content, boolean isPost, int noOfLikes, String userId, String timeDate, ArrayList<Comment> commentArrayList) {
        this.title = title;
        this.content = content;
        this.isPost = isPost;
        this.noOfLikes = noOfLikes;
        this.userId = userId;
        this.timeDate = timeDate;
        this.commentArrayList = commentArrayList;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public ArrayList<Comment> getCommentArrayList() {
        return commentArrayList;
    }

    public void setCommentArrayList(ArrayList<Comment> commentArrayList) {
        this.commentArrayList = commentArrayList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }

    public int getNoOfLikes() {
        return noOfLikes;
    }

    public void setNoOfLikes(int noOfLikes) {
        this.noOfLikes = noOfLikes;
    }
}

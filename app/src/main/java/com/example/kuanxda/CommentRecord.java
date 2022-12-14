package com.example.kuanxda;

import java.util.List;
//评论的实体类
public class CommentRecord<T> {
    List<Comment> comments;

    public List<Comment> getComments() {
        return comments;
    }

    public static class Comment<T> {
        private String id;
        private String appKey;
        private String pUserId;
        private String userName;
        private String shareId;
        private String parentCommentId;
        private String parentCommentUserId;
        private String replyCommentId;
        private String replyCommentUserId;
        private int commentLevel;
        private String content;
        private int status;
        private int praiseNum;
        private int topStatus;
        private String createTime;

        public String getId() {
            return id;
        }

        public String getAppKey() {
            return appKey;
        }

        public String getpUserId() {
            return pUserId;
        }

        public String getUserName() {
            return userName;
        }

        public String getShareId() {
            return shareId;
        }

        public String getParentCommentId() {
            return parentCommentId;
        }

        public String getParentCommentUserId() {
            return parentCommentUserId;
        }

        public String getReplyCommentId() {
            return replyCommentId;
        }

        public String getReplyCommentUserId() {
            return replyCommentUserId;
        }

        public int getCommentLevel() {
            return commentLevel;
        }

        public String getContent() {
            return content;
        }

        public int getStatus() {
            return status;
        }

        public int getPraiseNum() {
            return praiseNum;
        }

        public int getTopStatus() {
            return topStatus;
        }

        public String getCreateTime() {
            return createTime;
        }
    }
}

package com.example.kuanxda;

import java.util.List;
//评论的实体类，未使用，使用了CommentRecord类
public class Comment {
    String userName;
    String comment_content;
    List<Comment> comment_list_second;

    public Comment(String userName, String comment_content, List<Comment> comment_list_second) {
        this.userName = userName;
        this.comment_content = comment_content;
        this.comment_list_second = comment_list_second;
    }

    public String getUserName() {
        return userName;
    }

    public String getComment_content() {
        return comment_content;
    }

    public List<Comment> getComment_list_second() {
        return comment_list_second;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public void setComment_list_second(List<Comment> comment_list_second) {
        this.comment_list_second = comment_list_second;
    }
}

package com.example.kuanxda;

import java.util.List;
//处理从网络获取的评论响应体的类
public class CommentResponse<T> {
    private int code;
    private String msg;
    private Data data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Data getData() {
        return data;
    }

    @Override
    public String toString() {
        return "CommentResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static class Data<T> {
        private List<Record> records;
        private int total;
        private int size;
        private int current;

        public List<Record> getRecord() {
            return records;
        }

        public int getTotal() {
            return total;
        }

        public int getSize() {
            return size;
        }

        public int getCurrent() {
            return current;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "records=" + records +
                    ", total=" + total +
                    ", size=" + size +
                    ", current=" + current +
                    '}';
        }

        public static class Record {
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

            @Override
            public String toString() {
                return "{" +
                        "id='" + id + '\'' +
                        ", appKey='" + appKey + '\'' +
                        ", pUserId='" + pUserId + '\'' +
                        ", userName='" + userName + '\'' +
                        ", shareId='" + shareId + '\'' +
                        ", parentCommentId='" + parentCommentId + '\'' +
                        ", parentCommentUserId='" + parentCommentUserId + '\'' +
                        ", replyCommentId='" + replyCommentId + '\'' +
                        ", replyCommentUserId='" + replyCommentUserId + '\'' +
                        ", commentLevel=" + commentLevel +
                        ", content='" + content + '\'' +
                        ", status=" + status +
                        ", praiseNum=" + praiseNum +
                        ", topStatus=" + topStatus +
                        ", createTime='" + createTime + '\'' +
                        '}';
            }
        }
    }


}

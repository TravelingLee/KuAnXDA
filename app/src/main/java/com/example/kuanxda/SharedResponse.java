package com.example.kuanxda;

import org.jetbrains.annotations.NotNull;

import java.util.List;
//从网络获取多个分享内容的响应实体类
public class SharedResponse<T> {
    private int code;
    private String msg;
    private Data data;

    public SharedResponse() {
    }

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
        return "SharedResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static class Data<T>{
        private List<Record> records;
        private int total;
        private int size;
        private int current;

        public List<Record> getRecords() {
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

        @NotNull
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
            private String pUserId;
            private String imageCode;
            private String title;
            private String content;
            private String createTime;
            private List<String> imageUrlList;
            private String likeId;
            private int likeNum;
            private boolean hasLike;
            private String collectId;
            private int collectNum;
            private boolean hasCollect;
            private boolean hasFocus;
            private String username;

            public String getId() {
                return id;
            }

            public String getpUserId() {
                return pUserId;
            }

            public String getImageCode() {
                return imageCode;
            }

            public String getTitle() {
                return title;
            }

            public String getContent() {
                return content;
            }

            public String getCreateTime() {
                return createTime;
            }

            public List<String> getImageUrlList() {
                return imageUrlList;
            }

            public String getLikeId() {
                return likeId;
            }

            public int getLikeNum() {
                return likeNum;
            }

            public boolean isHasLike() {
                return hasLike;
            }

            public String getCollectId() {
                return collectId;
            }

            public int getCollectNum() {
                return collectNum;
            }

            public boolean isHasCollect() {
                return hasCollect;
            }

            public boolean isHasFocus() {
                return hasFocus;
            }

            public String getUsername() {
                return username;
            }

            @NotNull
            @Override
            public String toString() {
                return "Record{" +
                        "id='" + id + '\'' +
                        ", pUserId='" + pUserId + '\'' +
                        ", imageCode='" + imageCode + '\'' +
                        ", title='" + title + '\'' +
                        ", content='" + content + '\'' +
                        ", createTime='" + createTime + '\'' +
                        ", imageUrlList=" + imageUrlList +
                        ", likeId='" + likeId + '\'' +
                        ", likeNum=" + likeNum +
                        ", hasLike=" + hasLike +
                        ", collectId='" + collectId + '\'' +
                        ", collectNum=" + collectNum +
                        ", hasCollect=" + hasCollect +
                        ", hasFocus=" + hasFocus +
                        ", username='" + username + '\'' +
                        '}';
            }
        }
    }
}

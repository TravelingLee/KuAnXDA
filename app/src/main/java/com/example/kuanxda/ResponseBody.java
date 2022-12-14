package com.example.kuanxda;
//登录和注册的响应实体类
public class ResponseBody<T> {
    private int code;
    private String msg;
    private Data data;

    @Override
    public String toString() {
        return "ResponseBody{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(Data data) {
        this.data = data;
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
    public class Data<Z> {
        @Override
        public String toString() {
            return "{" +
                    "id='" + id + '\'' +
                    ", appKey='" + appKey + '\'' +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", sex='" + sex + '\'' +
                    ", introduce='" + introduce + '\'' +
                    ", avatar='" + avatar + '\'' +
                    '}';
        }

        private String id;
        private String appKey;
        private String username;
        private String password;
        private int sex;
        private String introduce;
        private String avatar;

        public String getId() {
            return id;
        }

        public String getAppKey() {
            return appKey;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public int getSex() {
            return sex;
        }

        public String getIntroduce() {
            return introduce;
        }

        public String getAvatar() {
            return avatar;
        }
    }
}

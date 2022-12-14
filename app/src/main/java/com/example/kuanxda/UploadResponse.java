package com.example.kuanxda;
//上传图片后的响应信息实体类
public class UploadResponse<H> {
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
        return "ModifyResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
    public class Data<V>{
        private long imageCode;
        private String[] imageUrlList;

        public long getImageCode() {
            return imageCode;
        }

        public String[] getImageUrlList() {
            return imageUrlList;
        }
    }
}

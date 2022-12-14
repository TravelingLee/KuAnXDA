package com.example.kuanxda;
//图片的实体类
public class Photo {
    private String ImageUrl;

    public Photo(String imageUrl) {
        ImageUrl = imageUrl;
    }
    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}

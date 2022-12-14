package com.example.kuanxda;

import androidx.annotation.NonNull;
//用来拼接请求参数的类
public class UrlBuilder {
    private final StringBuilder sb = new StringBuilder();

    public UrlBuilder() {
        sb.append("");
    }

    static class Builder extends UrlBuilder {
        @NonNull
        @Override
        public String toString() {
            return super.sb.toString();
        }

        public UrlBuilder.Builder urlPath(String path){
            super.sb.append(path);
            return this;
        }
        public UrlBuilder.Builder append(String key,String value){
            if (!super.sb.toString().contains("?")&&!super.sb.toString().isEmpty()){
                super.sb.append("?");
            }
            super.sb.append(key).append("=").append(value).append("&");
            return this;
        }
        public UrlBuilder.Builder append(String key,int value){
            if (!super.sb.toString().contains("?")&&!super.sb.toString().isEmpty()){
                super.sb.append("?");
            }
            super.sb.append(key).append("=").append(value).append("&");
            return this;
        }
        public UrlBuilder.Builder append(String key,long value){
            if (!super.sb.toString().contains("?")&&!super.sb.toString().isEmpty()){
                super.sb.append("?");
            }
            super.sb.append(key).append("=").append(value).append("&");
            return this;
        }
        public UrlBuilder.Builder append(String key,boolean value){
            if (!super.sb.toString().contains("?")&&!super.sb.toString().isEmpty()){
                super.sb.append("?");
            }
            super.sb.append(key).append("=").append(value).append("&");
            return this;
        }
        public UrlBuilder.Builder append(String key,float value){
            if (!super.sb.toString().contains("?")&&!super.sb.toString().isEmpty()){
                super.sb.append("?");
            }
            super.sb.append(key).append("=").append(value).append("&");
            return this;
        }
        public UrlBuilder.Builder append(String key,double value){
            if (!super.sb.toString().contains("?")&&!super.sb.toString().isEmpty()){
                super.sb.append("?");
            }
            super.sb.append(key).append("=").append(value).append("&");
            return this;
        }
        public UrlBuilder.Builder build(){
            super.sb.deleteCharAt(super.sb.lastIndexOf("&"));
            return this;
        }
    }
}

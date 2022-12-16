package com.example.kuanxda;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//图片的适配器类
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private Context mContext;
    private List<SharedResponse.Data.Record> recordList;
    private List<Photo> photoList;

    SharedPreferences spf;
    SharedPreferences.Editor editor;

    private static int currentPage = 0;
    private static int pageSize = 10;

    private static final String appId = "2c442f1f2e004dd78f2f586abd8ed6d2";
    private static final String appSecret = "78870a77cac04fb964283a1f58e6c9d41ec97";
    private static final String single_share_url = "http://47.107.52.7:88/member/photo/share/detail";
    private static final String get_comment_url = "http://47.107.52.7:88/member/photo/comment/first";


    OkHttpClient httpClient = new OkHttpClient()
            .newBuilder()
            .connectTimeout(60000, TimeUnit.MILLISECONDS)
            .readTimeout(60000, TimeUnit.MILLISECONDS)
            .build();

    Gson gson = new Gson();
    Intent intent;

    @SuppressLint("CommitPrefEdits")
    public PhotoAdapter(Context mContext, List<SharedResponse.Data.Record> recordList) {
        this.mContext = mContext;
        this.recordList = recordList;
        spf = mContext.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        editor = spf.edit();
        System.out.println(this.recordList);
    }

    public void updateData(){
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, null);
        return new PhotoAdapter.ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.ViewHolder holder, int position) {
        System.out.println("adapter收到的recordList"+recordList.get(position));
        if (recordList.get(position).getImageUrlList().size()!=0){
            Glide.with(mContext)
                    .load(recordList.get(position).getImageUrlList().get(0))
                    .into(holder.photoIv);
        }
        else {
            Glide.with(mContext)
                    .load(R.drawable.android_logo)
                    .into(holder.photoIv);
        }
        holder.id = recordList.get(position).getId();
        holder.content = recordList.get(position).getContent();
        holder.pUserId = recordList.get(position).getpUserId();
        holder.imageCode = recordList.get(position).getImageCode();
        holder.title = recordList.get(position).getTitle();
        holder.createTime = recordList.get(position).getCreateTime();
        holder.imageUrlList = recordList.get(position).getImageUrlList();
        holder.likeId = recordList.get(position).getLikeId();
        holder.likeNum = recordList.get(position).getLikeNum();
        holder.hasLike = recordList.get(position).isHasLike();
        holder.collectId = recordList.get(position).getCollectId();
        holder.collectNum = recordList.get(position).getCollectNum();
        holder.hasCollect = recordList.get(position).isHasCollect();
        holder.hasFocus = recordList.get(position).isHasFocus();
        holder.username = recordList.get(position).getUsername();
//        问题：某些字段的值是null，所以出现了一些空值导致空指针异常
        System.out.println("id1:" + holder.id);
        System.out.println("id2:" + recordList.get(position).getpUserId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), DetailActivity.class);
                if (!PersonalFragment.have_login){
                    Toast.makeText(mContext,"请先登录！",Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Headers comment_headers = new Headers.Builder()
                                .add("appId", appId)
                                .add("appSecret", appSecret)
                                .add("Accept", "application/json, text/plain, */*")
                                .build();
                        System.out.println("holder.id:"+holder.id);
                        UrlBuilder comment_url = new UrlBuilder.Builder()
                                .urlPath(get_comment_url)
                                .append("current",currentPage)
                                .append("size",pageSize)
                                .append("shareId",holder.id)
                                .build();
                        Request comment_request = new Request.Builder()
                                .url(comment_url.toString())
                                .headers(comment_headers)
                                .get()
                                .build();
                        Call call = httpClient.newCall(comment_request);
                        try {
                            Response response = call.execute();
                            Type jsonType = new TypeToken<CommentResponse<Object>>() {
                            }.getType();
                            String body = response.body().string();
                            System.out.println("响应体"+body);
                            CommentResponse<Object> commentResponse = gson.fromJson(body, jsonType);
                            System.out.println("评论响应体"+commentResponse);
                            System.out.println("record字符串："+commentResponse.getData().getRecord().toString());
                            System.out.println("intent:"+intent);
                            intent.putExtra("first_comment_list", commentResponse.getData().getRecord().toString());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Headers headers = new Headers.Builder()
                                .add("appId", appId)
                                .add("appSecret", appSecret)
                                .add("Accept", "application/json, text/plain, */*")
                                .build();
                        UrlBuilder urlBuilder = new UrlBuilder.Builder()
                                .urlPath(single_share_url)
                                .append("shareId", holder.id)
                                .append("userId", spf.getString("id", null))
                                .build();
                        Request request = new Request.Builder()
                                .url(urlBuilder.toString())
                                .headers(headers)
                                .get()
                                .build();
                        Call call = httpClient.newCall(request);
                        try {
                            Response response = call.execute();
                            System.out.println("onResponse");
                            Type jsonType = new TypeToken<SingleSharedResponse<Object>>() {
                            }.getType();
                            String body = response.body().string();
                            SingleSharedResponse<Object> singleSharedResponse = gson.fromJson(body, jsonType);

                            intent.putExtra("username", holder.username);
//                                System.out.println("获取的username"+singleSharedResponse.getData().getUsername());
                            intent.putExtra("id", holder.id);
                            intent.putExtra("hasFocus", singleSharedResponse.getData().isHasFocus());
                            System.out.println("获取的选中状态"+singleSharedResponse.getData().isHasFocus());
                            intent.putExtra("hasCollect", singleSharedResponse.getData().isHasCollect());
                            intent.putExtra("hasLike", singleSharedResponse.getData().isHasLike());
                            intent.putExtra("likeId", singleSharedResponse.getData().getLikeId());
                            intent.putExtra("likeNum", singleSharedResponse.getData().getLikeNum());
                            intent.putExtra("collectId", singleSharedResponse.getData().getCollectId());
                            intent.putExtra("collectNum", singleSharedResponse.getData().getCollectNum());
                            intent.putExtra("content", holder.content);
                            intent.putExtra("pUserId", holder.pUserId);
                            intent.putExtra("imageCode", holder.imageCode);
                            intent.putExtra("title", holder.title);
                            intent.putExtra("createTime", holder.createTime);
                            System.out.println("ArrayList图片列表"+(ArrayList<String>)recordList.get(position).getImageUrlList());
                            intent.putStringArrayListExtra("imageUrlList", (ArrayList<String>) recordList.get(position).getImageUrlList());
                            System.out.println("主图地址来源："+singleSharedResponse.getData());
//                                System.out.println(singleSharedResponse.getData().getpUserId());

                            System.out.println("adapter" + intent);
                            v.getContext().startActivity(intent);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                System.out.println(666666);

            }


        });
    }

    public int getItemCount() {
        return recordList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photoIv;

        private String content;
        private String id;
        private String pUserId;
        private String imageCode;
        private String title;
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

        private RecyclerView.Adapter adapter;

        public ViewHolder(View view, RecyclerView.Adapter photoAdapter) {
            super(view);
            this.photoIv = view.findViewById(R.id.photo);
            this.adapter = photoAdapter;
        }

    }
}
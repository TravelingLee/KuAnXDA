package com.example.kuanxda;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
//分享内容的详情页的activity
public class DetailActivity extends AppCompatActivity {
    private final Context mContext = DetailActivity.this;

    private String username;
    private String s_id;
    private List<String> imageList;
    private List<String> img_list = new ArrayList<>();
    private int collectNum;
    private int likeNum;
    private String createTime;
    private boolean hasLike;
    private boolean hasCollect;
    private boolean hasFocus;
    private String title_text;
    private String content_text;
    private String userId;
    private String shareId;
    private String collectId;
    private String likeId;
    private String pUserId;
    private ArrayList<String> commentList;
    private String commentList_string;

    private boolean isCollect;
    private boolean isLike;
    private boolean isFocus;


    private static final String post_comment_url_first = "http://47.107.52.7:88/member/photo/comment/first";
    private static final String like_url = "http://47.107.52.7:88/member/photo/like";
    private static final String single_share_url = "http://47.107.52.7:88/member/photo/share/detail";
    private static final String appId = "2c442f1f2e004dd78f2f586abd8ed6d2";
    private static final String appSecret = "78870a77cac04fb964283a1f58e6c9d41ec97";

    ImageView iv_avatar;
    TextView tv_username;
    TextView tv_time;
    TextView focus;
    ImageView iv_main_photo;
    ImageView iv_like;
    ImageView collect;
    TextView like_num;
    TextView collect_num;
    TextView title;
    TextView content;
    ImageView go_back;
    Button publish_btn;
    TextView et_comment;
    RecyclerView comment_list;

    SharedPreferences spf;
    SharedPreferences.Editor editor;
    OkHttpClient httpClient = new OkHttpClient()
            .newBuilder()
            .connectTimeout(60000, TimeUnit.MILLISECONDS)
            .readTimeout(60000, TimeUnit.MILLISECONDS)
            .build();
    Gson gson = new Gson();

    public void setComment(String comment_content,String shareId,String userId,String userName){
        if (comment_content.isEmpty()){
            Toast.makeText(mContext,"评论不能为空",Toast.LENGTH_SHORT).show();
        }else {
            TextView comment = new TextView(mContext);
            comment.setText(comment_content);
            Headers headers = new Headers.Builder()
                    .add("appId", "2c442f1f2e004dd78f2f586abd8ed6d2")
                    .add("appSecret", "78870a77cac04fb964283a1f58e6c9d41ec97")
                    .add("Accept", "application/json, text/plain, */*")
                    .build();
            Map<String,Object> param_comment = new HashMap<>();
            param_comment.put("content",comment_content);
            param_comment.put("shareId",shareId);
            param_comment.put("userId",userId);
            param_comment.put("userName",userName);

            String body_comment = gson.toJson(param_comment);
            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
            Request request_comment = new Request.Builder()
                    .url(post_comment_url_first)
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON,body_comment))
                    .build();
            httpClient.newCall(request_comment)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.d("COMMENT_ERR",e.toString());
                            Looper.prepare();
                            Toast.makeText(mContext,"评论失败：请求异常",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Looper.prepare();
                            Toast.makeText(mContext,"评论成功！",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    });
        }
    }

    public void setAvatar(ImageView imageView) {
        Glide.with(DetailActivity.this)
                .load(spf.getString("avatar", null))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(new GlideCircleTransform(DetailActivity.this))
                .crossFade()
                .into(imageView);
    }

    public void setUsername(String username) {
        this.tv_username.setText(username);
    }

    public void setCollectNum(String collectNum) {
        collect_num.setText(String.valueOf(collectNum));
    }

    public void setLikeNum(String likeNum) {
        like_num.setText(String.valueOf(likeNum));
    }

    public void setContent(String content_text) {
        content.setText(content_text);
    }

    public void setTitle(String title_text) {
        title.setText(title_text);
    }

    public void setCreateTime(String createTime) {
        this.tv_time.setText(createTime);
    }

    @SuppressLint("ResourceAsColor")
    public void setFocus(boolean hasFocus) {
        if (hasFocus) {
            focus.setText("已关注");
            focus.setLinksClickable(false);
            focus.setTextColor(Color.rgb(169, 169, 169));
        } else {
            focus.setText("+关注");
            focus.setLinksClickable(true);
            focus.setTextColor(R.color.textColor);
        }
        isFocus = hasFocus;
        focus.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                System.out.println("关注状态：" + isFocus);
                if (isFocus) {
                    isFocus = false;
//                    focus.setBackgroundColor(R.color.white);
//                    focus.setTextColor(R.color.textColor);
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable follow = getDrawable(R.drawable.follow);
                    focus.setBackground(follow);
                    focus.setText("+关注");
//                    System.out.println("s_id:"+s_id);
                    UrlBuilder urlBuilder = new UrlBuilder.Builder()
                            .urlPath("http://47.107.52.7:88/member/photo/focus/cancel")
                            .append("focusUserId", pUserId)
                            .append("userId", spf.getString("id", null))
                            .build();
                    MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
                    Headers headers = new Headers.Builder()
                            .add("appId", "2c442f1f2e004dd78f2f586abd8ed6d2")
                            .add("appSecret", "78870a77cac04fb964283a1f58e6c9d41ec97")
                            .add("Accept", "application/json, text/plain, */*")
                            .build();
                    Request request = new Request.Builder()
                            .url(urlBuilder.toString())
                            .headers(headers)
                            .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                            .build();
                    httpClient.newCall(request)
                            .enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    Log.e("FOCUS_ERR", e.toString());
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    System.out.println("response上");
                                    Log.d("FOCUS_OK", response.message());
                                }
                            });
                } else {
                    isFocus = true;
                    focus.setText("已关注");
                    focus.setLinksClickable(false);
                    focus.setTextColor(Color.rgb(169, 169, 169));
//                    System.out.println("s_id2:"+s_id);


                    UrlBuilder urlBuilder = new UrlBuilder.Builder()
                            .urlPath("http://47.107.52.7:88/member/photo/focus")
                            .append("focusUserId", pUserId)
                            .append("userId", spf.getString("id", null))
                            .build();
                    MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
                    Headers headers = new Headers.Builder()
                            .add("appId", "2c442f1f2e004dd78f2f586abd8ed6d2")
                            .add("appSecret", "78870a77cac04fb964283a1f58e6c9d41ec97")
                            .add("Accept", "application/json, text/plain, */*")
                            .build();
                    Request request = new Request.Builder()
                            .url(urlBuilder.toString())
                            .headers(headers)
                            .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                            .build();
                    httpClient.newCall(request)
                            .enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    Log.e("CANCEL_FOCUS_ERR", e.toString());
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    System.out.println("response下");
                                    Log.d("CANCEL_FOCUS_OK", response.message());
                                }
                            });
                }

            }
        });
    }

    @SuppressLint("ResourceAsColor")
    public void setLike(int color, boolean hasLike, String s_id, String likeId) {
        if (hasLike) {
            iv_like.setBackgroundColor(R.color.textColor);
        } else {
            iv_like.setBackgroundColor(R.color.black);
        }
        isLike = hasLike;
        iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("选中状态like：" + isLike);
                if (isLike) {
                    isLike = false;
                    iv_like.setBackgroundColor(R.color.black);
                    likeNum--;
                    like_num.setTextColor(R.color.black);
                    System.out.println(likeNum);
                    like_num.setText(String.valueOf(likeNum));
                    System.out.println("s_id:" + s_id);
                    UrlBuilder urlBuilder = new UrlBuilder.Builder()
                            .urlPath("http://47.107.52.7:88/member/photo/like/cancel")
                            .append("likeId", likeId)
                            .build();
                    MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
                    Headers headers = new Headers.Builder()
                            .add("appId", "2c442f1f2e004dd78f2f586abd8ed6d2")
                            .add("appSecret", "78870a77cac04fb964283a1f58e6c9d41ec97")
                            .add("Accept", "application/json, text/plain, */*")
                            .build();
                    Request request = new Request.Builder()
                            .url(urlBuilder.toString())
                            .headers(headers)
                            .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                            .build();
                    httpClient.newCall(request)
                            .enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    Log.e("CANCEL_LIKE_ERR", e.toString());
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    System.out.println("response上");
                                    Log.d("CANCEL_LIKE_OK", response.message());
                                }
                            });
                } else {
                    isLike = true;
                    iv_like.setBackgroundColor(R.color.textColor);
                    likeNum++;
                    like_num.setTextColor(R.color.textColor);
                    System.out.println(likeNum);
                    like_num.setText(String.valueOf(likeNum));
                    System.out.println("s_id2:" + s_id);


                    UrlBuilder urlBuilder = new UrlBuilder.Builder()
                            .urlPath("http://47.107.52.7:88/member/photo/like")
                            .append("shareId", s_id)
                            .append("userId", spf.getString("id", null))
                            .build();
                    MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
                    Headers headers = new Headers.Builder()
                            .add("appId", "2c442f1f2e004dd78f2f586abd8ed6d2")
                            .add("appSecret", "78870a77cac04fb964283a1f58e6c9d41ec97")
                            .add("Accept", "application/json, text/plain, */*")
                            .build();
                    Request request = new Request.Builder()
                            .url(urlBuilder.toString())
                            .headers(headers)
                            .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                            .build();
                    httpClient.newCall(request)
                            .enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    Log.e("LIKE_ERR", e.toString());
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    System.out.println("response下");
                                    Log.d("LIKE_OK", response.message());
                                }
                            });
                }
            }
        });
//        if (hasLike) {
//            iv_like.setBackgroundColor(color);
//        } else {
//            iv_like.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    iv_like.setBackgroundColor(color);
//                    likeNum++;
//                    like_num.setTextColor(color);
//                    like_num.setText(String.valueOf(likeNum));
//                    Headers headers = new Headers.Builder()
//                            .add("appId", appId)
//                            .add("appSecret", appSecret)
//                            .add("Accept", "application/json, text/plain, */*")
//                            .build();
//                    MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
//                    UrlBuilder urlBuilder = new UrlBuilder.Builder()
//                            .urlPath(like_url)
//                            .append("shareId", s_id)
//                            .append("userId", spf.getString("id", null))
//                            .build();
//                    Request request = new Request.Builder()
//                            .url(urlBuilder.toString())
//                            .headers(headers)
//                            .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
//                            .build();
//                    httpClient.newCall(request)
//                            .enqueue(new Callback() {
//                                @Override
//                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                                    Log.e("LIKE_ERR", e.toString());
//                                }
//
//                                @SuppressLint("CommitPrefEdits")
//                                @Override
//                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                                    Log.d("LIKE_OK", response.message());
//                                    editor = spf.edit();
//                                    editor.putInt("likeNum", likeNum);
//                                    editor.putBoolean("hasLike", hasLike);
//
//                                }
//                            });
//                }
//            });
//        }
    }

    public void setCollectState() {

    }

    @SuppressLint("ResourceAsColor")
    public void setCollect(int color, boolean hasCollect, String s_id, String collectId) {
//        collect.setBackgroundColor(color);
        if (hasCollect) {
            collect.setBackgroundColor(R.color.textColor);
        } else {
            collect.setBackgroundColor(R.color.black);
        }
        isCollect = hasCollect;
        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("选中状态：" + isCollect);
                if (isCollect) {
                    isCollect = false;
                    collect.setBackgroundColor(R.color.black);
                    collectNum--;
                    collect_num.setTextColor(color);
                    System.out.println(collectNum);
                    collect_num.setText(String.valueOf(collectNum));
                    System.out.println("s_id:" + s_id);
                    UrlBuilder urlBuilder = new UrlBuilder.Builder()
                            .urlPath("http://47.107.52.7:88/member/photo/collect/cancel")
                            .append("collectId", collectId)
                            .build();
                    MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
                    Headers headers = new Headers.Builder()
                            .add("appId", "2c442f1f2e004dd78f2f586abd8ed6d2")
                            .add("appSecret", "78870a77cac04fb964283a1f58e6c9d41ec97")
                            .add("Accept", "application/json, text/plain, */*")
                            .build();
                    Request request = new Request.Builder()
                            .url(urlBuilder.toString())
                            .headers(headers)
                            .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                            .build();
                    httpClient.newCall(request)
                            .enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    Log.e("CANCEL_COLLECT_ERR", e.toString());
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    System.out.println("response上");
                                    Log.d("CANCEL_COLLECT_OK", response.message());
                                }
                            });
                } else {
                    isCollect = true;
                    collect.setBackgroundColor(R.color.textColor);
                    collectNum++;
                    collect_num.setTextColor(color);
                    System.out.println(collectNum);
                    collect_num.setText(String.valueOf(collectNum));
                    System.out.println("s_id2:" + s_id);


                    UrlBuilder urlBuilder = new UrlBuilder.Builder()
                            .urlPath("http://47.107.52.7:88/member/photo/collect")
                            .append("shareId", s_id)
                            .append("userId", spf.getString("id", null))
                            .build();
                    MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
                    Headers headers = new Headers.Builder()
                            .add("appId", "2c442f1f2e004dd78f2f586abd8ed6d2")
                            .add("appSecret", "78870a77cac04fb964283a1f58e6c9d41ec97")
                            .add("Accept", "application/json, text/plain, */*")
                            .build();
                    Request request = new Request.Builder()
                            .url(urlBuilder.toString())
                            .headers(headers)
                            .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                            .build();
                    httpClient.newCall(request)
                            .enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    Log.e("COLLECT_ERR", e.toString());
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    System.out.println("response下");
                                    Log.d("COLLECT_OK", response.message());
                                }
                            });
                }
            }
        });
    }

    public void setMainPhoto(String url, ImageView iv_main, List<String> image_list) {
        Glide.with(DetailActivity.this)
                .load(url)
                .crossFade()
                .into(iv_main);
        iv_main.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                System.out.println("启动预览");
                ImageViewPager imageViewPager = findViewById(R.id.image_view_pager);
                imageViewPager.setData(image_list);
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        spf = getSharedPreferences("user_info", MODE_PRIVATE);

        go_back = findViewById(R.id.go_back);
        iv_avatar = findViewById(R.id.detail_avatar);
        tv_username = findViewById(R.id.username);
        tv_time = findViewById(R.id.publish_time);
        focus = findViewById(R.id.tv_focus);
        iv_main_photo = findViewById(R.id.main_photo);
        iv_like = findViewById(R.id.iv_good);
        collect = findViewById(R.id.iv_collect);
        like_num = findViewById(R.id.good_number);
        collect_num = findViewById(R.id.collect_number);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        publish_btn = findViewById(R.id.publish_btn);
        et_comment = findViewById(R.id.et_comment);
        comment_list = findViewById(R.id.comment_list);

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        System.out.println("detail：" + intent);
        s_id = intent.getStringExtra("id");
//        Headers headers = new Headers.Builder()
//                .add("appId", appId)
//                .add("appSecret", appSecret)
//                .add("Accept", "application/json, text/plain, */*")
//                .build();
//        UrlBuilder urlBuilder = new UrlBuilder.Builder()
//                .urlPath(single_share_url)
//                .append("shareId", id)
//                .append("userId", spf.getString("id", null))
//                .build();
//        Request request = new Request.Builder()
//                .url(urlBuilder.toString())
//                .headers(headers)
//                .get()
//                .build();
//        System.out.println(666666);
//        httpClient.newCall(request)
//                .enqueue(new Callback() {
//                    @Override
//                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                        Log.e("GET_DETAIL_ERR", e.toString());
//                    }
//
//                    @Override
//                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                        System.out.println("onResponse");
//                        Type jsonType = new TypeToken<SingleSharedResponse<Object>>() {
//                        }.getType();
//                        String body = response.body().string();
//                        SingleSharedResponse<Object> singleSharedResponse = gson.fromJson(body, jsonType);

        imageList = intent.getStringArrayListExtra("imageUrlList");
        System.out.println("接收的imageList" + imageList);
        collectNum = intent.getIntExtra("collectNum", 0);
        likeNum = intent.getIntExtra("likeNum", 0);
        createTime = intent.getStringExtra("createTime");
        hasLike = intent.getBooleanExtra("hasLike", false);
        hasCollect = intent.getBooleanExtra("hasCollect", false);
        hasFocus = intent.getBooleanExtra("hasFocus", false);
        System.out.println("接收的选中状态" + hasFocus);
        title_text = intent.getStringExtra("title");
        content_text = intent.getStringExtra("content");
        username = intent.getStringExtra("username");
        System.out.println("接收的username" + username);
        collectId = intent.getStringExtra("collectId");
        likeId = intent.getStringExtra("likeId");
        pUserId = intent.getStringExtra("pUserId");
        commentList_string = intent.getStringExtra("first_comment_list");
        System.out.println("评论列表："+commentList_string);


        Type jsonType = new TypeToken<List<Object>>(){}.getType();
        List<CommentRecord.Comment> commentRecords = gson.fromJson(commentList_string,jsonType);
        System.out.println(commentRecords);


        CommentAdapter commentAdapter = new CommentAdapter(mContext,commentRecords);

        comment_list.setAdapter(commentAdapter);

        publish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setComment(et_comment.getText().toString().trim(),s_id,spf.getString("id", null),username);
            }
        });
        if (imageList!=null && imageList.size()!=0)
        for (int i = 0; i < imageList.size(); i++) {
            System.out.println(imageList.get(i));
            img_list.add(imageList.get(i));
        }

        if (username != null) {
            setUsername(username);
        }
        if (spf.getString("avatar", null) != null) {
            System.out.println("图片地址：" + spf.getString("avatar", null));
            setAvatar(iv_avatar);
        }
        if (createTime != null) {
            long time = Long.parseLong(createTime);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(time);
            String pubTime = sdf.format(date);
            setCreateTime(pubTime);
        }
        System.out.println("是否关注？" + hasFocus);
        setFocus(hasFocus);
        System.out.println(imageList);
        if (imageList != null && imageList.size() != 0) {
            System.out.println("主图地址：" + imageList.get(0));
            setMainPhoto(imageList.get(0), iv_main_photo, img_list);
        }
        setLike(R.color.textColor, hasLike, s_id, likeId);
        setCollect(R.color.textColor, hasCollect, s_id, collectId);
        if (title_text != null) {
            setTitle(title_text);
        }
        if (content_text != null) {
            setContent(content_text);
        }
        setLikeNum(String.valueOf(likeNum));
        setCollectNum(String.valueOf(collectNum));
    }
//                });
//    }
}
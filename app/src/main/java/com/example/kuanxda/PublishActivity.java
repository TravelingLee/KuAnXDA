package com.example.kuanxda;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lwkandroid.widget.ngv.DefaultNgvAdapter;
import com.lwkandroid.widget.ngv.INgvImageLoader;
import com.lwkandroid.widget.ngv.NgvChildImageView;
import com.lwkandroid.widget.ngv.NineGridView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
//发布分享内容的页面的activity
public class PublishActivity extends AppCompatActivity {
    ImageView add_image;
    int REQUEST_CODE_CHOOSE = 1;
    List<String> mSelected;
    Context mContext;
    NineGridView mNineGridView;
    GlideLoader glideLoader;
    DefaultNgvAdapter<String> ngvAdapter;
    List<String> allSelected = new ArrayList<>();

    ImageView go_back;
    LinearLayout draft;
    LinearLayout cancel;

    ImageView iv_add_label;
    EditText et_add_label;
    TextView tv_add_label;
    TextView tv_publish;

    String label;

    private static final String upload_url = "http://47.107.52.7:88/member/photo/image/upload";
    private static final String add_share_url = "http://47.107.52.7:88/member/photo/share/add";
    private static final String appId = "2c442f1f2e004dd78f2f586abd8ed6d2";
    private static final String appSecret = "78870a77cac04fb964283a1f58e6c9d41ec97";

    OkHttpClient httpClient = new OkHttpClient();
    Gson gson = new Gson();

    MultipartBody.Builder builder;
    RequestBody requestBody;

    String user_id;
    long imageCode;
    String content;
    String title;

    EditText et_edit_text;
    EditText et_title;

    Map<String,Object> param = new HashMap<>();

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"ResourceType", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        et_edit_text = findViewById(R.id.et_edit_text);
        et_title = findViewById(R.id.et_title);

        glideLoader = new GlideLoader();

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable title_bg = getDrawable(R.drawable.title_bg);

        iv_add_label = findViewById(R.id.iv_add_label);
        et_add_label = findViewById(R.id.et_add_label);
        tv_add_label = findViewById(R.id.tv_add_label);
        tv_publish = findViewById(R.id.text_publish);

        tv_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = et_edit_text.getText().toString().trim();
                title = et_title.getText().toString().trim();

                if (title.trim().equals("")){
                    Toast.makeText(mContext,"标题不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }else if (content.trim().equals("")){
                    Toast.makeText(mContext,"内容不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }else if (allSelected.size()==0){
                    Toast.makeText(mContext,"还没选图片呢~",Toast.LENGTH_SHORT).show();
                    return;
                }

                File[] files = new File[allSelected.size()];
                for (int i = 0; i < allSelected.size(); i++) {
                    File file = new File(allSelected.get(i));
                    files[i] = file;
                }

                builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);

                if (files!=null)
                for (File file:files
                     ) {
                    System.out.println(file.getAbsolutePath());
//                    System.out.println(file.getAbsoluteFile());

                    builder.addFormDataPart("fileList",file.getName(),RequestBody.create(MediaType.parse("multipart/form-data"),file));
                }
                System.out.println(builder);

                RequestBody requestBody = builder.build();

                Headers headers = new Headers.Builder()
                        .add("appId",appId)
                        .add("appSecret",appSecret)
                        .add("Accept", "multipart/form-data, text/plain, */*")
                        .build();
                Request request = new Request.Builder()
                        .url(upload_url)
                        .headers(headers)
                        .post(requestBody)
                        .build();
                httpClient.newCall(request)
                        .enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Log.e("PUBLISH_ERR",e.toString());
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                Type jsonType = new TypeToken<UploadResponse<Object>>(){}.getType();
                                String body = response.body().string();
                                UploadResponse<Object> uploadResponse = gson.fromJson(body,jsonType);
                                String[] imageUrlList = uploadResponse.getData().getImageUrlList();
                                System.out.println(Arrays.toString(imageUrlList));
                                imageCode = uploadResponse.getData().getImageCode();
                                new Thread(()->{
                                    Headers headers_add = new Headers.Builder()
                                            .add("appId",appId)
                                            .add("appSecret",appSecret)
                                            .add("Accept", "application/json, text/plain, */*")
                                            .build();
                                    param.put("content",content);
                                    param.put("title",title);
                                    param.put("pUserId",user_id);
                                    param.put("imageCode",imageCode);

                                    String body_add = gson.toJson(param);

                                    MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

                                    Request request_add = new Request.Builder()
                                            .url(add_share_url)
                                            .headers(headers_add)
                                            .post(RequestBody.create(MEDIA_TYPE_JSON,body_add))
                                            .build();
                                    System.out.println(request_add.toString());
                                    httpClient.newCall(request_add).enqueue(callback);
                                }).start();
//                                Log.d("imageList", Arrays.toString(imageList));
                            }
                            final Callback callback = new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    Log.e("SHARE_ERR",e.toString());
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    Log.d("SHARE_OK",response.body().string());
                                    Looper.prepare();
                                    Toast.makeText(mContext,"发布成功！",Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            };
                        });
            }
        });

        iv_add_label.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                label = String.valueOf(et_add_label.getText());
                System.out.print("输出一行废话");
                System.err.println(label);
                Log.e("label", String.valueOf(label));
                tv_add_label.setText(label);
                tv_add_label.setBackground(title_bg);
                et_add_label.setText("");
            }
        });

        go_back = findViewById(R.id.go_back);
        draft = findViewById(R.id.draft);
        cancel = findViewById(R.id.cancel);

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder tip_dialog_builder = new AlertDialog.Builder(PublishActivity.this);
                tip_dialog_builder.setTitle("确定退出吗？");
                tip_dialog_builder.setMessage("已编辑的内容将不会保存");
                tip_dialog_builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("取消",null);
                tip_dialog_builder.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder tip_dialog_builder = new AlertDialog.Builder(PublishActivity.this);
                tip_dialog_builder.setTitle("确定退出吗？");
                tip_dialog_builder.setMessage("已编辑的内容将不会保存");
                tip_dialog_builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("取消",null);
                tip_dialog_builder.show();
            }
        });

        draft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"未保存任何草稿",Toast.LENGTH_SHORT).show();
            }
        });

        mNineGridView = findViewById(R.id.nine_grid_view);
        ngvAdapter = new DefaultNgvAdapter<>(9, new GlideLoader());
        mNineGridView.setAdapter(ngvAdapter);
        mNineGridView.setHorizontalChildCount(4);
        mNineGridView.getChildCount();
        mNineGridView.setIconPlusDrawable(R.drawable.ic_baseline_add_box_24);

        mContext = PublishActivity.this;

        add_image = findViewById(R.id.add_image);
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("打开选择器");
                if (checkPermissionREAD_EXTERNAL_STORAGE(mContext)){
                    Matisse.from(PublishActivity.this)
                            .choose(MimeType.ofAll())
                            .countable(true)
                            .maxSelectable(9)
                            .capture(true)
                            .captureStrategy(new CaptureStrategy(true,"com.example.kuanxda.fileprovider"))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideEngine())
                            .forResult(REQUEST_CODE_CHOOSE);
                }
            }
        });
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("进入回调");
        System.out.println(mNineGridView.getChildCount());
        if (requestCode==REQUEST_CODE_CHOOSE&&resultCode==RESULT_OK){
            mSelected = Matisse.obtainPathResult(data);
            System.out.println(mSelected.get(0));

            if (9-(mNineGridView.getChildCount()+mSelected.size())<0){
                int canAddCount = 9-mNineGridView.getChildCount();
                String str_canAddCount = Integer.toString(canAddCount);
                Toast.makeText(mContext,"只能再添加"+str_canAddCount+"张图片",Toast.LENGTH_SHORT).show();
                return;
            }
            allSelected.addAll(mSelected);

            System.out.println(ngvAdapter);
            ngvAdapter.addDataList(mSelected);
            //设置点击事件
            ngvAdapter.setOnChildClickListener(new DefaultNgvAdapter.OnChildClickedListener<String>()
            {

                @Override
                public void onPlusImageClicked(ImageView plusImageView, int dValueToLimited)
                {
                    //点击“+”号图片后的回调
                    //plusImageView代表“+”号图片对象，dValueToLimited代表当前可继续添加的图片数量
                }

                @Override
                public void onContentImageClicked(int position, String data, NgvChildImageView childImageView) {

                }

                @Override
                public void onImageDeleted(int position, String data) {
                    ngvAdapter.removeData(data);
                }
            });
        }
    }
    private static class GlideLoader implements INgvImageLoader<String> {
        @Override
        public void load(String source, ImageView imageView, int width, int height) {
            Glide.with(imageView.getContext())
                    .load(source)
                    .override(64,64)
                    .into(imageView);
        }
    }
}
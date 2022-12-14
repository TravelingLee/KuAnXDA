package com.example.kuanxda;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
//编辑个人信息的activity
public class EditActivity extends AppCompatActivity {
    int edit_avatar_path;
    private Context mContext;
    ImageView avatar_img;
    ImageView goBack_img;
    RelativeLayout nickname_cell;
    RelativeLayout sex_cell;
    RelativeLayout introduction_cell;
    TextView nickname;
    TextView sex;
    TextView introduction;
    EditText editText_nickname;
    EditText editText_introduction;
    ConstraintLayout parent;

    private View dialogView;
    private AlertDialog.Builder sureChanged;

    private static final String appKey = "2c442f1f2e004dd78f2f586abd8ed6d2";
    private static final String appSecret = "78870a77cac04fb964283a1f58e6c9d41ec97q";
    private static final String url = "http://47.107.52.7:88/member/photo/user/update";


    private boolean clicked = false;

    String[] sex_array = {"男", "女", "保密"};
    int position = 0;

    Gson gson = new Gson();

    SharedPreferences spf;
    SharedPreferences.Editor editor;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        spf = getSharedPreferences("user_info",Context.MODE_PRIVATE);
        editor = spf.edit();

        OkHttpClient httpClient = new OkHttpClient();

        Intent intent = getIntent();
        avatar_img = findViewById(R.id.edit_avatar);
        goBack_img = findViewById(R.id.go_back);
        nickname_cell = findViewById(R.id.cell_1);
        sex_cell = findViewById(R.id.cell_2);
        introduction_cell = findViewById(R.id.cell_3);

        nickname = findViewById(R.id.nickname);
        sex = findViewById(R.id.sex);
        introduction = findViewById(R.id.introduction);

        editText_nickname = new EditText(this);
        editText_introduction = new EditText(this);

        mContext = EditActivity.this;

        String intent_nickname = intent.getStringExtra(PersonalFragment.KEY_NICKNAME);
        String intent_avatar = intent.getStringExtra(PersonalFragment.KEY_AVATAR);
        String intent_sex = intent.getStringExtra(PersonalFragment.KEY_SEX);
        String intent_introduction = intent.getStringExtra(PersonalFragment.KEY_INTRODUCTION);
        String intent_id = intent.getStringExtra(PersonalFragment.KEY_ID);

        if (intent_nickname != null) nickname.setText(intent_nickname);
        Log.d("INTENT_AVATAR", intent_avatar);
        if (intent_avatar != null)
            Glide.with(mContext)
                    .load(intent_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .transform(new GlideCircleTransform(mContext))
                    .crossFade()
                    .into(avatar_img);
        if (intent_sex != null) {
            if (intent_sex.equals("female")) sex.setText("女");
            else if (intent_sex.equals("male")) sex.setText("男");
        }
        ;
        if (intent_introduction != null) introduction.setText(intent_introduction);

        goBack_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nickname_cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = true;
                editText_nickname.setText(nickname.getText());
                AlertDialog.Builder edit_text_builder = new AlertDialog.Builder(EditActivity.this);
                edit_text_builder.setTitle("编辑昵称");
                edit_text_builder.setView(editText_nickname);
                edit_text_builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText_nickname.getParent() != null) {
                            ViewGroup vg = (ViewGroup) editText_nickname.getParent();
                            vg.removeAllViews();
                        }
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nickname.setText(editText_nickname.getText());
                        System.out.println("ET_NICKNAME"+editText_nickname.getText());

                        Headers headers = new Headers.Builder()
                                .add("appId", "2c442f1f2e004dd78f2f586abd8ed6d2")
                                .add("appSecret", "78870a77cac04fb964283a1f58e6c9d41ec97")
                                .add("Accept", "application/json, text/plain, */*")
                                .build();

                        Map<String, Object> param = new HashMap<>();
                        param.put("username",editText_nickname.getText().toString().trim());
                        param.put("id", intent_id);

                        String body = gson.toJson(param);
                        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

                        Request request = new Request.Builder()
                                .headers(headers)
                                .url(url)
                                .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                                .build();

                        httpClient.newCall(request)
                                .enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        Log.e("MODIFY_ERR", e.toString());
                                        Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        Type jsonType = new TypeToken<ResponseBody<Object>>() {
                                        }.getType();
                                        String body = response.body().string();
                                        ResponseBody<Object> modifyResponse = gson.fromJson(body, jsonType);
                                        if (modifyResponse.getCode() != 200) {
                                            Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                                        }
                                        editor.putString("name",editText_nickname.getText().toString().trim());
                                    }
                                });

                        if (editText_nickname.getParent() != null) {
                            ViewGroup vg = (ViewGroup) editText_nickname.getParent();
                            vg.removeAllViews();
                        }
                    }
                }).create();
                edit_text_builder.show();
            }
        });

        sex_cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = true;
                System.out.println("修改性别");
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                builder.setSingleChoiceItems(sex_array, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println(which);
                        position = which;
                        sex.setText(sex_array[which]);

                        Headers headers = new Headers.Builder()
                                .add("appId", "2c442f1f2e004dd78f2f586abd8ed6d2")
                                .add("appSecret", "78870a77cac04fb964283a1f58e6c9d41ec97")
                                .add("Accept", "application/json, text/plain, */*")
                                .build();

                        Map<String, Object> param = new HashMap<>();
                        if (position==0){
                            param.put("sex", 0);
                        }else if (position==1){
                            param.put("sex",1);
                        }else {
                            param.put("sex",2);
                        }
                        param.put("id", intent_id);

                        String body = gson.toJson(param);
                        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

                        Request request = new Request.Builder()
                                .headers(headers)
                                .url(url)
                                .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                                .build();

                        httpClient.newCall(request)
                                .enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        Log.e("MODIFY_ERR", e.toString());
                                        Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        Type jsonType = new TypeToken<ResponseBody<Object>>() {
                                        }.getType();
                                        String body = response.body().string();
                                        ResponseBody<Object> modifyResponse = gson.fromJson(body, jsonType);
                                        if (modifyResponse.getCode() != 200) {
                                            Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                                        }
                                        if (sex.getText().toString().trim().equals("男")) {
                                            editor.putInt("sex",0);
                                        } else if (sex.getText().toString().trim().equals("女")) {
                                            editor.putInt("sex",1);
                                        }else {
                                            editor.putInt("sex",2);
                                        }
                                    }
                                });

                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        introduction_cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = true;
                editText_introduction.setText(introduction.getText());
                AlertDialog.Builder edit_text_builder = new AlertDialog.Builder(EditActivity.this);
                edit_text_builder.setTitle("编辑个人介绍");
                edit_text_builder.setView(editText_introduction);
                edit_text_builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText_introduction.getParent() != null) {
                            ViewGroup vg = (ViewGroup) editText_introduction.getParent();
                            vg.removeAllViews();
                        }
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        introduction.setText(editText_introduction.getText());
                        Headers headers = new Headers.Builder()
                                .add("appId", "2c442f1f2e004dd78f2f586abd8ed6d2")
                                .add("appSecret", "78870a77cac04fb964283a1f58e6c9d41ec97")
                                .add("Accept", "application/json, text/plain, */*")
                                .build();
                        Map<String, Object> param = new HashMap<>();
                        param.put("introduce", editText_introduction.getText().toString().trim());
                        param.put("id", intent_id);

                        String body = gson.toJson(param);
                        System.out.println("测试");

                        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

                        Request request = new Request.Builder()
                                .headers(headers)
                                .url(url)
                                .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                                .build();
                        httpClient.newCall(request)
                                .enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        Log.e("MODIFY_ERR", e.toString());
                                        Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        Type jsonType = new TypeToken<ResponseBody<Object>>() {
                                        }.getType();
                                        String body = response.body().string();
                                        ResponseBody<Object> modifyResponse = gson.fromJson(body, jsonType);
                                        if (modifyResponse.getCode() != 200) {
                                            Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        if (editText_introduction.getParent() != null) {
                            ViewGroup vg = (ViewGroup) editText_introduction.getParent();
                            vg.removeAllViews();
                        }
                    }
                }).create();
                edit_text_builder.show();
            }
        });
    }
}
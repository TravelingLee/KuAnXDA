package com.example.kuanxda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//应用入口，主activity
public class MainActivity extends AppCompatActivity {
    public static final String USER_NAME = "com.example.kuanxda.username";
    public static final String AVATAR_ID = "com.example.kuanxda.avatar_id";
    public static final String USER_ID = "com.example.kuanxda.userid";

    BottomNavigationView bnv;
    private MenuItem publish;
    private ImageView add;
    private List<Fragment> fragments;
    Fragment fragment;
    private LinearLayout linear_publish;

    SharedPreferences spf;
    SharedPreferences.Editor editor;

    String user_id;

    private static final String dynamic_url = "http://47.107.52.7:88/member/photo/share";
    private static final String appId = "2c442f1f2e004dd78f2f586abd8ed6d2";
    private static final String appSecret = "78870a77cac04fb964283a1f58e6c9d41ec97";

    private int current_page = 0;
    private static final int page_size = 10;
    private static String userId;

    OkHttpClient httpClient = new OkHttpClient();
    Gson gson = new Gson();

    @SuppressLint("HandlerLeak")
    private static Handler handler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if (msg.what==1){
                PhotoAdapter photoAdapter = (PhotoAdapter) msg.obj;
                photoAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PersonalFragment()).commit();
        }
        spf = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        user_id = spf.getString("id", null);
        userId = spf.getString("id", null);
        initView();
    }

    private void initView() {
        fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

        bnv = findViewById(R.id.navigation_bar);
        fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new PersonalFragment());
        publish = bnv.getMenu().getItem(2);
        add = findViewById(R.id.add);
        add.bringToFront();
        linear_publish = findViewById(R.id.Linear_publish);

        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_index: {
                        fragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    }
                    break;
                    case R.id.nav_personal: {
                        fragment = new PersonalFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    }
                    break;
                    case R.id.nav_found: {
                        new Thread(() -> {
                            Headers headers = new Headers.Builder()
                                    .add("appId", appId)
                                    .add("appSecret", appSecret)
                                    .add("Accept", "application/json, text/plain, */*")
                                    .build();
                            UrlBuilder.Builder urlBuilder = new UrlBuilder.Builder()
                                    .urlPath(dynamic_url)
                                    .append("current", current_page)
                                    .append("size", page_size)
                                    .append("userId", userId)
                                    .build();

                            Request request = new Request.Builder()
                                    .url(urlBuilder.toString())
                                    .headers(headers)
                                    .get()
                                    .build();
                            httpClient.newCall(request)
                                    .enqueue(new Callback() {
                                        @Override
                                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                            Log.e("GET_DYNAMIC_ERR", e.toString());
                                        }

                                        @Override
                                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                            Type jsonType = new TypeToken<SharedResponse<Object>>() {
                                            }.getType();
                                            String body = response.body().string();
                                            System.out.println(body);
                                            SharedResponse<Object> sharedResponse = gson.fromJson(body, jsonType);
                                            List<SharedResponse.Data.Record> record = sharedResponse.getData().getRecords();
                                            System.out.println("启动FoundFragment" + record.toString());
                                            System.out.println(record);
                                            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FoundFragment(record)).commit();


//                                        PhotoFragment photoFragment = new PhotoFragment(record);
                                        }
                                    });
                        }).start();
                    }
                    break;
                }
//                System.out.println("试一试"+fragment.getClass());
                return true;
            }
        });

        linear_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_id == null) {
                    Toast.makeText(getApplicationContext(), "请先登录！", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, PublishActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
//                intent.putExtra(USER_NAME,)
            }
        });

    }
}
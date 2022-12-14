package com.example.kuanxda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
//登录页面的activity
public class LoginActivity extends AppCompatActivity {

    Button sign_up;
    Button login;

    private String username;
    private String password;

    private EditText et_username;
    private EditText et_password;

    private static final String url = "http://47.107.52.7:88/member/photo/user/login";
    private static final String appId = "2c442f1f2e004dd78f2f586abd8ed6d2";
    private static final String appSecret = "78870a77cac04fb964283a1f58e6c9d41ec97";

    private Context mContext;
    private final Gson gson = new Gson();

    public static String USER_ID = "com.example.kuanxda.USER_ID";
    public static String USER_NAME = "com.example.kuanxda.USER_NAME";
    public static String INTRODUCTION = "com.example.kuanxda.INTRODUCTION";
    public static String AVATAR = "com.example.kuanxda.AVATAR";
    public static String SEX = "com.example.kuanxda.SEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = LoginActivity.this;

        sign_up = findViewById(R.id.btn_sign_up);
        login = findViewById(R.id.btn_login);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                登录逻辑
                if (et_username.getText().toString().trim().isEmpty()){
                    Toast.makeText(mContext,"用户名不可为空，请重新输入",Toast.LENGTH_LONG).show();
                    return;
                }else if (et_password.getText().toString().trim().isEmpty()){
                    Toast.makeText(mContext,"密码不可为空，请重新输入",Toast.LENGTH_LONG).show();
                    return;
                }
                username = et_username.getText().toString().trim();
                password = et_password.getText().toString().trim();

                MediaType jsonType = MediaType.parse("application/json; charset=utf-8");

                OkHttpClient httpClient = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000,TimeUnit.MILLISECONDS)
                        .readTimeout(60000,TimeUnit.MILLISECONDS)
                        .build();
//                键值对参数
//                RequestBody requestBody = new FormBody.Builder()
//                        .add("username", username)
//                        .add("password", password)
//                        .build();
                UrlBuilder ub = new UrlBuilder.Builder()
                        .urlPath(url)
                        .append("username",username)
                        .append("password",password)
                        .build();
                String newUrl = ub.toString();
                Log.d("LOGIN_URL",newUrl);

                Headers headers = new Headers.Builder()
                        .add("appId",appId)
                        .add("appSecret",appSecret)
                        .add("Accept", "application/json, text/plain, */*")
                        .build();

                Request request = new Request.Builder()
                        .url(newUrl)
                        .headers(headers)
//                        content就是参数字符串
                        .post(RequestBody.create(jsonType,""))
                        .build();

                Call call = httpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e("ERR_LOGIN",e.toString());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Type jsonType = new TypeToken<ResponseBody<Object>>(){}.getType();
                        String body = response.body().string();
                        ResponseBody<Object> dataResponse = gson.fromJson(body,jsonType);
                        Log.d("BODY",body);
                        String data = dataResponse.getData().toString();
                        Log.d("DATA",data);
                        Log.d("OK_LOGIN",dataResponse.toString());
                        Looper.prepare();
                        if (dataResponse.getCode()==500){
                            Toast.makeText(mContext,dataResponse.getMsg(),Toast.LENGTH_LONG).show();
                            return;
                        }else if (dataResponse.getCode()==200){
                            Toast.makeText(mContext,dataResponse.getMsg(),Toast.LENGTH_LONG).show();
                        }
                        Intent intent = new Intent();
                        intent.putExtra(USER_ID,dataResponse.getData().getId());
                        intent.putExtra(USER_NAME,dataResponse.getData().getUsername());
                        intent.putExtra(INTRODUCTION,dataResponse.getData().getIntroduce());
                        intent.putExtra(AVATAR,dataResponse.getData().getAvatar());
                        intent.putExtra(SEX,dataResponse.getData().getSex());
                        setResult(2,intent);
                        finish();
                        Looper.loop();
                    }
                });
            }
        });
    }
}
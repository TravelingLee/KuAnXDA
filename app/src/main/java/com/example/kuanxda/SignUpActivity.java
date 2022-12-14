package com.example.kuanxda;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
//注册页面的activity
public class SignUpActivity extends AppCompatActivity {

    Button sign_up;
    Button login;

    private String username;
    private String password;

    private EditText et_username;
    private EditText et_password;
    private EditText et_password1;

    private static final String url = "http://47.107.52.7:88/member/photo/user/register";
    private static final String appId = "2c442f1f2e004dd78f2f586abd8ed6d2";
    private static final String appSecret = "78870a77cac04fb964283a1f58e6c9d41ec97";

    private Context mContext;
    private final Gson gson = new Gson();
    Map<String,Object> param = new HashMap<>();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mContext = SignUpActivity.this;

        login = findViewById(R.id.btn_login);
        sign_up = findViewById(R.id.btn_sign_up);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_password1 = findViewById(R.id.et_password1);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                注册逻辑
                if (!et_password.getText().toString().trim().equals(et_password1.getText().toString().trim())) {
                    Toast.makeText(mContext, "密码不匹配，请重新输入", Toast.LENGTH_LONG).show();
                    return;
                } else if (et_password.getText().toString().isEmpty() || et_password1.getText().toString().isEmpty()) {
                    Toast.makeText(mContext, "密码不可为空，请重新输入", Toast.LENGTH_LONG).show();
                    return;
                } else if (et_password.getText().toString().trim().contains(" ") || et_password1.getText().toString().trim().contains(" ")) {
                    Toast.makeText(mContext, "密码不能包含空格，请重新输入", Toast.LENGTH_LONG).show();
                    return;
                }else if (et_username.getText().toString().trim().isEmpty()){
                    Toast.makeText(mContext,"用户名不可为空，请重新输入",Toast.LENGTH_LONG).show();
                    return;
                }
                username = et_username.getText().toString().trim();
                password = et_password.getText().toString().trim();

                OkHttpClient httpClient = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000,TimeUnit.MILLISECONDS)
                        .build();
//                RequestBody requestBody = new FormBody.Builder()
//                        .add("username", username)
//                        .add("password", password)
//                        .build();
                param.put("username",username);
                param.put("password",password);

                String paramBody = gson.toJson(param);

                MediaType jsonType = MediaType.parse("application/json; charset=utf-8");

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("appId", appId)
                        .addHeader("appSecret", appSecret)
                        .post(RequestBody.create(jsonType,paramBody))
                        .build();

                Call call = httpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e("ERR_SIGN_UP",e.toString());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        System.out.println(response);
                        String body = response.body().string();
                        Type jsonType = new TypeToken<ResponseBody<Object>>(){}.getType();
                        ResponseBody<Object> dataResponse = gson.fromJson(body,jsonType);
                        Log.d("OK_SIGN_UP",dataResponse.toString());
                        Looper.prepare();
                        if (dataResponse.getCode()==200){
                            Toast.makeText(mContext,"注册成功！请前往登录",Toast.LENGTH_LONG).show();
                            finish();
                        }else if (dataResponse.getCode()==500){
                            Toast.makeText(mContext,dataResponse.getMsg(),Toast.LENGTH_LONG).show();
                        }
                        Looper.loop();
                    }
                });
//                    request_result = Objects.requireNonNull(httpClient.newCall(request).execute().body()).string();
            }
        });
    }
}
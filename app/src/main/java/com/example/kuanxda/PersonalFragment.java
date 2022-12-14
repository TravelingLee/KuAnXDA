package com.example.kuanxda;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * “个人中心”页面的fragment
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context mContext;
    private TextView editInfo;
    private ImageView avatar;
    private TextView nickname;
    private TextView id;
    private ImageView sex;
    private TextView introduction;
    private TabLayout tab_bar;

    public static final String KEY_AVATAR = "com.example.kuanxda.user_avatar";
    public static final String KEY_NICKNAME = "com.example.kuanxda.nick_name";
    public static final String KEY_SEX = "com.example.kuanxda.user_sex";
    public static final String KEY_INTRODUCTION = "com.example.kuanxda.introduction";
    public static final String KEY_ID = "com.example.kuanxda.id";

    private String NAME;
    private String ID;
    private int SEX;
    private String INTRODUCTION;
    private String AVATAR;

    View view;
    private RecyclerView mRecyclerView;

    public static boolean have_login = false;

    SharedPreferences spf;
    SharedPreferences.Editor editor;

    private static final String dynamic_url = "http://47.107.52.7:88/member/photo/share/myself";
    private static final String collect_url = "http://47.107.52.7:88/member/photo/collect";
    private static final String like_url = "http://47.107.52.7:88/member/photo/like";

    private static final String appId = "2c442f1f2e004dd78f2f586abd8ed6d2";
    private static final String appSecret = "78870a77cac04fb964283a1f58e6c9d41ec97";

    private int current_page = 0;
    private static final int page_size = 10;
    private static String userId;

    OkHttpClient httpClient = new OkHttpClient();
    Gson gson = new Gson();

    List<SharedResponse.Data.Record> n_record;

    public PersonalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalFragment newInstance(String param1, String param2) {
        PersonalFragment fragment = new PersonalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        spf = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        userId = spf.getString("id", null);
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_personal, container, false);
        }
        mContext = getContext();
        avatar = view.findViewById(R.id.avatar);
        mRecyclerView = view.findViewById(R.id.photo_recycle_view);
        editInfo = view.findViewById(R.id.edit_info);
        nickname = view.findViewById(R.id.username);
        id = view.findViewById(R.id.user_id);
        sex = view.findViewById(R.id.sex);
        introduction = view.findViewById(R.id.textInfo);
        tab_bar = view.findViewById(R.id.tab_bar);

        tab_bar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getSharedList(dynamic_url, 10, 0, "DYNAMIC");
                        break;
                    case 1:
                        getSharedList(collect_url, 10, 0, "COLLECT");
                        break;
                    case 2:
                        getSharedList(like_url, 10, 0, "COLLECT");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        have_login = spf.getString("id", "") != null && !spf.getString("id", "").equals("");
        System.out.println("尼玛的" + spf.getString("id", ""));

        if (have_login) {
            editInfo.setText("编辑资料");
            editInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EditActivity.class);
                    intent.putExtra(KEY_AVATAR, spf.getString("avatar", null));
                    intent.putExtra(KEY_NICKNAME, nickname.getText());
                    if (sex.getResources().getResourceName(R.id.sex).equals("male")) {
                        intent.putExtra(KEY_SEX, "male");
                    } else {
                        intent.putExtra(KEY_SEX, "female");
                    }
                    intent.putExtra(KEY_INTRODUCTION, introduction.getText());
                    intent.putExtra(KEY_ID, id.getText());
                    startActivity(intent);
                }
            });
            nickname.setText(spf.getString("name", "未登录"));
            id.setText(spf.getString("id", ""));
            introduction.setText(spf.getString("introduction", ""));
            if (spf.getInt("sex", 2) == 0) {
                sex.setImageResource(R.drawable.male);
            } else if (spf.getInt("sex", 2) == 1) {
                sex.setImageResource(R.drawable.female);
            }
            if (spf.getString("avatar", null) != null) {
                Glide.with(mContext)
                        .load(spf.getString("avatar", null))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .crossFade()
                        .transform(new GlideCircleTransform(mContext))
                        .into(avatar);
            }
        } else {
            Toast.makeText(mContext, "请先登录！", Toast.LENGTH_LONG).show();
            editInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, 3);
                }
            });
//            Intent intent = new Intent(getActivity(),LoginActivity.class);
//            startActivityForResult(intent,3);
        }

//        Glide.with(mContext)
//                .load(R.drawable.male)
//                .into()
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getSharedList(dynamic_url, 10, 0, "INIT");
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        have_login = true;
        if (data != null) {
            NAME = data.getStringExtra(LoginActivity.USER_NAME);
            ID = data.getStringExtra(LoginActivity.USER_ID);
            INTRODUCTION = data.getStringExtra(LoginActivity.INTRODUCTION);
            SEX = data.getIntExtra(LoginActivity.SEX, 2);
            AVATAR = data.getStringExtra(LoginActivity.AVATAR);
        }

        spf = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        editor = spf.edit();

        editor.putString("name", NAME);
        editor.putString("id", ID);
        editor.putString("introduction", INTRODUCTION);
        editor.putInt("sex", SEX);
        editor.putString("avatar", AVATAR);
        editor.apply();

        editInfo.setText("编辑资料");
        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra(KEY_AVATAR, R.drawable.android_logo);
                intent.putExtra(KEY_NICKNAME, nickname.getText());
                intent.putExtra(KEY_AVATAR, AVATAR);
                if (sex.getResources().getResourceName(R.id.sex).equals("male")) {
                    intent.putExtra(KEY_SEX, "male");
                } else {
                    intent.putExtra(KEY_SEX, "female");
                }
                intent.putExtra(KEY_INTRODUCTION, introduction.getText());
                intent.putExtra(KEY_ID, id.getText());
                startActivity(intent);
            }
        });

        nickname.setText(NAME);
        id.setText(ID);
        introduction.setText(INTRODUCTION);
        if (SEX == 0) {
            sex.setImageResource(R.drawable.male);
        } else if (SEX == 1) {
            sex.setImageResource(R.drawable.female);
        }
        if (AVATAR != null) {
            Glide.with(mContext)
                    .load(data.getStringExtra(LoginActivity.AVATAR))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .transform(new GlideCircleTransform(mContext))
                    .crossFade()
                    .into(avatar);
        }
    }

    private void getSharedList(String url, int pageSize, int currentPage, String TAG) {
        if (have_login)
        new Thread(() -> {
            Headers headers = new Headers.Builder()
                    .add("appId", appId)
                    .add("appSecret", appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();
            UrlBuilder.Builder urlBuilder = new UrlBuilder.Builder()
                    .urlPath(url)
                    .append("current", currentPage)
                    .append("size", pageSize)
                    .append("userId", spf.getString("id",null))
                    .build();

            System.out.println(urlBuilder.toString());

            Request request = new Request.Builder()
                    .url(urlBuilder.toString())
                    .headers(headers)
                    .get()
                    .build();
            httpClient.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e("GET_" + TAG + "_ERR", e.toString());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Log.e("GET_" + TAG + "_OK", response.message());
                            Type jsonType = new TypeToken<SharedResponse<Object>>() {
                            }.getType();
                            String body = response.body().string();
                            System.out.println("首次登录的响应体"+body);
                            SharedResponse<Object> sharedResponse = gson.fromJson(body, jsonType);
                            List<SharedResponse.Data.Record> record = sharedResponse.getData().getRecords();
                            System.out.println(record.toString());
//                            n_record = record;
                            getChildFragmentManager().beginTransaction().add(R.id.photo_fragment_container, new PhotoFragment(record)).commit();

//                                        PhotoFragment photoFragment = new PhotoFragment(record);
                        }
                    });
        }).start();
    }
}
package com.example.kuanxda;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 发现页的fragment
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoundFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private List<SharedResponse.Data.Record> recordList;
    private String[] photos;
    private List<Photo> photoList;

    private static final String dynamic_url = "http://47.107.52.7:88/member/photo/share";
    private static final String appId = "2c442f1f2e004dd78f2f586abd8ed6d2";
    private static final String appSecret = "78870a77cac04fb964283a1f58e6c9d41ec97";
    private static String userId;
    private static final int page_size = 10;
    private static int current_page = 0;

    OkHttpClient httpClient = new OkHttpClient()
            .newBuilder()
            .connectTimeout(60000, TimeUnit.MILLISECONDS)
            .readTimeout(60000,TimeUnit.MILLISECONDS)
            .build();
    Gson gson = new Gson();

    SharedPreferences spf;
    SharedPreferences.Editor editor;

    public FoundFragment(List<SharedResponse.Data.Record> records) {
        // Required empty public constructor
        this.recordList = records;
        System.out.println("呵呵呵"+records.toString());
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public PhotoFragment newInstance(String param1, String param2) {
        PhotoFragment fragment = new PhotoFragment(this.recordList);
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
        userId = spf.getString("id",null);
        View root = inflater.inflate(R.layout.fragment_photo, container, false);
        recyclerView = root.findViewById(R.id.photo_recycle_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        System.out.println("photo_fragment:"+recordList);
        System.out.println("recordList"+recordList);
        PhotoAdapter photoAdapter = new PhotoAdapter(this.getActivity(),recordList);

        recyclerView.addItemDecoration(new MyDecoration());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                current_page ++;
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE){
                    Log.d("------------","It is the END!");
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
                                        recordList.addAll(record);
                                        photoAdapter.notifyDataSetChanged();
//                                        FoundFragment.this.notify();

//                                        PhotoFragment photoFragment = new PhotoFragment(record);
                                    }
                                });
                    }).start();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

//        initData();
        System.out.println("哈哈哈哈哈");
        recyclerView.setAdapter(photoAdapter);


//        recyclerView
        // Inflate the layout for this fragment
        return root;
    }


//    protected boolean isSlideToBottom(RecyclerView recyclerView){
//
//    }

    private void initData() {
        //获取数据
        Iterator<SharedResponse.Data.Record> it = recordList.iterator();
        while (it.hasNext()){
//            photoList.add(new Photo())
        }
    }

    class MyDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int gap = getResources().getDimensionPixelSize(R.dimen.divider);
            outRect.set(gap, gap, gap, gap);
        }
    }
}
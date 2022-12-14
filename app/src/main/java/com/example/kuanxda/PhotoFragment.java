package com.example.kuanxda;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Iterator;
import java.util.List;

/**
 * “个人中心”页面中的图片列表
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoFragment extends Fragment {

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

    public PhotoFragment(List<SharedResponse.Data.Record> records) {
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
        View root = inflater.inflate(R.layout.fragment_photo, container, false);
        recyclerView = root.findViewById(R.id.photo_recycle_view);
        recyclerView.setHasFixedSize(true);

//        initData();
        System.out.println("哈哈哈哈哈");

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        System.out.println("photo_fragment:"+recordList);

        PhotoAdapter photoAdapter = new PhotoAdapter(this.getActivity(),recordList);

        recyclerView.addItemDecoration(new MyDecoration());
        recyclerView.setAdapter(photoAdapter);
//        recyclerView
        // Inflate the layout for this fragment
        return root;
    }

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
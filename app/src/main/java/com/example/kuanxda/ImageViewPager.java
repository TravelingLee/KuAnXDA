package com.example.kuanxda;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//多图片预览的组件
public class ImageViewPager extends ViewPager {
    private List<String> urlList;
    private List<PhotoView> viewList;

    public ImageViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewPager(@NonNull Context context) {
        super(context);
    }

    public void init() {
        setBackgroundColor(Color.BLACK);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setData(List<String> imageList) {
        init();
        System.out.println("图片数组："+imageList);
        if (imageList == null || imageList.size() == 0) {
            return;
        }
        this.urlList = imageList;
        viewList = Stream.generate(() -> {
            PhotoView photoView = new PhotoView(getContext());
            LinearLayout.LayoutParams layoutParams = new
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photoView.setLayoutParams(layoutParams);
            return photoView;
        }).limit(urlList.size())
                .collect(Collectors.toList());
        System.out.println("Done");
        setAdapter(new ImagePageAdapter());
    }

    class ImagePageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return urlList == null ? 0 : urlList.size();
        }

        @NonNull
        @Override
        //实例化item,container 是预览窗口的父视图
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            //获取item视图块
            PhotoView photoView = viewList.get(position);
            //把图片加载到里面去
            Glide.with(getContext())
                    .load(urlList.get(position))
                    .into(photoView);
            //往父视图里面加入一个item
            container.addView(photoView);

            return viewList.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}

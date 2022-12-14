package com.example.kuanxda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
//评论的适配器类
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context mContext;
    private List<CommentRecord.Comment> comment_list;

    public CommentAdapter(Context mContext,List<CommentRecord.Comment> comment_list) {
        this.mContext = mContext;
        this.comment_list = comment_list;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,null);
        return new CommentAdapter.ViewHolder(view,this);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        holder.first_comment = comment_list.get(position).getContent();
        holder.username = comment_list.get(position).getUserName();
    }

    @Override
    public int getItemCount() {
        return comment_list.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        private String username;
        private String first_comment;

        private RecyclerView.Adapter adapter;

        public ViewHolder(@NonNull View itemView,RecyclerView.Adapter adapter) {
            super(itemView);
            this.adapter = adapter;
        }
    }
}

package com.liuqiang.xatulibrary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuqiang.xatulibrary.R;
import com.liuqiang.xatulibrary.common.DoubanBook;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuqiang on 15-9-19.
 */
public class AddbookAdapter extends BaseAdapter {

    private List<DoubanBook> list=new ArrayList<>();
    private Context context;


    public AddbookAdapter(List<DoubanBook> list,Context context){
        this.list=list;
        this.context=context;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DoubanBook book=list.get(position);
        MyViewHolder holder;
        if (convertView == null){
            convertView= LayoutInflater.from(context).inflate(R.layout.addbook,parent,false);
            holder=new MyViewHolder();
            holder.cover=(ImageView)convertView.findViewById(R.id.addbook_cover);
            holder.name=(TextView)convertView.findViewById(R.id.add_name);
            holder.author=(TextView)convertView.findViewById(R.id.add_author);
            convertView.setTag(holder);
        }else {
            holder=(MyViewHolder)convertView.getTag();
        }
        holder.cover.setImageBitmap(book.getCover());
        holder.name.setText(book.getBookname());
        holder.author.setText(book.getAuthor());

        return convertView;
    }

    private class MyViewHolder{
        private ImageView cover;
        private TextView name;
        private TextView author;
    }
}

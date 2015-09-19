package com.liuqiang.xatulibrary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.liuqiang.xatulibrary.common.Book;
import com.liuqiang.xatulibrary.R;

import java.util.List;


/**
 * Created by liuqiang on 15-8-29.
 */
public class BookInfoAdapter extends BaseAdapter {
    private Context context;
    private List<Book> list;

    public BookInfoAdapter(Context context, List<Book> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Book bookInfo = list.get(i);
        View v;
        MyViewHolder viewHolder;
        if (view == null) {
            v = LayoutInflater.from(context).inflate(R.layout.searchinfo, null, false);
            viewHolder = new MyViewHolder();
            viewHolder.name = (TextView) v.findViewById(R.id.info_bookname);
            viewHolder.author = (TextView) v.findViewById(R.id.info_author);
            viewHolder.bookNumber = (TextView) v.findViewById(R.id.info_booknumber);
            viewHolder.number_all = (TextView) v.findViewById(R.id.info_all);
            viewHolder.number_free = (TextView) v.findViewById(R.id.info_free);
            viewHolder.borrow = (Button) v.findViewById(R.id.borrowAgain);
            v.setTag(viewHolder);
        } else {
            v = view;
            viewHolder = (MyViewHolder) v.getTag();
        }
        viewHolder.name.setText(bookInfo.getBookname());
        viewHolder.author.setText(bookInfo.getAuthor());
        viewHolder.bookNumber.setText(bookInfo.getBookNumber());
        viewHolder.number_all.setText(bookInfo.getNumber_all());
        viewHolder.number_free.setText(bookInfo.getNumber_free());
        return v;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    class MyViewHolder {
        TextView name;
        TextView author;
        TextView bookNumber;
        TextView number_all;
        TextView number_free;
        Button borrow;
    }
}

package com.liuqiang.xatulibrary.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.liuqiang.xatulibrary.R;
import com.liuqiang.xatulibrary.common.DoubanBook;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuqiang on 15-9-8.
 */
public class LibraryAdapter extends BaseAdapter {

    private Context context;
    private List<DoubanBook> list = new ArrayList<>();
    MyViewHolder holder;
    private static final String TAG="LibraryAdapter";

    public LibraryAdapter(Context context, List<DoubanBook> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final DoubanBook book = list.get(i);
//        final int free=DataSupport.where("bookname =?", "Clojure编程").find(DoubanBook.class).get(0).getFreePages();
        View v;
        if (view == null) {
            holder = new MyViewHolder();
            v = LayoutInflater.from(context).inflate(R.layout.librarybook, viewGroup, false);
            holder.name = (TextView) v.findViewById(R.id.library_name);
            holder.author = (TextView) v.findViewById(R.id.library_author);
            holder.imageView = (ImageView) v.findViewById(R.id.iv_icon);
            holder.seekBar = (SeekBar) v.findViewById(R.id.library_seekbar);
            holder.allpages = (TextView) v.findViewById(R.id.library_allpages);
            holder.freepages= (TextView) v.findViewById(R.id.library_freepages);
            v.setTag(holder);
        } else {
            v = view;
            holder = (MyViewHolder) v.getTag();
        }
        final String ISBN = book.getISBN_number();
        Log.e(TAG, ISBN);
        holder.name.setText(book.getBookname());
        holder.author.setText(book.getAuthor());
        holder.imageView.setImageBitmap(book.getCover());
        final int free=DataSupport.where("isbn_number =?", ISBN).find(DoubanBook.class).get(0).getFreePages();
//        final int free=5;
        holder.freepages.setText("已经读了" + free + "页");
        Log.e("get freepages",""+ book.getFreePages() );
        holder.seekBar.setMax(book.getAllPages());
        holder.seekBar.setTag(i);
        holder.seekBar.setProgress(free);
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                View v=(View)seekBar.getParent();
                if (v!=null){
                    TextView freepages=(TextView)v.findViewById(R.id.library_freepages);
                    freepages.setText("已经读了"+progress+"页");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ContentValues values = new ContentValues();
                values.put("freepages", seekBar.getProgress());
                Log.e("progress",""+seekBar.getProgress());
                DataSupport.updateAll(DoubanBook.class, values, "isbn_number =?", ISBN);
            }
        });
        holder.allpages.setText("全书共"+book.getAllPages()+"页");
        holder.id = i;
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

    public static class MyViewHolder {
        TextView name;
        TextView author;
        ImageView imageView;
        SeekBar seekBar;
        int id;
        TextView allpages;
        TextView freepages;

    }


}


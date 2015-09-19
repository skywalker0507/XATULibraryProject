package com.liuqiang.xatulibrary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.liuqiang.xatulibrary.common.Book;
import com.liuqiang.xatulibrary.R;
import com.liuqiang.xatulibrary.networking.RenewManager;
import com.txusballesteros.widgets.FitChart;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by liuqiang on 15-7-26.
 */
public class BookListAdapter extends BaseAdapter {
    private Context context;
    private List<Book> list;
    private ImageView image;
    private EditText ed_code;
    private RenewManager manager;
    private TextView times;
    private MyViewHolder viewHolder;
    private BtnClickListener mClickListener = null;
    private long days = 0;
    private long usedDays = 0;

    public BookListAdapter(Context context, List<Book> list, RenewManager manager, BtnClickListener listener) {
        this.context = context;
        this.list = list;
        this.manager = manager;
        this.mClickListener = listener;
    }

    public interface BtnClickListener {
        public abstract void onBtnClick(int position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Book book = list.get(position);
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.bookitem, null, false);
            viewHolder = new MyViewHolder();
            viewHolder.bookName = (TextView) view.findViewById(R.id.tv_bookname);
            viewHolder.author = (TextView) view.findViewById(R.id.tv_author);
            viewHolder.begin = (TextView) view.findViewById(R.id.begintime);
            viewHolder.end = (TextView) view.findViewById(R.id.endtime);
            viewHolder.fitChart = (FitChart) view.findViewById(R.id.fitChart);
            viewHolder.but = (Button) view.findViewById(R.id.borrowAgain);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (MyViewHolder) view.getTag();
        }

        Date date = new Date();//获取时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = sdf.format(date);
        String beginTime = book.getBeginTime();
        String endTime = book.getEndTime();
        try {
            days = getDistanceDays(beginTime, endTime);
            usedDays = getDistanceDays(beginTime, currentTime);
            if (days - usedDays < 4) {
                viewHolder.end.setTextColor(Color.parseColor("#D32F2F"));
            }
            viewHolder.fitChart.setMinValue(0f);
            viewHolder.fitChart.setMaxValue(days);
            viewHolder.fitChart.setValue(usedDays);
        } catch (Exception e) {
            e.printStackTrace();
        }
        times = (TextView) view.findViewById(R.id.borrowtimes);
        times.setText(book.getTimes());
        viewHolder.bookName.setText(book.getBookname());
        viewHolder.author.setText(book.getAuthor());
        viewHolder.begin.setText(book.getBeginTime());
        viewHolder.end.setText(book.getEndTime());
        viewHolder.but.setTag(position);
        if (usedDays > days) {
            Log.e("usedDays", "" + usedDays);
            Log.e("days", "" + days);
            viewHolder.but.setBackgroundColor(Color.parseColor("#9E9E9E"));
            viewHolder.but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "已经超期,不能再续借了", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (Integer.parseInt(book.getTimes()) == 1) {
            viewHolder.but.setBackgroundColor(Color.parseColor("#9E9E9E"));
            viewHolder.but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "你已经续借过一次了，不能再续借了", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            viewHolder.but.setBackgroundColor(Color.parseColor("#4CAF50"));
            viewHolder.but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null)
                        mClickListener.onBtnClick((Integer) view.getTag());
                }
            });
        }
        Log.e("times", book.getTimes());
        /*viewHolder.but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mClickListener != null)
                    mClickListener.onBtnClick((Integer) view.getTag());
            }
        });*/
      /*  Date date=new Date();//获取时间
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String currentTime=sdf.format(date);
        String beginTime=book.getBeginTime();
        String endTime=book.getEndTime();
        try {
            long days=getDistanceDays(beginTime,endTime);
            long usedDays=getDistanceDays(beginTime,currentTime);
            Log.e("freeDay",""+usedDays);
            if (days-usedDays<4){
                viewHolder.end.setTextColor(Color.parseColor("#D32F2F"));
            }
            viewHolder.fitChart.setMinValue(0f);
            viewHolder.fitChart.setMaxValue(days);
            viewHolder.fitChart.setValue(usedDays);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }


    public static class MyViewHolder {
        TextView bookName;
        TextView author;
        TextView begin;
        TextView end;
        Button but;
        FitChart fitChart;
    }

    public static long getDistanceDays(String str1, String str2)
            throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date one;
        Date two;
        long days = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            days = diff / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

}

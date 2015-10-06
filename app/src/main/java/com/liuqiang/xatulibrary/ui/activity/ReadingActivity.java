package com.liuqiang.xatulibrary.ui.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.liuqiang.xatulibrary.R;
import com.liuqiang.xatulibrary.common.DoubanBook;
import com.liuqiang.xatulibrary.common.Times;
import com.squareup.timessquare.CalendarPickerView;
import com.txusballesteros.widgets.FitChart;

import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by liuqiang on 15-9-15.
 */
public class ReadingActivity extends AppCompatActivity {
    private CalendarPickerView calendar;
    private Button bt_begin, bt_end;
    private TextView tv_begin, tv_end, tv_readpages, tv_freepages;
    //    private ImageButton touch;
    private FitChart fitChartDays,fitChartPages;
    private Button setting, detail;
    private Calendar calendar_begin, calendar_end;
    private SimpleDateFormat sdf;
    private MyHandler handler;
    private Date date_begin,date_end;
    private List<Date> dates = new ArrayList<Date>();
    private Date today;
    private String ISBN;
    private DoubanBook book;
    private int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        findView();
        calendar_begin=Calendar.getInstance(Locale.CHINA);
        calendar_end=Calendar.getInstance(Locale.CHINA);
        handler=new MyHandler();
        today=new Date();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        Intent intent = getIntent();
        ISBN = intent.getStringExtra("ISBN");
        book = DataSupport.where("isbn_number = ?",ISBN).find(DoubanBook.class).get(0);
        Log.e("book",book.getBookname()+book.getAllPages()+book.getBegin());
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String b = book.getBegin();
        String e=book.getEnd();
        try {
               if (b==null){
                   Calendar week=Calendar.getInstance();
                   week.setTime(today);
                   week.add(Calendar.DAY_OF_MONTH, -7);
                   Log.e("week", sdf.format(week.getTime()));
                   date_begin=week.getTime();

               }else {
                   date_begin= sdf.parse(b);
               }
            if (e==null){
                Calendar tmp=Calendar.getInstance();
                tmp.setTime(date_begin);
                tmp.add(Calendar.MONTH, 2);
                date_end = tmp.getTime();
            }else {
                date_end=sdf.parse(e);
            }
            calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
                @Override
                public void onDateSelected(Date date) {
                    String d = sdf.format(date);
                    Times time = new Times();
                    time.setDate(d);
                    Log.e("dataeee", d);
                    Toast.makeText(ReadingActivity.this, "hello world", Toast.LENGTH_SHORT).show();
                    if (!checkTimeExist(ISBN, d)) {
                        time.save();
                        book.getTimes().add(time);
                        book.save();
                    }
                }


                @Override
                public void onDateUnselected(Date date) {
                    String d = sdf.format(date);
                    Times time = new Times();
                    time.setDate(d);
                    if (checkTimeExist(""+id, d)) {
                        DataSupport.deleteAll(Times.class, "doubanbook_id = ? and date = ?", ISBN, d);
                    }
                }
            });
            id = DataSupport.where("isbn_number = ?", ISBN).find(DoubanBook.class).get(0).getId();
            List<Times> list=DataSupport.where("doubanbook_id = ?",""+id).find(Times.class);
            for (int i = 1; i < list.size(); i++) {
                Times time = list.get(i);
                String s1 = time.getDate();
                Date date = sdf.parse(s1);
                dates.add(date);
//                time.setDate(s1);
//                time.save();
            }
            Calendar week=Calendar.getInstance();
            week.setTime(today);
            week.add(Calendar.DAY_OF_MONTH, -7);
            Log.e("week", sdf.format(week.getTime()));
            calendar.init(date_begin, today) //
                    .inMode(CalendarPickerView.SelectionMode.MULTIPLE) //
                    .withSelectedDates(dates);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(ReadingActivity.this);
                View view = inflater.inflate(R.layout.dialog_setting, null);
                tv_begin = (TextView) view.findViewById(R.id.reading_begintime);
                tv_end = (TextView) view.findViewById(R.id.reading_endtime);
                bt_begin = (Button) view.findViewById(R.id.reading_setbegintime);
                bt_end = (Button) view.findViewById(R.id.reading_setendtime);
                tv_begin.setText(sdf.format(date_begin));
                tv_end.setText(sdf.format(date_end));
                bt_begin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog date = new DatePickerDialog(ReadingActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                calendar_begin.set(Calendar.YEAR, year);
                                calendar_begin.set(Calendar.MONTH, monthOfYear);
                                calendar_begin.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                date_begin = calendar_begin.getTime();
//                                tv_begin.setText(sdf.format(date_begin));
                            }
                        }, calendar_begin.get(Calendar.YEAR),
                                calendar_begin.get(Calendar.MONTH),
                                calendar_begin.get(Calendar.DAY_OF_MONTH));
                        Message message = new Message();
                        message.what = 0;
                        message.obj = sdf.format(date_begin);
                        handler.sendMessage(message);

                        date.show();
                    }
                });

                bt_end.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ReadingActivity.this, "hello", Toast.LENGTH_SHORT).show();
                        Dialog date = new DatePickerDialog(ReadingActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                calendar_end.set(Calendar.YEAR, year);
                                calendar_end.set(Calendar.MONTH, monthOfYear);
                                calendar_end.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                date_end = calendar_end.getTime();
                                if (date_end.getTime() < today.getTime()) {
                                    Toast.makeText(ReadingActivity.this, "截至日期在今天以前，请重新选取", Toast.LENGTH_SHORT).show();
                                } else {
                                    Message message = new Message();
                                    message.what = 1;
                                    message.obj = sdf.format(date_end);
                                    handler.sendMessage(message);
                                }
                            }
                        }, calendar_end.get(Calendar.YEAR),
                                calendar_end.get(Calendar.MONTH),
                                calendar_end.get(Calendar.DAY_OF_MONTH));
                        date.show();
                    }
                });
                AlertDialog dialog = new AlertDialog.Builder(ReadingActivity.this)
                        .setView(view)
                        .setTitle("设置阅读进度")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ReadingActivity.this,
                                        tv_begin.getText()+"--"+tv_end.getText(),Toast.LENGTH_SHORT).show();
                                ContentValues values = new ContentValues();
                                values.put("begin",tv_begin.getText().toString());
                                values.put("end",tv_end.getText().toString());
                                DataSupport.updateAll(DoubanBook.class,values,"isbn_number = ?", ISBN);
                                Message message=new Message();
                                message.what=3;
                                handler.sendMessage(message);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                dialog.show();

            }
        });

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(ReadingActivity.this);
                View view = inflater.inflate(R.layout.dialog_detail, null);
                fitChartDays = (FitChart) view.findViewById(R.id.reading_fit_days);
                fitChartPages=(FitChart)view.findViewById(R.id.reading_fit_pages);
                try {
                    fitChartDays.setMaxValue(getDistanceDays(sdf.format(date_begin),sdf.format(date_end)));
                    Log.e("days", book.getBegin() + "--" + book.getEnd());
                    fitChartDays.setValue(getDistanceDays(book.getBegin(),sdf.format(new Date())));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                fitChartPages.setMaxValue(book.getAllPages());
                fitChartPages.setValue(book.getFreePages());
                AlertDialog dialog = new AlertDialog.Builder(ReadingActivity.this)
                        .setTitle("查看进度")
                        .setView(view)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                       .create();
                dialog.show();
            }
        });
    }

    protected void findView() {
        setting = (Button) findViewById(R.id.reading_setting);
        detail = (Button) findViewById(R.id.reading_detail);
    }






    private Boolean checkTimeExist(String id, String time) {
        Boolean result = false;
        List<Times> list = DataSupport.where("doubanbook_id = ?", id).find(Times.class);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Times times = list.get(i);
                if (time.equals(times.getDate())) {
                    result = true;
                    break;
                }
            }
        } else {
            result = false;
        }
        return result;
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

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    tv_begin.setText((String)msg.obj);
                    break;
                case 1:
                    tv_end.setText((String) msg.obj);
                case 3:
                    calendar.init(date_begin,today) //
                            .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                            .withSelectedDates(dates);
                    break;

            }
        }
    }

}

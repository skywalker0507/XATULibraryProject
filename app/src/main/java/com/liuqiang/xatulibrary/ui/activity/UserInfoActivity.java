package com.liuqiang.xatulibrary.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liuqiang.xatulibrary.R;
import com.liuqiang.xatulibrary.base.BaseActivity;
import com.liuqiang.xatulibrary.common.Book;
import com.liuqiang.xatulibrary.common.UserData;
import com.liuqiang.xatulibrary.networking.GetUserInfoThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by liuqiang on 15-7-21.
 */
public class UserInfoActivity extends BaseActivity {
    private Button loadout;
    private MyHander hander;
    private List<Book> books = new ArrayList<>();
    private TextView info_name, info_studentID, info_colloge, info_studentType,
            info_begin, info_end, info_needToReturn, hadOverDay, info_allCanBorrow,
            info_allBorrowed, info_currentBorrowed;
    private ProgressDialog progressDialog;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在加载数据");
        progressDialog.setMessage("加载中，请稍后...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        hander = new MyHander();
        GetUserInfoThread thread = new GetUserInfoThread(hander);
        new Thread(thread).start();
    }

    @Override
    protected void findView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar1);
        imageView = (ImageView) findViewById(R.id.backdrop1);

        loadout = (Button) findViewById(R.id.loadout);
        info_name = (TextView) findViewById(R.id.info_name);
        info_studentID = (TextView) findViewById(R.id.info_studentID);
        info_colloge = (TextView) findViewById(R.id.info_colloge);
        info_studentType = (TextView) findViewById(R.id.info_studentType);
        info_begin = (TextView) findViewById(R.id.info_begin);
        info_end = (TextView) findViewById(R.id.info_end);
        info_needToReturn = (TextView) findViewById(R.id.info_needToReturn);
        hadOverDay = (TextView) findViewById(R.id.hadOverDay);
        info_allCanBorrow = (TextView) findViewById(R.id.info_allCanBorrow);
        info_allBorrowed = (TextView) findViewById(R.id.info_allBorrowed);
        info_currentBorrowed = (TextView) findViewById(R.id.info_currentBorrowed);

    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Glide.with(this).load((R.drawable.header3)).centerCrop().into(imageView);
    }

    @Override
    protected void setOnClick() {
        loadout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserData.clearData();
//                MyApplication myApplication=(MyApplication)getApplicationContext();
//                myApplication.clearList();
                startActivity(new Intent(UserInfoActivity.this, UserLoginActivity.class));
                finish();
            }
        });
    }

    private class MyHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Map<String, String> userInfo = (Map) msg.obj;
                    info_studentID.setText(userInfo.get("studentID"));
                    info_name.setText(userInfo.get("studentName"));
                    collapsingToolbar.setTitle(userInfo.get("studentName"));
                    info_studentType.setText(userInfo.get("stydentType"));
                    info_colloge.setText(userInfo.get("colloge"));
                    break;
                case 2:
                    Map<String, String> borrowInfo = (Map) msg.obj;
                    info_needToReturn.setText(borrowInfo.get("allBook"));
                    hadOverDay.setText(borrowInfo.get("overBook"));
                    info_allBorrowed.setText(borrowInfo.get("allBorrowedBooks"));
                    info_allCanBorrow.setText(borrowInfo.get("maxBookNum"));
                    info_begin.setText(borrowInfo.get("begin"));
                    info_end.setText(borrowInfo.get("end"));
                    info_currentBorrowed.setText("当前借阅量为 " + UserData.getBorrowNumber() + " 本");
                    progressDialog.dismiss();
                    break;


            }

        }
    }
}

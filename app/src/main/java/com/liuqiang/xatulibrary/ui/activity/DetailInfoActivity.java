package com.liuqiang.xatulibrary.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.liuqiang.xatulibrary.R;
import com.liuqiang.xatulibrary.base.BaseActivity;
import com.liuqiang.xatulibrary.common.Book;
import com.liuqiang.xatulibrary.common.DoubanBook;
import com.liuqiang.xatulibrary.common.UserData;
import com.liuqiang.xatulibrary.networking.GetDetailInfo;

import org.litepal.AddToLruCacheUtil;

/**
 * Created by liuqiang on 15-9-4.
 */
public class DetailInfoActivity extends BaseActivity {
    private FloatingActionMenu menu;
    private Handler mUiHandler = new Handler();
    private FloatingActionButton baidu;
    private FloatingActionButton douban;
    private Book book;
    //    private WebView webView;
    private String douban_url;
    private ImageView header;
    private MyHandler handler;
    private GetDetailInfo getDetailInfo;
    private ImageView detail_cover;
    private TextView detail_average, detail_numRaters, detail_tags,
            detail_author, detail_publisher, detail_pubdate, detail_pages, detail_isbn, detail_price,
            detail_summary;
    private ProgressDialog progressDialog;
    public AddToLruCacheUtil util;
    private CollapsingToolbarLayout collapsingToolbar;
    private android.support.design.widget.FloatingActionButton addtolib;
    private Toolbar toolbar;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nfo);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在加载数据");
        progressDialog.setMessage("加载中，请稍后...");
        progressDialog.setCancelable(true);
//        progressDialog.show();
        initView();
        Intent intent = getIntent();

        handler = new MyHandler();
        getDetailInfo = new GetDetailInfo(DetailInfoActivity.this);
        String ISBN=intent.getStringExtra("add_isbn");
        if (!ISBN.isEmpty()){
            getDetailInfo.getAllInfo(ISBN,handler,10);
        }else {
            book = (Book) intent.getSerializableExtra("book_message");
            util = new AddToLruCacheUtil();
            String url= UserData.getUrl();
            String doubanUrl=util.getUrlCache(url);
            if (doubanUrl!=null){

            }
            getDetailInfo.GetDoubanUrl(url, handler);
        }
    }


    private void getIntentText() {
        Intent intent = getIntent();
        String ISBN=intent.getStringExtra("add_isbn");
        if (ISBN.isEmpty()){

        }
        book = (Book) intent.getSerializableExtra("book_message");
    }

    @Override
    protected void findView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        header = (ImageView) findViewById(R.id.backdrop);


        detail_cover = (ImageView) findViewById(R.id.detail_cover);
        detail_average = (TextView) findViewById(R.id.detail_average);
        detail_numRaters = (TextView) findViewById(R.id.detail_numRaters);
        detail_tags = (TextView) findViewById(R.id.detail_tags);
        detail_author = (TextView) findViewById(R.id.detail_author);
        detail_publisher = (TextView) findViewById(R.id.detail_publisher);
        detail_pubdate = (TextView) findViewById(R.id.detail_pubdate);
        detail_pages = (TextView) findViewById(R.id.detail_pages);
        detail_isbn = (TextView) findViewById(R.id.detail_isbn);
        detail_price = (TextView) findViewById(R.id.detail_price);
        detail_summary = (TextView) findViewById(R.id.detail_summary);


        menu = (FloatingActionMenu) findViewById(R.id.indetail).findViewById(R.id.search_menu);
        baidu = (FloatingActionButton) findViewById(R.id.indetail).findViewById(R.id.baidu);
        douban = (FloatingActionButton) findViewById(R.id.indetail).findViewById(R.id.douban);
        addtolib=(android.support.design.widget.FloatingActionButton)findViewById(R.id.fab_addtolib);
    }

    @Override
    protected void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Glide.with(this).load((R.drawable.header1)).centerCrop().into(header);

        menu.hideMenuButton(false);
        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                menu.showMenuButton(true);
            }
        }, 100);
        menu.setClosedOnTouchOutside(true);
    }

    @Override
    protected void setOnClick() {

        baidu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = book.getBookname();
                Log.e("------>>>>>>>", key);
                String url = "http://www.baidu.com.cn/s?wd=" + key + "&cl=3";
                Intent intent = new Intent(DetailInfoActivity.this, SearchActivity.class);
                Log.e("------>>>>>>>", url);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        douban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailInfoActivity.this, SearchActivity.class);
                intent.putExtra("url", douban_url);
                startActivity(intent);


            }
        });

        addtolib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog=new AlertDialog.Builder(DetailInfoActivity.this)
                        .setMessage("是否将此书加入我的图书馆?")
                        .setPositiveButton("加入", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                            book.save();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    douban_url = (String) msg.obj;
                    String ISBN = douban_url.replace("http://book.douban.com/isbn/", "").replace("/", "");
                    getDetailInfo.getBookInfo(handler,ISBN);
                    Log.e("douban url", douban_url);
                    break;
                case 2:
                case 1000:
                    final DoubanBook book = (DoubanBook) msg.obj;
                    detail_cover.setImageBitmap(book.getCover());
                    detail_author.setText(book.getAuthor());
                    detail_average.setText(book.getAverage());
                    detail_isbn.setText(book.getISBN_number());
                    detail_numRaters.setText(book.getNumRaters());
                    detail_pages.setText(String.valueOf(book.getAllPages()));
                    detail_price.setText(book.getPrice());
                    detail_pubdate.setText(book.getPubdate());
                    detail_publisher.setText(book.getPublisher());
                    detail_summary.setText(book.getSummary());
                    collapsingToolbar.setTitle(book.getBookname());
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < book.getTags().size(); i++) {
                        builder.append("  " + book.getTags().get(i));
                    }
                    detail_tags.setText(builder.toString());
                    break;
                case 10:
                    Toast.makeText(DetailInfoActivity.this, "未找到相关数据！", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                default:
                    break;

            }

        }
    }
}

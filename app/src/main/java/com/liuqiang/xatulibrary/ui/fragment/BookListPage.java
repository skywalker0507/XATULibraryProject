package com.liuqiang.xatulibrary.ui.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.liuqiang.xatulibrary.R;
import com.liuqiang.xatulibrary.adapter.BookListAdapter;
import com.liuqiang.xatulibrary.base.BaseFragment;
import com.liuqiang.xatulibrary.common.Book;
import com.liuqiang.xatulibrary.common.DoubanBook;
import com.liuqiang.xatulibrary.common.NetworkState;
import com.liuqiang.xatulibrary.common.UserData;
import com.liuqiang.xatulibrary.networking.GetBookListThread;
import com.liuqiang.xatulibrary.networking.GetDetailInfo;
import com.liuqiang.xatulibrary.networking.RenewManager;
import com.liuqiang.xatulibrary.ui.activity.DetailInfoActivity;
import com.liuqiang.xatulibrary.util.DBUtil;

import org.litepal.AddToLruCacheUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuqiang on 15-7-20.
 */
public class BookListPage extends BaseFragment {
    private List<Book> books = new ArrayList<>();
    private ListView listView;
    private TextView noBook;
    private RenewManager manager;
    private ProgressDialog dialog;
    private GetListHandler handler;
    private DoRenewHandler doRenewHandler;
    private ImageView image;
    private EditText ed_code;
    private BookListAdapter adapter;
//    private DBManager dbManager;
    private GetDetailInfo getDetailInfo;
    public AddToLruCacheUtil util;
    private DBUtil dbUtil;

    public static BookListPage newInstance() {
        BookListPage mFragment = new BookListPage();
        return mFragment;
    }

    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_booklist;
    }

    @Override
    public void findView(View view) {
        noBook = (TextView) view.findViewById(R.id.tv_noBook);
    }

    @Override
    public void initView() {


    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new GetListHandler();
        doRenewHandler = new DoRenewHandler();
        manager = new RenewManager();
        util = new AddToLruCacheUtil();
        dbUtil=new DBUtil();
        if (NetworkState.isNetworkAvailable(getActivity())){
            getDetailInfo = new GetDetailInfo(getActivity());
            GetBookListThread thread = new GetBookListThread(UserData.getUserCookie(), handler);
            new Thread(thread).start();
        }else {
            Toast.makeText(getActivity(),"当前网络不可用",Toast.LENGTH_SHORT).show();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booklist, container, false);
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("正在加载数据");
        dialog.setMessage("正在加载数据,请稍后。。。");
        dialog.show();
        listView = (ListView) view.findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Book book = books.get(position);
                UserData.setUrl(book.getUrl());
                Intent intent = new Intent(getActivity(), DetailInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("book_message", book);
                intent.putExtra("type","normal");
                intent.putExtras(bundle);

                startActivity(intent);
            }

        });
//        }

        return view;
    }

    private class GetListHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    books = (List<Book>) msg.obj;
                    UserData.setBorrowNumber(String.valueOf(books.size()));
//                    dbManager = new DBManager(getActivity());
//                    Log.e("db count", "" + dbManager.getCount());
                    if (UserData.isFirstLoad()) {
                        for (int i = 0; i < books.size(); i++) {
                            Book book = books.get(i);
//                            dbManager.addToDB(book.getBookname(), book.getAuthor(), "",
//                                    0, 0, book.getBeginTime(), "", book.getUrl(),0);
                            DoubanBook doubanBook=new DoubanBook();
                            doubanBook.setBookname(book.getBookname());
                            doubanBook.setAuthor(book.getAuthor());
                            doubanBook.setBegin(book.getBeginTime());
                            doubanBook.setUrl(book.getUrl());
                            doubanBook.save();
                            getDetailInfo.GetISBN(book.getUrl());
                        }

                        UserData.setFirstLoad(false);
                    }else {
                        for (int i=0;i<books.size();i++){
                            Book book = books.get(i);
                            if(!dbUtil.checkISBNExists(book.getBookname())){
                                getDetailInfo.GetISBN(book.getUrl());
                            }
                        }
                    }
                    adapter = new BookListAdapter(getActivity(), books, manager,
                            new BookListAdapter.BtnClickListener() {
                                @Override
                                public void onBtnClick(final int position) {
                                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                                    View v = inflater.inflate(R.layout.alertdialog, null);
                                    image = (ImageView) v.findViewById(R.id.alert_image);
                                    ed_code = (EditText) v.findViewById(R.id.alert_code);
                                    manager.getVerifyImg(doRenewHandler);
                                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                            .setTitle("续借验证码")
                                            .setView(v)
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            })
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    String bar_code = books.get(position).getMaskNumber();
                                                    String check = books.get(position).getCheck();
                                                    String code = ed_code.getText().toString();
                                                    String time = String.valueOf(System.currentTimeMillis());
                                                    manager.doRenew(bar_code, check, code, time, doRenewHandler);
                                                }
                                            }).create();
                                    dialog.show();
                                }
                            });
                    listView.setAdapter(adapter);
                    dialog.dismiss();
                    break;
                case 1:
                    noBook.setVisibility(View.VISIBLE);
                    UserData.setBorrowNumber("0");
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    private class DoRenewHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    image.setImageBitmap((Bitmap) msg.obj);
                    break;
                case 2:
                    Toast.makeText(getActivity(), "续借成功！", Toast.LENGTH_SHORT).show();
                    GetBookListThread thread = new GetBookListThread(UserData.getUserCookie(), handler);
                    new Thread(thread).start();
                    break;
                case 10:
                    Toast.makeText(getActivity(), "验证码错误", Toast.LENGTH_SHORT).show();
                    break;
                case 11:
                    Toast.makeText(getActivity(), "你已经续借过了，不能再续借了", Toast.LENGTH_SHORT).show();
                    break;
                case 12:
                    Toast.makeText(getActivity(), "当前图书已超期，不能续借了", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;

            }
        }
    }


}

package com.liuqiang.xatulibrary.ui.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.liuqiang.xatulibrary.R;
import com.liuqiang.xatulibrary.adapter.LibraryAdapter;
import com.liuqiang.xatulibrary.common.DoubanBook;
import com.liuqiang.xatulibrary.networking.GetDetailInfo;
import com.liuqiang.xatulibrary.ui.activity.DetailInfoActivity;
import com.liuqiang.xatulibrary.ui.activity.MainActivity;
import com.liuqiang.xatulibrary.ui.activity.ReadingActivity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuqiang on 15-9-9.
 */
public class LibraryFragment extends Fragment {
    private ListView listView;
    private LibraryAdapter adapter;
//    private DBManager dbManager;
    private GetDetailInfo getDetailInfo;
    private List<DoubanBook> books = new ArrayList<>();
    private int count;
    private MyHandler handler;
    private ProgressDialog dialog;
    private int flag = 2;
    private FloatingActionMenu menu;
    private FloatingActionButton scan;
    private FloatingActionButton add;
    private Handler mUiHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDetailInfo = new GetDetailInfo(getActivity());
        DoubanBook last= DataSupport.findLast(DoubanBook.class);
        count=last.getId();
        Log.e("count", "" + count);
        handler = new MyHandler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_lib, container, false);
        listView = (ListView) view.findViewById(R.id.lib_listview);
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("正在加载数据");
        dialog.setMessage("正在加载数据,请稍后。。。");
        dialog.show();
        menu=(FloatingActionMenu)view.findViewById(R.id.add_menu);
        scan=(FloatingActionButton)view.findViewById(R.id.library_scan);
        add=(FloatingActionButton)view.findViewById(R.id.library_add);
        if (count > 0) {
//            String isbn = dbManager.readBookInfo(1, "isbn");
            DoubanBook doubanBook=DataSupport.find(DoubanBook.class, 1);
            String isbn=doubanBook.getISBN_number();
            getDetailInfo.getAllInfo(isbn, handler,1);

        }

        menu.hideMenuButton(false);
        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                menu.showMenuButton(true);
            }
        }, 100);
        menu.setClosedOnTouchOutside(true);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator.forSupportFragment(LibraryFragment.this).initiateScan();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater1=LayoutInflater.from(getActivity());
                View view1=inflater1.inflate(R.layout.dialog_addbook, null);
                SearchView searchView=(SearchView)view1.findViewById(R.id.add_search);
                ListView searchlist=(ListView)view1.findViewById(R.id.add_listview);
//                AddbookAdapter addbookAdapter=new AddbookAdapter();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
                AlertDialog dialog=new AlertDialog.Builder(getActivity())
                        .setView(view1)
                        .setTitle("请输入书名")
                        .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "ok", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getActivity(),ReadingActivity.class);
                intent.putExtra("ID", position + 1);
                Log.e("id position", id + "--" + position);
                MainActivity activity=(MainActivity)getActivity();
                activity.startActivity(intent);
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("hello world","helo");
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if((result==null)||(result.getContents()==null))
        {
            return ;
        }
        Intent intent=new Intent(getActivity(), DetailInfoActivity.class);
        intent.putExtra("add_isbn",result.getContents());
        startActivity(intent);
        Log.e("OUTPUT", result.getContents());
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 500:
                    DoubanBook book = (DoubanBook) msg.obj;
//                    Log.e("name in count",book.getBookname());
                    books.add(book);
                    if (flag <=count+1) {
                        if (flag<=count){
                            Log.e("flag", "" + flag + "--" + count);
//                            String isbn = dbManager.readBookInfo(flag, "isbn");
                            DoubanBook doubanBook=DataSupport.find(DoubanBook.class, flag);
                            String isbn=doubanBook.getISBN_number();
                            flag = flag + 1;
                            Log.e("isbn", isbn);
                                getDetailInfo.getAllInfo(isbn, handler,1);
                        }else {
                            dialog.dismiss();
                            adapter=new LibraryAdapter(getActivity(),books);
                            listView.setAdapter(adapter);
                        }
                    }
                    break;
            }
        }
    }


}






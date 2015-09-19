package com.liuqiang.xatulibrary.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.liuqiang.xatulibrary.common.Book;
import com.liuqiang.xatulibrary.R;
import com.liuqiang.xatulibrary.common.UserData;
import com.liuqiang.xatulibrary.adapter.BookInfoAdapter;
import com.liuqiang.xatulibrary.networking.AnySearchThread;
import com.liuqiang.xatulibrary.networking.SearchThread;
import com.liuqiang.xatulibrary.ui.activity.DetailInfoActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liuqiang on 15-7-20.
 */
public class SearchPage extends Fragment implements SearchView.OnQueryTextListener {

    private SearchView searchView;
    private ListView listView;
    private List<Book> list = new ArrayList<>();
    private List<Book> allList = new ArrayList<>();
    private List<String> history = new ArrayList<>();
    private BookInfoAdapter adapter;
    private TextView keyword;
    private TextView count;
    private String number;
    private int counter = 1;
    private int numbers;
    private RadioGroup group;
    private RadioButton title;
    private RadioButton author;
    private RadioButton ISBN;
    private RadioButton any;
    private View loadMoreView;
    int flag = 0;
    private LinearLayout linearLayout;
    private int visibleLastIndex = 0; // 最后的可视项索引
    @SuppressWarnings("unused")
    private int visibleItemCount; // 当前窗口可见项总数

    private String lastSearched = "";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    list.clear();
                    list = (List<Book>) msg.obj;
                   /* adapter=new BookInfoAdapter(getActivity(),allList);
                    listView.setAdapter(adapter);*/
                    listView.setSelection(visibleLastIndex);
                    doAdd(list);
                    keyword.setText(searchView.getQuery());
                    linearLayout.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    number = (String) msg.obj;
                    count.setText(number);
                    numbers = Integer.parseInt(number);
                    break;
                case 10:
                    linearLayout.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "未搜到相关数据", Toast.LENGTH_SHORT).show();
                    listView.removeFooterView(loadMoreView);

                default:
                    break;
            }
        }
    };

    public static SearchPage newInstance() {
        SearchPage mFragment = new SearchPage();
        return mFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_search, container, false);
        loadMoreView = inflater.inflate(R.layout.loading_view, container, false);
        listView = (ListView) view.findViewById(R.id.listview_search);
        adapter = new BookInfoAdapter(getActivity(), allList);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            boolean isLastRow = false;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int lastIndex = adapter.getCount(); // 加上底部的loadMoreView项
                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (visibleLastIndex == lastIndex)) {
                    if (numbers <= 20) {
                        listView.removeFooterView(loadMoreView);
                    } else {
                        counter = counter + 1;
                        flag = 1;
                        if (getType() == "any") {
                            AnySearchThread thread = new AnySearchThread(searchView.getQuery().toString(), handler, counter);
                            new Thread(thread).start();
                        } else {
                            SearchThread thread = new SearchThread(getType(), searchView.getQuery().toString(), handler, counter);
                            new Thread(thread).start();
                        }
                    }

                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCounts, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
                    isLastRow = true;
                }
                visibleItemCount = visibleItemCounts;
                visibleLastIndex = firstVisibleItem + visibleItemCounts - 1;

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Book book = allList.get(i);
                UserData.setUrl(book.getUrl());
                Intent intent = new Intent(getActivity(), DetailInfoActivity.class);
                intent.putExtra("flag", 2);
                Bundle bundle = new Bundle();
                bundle.putSerializable("book_message", book);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
        linearLayout = (LinearLayout) view.findViewById(R.id.layout1);
        keyword = (TextView) view.findViewById(R.id.tv_keywords);
        count = (TextView) view.findViewById(R.id.tv_count);
        searchView = (SearchView) view.findViewById(R.id.searchKeyword);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        group = (RadioGroup) view.findViewById(R.id.radiogroup);
        title = (RadioButton) view.findViewById(R.id.rd_bookname);
        author = (RadioButton) view.findViewById(R.id.rd_author);
        ISBN = (RadioButton) view.findViewById(R.id.rd_ISBN);
        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query != lastSearched) {
            allList.clear();
            counter = 1;
        }
        if (query.isEmpty()) {
            Toast.makeText(getActivity(), "关键词不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (query.length() < 2) {
            Toast.makeText(getActivity(), "关键词过短", Toast.LENGTH_SHORT).show();
            return false;
        }
      /* if (lastSearched.equals(query)) {
            searchView.clearFocus();
            return false;
        }*/
        lastSearched = query;
        switch (getType()) {
            case "any":
                AnySearchThread thread = new AnySearchThread(query, handler, counter);
                new Thread(thread).start();
                break;
            default:
                SearchThread thread1 = new SearchThread(getType(), query, handler, counter);
                new Thread(thread1).start();
        }
        return false;
    }

    protected void doAdd(List<Book> list) {
        allList.addAll(list);
        adapter.notifyDataSetChanged();
        Log.d("adapter number list", number + "----" + adapter.getCount() + "--" + allList.size() + list.size());
        if (adapter.getCount() == numbers) {
            if (listView.getFooterViewsCount() == 1)
                listView.removeFooterView(loadMoreView);
        } else {
            if (listView.getFooterViewsCount() == 0)
                listView.addFooterView(loadMoreView);
        }
    }

    private String getType() {
        int index = group.getCheckedRadioButtonId();
        switch (index) {
            case R.id.rd_bookname:
                return "title";

            case R.id.rd_author:
                return "author";
            case R.id.rd_ISBN:
                return "isbn";
            default:
                return "any";
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        return false;

    }
}

package com.liuqiang.xatulibrary.ui.activity;


import android.content.Intent;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.liuqiang.xatulibrary.R;
import com.liuqiang.xatulibrary.base.BaseActivity;


/**
 * Created by liuqiang on 15-7-31.
 */
public class SearchActivity extends BaseActivity {
    private WebView webView;
    @Override
    protected void findView() {
        webView = (WebView) findViewById(R.id.web_view);
    }

    @Override
    protected void initView() {

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        Log.e("url----->>", url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl(url);
    }

    @Override
    protected void setOnClick() {

    }
}

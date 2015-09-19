package com.liuqiang.xatulibrary.ui.activity;

import android.content.Intent;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.liuqiang.xatulibrary.R;
import com.liuqiang.xatulibrary.base.BaseActivity;
import com.liuqiang.xatulibrary.common.UserData;

/**
 * Created by liuqiang on 15-9-5.
 */
public class RegisterActivity extends BaseActivity {

    private WebView webView;
    private static String url = "http://222.25.12.227:8080/reader/redr_con.php";
    @Override
    protected void findView() {
        webView = (WebView) findViewById(R.id.wb_register);
    }

    @Override
    protected void initView() {

        Intent intent = getIntent();
        String cookie = intent.getStringExtra("cookie");
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
        cookieSyncManager.sync();
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.setCookie(url, "PHPSESSID=" + cookie);
        Log.e("cookie", "" + UserData.getUserCookie());
        CookieSyncManager.getInstance().sync();
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setBlockNetworkImage(false);
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

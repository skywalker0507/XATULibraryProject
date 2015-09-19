package com.liuqiang.xatulibrary.ui.fragment;

import android.util.Log;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.liuqiang.xatulibrary.R;
import com.liuqiang.xatulibrary.base.BaseFragment;
import com.liuqiang.xatulibrary.common.UserData;

/**
 * Created by liuqiang on 15-7-20.
 */
public class Redr_lostFragment extends BaseFragment {
    private WebView webView;
    private String URL = "http://222.25.12.227:8080/reader/redr_lost.php";
/*
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_redr_lost, container, false);
        webView = (WebView) rootview.findViewById(R.id.web_redr_lost);
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(getActivity());
        cookieSyncManager.sync();
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.setCookie(URL, "PHPSESSID=" + UserData.getUserCookie());
        Log.e("cookie", "" + UserData.getUserCookie());
        CookieSyncManager.getInstance().sync();
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
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

        webView.loadUrl(URL);
        return rootview;
    }*/

    @Override
    public int onSetLayoutId() {
        return R.id.web_redr_lost;
    }

    @Override
    public void findView(View view) {
        webView = (WebView) view.findViewById(R.id.web_redr_lost);
    }

    @Override
    public void initView() {
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(getActivity());
        cookieSyncManager.sync();
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.setCookie(URL, "PHPSESSID=" + UserData.getUserCookie());
        Log.e("cookie", "" + UserData.getUserCookie());
        CookieSyncManager.getInstance().sync();
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
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

        webView.loadUrl(URL);
    }

}

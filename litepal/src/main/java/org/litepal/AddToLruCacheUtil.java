package org.litepal;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by liuqiang on 15-9-13.
 */
public class AddToLruCacheUtil {

    private LruCache<String, String> mJsonCache;
    private LruCache<String, Bitmap> mBitmapCache;
    private LruCache<String,String> mUrlCache;

    public AddToLruCacheUtil(){

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mJsonCache = new LruCache<String, String>(cacheSize);
        mBitmapCache = new LruCache<String, Bitmap>(cacheSize);
        mUrlCache=new LruCache<String, String>(cacheSize);
    }


    public void addJsonLruCache(String key, String value) {
        if (getJsonLruCache(key)==null)
        mJsonCache.put(key, value);
    }

    public void addUrlCache(String key,String url){
        if (getUrlCache(key)==null)
            mUrlCache.put(key,url);
    }

    public void addBitmapLruCache(String key, Bitmap value) {
        if (getBitmapLruCache(key)==null)
        mBitmapCache.put(key, value);
    }

    public String getJsonLruCache(String key) {
        return mJsonCache.get(key);
    }

    public Bitmap getBitmapLruCache(String key) {
        return mBitmapCache.get(key);
    }

    public String getUrlCache(String key){
        return mUrlCache.get(key);
    }
}

package com.liuqiang.xatulibrary.networking;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.liuqiang.xatulibrary.common.DoubanBook;
import com.liuqiang.xatulibrary.common.GetDiskCache;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.litepal.AddToLruCacheUtil;
import org.litepal.LitePalApplication;
import org.litepal.crud.DataSupport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import libcore.io.DiskLruCache;


/**
 * Created by liuqiang on 15-9-4.
 */
public class GetDetailInfo {
    public static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko";
//    public String ISBN;
    private DiskLruCache mDiskLruCache;
    private GetDiskCache getDiskCache;
    private AddToLruCacheUtil util;

    private Context context;
    public GetDetailInfo(Context context) {
        LitePalApplication application=(LitePalApplication)LitePalApplication.getContext();
        this.util=application.getUtil();
        this.context=context;
    }

    public void GetDoubanUrl(final String library_url, final Handler handler) {
        Thread thread = new Thread(new Runnable() {
            String url;
            @Override
            public void run() {
                String tmp=util.getUrlCache(library_url);
                    if (tmp!=null){
                        url="http://book.douban.com/isbn/"+tmp;
                        Log.e("tmp",tmp);
                    }else {

                        try {
                            HttpClient httpClient = new DefaultHttpClient();
                            HttpPost post = new HttpPost(library_url);
                            Log.e("get douban ", library_url);
                            post.setHeader("User-Agent", USER_AGENT);
                            post.setHeader("content-encoding", "gzip");
                            HttpResponse response = null;
                            response = httpClient.execute(post);
                            if (response.getStatusLine().getStatusCode() == 200) {
                                HttpEntity entity = response.getEntity();
                                String s = EntityUtils.toString(entity, "UTF-8");
                                Document document = Jsoup.parse(s);
                                url = document.select("div.left_content>ul").select("li").
                                        get(0).select("a").attr("href");
                                Log.e("getDetailInfo----->>>", url);
                                util.addUrlCache(library_url,url);
                                Log.e("get url", util.getUrlCache(library_url));
//                                ISBN = url.replace("http://book.douban.com/isbn/", "").replace("/", "");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                Message message = new Message();
                message.what = 1;
                message.obj = url;
                Log.e("getDetailInfo finally", url);
                handler.sendMessage(message);
            }
        });
        thread.start();

    }

    public void GetISBN(final String library_url) { //run in BookListPage,write isbn to DB
        Thread thread = new Thread(new Runnable() {
            String url;
            String ISBN;
            @Override
            public void run() {
                String tmp=util.getUrlCache(library_url);
                if (tmp!=null){
                    ISBN=tmp;
                }else {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost post = new HttpPost(library_url);
                    Log.e("get douban ", library_url);
                    post.setHeader("User-Agent", USER_AGENT);
                    post.setHeader("content-encoding", "gzip");
                    HttpResponse response = null;
                    try {
                        response = httpClient.execute(post);
                        if (response.getStatusLine().getStatusCode() == 200) {
                            HttpEntity entity = response.getEntity();
                            String s = EntityUtils.toString(entity, "UTF-8");
                            Document document = Jsoup.parse(s);
                            url = document.select("div.left_content>ul").select("li").
                                    get(0).select("a").attr("href");
                            Log.e("getDetailInfo", url);
                            ISBN = url.replace("http://book.douban.com/isbn/", "").replace("/", "");
                            util.addUrlCache(library_url, ISBN);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                dbManager=new DBManager(context);
//                dbManager.updateBookInfoByURL(library_url, "isbn", ISBN);
                ContentValues values = new ContentValues();
                values.put("isbn_number", ISBN);
                DataSupport.updateAll(DoubanBook.class, values, "url = ? ", library_url);
            }
        });
        thread.start();

    }

    public void getBookInfo(final Handler handler,final String ISBN) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DoubanBook book;
                String jsonObject="";
                String douban_url = "https://api.douban.com/v2/book/isbn/:" + ISBN;
                StringBuilder builder = new StringBuilder();
                String tmp=util.getJsonLruCache(ISBN);
                    try {
                        if (tmp!=null){
                            jsonObject=tmp;
                        }else {
                            URL url = new URL(douban_url);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setReadTimeout(8000);
                            connection.setConnectTimeout(8000);
                            int statusCode = connection.getResponseCode();
                            Log.e("statusCode", "" + statusCode);
                            if (statusCode == 200) {
                                String line;
                                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                while ((line = reader.readLine()) != null) {
                                    builder.append(line);
                                }
                                jsonObject = builder.toString();
                                util.addJsonLruCache(ISBN,jsonObject);
                            } else {
                                Message message = new Message();
                                message.what = 10;
                                handler.sendMessage(message);
                            }
                        }
                            book = parseBookInfo(jsonObject,ISBN);
                            Message message = new Message();
                            message.what = 2;
                            message.obj = book;
                            handler.sendMessage(message);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        });
        thread.start();

    }

    public void getAllInfo(final String ISBN,final Handler handler, final int flag) {  //run in LibraryFragment
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                DoubanBook book;
                String jsonObject="";
                String tmp=util.getJsonLruCache(ISBN);
                if (tmp!=null){
                    jsonObject=tmp;
                }else {
                    String douban_url = "https://api.douban.com/v2/book/isbn/:" + ISBN;
                    StringBuilder builder = new StringBuilder();
                    try {
                        URL url = new URL(douban_url);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setReadTimeout(8000);
                        connection.setConnectTimeout(8000);
                        int statusCode = connection.getResponseCode();
                        Log.e("statusCode", "" + statusCode);
                        if (statusCode == 200) {
                            String line;
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            while ((line = reader.readLine()) != null) {
                                builder.append(line);
                            }
                            jsonObject=builder.toString();
                            util.addJsonLruCache(ISBN,jsonObject);
                        } else {
                            Message message = new Message();
                            message.what = 501;
                            handler.sendMessage(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                book = parseBookInfo(jsonObject, ISBN);
//                dbManager = new DBManager(context);
                Log.e("isbn", ISBN + "---"+book.getAllPages());
//                dbManager.updateBookInfoByISBN(ISBN, "allpages", book.getAllPages());
//                ContentValues values = new ContentValues();
//                values.put("allpages", book.getAllPages());
//                DataSupport.updateAll(DoubanBook.class, values, "isbn_number = ?", ISBN);
                if (flag==1) {
                    Message message = new Message();
                    message.what = 500;
                    message.obj = book;
                    handler.sendMessage(message);
                }else {
                    Message message = new Message();
                    message.what = 1000;
                    message.obj = book;
                    handler.sendMessage(message);
                }
            }
        });
        thread.start();
    }

    public DoubanBook parseBookInfo(String jsonObject,String ISBN) {
        DoubanBook book = new DoubanBook();
        getDiskCache=new GetDiskCache();
        DiskLruCache.Snapshot snapShot = null;
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        try {
            mDiskLruCache=DiskLruCache
                    .open(getDiskCache.getDiskCacheDir(context,"bitmap"),
                            getDiskCache.getAppVersion(context), 1, 10 * 1024 * 1024);
            JSONObject object = new JSONObject(jsonObject);
            ContentValues values = new ContentValues();
            String title = object.getString("title");
            book.setBookname(title);
            String author = object.getString("author");
//            String author=DataSupport.where("isbn_number = ?",ISBN).find(DoubanBook.class).get(0).getAuthor();
            book.setAuthor(author);
            String pages = object.getString("pages");
            values.put("allpages",pages);
            book.setAllPages(Integer.parseInt(pages));
            String publisher = object.getString("publisher");
            values.put("publisher",publisher);
            book.setPublisher(publisher);
            String pubdate = object.getString("pubdate");
            values.put("pubdate",pubdate);
            book.setPubdate(pubdate);
            String isbn = object.getString("isbn13");
            book.setISBN_number(ISBN);
            List<String> tags = parseJSONArray2String(object.getJSONArray("tags"));
            book.setTags(tags);

            //豆瓣评分
            JSONObject rating = object.optJSONObject("rating");
            String numRaters = rating.getString("numRaters");
            values.put("numraters",numRaters);
            book.setNumRaters(numRaters);
            String average = rating.getString("average");
            values.put("average",average);
            book.setAverage(average);
            //图片
            JSONObject images = object.getJSONObject("images");
            String small = images.getString("small");
            String medium = images.getString("medium");
            String large = images.getString("large");
            Log.e("image", medium);
            Bitmap bitmap=util.getBitmapLruCache(medium);
            if (bitmap!=null){
                book.setCover(bitmap);
            }else {
                String key = getDiskCache.hashKeyForDisk(medium);
                snapShot = mDiskLruCache.get(key);
                if (snapShot == null) {
                    DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                    if (editor != null) {
                        OutputStream outputStream = editor.newOutputStream(0);
                        if (DownloadBitmap(medium, outputStream)) {
                            editor.commit();
                        } else {
                            editor.abort();
                        }
                        mDiskLruCache.flush();
                    }
                    snapShot = mDiskLruCache.get(key);
                }
           /* if (snapShot!=null){
                InputStream is = snapShot.getInputStream(0);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
            }*/

                if (snapShot != null) {
                    fileInputStream = (FileInputStream) snapShot.getInputStream(0);
                    fileDescriptor = fileInputStream.getFD();
                }
                // 将缓存数据解析成Bitmap对象
                bitmap = null;
                if (fileDescriptor != null) {
                    bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    util.addBitmapLruCache(medium,bitmap);
                }
                if (bitmap != null) {
                    // 将Bitmap对象添加到内存缓存当中
                    book.setCover(bitmap);
                }
            }
//            book.setCover(DownloadBitmap(large));
            String summary = object.getString("summary");
            values.put("summary",summary);
            book.setSummary(summary);
            String price = object.getString("price");
            values.put("price",price);
            book.setPrice(price);
            DataSupport.updateAll(DoubanBook.class, values, "isbn_number = ?", ISBN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return book;
    }

    private List<String> parseJSONArray2String(JSONArray array) {
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);
                tags.add(object.getString("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tags;
    }

    private boolean DownloadBitmap(String bmurl,OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(bmurl);
            Log.e("time--->>>>>>>>>>","");
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
package com.liuqiang.xatulibrary.networking;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.liuqiang.xatulibrary.common.Book;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuqiang on 15-8-29.
 */
public class SearchThread implements Runnable {

    private String type;
    private String keyword;
    private List<Book> list=new ArrayList<>();
    private String number;
    private Handler handler;
    private int count;
    private String stringCount;
    public static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko";

    public SearchThread(String type, String keyword,Handler handler){
        this.type=type;
        this.keyword=keyword;
        this.handler=handler;
    }

    public SearchThread(String type, String keyword,Handler handler,int count){
        this.type=type;
        this.keyword=keyword;
        this.handler=handler;
        this.count=count;
    }
    @Override
    public void run() {
        stringCount = Integer.toString(count);
        String url;
        keyword=keyword.replaceAll("\\s{1,}","+");
        Log.e("type---------", type);
        switch (type) {
            case "isbn":
                url = "http://222.25.12.227:8080/opac/openlink.php?isbn=" + keyword + "&_m=1";
                break;
            default:
                    url = "http://222.25.12.227:8080/opac/openlink.php?dept=ALL&" +
                            type + "=" + keyword + "&doctype=ALL&lang_code=ALL&match_flag=forward&displaypg=20" +
                            "&showmode=list&orderby=DESC&sort=CATA_DATE&onlylendable=no&count=" + number + "&with_ebook" +
                            "=&page=" + stringCount;
                break;
        }

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            Log.e("url", url);
            post.setHeader("User-Agent", USER_AGENT);
            post.setHeader("content-encoding", "gzip");
            try {
                HttpResponse response = httpClient.execute(post);
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    String s = EntityUtils.toString(entity, "utf-8");
                    Document document = Jsoup.parse(s);
                    try {
                        if (document.select("div#container").select("font").text().equals("0")) {
                            Message message=new Message();
                            message.what=10;
                            handler.sendMessage(message);
                            Log.e("have no ", "--0--");
                        } else {
                            number = document.select("div.book_article").get(0).select("strong").text();
                            Message message1 = new Message();
                            message1.what = 2;
                            message1.obj = number;
                            handler.sendMessage(message1);
                            Elements books = document.select("div.book_article").get(3).
                                    select("li.book_list_info");
                            for (org.jsoup.nodes.Element e : books) {
                                Book bookInfo = new Book();
                                url = "http://222.25.12.227:8080/opac/" + e.select("a").attr("href");
                                bookInfo.setUrl(url);
                                String name = e.select("a").text().replaceAll("中文图书", "").replaceAll(" 馆藏", "");
                                bookInfo.setBookname(name);
                                String author = e.childNode(3).childNode(2).toString();
                                bookInfo.setAuthor(author);
                                String[] items = e.text().replace("中文图书", "").replace(name, "").replace(author, " ").split("\\s{1,}");
                                bookInfo.setBookNumber(items[1]);
                                bookInfo.setNumber_all(items[2]);
                                bookInfo.setNumber_free(items[3]);
                                list.add(bookInfo);
                            }
                            Message message = new Message();
                            message.what = 1;
                            message.obj = list;
                            handler.sendMessage(message);
                        }
                    } catch (Exception e) {

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
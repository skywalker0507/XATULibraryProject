package com.liuqiang.xatulibrary.networking;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.liuqiang.xatulibrary.common.Book;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by liuqiang on 15-9-2.
 */
public class AnySearchThread implements Runnable {

    private String keyword;
    private Handler handler;
    private int count;
    private String stringCount;

    public AnySearchThread(String keyword, Handler handler, int count) {
        this.keyword = keyword;
        this.handler = handler;
        this.count = count;
    }

    @Override
    public void run() {
        stringCount = Integer.toString(count);
        keyword = keyword.replaceAll("\\s{1,}", "+");
        String url = "http://222.25.12.227:8080/opac/search_adv_result.php?sType0=any&q0="
                + keyword + "&with_ebook=&page=" + stringCount;
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        Log.e("first url", url);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String s = EntityUtils.toString(entity, "utf-8");
                Document document = Jsoup.parse(s);
                String header = document.select("div.box_bgcolor").text();
                Log.e("header", header);
                Elements elements = document.select("div.box_bgcolor").select("font");
                Log.e("keyword and count", elements.get(0).text() + "--" + elements.get(2).text());
                String number = elements.get(2).text();
                Message message = new Message();
                message.what = 2;
                message.obj = number;
                handler.sendMessage(message);
                List<Book> list = new ArrayList<>();
                Element element = document.select("table").get(0);
                Elements titleName = element.select("tr");
                for (int i = 1; i < titleName.size(); i++) {
                    Book bookInfo = new Book();
                    Elements e = titleName.get(i).select("td");
                    String bookname = e.get(0).text() + ":" +
                            e.get(1).text();
                    String author = e.get(2).text();
                    Log.e("url", e.get(1).attr("href"));
                    String u = "http://222.25.12.227:8080/opac/" + e.get(1).select("a").attr("href");
                    String bookNumber = e.get(4).text();
                    bookInfo.setBookname(bookname);
                    bookInfo.setAuthor(author);
                    bookInfo.setBookNumber(bookNumber);
                    bookInfo.setUrl(u);
                    list.add(bookInfo);
                }
                Message message1 = new Message();
                message1.what = 1;
                message1.obj = list;
                handler.sendMessage(message1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

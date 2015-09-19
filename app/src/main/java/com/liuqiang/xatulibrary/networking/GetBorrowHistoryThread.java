package com.liuqiang.xatulibrary.networking;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.liuqiang.xatulibrary.common.DoubanBook;
import com.liuqiang.xatulibrary.common.UserData;

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
 * Created by liuqiang on 15-9-8.
 */
public class GetBorrowHistoryThread implements Runnable {

    private List<DoubanBook> list = new ArrayList<>();
    private Handler handler;
    private static String histroy_url = "http://222.25.12.227:8080/reader/book_hist.php";
    private static String Referer = "http://222.25.12.227:8080/reader/redr_info.php";

    public GetBorrowHistoryThread(List<DoubanBook> list, Handler handler) {
        this.handler = handler;
        this.list = list;
    }

    @Override
    public void run() {
        String cookie = UserData.getUserCookie();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(histroy_url);
        httpGet.setHeader("Cookie", "PHPSESSID=" + cookie);
        httpGet.setHeader("Referer", Referer);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String s = EntityUtils.toString(entity, "utf-8");
//                Log.e("get library",s);
                Document document = Jsoup.parse(s);
                Elements elements = document.select("table").select("tr");
                for (int i = 1; i < elements.size(); i++) {
                    Element element = elements.get(i);
                    DoubanBook book = new DoubanBook();
                    String name = element.select("td").get(2).text();
                    String author = element.select("td").get(3).text();
                    book.setBookname(name);
                    book.setAuthor(author);
                    list.add(book);
                    Log.e("name", name);
                    Log.e("author", author);
                }

                Message message = new Message();
                message.what = 1;
                message.obj = list;
                handler.sendMessage(message);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

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
 * Created by liuqiang on 15-9-7.
 */
public class GetBookListThread implements Runnable {

    private Handler handler;
    private String cookie;
    public static final String BOOK_LIST_URL = "http://222.25.12.227:8080/reader/book_lst.php";
    public static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko";


    public GetBookListThread(String cookie,Handler handler){
        this.handler=handler;
        this.cookie=cookie;
    }

    public GetBookListThread(String cookie){
        this.cookie=cookie;
    }
    @Override
    public void run() {

        try {
            HttpGet httpGet = new HttpGet(BOOK_LIST_URL);
            HttpClient httpClient = new DefaultHttpClient();
            httpGet.addHeader("Cookie", "PHPSESSID=" + cookie);
            HttpResponse httpResponse=httpClient.execute(httpGet);
            if(httpResponse.getStatusLine().getStatusCode()==200) {
                Log.d("thread-------->>>>", "success");
                try {
                    HttpEntity entity1 = httpResponse.getEntity();
                    String s = EntityUtils.toString(entity1, "utf-8");
//                    Log.e("all",s);
                    Document doc = Jsoup.parse(s);
                    List<Book> list = new ArrayList<>();
                    Element element = doc.select("table").get(0);
                    Elements titleName = element.select("tr");
                    Elements checks = doc.getElementsByAttribute("onclick");
                    Log.e("size", "" + titleName.size());
                    for (int i = 1, j = 0; i < titleName.size(); i++, j++) {
                        if (checks.size()==0){
                            Message message=new Message();
                            message.what=1;
                            handler.sendMessage(message);
                        }else {
                            String[] checkitems = checks.get(j).toString().split(",");
                            String check = checkitems[1].replaceAll("'", "");
                            Log.e("check", check);
                            Element e = titleName.get(i);
                            String item = e.select("td").get(1).text();
                            String[] strs = item.split("/");
                            String name = strs[0];
                            String author = strs[1];
                            String u = e.select("td").get(1).select("a").attr("href");
                            String url = u.replace("..", "http://222.25.12.227:8080/");
                            String begin = e.select("td").get(2).text();
                            String end = e.select("td").get(3).select("font").text();
                            String ISBN = e.select("td").get(0).text();
                            String time = e.select("td").get(4).text();
                            if (!name.isEmpty()) {
                                Book bookitem = new Book();
                                bookitem.setBookname(name);
                                bookitem.setBeginTime(begin);
                                bookitem.setEndTime(end);
                                bookitem.setMaskNumber(ISBN);
                                bookitem.setAuthor(author);
                                bookitem.setUrl(url);
                                bookitem.setTimes(time);
                                bookitem.setCheck(check);
                                list.add(bookitem);
                            }
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = list;
                            handler.sendMessage(msg);
                            Log.e("success", "succsess" + list.size());
                        }
                    }
                   /* Message msg = new Message();
                    msg.what = 0;
                    msg.obj = list;
                    handler.sendMessage(msg);
                    Log.e("success","succsess");*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    }


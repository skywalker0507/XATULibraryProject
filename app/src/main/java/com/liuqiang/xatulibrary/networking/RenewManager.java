package com.liuqiang.xatulibrary.networking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.liuqiang.xatulibrary.common.UserData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by liuqiang on 15-9-7.
 */
public class RenewManager {
    public static String COOKIE = "";
    public static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko";
    public static final String Referer_url = "http://222.25.12.227:8080/reader/book_lst.php";
    public static final String image_url = "http://222.25.12.227:8080/reader/captcha.php";

    public RenewManager() {
        COOKIE = UserData.getUserCookie();
    }

    public void getVerifyImg(final Handler handler) {
        Thread httpThread = new Thread(new Runnable() {

            @Override
            public void run() {
                HttpGet httpGet = new HttpGet(image_url);
                Log.e("image url", image_url);
                httpGet.setHeader("Cookie", "PHPSESSID=" + COOKIE);
                httpGet.setHeader("User-Agent", USER_AGENT);
                httpGet.setHeader("Referer", Referer_url);
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = null;
                try {
                    response = httpClient.execute(httpGet);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Log.d("TAG", "response code: "
                        + response.getStatusLine().getStatusCode());
                try {
                    byte[] buffer = EntityUtils.toByteArray(response
                            .getEntity());
                    Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0,
                            buffer.length);
                    Message message = new Message();
                    message.obj = bitmap;
                    message.what = 1;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        });

        httpThread.start();
    }

    public void doRenew(final String bar_code, final String check, final String code,
                        final String time, final Handler handler) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://222.25.12.227:8080/reader/ajax_renew.php?bar_code=" + bar_code + "" +
                        "&check=" + check + "&captcha=" + code + "&time=" + time;
                HttpGet httpGet = new HttpGet(url);
                Log.e("Renew_url", url);
                httpGet.setHeader("Cookie", "PHPSESSID=" + COOKIE);
                httpGet.setHeader("User-Agent", USER_AGENT);
                httpGet.setHeader("Referer", Referer_url);
                HttpClient httpClient = new DefaultHttpClient();
                try {
                    HttpResponse response = httpClient.execute(httpGet);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity1 = response.getEntity();
                        String s = EntityUtils.toString(entity1, "utf-8");
                        Log.e("llll", "" + s.length());
                        //错误的验证码(wrong check code).length==30
//                        Log.e("hhhhh",s);
                        if (s.length() == 30) {
                            Message message = new Message();
                            message.what = 10;
                            handler.sendMessage(message);
                        } else if (s.length() == 43) {
                            Message message = new Message();
                            message.what = 11;
                            handler.sendMessage(message);
                        } else if (s.length() == 37) { //超期！不得续借！
                            Message message = new Message();
                            message.what = 12;
                            handler.sendMessage(message);
                        } else {
                            Message message = new Message();
                            message.what = 2;
                            handler.sendMessage(message);
                            GetBookListThread thread1 = new GetBookListThread(UserData.getUserCookie());
                            new Thread(thread1).start();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


}

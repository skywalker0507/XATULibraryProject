package com.liuqiang.xatulibrary.networking;

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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuqiang on 15-9-5.
 */
public class GetUserInfoThread implements Runnable {

    private static String user_page="http://222.25.12.227:8080/reader/redr_info.php";
    private Handler handler;
    private Map<String,String> userInfo=new HashMap<String,String>();
    private Map<String,String> borrowInfo=new HashMap<String,String>();

    public GetUserInfoThread(Handler handler){
        this.handler=handler;
    }

    @Override
    public void run() {
        HttpClient httpClient=new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(user_page);

        httpGet.setHeader("Cookie", "PHPSESSID=" + UserData.getUserCookie());
        httpGet.setHeader("Referer","http://222.25.12.227:8080/reader/login.php");
        try {
            HttpResponse response=httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode()==200){
                HttpEntity entity = response.getEntity();
                String s = EntityUtils.toString(entity, "utf-8");
//                Log.e("GetUserInfo",s);
                Document document= Jsoup.parse(s);
                Elements borrow_Info=document.select("div.mylib_msg");
                Log.e("borrowInfo",borrow_Info.text());
                Element user_Info=document.select("table").get(0);
                Log.e("userInfo",user_Info.text());
                Elements table=user_Info.select("tr");
                String studentID=table.get(0).select("td").get(2).text();
                String studentName=table.get(0).select("td").get(1).text();
                String stydentType=table.get(3).select("td").get(0).text();
                String colloge=table.get(6).select("td").get(0).text();

                String allBook=borrow_Info.select("a").get(0).text();
                String overBook=borrow_Info.select("a").get(1).text();
                String begin=table.get(1).select("td").get(1).text();
                String end=table.get(1).select("td").get(0).text();
                String maxBookNum=table.get(2).select("td").get(0).text();
                String allBorrowedBooks=table.get(3).select("td").get(2).text();
                StringBuilder builder=new StringBuilder();

                userInfo.put("studentID",studentID);
                userInfo.put("studentName",studentName);
                userInfo.put("stydentType",stydentType);
                userInfo.put("colloge",colloge);
                Message userMessage=new Message();
                userMessage.what=1;
                userMessage.obj=userInfo;
                handler.sendMessage(userMessage);

                borrowInfo.put("allBook", allBook);
                borrowInfo.put("overBook",overBook);
                borrowInfo.put("begin",begin);
                borrowInfo.put("end",end);
                borrowInfo.put("maxBookNum", maxBookNum);
                borrowInfo.put("allBorrowedBooks", allBorrowedBooks);
                Message borrowMessage=new Message();
                borrowMessage.what=2;
                borrowMessage.obj=borrowInfo;
                handler.sendMessage(borrowMessage);
                builder.append(studentName+",");
                builder.append(studentID+",");
                builder.append(allBook+",");
                builder.append(overBook+",");
                builder.append(begin+",");
                builder.append(end+",");
                builder.append(maxBookNum+",");
                builder.append(allBorrowedBooks+",");
                builder.append(stydentType+",");
                Log.e("all", builder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

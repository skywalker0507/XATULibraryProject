package com.liuqiang.xatulibrary.networking;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.liuqiang.xatulibrary.common.UserData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class LoginManager {


    public static String COOKIE = "";
    public static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko";

    //    图书馆主页
    public static final String HOST = "http://library.xatu.cn/";
    //    登陆页面
    public static final String LOGINPAGE_URL = "http://222.25.12.227:8080/reader/login.php";
    //	获取验证码
    public static final String CODE_URI = "http://222.25.12.227:8080/reader/captcha.php";
    //    认证页面
    public static final String VERIFY_URL = "http://222.25.12.227:8080/reader/redr_verify.php";
    //    借阅列表
    public static final String BOOK_LIST_URL = "http://222.25.12.227:8080/reader/book_lst.php";
    //    读者挂失
    public static final String REDR_LOST_URL = "http://222.25.12.227:8080/reader/redr_lost.php";
    private Context context;


    public LoginManager(Context context) {
        this.context=context;
    }

    /**
     * 进行第一次网页请求，希望获取对应的cookies值
     */
    public void getWebCookies(final Handler handler) {

        Thread httpThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    HttpGet httpGet = new HttpGet(LOGINPAGE_URL);
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(httpGet);
                    if (!httpGet.isAborted()) {
                        String cookie = ((AbstractHttpClient) httpClient)
                                .getCookieStore().getCookies().get(0).getValue();
                        Log.d("TAG", "response code: "
                                + response.getStatusLine().getStatusCode());
                        Log.e("TAG", "cookie: " + cookie);
                        COOKIE = cookie;
                        UserData.setUserCookie(cookie);
                        handler.sendEmptyMessage(1);
                    }else {
                        Log.e("error","error");
                        Toast.makeText(context,"hjh",Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        });
        httpThread.start();
    }

    /**
     * 根据第一次请求获得的cookie， 请求相应的验证码图片
     */
    public void getVerifyImg(final Handler handler) {
        Thread httpThread = new Thread(new Runnable() {

            @Override
            public void run() {
                HttpGet httpGet = new HttpGet(CODE_URI);
                httpGet.setHeader("Cookie", "PHPSESSID=" + COOKIE);
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = null;
                try {
                    if (!httpGet.isAborted()) {
                        response = httpClient.execute(httpGet);
                        try {
                            byte[] buffer = EntityUtils.toByteArray(response
                                    .getEntity());
                            Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0,
                                    buffer.length);
                            Message message = new Message();
                            message.obj = bitmap;
                            message.what = 2;
                            handler.sendMessage(message);
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
               /* Log.d("TAG", "response code: "
                        + response.getStatusLine().getStatusCode());*/

            }
        });

        httpThread.start();
    }

    /**
     * 进行登陆操作
     *
     * @param account
     * @param pwd
     * @param code
     */
    public void doPost(final String account, final String pwd,
                       final String code, final Handler handler) {
        final Thread httpThread = new Thread(new Runnable() {

            @Override
            public void run() {
                int what = 14;
                if (account.isEmpty()) {
                    what = 10;
                } else if (pwd.isEmpty()) {
                    what = 11;
                } else if (code.isEmpty()) {
                    what = 12;
                } else {

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(VERIFY_URL);
                    HttpContext context = new BasicHttpContext();
                    httpPost.setHeader
                            ("User-Agent", USER_AGENT);
                    Log.d("1_____>>>", "1");
                    httpPost.addHeader("Cookie", "PHPSESSID=" + COOKIE);
                    Log.d("COOKICE------>>>>>", COOKIE);
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    Log.d("code----->>>>>", code);
                    params.add(new BasicNameValuePair("captcha", code));
                    params.add(new BasicNameValuePair("number", account));
                    params.add(new BasicNameValuePair("passwd", pwd));
                    params.add(new BasicNameValuePair("returnUrl", null));
                    params.add(new BasicNameValuePair("select", "cert_no"));
                    System.out.println("number=" + account + "---------" + "password=" + pwd);
                    try {
                        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
                        httpPost.setEntity(entity);
                        Log.d("2________>>>", "2");
                        try {
                            HttpResponse httpResponse = httpClient.execute(httpPost, context);
                            Log.d("3------>>", String.valueOf(httpResponse.getStatusLine().getStatusCode()));

                            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                                HttpUriRequest curReq = (HttpUriRequest) context
                                        .getAttribute(ExecutionContext.HTTP_REQUEST);
                                HttpHost curHost = (HttpHost) context
                                        .getAttribute(ExecutionContext.HTTP_TARGET_HOST);
                                String getUrl = curReq.getURI().toString();
                                Log.e("curReq:url:", getUrl + getUrl.length());
                                String curUrl = (curReq.getURI().isAbsolute()) ? curReq.getURI()
                                        .toString() : (curHost.toURI() + curReq.getURI());
                                Log.e("url", curUrl + curUrl.length());
                                if (getUrl.length() == 20) {
//									 getUrl==/reader/redr_con.php
                                    Message message = new Message();
                                    message.obj = COOKIE;
                                    message.what = 100;
                                    handler.sendMessage(message);
                                } else {

                                    HttpEntity httpEntity = httpResponse.getEntity();
                                    String s = EntityUtils.toString(httpEntity, "utf-8");
                                    Document doc = Jsoup.parse(s);
                                    String element = doc.select("table").get(0).
                                            select("tr").get(6).select("td").get(1).text();
                                    int i = element.length();
                                    System.out.println(element + "" + i);
                                    if (i == 23) {
                                        what = 15; //验证码错误(wrong check code)

                                    } else if (i == 13) {
                                        what = 16; //对不起，密码错误，请查实！

                                    } else {
                                        what = 20;
//									 getBookList(handler, 3);


                                    }
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }
                Message msg = new Message();
                msg.what = what;
                Log.e("what=====", String.valueOf(what));
                if (what == 20) {
                    msg.obj = "LOGIN_OK";

                } else {
                    msg.obj = "ERORR";
                }

                handler.sendMessage(msg);
            }
        });
        httpThread.start();
    }

	/*public  void getBookList(final Handler handler, final int message) {
        Thread httpThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpGet httpGet = new HttpGet(BOOK_LIST_URL);
					HttpClient httpClient = new DefaultHttpClient();
					httpGet.addHeader("Cookie", "PHPSESSID=" + COOKIE);
						HttpResponse httpResponse=httpClient.execute(httpGet);
						if(httpResponse.getStatusLine().getStatusCode()==200) {
							Log.d("thread-------->>>>", "success");
							try {
								HttpEntity entity1 = httpResponse.getEntity();
								String s = EntityUtils.toString(entity1, "utf-8");
								Document doc = Jsoup.parse(s);
								*//*String js=doc.getElementsByAttribute("onclick").toString();
                                String js0=doc.getElementsByAttribute("onclick").get(1).toString();
								String[] a=js0.split(",");
								Log.e("haha",a[1].replaceAll("'",""));*//*
                                List<Book> list = new ArrayList<>();
								Element element = doc.select("table").get(0);
								Elements titleName = element.select("tr");
								Elements checks=doc.getElementsByAttribute("onclick");
								Log.e("size",""+titleName.size());
								for (int i=1,j=0;i<titleName.size();i++,j++) {
									String[] checkitems=checks.get(j).toString().split(",");
									String check=checkitems[1].replaceAll("'","");
									Log.e("check",check);
									Element e=titleName.get(i);
									String item = e.select("td").get(1).text();
									String[] strs = item.split("/");
									String name = strs[0];
									String author = strs[1];
									String u = e.select("td").get(1).select("a").attr("href");
									String url=u.replace("..", "http://222.25.12.227:8080/");
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
								}
								Message msg = new Message();
								msg.what = message;
								msg.obj = list;
								handler.sendMessage(msg);

							}catch (Exception e){
								e.printStackTrace();
							}
						}


					} catch (IOException e) {
						e.printStackTrace();
					}

			}
		});
		httpThread.start();

	}*/


}

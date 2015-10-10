package com.liuqiang.xatulibrary.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.liuqiang.xatulibrary.R;
import com.liuqiang.xatulibrary.common.Book;
import com.liuqiang.xatulibrary.common.NetworkState;
import com.liuqiang.xatulibrary.common.UserData;
import com.liuqiang.xatulibrary.networking.LoginManager;
import com.liuqiang.xatulibrary.service.NetStateService;
import com.liuqiang.xatulibrary.util.AESEncryptor;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liuqiang on 15-7-20.
 */
public class UserLoginActivity extends AppCompatActivity {
    private EditText studentID;
    private EditText password;
    private EditText code;
    private ImageView verifyIv;
    private CheckBox showpassword;
    private CheckBox isremembered;
    private Button login, register;
    private MyHandler msgHandler;
    private LoginManager manager;
    private List<Book> list = new ArrayList<>();
    private String s;
    private final String LOGIN_OK = "LOGIN_OK";
    private SwipeRefreshLayout mSwipeLayout;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userload);
        startService(new Intent(this, NetStateService.class));
        if (!NetworkState.isNetworkAvailable(UserLoginActivity.this)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(UserLoginActivity.this)
                    .setTitle("提醒")
                    .setMessage("你没有联网哦,请检查网络后再试")
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            dialog.show();
        }
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.refresh1);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeLayout.setRefreshing(false);
                        manager.getVerifyImg(msgHandler);
                    }
                }, 3000);
            }


        });
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        init();
    }

    private void init() {
        studentID = (EditText) findViewById(R.id.et_studentID);
        register = (Button) findViewById(R.id.bt_Register);
        password = (EditText) findViewById(R.id.et_password);
        isremembered = (CheckBox) findViewById(R.id.isremember);
        showpassword = (CheckBox) findViewById(R.id.showpassword);
        showpassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (showpassword.isChecked()) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        code = (EditText) findViewById(R.id.verifycode);
        verifyIv = (ImageView) findViewById(R.id.verifyImage);
        login = (Button) findViewById(R.id.login);
        msgHandler = new MyHandler();
        manager = new LoginManager(UserLoginActivity.this);
        manager.getWebCookies(msgHandler);
        isRemember();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(UserLoginActivity.this);
                dialog.setTitle("正在登陆");
                dialog.setMessage("正在登陆,请稍后。。。");
                String myaccount = studentID.getText().toString();
                String mypassword = password.getText().toString();
                String mycode = code.getText().toString();
                if (!myaccount.isEmpty() & !mypassword.isEmpty() & !mycode.isEmpty()) {
                    dialog.show();
                }
                manager.doPost(myaccount, mypassword, mycode,
                        msgHandler);
                System.out.println("s-------" + s);

            }

        });

        verifyIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manager.getVerifyImg(msgHandler);

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserLoginActivity.this, RegisterActivity.class));
            }
        });

    }


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    manager.getVerifyImg(msgHandler);
                    break;
                case 2:
                    verifyIv.setImageBitmap((Bitmap) msg.obj);
                    break;
                case 10:
                    dialog.dismiss();
                    Toast.makeText(UserLoginActivity.this, "请输入您的学号", Toast.LENGTH_SHORT).show();
                    break;
                case 11:
                    dialog.dismiss();
                    Toast.makeText(UserLoginActivity.this, "请输入您的密码", Toast.LENGTH_SHORT).show();
                    break;
                case 12:
                    dialog.dismiss();
                    Toast.makeText(UserLoginActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    break;
                case 13:
                    dialog.dismiss();
                    Toast.makeText(UserLoginActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    manager.getVerifyImg(msgHandler);
                    break;

                case 15:
                    dialog.dismiss();
                    Toast.makeText(UserLoginActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    manager.getVerifyImg(msgHandler);
                    break;
                case 16:
                    dialog.dismiss();
                    Toast.makeText(UserLoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                    manager.getVerifyImg(msgHandler);
                    break;


                case 20:
                    s = (String) msg.obj;
                    Log.e("s-----------", s);
                    dialog.dismiss();
                    if (s == LOGIN_OK) {
                        startActivity(new Intent(UserLoginActivity.this, MainActivity.class));
                        getData();
                        UserData.setLoginSucess(true);
                        Toast.makeText(UserLoginActivity.this, "success", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UserLoginActivity.this, "fale", Toast.LENGTH_SHORT).show();

                    }
                    break;

                case 100:
                    String cookie = (String) msg.obj;
                    Toast.makeText(UserLoginActivity.this, "你还没有验证，请先验证", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserLoginActivity.this, RegisterActivity.class);
                    intent.putExtra("cookie", cookie);
                    startActivity(new Intent(UserLoginActivity.this, RegisterActivity.class));
                    break;

                default:
                    break;
            }

        }
    }

    public void isRemember() {
        Boolean isremember = UserData.isRememberPassword();
        if (isremember) {
            String myacount = UserData.getUserName();
            String mypassword = UserData.getPassWord();
            Log.e("Account",myacount+"--"+mypassword);
            try {
                //AES解密
                studentID.setText(AESEncryptor.decrypt("qwerty",myacount)); //"qwerty为脏数据

                password.setText(AESEncryptor.decrypt("poiuyt", mypassword).toString());
                Log.e("Acount",AESEncryptor.decrypt("qwerty",myacount)+"=="
                        +AESEncryptor.decrypt("poiuyt", mypassword).toString());
            }catch (Exception e){
                e.printStackTrace();
            }

            isremembered.setChecked(true);
        }

    }

    public void getData() {
//        editor=pref.edit();
        if (isremembered.isChecked()) {
//            editor.putBoolean("autoload",true);
//            editor.putBoolean("remember_password",true);
//            editor.putString("account", studentID.getText().toString());
//            editor.putString("password",password.getText().toString());

            UserData.setRememberPassword(true);
            try {
                //AES加密
                UserData.setUserName(AESEncryptor.encrypt("qwerty", studentID.getText().toString()));
                UserData.setPassWord(AESEncryptor.encrypt("poiuyt", password.getText().toString()));
            }catch (Exception e){
               e.printStackTrace();
            }
        } else {
            UserData.clearData();
        }

    }


}

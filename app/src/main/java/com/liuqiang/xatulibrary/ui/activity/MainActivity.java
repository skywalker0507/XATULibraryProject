package com.liuqiang.xatulibrary.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.liuqiang.xatulibrary.service.NetStateService;
import com.liuqiang.xatulibrary.R;
import com.liuqiang.xatulibrary.common.UserData;
import com.liuqiang.xatulibrary.ui.fragment.LibraryFragment;
import com.liuqiang.xatulibrary.ui.fragment.MainFragment;
import com.liuqiang.xatulibrary.ui.fragment.RecommendFragment;
import com.liuqiang.xatulibrary.ui.fragment.Redr_lostFragment;
import com.liuqiang.xatulibrary.ui.fragment.TipsFragment;
import com.liuqiang.xatulibrary.util.AESEncryptor;

import br.liveo.Model.HelpLiveo;
import br.liveo.interfaces.OnItemClickListener;
import br.liveo.interfaces.OnPrepareOptionsMenuLiveo;
import br.liveo.navigationliveo.NavigationLiveo;


public class MainActivity extends NavigationLiveo implements OnItemClickListener {

    private HelpLiveo mHelpLiveo;
    private final static int LOGIN = 1;
    private long exitTime = 0;
//    SharedPreferences pref;
//    List<Book> books;

    @Override
    public void onInt(Bundle bundle) {
//        MyApplication myApplication=(MyApplication)getApplicationContext();
//        myApplication.clearList();
//        books=(ArrayList<Book>)getIntent().getSerializableExtra("booklist");
        // User Information
        this.userName.setText(AESEncryptor.decrypt("qwerty", UserData.getUserName()));
        this.userPhoto.setImageResource(R.drawable.ic_no_user);
        this.userBackground.setImageResource(R.drawable.ic_user_background_first);

        // Creating items navigation
        mHelpLiveo = new HelpLiveo();
        mHelpLiveo.add("首页", R.drawable.ic_inbox_black_24dp);
        mHelpLiveo.addSubHeader("读者服务"); //Item subHeader
        mHelpLiveo.add("我的图书馆", R.drawable.ic_drafts_black_24dp);
        mHelpLiveo.add("读者须知", R.drawable.ic_settings_black_24dp);
        mHelpLiveo.add("读者挂失", R.drawable.ic_star_black_24dp);
        mHelpLiveo.add("读者荐购", R.drawable.ic_send_black_24dp);
        mHelpLiveo.addSeparator();
       /* mHelpLiveo.addSeparator(); // Item separator
        mHelpLiveo.add(getString(R.string.trash), R.mipmap.ic_delete_black_24dp);
        mHelpLiveo.add(getString(R.string.spam), R.mipmap.ic_report_black_24dp, 120);*/

        with(this).startingPosition(0) //Starting position in the list
                .addAllHelpItem(mHelpLiveo.getHelp())
                .colorNameSubHeader(R.color.nliveo_blue_colorPrimary)
                .colorItemSelected(R.color.nliveo_blue_colorPrimary)
                .footerItem("设置", R.drawable.ic_settings_black_24dp)
                .setOnClickUser(onClickPhoto)
                .setOnPrepareOptionsMenu(onPrepare)
                .setOnClickFooter(onClickFooter)
                .build();

        int position = this.getCurrentPosition();
        this.setElevationToolBar(position != 0 ? 15 : 0);
//        pref= getApplicationContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }

    @Override
    public void onItemClick(int position) {
        Fragment mFragment;
        FragmentManager mFragmentManager = getSupportFragmentManager();
//        if (pref.getBoolean("autoload", false)) {
        if (UserData.isLoginSucess()) {
            switch (position) {
                case 0:
                    mFragment = new MainFragment();
                    break;
                case 2:
                    mFragment = new LibraryFragment();
                    break;
                case 3:
                    mFragment = new TipsFragment();
                    break;

                case 4:
                    mFragment = new Redr_lostFragment();
                    break;
                case 5:
                    mFragment = new RecommendFragment();
                    break;
                default:
                    mFragment = new MainFragment();
                    break;
            }

            if (mFragment != null) {
                mFragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
            }

            setElevationToolBar(position != 0 ? 15 : 0);

        }
    }

    private OnPrepareOptionsMenuLiveo onPrepare = new OnPrepareOptionsMenuLiveo() {
        @Override
        public void onPrepareOptionsMenu(Menu menu, int position, boolean visible) {
        }
    };

    private View.OnClickListener onClickPhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
            startActivity(intent);

            closeDrawer();
        }
    };

    private View.OnClickListener onClickFooter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            closeDrawer();
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
//                MyApplication myApplication=(MyApplication)getApplicationContext();
//                myApplication.clearList();
                stopService(new Intent(this, NetStateService.class));
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

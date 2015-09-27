package com.liuqiang.xatulibrary.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liuqiang on 15-9-19.
 */
public abstract class BaseFragment extends Fragment{

    private View mLayoutView;
    private boolean mIsInitDate = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mLayoutView==null){
            mLayoutView = inflater.inflate(onSetLayoutId(),container, false);
            findView(mLayoutView);
            initView();
        }
        return mLayoutView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!mIsInitDate){
            mIsInitDate = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    /**
     * 设置布局文件
     * @return 返回布局文件资源Id
     */
    public abstract int onSetLayoutId();

    /**
     * 获取布局控件viw.findViewById(R.id.xx);
     * @param view
     */
    public abstract void findView(View view);

    /**
     * 初始化View相关参数，例如实例化对象、设置View参数等等
     */
    public abstract void initView();

}

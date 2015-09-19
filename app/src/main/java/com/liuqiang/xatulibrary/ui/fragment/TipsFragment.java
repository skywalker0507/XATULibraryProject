package com.liuqiang.xatulibrary.ui.fragment;

import android.view.View;

import com.liuqiang.xatulibrary.R;
import com.liuqiang.xatulibrary.base.BaseFragment;

/**
 * Created by liuqiang on 15-7-20.
 */
public class TipsFragment extends BaseFragment {

    public static TipsFragment newInstance() {
        TipsFragment mFragment = new TipsFragment();
        return mFragment;
    }

/*    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_main, container, false);

        return rootView;
    }*/

    @Override
    public int onSetLayoutId() {
        return R.layout.page_main;
    }

    @Override
    public void findView(View view) {

    }

    @Override
    public void initView() {

    }

}

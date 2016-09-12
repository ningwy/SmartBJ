package io.github.ningwy.smartbj.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.ningwy.smartbj.ui.activity.MainActivity;

/**
 * base fragment
 * Created by ningwy on 2016/8/3.
 */
public abstract class BaseFragment extends Fragment {

    protected MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = initView(); //View
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initEvent();
        initData();
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 继承BaseFragment要求必须实现该方法初始化视图布局
     * @return 要初始化的视图布局
     */
    protected abstract View initView();

    /**
     * 如果需要初始化数据，则选择继承该方法
     */
    protected void initData() {

    }

    /**
     * 如果需要初始化事件，则选择继承该方法
     */
    protected void initEvent() {

    }
}

package io.github.ningwy.smartbj.newstagpage;

import android.view.View;

import io.github.ningwy.smartbj.ui.activity.MainActivity;

public abstract class BaseNewsPage {

    protected MainActivity mainActivity;

    //根布局
    protected View root;

    public BaseNewsPage(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        root = initView();
        initEvent();
    }

    /**
     * 初始化事件
     */
    protected void initEvent() {
    }

    protected abstract View initView();

    //得到根布局
    public View getRoot() {
        initData();//得到根布局之前加载数据
        return root;
    }

    /**
     * 初始化数据
     */
    protected void initData() {
    }
}

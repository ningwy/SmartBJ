package io.github.ningwy.smartbj.basepage;

import android.view.View;

import io.github.ningwy.smartbj.ui.activity.MainActivity;

/**
 * 首页
 * Created by ningwy on 2016/8/3.
 */
public class HomeTagPage extends BaseTagPage {

    public HomeTagPage(MainActivity context) {
        super(context);
    }

    @Override
    public void initData() {

        //将左侧菜单按钮隐藏
        ib_main_menu.setVisibility(View.GONE);

        tv_title.setText("首页");
        tv_content.setText("这是首页的内容");
    }
}

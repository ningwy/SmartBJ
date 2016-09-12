package io.github.ningwy.smartbj.basepage;

import android.view.View;

import io.github.ningwy.smartbj.ui.activity.MainActivity;

/**
 * 设置中心
 * Created by ningwy on 2016/8/3.
 */
public class SettingTagPage extends BaseTagPage {
    public SettingTagPage(MainActivity context) {
        super(context);
    }

    @Override
    public void initData() {

        //将左侧菜单按钮隐藏
        ib_main_menu.setVisibility(View.GONE);

        tv_title.setText("设置中心");
        tv_content.setText("这是设置中心的内容");
    }
}

package io.github.ningwy.smartbj.basepage;

import io.github.ningwy.smartbj.ui.activity.MainActivity;

/**
 * 智慧服务
 * Created by ningwy on 2016/8/3.
 */
public class SmartServiceTagPage extends BaseTagPage {
    public SmartServiceTagPage(MainActivity context) {
        super(context);
    }

    @Override
    public void initData() {
        tv_title.setText("智慧服务");
        tv_content.setText("这是智慧服务的内容");
    }
}

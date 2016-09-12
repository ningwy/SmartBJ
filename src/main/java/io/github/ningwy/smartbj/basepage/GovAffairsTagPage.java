package io.github.ningwy.smartbj.basepage;

import io.github.ningwy.smartbj.ui.activity.MainActivity;

/**
 * 政务页
 * Created by ningwy on 2016/8/3.
 */
public class GovAffairsTagPage extends BaseTagPage {
    public GovAffairsTagPage(MainActivity context) {
        super(context);
    }

    @Override
    public void initData() {

        tv_title.setText("政务");
        tv_content.setText("这是政务的内容");
    }
}

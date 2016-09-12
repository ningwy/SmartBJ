package io.github.ningwy.smartbj.newstagpage;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import io.github.ningwy.smartbj.ui.activity.MainActivity;

/**
 * 新闻页面下的子页面——互动
 * Created by ningwy on 2016/8/8.
 */
public class InteractNewsPage extends BaseNewsPage {
    public InteractNewsPage(MainActivity mainActivity) {
        super(mainActivity);
    }

    @Override
    protected View initView() {
        // 要展示的内容，
        TextView tv = new TextView(mainActivity);
        tv.setText("互动的内容");
        tv.setTextSize(25);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }
}

package io.github.ningwy.smartbj.basepage;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import io.github.ningwy.smartbj.R;
import io.github.ningwy.smartbj.ui.activity.MainActivity;

/**
 *
 * Created by ningwy on 2016/8/3.
 */
public class BaseTagPage {

    protected MainActivity mainActivity;

    protected View root;

    protected ImageButton ib_main_menu;
    protected ImageButton ib_main_listorgrid;

    protected TextView tv_title;

    protected FrameLayout fl_main_content;

    protected TextView tv_content;

    public BaseTagPage(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initView();
        initEvent();
    }

    /**
     * 初始化事件
     */
    protected void initEvent() {
        ib_main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.DrawerLayoutToggle();
            }
        });
    }

    /**
     * 初始化数据
     */
    protected void initData() {
    }

    /**
     * 初始化视图
     */
    private void initView() {
        root = View.inflate(mainActivity, R.layout.drawer_layout_main, null);
        ib_main_menu = (ImageButton) root.findViewById(R.id.ib_main_menu);
        ib_main_listorgrid = (ImageButton) root.findViewById(R.id.ib_main_listorgrid);
        tv_title = (TextView) root.findViewById(R.id.tv_title);
        fl_main_content = (FrameLayout) root.findViewById(R.id.fl_main_content);
        tv_content = (TextView) root.findViewById(R.id.tv_content);
    }

    /**
     * @return 得到根视图
     */
    public View getRoot() {
        initData();//等到要返回视图也就是页面要显示的时候才初始化数据
        return root;
    }

    /**
     * 在有左侧菜单的页面中，用于点击左侧菜单选项切换页面的方法
     *
     * @param postion 要切换到的页面的位置
     */
    public void switchPage(int postion) {

    }

}

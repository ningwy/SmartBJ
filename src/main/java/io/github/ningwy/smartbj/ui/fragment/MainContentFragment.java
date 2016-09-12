package io.github.ningwy.smartbj.ui.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.ningwy.smartbj.R;
import io.github.ningwy.smartbj.basepage.BaseTagPage;
import io.github.ningwy.smartbj.basepage.GovAffairsTagPage;
import io.github.ningwy.smartbj.basepage.HomeTagPage;
import io.github.ningwy.smartbj.basepage.NewsTagPage;
import io.github.ningwy.smartbj.basepage.SettingTagPage;
import io.github.ningwy.smartbj.basepage.SmartServiceTagPage;
import io.github.ningwy.smartbj.view.MyViewPager;

/**
 * 主内容区域的Fragment
 * Created by ningwy on 2016/8/3.
 */
public class MainContentFragment extends BaseFragment {

    private MyViewPager vp_main_content;

    private RadioGroup rg_tag;

    //五个tag
    private RadioButton rb_tag_home;

    private RadioButton rb_tag_news;

    private RadioButton rb_tag_govaffairs;

    private RadioButton rb_tag_smart_service;

    private RadioButton rb_tag_setting;

    //adapter数据
    private List<BaseTagPage> pages = new ArrayList<>();

    //viewpager当前页面编号
    private int selectId;

    @Override
    protected View initView() {
        View root = View.inflate(mainActivity, R.layout.fragment_content_tag, null);
        vp_main_content = (MyViewPager) root.findViewById(R.id.vp_main_content);
        rg_tag = (RadioGroup) root.findViewById(R.id.rg_tag);
        rb_tag_home = (RadioButton) root.findViewById(R.id.rb_tag_home);
        rb_tag_news = (RadioButton) root.findViewById(R.id.rb_tag_news);
        rb_tag_govaffairs = (RadioButton) root.findViewById(R.id.rb_tag_govaffairs);
        rb_tag_smart_service = (RadioButton) root.findViewById(R.id.rb_tag_smart_service);
        rb_tag_setting = (RadioButton) root.findViewById(R.id.rb_tag_setting);

        rb_tag_home.setChecked(true);//默认打开应用的位置是主页

        return root;
    }

    @Override
    protected void initEvent() {

        //给RadioGroup设置监听，用于点击各个tag时跳转到相应页面
        rg_tag.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_tag_home ://首页
                        selectId = 0;
                        break;

                    case R.id.rb_tag_news ://新闻中心
                        selectId = 1;
                        break;

                    case R.id.rb_tag_smart_service ://智慧服务
                        selectId = 2;
                        break;

                    case R.id.rb_tag_govaffairs ://政务
                        selectId = 3;
                        break;

                    case R.id.rb_tag_setting ://设置中心
                        selectId = 4;
                        break;
                }

                selectPage();//跳转到相应页面
                setDrawerOpen();
            }
        });

    }

    /**
     * 屏蔽首页(0)和设置中心(4)页面菜单按钮滑动出左侧菜单
     */
    private void setDrawerOpen() {
        if (selectId == 0 || selectId == pages.size() - 1) {
            mainActivity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            mainActivity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    /**
     * 左侧菜单点击，让主界面切换不同的页面
     * @param position 切换的界面编号
     */
    public void leftMenuClickSwitchPage(int position) {
        BaseTagPage baseTagPage = pages.get(selectId);
        baseTagPage.switchPage(position);
    }

    /**
     * 跳转到selectId数值所在的页面
     */
    private void selectPage() {
        vp_main_content.setCurrentItem(selectId);
    }

    @Override
    protected void initData() {
        pages.add(new HomeTagPage(mainActivity));
        pages.add(new NewsTagPage(mainActivity));
        pages.add(new SmartServiceTagPage(mainActivity));
        pages.add(new GovAffairsTagPage(mainActivity));
        pages.add(new SettingTagPage(mainActivity));

        vp_main_content.setAdapter(new MyAdapter());

        setDrawerOpen();
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseTagPage baseTagPage = pages.get(position);
            View view = baseTagPage.getRoot();//得到布局视图
            container.addView(view);//添加视图
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//添加过滤
        }

    }
}

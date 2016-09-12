package io.github.ningwy.smartbj.newstagpage;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.TabPageIndicator;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import io.github.ningwy.smartbj.R;
import io.github.ningwy.smartbj.domain.NewsData;
import io.github.ningwy.smartbj.newstpipage.NewsTPIPage;
import io.github.ningwy.smartbj.ui.activity.MainActivity;

/**
 * 新闻页面的子页面——新闻
 * Created by ningwy on 2016/8/8.
 */
public class SubNewsPage extends BaseNewsPage {

    @ViewInject(value = R.id.newcenter_tpi)
    private TabPageIndicator tpi_newscenter;

    @ViewInject(value = R.id.newcenter_vp)
    private ViewPager vp_newscenter;

    //数据容器
    private List<NewsData.NewsCenterData.Children> children;

    public SubNewsPage(MainActivity mainActivity, List<NewsData.NewsCenterData.Children> children) {
        super(mainActivity);
        this.children = children;
    }

    @Override
    protected View initView() {

        View root = View.inflate(mainActivity, R.layout.news_pager_content, null);
        x.view().inject(this, root);

        return root;
    }

    @Override
    protected void initEvent() {
        //给ViewPager添加事件，但是在ViewPagerIndicator里面做了将ViewPager至空处理，只能给
        //ViewPagerIndicator添加
        tpi_newscenter.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    //第一个可以划出左侧菜单
                    mainActivity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mainActivity.setDrawerLeftEdgeSize(mainActivity, mainActivity.getDrawerLayout(), 0.0f);
                } else {
                    //其他的不可用划出左侧菜单
//                    mainActivity.setDrawerLeftEdgeSize(mainActivity, mainActivity.getDrawerLayout(), 0.0f);
                    mainActivity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initData() {
        MyAdapter adapter = new MyAdapter();
        vp_newscenter.setAdapter(adapter);
        tpi_newscenter.setViewPager(vp_newscenter);
    }

    /**
     * 使用BitmapUtils显示图片
     *
     * 使用ViewUtils设置按钮的点击事件,方法必须要私有化， 参数格式和type的参数一致,为了混淆方便，方法名要以Event或者Click结尾
     * type可以不写，默认是点击事件类型
     */
    @Event(value = R.id.ib_subnews_next, type = View.OnClickListener.class)
    private void nextNewsTagEvent(View view) {
        //切换到下个页面
        vp_newscenter.setCurrentItem(vp_newscenter.getCurrentItem() + 1);
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public CharSequence getPageTitle(int position) {
            return children.get(position).title;
        }

        @Override
        public int getCount() {
            return children.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // 要展示的内容，
            NewsTPIPage newsTPIPage = new NewsTPIPage(mainActivity, children.get(position));
            View view = newsTPIPage.getRoot();
            //添加视图
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}

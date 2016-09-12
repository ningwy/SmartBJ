package io.github.ningwy.smartbj.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Field;
import java.util.List;

import io.github.ningwy.smartbj.R;
import io.github.ningwy.smartbj.domain.NewsData;
import io.github.ningwy.smartbj.ui.fragment.LeftMenuFragment;
import io.github.ningwy.smartbj.ui.fragment.MainContentFragment;

@ContentView(value = R.layout.activity_main)
public class MainActivity extends FragmentActivity {

    private static final String LEFT_MENU = "left_menu";
    private static final String MAIN_CONTENT = "main_content";

    private List<Fragment> fragments;

    //从服务器中获取到的新闻中心数据
    private NewsData newsData;

    @ViewInject(value = R.id.dl_main)
    private DrawerLayout dl_main;

    public DrawerLayout getDrawerLayout() {
        return dl_main;
    }

    public NewsData getNewsData() {
        return newsData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);

        initData();
        setDrawerLeftEdgeSize(this, dl_main, 0.2f);
    }

    /**
     * 初始化Fragment数据
     */
    private void initData() {
        FragmentManager manager = getSupportFragmentManager();
        //获取fragment事务
        FragmentTransaction transaction = manager.beginTransaction();
        //替换主要内容区域
        transaction.replace(R.id.ll_main_content, new MainContentFragment(), MAIN_CONTENT);
        //替换左侧侧滑菜单区域
        transaction.replace(R.id.ll_left_menu, new LeftMenuFragment(), LEFT_MENU);
        //提交事务
        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        if (dl_main.isDrawerOpen(Gravity.START)) {
            dl_main.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 控制DrawerLayout开关的方法
     */
    public void DrawerLayoutToggle() {
        if (dl_main.isDrawerOpen(Gravity.START)) {
            dl_main.closeDrawer(Gravity.START);
        } else {
            dl_main.openDrawer(Gravity.START);
        }
    }

    /**
     * 设置DrawerLayout划出来的感应位置(默认在屏幕边缘才能划出来)
     *
     * @param activity               Activity
     * @param drawerLayout           DrawerLayout
     * @param displayWidthPercentage 划出来时感应位置的屏幕比例
     */
    public void setDrawerLeftEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null)
            return;
        try {
            // find ViewDragHelper and set it accessible
            Field leftDraggerField = drawerLayout.getClass().getDeclaredField(
                    "mLeftDragger");
            leftDraggerField.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField
                    .get(drawerLayout);
            // find edgesize and set is accessible
            Field edgeSizeField = leftDragger.getClass().getDeclaredField(
                    "mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);
            // set new edgesize
            // Point displaySize = new Point();
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize,
                    (int) (dm.widthPixels * displayWidthPercentage)));
        } catch (NoSuchFieldException e) {
            // ignore
        } catch (IllegalArgumentException e) {
            // ignore
        } catch (IllegalAccessException e) {
            // ignore
        }
    }

    /**
     * @return 返回侧滑菜单fragment
     */
    public LeftMenuFragment getLeLeftMenuFragment() {
        FragmentManager manager = getSupportFragmentManager();
        return (LeftMenuFragment) manager.findFragmentByTag(LEFT_MENU);
    }

    /**
     * @return 返回主内容区域的fragment
     */
    public MainContentFragment getMainContentFragment() {
        return (MainContentFragment) getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT);
    }

    /**
     * 获取服务器的新闻中心的数据
     *
     * @param newsData 数据封装的对象
     */
    public void getNewsData(NewsData newsData) {
        this.newsData = newsData;
    }
}

package io.github.ningwy.smartbj.basepage;

import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import io.github.ningwy.smartbj.domain.NewsData;
import io.github.ningwy.smartbj.newstagpage.BaseNewsPage;
import io.github.ningwy.smartbj.newstagpage.InteractNewsPage;
import io.github.ningwy.smartbj.newstagpage.PhotoNewsPage;
import io.github.ningwy.smartbj.newstagpage.SubNewsPage;
import io.github.ningwy.smartbj.newstagpage.TopicNewsPage;
import io.github.ningwy.smartbj.ui.activity.MainActivity;
import io.github.ningwy.smartbj.ui.fragment.LeftMenuFragment;
import io.github.ningwy.smartbj.utils.MyConstants;
import io.github.ningwy.smartbj.utils.SPTools;

/**
 * 新闻中心
 * Created by ningwy on 2016/8/3.
 */
public class NewsTagPage extends BaseTagPage {

    //新闻中心子页面容器
    private List<BaseNewsPage> baseNewsPages = new ArrayList<>();

    //从服务器获取到的json数据封装成的对象
    private NewsData newsData;

    //新闻中心子页面编号
    private int selectIndex;

    private Gson gson;

    public NewsTagPage(MainActivity context) {
        super(context);
    }

    @Override
    public void initData() {

        gson = new Gson();

        //读取缓存数据
        String jsonCache = SPTools.getString(mainActivity, MyConstants.NEWS_URL, "");
        if (!TextUtils.isEmpty(jsonCache)) {
            //不为空
            parseJsonData(jsonCache);
        } else {
            //为空，从网络读取数据
            getDataFromNet();
        }

    }

    //从网络获取数据
    private void getDataFromNet() {
        RequestParams params = new RequestParams(MyConstants.NEWS_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //缓存数据
                SPTools.setString(mainActivity, MyConstants.NEWS_URL, result);
                parseJsonData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 解析json数据
     *
     * @param jsonData json数据
     */
    private void parseJsonData(String jsonData) {
        newsData = gson.fromJson(jsonData, NewsData.class);
        //通过MainActivity把newsData传递到LeLeftMenuFragment中去
        mainActivity.getLeLeftMenuFragment().setNewsData(newsData.data);

        //设置左侧菜单的回调监听
        mainActivity.getLeLeftMenuFragment().setOnSwitchPageListener(new LeftMenuFragment.OnSwitchPageListener() {
            @Override
            public void changePage(int position) {
                switchPage(position);
            }
        });

        //给新闻页面的子页面添加数据：往容器中添加数据
        for (NewsData.NewsCenterData data : newsData.data) {
            BaseNewsPage baseNewsPage = null;
            switch (data.id) {
                case "10000"://新闻
                    baseNewsPage = new SubNewsPage(mainActivity, newsData.data.get(0).children);
                    break;
                case "10002"://专题
                    baseNewsPage = new TopicNewsPage(mainActivity);
                    break;
                case "10003"://组图
                    baseNewsPage = new PhotoNewsPage(mainActivity);
                    break;
                case "10004"://互动
                    baseNewsPage = new InteractNewsPage(mainActivity);
                    break;
            }
            baseNewsPages.add(baseNewsPage);
        }
        //默认选择第0页也即新闻页
        switchPage(0);
    }

    @Override
    public void switchPage(int postion) {
        BaseNewsPage baseNewsPage = baseNewsPages.get(postion);

        //设置标题
        tv_title.setText(newsData.data.get(postion).title);

        //添加view之前清除之前的view
        fl_main_content.removeAllViews();

        //如果是PhotoNewsPage，则显示右边的两个切换按钮
        if (baseNewsPage instanceof PhotoNewsPage) {
            ib_main_listorgrid.setVisibility(View.VISIBLE);
            //将PhotoNewsPage页面和ib_main_listorgrid绑定到一起
            ib_main_listorgrid.setTag(baseNewsPage);
            ib_main_listorgrid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoNewsPage photoNewsPage = ((PhotoNewsPage) ib_main_listorgrid.getTag());
                    if (photoNewsPage != null) {
                        photoNewsPage.switchListAndGrid(ib_main_listorgrid);
                    }
                }
            });
        } else {
            ib_main_listorgrid.setVisibility(View.GONE);
        }

        //添加视图到flameLayout中
        fl_main_content.addView(baseNewsPage.getRoot());
    }
}

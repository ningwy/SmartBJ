package io.github.ningwy.smartbj.newstpipage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import io.github.ningwy.smartbj.R;
import io.github.ningwy.smartbj.domain.NewsData;
import io.github.ningwy.smartbj.domain.TPINewsData;
import io.github.ningwy.smartbj.ui.activity.MainActivity;
import io.github.ningwy.smartbj.ui.activity.NewsDetailActivity;
import io.github.ningwy.smartbj.utils.DensityUtil;
import io.github.ningwy.smartbj.utils.MyConstants;
import io.github.ningwy.smartbj.utils.SPTools;
import io.github.ningwy.smartbj.view.RefreshListView;

/**
 * 新闻页面下的子新闻页面的TPI滑轮对应的页面
 * Created by ningwy on 2016/8/14.
 */
public class NewsTPIPage {

    @ViewInject(value = R.id.vp_lunbo_pic)
    private ViewPager vp_lunbo_pic;

    @ViewInject(value = R.id.tv_lunbo_pic_desc)
    private TextView tv_lunbo_pic_desc;

    @ViewInject(value = R.id.ll_lunbo_pic_points)
    private LinearLayout ll_lunbo_pic_points;

    @ViewInject(value = R.id.lv_tpi_news)
    private RefreshListView lv_tpi_news;

    private MainActivity mainActivity;

    //数据
    private NewsData.NewsCenterData.Children children;
    //轮播图数据
    private List<TPINewsData.TPINewsData_Data.TopNews> tpiNewsDataTopNewsList = new ArrayList<>();
    //ListView数据
    private List<TPINewsData.TPINewsData_Data.News> tpiNewsDataNewsList = new ArrayList<>();

    private boolean isFresh = false;//记录是否是刷新数据

    private String loadingMoreDatasUrl;//加载更多数据的url
    private String loadingDataUrl;//加载最新数据的url

    //根视图
    private View root;

    private Gson gson;

    private MyAdapter adapter;
    private NewsListAdapter newsListAdapter;

    //轮播图片的位置记录
    private int picSelectedIndex;

    private Handler handler;

    public NewsTPIPage(MainActivity mainActivity, NewsData.NewsCenterData.Children children) {
        this.mainActivity = mainActivity;
        this.children = children;

        initView();
        initEvent();
    }

    /**
     * 解析json数据
     */
    private TPINewsData parseJson(String json) {
        TPINewsData tpiNewsData = gson.fromJson(json, TPINewsData.class);
        //初始化加载更多的url
        if (!TextUtils.isEmpty(tpiNewsData.data.more)) {
            loadingMoreDatasUrl = MyConstants.SERVER_URL + tpiNewsData.data.more;
        } else {
            loadingMoreDatasUrl = "";
        }

        return tpiNewsData;
    }

    /**
     * 处理数据
     *
     * @param tpiNewsData 传递过来的数据的对象
     */
    private void processData(TPINewsData tpiNewsData) {
        setTopNewsData(tpiNewsData);
        //初始化轮播图小圆点
        initPoints();
        //设置图片描述信息和小圆点
        setPicDescAndPoints(picSelectedIndex);
        //让轮播图开始自动播放
        startPicAutoPlay();
        //设置newsdata
        setNewsData(tpiNewsData);
    }

    /**
     * 设置news数据
     *
     * @param tpiNewsData
     */
    private void setNewsData(TPINewsData tpiNewsData) {
        tpiNewsDataNewsList = tpiNewsData.data.news;
        adapter.notifyDataSetChanged();//通知数据更新
        newsListAdapter.notifyDataSetChanged();//通知数据更新
    }

    /**
     * 设置topnews数据
     *
     * @param tpiNewsData
     */
    private void setTopNewsData(TPINewsData tpiNewsData) {
        tpiNewsDataTopNewsList = tpiNewsData.data.topnews;
    }

    /**
     * 轮播图开始自动播放
     */
    private void startPicAutoPlay() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //防止访问数据失败时被除数为0
                if (vp_lunbo_pic.getAdapter().getCount() == 0) {
                    return;
                }
                vp_lunbo_pic.setCurrentItem((vp_lunbo_pic.getCurrentItem() + 1) % vp_lunbo_pic.getAdapter().getCount());
                //清除handler，为了不让第二次播放的时候轮播速度变快了
                handler.removeCallbacksAndMessages(null);//null表示清除所有
                handler.postDelayed(this, 2000);
            }
        }, 2000);
    }

    /**
     * 轮播图停止播放
     */
    private void stopPicAutoPlay() {
        //清除handler
        handler.removeCallbacksAndMessages(null);//null表示清除所有
    }

    /**
     * 初始化小圆点
     */
    private void initPoints() {
        //添加之前先移除
        ll_lunbo_pic_points.removeAllViews();
        //添加小圆点
        for (int i = 0; i < tpiNewsDataTopNewsList.size(); i++) {
            //设置圆点
            View v_point = new View(mainActivity);
            v_point.setBackgroundResource(R.drawable.tpi_pic_point_selector);
            v_point.setEnabled(false);//默认为灰点
            //设置参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(mainActivity, 5), DensityUtil.dip2px(mainActivity, 5));
            params.leftMargin = DensityUtil.dip2px(mainActivity, 10);
            /**
             * 注意是给v_point设置参数，而不是给ll_lunbo_pic_points设置参数
             */
            v_point.setLayoutParams(params);
            ll_lunbo_pic_points.addView(v_point);
        }
    }

    /**
     * 设置图片描述信息和小圆点状态
     */
    private void setPicDescAndPoints(int position) {
        //防止数组越界
        if (position < 0 || position > tpiNewsDataTopNewsList.size() - 1) {
            return;
        }
        //设置描述信息
        tv_lunbo_pic_desc.setText(tpiNewsDataTopNewsList.get(picSelectedIndex).title);
        //设置点是否选择
        for (int i = 0; i < tpiNewsDataTopNewsList.size(); i++) {
            //设置点的状态
            ll_lunbo_pic_points.getChildAt(i).setEnabled(position == i);
        }
    }

    /**
     * 从网络获取数据
     */
    private void getDataFromNet(final String url, final boolean isLoadingMore) {
        //网络请求数据
        RequestParams requestParams = new RequestParams(url);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                //缓存数据
                SPTools.setString(mainActivity, url, result);

                TPINewsData tpiNewsData = parseJson(result);//解析json数据

                //判断是否是加载更多数据
                if (isLoadingMore) {
                    //原有的数据 +　新数据
                    tpiNewsDataNewsList.addAll(tpiNewsData.data.news);
                    //更新界面
                    newsListAdapter.notifyDataSetChanged();
                    Toast.makeText(mainActivity, "加载数据成功", Toast.LENGTH_SHORT).show();
                } else {
                    //第一次取数据或刷新数据
                    //处理数据
                    processData(tpiNewsData);
                    if (isFresh) {
                        Toast.makeText(mainActivity, "刷新数据成功", Toast.LENGTH_SHORT).show();
                    }
                }
                lv_tpi_news.refreshStateFinish();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(mainActivity, "数据更新失败.......", Toast.LENGTH_SHORT).show();
                lv_tpi_news.refreshStateFinish();
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
     * 初始化事件
     */
    private void initEvent() {

        //ListView的点击事件
        lv_tpi_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newsDetailIntent = new Intent(mainActivity, NewsDetailActivity.class);
                TPINewsData.TPINewsData_Data.News news = tpiNewsDataNewsList.get(position - 1);

                String newsId = news.id;//新闻的id
                String readNewsId = SPTools.getString(mainActivity, MyConstants.READNEWSID, "");//已读新闻的id
                if (TextUtils.isEmpty(readNewsId)) {
                    readNewsId = newsId;//如果为空，则将获取到的新闻id给已读id
                } else {
                    //不为空，则添加新闻id
                    readNewsId += "," + newsId;
                }
                //保存到SharedPreferences中去
                SPTools.setString(mainActivity, MyConstants.READNEWSID, readNewsId);
                //添加完id后修改已读新闻字体颜色
                newsListAdapter.notifyDataSetChanged();

                //新闻详情页的url
                String newsDetailUrl = news.url;
                //新闻详情标题
                String newsTitle = news.title;
                //添加标题
                newsDetailIntent.putExtra("newsDetailUrl", newsDetailUrl);
                newsDetailIntent.putExtra("newsTitle", newsTitle);
                mainActivity.startActivity(newsDetailIntent);
            }
        });

        //ListView的刷新数据和加载更多数据的回调监听
        lv_tpi_news.setOnRefreshDataListener(new RefreshListView.OnRefreshDataListener() {
            @Override
            public void refreshData() {
                isFresh = true;
                //刷新数据
                getDataFromNet(loadingDataUrl, false);
            }

            @Override
            public void loadingMore() {
                //判断是否有更多的数据
                if (TextUtils.isEmpty(loadingMoreDatasUrl)) {
                    Toast.makeText(mainActivity, "没有更多数据", Toast.LENGTH_SHORT).show();
                    //关闭刷新数据的状态
                    lv_tpi_news.refreshStateFinish();
                } else {
                    //有数据
                    getDataFromNet(loadingMoreDatasUrl, true);
                }
            }
        });

        //ViewPager的翻页监听事件
        vp_lunbo_pic.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                picSelectedIndex = position;//记录位置
                //设置图片描述信息和小圆点
                setPicDescAndPoints(picSelectedIndex);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //ViewPager的触摸监听事件
        vp_lunbo_pic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://按下时
                        stopPicAutoPlay();//停止播放
                        break;

                    case MotionEvent.ACTION_CANCEL://取消时
                        startPicAutoPlay();//开始播放
                        break;

                    case MotionEvent.ACTION_UP://放开时
                        startPicAutoPlay();//开始播放
                        break;
                }
                return true;
            }
        });

    }

    /**
     * 初始化视图
     */
    private void initView() {
        root = View.inflate(mainActivity, R.layout.news_tpi_page, null);
        x.view().inject(this, root);//xutils3注入

        View lunbo_pic_view = View.inflate(mainActivity, R.layout.lunbo_pic, null);
        x.view().inject(this, lunbo_pic_view);

        lv_tpi_news.setIsRefreshHead(true);//启用自定义的下拉刷新和上拉加载更多功能
        //把轮播图加到listView中
        lv_tpi_news.addHeaderView(lunbo_pic_view);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        handler = new Handler();
        gson = new Gson();
        adapter = new MyAdapter();
        newsListAdapter = new NewsListAdapter();
        //设置adapter
        vp_lunbo_pic.setAdapter(adapter);
        lv_tpi_news.setAdapter(newsListAdapter);

        loadingDataUrl = MyConstants.SERVER_URL + children.url;
        //读取缓存
        String jsonCache = SPTools.getString(mainActivity, loadingDataUrl, "");

        if (!TextUtils.isEmpty(jsonCache)) {
            //不为空
            TPINewsData tpiNewsData = parseJson(jsonCache);//解析接送数据
            processData(tpiNewsData);
        }
        getDataFromNet(loadingDataUrl, false);//从网络获取数据
    }

    /**
     * @return 得到根视图
     */
    public View getRoot() {

        //返回视图之前初始化数据
        initData();

        return root;
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return tpiNewsDataTopNewsList.size();
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {

            ImageView imageView = new ImageView(mainActivity);

            ImageOptions options = new ImageOptions.Builder()
                    // 是否忽略GIF格式的图片
                    .setIgnoreGif(false)
                    // 图片缩放模式
                    .setImageScaleType(ImageView.ScaleType.FIT_XY)
                    // 下载中显示的图片
                    .setLoadingDrawableId(R.drawable.home_scroll_default)
                    // 下载失败显示的图片
                    .setFailureDrawableId(R.drawable.home_scroll_default)
                    // 得到ImageOptions对象
                    .build();
            x.image().bind(imageView, tpiNewsDataTopNewsList.get(position).topimage, options);
            //添加到容器中
            container.addView(imageView);

            return imageView;
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

    private class NewsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return tpiNewsDataNewsList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHodler;
            if (convertView == null) {
                convertView = View.inflate(mainActivity, R.layout.news_tpi_list_item, null);
                viewHodler = new ViewHolder();
                viewHodler.iv_tpi_list_icon = (ImageView) convertView.findViewById(R.id.iv_tpi_list_icon);
                viewHodler.tv_tpi_list_title = (TextView) convertView.findViewById(R.id.tv_tpi_list_title);
                viewHodler.tv_tpi_list_time = (TextView) convertView.findViewById(R.id.tv_tpi_list_time);
                viewHodler.iv_tpi_list_comment = (ImageView) convertView.findViewById(R.id.iv_tpi_list_comment);
                convertView.setTag(viewHodler);
            } else {
                viewHodler = (ViewHolder) convertView.getTag();
            }

            String newsId = tpiNewsDataNewsList.get(position).id;
            String readNewsId = SPTools.getString(mainActivity, MyConstants.READNEWSID, "");
            if (TextUtils.isEmpty(readNewsId) || !readNewsId.contains(newsId)) {
                //未读新闻设置为黑色
                viewHodler.tv_tpi_list_title.setTextColor(Color.BLACK);
                viewHodler.tv_tpi_list_time.setTextColor(Color.BLACK);
            } else {
                //已读新闻设置为灰色
                viewHodler.tv_tpi_list_title.setTextColor(Color.GRAY);
                viewHodler.tv_tpi_list_time.setTextColor(Color.GRAY);
            }

            //设置数据
            //设置图标
            ImageOptions options = new ImageOptions.Builder()
                    // 是否忽略GIF格式的图片
                    .setIgnoreGif(false)
                    // 图片缩放模式
                    .setImageScaleType(ImageView.ScaleType.FIT_XY)
                    // 得到ImageOptions对象
                    .build();
            String imageUrl = tpiNewsDataNewsList.get(position).listimage;
            x.image().bind(viewHodler.iv_tpi_list_icon, imageUrl, options);

            viewHodler.tv_tpi_list_title.setText(tpiNewsDataNewsList.get(position).title);
            viewHodler.tv_tpi_list_time.setText(tpiNewsDataNewsList.get(position).pubdate);

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView iv_tpi_list_icon;
        TextView tv_tpi_list_title;
        TextView tv_tpi_list_time;
        ImageView iv_tpi_list_comment;
    }

}

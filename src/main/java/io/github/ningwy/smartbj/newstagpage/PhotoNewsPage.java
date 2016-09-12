package io.github.ningwy.smartbj.newstagpage;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import io.github.ningwy.smartbj.R;
import io.github.ningwy.smartbj.domain.PhotosNewsData;
import io.github.ningwy.smartbj.ui.activity.MainActivity;
import io.github.ningwy.smartbj.utils.BitmapCacheUtils;
import io.github.ningwy.smartbj.utils.MyConstants;
import io.github.ningwy.smartbj.utils.SPTools;

/**
 * 新闻页面下的子页面——组图
 * Created by ningwy on 2016/8/8.
 */
public class PhotoNewsPage extends BaseNewsPage {

    @ViewInject(value = R.id.lv_photos_news)
    private ListView lv_photos_news;

    @ViewInject(value = R.id.gv_photos_news)
    private GridView gv_photos_news;

    //数据容器
    private List<PhotosNewsData.PhotosNewsData_Data.News> news = new ArrayList<>();

    private Gson gson;
    private MyAdapter adapter;

    private BitmapCacheUtils bitmapCacheUtils;

    /**
     * 切换ListView和GridView的显示
     *  false：ListView显示
     *  true：GridView显示
     */
    private boolean isGridShow;

    public PhotoNewsPage(MainActivity mainActivity) {
        super(mainActivity);
    }

    @Override
    protected View initView() {
        // 要展示的内容，
        View photos_view = View.inflate(mainActivity, R.layout.photos_news_layout, null);
        x.view().inject(this, photos_view);//xutils3注入
        return photos_view;
    }

    @Override
    protected void initData() {

        bitmapCacheUtils = new BitmapCacheUtils(mainActivity);

        gson = new Gson();
        adapter = new MyAdapter();
        lv_photos_news.setAdapter(adapter);
        gv_photos_news.setAdapter(adapter);

        //获取缓存数据
        String jsonCache = SPTools.getString(mainActivity, MyConstants.PHOTOS_URL, "");
        if (!TextUtils.isEmpty(jsonCache)) {
            //不为空
            PhotosNewsData photosNewsData = parseJson(jsonCache);//解析json数据
            processData(photosNewsData);//处理数据
        } else {
            //为空
            getDataFromNet();//从网络获取数据
        }
    }

    /**
     * 根据isGridShow切换ListView和GridView的显示
     * @param imageButton
     */
    public void switchListAndGrid(ImageButton imageButton) {
        if (isGridShow) {
            //GridView显示
            imageButton.setImageResource(R.drawable.icon_pic_grid_type);
            gv_photos_news.setVisibility(View.VISIBLE);
            lv_photos_news.setVisibility(View.GONE);
        } else {
            //ListView显示
            imageButton.setImageResource(R.drawable.icon_pic_list_type);
            lv_photos_news.setVisibility(View.VISIBLE);
            gv_photos_news.setVisibility(View.GONE);
        }
        //取反
        isGridShow = !isGridShow;
    }

    /**
     * 从网络获取数据
     */
    private void getDataFromNet() {
        RequestParams params = new RequestParams(MyConstants.PHOTOS_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                //缓存数据
                SPTools.setString(mainActivity, MyConstants.PHOTOS_URL, result);
                //解析数据
                PhotosNewsData photosNewsData = parseJson(result);//解析json数据
                processData(photosNewsData);//处理数据
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
     * 处理数据
     * @param photosNewsData 出入的要出里的数据
     */
    private void processData(PhotosNewsData photosNewsData) {
        news = photosNewsData.data.news;
        adapter.notifyDataSetChanged();//通知数据更新
    }

    /**
     * 处理json数据
     * @param jsonCache json数据
     */
    private PhotosNewsData parseJson(String jsonCache) {
        PhotosNewsData photosNewsData = gson.fromJson(jsonCache, PhotosNewsData.class);
        return photosNewsData;
    }



    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return news.size();
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
            ViewHodler holder;
            if (convertView == null) {
                convertView = View.inflate(mainActivity, R.layout.photos_news_item, null);
                holder = new ViewHodler();
                holder.iv_photos_pic = (ImageView) convertView.findViewById(R.id.iv_photos_pic);
                holder.tv_photos_desc = (TextView) convertView.findViewById(R.id.tv_photos_desc);
                convertView.setTag(holder);
            } else {
                holder = (ViewHodler) convertView.getTag();
            }
            //填充数据
            PhotosNewsData.PhotosNewsData_Data.News photoNews = PhotoNewsPage.this.news.get(position);
            //图片描述
            holder.tv_photos_desc.setText(photoNews.title);

            bitmapCacheUtils.display(holder.iv_photos_pic, photoNews.listimage);

            return convertView;
        }
    }

    class ViewHodler {
        ImageView iv_photos_pic;
        TextView tv_photos_desc;
    }
}

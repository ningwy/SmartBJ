package io.github.ningwy.smartbj.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import io.github.ningwy.smartbj.R;
import io.github.ningwy.smartbj.utils.DensityUtil;
import io.github.ningwy.smartbj.utils.MyConstants;
import io.github.ningwy.smartbj.utils.SPTools;

public class GuideActivity extends Activity {

    private ViewPager vp_guide;
    private LinearLayout ll_guide_points;
    private View red_point;
    private Button bt_guide_startexp;

    List<ImageView> data;

    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);

        initView();//初始化视图

        initData();//初始化数据

        initEven();//初始化事件

    }

    //初始化事件
    private void initEven() {

        //开始体验 按钮点击事件
        bt_guide_startexp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存设置状态
                SPTools.setBoolean(getApplicationContext(), MyConstants.ISSETUP, true);
                //进入主页面
                Intent main = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(main);
                //关闭自己
                finish();
            }
        });

        //ViewPager的页面滑动监听事件
        vp_guide.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * 页面滑动过程中回调的方法
             * @param position 当前处于哪个页面(滑动开始的页面)
             * @param positionOffset 滑动的偏移量比例(小于1)
             * @param positionOffsetPixels 滑动经过的像素
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //计算红点的偏移量
                int dis_point = ll_guide_points.getChildAt(1).getLeft() - ll_guide_points.getChildAt(0).getLeft();
                float distance = dis_point * (positionOffset + position);
                //设置红点的偏移量
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) red_point.getLayoutParams();
                params.leftMargin = Math.round(distance);
                red_point.setLayoutParams(params);
            }

            //当滑动到某一个页面时回调的方法(有position参数，可以知道具体滑动到哪个页面)
            @Override
            public void onPageSelected(int position) {
                //当页面滑动到最后一个页面时，显示"开始体验"按钮
                if (position == data.size() - 1) {
                    bt_guide_startexp.setVisibility(View.VISIBLE);
                } else {
                    bt_guide_startexp.setVisibility(View.GONE);
                }
            }

            //页面滑动完成后回调的方法
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //初始化数据
    private void initData() {
        int[] pics = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
        data = new ArrayList<>();

        //往data填充数据
        for (int i = 0; i < pics.length; i++) {
            ImageView image = new ImageView(this);
            image.setImageResource(pics[i]);
            data.add(image);

            //初始化ll_guide_points的视图
            View gray_points = new View(this);
            gray_points.setBackgroundResource(R.drawable.gray_points);
            int dip = 10;
            //设置参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(this, dip), DensityUtil.dip2px(this, dip));
            if (i != 0) {
                params.leftMargin = DensityUtil.dip2px(this, dip);//设置左边距，让三个点有间隔(但第一个点不需要，故没有加)
            }
            gray_points.setLayoutParams(params);
            //添加到ll_guide_points中
            ll_guide_points.addView(gray_points);
        }
        //为ViewPager设置adapter
        adapter = new MyAdapter();
        vp_guide.setAdapter(adapter);

    }

    //初始化视图
    private void initView() {
        vp_guide = (ViewPager) findViewById(R.id.vp_guide);
        ll_guide_points = (LinearLayout) findViewById(R.id.ll_guide_points);
        red_point = findViewById(R.id.view_red_point);
        bt_guide_startexp = (Button) findViewById(R.id.bt_guide_startexp);

    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View child = data.get(position);
            container.addView(child);
            return child;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);//从ViewGroup中移除view
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {

            return view == object;
        }
    }
}

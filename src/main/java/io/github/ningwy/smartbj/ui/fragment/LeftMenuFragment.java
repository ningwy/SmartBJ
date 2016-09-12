package io.github.ningwy.smartbj.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.ningwy.smartbj.R;
import io.github.ningwy.smartbj.domain.NewsData;
import io.github.ningwy.smartbj.utils.DensityUtil;

/**
 * 左侧菜单的Fragment
 * Created by ningwy on 2016/8/3.
 */
public class LeftMenuFragment extends BaseFragment {

    public interface OnSwitchPageListener {
        void changePage(int position);
    }

    public OnSwitchPageListener listener;

    public void setOnSwitchPageListener(OnSwitchPageListener listener) {
        this.listener = listener;
    }

    //从新闻中心传过来的数据
    private List<NewsData.NewsCenterData> data = new ArrayList<>();

    private ListView listView;

    private MyAdapter adapter;

    //选中的位置
    private int selectPosition;

    @Override
    protected View initView() {
        listView = new ListView(mainActivity);

        //设置参数
        //设置选择背景颜色：透明色
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        //选中拖动的背景色 设置成透明
        listView.setCacheColorHint(Color.TRANSPARENT);

        //设置距离顶部距离:200dp
        listView.setPadding(0, DensityUtil.px2dip(mainActivity, 200), 0, 0);

        //设置每个item之间的距离
        listView.setDividerHeight(50);



        return listView;
    }

    public void setNewsData(List<NewsData.NewsCenterData> data) {
        this.data = data;
        adapter.notifyDataSetChanged();//数据传递过来时刷新adapter
    }

    @Override
    protected void initData() {
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
    }

    @Override
    protected void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;

                //切换到相应的页面
                if (listener != null) {
                    listener.changePage(position);
                } else {
//                    mainActivity.getMainContentFragment().leftMenuClickSwitchPage(position);
                }

                //关闭DrawerLayout
                mainActivity.DrawerLayoutToggle();

                //更新界面数据
                adapter.notifyDataSetChanged();
            }
        });
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = (TextView) View.inflate(mainActivity, R.layout.drawer_layout_left, null);
            } else {
                textView = (TextView) convertView;
            }
            textView.setText(data.get(position).title);

            //选中的textview修改状态
            textView.setEnabled(selectPosition == position);

            return textView;
        }
    }

}

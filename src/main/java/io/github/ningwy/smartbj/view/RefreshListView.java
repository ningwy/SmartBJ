package io.github.ningwy.smartbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.ningwy.smartbj.R;

/**
 * 自定义能够刷新数据的ListView
 * Created by ningwy on 2016/8/20.
 */
public class RefreshListView extends ListView {

    //头部组件的容器
    private LinearLayout head_container;
    @ViewInject(value = R.id.pb_refresh_listview_head)
    private ProgressBar pb_refresh_listview_head;
    @ViewInject(value = R.id.iv_refresh_listview_arrow)
    private ImageView iv_refresh_listview_arrow;
    @ViewInject(value = R.id.tv_refresh_listview_head_state)
    private TextView tv_refresh_listview_head_state;
    @ViewInject(value = R.id.tv_refresh_listview_time)
    private TextView tv_refresh_listview_time;

    @ViewInject(value = R.id.ll_refresh_listview_head)
    private LinearLayout ll_refresh_listview_head;

    //尾部组件
    private View foot;

    //头部容器的高度
    private int headHeight;
    //尾部组件的高度
    private int footHeigth;

    //向上的旋转动画
    private RotateAnimation up_ra;
    //向上的旋转动画
    private RotateAnimation down_ra;

//    private float downY = -1;
//    public static final int DOWN_REFRESH = 0;//下拉刷新
//    public static final int UP_REFRESH = 1;//松开刷新
//    public static final int REFRESH_FINISH = 2;//刷新完成
//    public static final int REFRESHING = 3;//刷新中
//    public int refreshState;//记录刷新的状态

    private int listViewOnScreanY;                // listview在屏幕中的y轴坐标位置

    private float downY = -1;
    private final int PULL_DOWN = 1;            // 下拉刷新状态
    private final int RELEASE_STATE = 2;            // 松开刷新
    private final int REFRESHING = 3;            // 正在刷新
    private int currentState = PULL_DOWN;    // 当前的状态

    private boolean isEnablePullRefresh;//下拉刷新是否可用
    private boolean	isLoadingMore;//是否是加载更多数据

    private View lunbotu;//轮播图组件

    private OnRefreshDataListener listener;//请求数据回调的接口

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initAnimation();
        initEvent();
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        //添加当前Listview的滑动事件
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //状态停止，如果listview显示最后一条 加载更多数据 的显示
                // 是否最后一条数据显示
                //System.out.println(getLastVisiblePosition() + ":" + getAdapter().getCount());
                if (getLastVisiblePosition() == getAdapter().getCount() - 1 && !isLoadingMore) {
                    //最后一条数据,显示加载更多的 组件
                    foot.setPadding(0, 0, 0, 0);//显示加载更多
                    setSelection(getAdapter().getCount());

                    //加载更多数据
                    isLoadingMore = true;
                    if (listener != null) {
                        listener.loadingMore();//实现该接口的组件取完成数据的加载
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        //向上动画
        up_ra = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        up_ra.setDuration(500);
        up_ra.setFillAfter(true);
        //向下动画
        down_ra = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        down_ra.setDuration(500);
        down_ra.setFillAfter(true);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        initHead();
        initFoot();
    }

    /**
     * 初始化尾部组件
     */
    private void initFoot() {
        foot = View.inflate(getContext(), R.layout.refresh_listview_foot, null);
        x.view().inject(this, foot);//xutils3注入
        //测量尾部组件的高度
        foot.measure(0, 0);
        footHeigth = foot.getMeasuredHeight();
        this.addFooterView(foot);//添加尾部组件

        //隐藏尾部组件
        foot.setPadding(0, 0, 0, -footHeigth);
    }

    /**
     * 初始化头部组件
     */
    private void initHead() {
        head_container = (LinearLayout) View.inflate(getContext(), R.layout.refresh_listview_container, null);
        x.view().inject(this, head_container);//xutils3注入
        //测量头部组件的高度
        head_container.measure(0, 0);
        headHeight = head_container.getMeasuredHeight();
        //隐藏头部组件
        head_container.setPadding(0, -headHeight, 0, 0);
        this.addHeaderView(head_container);//添加头部组件
    }

    /*
     * (non-Javadoc)
	 *
	 * @see android.widget.AbsListView#onTouchEvent(android.view.MotionEvent)
	 * 覆盖此完成自己的事件处理
	 */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 需要我们的功能屏蔽掉父类的touch事件
        // 下拉拖动（当listview显示第一个条数据）

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:// 按下
                downY = ev.getY();// 按下时y轴坐标
                break;
            case MotionEvent.ACTION_MOVE:// 移动

                if (!isEnablePullRefresh) {
                    //没有启用下拉刷新
                    break;
                }

                //现在是否处于刷新数据的状态
                if (currentState == REFRESHING) {
                    //正在刷新
                    break;
                }


                if (!isLunboFullShow()) {
                    // 轮播图没有完全显示
                    break;
                }

                if (downY == -1) { // 按下的时候没有获取坐标
                    downY = ev.getY();
                }

                // 获取移动位置的坐标
                float moveY = ev.getY();

                // 移动的位置间距
                float dy = moveY - downY;
                // System.out.println("dy:" + dy);
                // 下拉拖动（当listview显示第一个条数据）处理自己的事件，不让listview原生的拖动事件生效
                if (dy > 0 && getFirstVisiblePosition() == 0) {

                    // 当前padding top 的参数值
                    float scrollYDis = -headHeight + dy;

                    if (scrollYDis < 0 && currentState != PULL_DOWN) {
                        // 刷新头没有完全显示
                        // 下拉刷新的状态
                        currentState = PULL_DOWN;// 目的只执行一次
                        refreshState();
                    } else if (scrollYDis >= 0 && currentState != RELEASE_STATE) {
                        currentState = RELEASE_STATE;// 记录松开刷新，只进了一次
                        refreshState();
                    }
                    head_container.setPadding(0, (int) scrollYDis, 0, 0);
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:// 松开
                downY = -1;
                //判断状态
                //如果是PULL_DOWN状态,松开恢复原状
                if (currentState == PULL_DOWN) {
                    head_container.setPadding(0, -headHeight, 0, 0);
                } else if (currentState == RELEASE_STATE) {
                    //刷新数据
                    head_container.setPadding(0, 0, 0, 0);
                    currentState = REFRESHING;//改变状态为正在刷新数据的状态
                    refreshState();//刷新界面
                    //真的刷新数据
                    if (listener != null) {
                        listener.refreshData();
                    }
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void refreshState() {
        switch (currentState) {
            case PULL_DOWN:// 下拉刷新
                System.out.println("下拉刷新");
                //改变文件
                tv_refresh_listview_head_state.setText("下拉刷新");
                iv_refresh_listview_arrow.startAnimation(down_ra);
                break;
            case RELEASE_STATE:// 松开刷新
                System.out.println("松开刷新");
                tv_refresh_listview_head_state.setText("松开刷新");
                iv_refresh_listview_arrow.startAnimation(up_ra);
                break;
            case REFRESHING://正在刷新状态
                iv_refresh_listview_arrow.clearAnimation();//清除所有动画
                iv_refresh_listview_arrow.setVisibility(View.GONE);//隐藏箭头
                pb_refresh_listview_head.setVisibility(View.VISIBLE);//显示进度条
                tv_refresh_listview_head_state.setText("正在刷新数据");

            default:
                break;
        }

    }

    /**
     * 用户自己选择是否启用下拉刷新头的功能
     * @param isPullrefresh
     *            true 启用下拉刷新 false 不用下拉刷新
     *
     */
    public void setIsRefreshHead(boolean isPullrefresh) {
        isEnablePullRefresh = isPullrefresh;
    }

    /**
     * 刷新数据成功,处理结果
     */
    public void refreshStateFinish() {
        //下拉刷新
        if (isLoadingMore) {
            //加载更多数据
            isLoadingMore = false;
            //隐藏加载更多数据的组件
            foot.setPadding(0, -footHeigth, 0, 0);
        } else {
            //改变下拉刷新
            tv_refresh_listview_head_state.setText("下拉刷新");
            iv_refresh_listview_arrow.setVisibility(View.VISIBLE);//显示箭头
            pb_refresh_listview_head.setVisibility(View.INVISIBLE);//隐藏进度条
            //设置刷新时间为当前时间
            tv_refresh_listview_time.setText(getDate());
            //隐藏刷新的头布局
            head_container.setPadding(0, -headHeight, 0, 0);

            currentState = PULL_DOWN;//初始化为下拉刷新的状态
        }
    }

    /**
     * @return 轮播图是否完全显示
     */
    private boolean isLunboFullShow() {
        // TODO Auto-generated method stub
        // 判断轮播图是否完全显示

        int[] location = new int[2];
        // 如果轮播图没有完全显示，相应的是Listview的事件
        // 判断轮播图是否完全显示
        // 取listview在屏幕中坐标 和 轮播图在屏幕中的坐标 判断
        // 取listview在屏幕中坐标
        if (listViewOnScreanY == 0) {
            this.getLocationOnScreen(location);
            // 获取listview在屏幕中的y轴坐标
            listViewOnScreanY = location[1];
        }

        // 轮播图在屏幕中的坐标
        lunbotu.getLocationOnScreen(location);
        // 判断
        if (location[1] < listViewOnScreanY) {
            // 轮播图没有完全显示
            // 继续相应listview的事件
            // System.out.println("没有显示");
            return false;
        }
        return true;

    }

    public interface OnRefreshDataListener {
        void refreshData();
        void loadingMore();
    }

    public void setOnRefreshDataListener(OnRefreshDataListener listener) {
        this.listener = listener;
    }

    /**
     * @return 返回格式化的系统当前时间
     */
    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        return format.format(new Date());
    }

    @Override
    public void addHeaderView(View view) {
        //判断  如果你使用下拉刷新 ，把头布局加下拉刷新的容器中，否则加载原生Listview的中
        if (isEnablePullRefresh) {
            //启用下拉刷新
            // 轮播图的组件
            lunbotu = view;
            head_container.addView(view);
        } else {
            //使用原生的ListView
            super.addHeaderView(view);
        }

    }
}

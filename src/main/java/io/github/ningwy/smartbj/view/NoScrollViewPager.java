package io.github.ningwy.smartbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 不能滑动的ViewPager
 * Created by ningwy on 2016/8/5.
 */
public class NoScrollViewPager extends MyViewPager {

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 返回false表示对触摸事件不处理
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept(拦截) the motion.
         * If we return true, onMotionEvent will be called and we do the actual
         * scrolling there.
         */
        return false;
    }
}

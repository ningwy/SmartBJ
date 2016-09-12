package io.github.ningwy.smartbj.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import io.github.ningwy.smartbj.R;
import io.github.ningwy.smartbj.utils.MyConstants;
import io.github.ningwy.smartbj.utils.SPTools;


public class SplashActivity extends Activity {

    private ImageView iv_splash;

    private AnimationSet as;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        initView();//初始化视图

        initAnimation();//初始化播放动画

        initEven();//初始化事件

    }

    //初始化事件
    private void initEven() {

        //给动画集合设置监听
        as.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                boolean isSetup = SPTools.getBoolean(SplashActivity.this, MyConstants.ISSETUP, false);
                if (isSetup) {
                    //进入主页面
                    Intent main = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(main);
                } else {
                    //进入设置向导页面
                    Intent guide = new Intent(SplashActivity.this, GuideActivity.class);
                    startActivity(guide);
                }
                //关闭自己
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    //初始化播放动画
    private void initAnimation() {
        as = new AnimationSet(false);

        //旋转动画
        RotateAnimation ra = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(2000);
        ra.setFillAfter(true);//播放完成后停留在当前状态

        //缩放动画
        ScaleAnimation sa = new ScaleAnimation(0, 1.0f, 0, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(2000);
        sa.setFillAfter(true);//播放完成后停留在当前状态

        //渐变动画
        AlphaAnimation aa = new AlphaAnimation(0, 1.0f);
        aa.setDuration(2000);
        aa.setFillAfter(true);//播放完成后停留在当前状态

        //添加到动画及
        as.addAnimation(ra);
        as.addAnimation(sa);
        as.addAnimation(aa);

        //播放动画
        iv_splash.startAnimation(as);

    }

    //初始化视图
    private void initView() {
        iv_splash = (ImageView) findViewById(R.id.iv_splash);
    }
}

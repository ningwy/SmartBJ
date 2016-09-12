package io.github.ningwy.smartbj;

import android.app.Application;

import org.xutils.x;

/**
 *
 * Created by ningwy on 2016/8/3.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
//        x.Ext.setDebug(BuildConfig.DEBUG);//设置输出debug日志，不过会影响性能，暂时关掉
    }
}

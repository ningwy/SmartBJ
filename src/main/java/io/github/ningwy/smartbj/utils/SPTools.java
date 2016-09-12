package io.github.ningwy.smartbj.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具类
 * Created by ningwy on 2016/7/28.
 */
public class SPTools {

    /**
     *
     * @param context context
     * @param key 设置putBoolean()方法中的key
     * @param value 设置putBoolean()方法中的value
     */
    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     *
     * @param context context
     * @param key getBoolean()方法中的key
     * @param defValue getBoolean()方法中的默认值
     * @return getBoolean()方法的值
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    /**
     *
     * @param context context
     * @param key putString()方法中的key
     * @param value putString()方法中的value
     */
    public static void setString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    /**
     *
     * @param context context
     * @param key getString()方法中的key
     * @param defValue getString()方法中的默认值
     * @return getString()方法的值
     */
    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIGFILE, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

}

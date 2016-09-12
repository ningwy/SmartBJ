package io.github.ningwy.smartbj.utils;

/**
 * 常数
 * Created by ningwy on 2016/7/28.
 */
public interface MyConstants {

    String CONFIGFILE = "cachevalue";//sp的文件名
    String ISSETUP = "issetup";//是否设置向导界面设置过数据

    String SERVER_URL = "http://172.20.51.114:8989/zhbj";//服务器URL
    String NEWS_URL = SERVER_URL + "/categories.json";//新闻中心的url
    String PHOTOS_URL = SERVER_URL + "/photos/photos_1.json";//组图url

    String READNEWSID = "readnewsid";//已读新闻的id

}

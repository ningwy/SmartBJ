package io.github.ningwy.smartbj.domain;

import java.util.List;

/**
 * 从服务器中获取到的新闻中心页面数据封装的类
 * Created by ningwy on 2016/8/7.
 */
public class NewsData {

    public int retcode;

    public List<NewsCenterData> data;

    public class NewsCenterData {
        public String id;
        public String title;
        public int type;
        public String url;
        public String url1;
        public String dayurl;
        public String excurl;
        public String weekurl;

        public List<Children> children;

        public class Children {
            public String id;
            public String title;
            public int type;
            public String url;
        }
    }

    public List<String> extend;

    @Override
    public String toString() {
        return "NewsData{" +
                "data=" + data +
                ", retcode=" + retcode +
                ", extend=" + extend +
                '}';
    }
}

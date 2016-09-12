package io.github.ningwy.smartbj.domain;

import java.util.List;

/**
 * tpi滚动条对应json数据的对应的类
 * Created by ningwy on 2016/8/14.
 */
public class TPINewsData {

    public int retcode;

    public TPINewsData_Data data;

    public class TPINewsData_Data {
        public String countcommenturl;
        public String more;
        public String title;
        public List<News> news;
        public List<Topic> topic;
        public List<TopNews> topnews;

        public class News {
            public String comment;
            public String commentlist;
            public String commenturl;
            public String id;
            public String listimage;
            public String pubdate;
            public String title;
            public String type;
            public String url;
        }

        public class Topic {
            public String description;
            public String listimage;
            public String id;
            public String sort;
            public String title;
            public String url;
        }

        public class TopNews {
            public String comment;
            public String commentlist;
            public String commenturl;
            public String id;
            public String pubdate;
            public String title;
            public String topimage;
            public String type;
            public String url;
        }
    }

}

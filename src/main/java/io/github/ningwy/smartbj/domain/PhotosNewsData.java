package io.github.ningwy.smartbj.domain;

import java.util.List;

/**
 * 组图新闻数据对象类
 * Created by ningwy on 2016/8/28.
 */
public class PhotosNewsData {

    public int retcode;
    public PhotosNewsData_Data data;

    public class PhotosNewsData_Data {
        public String countcommenturl;
        public String more;
        public String title;
        public List<News> news;

        public class News {
            public boolean comment;
            public String commentlist;
            public String commenturl;
            public int id;
            public String largeimage;
            public String listimage;
            public String pubdate;
            public String smallimage;
            public String title;
            public String type;
            public String url;
        }
    }
}

package io.github.ningwy.smartbj.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.ningwy.smartbj.ui.activity.MainActivity;

/**
 * 图片三级缓存工具类
 * Created by ningwy on 2016/8/28.
 */
public class BitmapCacheUtils {

    private MainActivity mainActivity;

    private int maxSize = (int) (Runtime.getRuntime().freeMemory() / 2);//申请的空间为可用内存的一半

    private LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(maxSize) {

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    //缓存目录
    private File cacheDir;

    //线程池
    private ExecutorService threadPool;

    //保留最后一次访问url的信息
    private Map<ImageView, String> ivs = new HashMap<>();

    public BitmapCacheUtils(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        cacheDir = mainActivity.getCacheDir();
        //初始化线程池为6个
        threadPool = Executors.newFixedThreadPool(6);
    }

    public void display(ImageView imageView, String ivUrl) {
        //1级：从内存中读取
        Bitmap bitmap = lruCache.get(ivUrl);
        if (bitmap != null) {
            Log.e("TAG", "从内存中获取");
            //不为空
            imageView.setImageBitmap(bitmap);
            return;//返回不执行剩下的代码
        }

        //2级：从硬盘中读取 目录/data/data/package/cache
        bitmap = getCacheFile(ivUrl);
        if (bitmap != null) {
            Log.e("TAG", "从缓存中获取");
            imageView.setImageBitmap(bitmap);
            return;
        }

        //3级：从网络中读取
        ivs.put(imageView, ivUrl);//保留最新的url，以免造成图片产生错位
        getDataFromNet(imageView, ivUrl);
    }

    /**
     * 从硬盘中获取bitmap
     *
     * @param ivUrl 把url当做名字
     * @return
     */
    private Bitmap getCacheFile(String ivUrl) {
        //把ivUrl转换成md5值，再把md5值当做文件名
        File file = new File(cacheDir, MD5Utils.md5(ivUrl));
        if (file != null && file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            //再往内存中写
            lruCache.put(ivUrl, bitmap);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * 从网络中获取
     *
     * @param imageView 要填充的图片
     * @param ivUrl     图片url
     */
    private void getDataFromNet(ImageView imageView, String ivUrl) {
        //由线程池执行下载任务
        threadPool.submit(new DownLoad(imageView, ivUrl));
    }

    private class DownLoad implements Runnable {

        private ImageView imageView;
        private String ivUrl;

        public DownLoad(ImageView imageView, String ivUrl) {
            this.imageView = imageView;
            this.ivUrl = ivUrl;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(ivUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setConnectTimeout(6000);
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    //保存到内存中去
                    lruCache.put(ivUrl, bitmap);
                    //保存到硬盘中去
                    saveBitmap(bitmap, ivUrl);

                    //主线程中更新界面
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //如果当前url和保存的最新的url一致，则更新界面
                            if (ivUrl.equals(ivs.get(imageView))) {
                                Log.e("TAG", "从网络中获取");
                                //更新界面
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    });
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存从网络上下载的图片到缓存目录中
     */
    private void saveBitmap(Bitmap bitmap, String ivUrl) {
        File file = new File(cacheDir, MD5Utils.md5(ivUrl));
        try {
            //压缩成最高品质
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}

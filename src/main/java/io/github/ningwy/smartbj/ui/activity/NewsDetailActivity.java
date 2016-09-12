package io.github.ningwy.smartbj.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.ClientCertRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import io.github.ningwy.smartbj.R;

public class NewsDetailActivity extends Activity {

    @ViewInject(value = R.id.ib_news_detail_back)
    private ImageButton ib_news_detail_back;

    @ViewInject(value = R.id.ib_news_detail_textsize)
    private ImageButton ib_news_detail_textsize;

    @ViewInject(value = R.id.ib_news_detail_share)
    private ImageButton ib_news_detail_share;

    @ViewInject(value = R.id.wv_news_detail)
    private WebView wv_news_detail;

    @ViewInject(value = R.id.pb_news_detail_loading)
    private ProgressBar pb_news_detail_loading;

    private WebSettings webSettings;

    //新闻标题
    private String newsTitle;
    //新闻url
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news_detail);
        initView();
        initData();
        initEvent();
    }

    /**
     *
     */
    private void initEvent() {

        //点击监听事件
        View.OnClickListener clickListener = new View.OnClickListener() {
            int checkTextSize = 2;

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ib_news_detail_back://返回
                        finish();//关闭当前Activity
                        break;

                    case R.id.ib_news_detail_textsize://改变字体
                        showChangeTextSizeDialog();
                        break;

                    case R.id.ib_news_detail_share://分享
                        showShare();
                        break;
                }
            }

            /**
             * 弹出改变字体大小的对话框
             */
            private void showChangeTextSizeDialog() {
                String[] textSizes = new String[]{"超大号", "大号", "正常", "小号", "超小号"};
                new AlertDialog.Builder(NewsDetailActivity.this)
                        .setTitle("改变文字大小")
                        .setSingleChoiceItems(textSizes, checkTextSize, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkTextSize = which;
                                changeTextSize();
                                dialog.dismiss();
                            }

                        })
                        .create()
                        .show();
            }

            /**
             * 改变字体大小
             */
            private void changeTextSize() {
                switch (checkTextSize) {
                    case 0://超大号
                        webSettings.setTextSize(WebSettings.TextSize.LARGEST);
                        break;
                    case 1://大号
                        webSettings.setTextSize(WebSettings.TextSize.LARGER);
                        break;
                    case 2://正常
                        webSettings.setTextSize(WebSettings.TextSize.NORMAL);
                        break;
                    case 3://小号
                        webSettings.setTextSize(WebSettings.TextSize.SMALLER);
                        break;
                    case 4://超小号
                        webSettings.setTextSize(WebSettings.TextSize.SMALLEST);
                        break;
                }
            }
        };
        ib_news_detail_back.setOnClickListener(clickListener);
        ib_news_detail_textsize.setOnClickListener(clickListener);
        ib_news_detail_share.setOnClickListener(clickListener);

        //WebView监听事件
        wv_news_detail.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                //加载页面完成时隐藏progressBar
                pb_news_detail_loading.setVisibility(View.INVISIBLE);
                super.onPageFinished(view, url);
            }
        });
    }

    /**
     *启动分享
     */
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle(newsTitle);
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl(url);
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);

        // 启动分享GUI
        oks.show(this);
    }

    /**
     *
     */
    private void initData() {
        Intent intent = getIntent();
        //url
        url = intent.getStringExtra("newsDetailUrl");
        //标题
        newsTitle = intent.getStringExtra("newsTitle");
        //webview加载数据
        wv_news_detail.loadUrl(url);
    }

    /**
     *
     */
    private void initView() {
        x.view().inject(this);
        webSettings = wv_news_detail.getSettings();
        //允许调用JavaScript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置允许放大缩小
        webSettings.setBuiltInZoomControls(true);
        //设置双击放大或者缩小
        webSettings.setUseWideViewPort(true);

        //初始化分享组件ShareSDK
        ShareSDK.initSDK(this);
    }
}

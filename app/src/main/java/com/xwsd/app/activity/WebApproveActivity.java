package com.xwsd.app.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.view.NavbarManage;

import butterknife.Bind;

/**
 * Created by Gx on 2016/8/25.
 * Web认证页面
 */
public class WebApproveActivity extends BaseActivity {

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.load_progress_bar)
    ProgressBar load_progress_bar;

    @Bind(R.id.web_view)
    WebView web_view;

    String title;

    String url;


    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_web_details);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //设置导航栏

        navbarManage.showLeft(true);
        navbarManage.showRight(false);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });

//        setWebView();
        initProgressBar();
//        得到标题
        title = getIntent().getStringExtra(UserParam.TITLE);
//        得到需要加载的链接
        url = getIntent().getStringExtra(UserParam.URL);
        navbarManage.setCentreStr(title);
        web_view.loadUrl(url);
        setWebView();
    }

    @Override
    public void onBackPressed() {
        if(web_view.canGoBack()){
            web_view.goBack();
        }else{
            super.onBackPressed();
        }
    }

    /**
     * 设置WebView
     */
    private void setWebView() {
        //支持JS脚本
        web_view.getSettings().setJavaScriptEnabled(true);
        //支持缩放
        web_view.getSettings().setSupportZoom(true);
        web_view.getSettings().setBuiltInZoomControls(true);
        web_view.getSettings().setSavePassword(false);
        web_view.getSettings().setUseWideViewPort(true);
        web_view.setInitialScale(25);

        //屏蔽跳转其他浏览器
        web_view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

    }

    /**
     * 初始化顶部进度条
     */
    private void initProgressBar() {
        web_view.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (load_progress_bar.getVisibility() == View.GONE) {
                    load_progress_bar.setVisibility(View.VISIBLE);
                }
                if (newProgress == 100) {
                    load_progress_bar.setVisibility(View.GONE);
                } else {
                    if (View.INVISIBLE == load_progress_bar.getVisibility()) {
                        load_progress_bar.setVisibility(View.VISIBLE);
                    }
                    load_progress_bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }
}

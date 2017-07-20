package com.xwsd.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.Bind;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebView;
import com.xwsd.app.AppManager;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.NavbarManage;

/**
 * Created by Gx on 2016/8/25.
 * H5详情页
 */
public class WebDetailsActivity extends BaseActivity {

    public static final int TYPE_NATIVE = 0;

    public static final int TYPE_NETWORK = 1;

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.webview_wechat)
    com.tencent.smtt.sdk.WebView webView;



    String url;

    private int type;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_web_details);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {


        //设置导航栏

        navbarManage.showLeft(true);
        navbarManage.showRight(true);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setRightStr("关闭");
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setOnRightClickListener(new NavbarManage.OnRightClickListener() {
            @Override
            public void onRightClick() {
                AppManager.getAppManager().finishActivity();
            }
        });
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });


        initProgressBar();
//        得到标题
        title = getIntent().getStringExtra(UserParam.TITLE);
//        得到需要加载的链接
        url = getIntent().getStringExtra(UserParam.URL);
        type = getIntent().getIntExtra(UserParam.TYPE, TYPE_NETWORK);
        navbarManage.setCentreStr(title);
        setWebView(url);
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }

    /**
     * 设置WebView
     */
    private void setWebView(String url) {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webView = (com.tencent.smtt.sdk.WebView) findViewById(R.id.webview_wechat);
        com.tencent.smtt.sdk.WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //支持缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webView.setInitialScale(25);
        webView.loadUrl(url);
        webView.setWebViewClient(new com.tencent.smtt.sdk.WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(com.tencent.smtt.sdk.WebView var1, int var2, String var3, String var4) {
                progressBar.setVisibility(View.GONE);
                ToastUtil.show("网页加载失败");

            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
           //     super.onReceivedSslError(webView, sslErrorHandler, sslError);
                sslErrorHandler.proceed();
            }
        });
    }

    /**
     * 初始化顶部进度条
     */
    private void initProgressBar() {

        //进度条
        webView.setWebChromeClient(new com.tencent.smtt.sdk.WebChromeClient() {
            @Override
            public void onProgressChanged(com.tencent.smtt.sdk.WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
        });
    }
}

package com.xwsd.app.activity;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.tencent.smtt.sdk.WebSettings;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
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

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.webview_wechat)
    com.tencent.smtt.sdk.WebView webView;

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
/*        url="https://gateway.yemadai.com/openAccount_mobile";*/
       /* url="https://login.taobao.com" ;*/
/*        String postDate = "txtName=zzz&QueryTypeLst=1&CertificateTxt=dsds";
        //由于webView.postUrl(url, postData)中 postData类型为byte[] ，
        //通过EncodingUtils.getBytes(data, charset)方法进行转换
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            web_view.postUrl(url, EncodingUtils.getBytes(postDate, "BASE64"));
        }*/


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

        TLog.error(url);
        com.tencent.smtt.sdk.WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //支持缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webView.setInitialScale(25);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);

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

package com.xwsd.app.activity;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.TLog;
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
/*        url="https://gateway.yemadai.com/openAccount_mobile";*/
       /* url="https://login.taobao.com" ;*/
/*        String postDate = "txtName=zzz&QueryTypeLst=1&CertificateTxt=dsds";
        //由于webView.postUrl(url, postData)中 postData类型为byte[] ，
        //通过EncodingUtils.getBytes(data, charset)方法进行转换
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            web_view.postUrl(url, EncodingUtils.getBytes(postDate, "BASE64"));
        }*/

        web_view.loadUrl(url);

        web_view.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                view.loadUrl(url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                TLog.error("加载网页:"+url);
                view.loadUrl(url);
                return true;
            }

            //shouldOverrideUrlLoading(WebView view, String url)
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 开始加载网页时处理 如：显示"加载提示" 的加载对话框

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 网页加载完成时处理  如：让 加载对话框 消失

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // 加载网页失败时处理 如：提示失败，或显示新的界面
               TLog.error("加载网页失败");
            }
        });
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
        web_view.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //handler.cancel(); 默认的处理方式，WebView变成空白页
                TLog.error("handler.proceed();//接受证书");
                handler.proceed();//接受证书
                //handleMessage(Message msg); 其他处理
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            web_view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); }
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
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

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

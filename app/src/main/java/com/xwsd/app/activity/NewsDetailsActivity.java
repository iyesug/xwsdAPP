package com.xwsd.app.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.NewsBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by Gx on 2016/9/25.
 * 讯息详情
 */
public class NewsDetailsActivity extends BaseActivity {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;


//    @Bind(R.id.load_progress_bar)
//    ProgressBar load_progress_bar;

    @Bind(R.id.web_view)
    WebView web_view;

    @Bind(R.id.error_layout)
    EmptyLayout mErrorLayout;

//    @Bind(R.id.tv_title)
//    TextView tv_title;
//
//    @Bind(R.id.tv_name)
//    TextView tv_name;
//
//    @Bind(R.id.tv_time)
//    TextView tv_time;

    String id;
    private NewsBean data;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_news_details);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.news_details);
        //设置导航栏
        navbarManage.showLeft(true);
        navbarManage.showRight(false);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setCentreStr(getString(R.string.news_details));
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });

//        initProgressBar();

        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                getData(id);
            }
        });

        //得到ID
        id = getIntent().getStringExtra(UserParam.DATA);
        getData(id);
    }

    /**
     * 得到详情数据
     */
    private void getData(String id) {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        ApiHttpClient.news(id,
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("获取资讯内容:" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("status") == 1) {
                                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                                data = GsonUtils.jsonToBean(response, NewsBean.class);
                                setData();

                            } else {
                                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        }
                    }
                }
        );
    }

    /**
     * 设置WebView
     */
    private void setWebView() {
        //支持JS脚本
        web_view.getSettings().setJavaScriptEnabled(true);
        //支持缩放
        web_view.getSettings().setSupportZoom(true);
        web_view.getSettings().setBuiltInZoomControls(false);
        web_view.getSettings().setSavePassword(false);
        web_view.getSettings().setUseWideViewPort(true);
        web_view.setHorizontalScrollBarEnabled(false);//水平不显示
        web_view.setVerticalScrollBarEnabled(false); //垂直不显示
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
     * 设置数据
     */
    public void setData() {
//        tv_title.setText(data.data.title);
//        tv_time.setText("时间：" + data.data.time.split(" ")[0]);
//        TLog.error("详情：" + data.data.content);
//        web_view.loadDataWithBaseURL(null, "<html><body>" + Html.fromHtml(data.data.content
//                        .replace("&lt;", "<")
//                        .replace("&gt;", ">")
//                        .replace("&nbsp;", " ")
//                        .replace("&quot;", "\"")).toString() + "</body></html>",
//                "text/html", "GBK", null);
        web_view.loadDataWithBaseURL(null,
                "<!DOCTYPE html> <html lang=\"zh-CN\"> <head> <meta charset=\"utf-8\"> <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no\"> " + "<title>新标预告</title> " +
                        "<style>#notice { padding:5px; margin:0;font-family: 'Helvetica Neue',Helvetica,sans-serif; font-size: 17px;line-height: 21px; color: #000; }#notice h3 {text-align: center; font-size: 20px; margin:5px 0; line-height: 1; font-weight: bold; }#notice .titleTime { line-height: 32px; font-size: 12px; text-align: center; border-bottom: 1px solid #eee; margin-bottom: 10px; color: #8f8f94; }#notice .body table { padding:3px; width:100%!important; border-spacing:0; border-collapse: collapse; }#notice .body td { border: 1px solid #eee; font-size: 12px; padding: 3px; } </style> </head> <body> <div id=\"notice\"> <h3>" +
                        "<!-- 公告标题替换 -->" + data.data.title + "<!--/ 公告标题替换 end -->" +
                        "</h3> <p class=\"titleTime\">作者：小微时贷 &nbsp; 时间：" +
                        "<!-- 公告时间替换 -->" + data.data.time.split(" ")[0] + "<!--. 公告时间替换 end -->" +
                        "</p> <div class=\"body\"> " +
                        "<!-- 公告内容替换 -->" + Html.fromHtml(data.data.content) + "<!--/ 公告内容替换 end -->" +
                        " </div> </div> </body> </html>", "text/html", "utf-8", null);
        setWebView();
    }

//    /**
//     * 初始化顶部进度条
//     */
//    private void initProgressBar() {
//        web_view.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                if (load_progress_bar.getVisibility() == View.GONE) {
//                    load_progress_bar.setVisibility(View.VISIBLE);
//                }
//                if (newProgress == 100) {
//                    load_progress_bar.setVisibility(View.GONE);
//                } else {
//                    if (View.INVISIBLE == load_progress_bar.getVisibility()) {
//                        load_progress_bar.setVisibility(View.VISIBLE);
//                    }
//                    load_progress_bar.setProgress(newProgress);
//                }
//                super.onProgressChanged(view, newProgress);
//            }
//        });
//    }
}
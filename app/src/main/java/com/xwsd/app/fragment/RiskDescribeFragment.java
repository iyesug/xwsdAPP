package com.xwsd.app.fragment;

import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.xwsd.app.R;
import com.xwsd.app.base.BaseFragment;

import butterknife.Bind;

/**
 * Created by Gx on 2016/10/13.
 * 风控描述
 */
public class RiskDescribeFragment extends BaseFragment {

    @Bind(R.id.web_view)
    WebView web_view;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_risk_describe, null);
        return view;
    }

    @Override
    protected void init() {
        setWebView();
        if (RiskControlFragment.oddrmBean != null &&
                RiskControlFragment.oddrmBean.data != null &&
                !TextUtils.isEmpty(RiskControlFragment.oddrmBean.data.oddLoanControl)) {
            web_view.loadDataWithBaseURL(null, Html.fromHtml(RiskControlFragment.oddrmBean.data.oddLoanControl).toString(), "text/html", "utf-8", null);
        }
    }

    /**
     * 设置WebView
     */
    private void setWebView() {
        //支持JS脚本
        web_view.getSettings().setJavaScriptEnabled(true);
        //不支持缩放
        web_view.getSettings().setSupportZoom(false);
        web_view.getSettings().setSavePassword(false);
        //自适应屏幕
        WebSettings settings = web_view.getSettings();
        int screenDensity = getResources().getDisplayMetrics().densityDpi;
        WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM;
        switch (screenDensity) {
            case DisplayMetrics.DENSITY_LOW:
                zoomDensity = WebSettings.ZoomDensity.CLOSE;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                zoomDensity = WebSettings.ZoomDensity.MEDIUM;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                zoomDensity = WebSettings.ZoomDensity.FAR;
                break;
        }
        settings.setDefaultZoom(zoomDensity);
    }
}

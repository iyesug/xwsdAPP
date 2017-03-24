package com.xwsd.app.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.activity.NewsDetailsActivity;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BasePullUpListFragment;
import com.xwsd.app.base.BaseUpDownListFragment;
import com.xwsd.app.bean.InfosBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.EmptyLayout;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Gx on 2016/8/22.
 * 公告讯息列表
 */
public class NoticeListFragment extends BaseUpDownListFragment {

    private String type;

    @Override
    protected View setContentView(LayoutInflater inflater) {

        type = getArguments().getString(UserParam.DATA);
        return inflater.inflate(R.layout.fragment_refresh_lists, null);
    }

    /**
     * 上拉加载
     */
    @Override
    public void pullUpCallBack() {
        ApiHttpClient.infos(type, null, null, null, currentPages, each_page_num, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                setPullUpState(BasePullUpListFragment.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("公告讯息列表：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        setPullUpState(BasePullUpListFragment.SUCCEED);
                        InfosBean infosBean = GsonUtils.jsonToBean(response, InfosBean.class);
                        setData(infosBean,TYPE_PULLUP);
                    } else {
                        setPullUpState(BasePullUpListFragment.LOAD_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    setPullUpState(BasePullUpListFragment.LOAD_ERROR);
                }
            }
        });
    }

    /**
     * 下拉刷新
     */
    @Override
    public void pullDownCallBack() {
        currentPages = 1;
        ApiHttpClient.infos(type, null, null, null, currentPages, each_page_num, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                AppContext.showToastShort(R.string.refurbish_failure);
                swipe_refresh_layout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("公告讯息列表：" + response);
                swipe_refresh_layout.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        InfosBean infosBean = GsonUtils.jsonToBean(response, InfosBean.class);
                        currentPages = infosBean.data.page;
                        allItemCount = infosBean.data.count;
                        mAdapter.replaceAll(infosBean.data.records);
                    } else {
                        AppContext.showToastShort(R.string.refurbish_failure);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppContext.showToastShort(R.string.refurbish_failure);
                }
            }
        });
    }

    @Override
    public void firstRequestData() {
        super.firstRequestData();
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        ApiHttpClient.infos(type, null, null, null, currentPages, each_page_num, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("公告讯息列表：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        InfosBean infosBean = GsonUtils.jsonToBean(response, InfosBean.class);
                        setData(infosBean,TYPE_FIRST);
                    } else {
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            }
        });

    }

    /**
     * 设置列表信息
     *
     * @param bean
     */
    private void setData(InfosBean bean, int type) {
        currentPages = bean.data.page;
        allItemCount = bean.data.count;

        if (bean.data.records == null || bean.data.records.size() <= 0) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }


        if (mAdapter == null) {
            mAdapter = new QuickAdapter<InfosBean.Data.Records>(getActivity(), R.layout.item_new_bid, bean.data.records) {
                @Override
                protected void convert(BaseAdapterHelper helper, InfosBean.Data.Records item) {
                    helper.setText(R.id.tv_title, item.title);
                    helper.setText(R.id.tv_time, item.time);
                    helper.setText(R.id.tv_look, item.click);
//                    helper.setText(R.id.tv_content, Html.fromHtml(item.content
//                            .replace("&lt;", "<")
//                            .replace("&gt;", ">")
//                            .replace("&quot;", "\"")));
                }
            };

            list_view.setAdapter(mAdapter);
            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position < mAdapter.getCount()) {
                        Intent intent = new Intent(getActivity(), NewsDetailsActivity.class);
                        intent.putExtra(UserParam.DATA, ((InfosBean.Data.Records) mAdapter.getItem(position)).id);
                        startActivity(intent);
                    }
                }
            });
        } else {
            if (type == 0) {
                mAdapter.replaceAll(bean.data.records);
            } else {
                mAdapter.addAll(bean.data.records);
            }
        }
    }
}
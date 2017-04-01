package com.xwsd.app.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.xwsd.app.R;
import com.xwsd.app.activity.ProjectDetailsActivity;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BasePullUpListFragment;
import com.xwsd.app.base.BaseUpDownListFragment;
import com.xwsd.app.bean.Odds;
import com.xwsd.app.bean.OddsBean;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.DoubleScreenDialog;
import com.xwsd.app.view.EmptyLayout;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Gx on 2016/8/19.
 * 全部标的
 */
public class ALLBidFragment extends BaseUpDownListFragment {

    DecimalFormat decimalFormat = new DecimalFormat("0.0");

    public OddsBean oddsBean;

    public String period;

    public String type;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_refresh_lists, null);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    modifyRecylerData();
                    //循环
                    handler.sendEmptyMessageDelayed(1, 1000);
                    break;
            }
            return false;
        }
    });

    /**
     * 修改数据源
     */
    private void modifyRecylerData() {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            Odds temp = (Odds) mAdapter.getItem(i);
            if (temp.countdown >= 1) {
                temp.countdown = temp.countdown - 1;
                if (temp.countdown != 0) {
                }
                mAdapter.set(i, temp);
            } else {
                continue;
            }
        }
    }

    /**
     * 上拉加载
     */
    @Override
    public void pullUpCallBack() {
        ApiHttpClient.odds(currentPages, each_page_num, period, type, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                setPullUpState(BasePullUpListFragment.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("全部标列表：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        setPullUpState(BasePullUpListFragment.SUCCEED);
                        oddsBean = GsonUtils.jsonToBean(response, OddsBean.class);
                        parseData(jsonObject);
                        setData(oddsBean, TYPE_PULLUP);
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
     * 解析数据
     */
    private void parseData(JSONObject jsonObject) throws JSONException {
        //解析月份
        JSONArray periodsArr = jsonObject.getJSONObject("data").getJSONArray("periods");
        List<OddsBean.Periods> periods = new ArrayList<OddsBean.Periods>();
        OddsBean.Periods periodsAll = new OddsBean.Periods();
        periodsAll.content = "全部";
        periods.add(periodsAll);
        for (int i = 0; i < periodsArr.length(); i++) {
            OddsBean.Periods periodsItem = new OddsBean.Periods();
            JSONObject object = periodsArr.getJSONObject(i);
            Iterator iterator = object.keys();
            while (iterator.hasNext()) {
                periodsItem.id = String.valueOf(iterator.next());
                periodsItem.content = object.getString(periodsItem.id);
            }
            periods.add(periodsItem);
        }
        oddsBean.mperiods = periods;

        //解析类型
        JSONArray typesArr = jsonObject.getJSONObject("data").getJSONArray("types");
        List<OddsBean.Types> types = new ArrayList<OddsBean.Types>();
        OddsBean.Types typesAll = new OddsBean.Types();
        typesAll.content = "全部";
        types.add(typesAll);
        for (int i = 0; i < typesArr.length(); i++) {
            OddsBean.Types typesItem = new OddsBean.Types();
            JSONObject object = typesArr.getJSONObject(i);
            Iterator iterator = object.keys();
            while (iterator.hasNext()) {
                typesItem.id = String.valueOf(iterator.next());
                typesItem.content = object.getString(typesItem.id);
            }
            types.add(typesItem);
        }
        oddsBean.mtypes = types;
    }


    /**
     * 下拉刷新
     */
    @Override
    public void pullDownCallBack() {
        currentPages = 1;
        ApiHttpClient.odds(currentPages, each_page_num, period, type, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToastShort(R.string.refurbish_failure);
                swipe_refresh_layout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("全部标列表：" + response);
                swipe_refresh_layout.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        oddsBean = GsonUtils.jsonToBean(response, OddsBean.class);
                        parseData(jsonObject);
                        currentPages = oddsBean.data.page;
                        allItemCount = oddsBean.data.count;
                        mAdapter.replaceAll(oddsBean.data.records);
                    } else {
                        ToastUtil.showToastShort(R.string.refurbish_failure);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showToastShort(R.string.refurbish_failure);
                }
            }
        });
    }

    /**
     * 得到筛选参数
     *
     * @param param
     */
    public void getScreenParam(int dataType, String param) {

        if (dataType == DoubleScreenDialog.TYPE_LEFT) {
            for (OddsBean.Periods periods : oddsBean.mperiods) {
                if (periods.content.equals(param)) {
                    period = periods.id;
                }
            }
        } else {
            for (OddsBean.Types types : oddsBean.mtypes) {
                if (types.content.equals(param)) {
                    type = types.id;
                }
            }
        }

        firstRequestData();
    }

    @Override
    public void firstRequestData() {
        super.firstRequestData();
        currentPages = 1;
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        ApiHttpClient.odds(currentPages, each_page_num, period, type, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("全部标列表：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        oddsBean = GsonUtils.jsonToBean(response, OddsBean.class);
                        parseData(jsonObject);
                        setData(oddsBean, TYPE_FIRST);
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
    private void setData(OddsBean bean, int type) {
        currentPages = bean.data.page;
        allItemCount = bean.data.count;

        if (bean.data.records == null || bean.data.records.size() <= 0) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }

        if (mAdapter == null) {
            mAdapter = new QuickAdapter<Odds>(getActivity(), R.layout.item_project, bean.data.records) {
                @Override
                protected void convert(BaseAdapterHelper helper, Odds item) {
                    helper.setText(R.id.tv_oddTitle, item.oddTitle);
                    helper.setText(R.id.tv_oddMoney, item.oddMoney);
                    helper.setText(R.id.tv_oddReward, decimalFormat.format(item.oddYearRate * 100));
                    helper.setText(R.id.tv_oddPeriod, item.oddPeriod.replace("个月", ""));
                    if(item.oddStyle.equals("newhand")){
                        helper.setBackgroundRes(R.id.flag_icon,R.mipmap.xin);
                    }else {
                        if (item.investType.equals("0")) {
                            helper.setBackgroundRes(R.id.flag_icon, R.mipmap.zi);
                        } else if (item.investType.equals("1")) {
                            helper.setBackgroundRes(R.id.flag_icon, R.mipmap.shoudong);
                        }
                    }
                    DonutProgress dp_schedule = helper.getView(R.id.dp_schedule);
//                    progress.setProgress(item.schedule);
                    if (item.schedule > 0 && item.schedule < 100) {
                        dp_schedule.setLoadAnimation(item.schedule, 70);
                    } else {
                        dp_schedule.setProgress(item.schedule);
                    }

                    //判断是否是新手标
                    if (item.oddReward > 0) {
                        helper.setVisible(R.id.tv_rewards, true);
                        helper.setText(R.id.tv_rewards, "+" + decimalFormat.format(item.oddReward * 100) + "%");
                    } else {
                        helper.setVisible(R.id.tv_rewards, false);
                    }

                    switch (item.progress) {
                        case "start":
                            if (item.schedule == 100) {
                                dp_schedule.setText("复审中");
                            } else {
                                switch (item.second) {
                                    case -1://立即投标
                                        dp_schedule.setText(item.schedule + "%");
                                        break;
                                    case 0://显示开始时间 2016-10-20 14:00:00
                                        dp_schedule.setText(item.openTime.substring(11, 16) + "&开始");
                                        break;
                                    default://开始倒计时
                                        if (item.countdown == -1) {
                                            item.countdown = item.second;
                                        }
                                        StringBuffer timeStr = new StringBuffer();
                                        int second = item.countdown % 60;
                                        int minute = (item.countdown - second) / 60;
/*                                        TLog.error("minute:" + minute);
                                        TLog.error("second:" + second);*/
                                        if (minute > 0) {
                                            if (minute < 10) {
                                                timeStr.append("0");
                                            }
                                            timeStr.append(minute);
                                        } else {
                                            timeStr.append("00");
                                        }

                                        timeStr.append(":");

                                        if (second > 0) {
                                            if (second < 10) {
                                                timeStr.append("0");
                                            }
                                            timeStr.append(second);
                                        } else {
                                            timeStr.append("00");
                                        }
                                        dp_schedule.setText(timeStr.toString());
                                        break;
                                }
                            }
                            break;
                        case "run":
                            dp_schedule.setText("还款中");
                            break;
                        default:
                            dp_schedule.setText("已结束");
                            break;
                    }
                }
            };

            list_view.setAdapter(mAdapter);
            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position < mAdapter.getCount()) {
                        Intent intent = new Intent(getActivity(), ProjectDetailsActivity.class);
                        intent.putExtra("oddNumber", ((Odds) mAdapter.getItem(position)).oddNumber);
                        intent.putExtra("type", "all");
                        startActivity(intent);
                    }
                    //埋点开始
                    BuriedPointUtil.buriedPoint("全部的标详情");
                }
            });
            //启动倒计时
            handler.sendEmptyMessageDelayed(1, 1000);
        } else {
            if (type == 0) {
                mAdapter.replaceAll(bean.data.records);
            } else {
                mAdapter.addAll(bean.data.records);
            }
        }
    }
}

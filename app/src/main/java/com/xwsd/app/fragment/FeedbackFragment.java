package com.xwsd.app.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.activity.CEOQuestionsActivity;
import com.xwsd.app.activity.UserActivity;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.base.BasePullUpListFragment;
import com.xwsd.app.base.BaseUpDownListFragment;
import com.xwsd.app.bean.InfosBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.SpinnerDialog;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Gx on 2016/8/22.
 * 问题反馈
 */
public class FeedbackFragment extends BaseUpDownListFragment implements View.OnClickListener {

    /**
     * 主题筛选
     */
    @Bind(R.id.theme_screen)
    TextView theme_screen;

    /**
     * 时间筛选
     */
    @Bind(R.id.time_screen)
    TextView time_screen;

    @Bind(R.id.iv_theme_screen_arrows)
    ImageView iv_theme_screen_arrows;

    @Bind(R.id.iv_time_screen_arrows)
    ImageView iv_time_screen_arrows;

    @Bind(R.id.iv_theme_screen_bottom_arrows)
    ImageView iv_theme_screen_bottom_arrows;

    @Bind(R.id.ll_theme_screen)
    LinearLayout ll_theme_screen;

    @Bind(R.id.ll_time_screen)
    LinearLayout ll_time_screen;

    @Bind(R.id.ll_theme_screen_bottom)
    LinearLayout ll_theme_screen_bottom;

    /**
     * 底部主题选择
     */
    @Bind(R.id.theme_screen_bottom)
    TextView theme_screen_bottom;

    @Bind(R.id.et_question)
    EditText et_question;

    RequestCall call;

    /**
     * 主题筛选
     */
    private SpinnerDialog themeScreenDialog;

    /**
     * 时间筛选
     */
    private SpinnerDialog timeScreenDialog;

    private SpinnerDialog bottomThemeScreenDialog;

    String theme;

    private String title;

    private String startTime;

    private String endTime;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

    /**
     * 一天的时间戳
     */
    private long dayTime = 86400000;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_feedback, null);
    }

    /**
     * 上拉加载
     */
    @Override
    public void pullUpCallBack() {
        ApiHttpClient.infos(ApiHttpClient.TYPE_INFOS_QUESTION, title, startTime, endTime, currentPages, each_page_num, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                setPullUpState(BasePullUpListFragment.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("问答反馈：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        setPullUpState(BasePullUpListFragment.SUCCEED);
                        InfosBean infosBean = GsonUtils.jsonToBean(response, InfosBean.class);
                        setData(infosBean, TYPE_PULLUP);
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
        ApiHttpClient.infos(ApiHttpClient.TYPE_INFOS_QUESTION, title, startTime, endTime, currentPages, each_page_num, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                AppContext.showToastShort(R.string.refurbish_failure);
                swipe_refresh_layout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("问答反馈：" + response);
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
        if (call != null) {
            call.cancel();
        }
        currentPages = 1;
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        call = ApiHttpClient.infos(ApiHttpClient.TYPE_INFOS_QUESTION, title, startTime, endTime, currentPages, each_page_num, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("问答反馈：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        InfosBean infosBean = GsonUtils.jsonToBean(response, InfosBean.class);
                        setData(infosBean, TYPE_FIRST);
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
            return;
        }

        if (mAdapter == null) {
            mAdapter = new QuickAdapter<InfosBean.Data.Records>(getActivity(), R.layout.item_feedback, bean.data.records) {
                @Override
                protected void convert(BaseAdapterHelper helper, InfosBean.Data.Records item) {
                    helper.setText(R.id.tv_theme, item.title);
                    helper.setText(R.id.tv_look, item.click);
                    helper.setText(R.id.tv_reply, item.answer);
                    helper.setText(R.id.tv_time, item.time);
                    helper.setText(R.id.tv_title, item.content);
                }
            };

            list_view.setAdapter(mAdapter);
            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position < mAdapter.getCount()) {
                        Intent intent = new Intent(getActivity(), CEOQuestionsActivity.class);
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

    /**
     * 设置箭头的类型
     *
     * @param tv
     * @param iv
     * @param type 0：初始状态；1：选中状态；2：下拉状态
     */
    private void setArrowsType(TextView tv, ImageView iv, int type) {

        switch (type) {
            case 0:
                tv.setTextColor(getResources().getColor(R.color.gray_text));
                iv.setImageResource(R.mipmap.ic_arrows_u_g);
                break;
            case 1:
                tv.setTextColor(getResources().getColor(R.color.blue_simple));
                iv.setImageResource(R.mipmap.ic_arrows_u_b);
                break;
            case 2:
                tv.setTextColor(getResources().getColor(R.color.blue_simple));
                iv.setImageResource(R.mipmap.ic_arrows_d_b);
                break;
        }
    }

    @OnClick({R.id.commit, R.id.ll_theme_screen, R.id.ll_time_screen, R.id.ll_theme_screen_bottom})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_theme_screen://主题筛选

                BuriedPointUtil.buriedPoint("CEO问答主题");

                if (themeScreenDialog == null) {
                    themeScreenDialog = new SpinnerDialog(getActivity(), ll_theme_screen);
                    themeScreenDialog.setData(getResources().getStringArray(R.array.theme_frame_all));
                    themeScreenDialog.setOnItemClickListener(new SpinnerDialog.OnItemClickListener() {
                        @Override
                        public void onClick(int position, String data) {
                            if (data.equals("全部")) {
                                title = null;
                            } else {
                                title = data;
                            }
                            firstRequestData();
                        }
                    });

                    themeScreenDialog.setWindowDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            if (title ==null) {
                                setArrowsType(theme_screen, iv_theme_screen_arrows, 0);
                            } else {
                                setArrowsType(theme_screen, iv_theme_screen_arrows, 1);
                            }
                        }
                    });
                }

                setArrowsType(theme_screen, iv_theme_screen_arrows, 2);
                themeScreenDialog.show(ll_theme_screen.getWidth());

                break;
            case R.id.ll_time_screen://时间筛选

                BuriedPointUtil.buriedPoint("CEO问答时间");

                if (timeScreenDialog == null) {
                    timeScreenDialog = new SpinnerDialog(getActivity(), ll_time_screen);
                    timeScreenDialog.setData(getResources().getStringArray(R.array.time_frame));
                    timeScreenDialog.setOnItemClickListener(new SpinnerDialog.OnItemClickListener() {
                        @Override
                        public void onClick(int position, String data) {
                            int dayNum = 1;
                            switch (position) {
                                case 0:
                                    startTime = null;
                                    endTime = null;
                                    firstRequestData();
                                    return;
                                case 1:
                                    break;
                                case 2:
                                    dayNum = 3;
                                    break;
                                case 3:
                                    dayNum = 7;
                                    break;
                                case 4:
                                    dayNum = 30;
                                    break;
                            }

                            startTime = getDateToString(new Date().getTime() - dayTime * dayNum);
                            endTime = sdf.format(new Date());

                            firstRequestData();
                        }
                    });

                    timeScreenDialog.setWindowDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            if (startTime ==null) {
                                setArrowsType(time_screen, iv_time_screen_arrows, 0);
                            } else {
                                setArrowsType(time_screen, iv_time_screen_arrows, 1);
                            }
                        }
                    });
                }

                setArrowsType(time_screen, iv_time_screen_arrows, 2);
                timeScreenDialog.show(ll_time_screen.getWidth());

                break;
            case R.id.ll_theme_screen_bottom:
                if (bottomThemeScreenDialog == null) {
                    bottomThemeScreenDialog = new SpinnerDialog(getActivity(), ll_theme_screen_bottom);
                    bottomThemeScreenDialog.setData(getResources().getStringArray(R.array.theme_frame));
                    bottomThemeScreenDialog.setOnItemClickListener(new SpinnerDialog.OnItemClickListener() {
                        @Override
                        public void onClick(int position, String data) {
                            theme = data;
                            theme_screen_bottom.setText(data);
                        }
                    });

                    bottomThemeScreenDialog.setWindowDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            if (theme ==null) {
                                setArrowsType(theme_screen_bottom, iv_theme_screen_bottom_arrows, 0);
                            } else {
                                setArrowsType(theme_screen_bottom, iv_theme_screen_bottom_arrows, 1);
                            }
                        }
                    });
                }

                setArrowsType(theme_screen_bottom, iv_theme_screen_bottom_arrows, 2);
                bottomThemeScreenDialog.show(ll_theme_screen.getWidth(),false);

                break;
            case R.id.commit:
//                判断用户是否登录
                if (AppContext.getUserBean() == null) {
                    Intent intent = new Intent(getActivity(), UserActivity.class);
                    startActivity(intent);
                    return;
                }

                //判断提问内容是否输入完整
                if (TextUtils.isEmpty(et_question.getText().toString().trim())) {
                    AppContext.showToastShort(R.string.question_data_null);
                    return;
                }

                //判断主题是否选择
                if (TextUtils.isEmpty(theme)) {
                    AppContext.showToastShort(R.string.theme_data_null);
                    return;
                }

                ((BaseActivity) getActivity()).showWaitDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (call != null) {
                            call.cancel();
                        }
                    }
                });
                call = ApiHttpClient.ask(AppContext.getUserBean().data.userId,
                        theme,
                        et_question.getText().toString().trim(),
                        ApiHttpClient.TYPE_QUESTIONS_CEO,
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ((BaseActivity) getActivity()).hideWaitDialog();
                                AppContext.showToastShort(getString(R.string.network_exception));
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                TLog.error("提问:" + response);
                                ((BaseActivity) getActivity()).hideWaitDialog();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    AppContext.showToastShort(jsonObject.getString("msg"));
                                    if (jsonObject.getInt("status") == 1) {

                                    } else {

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    AppContext.showToastShort(getString(R.string.network_exception));
                                }
                            }
                        });
                break;
        }
    }

    /**
     * 时间戳转换成字符窜
     */
    private String getDateToString(long time) {
        return sdf.format(new Date(time));
    }
}

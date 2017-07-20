package com.xwsd.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.CEOQuestionsBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gx on 2016/9/25.
 * CEO问答
 */
public class CEOQuestionsActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    RequestCall call;

    String id;

    @Bind(R.id.error_layout)
    EmptyLayout mErrorLayout;

//    @Bind(R.id.list_view)
//    ExpandableListView list_view;

    @Bind(R.id.list_view)
    ListView list_view;

    @Bind(R.id.et_reply)
    EditText et_reply;

    @Bind(R.id.ll_bottom)
    LinearLayout ll_bottom;

    protected ChatAdapter adapter;

    CEOQuestionsBean questionsBean;

    List<CEOQuestionsBean.Data.Answers.Replies> replies;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_ceo_questions);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.ceo_questions);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.ceo_questions));
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
        id = getIntent().getStringExtra(UserParam.DATA);
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        getData();
    }

    @OnClick({R.id.commit})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                //判断用户是否登录
                if (AppContext.getUserBean() == null) {
                    Intent intent = new Intent(CEOQuestionsActivity.this, UserActivity.class);
                    startActivity(intent);
                    return;
                }

                //判断用户是否登录
                if (TextUtils.isEmpty(et_reply.getText().toString().trim())) {
                    ToastUtil.showToastShort(R.string.reply_data_null);
                    return;
                }

                showWaitDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (call != null) {
                            call.cancel();
                        }
                    }
                });
                call = ApiHttpClient.reply(AppContext.getUserBean().data.userId,
                        questionsBean.data.id,
                        questionsBean.data.answers.get(1).id,
                        et_reply.getText().toString().trim(),
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                hideWaitDialog();
                                ToastUtil.showToastShort(getString(R.string.network_exception));
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                TLog.error("问答回复:" + response);
                                hideWaitDialog();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    ToastUtil.showToastShort(jsonObject.getString("msg"));
                                    if (jsonObject.getInt("status") == 1) {
                                        //刷新数据
                                        getData();
                                    } else {

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    ToastUtil.showToastShort(getString(R.string.network_exception));
                                }
                            }
                        });
                break;
        }
    }

    /**
     * 得到数据
     */
    public void getData() {
        if (id != null) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            ApiHttpClient.answers(id, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }

                @Override
                public void onResponse(String response, int id) {
                    TLog.error("CEO问答：" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt("status") == 1) {
                            questionsBean = GsonUtils.jsonToBean(response, CEOQuestionsBean.class);
                            setData(questionsBean);
                            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
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
    }

    /**
     * 设置数据
     *
     * @param questionsBean
     */
    private void setData(CEOQuestionsBean questionsBean) {

        if (questionsBean != null) {
//           解析数据
            replies = new ArrayList<>();
//            问题
            CEOQuestionsBean.Data.Answers.Replies question = new CEOQuestionsBean.Data.Answers.Replies();
            question.id = questionsBean.data.id;
            question.username = questionsBean.data.username;
            question.photo = questionsBean.data.photo;
            question.time = questionsBean.data.time;
            question.content = questionsBean.data.content;
            replies.add(question);

            if (questionsBean.data.answers != null && questionsBean.data.answers.size() >0) {
//            CEO回答
                CEOQuestionsBean.Data.Answers.Replies reply = new CEOQuestionsBean.Data.Answers.Replies();
                reply.id = questionsBean.data.answers.get(0).id;
                reply.username = questionsBean.data.answers.get(0).username;
                reply.photo = questionsBean.data.answers.get(0).photo;
                reply.time = questionsBean.data.answers.get(0).time;
                reply.content = questionsBean.data.answers.get(0).content;
                replies.add(reply);

//            后续问答
                replies.addAll(questionsBean.data.answers.get(0).replies);
            }

            if (adapter == null) {
                adapter = new ChatAdapter();
                list_view.setAdapter(adapter);
                setBottom();

//                adapter = new ChatAdapter();
//                //去箭头
//                list_view.setGroupIndicator(null);
//                list_view.setAdapter(adapter);
//
//                //默认展开
//                int groupCount = list_view.getCount();
//
//                for (int i = 0; i < groupCount; i++) {
//                    list_view.expandGroup(i);
//                }
//
//                //屏蔽组点击收缩
//                list_view.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//                    @Override
//                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                        return true;
//                    }
//                });
//
//                setHeader();
//                setBottom();

            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 设置底部
     * 判断问题是否是该用户提的，如果是就能回复，如果否就隐藏
     */
    private void setBottom() {
        if (AppContext.getUserBean() == null ||
                !questionsBean.data.username.equals(AppContext.getUserBean().data.userName) ||
                questionsBean.data.answers == null ||
                questionsBean.data.answers.size() <= 0) {
            ll_bottom.setVisibility(View.GONE);
        } else {
            ll_bottom.setVisibility(View.VISIBLE);
        }
    }

//    /**
//     * 设置头部
//     */
//    private void setHeader() {
//        View view = LayoutInflater.from(CEOQuestionsActivity.this).inflate(R.layout.item_questions_my, null);
//        ImageView iv_portrait = (ImageView) view.findViewById(R.id.iv_portrait);
//        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
//        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
//        TextView tv_tiem = (TextView) view.findViewById(R.id.tv_tiem);
//
//        navbarManage.setCentreStr(questionsBean.data.title);
//        tv_name.setText(questionsBean.data.username);
//        tv_content.setText(questionsBean.data.content);
//        if (questionsBean.data.answers == null ||
//                questionsBean.data.answers.size() <= 0) {
//            tv_tiem.setText(questionsBean.data.time);
//            tv_tiem.setVisibility(View.VISIBLE);
//        }
//
//        ApiHttpClient.lodCircleImg(iv_portrait, questionsBean.data.photo, R.mipmap.ic_launcher, R.mipmap.ic_launcher_overturn);
//
//        list_view.addHeaderView(view);
//    }

    /**
     * 聊天列表的适配器
     */
    private class ChatAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return replies == null ? 0 : replies.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            //TODO 非最优做法，有时间可将左右气泡条目合成一个布局
            //            if (convertView == null) {
            holder = new ViewHolder();
            if (replies.get(position).username.equals("CEO")) {
                convertView = LayoutInflater.from(CEOQuestionsActivity.this).inflate(R.layout.item_questions_other, null);
            } else {
                convertView = LayoutInflater.from(CEOQuestionsActivity.this).inflate(R.layout.item_questions_my, null);
            }
            holder.iv_portrait = (ImageView) convertView.findViewById(R.id.iv_portrait);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_tiem = (TextView) convertView.findViewById(R.id.tv_tiem);
            convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }

            holder.tv_name.setText(replies.get(position).username);
            holder.tv_content.setText(replies.get(position).content);

            if (replies.size() - 1 == position) {
                holder.tv_tiem.setVisibility(View.VISIBLE);
                holder.tv_tiem.setText(replies.get(position).time);
            }

            ApiHttpClient.lodCircleImg(holder.iv_portrait,
                    replies.get(position).photo,
                    R.drawable.ic_load, R.drawable.ic_load);

            holder.tv_tiem.setText(replies.get(position).time);

            return convertView;
        }

        class ViewHolder {
            public ImageView iv_portrait;
            public TextView tv_name;
            public TextView tv_content;
            public TextView tv_tiem;
        }
    }


//    /**
//     * 聊天列表的适配器
//     */
//    private class ChatAdapter extends BaseExpandableListAdapter {
//        @Override
//        public int getGroupCount() {
//            return questionsBean.data.answers == null ? 0 : questionsBean.data.answers.size();
//        }
//
//        @Override
//        public int getChildrenCount(int groupPosition) {
//            return questionsBean.data.answers.get(groupPosition).replies == null ? 0 : questionsBean.data.answers.get(groupPosition).replies.size();
//        }
//
//        @Override
//        public Object getGroup(int groupPosition) {
//            return questionsBean.data.answers.get(groupPosition);
//        }
//
//        @Override
//        public Object getChild(int groupPosition, int childPosition) {
//            return replies.get(position);
//        }
//
//        @Override
//        public long getGroupId(int groupPosition) {
//            return groupPosition;
//        }
//
//        @Override
//        public long getChildId(int groupPosition, int childPosition) {
//            return childPosition;
//        }
//
//        @Override
//        public boolean hasStableIds() {
//            return false;
//        }
//
//        @Override
//        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//            ViewHolder holder;
//            if (convertView == null) {
//                holder = new ViewHolder();
//                convertView = LayoutInflater.from(CEOQuestionsActivity.this).inflate(R.layout.item_questions_other, null);
//                holder.iv_portrait = (ImageView) convertView.findViewById(R.id.iv_portrait);
//                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//                holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
//                holder.tv_tiem = (TextView) convertView.findViewById(R.id.tv_tiem);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
////            holder.iv_portrait.setImageBitmap();
//            holder.tv_name.setText(questionsBean.data.answers.get(groupPosition).username);
//            holder.tv_content.setText(questionsBean.data.answers.get(groupPosition).content);
//            //如果没有人继续提问就显示CEO回答的时间
//            if (questionsBean.data.answers.get(groupPosition).replies == null ||
//                    questionsBean.data.answers.get(groupPosition).replies.size() <= 0) {
//                holder.tv_tiem.setVisibility(View.VISIBLE);
//                holder.tv_tiem.setText(questionsBean.data.answers.get(groupPosition).time);
//            }
//
//            ApiHttpClient.lodCircleImg(holder.iv_portrait,
//                    questionsBean.data.answers.get(groupPosition).photo,
//                    R.mipmap.ic_launcher, R.mipmap.ic_launcher_overturn);
//
//            return convertView;
//        }
//
//        @Override
//        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//            ViewHolder holder;
//
//
////            if (convertView == null) {
//            holder = new ViewHolder();
//            if (replies.get(position).username.equals("CEO")) {
//                convertView = LayoutInflater.from(CEOQuestionsActivity.this).inflate(R.layout.item_questions_other, null);
//            } else {
//                convertView = LayoutInflater.from(CEOQuestionsActivity.this).inflate(R.layout.item_questions_my, null);
//            }
//            holder.iv_portrait = (ImageView) convertView.findViewById(R.id.iv_portrait);
//            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
//            holder.tv_tiem = (TextView) convertView.findViewById(R.id.tv_tiem);
//            convertView.setTag(holder);
////            } else {
////                holder = (ViewHolder) convertView.getTag();
////            }
//
//            holder.tv_name.setText(replies.get(position).username);
//            holder.tv_content.setText(replies.get(position).content);
//
//            if (questionsBean.data.answers.get(groupPosition).replies.size() - 1 == childPosition) {
//                holder.tv_tiem.setVisibility(View.VISIBLE);
//                holder.tv_tiem.setText(questionsBean.data.answers.get(groupPosition).time);
//            }
//
//            ApiHttpClient.lodCircleImg(holder.iv_portrait,
//                    replies.get(position).photo,
//                    R.mipmap.ic_launcher, R.mipmap.ic_launcher_overturn);
//
//            holder.tv_tiem.setText(replies.get(position).time);
//            return convertView;
//        }
//
//        @Override
//        public boolean isChildSelectable(int groupPosition, int childPosition) {
//            return false;
//        }
//
//        class ViewHolder {
//            public ImageView iv_portrait;
//            public TextView tv_name;
//            public TextView tv_content;
//            public TextView tv_tiem;
//        }
//    }
}

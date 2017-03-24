package com.xwsd.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.xwsd.app.AppContext;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.bean.UserBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Gx on 2016/9/12.
 * 获取用户信息的服务
 */
public class UserService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //加上这一句，防止app退出后空指针异常。
        if (intent != null) {
            ApiHttpClient.getUserInfo(intent.getStringExtra(UserParam.USER_ID), new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                }

                @Override
                public void onResponse(String response, int id) {
                    TLog.error("用户服务获取信息:" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt("status") == 1) {
                            UserBean userBean = GsonUtils.jsonToBean(response, UserBean.class);
                            AppContext.setUserBean(userBean);
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

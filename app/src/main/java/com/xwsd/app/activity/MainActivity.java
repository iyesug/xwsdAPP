package com.xwsd.app.activity;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import butterknife.Bind;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.constant.BroadcastParam;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.fragment.AccountFragment;
import com.xwsd.app.fragment.HomeFragment;
import com.xwsd.app.fragment.NewsFragment;
import com.xwsd.app.fragment.ProjectFragment;
import com.xwsd.app.service.UserService;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.DoubleClickExitHelper;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by Gx on 2016/8/18.
 * 主页面，管理四个Fragment(首页，项目，账户，讯息)
 */
public class MainActivity extends BaseActivity {

    /**
     * 首页
     */
    public HomeFragment homeFragment;

    /**
     * 项目
     */
    public ProjectFragment projectFragment;

    /**
     * 账户
     */
    public AccountFragment accountFragment;

    /**
     * 资讯
     */
    public NewsFragment newsFragment;

    /**
     * 底部TAB组
     */
    @Bind(R.id.main_tab)
    public RadioGroup main_tab;

    @Bind(R.id.tab_home)
    public RadioButton tab_home;

    @Bind(R.id.tab_project)
    public RadioButton tab_project;

    @Bind(R.id.tab_account)
    public RadioButton tab_account;

    @Bind(R.id.tab_news)
    public RadioButton tab_news;

    RequestCall pvCall;

    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager fragmentManager;

    /**
     * 当前显示的是那的Fragment
     */
    public int nowShowWho;

    private DoubleClickExitHelper mDoubleClickExit;

    /**
     * 广播接收者，用于处理相对应的Action
     */
    private MyBroadcastReciever myBroadcastReciever = new MyBroadcastReciever();

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_main);
        pushBuriedPoint();
        AppContext.setNeedLock(false);
//        开启线程检查更新
        new Thread() {
            @Override
            public void run() {
                checkVersion();
            }
        }.start();

    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void init(Bundle savedInstanceState) {
        title="主页";
        mDoubleClickExit = new DoubleClickExitHelper(this);
        fragmentManager = getSupportFragmentManager();
//        设置TAB的状态更新事件
        main_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setTabSelection(checkedId);
            }
        });

//        默认选中首页
        nowShowWho = R.id.tab_home;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        homeFragment = new HomeFragment();
        transaction.add(R.id.frame_content, homeFragment).commit();

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastParam.ACCOUNT_UPDATE_RECHARGE);
        intentFilter.addAction(BroadcastParam.ACCOUNT_UPDATE_WITHDRAW);
        intentFilter.addAction(BroadcastParam.USER_CHANGE_OUT);
        intentFilter.addAction(BroadcastParam.NEED_ENTER_ACCOUNT);
        intentFilter.addAction(BroadcastParam.BUY_BID);
        intentFilter.addAction(BroadcastParam.BUY_CREDITORS);
        intentFilter.addAction(BroadcastParam.USER_CHANGE_LOGIN);
        registerReceiver(myBroadcastReciever, intentFilter);

//        //判断是否开通存管
//        if (AppContext.getUserBean() != null ) {
//            {
//                if (AppContext.getUserBean().data.custodyId == null || "0".equals(AppContext.getUserBean().data.custodyId)
//                        || "".equals(AppContext.getUserBean().data.custodyId)) {
//                    showDialog();
//                }
//            }
//        }
    }
//    //开通存管对话框
//    private void showDialog(){
//        new PicDialog(this,
//                new PicDialog.DialogClickListener() {
//
//                    @Override
//                    public void close(Dialog dialog, String pass) {
//                        dialog.dismiss();
//
//                    }
//
//                    @Override
//                    public void commit(Dialog dialog) {
//                        dialog.dismiss();
//                        Intent intent = new Intent(MainActivity.this, OpenDepositoryActivity.class);
//                        startActivity(intent);
//                    }
//                }).show();
//    }
    /**
     * 根据传入的id来设置选中的tab页。
     *
     * @param id RadioButton 的Id
     */
    private void setTabSelection(int id) {
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (id) {
            case R.id.tab_home://首页
                BuriedPointUtil.buriedPoint("tab首页");
                nowShowWho = R.id.tab_home;
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.frame_content, homeFragment);
                } else {
                    AppContext.setNeedLock(false);

                    transaction.show(homeFragment);
                    homeFragment.getData(1);
                }

                break;
            case R.id.tab_project://项目
                BuriedPointUtil.buriedPoint("tab项目");
                nowShowWho = R.id.tab_project;
                if (projectFragment == null) {
                    projectFragment = new ProjectFragment();
                    transaction.add(R.id.frame_content, projectFragment);
                } else {
                    AppContext.setNeedLock(false);

                    transaction.show(projectFragment);
                }
                break;
            case R.id.tab_account://账户
                BuriedPointUtil.buriedPoint("tab账户");
//                判断用户是否登录
                if (AppContext.getUserBean() == null) {
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    intent.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
                    intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                    startActivity(intent);
//                    更改选中的状态（选中上个）
                    main_tab.check(nowShowWho);
                    return;
                }
                nowShowWho = R.id.tab_account;
                if (accountFragment == null) {
                    accountFragment = new AccountFragment();
                    transaction.add(R.id.frame_content, accountFragment);
                } else {
                    AppContext.setNeedLock(true);
                    transaction.show(accountFragment);
                    accountFragment.getData();
                    accountFragment.agreeCard("baofoo");
                    accountFragment.agreeCard("fuiou");
                }

                break;
            case R.id.tab_news://资讯

                //埋点开始
                BuriedPointUtil.buriedPoint("tab资讯");

                nowShowWho = R.id.tab_news;
                if (newsFragment == null) {
                    newsFragment = new NewsFragment();
                    transaction.add(R.id.frame_content, newsFragment);
                } else {
                    AppContext.setNeedLock(false);
                    transaction.show(newsFragment);
                }

                break;
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {

        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (projectFragment != null) {
            transaction.hide(projectFragment);
        }
        if (accountFragment != null) {
            transaction.hide(accountFragment);
        }
        if (newsFragment != null) {
            transaction.hide(newsFragment);
        }

    }

    /**
     * 监听返回--是否退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return mDoubleClickExit.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解绑广播
        unregisterReceiver(myBroadcastReciever);
    }


    /**
     * 上传埋点数据到服务器，一天最多传一次数据，
     */
    public void pushBuriedPoint() {

        //判断用户打开app的时间是不是已上传过埋点数据的当日，如果不是当日就把埋点数据传给服务器
        SharedPreferences pushTime = getSharedPreferences("pushTime", Context.MODE_PRIVATE);
        Time t = new Time();
        t.setToNow();
        int lastmonth = t.month + 1;
        final String str = t.year + "年" + lastmonth + "月" + t.monthDay + "日";
        if (pushTime.getString("pushTime", "").toString().equals(str) == true) {

            TLog.error("今日埋点数据已上传！");
            //埋点读取所有数据开始
            StringBuffer stringBuffer = new StringBuffer();
            Map<String, ?> allContent = BuriedPointUtil.getAll();

            //遍历map的方法
            for (Map.Entry<String, ?> entry : allContent.entrySet()) {
                stringBuffer.append(entry.getKey()).append(":").append(entry.getValue()).append("|");
            }

            //去掉最后一个“|”字符
            if (stringBuffer.length() > 0) {
                stringBuffer.replace(stringBuffer.length() - 1, stringBuffer.length(), "");
            }

            String buriedPoint = stringBuffer.toString();
            TLog.error("埋点的输出：" + buriedPoint);
            //埋点读取所有数据结束

        } else {

            pushTime.edit()
                    .putString("pushTime", str)
                    .commit();

            //埋点读取所有数据开始
            StringBuffer stringBuffer = new StringBuffer();
            Map<String, ?> allContent = BuriedPointUtil.getAll();

            //遍历map的方法
            for (Map.Entry<String, ?> entry : allContent.entrySet()) {
                stringBuffer.append(entry.getKey()).append(":").append(entry.getValue()).append("|");
            }

            //去掉最后一个“|”字符
            if (stringBuffer.length() > 0) {
                stringBuffer.replace(stringBuffer.length() - 1, stringBuffer.length(), "");
            }

            String buriedPoint = stringBuffer.toString();
            TLog.error("埋点的输出：" + buriedPoint);
            //埋点读取所有数据结束

            if (AppContext.getUserBean() != null) {

                pvCall = ApiHttpClient.pvToservice(AppContext.getUserBean().data.userId, stringBuffer.toString(), new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showToast(getString(R.string.network_exception));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("埋点发送服务器是否成功信息: " + response);
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getInt("status") == 1) {
                                TLog.error("埋点数据已成功上传！");
                            } else {
                                TLog.error("上传埋点数据失败！");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.showToastShort(getString(R.string.network_exception));
                        }
                    }
                });
            }

            //第二天，sp数据清零，重新开始统计

            BuriedPointUtil.clearData();
        }

    }

    /**
     * 广播类(用来更新数据)
     */
    class MyBroadcastReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BroadcastParam.ACCOUNT_UPDATE_RECHARGE://充值
                    //启动服务获取用户信息
                    startService(new Intent(MainActivity.this, UserService.class).putExtra(UserParam.USER_ID, AppContext.getUserBean().data.userId));
                    break;
                case BroadcastParam.ACCOUNT_UPDATE_WITHDRAW://提现
                    //启动服务获取用户信息
                    startService(new Intent(MainActivity.this, UserService.class).putExtra(UserParam.USER_ID, AppContext.getUserBean().data.userId));
                    break;
                case BroadcastParam.USER_CHANGE_OUT://用户退出
                    AppContext.setUserBean(null);
                    tab_home.setChecked(true);
                    break;
                case BroadcastParam.NEED_ENTER_ACCOUNT://需要进入账户页面
                    tab_account.setChecked(true);
                    break;
                case BroadcastParam.BUY_BID://投标

                    break;
                case BroadcastParam.BUY_CREDITORS://购买债权

                    break;
                case BroadcastParam.USER_CHANGE_LOGIN://登录成功
                    //启动服务获取用户信息
                    startService(new Intent(MainActivity.this, UserService.class).putExtra(UserParam.USER_ID, AppContext.getUserBean().data.userId));
                    tab_home.setChecked(true);
                    break;
            }
        }
    }




    /**********************以下部分为版本更新相关***********************/


    /**
     * 更新包下载地址
     */
    private String downloadUrl;
    private String content;


    /**
     * 检查服务器版本号(判断是否需要更新)
     */
    private void checkVersion() {
        ApiHttpClient.updataVersion("version", new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToastShort(getString(R.string.network_exception));
            }
            @Override
            public void onResponse(String response, int id) {
                TLog.error("请求版本是否成功信息: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 1) {

                        //版本号
                        final int version = jsonObject.getJSONObject("data").getInt("vCode");
                        //下载地址
                        downloadUrl = jsonObject.getJSONObject("data").getString("downloadUrl");
                        content = jsonObject.getJSONObject("data").getString("content");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                        关闭加载进度框
                                hideWaitDialog();

//                        比对版本号

                                if (getVersionCode() < version) {

                                    new com.gnwai.iosdialog.AlertDialog(MainActivity.this)
                                            .builder()
                                            .setTitle("升级提示：")
                                            .setMsg(content)
                                            .setPositiveButton("马上升级", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                            != PackageManager.PERMISSION_GRANTED) {

                                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                                    } else {
                                                        new DownloadFileAsync().execute();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("取消", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                }
                                            }).show();
                                } else {
//                                    ToastUtil.showToastShort(R.string.already_newest);
                                }
                            }
                        });
                    } else {
                        TLog.error("请求版本失败！");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideWaitDialog();
                            ToastUtil.showToastShort(R.string.network_error);
                        }
                    });
                }

            }
        });

    }

    /**
     * 获取本地版本号
     */
    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pki = pm.getPackageInfo(getPackageName(), 0);
            return pki.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 显示升级对话框
     */

    private AlertDialog updateDialog;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    /**
     * 下载更新包的异步线程
     */
    class DownloadFileAsync extends AsyncTask<String, String, String> {

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        String name = "update.apk";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            显示对话框
            showDownloadDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;

            try {
                URL url = new URL(downloadUrl);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(connection.getInputStream());


                FileOutputStream out = new FileOutputStream(new File(path, name));

                int length = connection.getContentLength();

                int len = 0, total_length = 0, value = 0;

                byte[] data = new byte[1024];

                while ((len = in.read(data)) != -1) {
                    total_length += len;
                    value = (int) ((total_length / (float) length) * 100);
                    out.write(data, 0, len);
                    //调用update函数，更新进度
                    publishProgress(String.valueOf(value));
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            progressBar.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            downloadDialog.dismiss();
            installApk(new File(path, name));
        }

    }

    /**
     * 安装APK
     *
     * @param file apk 文件
     */
    private void installApk(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    /**
     * 显示下载对话框
     */
    private AlertDialog downloadDialog;
    private NumberProgressBar progressBar;

    private void showDownloadDialog() {
        downloadDialog = new AlertDialog.Builder(MainActivity.this).create();
        View view = View.inflate(MainActivity.this, R.layout.dialog_download, null);
        progressBar = (NumberProgressBar) view.findViewById(R.id.pg_download);
        progressBar.setMax(100);
        downloadDialog.setView(view);
        downloadDialog.setCancelable(false);
        downloadDialog.show();
    }


}

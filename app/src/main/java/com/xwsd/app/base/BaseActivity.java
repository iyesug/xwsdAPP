package com.xwsd.app.base;

import android.app.ActivityManager;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import butterknife.ButterKnife;
import com.baidu.mobstat.StatService;
import com.gnwai.loadingview.DialogControl;
import com.gnwai.loadingview.LoadDialog;
import com.xwsd.app.AppContext;
import com.xwsd.app.AppManager;
import com.xwsd.app.R;
import com.xwsd.app.activity.GestureLockSettingsActivity;
import com.xwsd.app.activity.GestureLoginActivity;
import com.xwsd.app.activity.UserActivity;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GesturePassward;
import com.xwsd.app.tools.TLog;

import java.util.List;

/**
 * 基础Activity
 * onBeforeSetContentLayout 方法中设置内容布局
 * init 方法可以做一些初始化的操作
 * 除此之外还封装了一个加载进度条方便调用
 * 以及对软键盘的简单处理
 */
public abstract class BaseActivity extends AppCompatActivity implements DialogControl {

    private boolean isVisible;

    private LoadDialog zProgressHUD;

    public String title="";

    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "share_date";

    @Override
    protected void onDestroy() {

        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            //Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        //将Activity加入管理栈
        AppManager.getAppManager().addActivity(this);
//        去掉标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置布局
        onBeforeSetContentLayout();
        // 通过注解绑定控件
        ButterKnife.bind(this);
//        初始化
        init(savedInstanceState);
        StatService.onPageStart(this,title);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;

    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public void setParam(String key, Object object) {

        String type = object.getClass().getSimpleName();
        SharedPreferences sp = this.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }

        editor.commit();
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public Object getParam(String key, Object defaultObject) {
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = this.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 子类在这里实现setContentView
     */
    protected abstract void onBeforeSetContentLayout();

    /**
     * 初始化
     *
     * @param savedInstanceState
     */
    protected abstract void init(Bundle savedInstanceState);


    /**
     * 界面暂停的时候隐藏软键盘
     */
    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this,title);
        if (this.isFinishing()) {
            hideSoftKeyboard(getCurrentFocus());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean isBackground = false;

    @Override
    protected void onStop() {
        super.onStop();
        if (isAppIsInBackground(BaseActivity.this)) {
            TLog.error("应用进入后台");
            isBackground = true;
        } else {
            TLog.error("还在前台");
            isBackground = false;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (isBackground &&
                AppContext.isNeedLock() &&
                !AppManager.getAppManager().currentActivity().getClass().equals(GestureLoginActivity.class) &&
                !AppManager.getAppManager().currentActivity().getClass().equals(GestureLockSettingsActivity.class)) {

//            if (ACache.get(BaseActivity.this).getAsBinary((String) getParam(UserParam.USER_ID, "")) == null) {
//                //关闭登录页面，和自己，防止跳出重复页面
//                AppManager.getAppManager().finishActivity(UserActivity.class);
//                AppManager.getAppManager().finishActivity(GestureLockSettingsActivity.class);
//                Intent intent = new Intent(this, GestureLockSettingsActivity.class);
//                startActivity(intent);
//            }

            if ("".equals(GesturePassward.getString((String)getParam(UserParam.USER_ID, ""),""))) {
//                关闭登录页面，和自己，防止跳出重复页面
                AppManager.getAppManager().finishActivity(UserActivity.class);
                AppManager.getAppManager().finishActivity(GestureLockSettingsActivity.class);
                Intent intent = new Intent(this, GestureLockSettingsActivity.class);
                startActivity(intent);
            } else {
                AppManager.getAppManager().finishActivity(UserActivity.class);
                AppManager.getAppManager().finishActivity(GestureLoginActivity.class);
                Intent intent = new Intent(this, GestureLoginActivity.class);
                startActivity(intent);
            }

        }
    }

    /**
     * 判断程序是否在前台运行
     *
     * @param context
     * @return
     */
    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            if(null!=runningProcesses) {
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    //前台程序
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public void hideSoftKeyboard(View view) {
        if (view == null)
            return;
        ((InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }

    @Override
    public LoadDialog showWaitDialog(DialogInterface.OnCancelListener onCancelListener) {
        return showWaitDialog(R.string.loading, onCancelListener);
    }

    @Override
    public LoadDialog showWaitDialog(int resid, DialogInterface.OnCancelListener onCancelListener) {
        return showWaitDialog(getString(resid), onCancelListener);
    }

    @Override
    public LoadDialog showWaitDialog(String message, DialogInterface.OnCancelListener onCancelListener) {
        if (isVisible) {
            if (zProgressHUD == null) {
                zProgressHUD = new LoadDialog(this);
                zProgressHUD.setOnCancelListener(onCancelListener);
            }
            if (zProgressHUD != null) {
                zProgressHUD.setMessage(message);
                zProgressHUD.show();
            }
            return zProgressHUD;
        }
        return null;
    }

    @Override
    public void hideWaitDialog() {
        if (isVisible && zProgressHUD != null) {
            try {
                zProgressHUD.dismiss();
                zProgressHUD = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
